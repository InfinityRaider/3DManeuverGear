package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSwordBlade extends ItemBase {
    public ItemSwordBlade() {
        super(Names.Items.SWORD_BLADE, new Properties()
                .tab(CreativeModeTab.TAB_MISC)
                .stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        if (stack.getItem() != this) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        ItemStack maneuverGear = DartHandler.instance.getManeuverGear(player);
        if (maneuverGear == null || !(maneuverGear.getItem() instanceof ItemManeuverGear)) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        ItemManeuverGear item  = (ItemManeuverGear) maneuverGear.getItem();
        stack.setCount(item.addBlades(maneuverGear, stack.getCount(), player.isDiscrete()));
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity attacker, LivingEntity attacked) {
        if(!attacker.getLevel().isClientSide() && attacker instanceof Player)  {
            DamageSource source = DamageSource.playerAttack((Player) attacker);
            attacker.hurt(source, 2.5F);
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced) {
        tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_1"));
        tooltip.add(new TranslatableComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_2"));
    }
}
