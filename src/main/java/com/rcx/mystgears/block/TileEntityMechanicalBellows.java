package com.rcx.mystgears.block;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.common.block.subtile.functional.SubTileExoflame;
import vazkii.botania.common.block.tile.mana.TileBellows;
import vazkii.botania.common.core.handler.ModSounds;

public class TileEntityMechanicalBellows extends TileBellows {

	public DefaultMechCapability capability = new DefaultMechCapability() {
		@Override
		public void setPower(double value, EnumFacing from) {
			if (!world.getBlockState(getPos()).getValue(BotaniaStateProps.CARDINALS).getOpposite().equals(from))
				return;
			super.setPower(value, from);
			if (value != getPower(null)) {
				onPowerChange();
			}
		}

		@Override
		public double getPower(EnumFacing from) {
			if (from == null || world.getBlockState(getPos()).getValue(BotaniaStateProps.CARDINALS).getOpposite().equals(from))
				return super.getPower(from);
			return 0;
		}

		@Override
		public void onPowerChange() {
			TileEntityMechanicalBellows source = TileEntityMechanicalBellows.this;
			source.updateNeighbors();
			source.markDirty();
		}

		@Override
		public boolean isInput(EnumFacing from) {
			return world.getBlockState(getPos()).getValue(BotaniaStateProps.CARDINALS).getOpposite().equals(from);
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return false;
		}
	};
	public double rotationModifier = 1;
	public double currentPower = 0;

	@Override
	public void onLoad() {
		super.onLoad();
		updateNeighbors();
	}

	@Override
	public void writePacketNBT(NBTTagCompound tag) {
		super.writePacketNBT(tag);
	}

	@Override
	public void readPacketNBT(NBTTagCompound tag) {
		super.readPacketNBT(tag);
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
		updateNeighbors();
	}

	public void updateNeighbors() {
		EnumFacing f = world.getBlockState(getPos()).getValue(BotaniaStateProps.CARDINALS).getOpposite();
		TileEntity t = world.getTileEntity(getPos().offset(f));
		if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()))
			capability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).getPower(f.getOpposite()), f);
		else
			capability.setPower(0, f);

		markDirty();
	}

	@Override
	public void update() {
		if (capability.getPower(null) != currentPower) {
			currentPower = capability.getPower(null);
			markDirty();
		}

		if(moving == 0F && currentPower > 0) {
			setActive(true);
			rotationModifier = currentPower / 40;
		}

		boolean disable = true;
		TileEntity tile = getLinkedTile();

		float max = 0.9F;
		float min = 0F;

		float incr = max / 20F;

		incr *= rotationModifier;

		if(movePos < max && active && moving >= 0F) {
			if(moving == 0F)
				world.playSound(null, pos, ModSounds.bellows, SoundCategory.BLOCKS, 0.1F, 3F);

			if(tile instanceof TileEntityFurnace) {
				TileEntityFurnace furnace = (TileEntityFurnace) tile;
				if(SubTileExoflame.canFurnaceSmelt(furnace)) {
					furnace.setField(2, (int) Math.min(199, furnace.getField(2) + 20 * rotationModifier)); // cookTime
					furnace.setField(0, (int) Math.max(0, furnace.getField(0) - 10 * rotationModifier)); // burnTime
				}

				BlockPos furnacePos = furnace.getPos();
				if(furnace.hasWorld() && furnace.getBlockType() == Blocks.LIT_FURNACE) {
					// Copypasta from TileBellows from BlockFurnace
					EnumFacing enumfacing = world.getBlockState(furnace.getPos()).getValue(BlockFurnace.FACING);
					double d0 = furnacePos.getX() + 0.5D;
					double d1 = furnacePos.getY() + world.rand.nextDouble() * 6.0D / 16.0D;
					double d2 = furnacePos.getZ() + 0.5D;
					double d3 = 0.52D;
					double d4 = world.rand.nextDouble() * 0.6D - 0.3D;

					switch (enumfacing) {
					case WEST:
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
						world.spawnParticle(EnumParticleTypes.FLAME, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
						break;
					case EAST:
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
						world.spawnParticle(EnumParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
						break;
					case NORTH:
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D);
						world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D);
						break;
					case SOUTH:
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D);
						world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D);
					default: break;
					}
				}
			}

			movePos += incr * 3;
			moving = incr * 3;
			if (movePos >= max) {
				movePos = Math.min(max, movePos);
				moving = 0F;
				if (disable)
					setActive(false);
			}
		} else if (movePos > min) {
			movePos -= incr;
			moving = -incr;
			if (movePos <= min) {
				movePos = Math.max(min, movePos);
				moving = 0F;
			}
		}
	}
}
