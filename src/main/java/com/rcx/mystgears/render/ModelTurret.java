package com.rcx.mystgears.render;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.TileEntityTurret;

import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ModelTurret extends ModelBase {

	private final ModelRenderer base;
	private final ModelRenderer attachment1;
	private final ModelRenderer attachment2;
	private final ModelRenderer rotated_panel;
	private final ModelRenderer ladder_sides;
	private final ModelRenderer headrest_sticks;
	private final ModelRenderer arm;
	private final ModelRenderer attachment3;
	private final ModelRenderer attachment4;
	private final ModelRenderer rotated_arm_panel;
	private final ModelRenderer gear_rack;
	private final ModelRenderer segment1;
	private final ModelRenderer segment2;
	private final ModelRenderer segment3;
	private final ModelRenderer segment4;
	private final ModelRenderer segment5;
	private final ModelRenderer segment6;
	private final ModelRenderer segment7;
	private final ModelRenderer segment8;
	private final ModelRenderer segment9;
	private final ModelRenderer segment10;
	private final ModelRenderer segment11;
	private final ModelRenderer segment12;
	private final ModelRenderer segment13;

	public ModelTurret() {
		textureWidth = 128;
		textureHeight = 128;

		base = new ModelRenderer(this);
		base.setRotationPoint(0.0F, 19.0F, 0.0F);
		base.cubeList.add(new ModelBox(base, 0, 0, -9.0F, -1.0F, -5.0F, 15, 2, 10, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 42, 42, -4.0F, -11.0F, -3.0F, 8, 7, 6, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 12, 49, -2.0F, -20.0F, -2.0F, 4, 19, 4, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 31, 32, 2.0F, -18.0F, -2.0F, 13, 4, 4, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 70, 10, -6.2F, -17.0F, -3.0F, 3, 1, 6, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 70, 10, -7.0F, -11.0F, -3.0F, 3, 1, 6, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 70, 10, -7.8F, -5.0F, -3.0F, 3, 1, 6, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 24, 15.0F, -33.0F, -3.0F, 2, 23, 6, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 64, 33, 14.5F, -25.0F, -3.5F, 3, 4, 7, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 12, 17.0F, -31.0F, -1.0F, 2, 2, 2, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 12, 17.0F, -17.0F, -1.0F, 2, 2, 2, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 0, 19.0F, -17.5F, -1.5F, 1, 3, 3, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 0, 12, -5.0F, -22.0F, -5.0F, 10, 2, 10, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 40, 0, -4.0F, -23.0F, -4.0F, 8, 1, 6, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 28, 55, -5.0F, -35.0F, 3.0F, 10, 13, 2, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 28, 70, -4.0F, -34.0F, 2.0F, 8, 12, 1, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 46, 70, -4.0F, -44.0F, 5.0F, 8, 6, 2, 0.0F, false));
		base.cubeList.add(new ModelBox(base, 74, 64, -3.0F, -43.0F, 4.0F, 6, 4, 1, 0.0F, false));

		attachment1 = new ModelRenderer(this);
		attachment1.setRotationPoint(6.0F, -0.5F, 0.0F);
		base.addChild(attachment1);
		setRotationAngle(attachment1, 0.0F, 0.0F, -0.2618F);
		attachment1.cubeList.add(new ModelBox(attachment1, 61, 0, -2.0F, -1.5F, -3.5F, 4, 3, 7, 0.0F, false));

		attachment2 = new ModelRenderer(this);
		attachment2.setRotationPoint(15.5F, -10.0F, 0.0F);
		base.addChild(attachment2);
		setRotationAngle(attachment2, 0.0F, 0.0F, 0.2618F);
		attachment2.cubeList.add(new ModelBox(attachment2, 64, 33, -1.5F, -2.0F, -3.5F, 3, 4, 7, 0.0F, false));

		rotated_panel = new ModelRenderer(this);
		rotated_panel.setRotationPoint(13.0F, -3.0F, 0.0F);
		base.addChild(rotated_panel);
		setRotationAngle(rotated_panel, 0.0F, 0.0F, 0.7854F);
		rotated_panel.cubeList.add(new ModelBox(rotated_panel, 0, 12, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
		rotated_panel.cubeList.add(new ModelBox(rotated_panel, 0, 0, 1.0F, -1.5F, -1.5F, 1, 3, 3, 0.0F, false));
		rotated_panel.cubeList.add(new ModelBox(rotated_panel, 60, 14, -3.1213F, -5.8787F, -3.0F, 2, 12, 6, 0.0F, false));
		rotated_panel.cubeList.add(new ModelBox(rotated_panel, 16, 24, -16.0707F, -1.9192F, -2.0F, 13, 4, 4, -0.01F, false));

		ladder_sides = new ModelRenderer(this);
		ladder_sides.setRotationPoint(-5.5F, -11.0F, 0.0F);
		base.addChild(ladder_sides);
		setRotationAngle(ladder_sides, 0.0F, 0.0F, 0.1309F);
		ladder_sides.cubeList.add(new ModelBox(ladder_sides, 0, 53, -1.5F, -10.0F, 3.0F, 3, 21, 1, 0.0F, false));
		ladder_sides.cubeList.add(new ModelBox(ladder_sides, 0, 53, -1.5F, -10.0F, -4.0F, 3, 21, 1, 0.0F, false));

		headrest_sticks = new ModelRenderer(this);
		headrest_sticks.setRotationPoint(0.0F, -34.5F, 4.5F);
		base.addChild(headrest_sticks);
		setRotationAngle(headrest_sticks, -0.3054F, 0.0F, 0.0F);
		headrest_sticks.cubeList.add(new ModelBox(headrest_sticks, 0, 16, -3.0F, -4.5F, -0.5F, 1, 5, 1, 0.0F, false));
		headrest_sticks.cubeList.add(new ModelBox(headrest_sticks, 0, 16, 2.0F, -4.5F, -0.5F, 1, 5, 1, 0.0F, false));

		arm = new ModelRenderer(this);
		arm.setRotationPoint(18.0F, -11.0F, 0.0F);
		arm.cubeList.add(new ModelBox(arm, 16, 32, 1.0F, -3.0F, -8.0F, 2, 6, 11, 0.0F, false));
		arm.cubeList.add(new ModelBox(arm, 64, 76, -15.0F, -3.0F, -21.0F, 5, 6, 2, 0.0F, false));
		arm.cubeList.add(new ModelBox(arm, 52, 55, -21.0F, -3.0F, -24.0F, 6, 10, 5, 0.0F, false));
		arm.cubeList.add(new ModelBox(arm, 0, 12, -19.0F, -1.0F, -19.0F, 2, 2, 2, 0.0F, false));
		arm.cubeList.add(new ModelBox(arm, 0, 6, -19.5F, -1.5F, -17.0F, 3, 3, 1, 0.0F, false));

		attachment3 = new ModelRenderer(this);
		attachment3.setRotationPoint(2.0F, 0.0F, -8.0F);
		arm.addChild(attachment3);
		setRotationAngle(attachment3, 0.0F, 0.2618F, 0.0F);
		attachment3.cubeList.add(new ModelBox(attachment3, 70, 44, -1.5F, -3.5F, -2.0F, 3, 7, 4, 0.0F, false));

		attachment4 = new ModelRenderer(this);
		attachment4.setRotationPoint(-10.0F, 0.0F, -20.0F);
		arm.addChild(attachment4);
		setRotationAngle(attachment4, 0.0F, -0.2618F, 0.0F);
		attachment4.cubeList.add(new ModelBox(attachment4, 5, 72, -2.0F, -3.5F, -1.5F, 4, 7, 3, 0.0F, false));

		rotated_arm_panel = new ModelRenderer(this);
		rotated_arm_panel.setRotationPoint(-5.0F, 0.0F, -13.0F);
		arm.addChild(rotated_arm_panel);
		setRotationAngle(rotated_arm_panel, 0.0F, -0.7854F, 0.0F);
		rotated_arm_panel.cubeList.add(new ModelBox(rotated_arm_panel, 0, 12, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
		rotated_arm_panel.cubeList.add(new ModelBox(rotated_arm_panel, 0, 6, -1.5F, -1.5F, 1.0F, 3, 3, 1, 0.0F, false));
		rotated_arm_panel.cubeList.add(new ModelBox(rotated_arm_panel, 30, 12, -8.1213F, -3.0F, -3.1213F, 16, 6, 2, 0.0F, false));

		gear_rack = new ModelRenderer(this);
		gear_rack.setRotationPoint(0.0F, 0.0F, 0.0F);
		arm.addChild(gear_rack);


		segment1 = new ModelRenderer(this);
		segment1.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment1);
		setRotationAngle(segment1, 1.5708F, 0.0F, 0.0F);
		segment1.cubeList.add(new ModelBox(segment1, 74, 55, -20.0F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment2 = new ModelRenderer(this);
		segment2.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment2);
		setRotationAngle(segment2, 1.3221F, 0.0F, 0.0F);
		segment2.cubeList.add(new ModelBox(segment2, 74, 55, -19.99F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment3 = new ModelRenderer(this);
		segment3.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment3);
		setRotationAngle(segment3, 1.0734F, 0.0F, 0.0F);
		segment3.cubeList.add(new ModelBox(segment3, 74, 55, -20.0F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment4 = new ModelRenderer(this);
		segment4.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment4);
		setRotationAngle(segment4, 0.8247F, 0.0F, 0.0F);
		segment4.cubeList.add(new ModelBox(segment4, 74, 55, -19.99F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment5 = new ModelRenderer(this);
		segment5.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment5);
		setRotationAngle(segment5, 0.576F, 0.0F, 0.0F);
		segment5.cubeList.add(new ModelBox(segment5, 74, 55, -20.0F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment6 = new ModelRenderer(this);
		segment6.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment6);
		setRotationAngle(segment6, 0.3272F, 0.0F, 0.0F);
		segment6.cubeList.add(new ModelBox(segment6, 74, 55, -19.99F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment7 = new ModelRenderer(this);
		segment7.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment7);
		setRotationAngle(segment7, 1.8195F, 0.0F, 0.0F);
		segment7.cubeList.add(new ModelBox(segment7, 74, 55, -19.99F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment8 = new ModelRenderer(this);
		segment8.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment8);
		setRotationAngle(segment8, 2.0682F, 0.0F, 0.0F);
		segment8.cubeList.add(new ModelBox(segment8, 74, 55, -20.0F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment9 = new ModelRenderer(this);
		segment9.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment9);
		setRotationAngle(segment9, 2.3169F, 0.0F, 0.0F);
		segment9.cubeList.add(new ModelBox(segment9, 74, 55, -19.99F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment10 = new ModelRenderer(this);
		segment10.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment10);
		setRotationAngle(segment10, 2.5656F, 0.0F, 0.0F);
		segment10.cubeList.add(new ModelBox(segment10, 74, 55, -20.0F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment11 = new ModelRenderer(this);
		segment11.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment11);
		setRotationAngle(segment11, 2.8143F, 0.0F, 0.0F);
		segment11.cubeList.add(new ModelBox(segment11, 74, 55, -19.99F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment12 = new ModelRenderer(this);
		segment12.setRotationPoint(0.0F, 0.0F, 0.0F);
		gear_rack.addChild(segment12);
		setRotationAngle(segment12, 3.0631F, 0.0F, 0.0F);
		segment12.cubeList.add(new ModelBox(segment12, 74, 55, -20.0F, -3.0F, -24.0F, 4, 6, 3, 0.0F, false));

		segment13 = new ModelRenderer(this);
		segment13.setRotationPoint(0.0F, -4.1F, 0.2F);
		gear_rack.addChild(segment13);
		setRotationAngle(segment13, 3.1416F, 0.0F, 0.0F);
		segment13.cubeList.add(new ModelBox(segment13, 28, 49, -19.99F, -2.992F, -23.9615F, 4, 2, 3, 0.0F, false));
		segment13.cubeList.add(new ModelBox(segment13, 69, 69, -21.0F, -0.992F, -24.9615F, 6, 2, 5, 0.0F, false));
	}

	ItemStack attachment = ItemStack.EMPTY;
	ItemStack gear = new ItemStack(RegistryHandler.GOLD_GEAR);
	ResourceLocation TEXTURE_BASE = new ResourceLocation(MysticalGears.MODID, "textures/model/turret_base_iron.png");
	ResourceLocation TEXTURE_OVERLAY = new ResourceLocation(MysticalGears.MODID, "textures/model/turret_overlay_gold.png");

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {
		TileEntity tileEntity = entityIn.world.getTileEntity(entityIn.getPosition());

		float partialTicks = ageInTicks - entityIn.ticksExisted;
		float gearAngle = 0;
		if (tileEntity != null && tileEntity instanceof TileEntityTurret) {
			gearAngle = (float) (((TileEntityTurret) tileEntity).lastAngle + (((TileEntityTurret) tileEntity).angle - ((TileEntityTurret) tileEntity).lastAngle) * partialTicks);
			attachment = ((TileEntityTurret) tileEntity).attachment;
			gear = ((TileEntityTurret) tileEntity).gears;
			TEXTURE_BASE = ((TileEntityTurret) tileEntity).baseMetalTexture;
			TEXTURE_OVERLAY = ((TileEntityTurret) tileEntity).extraMetalTexture;
		}

		GlStateManager.pushMatrix();
		GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
		MysticalMechanicsAPI.IMPL.renderGear(gear, ItemStack.EMPTY, false, partialTicks, -1.3125, 1, gearAngle);
		GlStateManager.popMatrix();

		gearAngle += headYaw;
		GlStateManager.rotate(180.0F + headYaw, 0.0F, 1.0F, 0.0F);

		GlStateManager.pushMatrix();
		GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);

		GlStateManager.pushMatrix();
		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.13F, 0.03125F);
		MysticalMechanicsAPI.IMPL.renderGear(gear, ItemStack.EMPTY, false, partialTicks, -1.3125, 1, -gearAngle + 22.5F);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 0.1875F, 0.1875F);
		MysticalMechanicsAPI.IMPL.renderGear(gear, ItemStack.EMPTY, false, partialTicks, -1.3125, 1, gearAngle + 45.0F);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, -0.6875F, 0.187F);
		MysticalMechanicsAPI.IMPL.renderGear(gear, ItemStack.EMPTY, false, partialTicks, -1.3125, 1, -gearAngle + 67.5F);
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();

		Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TEXTURE_BASE);
		base.render(scale);
		Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TEXTURE_OVERLAY);
		base.render(scale);

		gearAngle -= headPitch;
		GlStateManager.translate(0.0F, -0.6875F, 0.0F);
		GlStateManager.rotate(headPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.6875F, 0.0F);

		GlStateManager.pushMatrix();
		GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, -0.6875F, 0.15625F);
		MysticalMechanicsAPI.IMPL.renderGear(gear, ItemStack.EMPTY, false, partialTicks, -1.3125, 1, gearAngle + 90.0F);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, -0.6875F, 0.1875F);
		MysticalMechanicsAPI.IMPL.renderGear(gear, ItemStack.EMPTY, false, partialTicks, -1.3125, 1, -gearAngle + 112.5F);
		GlStateManager.popMatrix();

		if (!attachment.equals(ItemStack.EMPTY)) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, -0.6875F, 0.1875F);
			MysticalMechanicsAPI.IMPL.renderGear(attachment, ItemStack.EMPTY, false, partialTicks, -1.71875, 1, -gearAngle);
			GlStateManager.popMatrix();
		}

		Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TEXTURE_BASE);
		arm.render(scale);
		Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(TEXTURE_OVERLAY);
		arm.render(scale);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scaleFactor, Entity entityIn) {
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}