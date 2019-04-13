package com.rcx.mystgears.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.rcx.mystgears.BotaniaCompat;
import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.subtile.ISpecialFlower;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ModItems;

public class BlockGearanium extends BlockFlower implements ISpecialFlower, IWandable, ILexiconable, IWandHUD {

	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	static final AxisAlignedBB AABB = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);

	public BlockGearanium() {
		setDefaultState(blockState.getBaseState().withProperty(BotaniaStateProps.COLOR, EnumDyeColor.WHITE).withProperty(type, EnumFlowerType.POPPY).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(UP, Boolean.valueOf(false)).withProperty(DOWN, Boolean.valueOf(false)));
		this.hasTileEntity = true;
		setRegistryName(MysticalGears.MODID, "gearanium");
		setUnlocalizedName("flower.gearanium");
		setHardness(0.1F);
		setSoundType(SoundType.PLANT);
		setTickRandomly(false);
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
		return AABB;
	}

	@Override
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.NONE;
	}

	@Nonnull
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { BotaniaStateProps.COLOR, getTypeProperty(), NORTH, EAST, SOUTH, WEST, UP, DOWN, ACTIVE } );
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		Boolean down = isAxle(worldIn, pos, EnumFacing.DOWN);
		Boolean up = isAxle(worldIn, pos, EnumFacing.UP);
		Boolean north = isAxle(worldIn, pos, EnumFacing.NORTH);
		Boolean east = isAxle(worldIn, pos, EnumFacing.EAST);
		Boolean south = isAxle(worldIn, pos, EnumFacing.SOUTH);
		Boolean west = isAxle(worldIn, pos, EnumFacing.WEST);
		Boolean active = false;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, EnumFacing.UP))
			active = tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, EnumFacing.UP).getPower(EnumFacing.UP) > 0;
			return state.withProperty(DOWN, down).withProperty(UP, up).withProperty(NORTH, north).withProperty(EAST, east).withProperty(SOUTH, south).withProperty(WEST, west).withProperty(ACTIVE, active);
	}

	public boolean isAxle(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		TileEntity tile = worldIn.getTileEntity(pos.offset(facing));
		if (tile == null)
			return false;
		return tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()) && tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).isInput(facing.getOpposite());
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BotaniaStateProps.COLOR).getMetadata();
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta > 15) {
			meta = 0;
		}
		return getDefaultState().withProperty(BotaniaStateProps.COLOR, EnumDyeColor.byMetadata(meta));
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
	}

	@Nonnull
	@Override
	public EnumFlowerColor getBlockType() {
		return EnumFlowerColor.RED;
	}

	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
		return new ItemStack(this);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).getBlock() == ModBlocks.redStringRelay || world.getBlockState(pos.down()).getBlock() == Blocks.MYCELIUM || this.isAxle(world, pos, EnumFacing.DOWN) || super.canPlaceBlockAt(world, pos);
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == ModBlocks.redStringRelay || state.getBlock() == Blocks.MYCELIUM || super.canSustainBush(state);
	}

	@Override
	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			((TileEntityGearanium) worldIn.getTileEntity(pos)).breakBlock(worldIn, pos, state, null);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return super.canBlockStay(worldIn, pos, state) || isAxle(worldIn, pos, EnumFacing.DOWN);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> list, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
		list.add(new ItemStack(this));
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int par5, int par6) {
		super.eventReceived(state, world, pos, par5, par6);
		TileEntity tileentity = world.getTileEntity(pos);
		state = this.getActualState(state, world, pos);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}

	@Override
	public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
		return BotaniaCompat.gearaniumEntry;
	}

	@Override
	public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, BlockPos pos, EnumFacing side) {
		return ((TileEntityGearanium) world.getTileEntity(pos)).onWanded(stack, player);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if(!stack.isEmpty() && stack.getItem() == ModItems.dye) {
			EnumDyeColor newColor = EnumDyeColor.byMetadata(stack.getItemDamage());
			EnumDyeColor oldColor = state.getValue(BotaniaStateProps.COLOR);
			if(newColor != oldColor)
				world.setBlockState(pos, state.withProperty(BotaniaStateProps.COLOR, newColor), 1 | 2);
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderHUD(Minecraft mc, ScaledResolution res, World world, BlockPos pos) {
		((TileEntityGearanium) world.getTileEntity(pos)).renderHUD(mc, res);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityGearanium();
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
		((TileEntityGearanium)world.getTileEntity(pos)).breakBlock(world, pos, state, player);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		((TileEntityGearanium)worldIn.getTileEntity(pos)).breakBlock(worldIn, pos, state, null);
		super.breakBlock(worldIn, pos, state);
	}
}
