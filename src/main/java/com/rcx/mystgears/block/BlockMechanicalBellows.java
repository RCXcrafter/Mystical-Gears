package com.rcx.mystgears.block;

import javax.annotation.Nonnull;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.compatibility.BotaniaCompat;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.state.BotaniaStateProps;

public class BlockMechanicalBellows extends Block implements ILexiconable  {

	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.1875, 0, 0.1875, 1-0.1875, 14.0 / 16, 1);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.1875, 0, 0, 1-0.1875, 14.0 / 16, 1-0.1875);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.1875, 0, 0.1875, 1, 14.0 / 16, 1-0.1875);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0, 0, 0.1875, 1-0.1875, 14.0 / 16, 1-0.1875);

	public BlockMechanicalBellows() {
		super(Material.WOOD);
		setHardness(2.0F);
		setSoundType(SoundType.WOOD);
		setDefaultState(blockState.getBaseState().withProperty(BotaniaStateProps.CARDINALS, EnumFacing.SOUTH));
		setUnlocalizedName("bellows_mechanical");
		setRegistryName(new ResourceLocation(MysticalGears.MODID, "bellows_mechanical"));
		setCreativeTab(MysticalGears.tab);
	}

	@Nonnull
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BotaniaStateProps.CARDINALS);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BotaniaStateProps.CARDINALS).getIndex();
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta < 2 || meta > 5) {
			meta = 2;
		}
		return getDefaultState().withProperty(BotaniaStateProps.CARDINALS, EnumFacing.getFront(meta));
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		switch (state.getValue(BotaniaStateProps.CARDINALS)) {
		case UP:
		default:
			return NORTH_AABB;
		case NORTH:
			return NORTH_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case WEST:
			return WEST_AABB;
		case EAST:
			return EAST_AABB;
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(BotaniaStateProps.CARDINALS, placer.getHorizontalFacing()), 2);
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
		((TileEntityMechanicalBellows)world.getTileEntity(pos)).breakBlock(world,pos,state,player);
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityMechanicalBellows();
	}

	@Override
	public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
		return BotaniaCompat.bellowsEntry;
	}
}
