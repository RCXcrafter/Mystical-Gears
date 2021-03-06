package com.rcx.mystgears.item;

import javax.annotation.Nullable;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IGearData;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGooglyEye extends ItemGear {

	public ItemGooglyEye() {
		super("googly_eye");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

	public void registerRecipe() {
		GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" W ", "WDW", " W ", 'W', "woolWhite", 'D', "dyeBlack"});
	}

	public void registerGear() {
		MysticalMechanicsAPI.IMPL.registerGear(this.getRegistryName(), Ingredient.fromItem(this), new IGearBehavior() {
			@Override
			public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				return 0;
			}

			@Override
			public double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				return power;
			}

			@Override
			public double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
				return power;
			}
		});
	}

	public void registerOredict() {}

	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}
}
