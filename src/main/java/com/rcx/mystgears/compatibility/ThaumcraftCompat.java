package com.rcx.mystgears.compatibility;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockEssentiaMotor;
import com.rcx.mystgears.block.BlockMechanicalCrafter;
import com.rcx.mystgears.block.BlockVisMotor;
import com.rcx.mystgears.block.TileEntityEssentiaMotor;
import com.rcx.mystgears.block.TileEntityMechanicalCrafter;
import com.rcx.mystgears.block.TileEntityVisMotor;
import com.rcx.mystgears.item.ItemGear;

import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;

public class ThaumcraftCompat {

	public static ItemBlock visMotor;
	public static ItemBlock essentiaMotor;

	public static ItemBlock mechanicalCrafter;

	public static void preInit() {
		if (ConfigHandler.visMotor) {
			visMotor = new ItemBlock(new BlockVisMotor());
			MysticalGears.blocks.add((ItemBlock) visMotor.setRegistryName(visMotor.getBlock().getRegistryName()));

			if (ConfigHandler.essentiaMotor) {
				essentiaMotor = new ItemBlock(new BlockEssentiaMotor());
				MysticalGears.blocks.add((ItemBlock) essentiaMotor.setRegistryName(essentiaMotor.getBlock().getRegistryName()));
			}
		}
		if (ConfigHandler.mechanicalCrafter) {
			mechanicalCrafter = new ItemBlock(new BlockMechanicalCrafter());
			MysticalGears.blocks.add((ItemBlock) mechanicalCrafter.setRegistryName(mechanicalCrafter.getBlock().getRegistryName()));	
		}
	}

	public static void init() {
		ResearchCategories.registerCategory("MECHANICS", "UNLOCKARTIFICE", new AspectList(), new ResourceLocation(MysticalGears.MODID, "textures/icons/mechanics.png"), new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_4.jpg"), new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"));

		ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/mechanics"));

		if (ConfigHandler.visMotor) {
			ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/vis_motor"));
			ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_vis_motor"), new ShapedArcaneRecipe(ItemGear.group, "VISMOTOR", 25, new AspectList().add(Aspect.ORDER, 1), visMotor, new Object[]{"PRP", "NAN", 'R', new ItemStack(ItemsTC.visResonator), 'N', "nuggetIron", 'A', new ItemStack(RegistryHandler.IRON_AXLE), 'P', "plankWood"}));
			GameRegistry.registerTileEntity(TileEntityVisMotor.class, new ResourceLocation(MysticalGears.MODID, "vis_motor"));

			if (ConfigHandler.essentiaMotor) {
				ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/essentia_motor"));
				ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_essentia_motor"), new ShapedArcaneRecipe(ItemGear.group, "ESSENTIAMOTOR", 35, new AspectList().add(Aspect.AIR, 1).add(Aspect.ORDER, 1), essentiaMotor, new Object[]{"PTP", "BMB", "PFP", 'M', new ItemStack(ItemsTC.morphicResonator), 'P', new ItemStack(BlocksTC.plankGreatwood), 'F', new ItemStack(RegistryHandler.GEARBOX_FRAME), 'T', new ItemStack(BlocksTC.tube), 'B', "plateBrass"}));
				GameRegistry.registerTileEntity(TileEntityEssentiaMotor.class, new ResourceLocation(MysticalGears.MODID, "essentia_motor"));
			}
		}
		if (ConfigHandler.mechanicalCrafter) {
			ThaumcraftApi.registerResearchLocation(new ResourceLocation(MysticalGears.MODID, "research/mechanical_crafter"));
			ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_mechanical_crafter"), new ShapedArcaneRecipe(ItemGear.group, "MECHANICALCRAFTER", 30, new AspectList().add(Aspect.EARTH, 1).add(Aspect.WATER, 1).add(Aspect.ORDER, 1), mechanicalCrafter, new Object[]{"AH ", "MCM", " P ", 'M', new ItemStack(ItemsTC.mechanismSimple), 'H', new ItemStack(Blocks.HOPPER), 'C', "workbench", 'A', new ItemStack(RegistryHandler.IRON_AXLE), 'P', new ItemStack(BlocksTC.plankGreatwood)}));
			GameRegistry.registerTileEntity(TileEntityMechanicalCrafter.class, new ResourceLocation(MysticalGears.MODID, "mechanical_crafter"));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
	}
}
