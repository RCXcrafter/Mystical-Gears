package com.rcx.mystgears.block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.rcx.mystgears.MysticalGears;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.crafting.TilePatternCrafter;

public class BlockMechanicalCrafter extends BlockTCDevice implements IBlockFacingHorizontal, IBlockEnabled {

	private RayTracer rayTracer = new RayTracer();

	public BlockMechanicalCrafter() {
		super(Material.WOOD, TileEntityMechanicalCrafter.class, "crafter_mechanical");
		setHardness(2.0F);
		setSoundType(SoundType.WOOD);
		//setDefaultState(blockState.getBaseState().withProperty(IBlockFacingHorizontal.FACING, EnumFacing.NORTH).withProperty(IBlockEnabled.ENABLED, true));
		//setUnlocalizedName("crafter_mechanical");
		//setRegistryName(new ResourceLocation(MysticalGears.MODID, "crafter_mechanical"));
		setCreativeTab(MysticalGears.tab);
	}

	@Override
	public int func_180651_a(IBlockState state) {
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
	public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		IBlockState bs = this.getDefaultState();
		bs = bs.withProperty(IBlockFacingHorizontal.FACING, placer.getHorizontalFacing());
		return bs;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		RayTraceResult hit = RayTracer.retraceBlock(world, player, pos);
		if (hit == null) {
			return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
		}
		TileEntity tile = world.getTileEntity(pos);
		if (hit.subHit == 0 && tile instanceof TilePatternCrafter) {
			if (!world.isRemote) {
				((TilePatternCrafter) tile).cycle();
				world.playSound((EntityPlayer) null, (double) pos.getX() + 0.5D,
						(double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundsTC.key,
						SoundCategory.BLOCKS, 0.5F, 1.0F);
			}

			return true;
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TilePatternCrafter) {
			RayTraceResult hit = RayTracer.retraceBlock(world, Minecraft.getMinecraft().player, pos);
			if (hit != null && hit.subHit == 0) {
				Cuboid6 cubeoid = ((TilePatternCrafter) tile).getCuboidByFacing(BlockStateUtils.getFacing(world.getBlockState(tile.getPos())));
				Vector3 v = new Vector3(pos);
				Cuboid6 c = cubeoid.sub(v);
				return (new AxisAlignedBB((double) ((float) c.min.x), (double) ((float) c.min.y),
						(double) ((float) c.min.z), (double) ((float) c.max.x), (double) ((float) c.max.y),
						(double) ((float) c.max.z))).offset(pos);
			}
		}
		return this.FULL_BLOCK_AABB;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return this.FULL_BLOCK_AABB;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		if (event.getTarget().typeOfHit == Type.BLOCK && event.getPlayer().world
				.getBlockState(event.getTarget().getBlockPos()).getBlock() == this) {
			RayTracer.retraceBlock(event.getPlayer().world, event.getPlayer(), event.getTarget().getBlockPos());
		}

	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && tile instanceof TilePatternCrafter) {
			List<IndexedCuboid6> cuboids = new LinkedList();
			if (tile instanceof TilePatternCrafter) {
				((TilePatternCrafter) tile).addTraceableCuboids(cuboids);
			}

			ArrayList<ExtendedMOP> list = new ArrayList();
			this.rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this,
					list);
			return list.size() > 0 ? (RayTraceResult) list.get(0) : super.collisionRayTrace(state, world, pos, start, end);
		}
		return super.collisionRayTrace(state, world, pos, start, end);
	}
}
