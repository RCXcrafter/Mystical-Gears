package com.rcx.mystgears.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTurret extends Block {

	public static HashMap<Ingredient, String> metalTextures = new HashMap<Ingredient, String>();
	public static HashMap<Ingredient, IAttachmentBehavior> attachmentBehaviors = new HashMap<Ingredient, IAttachmentBehavior>();

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
		MinecraftForge.EVENT_BUS.register(this);
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
			return true;
		}
		return false;
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

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGameOverlayRender(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.TEXT) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			World world = player.getEntityWorld();
			RayTraceResult result = player.rayTrace(6.0, event.getPartialTicks());
			if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
				IBlockState blockstate = world.getBlockState(result.getBlockPos());
				if (blockstate.getBlock() instanceof BlockTurret) {
					List<String> text = this.getTextOverlay(world, result.getBlockPos(), blockstate, player, result);
					for (int i = 0; i < text.size(); i++) {
						Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text.get(i), event.getResolution().getScaledWidth() / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(text.get(i)) / 2, event.getResolution().getScaledHeight() / 2 + 40 + 11 * i, 0xFFFFFF);
					}
				}
			}
		}
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("minecraft:textures/gui/icons.png"));
		GlStateManager.enableDepth();
	}

	public List<String> getTextOverlay(World world, BlockPos pos, IBlockState state, EntityPlayer player, RayTraceResult result) {
		ArrayList<String> text = new ArrayList<>();
		ItemStack heldItem = player.getHeldItemMainhand();
		if (!heldItem.isEmpty()) {
			boolean attachment = hasAttachmentBehavior(heldItem);
			boolean gear = MysticalMechanicsAPI.IMPL.isValidGear(heldItem);
			boolean skin = hasMetalTexture(heldItem);
			if (attachment || gear || skin) {
				String attachmentText = I18n.format("mystgears.tooltip.turret.add_attachment");
				String gearText = I18n.format("mystgears.tooltip.turret.gears");
				String skinText = I18n.format("mystgears.tooltip.turret.skin_primary");
				String skin2Text = I18n.format("mystgears.tooltip.turret.skin_secondary");

				if (attachment) {
					attachmentText = "§n" + attachmentText;
				} else if (gear) {
					gearText = "§n" + gearText;
				} else if (skin) {
					if (result.sideHit.equals(EnumFacing.UP))
						skin2Text = "§n" + skin2Text;
					else
						skinText = "§n" + skinText;
				}

				text.add(I18n.format("mystgears.tooltip.turret.ride"));
				text.add(attachmentText);
				text.add(I18n.format("mystgears.tooltip.turret.remove_attachment"));
				text.add(gearText);
				text.add(skinText);
				text.add(skin2Text);
			}
		}
		return text;
	}
}
