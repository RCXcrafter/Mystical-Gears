package com.rcx.mystgears.block;

import javax.annotation.Nonnull;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.MysticalMechanics;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEssentiaMotor extends Block {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	private static final AxisAlignedBB X_AABB = new AxisAlignedBB(0, 0.25, 0.25, 1, 0.75, 0.75);
	private static final AxisAlignedBB Y_AABB = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
	private static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.25, 0.25, 0, 0.75, 0.75, 1);

	public BlockEssentiaMotor() {
		super(Material.IRON);
		setHardness(2.0F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("essentia_motor");
		setRegistryName(new ResourceLocation(MysticalGears.MODID, "essentia_motor"));
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
		setCreativeTab(MysticalMechanics.creativeTab);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		switch (state.getValue(FACING).getAxis()) {
		default:
			return X_AABB;
		case X:
			return X_AABB;
		case Y:
			return Y_AABB;
		case Z:
			return Z_AABB;
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		if (state.getValue(FACING).equals(face) || state.getValue(FACING).getOpposite().equals(face))
			return BlockFaceShape.CENTER_SMALL;
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing direction = EnumFacing.getDirectionFromEntityLiving(pos, placer);
		if (!placer.isSneaking())
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
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityEssentiaMotor();
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
		((TileEntityEssentiaMotor)world.getTileEntity(pos)).breakBlock(world, pos, state, player);
		super.onBlockHarvested(world, pos, state, player);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		((TileEntityEssentiaMotor)worldIn.getTileEntity(pos)).breakBlock(worldIn, pos, state, null);
		super.breakBlock(worldIn, pos, state);
	}
}
