package com.rcx.mystgears.block;

import javax.annotation.Nullable;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import thaumcraft.common.world.aura.AuraHandler;

public class TileEntityVisMotor extends TileEntity implements ITickable {

	public DefaultMechCapability mechCapability = new DefaultMechCapability(){
		@Override
		public void setPower(double value, EnumFacing from) {
			if(from == null)
				super.setPower(value, from);
		}

		@Override
		public double getPower(EnumFacing from) {
			if (from == null || world.getBlockState(getPos()).getValue(BlockVisMotor.FACING).equals(from))
				return super.getPower(from);
			return 0;
		}

		@Override
		public void onPowerChange(){
			TileEntityVisMotor source = TileEntityVisMotor.this;
			source.updateNeighbors();
			source.markDirty();
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return world.getBlockState(getPos()).getValue(BlockVisMotor.FACING).equals(from);
		}

		@Override
		public boolean isInput(EnumFacing from) {
			return false;
		}
	};

	public int timer = 0;
	public static int maxTime = 1000;
	public static double outputPower = 30;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("mech_power", mechCapability.power);
		tag.setInteger("timer", timer);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("mech_power"))
			mechCapability.power = tag.getDouble("mech_power");

		if (tag.hasKey("timer"))
			timer = tag.getInteger("timer");
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(BlockVisMotor.FACING).equals(facing)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(BlockVisMotor.FACING).equals(facing)) {
			T result = (T) this.mechCapability;
			return result;
		}
		return super.getCapability(capability, facing);
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		mechCapability.setPower(0f, null);
		updateNeighbors();
	}

	public void updateNeighbors() {
		EnumFacing f = world.getBlockState(getPos()).getValue(BlockVisMotor.FACING);
		TileEntity t = world.getTileEntity(getPos().offset(f));
		if (t != null){
			if (t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite())) {
				t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).setPower(mechCapability.getPower(f),f.getOpposite());
				t.markDirty();
			}
		}
	}

	@Override
	public void update() {
		double wantedPower = outputPower;
		if (timer == 0) {
			float vis = AuraHandler.drainVis(getWorld(), getPos(), 1.0F, false);
			timer = (int) (vis * maxTime);
			markDirty();
			if (timer == 0)
				wantedPower = 0;
		} else
			timer--;

		if (mechCapability.getPower(null) != wantedPower){
			mechCapability.setPower(wantedPower, null);
			markDirty();
			updateNeighbors();
		}
	}
}
