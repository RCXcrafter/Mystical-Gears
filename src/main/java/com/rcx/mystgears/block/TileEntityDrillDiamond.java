package com.rcx.mystgears.block;

public class TileEntityDrillDiamond extends TileEntityDrill {
	
	public static int diamondDrillLevel = 3;
	public static float diamondDrillSpeed = 0.003f;
	
	@Override
	public void update() {
		if (facing == null) {
			try {
				facing = world.getBlockState(getPos()).getValue(BlockGatlingGunBase.FACING);
			} catch (IllegalArgumentException e) {
				return;
			}
		}

		if (capability.getPower(null) != currentPower) {
			currentPower = capability.getPower(null);
		}

		if(world.isRemote) {
			lastAngle = angle;
			angle += currentPower;
			return;
		}

		if (!(hardness > 0.0f) || currentPower == 0)
			return;

		if (harvestLevel <= diamondDrillLevel)
			progress += diamondDrillSpeed * currentPower;
		else
			progress += diamondDrillSpeed * currentPower / 4.0f;

		if (hardness < progress) {
			world.destroyBlock(breakingPos, harvestLevel <= diamondDrillLevel);
			world.sendBlockBreakProgress(999, breakingPos, -1);
			progress = 0.0f;
		} else {
			int breakAnimation = Math.min((int) (10.0f * progress / hardness), 10);
			if (breakAnimation != previousBreakAnimation)
				world.sendBlockBreakProgress(999, breakingPos, breakAnimation);
		}
	}
}
