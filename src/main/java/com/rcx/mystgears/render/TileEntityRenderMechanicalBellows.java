package com.rcx.mystgears.render;

import javax.annotation.Nullable;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.TileEntityMechanicalBellows;
import com.rcx.mystgears.compatibility.BotaniaCompat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class TileEntityRenderMechanicalBellows extends TileEntitySpecialRenderer<TileEntityMechanicalBellows> {

	private static final float[] ROTATIONS = new float[] {
			180F, 0F, 90F, 270F
	};

	private static final ResourceLocation texture = new ResourceLocation(MysticalGears.MODID, "textures/model/bellows_mechanical.png");
	private static final ModelMechanicalBellows model = new ModelMechanicalBellows();

	@Override
	public void render(@Nullable TileEntityMechanicalBellows bellows, double d0, double d1, double d2, float f, int digProgress, float unused) {
		if (bellows != null) {
			if (bellows.getWorld() == null) {
				bellows = null;
			} else if (!bellows.getWorld().isBlockLoaded(bellows.getPos(), false) || bellows.getWorld().getBlockState(bellows.getPos()).getBlock() != BotaniaCompat.bellows.getBlock())
				return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.translate(d0, d1, d2);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		int meta = bellows != null && bellows.getWorld() != null ? bellows.getBlockMetadata() : 0;

		GlStateManager.translate(0.5F, 1.5F, 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.rotate(ROTATIONS[Math.max(Math.min(ROTATIONS.length, meta - 2), 0)], 0F, 1F, 0F);
		model.render(Math.max(0.1F, 1F - (bellows == null ? 0 : bellows.movePos + bellows.moving * f + 0.1F)));
		GlStateManager.color(1F, 1F, 1F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.enableRescaleNormal();
		GlStateManager.popMatrix();
	}
}
