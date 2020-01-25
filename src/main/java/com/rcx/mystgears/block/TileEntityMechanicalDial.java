package com.rcx.mystgears.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import teamroots.embers.block.BlockFluidGauge;
import teamroots.embers.tileentity.TileEntityBaseGauge;

public class TileEntityMechanicalDial extends TileEntityBaseGauge {

	@Override
	public int calculateComparatorValue(TileEntity tileEntity, EnumFacing facing) {
		return 0;
	}

	@Override
	public String getDialType() {
		return BlockFluidGauge.DIAL_TYPE;
	}

	@Override
	public void update() {}
}