package com.rcx.mystgears.block;

import java.util.HashMap;

import javax.annotation.Nonnull;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.util.IAttachmentBehavior;

import mysticalmechanics.MysticalMechanics;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;

public class BlockTurret extends Block {

	public static HashMap<Ingredient, String> metalTextures = new HashMap<Ingredient, String>();
	public static HashMap<Ingredient, IAttachmentBehavior> attachmentBehaviors = new HashMap<Ingredient, IAttachmentBehavior>();

	static {
		metalTextures.put(new OreIngredient("ingotIron"), "iron");
		metalTextures.put(new OreIngredient("ingotGold"), "gold");
		metalTextures.put(new OreIngredient("stickWood"), "wood");
		metalTextures.put(new OreIngredient("ingotCopper"), "copper");
		metalTextures.put(new OreIngredient("ingotLead"), "lead");
		metalTextures.put(new OreIngredient("ingotSilver"), "silver");
		metalTextures.put(new OreIngredient("ingotDawnstone"), "dawnstone");
		metalTextures.put(new OreIngredient("ingotAluminum"), "aluminum");
		metalTextures.put(new OreIngredient("ingotAluminium"), "aluminum");
		metalTextures.put(new OreIngredient("ingotBronze"), "bronze");
		metalTextures.put(new OreIngredient("ingotElectrum"), "electrum");
		metalTextures.put(new OreIngredient("ingotNickel"), "nickel");
		metalTextures.put(new OreIngredient("ingotTin"), "tin");
		metalTextures.put(new OreIngredient("ingotAntimony"), "antimony");
		metalTextures.put(new OreIngredient("ingotBrass"), "brass");
		metalTextures.put(new OreIngredient("ingotThaumium"), "thaumium");
		metalTextures.put(new OreIngredient("ingotVoid"), "voidmetal");
		metalTextures.put(new OreIngredient("ingotManasteel"), "manasteel");
		metalTextures.put(new OreIngredient("ingotTerrasteel"), "terrasteel");
		metalTextures.put(new OreIngredient("ingotElvenElementium"), "elementium");
		metalTextures.put(new OreIngredient("ingotCrystalMatrix"), "crystalmatrix");
		metalTextures.put(new OreIngredient("ingotCosmicNeutronium"), "neutronium");
		metalTextures.put(new OreIngredient("ingotInfinity"), "infinity");
	}

	public static boolean hasMetalTexture(ItemStack metal) {
		for (Ingredient ingredient : metalTextures.keySet()) {
			if (ingredient.apply(metal))
				return true;
		}
		return false;
	}

	public static String getMetalTexture(ItemStack metal) {
		for (Ingredient ingredient : metalTextures.keySet()) {
			if (ingredient.apply(metal))
				return metalTextures.get(ingredient);
		}
		return "iron";
	}

	public static boolean hasAttachmentBehavior(ItemStack metal) {
		for (Ingredient ingredient : attachmentBehaviors.keySet()) {
			if (ingredient.apply(metal))
				return true;
		}
		return false;
	}

	public static IAttachmentBehavior getAttachmentBehavior(ItemStack metal) {
		for (Ingredient ingredient : attachmentBehaviors.keySet()) {
			if (ingredient.apply(metal))
				return attachmentBehaviors.get(ingredient);
		}
		return null;
	}

	public BlockTurret() {
		super(Material.IRON);
		setHardness(2.0F);
		setSoundType(SoundType.METAL);
		setUnlocalizedName("mechanical_turret");
		setRegistryName(new ResourceLocation(MysticalGears.MODID, "mechanical_turret"));
		setCreativeTab(MysticalMechanics.creativeTab);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) == null || !(worldIn.getTileEntity(pos) instanceof TileEntityTurret))
			return false;
		TileEntityTurret turretEntity = ((TileEntityTurret) worldIn.getTileEntity(pos));
		if (hasAttachmentBehavior(playerIn.getHeldItem(hand))) {
			ItemStack copyStack = playerIn.getHeldItem(hand).copy();
			copyStack.setCount(1);
			playerIn.getHeldItem(hand).shrink(1);
			if (playerIn.getHeldItem(hand).isEmpty())
				playerIn.setHeldItem(hand, ItemStack.EMPTY);
			if (!worldIn.isRemote)
				worldIn.spawnEntity(new EntityItem(worldIn, playerIn.posX, playerIn.posY + playerIn.height / 2.0f, playerIn.posZ, turretEntity.attachment));
			turretEntity.setAttachment(copyStack);
			turretEntity.markDirty();
			worldIn.playSound(null, pos, MysticalMechanicsAPI.GEAR_ADD, SoundCategory.BLOCKS, 1.0f, 1.0f);
			return true;
		}
		if (MysticalMechanicsAPI.IMPL.isValidGear(playerIn.getHeldItem(hand))) {
			turretEntity.gears = playerIn.getHeldItem(hand).copy();
			turretEntity.markDirty();
			worldIn.playSound(null, pos, MysticalMechanicsAPI.GEAR_ADD, SoundCategory.BLOCKS, 1.0f, 1.0f);
			return true;
		}
		if (hasMetalTexture(playerIn.getHeldItem(hand))) {
			if (facing.equals(EnumFacing.UP))
				turretEntity.setExtraMetal(playerIn.getHeldItem(hand));
			else
				turretEntity.setBaseMetal(playerIn.getHeldItem(hand));
			turretEntity.markDirty();
			worldIn.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
			return true;
		}
		if (!playerIn.isSneaking() && turretEntity.getEntity() != null) {
			playerIn.startRiding(turretEntity.getEntity());
			worldIn.updateComparatorOutputLevel(pos, this);
			return true;
		}
		if (playerIn.getHeldItem(hand).isEmpty()) {
			if (!worldIn.isRemote)
				worldIn.spawnEntity(new EntityItem(worldIn, playerIn.posX, playerIn.posY + playerIn.height / 2.0f, playerIn.posZ, turretEntity.attachment));
			turretEntity.setAttachment(ItemStack.EMPTY);
			turretEntity.markDirty();
			worldIn.playSound(null, pos, MysticalMechanicsAPI.GEAR_ADD, SoundCategory.BLOCKS, 1.0f, 1.0f);
		}
		return true;
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
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityTurret();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityTurret)
			((TileEntityTurret)worldIn.getTileEntity(pos)).breakBlock(worldIn, pos, state, null);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		if ((TileEntityTurret) worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityTurret && ((TileEntityTurret) worldIn.getTileEntity(pos)).getEntity() != null && ((TileEntityTurret) worldIn.getTileEntity(pos)).getEntity().isBeingRidden())
			return 16;
		return 0;
	}
}
