package com.rcx.mystgears.render;

import com.rcx.mystgears.block.TileEntityGatlingGunBase;

public class TileEntityRenderGatlingGunEmber extends TileEntityRenderGatlingGunBase {

	public TileEntityRenderGatlingGunEmber(){
		super();
	}

	@Override
	public void render(TileEntityGatlingGunBase tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

	}
}