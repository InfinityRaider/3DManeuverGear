package com.infinityraider.maneuvergear.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.infinitylib.item.InfinityItemProperty;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.reference.Reference;
import com.infinityraider.maneuvergear.reference.Names;
import com.google.common.collect.Multimap;
import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.infinitylib.modules.dualwield.IDualWieldedWeapon;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
public class ItemManeuverGearHandle extends ItemBase implements IDualWieldedWeapon {
    public static int getDurability() {
        return ManeuverGear.instance.getConfig().getDurability();
    }

    private final Multimap<Attribute, AttributeModifier> attributeModifiersWithBlade;
    private final Multimap<Attribute, AttributeModifier> attributeModifiersWithoutBlade;

    public ItemManeuverGearHandle() {
        super(Names.Items.MANEUVER_HANDLE, new Properties()
                .group(ItemGroup.COMBAT)
                .maxStackSize(1)
                .maxDamage(getDurability())
        );
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        this.attributeModifiersWithBlade = builder
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Bladed Maneuver Handle Attack Damage Modifier",
                        3.0 + (ManeuverGear.instance.getConfig().getDamage()), AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Bladed Maneuver Handle Speed Damage Modifier",
                        1.8, AttributeModifier.Operation.ADDITION))
                .build();
        builder = ImmutableMultimap.builder();
        this.attributeModifiersWithoutBlade = builder
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Empty Maneuver Handle Attack Damage Modifier",
                        0, AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Empty Maneuver Handle Attack Speed Modifier",
                        1.8, AttributeModifier.Operation.ADDITION))
                .build();
    }

    /**
     * Checks if there is a sword blade present on the respective handle
     * @param stack the ItemStack holding this
     * @return if there is a blade present
     */
    public boolean hasSwordBlade(ItemStack stack) {
        if (!isValidManeuverGearHandleStack(stack)) {
            return false;
        }
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getInt(Names.NBT.DAMAGE) > 0;
    }

    public int getBladeDamage(ItemStack stack) {
        if(!isValidManeuverGearHandleStack(stack)) {
            return 0;
        }
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            return 0;
        }
        return tag.getInt(Names.NBT.DAMAGE);
    }

    /**
     * Attempts to damage the sword blade
     * @param stack the ItemStack holding this
     */
    public void damageSwordBlade(LivingEntity entity, ItemStack stack, int amount) {
        if(!hasSwordBlade(stack)) {
            return;
        }
        CompoundNBT tag = stack.getTag();
        if(tag == null) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        }
        int dmg = tag.getInt(Names.NBT.DAMAGE);
        dmg = dmg - amount;
        tag.putInt(Names.NBT.DAMAGE, dmg);
        if(dmg <= 0) {
            this.onSwordBladeBroken(entity);
        }
    }

    /**
     * Tries to apply a sword blade from the holster, this can fail if the player is not wearing a holster, the respective holster is empty,
     * or there is already a blade on the handle
     * @param entity the entity wielding the gear
     * @param stack the ItemStack holding this
     * @param left to add a blade to the left or the right handle
     * @return if a blade was successfully applied
     */
    public boolean applySwordBlade(LivingEntity entity, ItemStack stack, boolean left) {
        if(!isValidManeuverGearHandleStack(stack)) {
            return false;
        }
        if(hasSwordBlade(stack)) {
            return false;
        }
        if(entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            ItemStack maneuverGearStack = DartHandler.instance.getManeuverGear(player);
            if(maneuverGearStack == null || !(maneuverGearStack.getItem() instanceof ItemManeuverGear)) {
                return false;
            }
            ItemManeuverGear maneuverGear = (ItemManeuverGear) maneuverGearStack.getItem();
            if(maneuverGear.getBladeCount(maneuverGearStack, left) > 0) {
                maneuverGear.removeBlades(maneuverGearStack, 1, left);
                CompoundNBT tag = stack.getTag();
                tag = tag == null ? new CompoundNBT() : tag;
                tag.putInt(Names.NBT.DAMAGE, getDurability());
                stack.setTag(tag);
                return true;
            }
        }
        return false;
    }

    private void onSwordBladeBroken(LivingEntity entity) {
        if(entity != null) {
            SoundType type = SoundType.ANVIL;
            entity.getEntityWorld().playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), type.getPlaceSound(), SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);
        }
    }

    /**
     * Checks if the stack is a valid stack containing Maneuver Gear
     * @param stack the stack to check
     * @return if the stack is valid
     */
    public boolean isValidManeuverGearHandleStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemManeuverGearHandle;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.matchesBlock(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            Material material = state.getMaterial();
            return material != Material.PLANTS
                    && material != Material.TALL_PLANTS
                    && material != Material.CORAL
                    && !state.isIn(BlockTags.LEAVES)
                    && material != Material.GOURD
                    ? 1.0F : 1.5F;
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        if ((double) state.getBlockHardness(world, pos) != 0.0D && this.hasSwordBlade(stack)) {
            this.damageSwordBlade(entity, stack, 2);
        }
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        this.damageSwordBlade(entity, stack, amount);
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public void onItemUsed(ItemStack stack, PlayerEntity player, boolean shift, boolean ctrl, Hand hand) {
        if (stack.getItem() != this) {
            return;
        }
        if (!DartHandler.instance.isWearingGear(player)) {
            return;
        }
        if (!player.getEntityWorld().isRemote) {
            boolean left = hand == Hand.OFF_HAND;
            if (shift) {
                if(!DartHandler.instance.isWearingGear(player)) {
                    return;
                }
                if (left ? DartHandler.instance.hasLeftDart(player) : DartHandler.instance.hasRightDart(player)) {
                    DartHandler.instance.retractDart(player, left);
                } else {
                    DartHandler.instance.fireDart(player.getEntityWorld(), player, left);
                }
            } else if (ctrl) {
                if (!hasSwordBlade(stack)) {
                    applySwordBlade(player, stack, left);
                }
            }
        }
    }

    @Override
    public boolean onItemAttack(ItemStack stack, PlayerEntity player, Entity e, boolean shift, boolean ctrl, Hand hand) {
        if(ctrl || shift) {
            return true;
        }
        if(!this.hasSwordBlade(stack)) {
            return true;
        }
        if(!player.getEntityWorld().isRemote) {
            if (!player.isCreative()) {
                this.damageSwordBlade(player, stack, 1);
            }
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if(this.hasSwordBlade(stack)) {
            player.getAttributeManager().reapplyModifiers(this.attributeModifiersWithBlade);
        } else {
            player.getAttributeManager().reapplyModifiers(this.attributeModifiersWithoutBlade);
        }
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if(slot == EquipmentSlotType.OFFHAND || slot == EquipmentSlotType.MAINHAND) {
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
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_1"));
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.damage")
                .appendSibling(new StringTextComponent(": " + this.getBladeDamage(stack) + "/" + getDurability())));
        if(advanced.isAdvanced()) {
            tooltip.add(new StringTextComponent(""));
            tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.handle_left_normal"));
            tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.handle_right_normal"));
            tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.handle_left_sneak"));
            tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.handle_right_sneak"));
            tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.handle_left_sprint"));
            tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.handle_right_sprint"));
        } else {
            tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip.more_info"));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Set<InfinityItemProperty> getModelProperties() {
        return ImmutableSet.of(new InfinityItemProperty(new ResourceLocation(ManeuverGear.instance.getModId(), Names.Objects.BLADE)) {
            @Override
            public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
                return (!stack.isEmpty()
                        && stack.getItem() instanceof ItemManeuverGearHandle
                        && ((ItemManeuverGearHandle) stack.getItem()).hasSwordBlade(stack))
                        ? 1
                        : 0;
            }
        });
    }
}
