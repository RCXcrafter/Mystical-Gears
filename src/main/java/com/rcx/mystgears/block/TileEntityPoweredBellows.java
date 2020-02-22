package com.rcx.mystgears.block;

import com.codetaylor.mc.pyrotech.modules.tech.machine.tile.TileMechanicalBellows;

import net.minecraft.tileentity.TileEntity;

public class TileEntityPoweredBellows extends TileMechanicalBellows {

	@Override
	protected boolean shouldProgress() {

		TileEntity tileEntity = this.world.getTileEntity(this.pos.up());

		if (tileEntity instanceof TileEntityPoweredBellowsTop) {
			return ((TileEntityPoweredBellowsTop) tileEntity).isPushing();
		}

		return false;
	}
}
