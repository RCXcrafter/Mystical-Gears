package com.rcx.mystgears.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.rcx.mystgears.GearBehaviorRegular;

import mysticalmechanics.api.IGearData;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

public class ItemFlywheel extends ItemGear {

	public double acceleration;
	public String oredictName;

	public ItemFlywheel(String name, double acceleration) {
		super("flywheel_" + name.toLowerCase());
		this.acceleration = acceleration;
		this.oredictName = "gearFlywheel" + name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

	public void registerGear() {
		MysticalMechanicsAPI.IMPL.registerGear(this.getRegistryName(), new OreIngredient(oredictName), new GearBehaviorRegular(0, 1) {
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
				return new FlywheelGearData(acceleration);
			}
		});
	}

	public void registerOredict() {
		OreDictionary.registerOre(oredictName, new ItemStack(this));
	}

	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(@Nonnull ItemStack par1ItemStack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
		tooltip.add(I18n.translateToLocal("desc.flywheel.name").trim());
	}

	public static class FlywheelGearData implements IGearData {

		public double power = 0;
		public boolean dirty = false;
		public double acceleration;

		public FlywheelGearData(double acceleration) {
			this.acceleration = acceleration;
		}

		public void setPower(double powerIn) {
			if (powerIn == power)
				return;
			power += Math.max(Math.min(powerIn - power, acceleration), -acceleration);

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
