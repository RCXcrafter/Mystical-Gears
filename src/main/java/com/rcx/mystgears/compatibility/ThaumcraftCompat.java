package com.rcx.mystgears.compatibility;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockVisMotor;
import com.rcx.mystgears.block.TileEntityVisMotor;

import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;

public class ThaumcraftCompat {
	

	public static ItemBlock visMotor;

	public static void preInit() {
		if (ConfigHandler.visMotor) {
			visMotor = new ItemBlock(new BlockVisMotor());
			MysticalGears.blocks.add((ItemBlock) visMotor.setRegistryName(visMotor.getBlock().getRegistryName()));
		}
	}

	public static void init() {
		ResearchCategories.registerCategory("MECHANICS", "UNLOCKARTIFICE",
				new AspectList(),
				new ResourceLocation(MysticalGears.MODID, "textures/icons/mechanics.png"),
				new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_4.jpg"),
				new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"));
		
		ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/mechanics"));

		if (ConfigHandler.visMotor) {
			ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/vis_motor"));
			ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_vis_motor"), new ShapedArcaneRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_vis_motor"), "VISMOTOR", 20, new AspectList(), visMotor, new Object[]{"GA ", "AB ", 'G', "gearIron", 'A', new ItemStack(RegistryHandler.IRON_AXLE), 'B', "sus"}));
			GameRegistry.registerTileEntity(TileEntityVisMotor.class, new ResourceLocation(MysticalGears.MODID, "vis_motor"));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
	}
}
