package com.rcx.mystgears;

import java.util.Map;

import com.rcx.mystgears.block.BlockGearanium;
import com.rcx.mystgears.block.BlockMechanicalBellows;
import com.rcx.mystgears.block.TileEntityGearanium;
import com.rcx.mystgears.block.TileEntityMechanicalBellows;
import com.rcx.mystgears.item.ItemBlockGearanium;
import com.rcx.mystgears.item.ItemGear;
import com.rcx.mystgears.render.TileEntityRenderMechanicalBellows;

import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.client.core.handler.ModelHandler;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.lexicon.BasicLexiconEntry;
import vazkii.botania.common.lexicon.page.PageCraftingRecipe;
import vazkii.botania.common.lexicon.page.PagePetalRecipe;
import vazkii.botania.common.lexicon.page.PageText;
import vazkii.botania.common.lib.LibOreDict;

public class BotaniaCompat {

	public static ItemBlockGearanium gearanium;
	public static ItemBlock bellows;
	public static LexiconEntry gearaniumEntry;
	public static LexiconEntry bellowsEntry;

	public static void preInit() {
		if (ConfigHandler.gearanium) {
			gearanium = new ItemBlockGearanium(new BlockGearanium());
			MysticalGears.blocks.add((ItemBlock) gearanium.setRegistryName(gearanium.getBlock().getRegistryName()));
		}
		if (ConfigHandler.bellows) {
			bellows = new ItemBlock(new BlockMechanicalBellows());
			MysticalGears.blocks.add((ItemBlock) bellows.setRegistryName(bellows.getBlock().getRegistryName()));
		}
	}

	public static void init() {
		if (ConfigHandler.gearanium) {
			GameRegistry.registerTileEntity(TileEntityGearanium.class, new ResourceLocation(MysticalGears.MODID, "gearanium"));

			RecipePetals gearaniumRecipe = BotaniaAPI.registerPetalRecipe(new ItemStack(gearanium), LibOreDict.PETAL[2], LibOreDict.PETAL[2], LibOreDict.PETAL[10], LibOreDict.PETAL[10], LibOreDict.REDSTONE_ROOT, "gearManasteel");

			gearaniumEntry = new BasicLexiconEntry("gearanium", BotaniaAPI.categoryFunctionalFlowers);
			gearaniumEntry.setLexiconPages(new PageText("0"), new PagePetalRecipe<>("1", gearaniumRecipe));
			LexiconRecipeMappings.map(new ItemStack(gearanium), gearaniumEntry, 0);
		}
		if (ConfigHandler.bellows) {
			GameRegistry.registerTileEntity(TileEntityMechanicalBellows.class, new ResourceLocation(MysticalGears.MODID, "bellows_mechanical"));

			GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_bellows_mechanical"), ItemGear.group, new ItemStack(bellows), new Object[]{"GA ", "AB ", 'G', "gearIron", 'A', new ItemStack(RegistryHandler.IRON_AXLE), 'B', new ItemStack(ModBlocks.bellows)});

			bellowsEntry = new BasicLexiconEntry("bellows_mechanical", BotaniaAPI.categoryMana);
			bellowsEntry.setLexiconPages(new PageText("0"), new PageCraftingRecipe("1", new ResourceLocation(MysticalGears.MODID, "recipe_bellows_mechanical")));
			LexiconRecipeMappings.map(new ItemStack(bellows), bellowsEntry, 0);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		if (ConfigHandler.gearanium)
			ModelLoader.setCustomStateMapper(gearanium.getBlock(), new IStateMapper() {
				@Override
				public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
					return new StateMap.Builder().ignore(BotaniaStateProps.COLOR, ((BlockFlower) gearanium.getBlock()).getTypeProperty()).build().putStateModelLocations(blockIn);
				}
			});
		if (ConfigHandler.bellows) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMechanicalBellows.class, new TileEntityRenderMechanicalBellows());
			ModelLoader.setCustomStateMapper(bellows.getBlock(), new StateMap.Builder().ignore(BotaniaStateProps.CARDINALS).build());
			ModelHandler.registerCustomItemblock(bellows.getBlock(), "bellows");
			bellows.setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
				public void renderByItem(ItemStack p_192838_1_, float partialTicks) {
					TileEntityRendererDispatcher.instance.render(new TileEntityMechanicalBellows(), 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
				}
			});
		}
	}
}
