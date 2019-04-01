package com.rcx.mystgears.block;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.ISubTileSlowableContainer;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.TileSpecialFlower;
import vazkii.botania.common.block.tile.string.TileRedStringRelay;

public class TileEntityGearanium extends TileEntity implements IWandBindable, ITickable {
	int ticksExisted = 0;
	public DefaultMechCapability capability = new DefaultMechCapability(){
		@Override
		public void setPower(double value, EnumFacing from) {
			if(from == null)
				super.setPower(value, from);
		}

		@Override
		public void onPowerChange(){
			TileEntityGearanium source = TileEntityGearanium.this;
			source.updateNeighbors();
			source.markDirty();
		}
	};
	
	Boolean overgrowth = false;
	Boolean overgrowthBoost = false;
	

	public TileEntityGearanium(){
		super();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	
	
	
	
	

	public boolean isOnSpecialSoil() {
		return world.getBlockState(pos.down()).getBlock() == ModBlocks.enchantedSoil;
	}

	public LexiconEntry getEntry() {
		return null;
	}

	public boolean onWanded(ItemStack wand, EntityPlayer player) {
		return false;
	}

	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
		
	}

	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		
	}

	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		
	}

	public List<ItemStack> getDrops(List<ItemStack> list) {
		return list;
	}

	@Override
	public boolean receiveClientEvent(int id, int param) {
		return super.receiveClientEvent(id, param);
	}

	@SideOnly(Side.CLIENT)
	public void renderHUD(Minecraft mc, ScaledResolution res) {
		
	}

	@Override
	public BlockPos getBinding() {
			return null;
	}

	@Override
	public boolean canSelect(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
			return false;
	}

	@Override
	public boolean bindTo(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
			return false;
	}

	public int getLightValue() {
			return 0;
	}

	public int getComparatorInputOverride() {
			return 0;
	}

	public int getPowerLevel(EnumFacing side) {
			return 0;
	}
	
	
	
	
	
	
	
	
	
	

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setDouble("mech_power", capability.power);
		//tag.setInteger("level", wantedPowerIndex);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		if (tag.hasKey("mech_power")){
			capability.power = tag.getDouble("mech_power");
		}
		//wantedPowerIndex = tag.getInteger("level") % wantedPower.length;
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
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY){
			return (T)this.capability;
		}
		return super.getCapability(capability, facing);
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		capability.setPower(0f,null);
		updateNeighbors();
	}

	public void updateNeighbors(){
		for (EnumFacing f : EnumFacing.values()){
			TileEntity t = world.getTileEntity(getPos().offset(f));
			if (t != null){
				if (t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite())){
					t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).setPower(capability.getPower(f.getOpposite()),f.getOpposite());
					t.markDirty();
				}
			}
		}
	}

	@Override
	public void update() {
		ticksExisted++;
		double wantedPower = 80;//this.wantedPower[wantedPowerIndex];
		if (capability.getPower(null) != wantedPower){
			capability.setPower(wantedPower,null);
			markDirty();
		}
		updateNeighbors();
		
		
		
		if("subtile" != null) {
			TileEntity tileBelow = world.getTileEntity(pos.down());
			if(tileBelow instanceof TileRedStringRelay) {
				BlockPos coords = ((TileRedStringRelay) tileBelow).getBinding();
				if(coords != null) {
					BlockPos currPos = pos;
					setPos(coords);
					//subTile.onUpdate();
					setPos(currPos);

					return;
				}
			}

			boolean special = isOnSpecialSoil();
			if(special) {
				this.overgrowth = true;
				//subTile.onUpdate();
				this.overgrowthBoost = true;
				
			}
			//subTile.onUpdate();
			this.overgrowth = false;
			this.overgrowthBoost = false;
		}
	}
}
