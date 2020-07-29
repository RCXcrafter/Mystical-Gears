package com.rcx.mystgears.util;

import mysticalmechanics.api.IGearData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IAttachmentBehavior {

	default void tick(World world, Vec3d pos, Vec3d direction, ItemStack gear, IGearData data, double power) {

	}

	default boolean hasData() {
		return false;
	}

	default IGearData createData() {
		return null;
	}
}
