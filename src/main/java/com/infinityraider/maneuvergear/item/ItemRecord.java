package com.infinityraider.maneuvergear.item;

import com.infinityraider.infinitylib.item.IInfinityItem;
import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.reference.Names;
import com.infinityraider.maneuvergear.reference.Reference;
import com.infinityraider.maneuvergear.registry.SoundRegistry;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

@MethodsReturnNonnullByDefault
public class ItemRecord extends MusicDiscItem implements IInfinityItem {
    public ItemRecord() {
        super(16, ItemRecord::getSoundEvent, new Properties()
                .maxStackSize(1).group(ItemGroup.MISC).rarity(Rarity.RARE));
    }

    @Override
    public String getInternalName() {
        return Names.Items.RECORD;
    }

    @Override
    public boolean isEnabled() {
        return !ManeuverGear.instance.getConfig().disableMusicDisc();
    }

    private static SoundEvent getSoundEvent() {
        return SoundRegistry.getInstance().soundEventRecord;
    }

    private static SoundEvent registerSoundAndCreateRecord(String name) {
        ResourceLocation loc = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "records." + name);
        SoundEvent sound = new SoundEvent(loc);
        for(Method method : SoundEvent.class.getDeclaredMethods()) {
            if(Modifier.isStatic(method.getModifiers()) && method.getParameterCount() > 0) {
                method.setAccessible(true);
                try {
                    method.invoke(null, loc.toString());
                } catch (Exception e) {
                    ManeuverGear.instance.getLogger().printStackTrace(e);
                }
                break;
            }
        }
        return sound;
    }
}
