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
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityRedstoneDynamo extends TileEntity implements ITickable {

	public DefaultMechCapability mechCapability = new DefaultMechCapability() {
		@Override
		public void setPower(double value, EnumFacing from) {
			if (!world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING).getOpposite().equals(from))
				return;
			super.setPower(value, from);
			if (value != getPower(null)) {
				onPowerChange();
			}
		}

		@Override
		public double getPower(EnumFacing from) {
			if (from == null || world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING).getOpposite().equals(from))
				return super.getPower(from);
			return 0;
		}

		@Override
		public void onPowerChange() {
			TileEntityRedstoneDynamo source = TileEntityRedstoneDynamo.this;
			source.updateNeighbors();
			source.markDirty();
		}

		@Override
		public boolean isInput(EnumFacing from) {
			return world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING).getOpposite().equals(from);
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return false;
		}
	};
	public IEnergyStorage energyHandler = new IEnergyStorage() {
		@Override
		public int getEnergyStored() {
			return fe;
		}

		@Override
		public int getMaxEnergyStored() {
			return MAX_FE;
		}

		@Override public boolean canExtract() {
			return true;
		}

		@Override public int extractEnergy(int maxExtract, boolean simulate) {
			int extracted = Math.min(fe, maxExtract);
			if (!simulate)
				fe -= extracted;
			return extracted;
		}

		@Override public int receiveEnergy(int maxReceive, boolean simulate) {
			return 0;
		}

		@Override public boolean canReceive() {
			return false;
		}
	};

	public int fe = 0;
	public double currentPower = 0;
	public static double FE_CONVERSION_RATE = 0.35;
	public static int MAX_FE = 20000;

	@Override
	public void onLoad() {
		super.onLoad();
		updateNeighbors();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("fe", fe);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("fe")){
			fe = tag.getInteger("fe");
		}
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
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (facing == null || world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING).getOpposite().equals(facing)) || capability == CapabilityEnergy.ENERGY && (facing == null || world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING).equals(facing))) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY && (facing == null || world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING).equals(facing))) {
			T result = (T) this.energyHandler;
			return result;
		}
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (facing == null || world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING).getOpposite().equals(facing))) {
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
		EnumFacing f = world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING).getOpposite();
		TileEntity t = world.getTileEntity(getPos().offset(f));
		if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()))
			mechCapability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).getPower(f.getOpposite()), f);
		else
			mechCapability.setPower(0, f);

		markDirty();
	}

	@Override
	public void update() {
		if (mechCapability.getPower(null) != currentPower) {
			currentPower = mechCapability.getPower(null);
			markDirty();
		}

		if(!world.isRemote) {
			fe = (int) Math.min(fe + currentPower * FE_CONVERSION_RATE, MAX_FE);

			int output = Math.min(fe, MAX_FE);
			//fe -= output;
			//fe += transmitEnergy(output);

			EnumFacing facing = world.getBlockState(getPos()).getValue(BlockRedstoneDynamo.FACING);
			BlockPos neighbor = getPos().offset(facing);
			if(!world.isBlockLoaded(neighbor))
				return;

			TileEntity te = world.getTileEntity(neighbor);
			if(te == null)
				return;

			IEnergyStorage storage = null;

			if(te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
				storage = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
			} else if(te.hasCapability(CapabilityEnergy.ENERGY, null)) {
				storage = te.getCapability(CapabilityEnergy.ENERGY, null);
			}

			if(storage != null) {
				output = storage.receiveEnergy(output, false);
			}

			fe -= output;
		}
	}
}
