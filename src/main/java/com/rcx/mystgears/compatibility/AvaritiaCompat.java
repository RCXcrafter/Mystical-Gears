package com.rcx.mystgears.compatibility;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.proxy.CommonProxy;

import morph.avaritia.init.ModItems;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.extreme.ExtremeShapedRecipe;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class AvaritiaCompat {

	public static void init() {
		if (ConfigHandler.infiniteMechRecipe)
			AvaritiaRecipeManager.EXTREME_RECIPES.put(new ResourceLocation(MysticalGears.MODID, "recipe_creative_mech_source"), new ExtremeShapedRecipe(new ItemStack(RegistryHandler.CREATIVE_MECH_SOURCE), CraftingHelper.parseShaped(new Object[] {
					"IMIAMAIMI",
					"MCACACACM",
					"IABGNGBAI",
					"ACGNMNGCA",
					"MANMXMNAM",
					"ACGNMNGCA",
					"IABGNGBAI",
					"MCACACACM",
					"IMIAMAIMI",
					'C', "gearCrystalMatrix",
					'N', "gearCosmicNeutronium",
					'I', "gearInfinity",
					'M', new ItemStack(RegistryHandler.MERGEBOX_FRAME),
					'G', new ItemStack(RegistryHandler.GEARBOX_FRAME),
					'B', new ItemStack(CommonProxy.windupBox),
					'A', new ItemStack(RegistryHandler.IRON_AXLE),
					'X', new ItemStack(ModItems.resource, 1, 5)
			})));
	}
}
