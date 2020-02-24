package com.rcx.mystgears.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;

import java.util.ArrayList;
import java.util.List;

public class BlockMechanicalDial extends Block {

	public static final PropertyDirection FACING = PropertyDirection.create("facing");

	public static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.125, 0.6875);
	public static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.3125, 0.875, 0.3125, 0.6875, 1.0, 0.6875);
	public static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.875, 0.6875, 0.6875, 1.0);
	public static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.125);
	public static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875, 0.3125, 0.3125, 1.0, 0.6875, 0.6875);
	public static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0, 0.3125, 0.3125, 0.125, 0.6875, 0.6875);

	public BlockMechanicalDial() {
		super(Material.IRON);
		setHardness(1.0f);
		setUnlocalizedName("mechanical_dial");
		setRegistryName(MysticalGears.MODID, "mechanical_dial");
		setCreativeTab(MysticalMechanics.creativeTab);
		fullBlock = false;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(FACING, face);
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side){
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
		if (world.isAirBlock(pos.offset(state.getValue(FACING),-1))){
			world.setBlockToAir(pos);
			this.dropBlockAsItem(world, pos, state, 0);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		switch (state.getValue(FACING)){
		case UP:
			return UP_AABB;
		case DOWN:
			return DOWN_AABB;
		case NORTH:
			return NORTH_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case WEST:
			return WEST_AABB;
		case EAST:
			return EAST_AABB;
		}
		return DOWN_AABB;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGameOverlayRender(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.TEXT) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			World world = player.getEntityWorld();
			RayTraceResult result = player.rayTrace(6.0, event.getPartialTicks());
			IBlockState blockstate = world.getBlockState(result.getBlockPos());
			if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && blockstate.getBlock() instanceof BlockMechanicalDial) {
				List<String> text = this.getTextOverlay(world, result.getBlockPos(), blockstate);
				for (int i = 0; i < text.size(); i++) {
					Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text.get(i), event.getResolution().getScaledWidth() / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(text.get(i)) / 2, event.getResolution().getScaledHeight() / 2 + 40 + 11 * i, 0xFFFFFF);
				}
			}
		}
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("minecraft:textures/gui/icons.png"));
		GlStateManager.enableDepth();
	}

	public List<String> getTextOverlay(World world, BlockPos pos, IBlockState state) {
		ArrayList<String> text = new ArrayList<>();
		EnumFacing facing = state.getValue(FACING);
		TileEntity tileEntity = world.getTileEntity(pos.offset(facing.getOpposite()));
		if (tileEntity != null)
			for (EnumFacing direction : EnumFacing.values()) {
				if (tileEntity.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, direction)) {
					IMechCapability handler = tileEntity.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, direction);
					if (handler != null) {
						if (handler.isInput(direction)) {
							if (handler.isOutput(direction))
								text.add(I18n.format("mystgears.tooltip.mechdial.mech", MysticalMechanicsAPI.IMPL.getDefaultUnit().format(handler.getPower(direction))));
							else
								text.add(I18n.format("mystgears.tooltip.mechdial.mech_input", MysticalMechanicsAPI.IMPL.getDefaultUnit().format(handler.getPower(direction))));
						} else if (handler.isOutput(direction)) {
							text.add(I18n.format("mystgears.tooltip.mechdial.mech_output", MysticalMechanicsAPI.IMPL.getDefaultUnit().format(handler.getPower(direction))));
						}
					}
				}
			}
		return text;
	}
}