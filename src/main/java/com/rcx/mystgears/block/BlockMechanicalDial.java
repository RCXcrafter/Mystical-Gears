package com.rcx.mystgears.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import teamroots.embers.block.BlockBaseGauge;
import javax.annotation.Nullable;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;

import java.util.ArrayList;

public class BlockMechanicalDial extends BlockBaseGauge {

	public static final String DIAL_TYPE = "mechanical";

	public BlockMechanicalDial(Material material, String name, boolean addToTab) {
		super(material);
		setUnlocalizedName(name);
		setRegistryName(MysticalGears.MODID, name);
		setCreativeTab(MysticalMechanics.creativeTab);
		setIsFullCube(false);
		setIsOpaqueCube(false);
		setHarvestProperties("pickaxe", 0);
		setHardness(1.0f);
	}

	@Override
	protected void getTEData(EnumFacing facing, ArrayList<String> text, TileEntity tileEntity) {
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
	}

	@Override
	public String getDialType() {
		return DIAL_TYPE;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityMechanicalDial();
	}
}