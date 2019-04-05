package com.rcx.mystgears;

import java.util.Map;

import com.rcx.mystgears.block.BlockGearanium;
import com.rcx.mystgears.block.TileEntityGearanium;
import com.rcx.mystgears.item.ItemBlockGearanium;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.common.lexicon.BasicLexiconEntry;
import vazkii.botania.common.lexicon.page.PagePetalRecipe;
import vazkii.botania.common.lexicon.page.PageText;
import vazkii.botania.common.lib.LibOreDict;

public class BotaniaCompat {

	public static ItemBlockGearanium gearanium;
	public static LexiconEntry gearaniumEntry;

	public static void preInit() {
		if (ConfigHandler.gearanium) {
			BotaniaCompat.gearanium = new ItemBlockGearanium(new BlockGearanium());
			MysticalGears.blocks.add((ItemBlock) BotaniaCompat.gearanium.setRegistryName(BotaniaCompat.gearanium.getBlock().getRegistryName()));
		}
	}

	public static void init() {
		if (ConfigHandler.gearanium) {
			GameRegistry.registerTileEntity(TileEntityGearanium.class, new ResourceLocation(MysticalGears.MODID, "gearanium"));

			RecipePetals gearaniumRecipe = BotaniaAPI.registerPetalRecipe(new ItemStack(BotaniaCompat.gearanium), LibOreDict.PETAL[2], LibOreDict.PETAL[2], LibOreDict.PETAL[10], LibOreDict.PETAL[10], LibOreDict.REDSTONE_ROOT, "gearManasteel");

			BotaniaCompat.gearaniumEntry = new BasicLexiconEntry("gearanium", BotaniaAPI.categoryFunctionalFlowers);
			BotaniaCompat.gearaniumEntry.setLexiconPages(new PageText("0"), new PagePetalRecipe<>("1", gearaniumRecipe));
			LexiconRecipeMappings.map(new ItemStack(BotaniaCompat.gearanium), BotaniaCompat.gearaniumEntry, 0);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		if (ConfigHandler.gearanium)
			ModelLoader.setCustomStateMapper(BotaniaCompat.gearanium.getBlock(), new IStateMapper() {
				@Override
				public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
					return new StateMap.Builder().ignore(BotaniaStateProps.COLOR, ((BlockFlower) BotaniaCompat.gearanium.getBlock()).getTypeProperty()).build().putStateModelLocations(blockIn);
				}
			});
	}
}
