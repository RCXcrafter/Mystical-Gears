package com.rcx.mystgears.compatibility;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockAuraEngine;
import com.rcx.mystgears.block.TileEntityAuraEngine;
import com.rcx.mystgears.item.ItemGear;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class NaturesAuraCompat {

	public static ItemBlock auraEngine;

	public static void preInit() {
		if (ConfigHandler.auraEngine) {
			auraEngine = new ItemBlock(new BlockAuraEngine());
			MysticalGears.blocks.add((ItemBlock) auraEngine.setRegistryName(auraEngine.getBlock().getRegistryName()));
		}
	}

	public static void init() {
		if (ConfigHandler.auraEngine) {
			GameRegistry.registerTileEntity(TileEntityAuraEngine.class, new ResourceLocation(MysticalGears.MODID, "aura_engine"));

			GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_aura_engine"), ItemGear.group, new ItemStack(auraEngine), new Object[]{"WWI", "IJG", "WWI", 'G', "gearInfusedIron", 'W', Item.REGISTRY.getObject(new ResourceLocation("naturesaura", "ancient_planks")), 'I', Item.REGISTRY.getObject(new ResourceLocation("naturesaura", "infused_iron")), 'J', Item.REGISTRY.getObject(new ResourceLocation("naturesaura", "token_joy"))});
		}
	}
}
