package com.rcx.mystgears.render;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.entity.EntityTurret;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTurret<T extends EntityTurret> extends RenderLivingBase<T> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(MysticalGears.MODID, "textures/model/turret_base_iron.png");
	private static final Logger LOGGER = LogManager.getLogger();

	public RenderTurret(RenderManager manager) {
		super(manager, new ModelTurret(), 0.0f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTurret entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		try {
			float f2 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
			float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
			this.renderLivingAt(entity, x, y, z);
			float f8 = this.handleRotationFloat(entity, partialTicks);
			float f4 = this.prepareScale(entity, partialTicks);
			float f5 = 0.0F;
			float f6 = 0.0F;

			GlStateManager.enableAlpha();
			this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);

			if (this.renderOutlines) {
				boolean flag1 = this.setScoreTeamColor(entity);
				GlStateManager.enableColorMaterial();
				GlStateManager.enableOutlineMode(this.getTeamColor(entity));

				this.renderModel(entity, f6, f5, f8, f2, f7, f4);

				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();

				if (flag1) {
					this.unsetScoreTeamColor();
				}
			} else {
				boolean flag = this.setDoRenderBrightness(entity, partialTicks);
				this.renderModel(entity, f6, f5, f8, f2, f7, f4);

				if (flag) {
					this.unsetBrightness();
				}
				GlStateManager.depthMask(true);
			}
			GlStateManager.disableRescaleNormal();
		} catch (Exception exception) {
			LOGGER.error("Couldn't render entity", (Throwable)exception);
		}
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}
}