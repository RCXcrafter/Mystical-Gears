package com.rcx.mystgears.compatibility;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockPoweredBellows;
import com.rcx.mystgears.block.TileEntityPoweredBellows;
import com.rcx.mystgears.block.TileEntityPoweredBellowsTop;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PyrotechCompat {

	public static ItemBlock poweredBellows;

	public static void preInit() {
		if (ConfigHandler.poweredBellows) {
			poweredBellows = new ItemBlock(new BlockPoweredBellows());
			MysticalGears.blocks.add((ItemBlock) poweredBellows.setRegistryName(poweredBellows.getBlock().getRegistryName()));	
		}
	}

	public static void init() {
		if (ConfigHandler.poweredBellows) {
			GameRegistry.registerTileEntity(TileEntityPoweredBellows.class, new ResourceLocation(MysticalGears.MODID, "powered_bellows"));
			GameRegistry.registerTileEntity(TileEntityPoweredBellowsTop.class, new ResourceLocation(MysticalGears.MODID, "powered_bellows_top"));
			
			//GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_mechanical_dial"), ItemGear.group, new ItemStack(mechanicalCrafter), new Object[]{"R", "P", "G", 'R', "dustRedstone", 'P', "paper", 'G', "plateGold"});	
		}
	}
}
