package com.rcx.mystgears.item;

import org.apache.commons.lang3.StringUtils;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.ConfigHandler;
import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import teamroots.embers.ConfigManager;
import teamroots.embers.RegistryManager;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

public class ItemGear extends Item {

	public static ResourceLocation group = new ResourceLocation("");
	public String name;
	public IGearBehavior behavior = null;

	public ItemGear(String name, IGearBehavior behavior) {
		super();
		this.name = name;
		this.behavior = behavior;
		this.setCreativeTab(CreativeTabs.REDSTONE);
		this.setUnlocalizedName("gear_" + name.toLowerCase());
		this.setRegistryName(MysticalGears.MODID, "gear_" + name.toLowerCase());
	}

	public ItemGear(String name) {
		super();
		this.name = name;
		this.setCreativeTab(CreativeTabs.REDSTONE);
		this.setUnlocalizedName("gear_" + name.toLowerCase());
		this.setRegistryName(MysticalGears.MODID, "gear_" + name.toLowerCase());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		String material = name;
		if(I18n.canTranslate("material." + name.toLowerCase() + ".name")) {
			material = I18n.translateToLocal("material." + name.toLowerCase() + ".name");
		} else {
			String[] looseWords = StringUtils.splitByCharacterTypeCamelCase(material);
			material = looseWords[0];
			for(String word : looseWords) {
				if(word.equals(looseWords[0]))
					continue;
				material = material + " " + word;
			}
		}

		if(I18n.canTranslate("item.mystgears.gear." + name  + ".name")) {
			return I18n.translateToLocal("item.mystgears.gear." + name  + ".name");
		}

		return I18n.translateToLocal("item.mystgears.gear.name").replace("@material", material);
	}

	public void registerRecipe() {
		GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "INI", " I ", 'I', "ingot" + name, 'N', "nugget" + name});
		if (ConfigHandler.embers && FluidRegistry.isFluidRegistered(name.toLowerCase())) {
			RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack(name.toLowerCase(), ConfigManager.stampGearAmount * RecipeRegistry.INGOT_AMOUNT), Ingredient.fromItem(RegistryManager.stamp_gear), new ItemStack(this, 1)));
		}
	}

	public void registerGear() {
		if (behavior != null)
			MysticalMechanicsAPI.IMPL.registerGear(this.getRegistryName(), new OreIngredient("gear" + name), behavior);
	}

	public void registerOredict() {
		OreDictionary.registerOre("gear" + name, new ItemStack(this));
	}

	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}
}
