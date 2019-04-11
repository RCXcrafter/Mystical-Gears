package com.rcx.mystgears.block;

import java.awt.Color;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.IManaNetwork;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.block.ModBlocks;

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

		@Override
		public boolean isInput(EnumFacing from) {
			return false;
		}
	};

	public static final int LINK_RANGE = 10;

	private static final String TAG_MANA = "mana";

	private static final String TAG_POOL_X = "poolX";
	private static final String TAG_POOL_Y = "poolY";
	private static final String TAG_POOL_Z = "poolZ";

	public static final int ROTATION_POWER = 30;
	public static final int MANA_USAGE = 10;

	public int mana = 0;

	public int redstoneSignal = 0;

	int sizeLastCheck = -1;
	TileEntity linkedPool = null;
	public int knownMana = -1;

	BlockPos cachedPoolCoordinates = null;
	BlockPos spoofLocation = null;

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

	public void linkPool() {
		boolean needsNew = false;
		if(linkedPool == null) {
			needsNew = true;

			if(cachedPoolCoordinates != null) {
				needsNew = false;
				if(getWorld().isBlockLoaded(cachedPoolCoordinates)) {
					needsNew = true;
					TileEntity tileAt = getWorld().getTileEntity(cachedPoolCoordinates);
					if(tileAt != null && tileAt instanceof IManaPool && !tileAt.isInvalid()) {
						linkedPool = tileAt;
						needsNew = false;
					}
					cachedPoolCoordinates = null;
				}
			}
		} else {
			TileEntity tileAt = getWorld().getTileEntity(linkedPool.getPos());
			if(tileAt != null && tileAt instanceof IManaPool)
				linkedPool = tileAt;
		}

		if(needsNew && ticksExisted == 1) { // Only for new flowers
			IManaNetwork network = BotaniaAPI.internalHandler.getManaNetworkInstance();
			int size = network.getAllPoolsInWorld(getWorld()).size();
			if(BotaniaAPI.internalHandler.shouldForceCheck() || size != sizeLastCheck) {
				linkedPool = network.getClosestPool(getPos(), getWorld(), LINK_RANGE);
				sizeLastCheck = size;
			}
		}
	}

	public void linkToForcefully(TileEntity pool) {
		linkedPool = pool;
	}

	public void addMana(int mana) {
		this.mana = Math.min(getMaxMana(), this.mana + mana);
	}

	public boolean onWanded(ItemStack wand, EntityPlayer player) {
		if(player == null)
			return false;

		knownMana = mana;
		SoundEvent evt = ForgeRegistries.SOUND_EVENTS.getValue(SubTileEntity.DING_SOUND_EVENT);
		if(evt != null)
			player.playSound(evt, 0.1F, 1F);

		return false;
	}

	public int getMaxMana() {
		return 1000;
	}

	public int getColor() {
		return 0x7130FF;
	}

	@Override
	public BlockPos getBinding() {
		if(linkedPool == null)
			return null;
		return linkedPool.getPos();
	}

	@Override
	public boolean canSelect(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean bindTo(EntityPlayer player, ItemStack wand, BlockPos pos, EnumFacing side) {
		int range = 10;
		range *= range;

		double dist = pos.distanceSq(getPos());
		if(range >= dist) {
			TileEntity tile = player.world.getTileEntity(pos);
			if(tile instanceof IManaPool) {
				linkedPool = tile;
				return true;
			}
		}

		return false;
	}

	public boolean isValidBinding() {
		return linkedPool != null && linkedPool.hasWorld() && !linkedPool.isInvalid() && getWorld().isBlockLoaded(linkedPool.getPos(), false) && getWorld().getTileEntity(linkedPool.getPos()) == linkedPool;
	}

	@SideOnly(Side.CLIENT)
	public void renderHUD(Minecraft mc, ScaledResolution res) {
		String name = I18n.format("tile.flower.gearanium.name");
		int color = getColor();
		BotaniaAPI.internalHandler.drawComplexManaHUD(color, knownMana, getMaxMana(), name, res, new ItemStack(ModBlocks.pool), isValidBinding());
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("mech_power", capability.power);

		tag.setInteger(TAG_MANA, mana);

		if(cachedPoolCoordinates != null) {
			tag.setInteger(TAG_POOL_X, cachedPoolCoordinates.getX());
			tag.setInteger(TAG_POOL_Y, cachedPoolCoordinates.getY());
			tag.setInteger(TAG_POOL_Z, cachedPoolCoordinates.getZ());
		} else {
			int x = linkedPool == null ? 0 : linkedPool.getPos().getX();
			int y = linkedPool == null ? -1 : linkedPool.getPos().getY();
			int z = linkedPool == null ? 0 : linkedPool.getPos().getZ();

			tag.setInteger(TAG_POOL_X, x);
			tag.setInteger(TAG_POOL_Y, y);
			tag.setInteger(TAG_POOL_Z, z);
		}
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("mech_power")){
			capability.power = tag.getDouble("mech_power");
		}

		mana = tag.getInteger(TAG_MANA);

		int x = tag.getInteger(TAG_POOL_X);
		int y = tag.getInteger(TAG_POOL_Y);
		int z = tag.getInteger(TAG_POOL_Z);

		cachedPoolCoordinates = y < 0 ? null : new BlockPos(x, y, z);
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
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY){
			return (T)this.capability;
		}
		return super.getCapability(capability, facing);
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		capability.setPower(0f,null);
		updateNeighbors();
	}

	public void updateNeighbors() {
		for (EnumFacing f : EnumFacing.values()) {
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
		if(isOnSpecialSoil()) {
			this.overgrowth = true;
			this.overgrowthBoost = true;
		} else {
			this.overgrowth = false;
			this.overgrowthBoost = false;
		}

		ticksExisted++;
		linkPool();

		redstoneSignal = 0;
		for(EnumFacing dir : EnumFacing.VALUES) {
			int redstoneSide = getWorld().getRedstonePower(getPos().offset(dir), dir);
			redstoneSignal = Math.max(redstoneSignal, redstoneSide);
		}

		if(getWorld().isRemote) {
			double particleChance = 1F - (double) mana / (double) getMaxMana() / 3.5F;
			Color color = new Color(getColor());
			if(Math.random() > particleChance)
				BotaniaAPI.internalHandler.sparkleFX(getWorld(), getPos().getX() + 0.3 + Math.random() * 0.5, getPos().getY() + 0.5 + Math.random()  * 0.5, getPos().getZ() + 0.3 + Math.random() * 0.5, color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, (float) Math.random(), 5);
		}

		boolean success = redstoneSignal == 0;
		if (success)
			if (overgrowthBoost) {
				success &= mana >= MANA_USAGE * 2;
			} else {
				success &= mana >= MANA_USAGE;
			}

		double wantedPower = overgrowthBoost ? ROTATION_POWER * 2 : ROTATION_POWER;

		int connections = 0;

		IBlockState state = getWorld().getBlockState(getPos());
		state = state.getBlock().getActualState(state, getWorld(), getPos());
		if (success) {
			if (state.getValue(BlockGearanium.DOWN).booleanValue())
				connections += 1;
			if (state.getValue(BlockGearanium.UP).booleanValue())
				connections += 1;
			if (state.getValue(BlockGearanium.NORTH).booleanValue())
				connections += 1;
			if (state.getValue(BlockGearanium.EAST).booleanValue())
				connections += 1;
			if (state.getValue(BlockGearanium.SOUTH).booleanValue())
				connections += 1;
			if (state.getValue(BlockGearanium.WEST).booleanValue())
				connections += 1;
			success &= connections > 0;
		}
		if (!success)
			wantedPower = 0;
		else
			if (overgrowthBoost) {
				mana -= MANA_USAGE * 2;
			} else {
				mana -= MANA_USAGE;
			}

		if (wantedPower > 0 && connections > 0)
			wantedPower /= connections;

		if (capability.getPower(null) != wantedPower){
			capability.setPower(wantedPower, null);
			markDirty();
			getWorld().setBlockState(getPos(), state, 2);
		}

		updateNeighbors();

		if(linkedPool != null && isValidBinding()) {
			IManaPool pool = (IManaPool) linkedPool;
			int manaInPool = pool.getCurrentMana();
			int manaMissing = getMaxMana() - mana;
			int manaToRemove = Math.min(manaMissing, manaInPool);
			pool.recieveMana(-manaToRemove);
			addMana(manaToRemove);
		}
	}
}
