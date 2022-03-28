package com.infinityraider.maneuvergear.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.infinitylib.item.InfinityItemProperty;
import com.infinityraider.infinitylib.render.item.IClientItemProperties;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.reference.Reference;
import com.infinityraider.maneuvergear.reference.Names;
import com.google.common.collect.Multimap;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.modules.dualwield.IDualWieldedWeapon;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemManeuverGearHandle extends ItemBase implements IDualWieldedWeapon {
    public static int getDurability() {
        return ManeuverGear.instance.getConfig().getDurability();
    }

    private final Multimap<Attribute, AttributeModifier> attributeModifiersWithBlade;
    private final Multimap<Attribute, AttributeModifier> attributeModifiersWithoutBlade;

    public ItemManeuverGearHandle() {
        super(Names.Items.MANEUVER_HANDLE, new Properties()
                .tab(CreativeModeTab.TAB_COMBAT)
                .stacksTo(1)
                .durability(getDurability())
        );
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        this.attributeModifiersWithBlade = builder
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Bladed Maneuver Handle Attack Damage Modifier",
                        3.0 + (ManeuverGear.instance.getConfig().getDamage()), AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Bladed Maneuver Handle Speed Damage Modifier",
                        1.8, AttributeModifier.Operation.ADDITION))
                .build();
        builder = ImmutableMultimap.builder();
        this.attributeModifiersWithoutBlade = builder
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Empty Maneuver Handle Attack Damage Modifier",
                        0, AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Empty Maneuver Handle Attack Speed Modifier",
                        1.8, AttributeModifier.Operation.ADDITION))
                .build();
    }

    /**
     * Checks if there is a sword blade present on the respective handle
     *
     * @param stack the ItemStack holding this
     * @return if there is a blade present
     */
    public boolean hasSwordBlade(ItemStack stack) {
        if (!isValidManeuverGearHandleStack(stack)) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getInt(Names.NBT.DAMAGE) > 0;
    }

    public int getBladeDamage(ItemStack stack) {
        if (!isValidManeuverGearHandleStack(stack)) {
            return 0;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return tag.getInt(Names.NBT.DAMAGE);
    }

    /**
     * Attempts to damage the sword blade
     *
     * @param stack the ItemStack holding this
     */
    public void damageSwordBlade(LivingEntity entity, ItemStack stack, int amount) {
        if (!hasSwordBlade(stack)) {
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            tag = new CompoundTag();
            stack.setTag(tag);
        }
        int dmg = tag.getInt(Names.NBT.DAMAGE);
        dmg = dmg - amount;
        tag.putInt(Names.NBT.DAMAGE, dmg);
        if (dmg <= 0) {
            this.onSwordBladeBroken(entity);
        }
    }

    /**
     * Tries to apply a sword blade from the holster, this can fail if the player is not wearing a holster, the respective holster is empty,
     * or there is already a blade on the handle
     *
     * @param entity the entity wielding the gear
     * @param stack  the ItemStack holding this
     * @param left   to add a blade to the left or the right handle
     * @return if a blade was successfully applied
     */
    public boolean applySwordBlade(LivingEntity entity, ItemStack stack, boolean left) {
        if (!isValidManeuverGearHandleStack(stack)) {
            return false;
        }
        if (hasSwordBlade(stack)) {
            return false;
        }
        if (entity instanceof Player) {
            Player player = (Player) entity;
            ItemStack maneuverGearStack = DartHandler.instance.getManeuverGear(player);
            if (maneuverGearStack == null || !(maneuverGearStack.getItem() instanceof ItemManeuverGear)) {
                return false;
            }
            ItemManeuverGear maneuverGear = (ItemManeuverGear) maneuverGearStack.getItem();
            if (maneuverGear.getBladeCount(maneuverGearStack, left) > 0) {
                maneuverGear.removeBlades(maneuverGearStack, 1, left);
                CompoundTag tag = stack.getTag();
                tag = tag == null ? new CompoundTag() : tag;
                tag.putInt(Names.NBT.DAMAGE, getDurability());
                stack.setTag(tag);
                return true;
            }
        }
        return false;
    }

    private void onSwordBladeBroken(LivingEntity entity) {
        if (entity != null) {
            SoundType type = SoundType.ANVIL;
            entity.getLevel().playSound(null, entity.getX(), entity.getY(), entity.getZ(), type.getPlaceSound(), SoundSource.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);
        }
    }

    /**
     * Checks if the stack is a valid stack containing Maneuver Gear
     *
     * @param stack the stack to check
     * @return if the stack is valid
     */
    public boolean isValidManeuverGearHandleStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemManeuverGearHandle;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (this.hasSwordBlade(stack)) {
            if (state.is(Blocks.COBWEB)) {
                return 15.0F;
            } else {
                Material material = state.getMaterial();
                return material != Material.PLANT
                        && material != Material.REPLACEABLE_PLANT
                        && !state.is(BlockTags.LEAVES)
                        && material != Material.LEAVES
                        ? 1.0F : 1.5F;
            }
        } else {
            return super.getDestroySpeed(stack, state);
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if ((double) state.getDestroySpeed(world, pos) != 0.0D && this.hasSwordBlade(stack)) {
            this.damageSwordBlade(entity, stack, 2);
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        this.damageSwordBlade(entity, stack, amount);
        return 0;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public void onItemUsed(ItemStack stack, Player player, boolean shift, boolean ctrl, InteractionHand hand) {
        if (stack.getItem() != this) {
            return;
        }
        if (!DartHandler.instance.isWearingGear(player)) {
            return;
        }
        if (!player.getLevel().isClientSide()) {
            boolean left = hand == InteractionHand.OFF_HAND;
            if (shift) {
                if (!DartHandler.instance.isWearingGear(player)) {
                    return;
                }
                if (left ? DartHandler.instance.hasLeftDart(player) : DartHandler.instance.hasRightDart(player)) {
                    DartHandler.instance.retractDart(player, left);
                } else {
                    DartHandler.instance.fireDart(player.getLevel(), player, left);
                }
            } else if (ctrl) {
                if (!hasSwordBlade(stack)) {
                    applySwordBlade(player, stack, left);
                }
            }
        }
    }

    @Override
    public boolean onItemAttack(ItemStack stack, Player player, Entity e, boolean shift, boolean ctrl, InteractionHand hand) {
        if (ctrl || shift) {
            return true;
        }
        if (!this.hasSwordBlade(stack)) {
            return true;
        }
        if (!player.getLevel().isClientSide()) {
            if (!player.isCreative()) {
                this.damageSwordBlade(player, stack, 1);
            }
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (this.hasSwordBlade(stack)) {
            player.getAttributes().addTransientAttributeModifiers(this.attributeModifiersWithBlade);
        } else {
            player.getAttributes().addTransientAttributeModifiers(this.attributeModifiersWithoutBlade);
        }
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EquipmentSlot.OFFHAND || slot == EquipmentSlot.MAINHAND) {
            if (this.hasSwordBlade(stack)) {
                return this.attributeModifiersWithBlade;
            } else {
                return this.attributeModifiersWithoutBlade;
            }
        }
        return multimap;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
        tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_1"));
        tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip.damage")
                .append(new TextComponent(": " + this.getBladeDamage(stack) + "/" + getDurability())));
        if (ManeuverGear.instance.proxy().isShiftPressed()) {
            tooltip.add(new TextComponent(""));
            tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip.handle_left_normal"));
            tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip.handle_right_normal"));
            tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip.handle_left_sneak"));
            tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip.handle_right_sneak"));
            tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip.handle_left_sprint"));
            tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip.handle_right_sprint"));
        } else {
            tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip.more_info"));
        }
    }

    @Override
    public Supplier<IClientItemProperties> getClientItemProperties() {
        return () -> new IClientItemProperties(){
            @Nonnull
            @Override
            public Set<InfinityItemProperty> getModelProperties() {
                return ImmutableSet.of(new InfinityItemProperty(new ResourceLocation(ManeuverGear.instance.getModId(), Names.Objects.BLADE)) {
                    @Override
                    public float getValue(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
                        return (!stack.isEmpty()
                                && stack.getItem() instanceof ItemManeuverGearHandle
                                && ((ItemManeuverGearHandle) stack.getItem()).hasSwordBlade(stack))
                                ? 1
                                : 0;
                    }
                });
            }
        };
    }
}
