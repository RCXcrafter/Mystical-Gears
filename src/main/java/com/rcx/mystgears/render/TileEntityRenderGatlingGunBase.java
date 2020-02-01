package com.rcx.mystgears.render;

import com.rcx.mystgears.block.BlockGatlingGunBase;
import com.rcx.mystgears.block.BlockGatlingGunEmber;
import com.rcx.mystgears.block.TileEntityGatlingGunBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TileEntityRenderGatlingGunBase extends TileEntitySpecialRenderer<TileEntityGatlingGunBase> {

	public TileEntityRenderGatlingGunBase(){
		super();
	}

	@Override
	public void render(TileEntityGatlingGunBase tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		Block block = state.getBlock();
		if (block instanceof BlockGatlingGunBase) {
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
			IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "facing=north"));

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);

			switch (state.getValue(BlockGatlingGunEmber.FACING)) {
			case DOWN:
				GlStateManager.rotate(-90, 1, 0, 0);
				break;
			case UP:
				GlStateManager.rotate(90, 1, 0, 0);
				break;
			case NORTH:
				break;
			case WEST:
				GlStateManager.rotate(90, 0, 1, 0);
				break;
			case SOUTH:
				GlStateManager.rotate(180, 0, 1, 0);
				break;
			case EAST:
				GlStateManager.rotate(270, 0, 1, 0);
				break;
			default:
				break;
			}

			double angle = tile.angle;
			double lastAngle = tile.lastAngle;

			GlStateManager.rotate(((float) (partialTicks * angle) + (1 - partialTicks) * (float) lastAngle), 0, 0, 1);
			GlStateManager.translate(-0.5, -0.5, -0.5);
			blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}
}