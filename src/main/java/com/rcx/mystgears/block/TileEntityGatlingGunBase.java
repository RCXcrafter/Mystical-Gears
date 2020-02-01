package com.rcx.mystgears.block;

import java.util.Random;

import javax.annotation.Nullable;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

public abstract class TileEntityGatlingGunBase extends TileEntity implements ITickable {

	public DefaultMechCapability capability = new DefaultMechCapability() {
		@Override
		public void setPower(double value, EnumFacing from) {
			try {
				if (world.getBlockState(getPos()).getValue(BlockGatlingGunBase.FACING).getOpposite().equals(from))
					super.setPower(value, from);
			} catch (IllegalArgumentException e) {}
		}

		@Override
		public double getPower(EnumFacing from) {
			try {
				if (from == null || world.getBlockState(getPos()).getValue(BlockGatlingGunBase.FACING).getOpposite().equals(from))
					return super.getPower(from);
			} catch (IllegalArgumentException e) {}
			return 0;
		}

		@Override
		public void onPowerChange() {
			TileEntityGatlingGunBase.this.markDirty();
		}

		@Override
		public boolean isInput(EnumFacing from) {
			try {
				return world.getBlockState(getPos()).getValue(BlockGatlingGunBase.FACING).getOpposite().equals(from);
			} catch (IllegalArgumentException e) {
				return false;
			}
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return false;
		}
	};

	Random random = new Random();
	EnumFacing facing = null;
	public double currentPower = 0;
	public double angle, lastAngle;

	@Override
	public void onLoad() {
		super.onLoad();
		try {
			facing = world.getBlockState(getPos()).getValue(BlockGatlingGunBase.FACING);
		} catch (IllegalArgumentException e) {
			return;
		}
		updateNeighbors();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		capability.writeToNBT(tag);      	
		return tag;
	}  

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		capability.readFromNBT(tag);
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
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY) {
			T result = (T) this.capability;
			return result;
		}
		return super.getCapability(capability, facing);
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		capability.setPower(0f, null);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if(world instanceof WorldServer) {
			SPacketUpdateTileEntity packet = this.getUpdatePacket();
			if (packet != null) {
				PlayerChunkMap chunkMap = ((WorldServer) world).getPlayerChunkMap();
				int i = this.getPos().getX() >> 4;
				int j = this.getPos().getZ() >> 4;
				PlayerChunkMapEntry entry = chunkMap.getEntry(i, j);
				if(entry != null) {
					entry.sendPacket(packet);
				}
			}
		}
	}

	public void updateNeighbors() {
		EnumFacing f = world.getBlockState(getPos()).getValue(BlockGatlingGunBase.FACING).getOpposite();
		TileEntity t = world.getTileEntity(getPos().offset(f));
		if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()))
			capability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).getPower(f.getOpposite()), f);
		else
			capability.setPower(0, f);
	}

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

		fireUpdate();
	}
	
	public abstract void fireUpdate();
}
