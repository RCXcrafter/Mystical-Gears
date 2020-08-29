package com.rcx.mystgears.compatibility;

import com.google.common.collect.Lists;
import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockGatlingGunEmber;
import com.rcx.mystgears.block.BlockGatlingGunWitchburn;
import com.rcx.mystgears.block.BlockTurret;
import com.rcx.mystgears.block.TileEntityGatlingGunEmber;
import com.rcx.mystgears.block.TileEntityGatlingGunWitchburn;
import com.rcx.mystgears.item.ItemGear;
import com.rcx.mystgears.render.TileEntityRenderGatlingGunBase;
import com.rcx.mystgears.util.EffectWitchburn;
import com.rcx.mystgears.util.IAttachmentBehavior;

import mysticalmechanics.api.IGearData;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.Registry;
import soot.handler.WitchburnHandler;
import teamroots.embers.RegistryManager;
import teamroots.embers.SoundManager;
import teamroots.embers.api.projectile.EffectArea;
import teamroots.embers.api.projectile.EffectDamage;
import teamroots.embers.api.projectile.EffectMulti;
import teamroots.embers.api.projectile.ProjectileFireball;
import teamroots.embers.damage.DamageEmber;
import teamroots.embers.particle.ParticleUtil;

public class EmbersCompat {

	public static ItemBlock emberGatlingGun;
	public static ItemBlock witchburnGatlingGun;

	public static void preInit() {
		if (ConfigHandler.emberGatlingGun) {
			emberGatlingGun = new ItemBlock(new BlockGatlingGunEmber());
			MysticalGears.blocks.add((ItemBlock) emberGatlingGun.setRegistryName(emberGatlingGun.getBlock().getRegistryName()));
			if (ConfigHandler.turret)
				BlockTurret.attachmentBehaviors.put(Ingredient.fromItem(emberGatlingGun), new IAttachmentBehavior() {
					public void tick(World world, Vec3d pos, Vec3d direction, ItemStack gear, IGearData data, double power) {
						if (data == null || !(data instanceof GatlingGunData))
							return;

						GatlingGunData gunData = (GatlingGunData) data;

						if(world.isRemote) {
							Vec3d particlePos = pos.add(direction.scale(0.1875));
							if (((int) power) > 0 && world.rand.nextInt(1 + Math.floorDiv(TileEntityGatlingGunEmber.particlePower, (int) power)) == 0) {
								ParticleUtil.spawnParticleGlow(world, (float) particlePos.x, (float) particlePos.y, (float) particlePos.z, 0, 0, 0, 255, 64, 16, 2.0f, 24);
							}
							return;
						}

						gunData.lastBullet += power;
						gunData.dirty = gunData.dirty || power != 0.0;

						if (gunData.lastBullet < TileEntityGatlingGunEmber.maxPower)
							return;

						gunData.lastBullet -= TileEntityGatlingGunEmber.maxPower;
						gunData.lastBullet = Math.min(gunData.lastBullet, TileEntityGatlingGunEmber.maxPower);

						Vec3d shootPos = pos.add(new Vec3d(world.rand.nextDouble() * 0.4 - 0.2, world.rand.nextDouble() * 0.4 - 0.2, world.rand.nextDouble() * 0.4 - 0.2).crossProduct(direction)).add(direction);

						EffectArea effect = new EffectArea(new EffectDamage(TileEntityGatlingGunEmber.damage, DamageEmber.EMBER_DAMAGE_SOURCE_FACTORY, 0, 0.5), 0.5, false);
						ProjectileFireball fireball = new ProjectileFireball(null, shootPos, direction.scale(0.85), 2.5, 60, effect);
						fireball.shoot(world);

						float volume = (float) (0.5f - Math.min(power, TileEntityGatlingGunEmber.maxPower) * 0.3f / TileEntityGatlingGunEmber.maxPower);
						float pitch = (float) (1.5f + Math.min(power, TileEntityGatlingGunEmber.maxPower) * 0.5f / TileEntityGatlingGunEmber.maxPower);
						world.playSound(null, shootPos.x, shootPos.y, shootPos.z, SoundManager.BLAZING_RAY_FIRE, SoundCategory.BLOCKS, volume, pitch);
					}

					public boolean hasData() {
						return true;
					}

					public IGearData createData() {
						return new GatlingGunData();
					}
				});

			if (ConfigHandler.soot) {
				witchburnGatlingGun = new ItemBlock(new BlockGatlingGunWitchburn());
				MysticalGears.blocks.add((ItemBlock) witchburnGatlingGun.setRegistryName(witchburnGatlingGun.getBlock().getRegistryName()));
				if (ConfigHandler.turret)
					BlockTurret.attachmentBehaviors.put(Ingredient.fromItem(witchburnGatlingGun), new IAttachmentBehavior() {
						public void tick(World world, Vec3d pos, Vec3d direction, ItemStack gear, IGearData data, double power) {
							if (data == null || !(data instanceof GatlingGunData))
								return;

							GatlingGunData gunData = (GatlingGunData) data;

							if(world.isRemote) {
								Vec3d particlePos = pos.add(direction.scale(0.1875));
								if (((int) power) > 0 && world.rand.nextInt(1 + Math.floorDiv(TileEntityGatlingGunEmber.particlePower, (int) power)) == 0) {
									ParticleUtil.spawnParticleGlow(world, (float) particlePos.x, (float) particlePos.y, (float) particlePos.z, 0, 0, 0, 64, 255, 16, 2.0f, 24);
								}
								return;
							}

							gunData.lastBullet += power;
							gunData.dirty = gunData.dirty || power != 0.0;

							if (gunData.lastBullet < TileEntityGatlingGunEmber.maxPower)
								return;

							gunData.lastBullet -= TileEntityGatlingGunEmber.maxPower;
							gunData.lastBullet = Math.min(gunData.lastBullet, TileEntityGatlingGunEmber.maxPower);

							Vec3d shootPos = pos.add(new Vec3d(world.rand.nextDouble() * 0.4 - 0.2, world.rand.nextDouble() * 0.4 - 0.2, world.rand.nextDouble() * 0.4 - 0.2).crossProduct(direction)).add(direction);

							EffectArea effect = new EffectArea(new EffectMulti(Lists.newArrayList(new EffectDamage(TileEntityGatlingGunEmber.damage, DamageEmber.EMBER_DAMAGE_SOURCE_FACTORY, 0, 0.5), new EffectWitchburn(500))), 0.5, false);
							ProjectileFireball fireball = new ProjectileFireball(null, shootPos, direction.scale(0.85), 2.5, 60, effect);
							fireball.setColor(WitchburnHandler.COLOR);
							fireball.shoot(world);

							float volume = (float) (0.5f - Math.min(power, TileEntityGatlingGunEmber.maxPower) * 0.3f / TileEntityGatlingGunEmber.maxPower);
							float pitch = (float) (1.5f + Math.min(power, TileEntityGatlingGunEmber.maxPower) * 0.5f / TileEntityGatlingGunEmber.maxPower);
							world.playSound(null, shootPos.x, shootPos.y, shootPos.z, SoundManager.BLAZING_RAY_FIRE, SoundCategory.BLOCKS, volume, pitch);
						}

						public boolean hasData() {
							return true;
						}

						public IGearData createData() {
							return new GatlingGunData();
						}
					});
			}
		}
	}

