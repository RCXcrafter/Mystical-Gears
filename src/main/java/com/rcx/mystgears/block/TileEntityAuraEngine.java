package com.rcx.mystgears.block;

import javax.annotation.Nullable;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
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

public class TileEntityAuraEngine extends TileEntity implements ITickable {

	public DefaultMechCapability mechCapability = new DefaultMechCapability(){
		@Override
		public void setPower(double value, EnumFacing from) {
			if(from == null)
				super.setPower(value, from);
		}

		@Override
		public double getPower(EnumFacing from) {
			if (from == null || world.getBlockState(getPos()).getValue(BlockAuraEngine.FACING).equals(from))
				return super.getPower(from);
			return 0;
		}

		@Override
		public void onPowerChange(){
			TileEntityAuraEngine source = TileEntityAuraEngine.this;
			source.updateNeighbors();
			source.markDirty();
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return world.getBlockState(getPos()).getValue(BlockAuraEngine.FACING).equals(from);
		}

		@Override
		public boolean isInput(EnumFacing from) {
			return false;
		}
	};

	public static int auraConsumption = 30;
	public static double outputPower = 30;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
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
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(BlockAuraEngine.FACING).equals(facing)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(BlockAuraEngine.FACING).equals(facing)) {
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
		if (!(world.getBlockState(getPos()).getBlock() instanceof BlockAuraEngine))
			return;
		EnumFacing f = world.getBlockState(getPos()).getValue(BlockAuraEngine.FACING);
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
		int redstoneSignal = 0;
		for(EnumFacing dir : EnumFacing.VALUES) {
			int redstoneSide = getWorld().getRedstonePower(getPos().offset(dir), dir);
			redstoneSignal = Math.max(redstoneSignal, redstoneSide);
		}

		double wantedPower = outputPower;

		if (redstoneSignal != 0) {
			wantedPower = 0;
		} else {
			BlockPos spot = IAuraChunk.getHighestSpot(this.world, this.pos, 20, this.pos);
			IAuraChunk chunk = IAuraChunk.getAuraChunk(this.world, spot);
			chunk.drainAura(spot, auraConsumption);
		}

		if (mechCapability.getPower(null) != wantedPower){
			mechCapability.setPower(wantedPower, null);
			//markDirty();
			//updateNeighbors();
		}
	}
}
