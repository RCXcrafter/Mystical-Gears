package com.rcx.mystgears.compatibility;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockEssentiaMotor;
import com.rcx.mystgears.block.BlockVisMotor;
import com.rcx.mystgears.block.TileEntityEssentiaMotor;
import com.rcx.mystgears.block.TileEntityVisMotor;

import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;

public class ThaumcraftCompat {

	public static ItemBlock visMotor;
	public static ItemBlock essentiaMotor;

	public static void preInit() {
		if (ConfigHandler.visMotor) {
			visMotor = new ItemBlock(new BlockVisMotor());
			MysticalGears.blocks.add((ItemBlock) visMotor.setRegistryName(visMotor.getBlock().getRegistryName()));
			
			if (ConfigHandler.essentiaMotor) {
				essentiaMotor = new ItemBlock(new BlockEssentiaMotor());
				MysticalGears.blocks.add((ItemBlock) essentiaMotor.setRegistryName(essentiaMotor.getBlock().getRegistryName()));
			}
		}
	}

	public static void init() {
		ResearchCategories.registerCategory("MECHANICS", "UNLOCKARTIFICE", new AspectList(), new ResourceLocation(MysticalGears.MODID, "textures/icons/mechanics.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_4.jpg"), new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"));

		ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/mechanics"));

		if (ConfigHandler.visMotor) {
			ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/vis_motor"));
			ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_vis_motor"), new ShapedArcaneRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_vis_motor"), "VISMOTOR", 20, new AspectList().add(Aspect.ORDER, 1), visMotor, new Object[]{"PRP", "EAE", 'R', new ItemStack(ItemsTC.visResonator), 'E', new ItemStack(ItemsTC.nuggets, 1, 10), 'A', new ItemStack(RegistryHandler.IRON_AXLE), 'P', "plankWood"}));
			GameRegistry.registerTileEntity(TileEntityVisMotor.class, new ResourceLocation(MysticalGears.MODID, "vis_motor"));

			if (ConfigHandler.essentiaMotor) {
				ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/essentia_motor"));
				ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_essentia_motor"), new ShapedArcaneRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_essentia_motor"), "ESSENTIAMOTOR", 20, new AspectList().add(Aspect.ORDER, 1), essentiaMotor, new Object[]{"PRP", "NAN", 'R', new ItemStack(ItemsTC.visResonator), 'N', "nuggetIron", 'A', new ItemStack(RegistryHandler.IRON_AXLE), 'P', "plankWood"}));
				GameRegistry.registerTileEntity(TileEntityEssentiaMotor.class, new ResourceLocation(MysticalGears.MODID, "essentia_motor"));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
	}
}
