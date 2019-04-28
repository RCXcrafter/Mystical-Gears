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

public class TileEntityWindupBox extends TileEntity implements ITickable {

	public DefaultMechCapability mechCapability = new DefaultMechCapability() {

		public double outputPower = 0;

		@Override
		public void setPower(double value, EnumFacing from) {
			if (world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).getOpposite().equals(from)) {
				super.setPower(value, from);
				if (value != getPower(null)) {
					onPowerChange();
				}
			} else if (world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).equals(from)) {
				if (value != outputPower) {
					outputPower = value;
					onPowerChange();
				}
			}
		}

		@Override
		public double getPower(EnumFacing from) {
			if (from == null || world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).getOpposite().equals(from))
				return super.getPower(from);
			if (world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).equals(from) && active)
				return outputPower;
			return 0;
		}

		@Override
		public void onPowerChange() {
			TileEntityWindupBox source = TileEntityWindupBox.this;
			source.updateNeighbors();
			source.markDirty();
		}

		@Override
		public boolean isInput(EnumFacing from) {
			return world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).getOpposite().equals(from);
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).equals(from);
		}
	};

	public double currentPower = 0;
	public double storedPower = 0;
	public double maxPower = 10000;
	public double output = 50;
	public boolean active = false;

	@Override
	public void onLoad() {
		super.onLoad();
		updateNeighbors();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("power", storedPower);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("power")){
			storedPower = tag.getInteger("power");
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
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).getOpposite().equals(facing)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).getOpposite().equals(facing)) {
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
		if (!(world.getBlockState(getPos()).getBlock() instanceof BlockWindupBox))
			return;
		EnumFacing f = world.getBlockState(getPos()).getValue(BlockWindupBox.FACING).getOpposite();
		TileEntity t = world.getTileEntity(getPos().offset(f));
		if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()))
			mechCapability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).getPower(f.getOpposite()), f);
		else
			mechCapability.setPower(0, f);
		

		TileEntity t2 = world.getTileEntity(getPos().offset(f.getOpposite()));
		if (t2 != null){
			if (t2.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f)) {
				t2.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f).setPower(mechCapability.getPower(f.getOpposite()),f);
				t2.markDirty();
			}
		}

		markDirty();
	}

	@Override
	public void update() {
		if (mechCapability.getPower(null) != currentPower) {
			currentPower = mechCapability.getPower(null);
			markDirty();
		}

		int redstoneSignal = 0;
		for(EnumFacing dir : EnumFacing.VALUES) {
			int redstoneSide = getWorld().getRedstonePower(getPos().offset(dir), dir);
			redstoneSignal = Math.max(redstoneSignal, redstoneSide);
		}

		if (active != (redstoneSignal != 0 && storedPower >= output)) {
			active = redstoneSignal != 0 && storedPower >= output;

			if (active && mechCapability.getPower(world.getBlockState(getPos()).getValue(BlockWindupBox.FACING)) != output) {
				mechCapability.setPower(output, world.getBlockState(getPos()).getValue(BlockWindupBox.FACING));
			} else if (!active && mechCapability.getPower(world.getBlockState(getPos()).getValue(BlockWindupBox.FACING)) != 0) {
				mechCapability.setPower(0, world.getBlockState(getPos()).getValue(BlockWindupBox.FACING));
			}
		}

		if (active) {
			storedPower -= output;
		}

		storedPower = Math.min(maxPower, storedPower + currentPower);
	}
}
