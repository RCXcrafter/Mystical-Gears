package com.rcx.mystgears.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.TileEntityGearanium;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.lexicon.IRecipeKeyProvider;
import vazkii.botania.common.core.handler.ConfigHandler;

public class ItemBlockGearanium extends ItemBlock implements IRecipeKeyProvider {

	public ItemBlockGearanium(Block block) {
		super(block);
	}

	@Override
	public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, @Nonnull IBlockState newState) {
		boolean placed = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
		if(placed) {
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileEntityGearanium) {
				TileEntityGearanium tile = (TileEntityGearanium) te;
				//tile.onBlockAdded(world, pos, newState);
				//tile.onBlockPlacedBy(world, pos, newState, player, stack);
				if(!world.isRemote)
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 8);
			}
		}
		return placed;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(@Nonnull ItemStack par1ItemStack, World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
		tooltip.add(TextFormatting.BLUE + I18n.format("botania.flowerType.functional"));
		if(ConfigHandler.referencesEnabled) {
			tooltip.add(TextFormatting.ITALIC + I18n.format("desc.gearanium.name"));
		}
		tooltip.add(TextFormatting.ITALIC + "[" + MysticalGears.NAME + "]");
	}

	@Override
	public String getKey(ItemStack arg0) {
		return "flower.gearanium";
	}
}
