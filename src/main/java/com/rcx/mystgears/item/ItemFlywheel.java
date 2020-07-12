package com.rcx.mystgears.item;

import javax.annotation.Nullable;

import com.rcx.mystgears.GearBehaviorRegular;
import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IGearData;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.VarGearBehavior;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFlywheel extends ItemGear {

	public ItemFlywheel() {
		super("flywheel");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

	public void registerRecipe() {
		GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" B ", "BGB", " B ", 'G', "gearIron", 'B', "blockIron"});
	}

	public void registerGear() {
		MysticalMechanicsAPI.IMPL.registerGear(this.getRegistryName(), Ingredient.fromItem(this), new GearBehaviorRegular(0, 1) {
			@Override
			public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				if (data != null && data instanceof FlywheelGearData)
					return ((FlywheelGearData) data).transformPower(super.transformPower(tile, facing, gear, data, power));
				return super.transformPower(tile, facing, gear, data, power);
			}
			@Override
			public double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				return this.transformPower(tile, facing, gear, data, power);
			}

			@Override
			public boolean hasData() {
				return true;
			}

			@Override
			public IGearData createData() {
				return new FlywheelGearData();
			}
		});
	}

	public void registerOredict() {}

	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	public static class FlywheelGearData implements IGearData {

		public double previousPower = 0;
		public boolean dirty = false;

		public double transformPower(double power) {
			previousPower += Math.min(power - previousPower, 1.0);
			if (!dirty && power != previousPower)
				dirty = true;
			return previousPower;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			previousPower = tag.getDouble("flywheel_power");
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setDouble("flywheel_power", previousPower);
			return tag;
		}

		@Override
		public boolean isDirty() {
			return dirty;
		}
	}
}
