package com.rcx.mystgears.item;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.util.TransformUtils;
import morph.avaritia.api.IHaloRenderItem;
import morph.avaritia.client.render.item.HaloRenderItem;
import morph.avaritia.entity.EntityImmortalItem;
import morph.avaritia.init.AvaritiaTextures;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGearAvaritia extends ItemGear implements IHaloRenderItem {

	public int haloColour;
	public int haloSize;
	public boolean haloTexture;
	public boolean halo;
	public boolean pulse;

	public ItemGearAvaritia(String name, int haloColour, int haloSize, boolean haloTexture, boolean halo, boolean pulse) {
		super(name);
		this.haloColour = haloColour;
		this.haloSize = haloSize;
		this.haloTexture = haloTexture;
		this.halo = halo;
		this.pulse = pulse;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel() {
		super.registerModel();
		final ModelResourceLocation location = new ModelResourceLocation(this.getRegistryName(), "inventory");
		IBakedModel wrapped = new HaloRenderItem(TransformUtils.DEFAULT_ITEM, modelRegistry -> modelRegistry.getObject(location));
		ModelRegistryHelper.register(location, wrapped);
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityImmortalItem(world, location, itemstack);
	}

	@Override
	public int getHaloColour(ItemStack item) {
		return haloColour;
	}

	@Override
	public int getHaloSize(ItemStack item) {
		return haloSize;
	}

	@Override
	public TextureAtlasSprite getHaloTexture(ItemStack item) {
		return haloTexture ? AvaritiaTextures.HALO : AvaritiaTextures.HALO_NOISE;
	}

	@Override
	public boolean shouldDrawHalo(ItemStack item) {
		return halo;
	}

	@Override
	public boolean shouldDrawPulse(ItemStack item) {
		return pulse;
	}
}
