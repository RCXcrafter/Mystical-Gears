package com.rcx.mystgears.block;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
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
	private final InventoryCrafting craftMatrix = new InventoryCrafting(new Container() {

		public boolean canInteractWith(EntityPlayer playerIn) {
			return false;
		}
	}, 3, 3);
	ItemStack outStack = null;

	@Override
	public void onLoad() {
		super.onLoad();
		updateNeighbors();
	}

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

		if (this.world.isRemote)
			this.rot += currentPower/3;

		int delay = (int) (20 / (currentPower / 30));

		if (!this.world.isRemote && this.count++ % delay == 0 && BlockStateUtils.isEnabled(this.getBlockMetadata())) {
			int amt = 9;
			switch (this.type) {
			case 0 : {
				amt = 9;
				break;
			}
			case 1 : {
				amt = 1;
				break;
			}
			case 2 :
			case 3 : {
				amt = 2;
				break;
			}
			case 4 : {
				amt = 4;
				break;
			}
			case 5 :
			case 6 : {
				amt = 3;
				break;
			}
			case 7 :
			case 8 : {
				amt = 6;
				break;
			}
			case 9 : {
				amt = 8;
			}
			}
			IItemHandler above = ThaumcraftInvHelper.getItemHandlerAt(this.getWorld(),
					this.getPos().up(), EnumFacing.DOWN);
			IItemHandler below = ThaumcraftInvHelper.getItemHandlerAt(this.getWorld(),
					this.getPos().down(), EnumFacing.UP);
			if (above != null && below != null) {
				for (int a = 0; a < above.getSlots(); ++a) {
					int i;
					ItemStack testStack = above.getStackInSlot(a).copy();
					if (testStack.isEmpty())
						continue;
					testStack.setCount(amt);
					if (InventoryUtils.removeStackFrom(this.getWorld(), this.getPos().up(),
							EnumFacing.DOWN, testStack.copy(),
							ThaumcraftInvHelper.InvFilter.BASEORE, true)
							.getCount() != amt
							|| !this.craft(testStack) || currentPower <= 0
							|| !ItemHandlerHelper
							.insertItem(below, this.outStack.copy(), true)
							.isEmpty())
						continue;
					boolean b = true;
					for (i = 0; i < 9; ++i) {
						if (this.craftMatrix.getStackInSlot(i) == null
								|| ItemHandlerHelper
								.insertItem(below,
										this.craftMatrix.getStackInSlot(i).copy(), true)
								.isEmpty())
							continue;
						b = false;
						break;
					}
					if (!b)
						continue;
					ItemHandlerHelper.insertItem(below, this.outStack.copy(),
							false);
					for (i = 0; i < 9; ++i) {
						if (this.craftMatrix.getStackInSlot(i) == null)
							continue;
						ItemHandlerHelper.insertItem(below,
								this.craftMatrix.getStackInSlot(i).copy(), false);
					}
					InventoryUtils.removeStackFrom(this.getWorld(), this.getPos().up(),
							EnumFacing.DOWN, testStack,
							ThaumcraftInvHelper.InvFilter.BASEORE, false);
					this.world.addBlockEvent(this.getPos(), this.getBlockType(), 1, 0);
					break;
				}
			}
		}
	}

	public boolean craft(ItemStack inStack) {
		this.outStack = ItemStack.EMPTY;
		this.craftMatrix.clear();
		int b;
		int a;
		switch (this.type) {
		case 0: {
			for (a = 0; a < 9; ++a) {
				this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
			}
			break;
		}
		case 1: {
			this.craftMatrix.setInventorySlotContents(0, ItemHandlerHelper.copyStackWithSize(inStack, 1));
			break;
		}
		case 2: {
			for (a = 0; a < 2; ++a) {
				this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
			}
			break;
		}
		case 3: {
			for (a = 0; a < 2; ++a) {
				this.craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
			}
			break;
		}
		case 4: {
			for (a = 0; a < 2; ++a) {
				for (b = 0; b < 2; ++b) {
					this.craftMatrix.setInventorySlotContents(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
				}
			}
			break;
		}
		case 5: {
			for (a = 0; a < 3; ++a) {
				this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
			}
			break;
		}
		case 6: {
			for (a = 0; a < 3; ++a) {
				this.craftMatrix.setInventorySlotContents(a * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
			}
			break;
		}
		case 7: {
			for (a = 0; a < 6; ++a) {
				this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
			}
			break;
		}
		case 8: {
			for (a = 0; a < 2; ++a) {
				for (b = 0; b < 3; ++b) {
					this.craftMatrix.setInventorySlotContents(a + b * 3, ItemHandlerHelper.copyStackWithSize(inStack, 1));
				}
			}
			break;
		}
		case 9: {
			for (a = 0; a < 9; ++a) {
				if (a == 4) continue;
				this.craftMatrix.setInventorySlotContents(a, ItemHandlerHelper.copyStackWithSize(inStack, 1));
			}
			break;
		}
		}
		IRecipe ir = CraftingManager.findMatchingRecipe(this.craftMatrix, this.world);
		if (ir == null) {
			return false;
		}
		this.outStack = ir.getCraftingResult(this.craftMatrix);
		NonNullList aitemstack = CraftingManager.getRemainingItems(this.craftMatrix, this.world);
		for (int i = 0; i < aitemstack.size(); ++i) {
			ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);
			ItemStack itemstack2 = (ItemStack)aitemstack.get(i);
			if (!itemstack1.isEmpty()) {
				this.craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
			}
			if (itemstack1.isEmpty() || !this.craftMatrix.getStackInSlot(i).isEmpty()) continue;
			this.craftMatrix.setInventorySlotContents(i, itemstack2);
		}
		return !this.outStack.isEmpty();
	}
}
