package com.rcx.mystgears.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockDrillBase extends Block {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final PropertyBool TIP = PropertyBool.create("tip");

	public BlockDrillBase() {
		super(Material.IRON);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityDrill) {
			((TileEntityDrill) tile).updateNeighbors();
			if (pos.offset(state.getValue(FACING)).equals(fromPos)) {
				IBlockState state2 = world.getBlockState(fromPos);
				((TileEntityDrill) tile).setBlock(state2, fromPos, state2.getBlockHardness(world, fromPos));
			}
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing direction = EnumFacing.getDirectionFromEntityLiving(pos, placer);
		if (placer.isSneaking())
			direction = direction.getOpposite();
		return this.getDefaultState().withProperty(FACING, direction);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, TIP});
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	/*@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
		((TileEntityDrill)world.getTileEntity(pos)).breakBlock(world, pos, state, player);
		super.onBlockHarvested(world, pos, state, player);
	}*/

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		((TileEntityDrill)worldIn.getTileEntity(pos)).breakBlock(worldIn, pos, state, null);
		super.breakBlock(worldIn, pos, state);
	}
}
