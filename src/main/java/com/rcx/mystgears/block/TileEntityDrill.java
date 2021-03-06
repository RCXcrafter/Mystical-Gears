package com.rcx.mystgears.block;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityDrill extends TileEntity implements ITickable {

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
			TileEntityDrill.this.markDirty();
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

	public static int drillLevel = 2;
	public static float drillSpeed = 0.002f;

	Random random = new Random();
	EnumFacing facing = null;
	public double currentPower = 0;
	public double angle, lastAngle;
	public IBlockState block = Blocks.AIR.getDefaultState();
	public BlockPos breakingPos;
	public float hardness = -1.0f;
	public int harvestLevel = 0;
	public float progress = 0.0f;
	public int previousBreakAnimation = 0;
	FakePlayer fakePlayer;

	@Override
	public void onLoad() {
		super.onLoad();
		try {
			facing = world.getBlockState(getPos()).getValue(BlockGatlingGunBase.FACING);
		} catch (IllegalArgumentException e) {
			return;
		}
		updateNeighbors();
		BlockPos fromPos = pos.offset(world.getBlockState(pos).getValue(BlockDrill.FACING));
		IBlockState state = world.getBlockState(fromPos);
		setBlock(state, fromPos, state.getBlockHardness(world, fromPos));
		createFakePlayer();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos().add(-1, -1, -1), getPos().add(2, 2, 2));
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
		world.sendBlockBreakProgress(fakePlayer.getEntityId(), breakingPos, -1);
		fakePlayer.setDead();
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

	void createFakePlayer() {
		if(!world.isRemote) {
			fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "mystgears_drill"));
			fakePlayer.connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer) {
				@Override
				public void sendPacket(Packet packetIn) {}
			};
		}
	}

	public void setBlock(IBlockState blockIn, BlockPos pos, float hardnessIn) {
		block = blockIn;
		breakingPos = pos;
		hardness = hardnessIn;
		progress = 0.0f;
		harvestLevel = blockIn.getBlock().getHarvestLevel(blockIn);
		world.sendBlockBreakProgress(999, breakingPos, -1);
		previousBreakAnimation = 0;
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

		if (world.isRemote) {
			lastAngle = angle;
			angle += currentPower;
			return;
		}

		if (hardness < 0.0f || currentPower == 0)
			return;

		if (harvestLevel <= drillLevel)
			progress += drillSpeed * currentPower;
		else
			progress += drillSpeed * currentPower / 4.0f;

		if (hardness < progress) {
			block.getBlock().onBlockHarvested(world, breakingPos, block, fakePlayer);
			world.destroyBlock(breakingPos, harvestLevel <= drillLevel);
			block.getBlock().breakBlock(world, breakingPos, block);
			world.sendBlockBreakProgress(fakePlayer.getEntityId(), breakingPos, -1);
			progress = 0.0f;
		} else {
			int breakAnimation = Math.min((int) (10.0f * progress / hardness), 10);
			if (breakAnimation != previousBreakAnimation)
				world.sendBlockBreakProgress(fakePlayer.getEntityId(), breakingPos, breakAnimation);
		}
	}
}
