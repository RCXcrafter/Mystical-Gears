package com.rcx.mystgears.compatibility;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockGatlingGunEmber;
import com.rcx.mystgears.block.BlockMechanicalDial;
import com.rcx.mystgears.block.TileEntityGatlingGunEmber;
import com.rcx.mystgears.block.TileEntityMechanicalDial;
import com.rcx.mystgears.item.ItemGear;
import com.rcx.mystgears.render.TileEntityRenderGatlingGunEmber;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import teamroots.embers.RegistryManager;

public class EmbersCompat {

	public static ItemBlock mechDial;
	public static ItemBlock emberGatlingGun;

	public static void preInit() {
		if (ConfigHandler.mechDial) {
			mechDial = new ItemBlock(new BlockMechanicalDial(Material.IRON, "mechanical_dial", false));
			MysticalGears.blocks.add((ItemBlock) mechDial.setRegistryName(mechDial.getBlock().getRegistryName()));
		}
		if (ConfigHandler.emberGatlingGun) {
			emberGatlingGun = new ItemBlock(new BlockGatlingGunEmber());
			MysticalGears.blocks.add((ItemBlock) emberGatlingGun.setRegistryName(emberGatlingGun.getBlock().getRegistryName()));
		}
	}

	public static void init() {
		if (ConfigHandler.mechDial) {
			GameRegistry.registerTileEntity(TileEntityMechanicalDial.class, new ResourceLocation(MysticalGears.MODID, "mech_dial"));

			GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_mechanical_dial"), ItemGear.group, new ItemStack(mechDial), new Object[]{"R", "P", "G", 'R', "dustRedstone", 'P', "paper", 'G', "plateGold"});
		}
		if (ConfigHandler.emberGatlingGun) {
			GameRegistry.registerTileEntity(TileEntityGatlingGunEmber.class, new ResourceLocation(MysticalGears.MODID, "ember_gatling_gun"));

			GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_ember_gatling_gun"), ItemGear.group, new ItemStack(emberGatlingGun), new Object[]{"IDD", "C I", "IDD", 'I', "plateIron", 'D', "plateDawnstone", 'C', new ItemStack(RegistryManager.shard_ember)});
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGatlingGunEmber.class, new TileEntityRenderGatlingGunEmber());
	}
}
