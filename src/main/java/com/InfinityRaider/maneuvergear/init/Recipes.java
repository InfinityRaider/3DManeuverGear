package com.InfinityRaider.maneuvergear.init;

import com.InfinityRaider.maneuvergear.item.IItemWithRecipe;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.crafting.IRecipe;

import java.lang.reflect.Field;

public class Recipes {
    public static void init() {
        for(Field field:Items.class.getDeclaredFields()) {
            try {
                Object obj = field.get(null);
                if(obj == null) {
                    continue;
                }
                if(!(obj instanceof IItemWithRecipe)) {
                    continue;
                }
                for(IRecipe recipe : ((IItemWithRecipe) obj).getRecipes()) {
                    GameRegistry.addRecipe(recipe);
                }
            } catch (Exception e) {
                LogHelper.printStackTrace(e);
            }
        }
    }
}
