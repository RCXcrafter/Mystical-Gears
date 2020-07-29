package com.rcx.mystgears.block;

import java.util.UUID;

import javax.annotation.Nullable;

import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.entity.EntityTurret;
import com.rcx.mystgears.util.IAttachmentBehavior;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.IGearData;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityTurret extends TileEntity implements ITickable {

	public DefaultMechCapability mechCapability = new DefaultMechCapability() {
		@Override
		public void setPower(double value, EnumFacing from) {
			if (from != null && !from.equals(EnumFacing.DOWN))
				return;
			super.setPower(value, from);
			if (value != getPower(null)) {
				onPowerChange();
			}
		}

		@Override
		public double getPower(EnumFacing from) {
			if (from == null || from.equals(EnumFacing.DOWN))
				return super.getPower(from);
			return 0;
		}

		@Override
		public void onPowerChange() {
			TileEntityTurret source = TileEntityTurret.this;
			source.updateNeighbors();
			source.markDirty();
		}

		@Override
		public boolean isInput(EnumFacing from) {
			return from.equals(EnumFacing.DOWN);
		}

		@Override
		public boolean isOutput(EnumFacing from) {
			return false;
		}
	};

	public UUID entityUUID = null;
	public Integer entityID = null;
	public double currentPower = 0;
	public double angle = 0;
	public double lastAngle = 0;

	public ItemStack attachment = ItemStack.EMPTY;
	public ItemStack gears = new ItemStack(RegistryHandler.GOLD_GEAR);
	public ItemStack baseMetal = new ItemStack(Items.IRON_INGOT);
	public ItemStack extraMetal = new ItemStack(Items.GOLD_INGOT);
	public IAttachmentBehavior behavior = null;
	public IGearData attachmentData = null;
	public ResourceLocation baseMetalTexture = new ResourceLocation(MysticalGears.MODID, "textures/model/turret_base_iron.png");
	public ResourceLocation extraMetalTexture = new ResourceLocation(MysticalGears.MODID, "textures/model/turret_overlay_gold.png");

	public void setAttachment(ItemStack item) {
		attachment = item;
		behavior = BlockTurret.getAttachmentBehavior(item);
		if (behavior != null) {
			if (behavior.hasData())
				attachmentData = behavior.createData();
		} else {
			attachmentData = null;
		}
	}

	public void setBaseMetal(ItemStack metal) {
		baseMetal = metal.copy();
		baseMetalTexture = new ResourceLocation(MysticalGears.MODID, "textures/model/turret_base_" + BlockTurret.getMetalTexture(metal) + ".png");
	}

	public void setExtraMetal(ItemStack metal) {
		extraMetal = metal.copy();
		extraMetalTexture = new ResourceLocation(MysticalGears.MODID, "textures/model/turret_overlay_" + BlockTurret.getMetalTexture(metal) + ".png");
	}

	@Override
	public void onLoad() {
		super.onLoad();
		getEntity();
		syncToClient();
		updateNeighbors();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		mechCapability.writeToNBT(tag);
		tag.setUniqueId("entity", entityUUID);
		tag.setTag("attachment", attachment.serializeNBT());
		tag.setTag("gearItem", gears.serializeNBT());
		tag.setTag("baseMetalItem", baseMetal.serializeNBT());
		tag.setTag("extraMetalItem", extraMetal.serializeNBT());
		if (attachmentData != null)
			tag.setTag("attachmentData", attachmentData.writeToNBT(new NBTTagCompound()));
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		mechCapability.readFromNBT(tag);
		if (tag.hasUniqueId("entity")) {
			entityUUID = tag.getUniqueId("entity");
		} else {
			entityUUID = null;
		}
		if (tag.hasKey("attachment"))
			setAttachment(new ItemStack(tag.getCompoundTag("attachment")));
		if (tag.hasKey("gearItem"))
			gears = new ItemStack(tag.getCompoundTag("gearItem"));
		if (tag.hasKey("baseMetalItem"))
			setBaseMetal(new ItemStack(tag.getCompoundTag("baseMetalItem")));
		if (tag.hasKey("extraMetalItem"))
			setExtraMetal(new ItemStack(tag.getCompoundTag("extraMetalItem")));
		if (behavior != null && behavior.hasData() && tag.hasKey("attachmentData")) {
			attachmentData.readFromNBT(tag.getCompoundTag("attachmentData"));
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (facing == null || facing.equals(EnumFacing.DOWN))) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (facing == null || facing.equals(EnumFacing.DOWN))) {
			T result = (T) this.mechCapability;
			return result;
		}
		return super.getCapability(capability, facing);
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		mechCapability.setPower(0f, null);
		updateNeighbors();
		if (getEntity() != null)
			getEntity().setDead();
		if (!attachment.isEmpty())
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), attachment);
	}

	public void updateNeighbors() {
		TileEntity t = world.getTileEntity(getPos().down());
		if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, EnumFacing.UP))
			mechCapability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, EnumFacing.UP).getPower(EnumFacing.UP), EnumFacing.DOWN);
		else
			mechCapability.setPower(0, EnumFacing.DOWN);

		markDirty();
	}

	@Override
	public void markDirty() {
		this.syncToClient();
	}

	@Override
	public void update() {
		if (mechCapability.getPower(null) != currentPower) {
			currentPower = mechCapability.getPower(null);
			markDirty();
		}
		lastAngle = angle;
		angle += currentPower;
		if (getEntity() != null && behavior != null) {
			behavior.tick(this.world, getEntity().getPositionEyes(0).add(getEntity().getLookVec().scale(1.5)), getEntity().getLookVec(), attachment, attachmentData, currentPower);
		}
	}

	public Entity getEntity() {
		if (entityID == null) {
			if(entityUUID == null) {
				return createEntity();
			} else {
				for (Entity entity : world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(-5, -5, -5), pos.add(5, 5, 5)))) {
					System.out.println(entity.getUniqueID());
					if (entity.getUniqueID().equals(entityUUID)) {//fec1c043-5273-47ed-853b-5cee7e1b681b
						entityID = entity.getEntityId();//74ab96bf-cd19-4641-a451-4b9252e4a600
						return entity;
					}
				}
				return createEntity();
			}
		}
		Entity entity = world.getEntityByID(entityID);
		if (entity == null || !(entity instanceof EntityTurret))
			entity = createEntity();
		return entity;
	}

	public Entity createEntity() {
		if (world.isRemote)
			return null;
		Entity turretEntity = new EntityTurret(world, pos);
		world.spawnEntity(turretEntity);
		entityUUID = turretEntity.getUniqueID();
		entityID = turretEntity.getEntityId();
		return turretEntity;
	}

	public void syncToClient() {
		if(world instanceof WorldServer) {
			SPacketUpdateTileEntity packet = this.getUpdatePacket();
			if (packet != null) {
				PlayerChunkMap chunkMap = ((WorldServer) world).getPlayerChunkMap();
				int i = this.getPos().getX() >> 4;
				int j = this.getPos().getZ() >> 4;
				PlayerChunkMapEntry entry = chunkMap.getEntry(i, j);
				if(entry != null) {
					entry.sendPacket(packet);
				}
			}
		}
	}
}
