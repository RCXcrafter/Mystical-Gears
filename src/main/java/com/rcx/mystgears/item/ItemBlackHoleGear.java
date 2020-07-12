package com.rcx.mystgears.item;

import java.util.List;

import javax.annotation.Nullable;

import com.rcx.mystgears.MysticalGears;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IGearData;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlackHoleGear extends ItemGear {

	public ItemBlackHoleGear() {
		super("black_hole");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
	}

	public void registerRecipe() {
		GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" D ", "DPD", " D ", 'D', "dustGlowstone", 'P', new ItemStack(Items.ENDER_PEARL)});
	}

	public void registerGear() {
		MysticalMechanicsAPI.IMPL.registerGear(this.getRegistryName(), Ingredient.fromItem(this), new IGearBehavior() {
			@Override
			public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				return 0;
			}

			@Override
			public double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				return power;
			}

			@Override
			public double transformVisualPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
				return power;
			}

			@Override
			public boolean canTick(ItemStack gear) {
				return true;
			}

			@Override
			public void tick(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, IGearData data, double power) {
				World world = tile.getWorld();
				AxisAlignedBB aabb = new AxisAlignedBB(tile.getPos().offset(facing));
				double distance = getSuckDistance(power);
				double vx = facing.getFrontOffsetX();
				double vy = facing.getFrontOffsetY();
				double vz = facing.getFrontOffsetZ();
				double suckVelocity = getSuckVelocity(power);
				aabb = aabb.expand(vx * distance, vy * distance, vz * distance);

				List<Entity> entities = world.getEntitiesWithinAABB(Entity.class,aabb);
				for(Entity entity : entities) {
					entity.addVelocity(-vx * suckVelocity, -vy * suckVelocity, -vz * suckVelocity);
				}
			}

			private double getSuckVelocity(double power) {
				return power / 800.0;
			}

			private double getSuckDistance(double power) {
				return Math.sqrt(power) / 3.0;
			}
		});
	}

	public void registerOredict() {}

	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
	}
}
