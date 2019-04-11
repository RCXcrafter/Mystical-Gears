package com.rcx.mystgears.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class ModelMechanicalBellows extends ModelBase {

	public ModelRenderer top;
	public ModelRenderer funnel;
	public ModelRenderer pipe;
	public ModelRenderer base;
	public ModelRenderer axle;
	public ModelRenderer input;
	public ModelRenderer top_1;

	public ModelMechanicalBellows() {
		textureWidth = 64;
		textureHeight = 32;

		top = new ModelRenderer(this, 0, 0);
		top.setRotationPoint(0.0F, 16.0F, 0.0F);
		top.addBox(-4.0F, -2.0F, -4.0F, 8, 1, 8, 0.0F);
		base = new ModelRenderer(this, 0, 9);
		base.setRotationPoint(0.0F, 16.0F, 0.0F);
		base.addBox(-5.0F, 6.0F, -5.0F, 10, 2, 10, 0.0F);
		pipe = new ModelRenderer(this, 0, 21);
		pipe.setRotationPoint(0.0F, 16.0F, 0.0F);
		pipe.addBox(-1.0F, 6.0F, -8.0F, 2, 2, 3, 0.0F);

		funnel = new ModelRenderer(this, 40, 0);
		funnel.setRotationPoint(0.0F, 0.0F, 0.0F);
		funnel.addBox(0.0F, 0.0F, 0.0F, 6, 7, 6, 0.0F);

		top_1 = new ModelRenderer(this, 4, 21);
		top_1.setRotationPoint(0.0F, 11.0F, 0.0F);
		top_1.addBox(-2.0F, -1.0F, -2.0F, 4, 3, 6, 0.0F);
		axle = new ModelRenderer(this, 40, 17);
		axle.setRotationPoint(0.0F, 8.0F, 0.0F);
		axle.addBox(-1.0F, 5.0F, -1.0F, 2, 8, 2, 0.0F);
		input = new ModelRenderer(this, 48, 14);
		input.setRotationPoint(0.0F, 0.0F, 0.0F);
		input.addBox(-2.0F, 10.0F, 4.0F, 4, 14, 4, 0.0F);
	}

	public void render(float fract) {
		float f5 = 1F / 16F;
		base.render(f5);
		pipe.render(f5);
		top_1.render(f5);
        axle.render(f5);
        input.render(f5);

		//float fract = Math.max(0.1F, (float) (Math.sin(((double) ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.2) + 1F) * 0.5F);
		float mov = (1F - fract) * 0.5F;

		GlStateManager.translate(0F, mov, 0F);
		top.render(f5);
		GlStateManager.translate(0F, -mov, 0F);

		GlStateManager.rotate(180F, 1F, 0F, 0F);
		GlStateManager.translate(-0.19F, -1.375F, -0.19F);
		GlStateManager.scale(1F, fract, 1F);
		funnel.render(f5);
		GlStateManager.scale(1F, 1F / fract, 1F);
	}
}
