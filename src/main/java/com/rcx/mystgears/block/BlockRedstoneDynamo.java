package com.rcx.mystgears.block;

import javax.annotation.Nonnull;

import com.rcx.mystgears.MysticalGears;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneDynamo extends Block {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public BlockRedstoneDynamo() {
		super(Material.IRON);
		setHardness(2.0F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("redstone_dynamo");
		setRegistryName(new ResourceLocation(MysticalGears.MODID, "redstone_dynamo"));
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN));
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing direction = facing;
		if (placer.isSneaking())
			direction = direction.getOpposite();
		return this.getDefaultState().withProperty(FACING, direction);
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}

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
		return new TileEntityRedstoneDynamo();
	}
}
