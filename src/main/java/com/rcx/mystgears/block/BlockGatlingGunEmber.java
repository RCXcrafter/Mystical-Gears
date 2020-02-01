package com.rcx.mystgears.block;

import javax.annotation.Nonnull;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.MysticalMechanics;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockGatlingGunEmber extends BlockGatlingGunBase {

	public BlockGatlingGunEmber() {
		super(Material.IRON);
		setHardness(2.0F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("ember_gatling_gun");
		setRegistryName(new ResourceLocation(MysticalGears.MODID, "ember_gatling_gun"));
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
		setCreativeTab(MysticalMechanics.creativeTab);
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityGatlingGunEmber();
	}
}
