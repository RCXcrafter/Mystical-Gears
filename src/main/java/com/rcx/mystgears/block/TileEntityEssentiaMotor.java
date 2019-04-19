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
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileEntityEssentiaMotor extends TileEntity implements ITickable, IEssentiaTransport {

	public DefaultMechCapability mechCapability = new DefaultMechCapability(){
		@Override
		public void setPower(double value, EnumFacing from) {
			if(from == null)
				super.setPower(value, from);
		}

		@Override
		public double getPower(EnumFacing from) {
			if (from == null || world.getBlockState(getPos()).getValue(BlockEssentiaMotor.FACING).equals(from))
				return super.getPower(from);
			return 0;
		}

		@Override
		public void onPowerChange(){
			TileEntityEssentiaMotor source = TileEntityEssentiaMotor.this;
			source.updateNeighbors();
			source.markDirty();
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return world.getBlockState(getPos()).getValue(BlockEssentiaMotor.FACING).equals(from);
		}

		@Override
		public boolean isInput(EnumFacing from) {
			return false;
		}
	};

	public double essentia = -1;
	public static double maxEssentia = 50;
	public static double outputPower = 30;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("essentia", essentia);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("timer"))
			essentia = tag.getDouble("essentia");
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
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(BlockEssentiaMotor.FACING).equals(facing)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(BlockEssentiaMotor.FACING).equals(facing)) {
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
		EnumFacing f = world.getBlockState(getPos()).getValue(BlockEssentiaMotor.FACING);
		TileEntity t = world.getTileEntity(getPos().offset(f));
		if (t != null){
			if (t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite())) {
				t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).setPower(mechCapability.getPower(f),f.getOpposite());
				t.markDirty();
			}
		}
	}

	private void fill() {
		if (!(world.getBlockState(getPos()).getBlock() instanceof BlockEssentiaMotor))
			return;

		EnumFacing face = world.getBlockState(getPos()).getValue(BlockEssentiaMotor.FACING).getOpposite();
		TileEntity te = ThaumcraftApiHelper.getConnectableTile(world, pos, face);
		if (te == null)
			return;

		IEssentiaTransport ic = (IEssentiaTransport) te;
		Aspect ta = null;
		if (!ic.canOutputTo(face.getOpposite()))
			return;

		if (ic.getEssentiaAmount(face.getOpposite()) > 0
				&& ic.getSuctionAmount(face.getOpposite()) < this.getSuctionAmount(face)
				&& this.getSuctionAmount(face) >= ic.getMinimumSuction()) {
			ta = ic.getEssentiaType(face.getOpposite());
		}

		if (ta == null)
			return;

		essentia += ic.takeEssentia(ta, 1, face.getOpposite());
		markDirty();
	}

	@Override
	public void update() {
		if (!world.isRemote && Math.ceil(essentia) < maxEssentia)
			fill();

		int redstoneSignal = 0;
		for(EnumFacing dir : EnumFacing.VALUES) {
			int redstoneSide = getWorld().getRedstonePower(getPos().offset(dir), dir);
			redstoneSignal = Math.max(redstoneSignal, redstoneSide);
		}

		double wantedPower = outputPower;
		if (redstoneSignal != 0 || essentia < 1)
			wantedPower = 0;
		else
			essentia -= 0.01;

		if (mechCapability.getPower(null) != wantedPower){
			mechCapability.setPower(wantedPower, null);
			markDirty();
			updateNeighbors();
		}
	}

	@Override
	public boolean isConnectable(EnumFacing facing) {
		if (!(world.getBlockState(getPos()).getBlock() instanceof BlockEssentiaMotor))
			return false;

		return world.getBlockState(getPos()).getValue(BlockEssentiaMotor.FACING).getOpposite().equals(facing);
	}

	@Override
	public boolean canInputFrom(EnumFacing facing) {
		return isConnectable(facing);
	}

	@Override
	public boolean canOutputTo(EnumFacing facing) {
		return false;
	}

	@Override
	public void setSuction(Aspect aspect, int amount) {
	}

	@Override
	public Aspect getSuctionType(EnumFacing facing) {
		return Aspect.MOTION;
	}

	@Override
	public int getSuctionAmount(EnumFacing facing) {
		return isConnectable(facing) && Math.ceil(essentia) < maxEssentia ? 128 : 0;
	}

	@Override
	public int takeEssentia(Aspect aspect, int amount, EnumFacing facing) {
		return 0;
	}

	@Override
	public int addEssentia(Aspect aspect, int amount, EnumFacing facing) {
		int input = (int) (canInputFrom(facing) ? Math.min(amount, maxEssentia - Math.ceil(essentia)) : 0);
		if (input > 0)
			this.markDirty();

		return input;
	}

	@Override
	public Aspect getEssentiaType(EnumFacing facing) {
		return null;
	}

	@Override
	public int getEssentiaAmount(EnumFacing facing) {
		return 0;
	}

	@Override
	public int getMinimumSuction() {
		return 0;
	}
}