	public static void init() {
		if (ConfigHandler.emberGatlingGun) {
			GameRegistry.registerTileEntity(TileEntityGatlingGunEmber.class, new ResourceLocation(MysticalGears.MODID, "ember_gatling_gun"));

			GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_ember_gatling_gun"), ItemGear.group, new ItemStack(emberGatlingGun), new Object[]{"IDD", "C I", "IDD", 'I', "plateIron", 'D', "plateDawnstone", 'C', new ItemStack(RegistryManager.shard_ember)});

			if (ConfigHandler.soot) {
				GameRegistry.registerTileEntity(TileEntityGatlingGunWitchburn.class, new ResourceLocation(MysticalGears.MODID, "witchburn_gatling_gun"));

				GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_witchburn_gatling_gun"), ItemGear.group, new ItemStack(witchburnGatlingGun), new Object[]{"LNN", "B L", "LNN", 'L', "plateLead", 'N', "plateNickel", 'B', new ItemStack(Registry.WITCH_FIRE)});
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		if (ConfigHandler.emberGatlingGun) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGatlingGunEmber.class, new TileEntityRenderGatlingGunBase());
			if (ConfigHandler.soot) {
				ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGatlingGunWitchburn.class, new TileEntityRenderGatlingGunBase());
			}
		}
	}

	public static class GatlingGunData implements IGearData {

		public double lastBullet = 0;
		public boolean dirty = false;

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			lastBullet = tag.getDouble("lastBullet");
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			tag.setDouble("lastBullet", lastBullet);
			return tag;
		}

		@Override
		public boolean isDirty() {
			return dirty;
		}
	}
}
