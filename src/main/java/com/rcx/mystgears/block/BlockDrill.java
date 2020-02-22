package com.rcx.mystgears.block;

import javax.annotation.Nonnull;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.MysticalMechanics;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockDrill extends BlockDrillBase {

	public BlockDrill() {
		super();
		setHardness(2.0F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("drill");
		setRegistryName(new ResourceLocation(MysticalGears.MODID, "drill"));
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(TIP, false));
		setCreativeTab(MysticalMechanics.creativeTab);
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityDrill();
	}
}
