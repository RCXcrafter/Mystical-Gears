package com.rcx.mystgears.block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.MysticalMechanics;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;

public class BlockMechanicalCrafter extends Block implements IBlockFacingHorizontal, IBlockEnabled {

	private RayTracer rayTracer = new RayTracer();

	public BlockMechanicalCrafter() {
		super(Material.WOOD);
		setHardness(2.0F);
		setSoundType(SoundType.WOOD);
		setDefaultState(blockState.getBaseState().withProperty(IBlockFacingHorizontal.FACING, EnumFacing.NORTH).withProperty(IBlockEnabled.ENABLED, true));
		setUnlocalizedName("crafter_mechanical");
		setRegistryName(new ResourceLocation(MysticalGears.MODID, "crafter_mechanical"));
		setCreativeTab(MysticalMechanics.creativeTab);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(IBlockFacingHorizontal.FACING, placer.getHorizontalFacing());
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
		if (hit == null) {
			return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
		}
		TileEntity tile = world.getTileEntity(pos);
		if (hit.subHit == 0 && tile instanceof TileEntityMechanicalCrafter) {
			if (!world.isRemote) {
				((TileEntityMechanicalCrafter) tile).cycle();
				world.playSound((EntityPlayer) null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundsTC.key, SoundCategory.BLOCKS, 0.5F, 1.0F);
			}
			return true;
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		RayTraceResult hit;
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityMechanicalCrafter && (hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos)) != null && hit.subHit == 0) {
			Cuboid6 cubeoid = ((TileEntityMechanicalCrafter) tile).getCuboidByFacing(BlockStateUtils.getFacing(tile.getBlockMetadata()));
			Vector3 v = new Vector3(pos);
			Cuboid6 c = cubeoid.sub(v);
			return new AxisAlignedBB(((float) c.min.x), ((float) c.min.y), ((float) c.min.z), ((float) c.max.x), ((float) c.max.y), ((float) c.max.z)).offset(pos);
		}
		return super.getSelectedBoundingBox(state, world, pos);
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		if (event.getTarget().typeOfHit == Type.BLOCK && event.getPlayer().world.getBlockState(event.getTarget().getBlockPos()).getBlock() == this) {
			RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), event.getTarget().getBlockPos());
		}
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityMechanicalCrafter) {
			List<IndexedCuboid6> cuboids = new LinkedList();
			if (tile instanceof TileEntityMechanicalCrafter) {
				((TileEntityMechanicalCrafter) tile).addTraceableCuboids(cuboids);
			}
			ArrayList<ExtendedMOP> list = new ArrayList();
			this.rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, list);
			return list.size() > 0 ? (RayTraceResult) list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
		}
		return super.collisionRayTrace(state, world, pos, start, end);
	}


	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		IBlockState state = world.getBlockState(pos);
		for (IProperty prop : state.getProperties().keySet()) {
			if (!prop.getName().equals("facing"))
				continue;
			world.setBlockState(pos, state.cycleProperty(prop));
			return true;
		}
		return false;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);
		this.updateState(worldIn, pos, state);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		worldIn.getTileEntity(pos);
    }

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos frompos) {
		this.updateState(worldIn, pos, state);
		super.neighborChanged(state, worldIn, pos, blockIn, frompos);
	}

	protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
		boolean flag = !worldIn.isBlockPowered(pos);
		if (flag != (Boolean) state.getValue((IProperty) IBlockEnabled.ENABLED))
			worldIn.setBlockState(pos, state.withProperty((IProperty) IBlockEnabled.ENABLED, (Comparable) Boolean.valueOf(flag)), 3);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty((IProperty) IBlockFacingHorizontal.FACING, (Comparable) BlockStateUtils.getFacing(meta)).withProperty((IProperty) IBlockEnabled.ENABLED, (Comparable) Boolean.valueOf(BlockStateUtils.isEnabled(meta)));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = state.getValue(IBlockFacingHorizontal.FACING).getIndex();
		if (!state.getValue(IBlockEnabled.ENABLED)) {
			i |= 0x8;
		}
		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {IBlockFacingHorizontal.FACING, IBlockEnabled.ENABLED});
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityMechanicalCrafter();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
}
