package com.rcx.mystgears.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.rcx.mystgears.GearBehaviorRegular;
import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.api.IGearData;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFlywheel extends ItemGear {

	public static double acceleration = 1.0;

	public ItemFlywheel() {
		super("flywheel");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

	public void registerRecipe() {
		GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "IGI", " I ", 'G', "gearDiamond", 'I', "ingotIron"});
	}

	public void registerGear() {
		MysticalMechanicsAPI.IMPL.registerGear(this.getRegistryName(), Ingredient.fromItem(this), new GearBehaviorRegular(700, 1) {
			@Override
			public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				if (data != null && data instanceof FlywheelGearData)
					return super.transformPower(tile, facing, gear, data, ((FlywheelGearData) data).power);
				return super.transformPower(tile, facing, gear, data, power);
			}
			@Override
			public double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				return this.transformPower(tile, facing, gear, data, power);
			}

			@Override
			public void tick(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double powerIn, double powerOut) {
				if(data instanceof FlywheelGearData) {
					((FlywheelGearData) data).setPower(powerIn);
				}
			}

			@Override
			public boolean canTick(ItemStack gear) {
				return true;
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

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(@Nonnull ItemStack par1ItemStack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
		tooltip.add(net.minecraft.client.resources.I18n.format("desc.flywheel.name"));
	}

	public static class FlywheelGearData implements IGearData {

		public double power = 0;
		public boolean dirty = false;

		public void setPower(double powerIn) {
			double delta = powerIn - power;
			power += Math.signum(delta) * Math.min(Math.abs(delta), acceleration);
			if (!dirty && powerIn != power)
				dirty = true;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			power = tag.getDouble("flywheel_power");
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setDouble("flywheel_power", power);
			return tag;
		}

		@Override
		public boolean isDirty() {
			return dirty;
		}
	}
}
