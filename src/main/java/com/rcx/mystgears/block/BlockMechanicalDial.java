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
import mysticalmechanics.api.IAxle;
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
		double sidedPower = 0.0;
		boolean flag = true;
		if (tileEntity instanceof IAxle) {
			IMechCapability handler = tileEntity.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing);
			if (handler != null && handler.getPower(null) != 0){
				text.add(I18n.format("mystgears.tooltip.mechdial.mech", MysticalMechanicsAPI.IMPL.getDefaultUnit().format(handler.getPower(null))));
				return;
			}
		}
		if (tileEntity.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing)) {
			IMechCapability handler = tileEntity.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing);
			if (handler != null && handler.getPower(facing) != 0){
				sidedPower = handler.getPower(facing);
				flag = false;
				text.add(I18n.format("mystgears.tooltip.mechdial.mech", MysticalMechanicsAPI.IMPL.getDefaultUnit().format(handler.getPower(facing))));
			}
		}
		try {
			if (tileEntity.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, null)) {
				IMechCapability handler = tileEntity.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, null);
				if (handler != null && handler.getPower(null) != 0 && handler.getPower(null) != sidedPower) {
					flag = false;
					if (sidedPower == 0)
						text.add(I18n.format("mystgears.tooltip.mechdial.mech", MysticalMechanicsAPI.IMPL.getDefaultUnit().format(handler.getPower(null))));
					else
						text.add(I18n.format("mystgears.tooltip.mechdial.mech_all", MysticalMechanicsAPI.IMPL.getDefaultUnit().format(handler.getPower(null))));
				}
			}
		} catch (NullPointerException e) {}
		if (flag && (tileEntity.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing) || tileEntity.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, null))) {
			text.add(I18n.format("mystgears.tooltip.mechdial.mech", MysticalMechanicsAPI.IMPL.getDefaultUnit().format(0.0)));
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