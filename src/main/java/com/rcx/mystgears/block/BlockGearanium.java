package com.rcx.mystgears.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.api.DefaultMechCapability;
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
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.api.subtile.ISpecialFlower;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.client.core.handler.ModelHandler;
import vazkii.botania.client.render.IModelRegister;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.BotaniaCreativeTab;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.lexicon.LexiconData;

public class BlockGearanium extends BlockFlower implements ISpecialFlower, IWandable, ILexiconable, IWandHUD {

	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	
	static final AxisAlignedBB AABB = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);

	public BlockGearanium() {
		//super();
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
		return new BlockStateContainer(this, new IProperty[] { BotaniaStateProps.COLOR, getTypeProperty(), NORTH, EAST, SOUTH, WEST, UP, DOWN } );
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		Boolean down = isAxle(worldIn, pos, EnumFacing.DOWN);
		Boolean up = isAxle(worldIn, pos, EnumFacing.UP);
		Boolean north = isAxle(worldIn, pos, EnumFacing.NORTH);
		Boolean east = isAxle(worldIn, pos, EnumFacing.EAST);
		Boolean south = isAxle(worldIn, pos, EnumFacing.SOUTH);
		Boolean west = isAxle(worldIn, pos, EnumFacing.WEST);
		return state.withProperty(DOWN, down).withProperty(UP, up).withProperty(NORTH, north).withProperty(EAST, east).withProperty(SOUTH, south).withProperty(WEST, west);
	}
	
	public boolean isAxle(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
		TileEntity tile = worldIn.getTileEntity(pos.offset(facing));
		if (tile == null)
			return false;
		return tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite());
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

	/*@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		if(world.getBlockState(pos).getBlock() != this)
			return world.getBlockState(pos).getLightValue(world, pos);

		return world.getTileEntity(pos) == null ? 0 : ((TileEntityGearanium) world.getTileEntity(pos)).getLightValue();
	}*/

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	/*@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		return ((TileEntityGearanium) world.getTileEntity(pos)).getComparatorInputOverride();
	}

	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return ((TileEntityGearanium) world.getTileEntity(pos)).getPowerLevel(side);
	}

	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getWeakPower(state, world, pos, side);
	}*/

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
		return world.getBlockState(pos.down()).getBlock() == ModBlocks.redStringRelay
				|| world.getBlockState(pos.down()).getBlock() == Blocks.MYCELIUM
				|| super.canPlaceBlockAt(world, pos);
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == ModBlocks.redStringRelay
				|| state.getBlock() == Blocks.MYCELIUM
				|| super.canSustainBush(state);
	}

	/*@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		((TileSpecialFlower) world.getTileEntity(pos)).onBlockHarvested(world, pos, state, player);
	}*/

	@Override
	public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
		if (willHarvest) {
			// Copy of super.removedByPlayer but don't set to air yet
			// This is so getDrops below will have a TE to work with
			onBlockHarvested(world, pos, state, player);
			return true;
		} else {
			return super.removedByPlayer(state, world, pos, player, willHarvest);
		}
	}

	@Override
	public void harvestBlock(@Nonnull World world, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, TileEntity te, ItemStack stack) {
		super.harvestBlock(world, player, pos, state, te, stack);
		// Now delete the block and TE
		world.setBlockToAir(pos);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> list, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
		list.add(new ItemStack(this));
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int par5, int par6) {
		super.eventReceived(state, world, pos, par5, par6);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}

	/*@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileSpecialFlower();
	}*/

	@Override
	public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.pureDaisy;
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

		return false;//((TileEntityGearanium) world.getTileEntity(pos)).onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
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
		TileEntityGearanium p = (TileEntityGearanium)world.getTileEntity(pos);
		p.breakBlock(world,pos,state,player);
	}
}
