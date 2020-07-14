package com.rcx.mystgears;

import java.util.Random;

import javax.annotation.Nullable;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IGearData;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import teamroots.embers.particle.ParticleUtil;

public class GearBehaviorRegular implements IGearBehavior {

	public double maxPower;
	public double powerTransfer;
	public String extraLore;
	IGearBehavior base = null;
	static Random random = new Random();

	public GearBehaviorRegular(double max, double transfer, String lore) {
		maxPower = max;
		powerTransfer = transfer;
		extraLore = lore;
	}

	public GearBehaviorRegular(double max, double transfer) {
		maxPower = max;
		powerTransfer = transfer;
	}

	public IGearBehavior setBase(IGearBehavior base) {
		this.base = base;
		return this;
	}

	@Override
	public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
		//if (base != null)
		//	power = base.transformPower(tile, facing, gear, data, power);
		if (power > maxPower && maxPower != 0)
			return maxPower * powerTransfer + Math.log10(power - maxPower + Math.log10(Math.exp(1)) / powerTransfer) - Math.log10(Math.log10(Math.exp(1)) / powerTransfer);

		return power * powerTransfer;
	}

	@Override
	public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double powerIn, double powerOut) {
		if (base != null)
			base.visualUpdate(tile, facing, gear, data, powerIn, powerOut);
		if (ConfigHandler.smoke && facing != null && tile.getWorld().isRemote) {
			Boolean doIt = random.nextInt(5) == 0;
			if(doIt && powerOut > maxPower * powerTransfer && maxPower != 0) {
				float xOff = 0.1f + random.nextFloat() * 0.8f;
				float yOff = 0.1f + random.nextFloat() * 0.8f;
				float zOff = 0.1f + random.nextFloat() * 0.8f;
				switch (facing.getAxis()) {
				case X:
					xOff = 0.5f + facing.getFrontOffsetX() / 2.0f; break;
				case Y:
					yOff = 0.5f + facing.getFrontOffsetY() / 2.0f; break;
				case Z:
					zOff = 0.5f + facing.getFrontOffsetZ() / 2.0f; break;
				}
				if (ConfigHandler.embers)
					ParticleUtil.spawnParticleSmoke(tile.getWorld(), tile.getPos().getX() + xOff, tile.getPos().getY() + yOff, tile.getPos().getZ() + zOff, 0, 0, 0, 128, 128, 128, 0.4f, random.nextFloat() * 4.0f + 4.0f, 40);
				else
					tile.getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, tile.getPos().getX() + xOff, tile.getPos().getY() + yOff, tile.getPos().getZ() + zOff, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}
