package com.rcx.mystgears.entity;

import com.rcx.mystgears.block.TileEntityTurret;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityTurret extends Entity {

	public EntityTurret(World world) {
		super(world);
		this.setSize(1.0F, 1.0F);
		this.noClip = true;
		this.isImmuneToFire = true;
	}

	public EntityTurret(World world, BlockPos pos) {
		this(world);
		this.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
	}

	@Override
	public double getMountedYOffset() {
		return 1.5;
	}

	@Override
	protected boolean canBeRidden(Entity entity) {
		return true;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public boolean isInvisible() {
		return false;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return 2.1875F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().expand(2, 4, 2);
	}

	@Override
	public void onUpdate() {
		if (this.world.getTileEntity(this.getPosition()) == null ||
				!(this.world.getTileEntity(this.getPosition()) instanceof TileEntityTurret) ||
				!((TileEntityTurret) this.world.getTileEntity(this.getPosition())).getEntity().equals(this))
			this.setDead();
		super.onUpdate();
		if (this.isBeingRidden()) {
			this.rotationPitch = Math.min(this.getControllingPassenger().rotationPitch, 45.0F);
			this.rotationYaw = this.getControllingPassenger().rotationYaw;
		}
	}

	@Override
	public Entity getControllingPassenger() {
		if (this.isBeingRidden())
			return this.getPassengers().get(0);
		return null;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);
		if (this.isPassenger(passenger)) {
			this.applyYawToEntity(passenger);
		}
	}

	@Override
	public void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		this.world.updateComparatorOutputLevel(this.getPosition(), this.world.getBlockState(this.getPosition()).getBlock());
	}

	protected void applyYawToEntity(Entity entityToUpdate) {
		entityToUpdate.setRenderYawOffset(this.rotationYaw);
		float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
		float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
		entityToUpdate.prevRotationYaw += f1 - f;
		entityToUpdate.rotationYaw += f1 - f;
		entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void applyOrientationToEntity(Entity entityToUpdate) {
		this.applyYawToEntity(entityToUpdate);
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {}
}
