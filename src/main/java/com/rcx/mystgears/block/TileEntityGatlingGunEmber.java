package com.rcx.mystgears.block;

import com.rcx.mystgears.compatibility.EmbersCompat;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import teamroots.embers.SoundManager;
import teamroots.embers.api.projectile.EffectArea;
import teamroots.embers.api.projectile.EffectDamage;
import teamroots.embers.api.projectile.ProjectileFireball;
import teamroots.embers.damage.DamageEmber;
import teamroots.embers.particle.ParticleUtil;

public class TileEntityGatlingGunEmber extends TileEntityGatlingGunBase {

	public static double maxPower = 500;
	public static int particlePower = (int) (maxPower / 1.6);
	public static float damage = 2.0f;
	public static ItemStack gunStack = new ItemStack(EmbersCompat.emberGatlingGun);

	public double lastBullet = 0;

	@Override
	public void fireUpdate() {
		double posX = getPos().getX();
		double posY = getPos().getY();
		double posZ = getPos().getZ();

		if(world.isRemote) {
			lastAngle = angle;
			angle += currentPower;
			if (((int) currentPower) > 0 && random.nextInt(1 + Math.floorDiv(particlePower, (int) currentPower)) == 0) {
				ParticleUtil.spawnParticleGlow(getWorld(), ((float) posX) + facing.getFrontOffsetX() * -0.3f + 0.5f, ((float) posY) + facing.getFrontOffsetY() * -0.3f + 0.5f, ((float) posZ) + facing.getFrontOffsetZ() * -0.3f + 0.5f, 0, 0, 0, 255, 64, 16, 2.0f, 24);
			}
			return;
		}

		lastBullet += currentPower;

		if (lastBullet < maxPower)
			return;

		lastBullet -= maxPower;

		lastBullet = Math.min(lastBullet, maxPower);

		double varX = random.nextDouble() * 0.4 - 0.2;
		double varY = random.nextDouble() * 0.4 - 0.2;

		switch (facing) {
		case DOWN:
		case UP:
			posX += varX;
			posZ += varY;
			break;
		case NORTH:
		case SOUTH:
			posX += varX;
			posY += varY;
			break;
		case WEST:
		case EAST:
			posZ += varX;
			posY += varY;
			break;
		default:
			break;
		}
		Vec3d shootPos = new Vec3d(posX + facing.getFrontOffsetX() * 0.55 + 0.5, posY + facing.getFrontOffsetY() * 0.55 + 0.5, posZ + facing.getFrontOffsetZ() * 0.55 + 0.5);

		EffectArea effect = new EffectArea(new EffectDamage(damage, DamageEmber.EMBER_DAMAGE_SOURCE_FACTORY, 0, 0.5), 0.5, false);
		ProjectileFireball fireball = new ProjectileFireball(null, shootPos, new Vec3d(facing.getFrontOffsetX() * 0.85, facing.getFrontOffsetY() * 0.85, facing.getFrontOffsetZ() * 0.85), 2.5, 20, effect);
		/*EmberProjectileEvent event = new EmberProjectileEvent(null, gunStack, 10000, fireball);
		MinecraftForge.EVENT_BUS.post(event);
		if (!event.isCanceled()) {
			for (IProjectilePreset projectile : event.getProjectiles()) {
				projectile.shoot(getWorld());
			}
		}*/
		fireball.shoot(getWorld());

		float volume = currentPower < maxPower/2.5 ? 0.5f : currentPower < maxPower/1.5 ? 0.4f : 0.3f;
		world.playSound(null, posX, posY, posZ, SoundManager.BLAZING_RAY_FIRE, SoundCategory.BLOCKS, volume, 1.5f);
	}
}
