package com.rcx.mystgears.compatibility;

import java.util.Random;

import javax.annotation.Nullable;

import com.rcx.mystgears.ConfigHandler;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import rustichromia.Registry;
import rustichromia.Rustichromia;
import rustichromia.item.ItemDisk;
import teamroots.embers.particle.ParticleUtil;

public class RustichromiaCompat {

	public static float stoneDiskLimitMultiplier = 10.0f;

	public static void init() {
		if (stoneDiskLimitMultiplier < 0.0f)
			return;

		MysticalMechanicsAPI.IMPL.unregisterGear(new ResourceLocation(Rustichromia.MODID, "disk_stone"));
		MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(Rustichromia.MODID, "disk_stone"), Ingredient.fromItem(Registry.DISK_STONE), new IGearBehavior() {
			Random random = new Random();

			@Override
			public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
				ItemDisk item = (ItemDisk) gear.getItem();
				double threshold = item.getAmount(gear);
				if(power < threshold)
					return 0;
				else
					if (power > threshold * stoneDiskLimitMultiplier)
						return threshold * stoneDiskLimitMultiplier + Math.log10(power - threshold * stoneDiskLimitMultiplier + Math.log10(Math.exp(1))) - Math.log10(Math.log10(Math.exp(1)));
					else    
						return power;
			}

			@Override
			public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
				if (ConfigHandler.smoke && facing != null && tile.getWorld().isRemote && tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing)) {
					IMechCapability capability = tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing);
					double power = capability.getPower(facing);
					ItemDisk item = (ItemDisk) gear.getItem();
					double threshold = item.getAmount(gear);
					Boolean doIt = random.nextInt(5) == 0;
					if(doIt && power > threshold * stoneDiskLimitMultiplier) {
						float xOff = 0.1f + random.nextFloat() * 0.8f;
						float yOff = 0.1f + random.nextFloat() * 0.8f;
						float zOff = 0.1f + random.nextFloat() * 0.8f;
						switch (facing.getAxis()) {
						case X:
							xOff = 0.5f + facing.getFrontOffsetX() / 2.0f; break;
						case Y:
							yOff = 0.5f + facing.getFrontOffsetY() / 2.0f; break;
						case Z:
							zOff = 0.5f + facing.getFrontOffsetZ() / 2.0f; break;
						}
						if (ConfigHandler.embers)
							ParticleUtil.spawnParticleSmoke(tile.getWorld(), tile.getPos().getX() + xOff, tile.getPos().getY() + yOff, tile.getPos().getZ() + zOff, 0, 0, 0, 128, 128, 128, 0.4f, random.nextFloat() * 4.0f + 4.0f, 40);
						else
							tile.getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, tile.getPos().getX() + xOff, tile.getPos().getY() + yOff, tile.getPos().getZ() + zOff, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		});
	}
}
