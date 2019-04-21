package com.rcx.mystgears.block;

import javax.annotation.Nullable;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.ThaumcraftInvHelper.InvFilter;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;

public class TileEntityMechanicalCrafter extends TilePatternCrafter {

	public DefaultMechCapability mechCapability = new DefaultMechCapability() {
		@Override
		public void setPower(double value, EnumFacing from) {
			if (!world.getBlockState(getPos()).getValue(IBlockFacingHorizontal.FACING).equals(from))
				return;
			super.setPower(value, from);
			if (value != getPower(null)) {
				onPowerChange();
			}
		}

		@Override
		public double getPower(EnumFacing from) {
			if (from == null || world.getBlockState(getPos()).getValue(IBlockFacingHorizontal.FACING).equals(from))
				return super.getPower(from);
			return 0;
		}

		@Override
		public void onPowerChange() {
			TileEntityMechanicalCrafter source = TileEntityMechanicalCrafter.this;
			source.updateNeighbors();
			source.markDirty();
		}

		@Override
		public boolean isInput(EnumFacing from) {
			return world.getBlockState(getPos()).getValue(IBlockFacingHorizontal.FACING).equals(from);
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return false;
		}
	};

	public double currentPower = 0;
	//public final InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerWorkbench(this), 3, 3);

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(IBlockFacingHorizontal.FACING).equals(facing)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && world.getBlockState(getPos()).getValue(IBlockFacingHorizontal.FACING).equals(facing)) {
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
		EnumFacing f = world.getBlockState(getPos()).getValue(IBlockFacingHorizontal.FACING);
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



		if (this.world.isRemote) {
			if (this.rotTicks > 0) {
				--this.rotTicks;
				if ((double) this.rotTicks % Math.floor((double) Math.max(1.0F, this.rp)) == 0.0D) {
					this.world.playSound((double) this.pos.getX() + 0.5D,
							(double) this.pos.getY() + 0.5D,
							(double) this.pos.getZ() + 0.5D, SoundsTC.clack, SoundCategory.BLOCKS,
							0.2F, 1.7F, false);
				}

				++this.rp;
			} else {
				this.rp *= 0.8F;
			}

			this.rot += this.rp;
		}

		/*if (!this.world.isRemote && this.count++ % 20 == 0
				&& BlockStateUtils.isEnabled(this.func_145832_p())) {
			if (this.power <= 0.0F) {
				this.power += AuraHelper.drainVis(this.func_145831_w(), this.func_174877_v(), 5.0F, false);
			}

			int amt = 9;
			switch (this.type) {
			case 0 :
				amt = 9;
				break;
			case 1 :
				amt = 1;
				break;
			case 2 :
			case 3 :
				amt = 2;
				break;
			case 4 :
				amt = 4;
				break;
			case 5 :
			case 6 :
				amt = 3;
				break;
			case 7 :
			case 8 :
				amt = 6;
				break;
			case 9 :
				amt = 8;
			}

			IItemHandler above = ThaumcraftInvHelper.getItemHandlerAt(this.world, this.pos.up(), EnumFacing.DOWN);
			IItemHandler below = ThaumcraftInvHelper.getItemHandlerAt(this.world, this.pos.down(), EnumFacing.UP);
			if (above != null && below != null) {
				for (int a = 0; a < above.getSlots(); ++a) {
					ItemStack testStack = above.getStackInSlot(a).copy();
					if (!testStack.isEmpty()) {
						testStack.func_190920_e(amt);
						if (InventoryUtils
								.removeStackFrom(this.world, this.pos.up(),
										EnumFacing.DOWN, testStack.copy(), InvFilter.BASEORE, true)
								.getCount() == amt && this.craft(testStack) && this.power >= 1.0F
								&& ItemHandlerHelper.insertItem(below, this.outStack.copy(), true)
								.func_190926_b()) {
							boolean b = true;

							int i;
							for (i = 0; i < 9; ++i) {
								if (this.craftMatrix.func_70301_a(i) != null && !ItemHandlerHelper
										.insertItem(below, this.craftMatrix.func_70301_a(i).copy(), true)
										.func_190926_b()) {
									b = false;
									break;
								}
							}

							if (b) {
								ItemHandlerHelper.insertItem(below, this.outStack.copy(), false);

								for (i = 0; i < 9; ++i) {
									if (this.craftMatrix.func_70301_a(i) != null) {
										ItemHandlerHelper.insertItem(below,
												this.craftMatrix.func_70301_a(i).copy(), false);
									}
								}

								InventoryUtils.removeStackFrom(this.func_145831_w(),
										this.func_174877_v().func_177984_a(), EnumFacing.DOWN, testStack,
										InvFilter.BASEORE, false);
								this.field_145850_b.func_175641_c(this.func_174877_v(), this.func_145838_q(), 1, 0);
								--this.power;
								break;
							}
						}
					}
				}
			}
		}*/
	}
}
