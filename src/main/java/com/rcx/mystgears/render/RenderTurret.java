package com.rcx.mystgears.render;

import java.nio.FloatBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.entity.EntityTurret;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTurret<T extends EntityTurret> extends Render<T> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(MysticalGears.MODID, "textures/model/turret_base_iron.png");
	private static final DynamicTexture TEXTURE_BRIGHTNESS = new DynamicTexture(16, 16);
	public ModelBase mainModel = new ModelTurret();
	protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
	private static final Logger LOGGER = LogManager.getLogger();

	public RenderTurret(RenderManager manager) {
		super(manager);
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
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public float prepareScale(T entitylivingbaseIn, float partialTicks) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		float f = 0.0625F;
		GlStateManager.translate(0.0F, -1.501F, 0.0F);
		return 0.0625F;
	}

	protected boolean setScoreTeamColor(T entityLivingBaseIn) {
		GlStateManager.disableLighting();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		return true;
	}

	protected void unsetScoreTeamColor() {
		GlStateManager.enableLighting();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	/**
	 * Renders the model in RenderLiving
	 */
	protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		boolean flag = this.isVisible(entitylivingbaseIn);
		boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player);
		if (flag || flag1) {
			if (!this.bindEntityTexture(entitylivingbaseIn)) {
				return;
			}
			if (flag1) {
				GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}
			this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
			if (flag1) {
				GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}
		}
	}

	protected boolean isVisible(T p_193115_1_) {
		return !p_193115_1_.isInvisible() || this.renderOutlines;
	}

	protected boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks) {
		return this.setBrightness(entityLivingBaseIn, partialTicks, true);
	}

	protected boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
		float f = entitylivingbaseIn.getBrightness();
		int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
		boolean flag = (i >> 24 & 255) > 0;

		if (!flag) {
			return false;
		} else if (!flag && !combineTextures) {
			return false;
		} else {
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			GlStateManager.enableTexture2D();
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.enableTexture2D();
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			this.brightnessBuffer.position(0);
			float f1 = (float)(i >> 24 & 255) / 255.0F;
			float f2 = (float)(i >> 16 & 255) / 255.0F;
			float f3 = (float)(i >> 8 & 255) / 255.0F;
			float f4 = (float)(i & 255) / 255.0F;
			this.brightnessBuffer.put(f2);
			this.brightnessBuffer.put(f3);
			this.brightnessBuffer.put(f4);
			this.brightnessBuffer.put(1.0F - f1);
			this.brightnessBuffer.flip();
			GlStateManager.glTexEnv(8960, 8705, this.brightnessBuffer);
			GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
			GlStateManager.enableTexture2D();
			GlStateManager.bindTexture(TEXTURE_BRIGHTNESS.getGlTextureId());
			GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			return true;
		}
	}

	protected void unsetBrightness() {
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
		GlStateManager.disableTexture2D();
		GlStateManager.bindTexture(0);
		GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
		GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	/**
	 * Sets a simple glTranslate on a LivingEntity.
	 */
	protected void renderLivingAt(T entityIn, double x, double y, double z) {
		GlStateManager.translate((float)x, (float)y, (float)z);
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	protected float handleRotationFloat(T livingBase, float partialTicks) {
		return (float)livingBase.ticksExisted + partialTicks;
	}

	/**
	 * Gets an RGBA int color multiplier to apply.
	 */
	protected int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime) {
		return 0;
	}
}