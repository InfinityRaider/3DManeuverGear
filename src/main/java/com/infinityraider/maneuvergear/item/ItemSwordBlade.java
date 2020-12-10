package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.ItemBase;
import com.infinityraider.maneuvergear.handler.DartHandler;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSwordBlade extends ItemBase {
    public ItemSwordBlade() {
        super(Names.Items.SWORD_BLADE, new Properties()
                .group(ItemGroup.MISC)
                .maxStackSize(16));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        if (stack == null || stack.getItem() != this) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        ItemStack maneuverGear = DartHandler.instance.getManeuverGear(player);
        if (maneuverGear == null || !(maneuverGear.getItem() instanceof ItemManeuverGear)) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        ItemManeuverGear item  = (ItemManeuverGear) maneuverGear.getItem();
        stack.setCount(item.addBlades(maneuverGear, stack.getCount(), player.isSneaking()));
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity attacker, LivingEntity attacked) {
        if(!attacker.getEntityWorld().isRemote && attacker instanceof PlayerEntity)  {
            DamageSource source = DamageSource.causePlayerDamage((PlayerEntity) attacker);
            attacker.attackEntityFrom(source, 2.5F);
        }
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_1"));
        tooltip.add(new TranslationTextComponent(Reference.MOD_ID + ".tooltip." + this.getInternalName() + "_2"));
    }
}
