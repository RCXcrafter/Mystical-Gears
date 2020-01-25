package com.rcx.mystgears.compatibility;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockMechanicalDial;
import com.rcx.mystgears.block.TileEntityMechanicalDial;
import com.rcx.mystgears.item.ItemGear;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EmbersCompat {

	public static ItemBlock mechDial;

	public static void preInit() {
		if (ConfigHandler.mechDial) {
			mechDial = new ItemBlock(new BlockMechanicalDial(Material.IRON, "mechanical_dial", false));
			MysticalGears.blocks.add((ItemBlock) mechDial.setRegistryName(mechDial.getBlock().getRegistryName()));
		}
	}

	public static void init() {
		if (ConfigHandler.mechDial) {
			GameRegistry.registerTileEntity(TileEntityMechanicalDial.class, new ResourceLocation(MysticalGears.MODID, "mech_dial"));

			GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_mechanical_dial"), ItemGear.group, new ItemStack(mechDial), new Object[]{"R", "P", "G", 'R', "dustRedstone", 'P', "paper", 'G', "plateGold"});
		}
	}
}
