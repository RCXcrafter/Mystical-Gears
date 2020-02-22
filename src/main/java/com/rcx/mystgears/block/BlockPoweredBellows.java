package com.rcx.mystgears.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.MysticalMechanics;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockPoweredBellows extends com.codetaylor.mc.pyrotech.modules.tech.machine.block.BlockMechanicalBellows {

	public BlockPoweredBellows() {
		super();
		setSoundType(SoundType.STONE);
		setUnlocalizedName("powered_bellows");
		setRegistryName(new ResourceLocation(MysticalGears.MODID, "powered_bellows"));
		setCreativeTab(MysticalMechanics.creativeTab);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		if (state.getValue(TYPE) == EnumType.TOP) {
			return new TileEntityPoweredBellowsTop();
		}
		return new TileEntityPoweredBellows();
	}
}
