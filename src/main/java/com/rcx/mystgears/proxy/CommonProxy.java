package com.rcx.mystgears.proxy;

import java.util.Random;

import javax.annotation.Nullable;

import com.rcx.mystgears.BotaniaCompat;
import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.block.BlockRedstoneDynamo;
import com.rcx.mystgears.block.TileEntityRedstoneDynamo;
import com.rcx.mystgears.GearBehaviorRegular;
import com.rcx.mystgears.item.ItemGear;
import com.rcx.mystgears.item.ItemGearAvaritia;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import teamroots.embers.ConfigManager;
import teamroots.embers.RegistryManager;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.Botania;

public class CommonProxy {

	static Random random = new Random();
	public static ItemBlock dynamo;

	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event.getSuggestedConfigurationFile());

		if (ConfigHandler.tab)
			MysticalGears.tab = new CreativeTabs("mystmech") {
				@Override
				public ItemStack getTabIconItem() {
					return new ItemStack(RegistryHandler.GOLD_GEAR);
				}
			};
		else
			MysticalGears.tab = CreativeTabs.REDSTONE;

		if (ConfigHandler.wood) MysticalGears.items.add(new ItemGear("Wood") {
			public void registerRecipe() {
				GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "INI", " I ", 'I', "plankWood", 'N', "stickWood"});
			}
		});
		if (ConfigHandler.stone) MysticalGears.items.add(new ItemGear("Stone") {
			public void registerRecipe() {
				GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "INI", " I ", 'I', "cobblestone", 'N', "stickWood"});
			}
		});
		if (ConfigHandler.diamond) MysticalGears.items.add(new ItemGear("Diamond") {
			public void registerRecipe() {
				String nugget = OreDictionary.doesOreNameExist("nuggetDiamond") ? "nuggetDiamond" : "ingotGold";
				GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "INI", " I ", 'I', "gemDiamond", 'N', nugget});
				if (ConfigHandler.embers && FluidRegistry.isFluidRegistered(name.toLowerCase())) {
					RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack(name.toLowerCase(), ConfigManager.stampGearAmount * RecipeRegistry.INGOT_AMOUNT), Ingredient.fromItem(RegistryManager.stamp_gear), new ItemStack(this, 1)));
				}
			}
		});
		if (ConfigHandler.embers) {
			if (ConfigHandler.lead) MysticalGears.items.add(new ItemGear("Lead"));
			if (ConfigHandler.copper) MysticalGears.items.add(new ItemGear("Copper"));
			if (ConfigHandler.aluminium) MysticalGears.items.add(new ItemGear("Aluminium") { //alumillium
				public void registerRecipe() {
					super.registerRecipe();
					GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "INI", " I ", 'I', "ingotAluminum", 'N', "nuggetAluminum"});
					if (ConfigHandler.embers && FluidRegistry.isFluidRegistered("aluminum")) {
						RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("aluminum", ConfigManager.stampGearAmount * RecipeRegistry.INGOT_AMOUNT), Ingredient.fromItem(RegistryManager.stamp_gear), new ItemStack(this, 1)));
					}
				}
				public void registerGear() {
					super.registerGear();
					if (behavior != null)
						MysticalMechanicsAPI.IMPL.registerGear(this.getRegistryName(), new OreIngredient("gearAluminum"), behavior);
				}
				public void registerOredict() {
					super.registerOredict();
					OreDictionary.registerOre("gearAluminum", new ItemStack(this));
				}
			});
			if (ConfigHandler.tin) MysticalGears.items.add(new ItemGear("Tin"));
			if (ConfigHandler.bronze) MysticalGears.items.add(new ItemGear("Bronze"));
			if (ConfigHandler.nickel) MysticalGears.items.add(new ItemGear("Nickel"));
			if (ConfigHandler.silver) MysticalGears.items.add(new ItemGear("Silver"));
			if (ConfigHandler.electrum) MysticalGears.items.add(new ItemGear("Electrum"));

		}
		if (ConfigHandler.soot && ConfigHandler.antimony) MysticalGears.items.add(new ItemGear("Antimony", new GearBehaviorRegular(0, 1) {
			@Override
			public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
				super.visualUpdate(tile, facing, gear);
				if(facing != null && tile.getWorld().isRemote && tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing)) {
					IMechCapability capability = tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing);
					double power = capability.getPower(facing);
					int particles = Math.min((int)Math.ceil(power / (maxPower / 3)), 5);
					if(power >= maxPower / 3)
						for(int i = 0; i < particles; i++) {
							float xOff = 0.1f + random.nextFloat() * 0.8f;
							float yOff = 0.1f + random.nextFloat() * 0.8f;
							float zOff = 0.1f + random.nextFloat() * 0.8f;
							switch (facing.getAxis()) {
							case X:
								xOff = 0.5f + facing.getFrontOffsetX() / 2.0f; break;
							case Y:
								yOff = 0.5f + facing.getFrontOffsetY() / 2.0f; break;
							case Z:
								zOff = 0.5f + facing.getFrontOffsetZ() / 2.0f; break;
							}
							ParticleUtil.spawnParticleGlow(tile.getWorld(), tile.getPos().getX() + xOff, tile.getPos().getY() + yOff, tile.getPos().getZ() + zOff, 0, 0, 0, 163, 255, 16, 2.0f, 24);
						}
				}
			}
		}) {
			public void registerRecipe() {
				//when you add a nugget stamp but don't add a nugget for your new ingot
				String nugget = OreDictionary.doesOreNameExist("nuggetAntimony") ? "nuggetAntimony" : "nuggetDawnstone";
				GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "INI", " I ", 'I', "ingot" + name, 'N', nugget});
				if (FluidRegistry.isFluidRegistered(name.toLowerCase())) {
					RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack(name.toLowerCase(), ConfigManager.stampGearAmount * RecipeRegistry.INGOT_AMOUNT), Ingredient.fromItem(RegistryManager.stamp_gear), new ItemStack(this, 1)));
				}
			}
		});
		if (ConfigHandler.avaritia) {
			if (ConfigHandler.crystalmatrix) MysticalGears.items.add(new ItemGear("CrystalMatrix") {
				public void registerRecipe() {
					String nugget = OreDictionary.doesOreNameExist("nuggetCrystalMatrix") ? "nuggetCrystalMatrix" : "gemDiamond";
					GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "INI", " I ", 'I', "ingot" + name, 'N', nugget});
					if (ConfigHandler.embers && FluidRegistry.isFluidRegistered(name.toLowerCase())) {
						RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack(name.toLowerCase(), ConfigManager.stampGearAmount * RecipeRegistry.INGOT_AMOUNT), Ingredient.fromItem(RegistryManager.stamp_gear), new ItemStack(this, 1)));
					}
				}
			});
			if (ConfigHandler.neutronium) MysticalGears.items.add(new ItemGearAvaritia("CosmicNeutronium", 0x99FFFFFF, 8, false, true, false));
			if (ConfigHandler.infinity) MysticalGears.items.add(new ItemGearAvaritia("Infinity", 0xFF000000, 10, true, true, true) {
				public void registerRecipe() {
					String nugget = OreDictionary.doesOreNameExist("nuggetInfinity") ? "nuggetInfinity" : "nuggetCosmicNeutronium";
					GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_gear_" + name.toLowerCase()), group, new ItemStack(this), new Object[]{" I ", "INI", " I ", 'I', "ingot" + name, 'N', nugget});
					if (ConfigHandler.embers && FluidRegistry.isFluidRegistered(name.toLowerCase())) {
						RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack(name.toLowerCase(), ConfigManager.stampGearAmount * RecipeRegistry.INGOT_AMOUNT), Ingredient.fromItem(RegistryManager.stamp_gear), new ItemStack(this, 1)));
					}
				}
			});
		}
		if (ConfigHandler.botania) {
			if (ConfigHandler.manasteel) MysticalGears.items.add(new ItemGear("Manasteel", new GearBehaviorRegular(0, 1) {
				@Override
				public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
					super.visualUpdate(tile, facing, gear);
					if(facing != null && tile.getWorld().isRemote && tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing)) {
						IMechCapability capability = tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing);
						double power = capability.getPower(facing);
						int particles = Math.min((int)Math.ceil(power / (maxPower / 3)), 5);
						if(power >= maxPower / 3)
							for(int i = 0; i < particles; i++) {
								float xOff = 0.1f + random.nextFloat() * 0.8f;
								float yOff = 0.1f + random.nextFloat() * 0.8f;
								float zOff = 0.1f + random.nextFloat() * 0.8f;
								switch (facing.getAxis()) {
								case X:
									xOff = 0.5f + facing.getFrontOffsetX() / 2.0f; break;
								case Y:
									yOff = 0.5f + facing.getFrontOffsetY() / 2.0f; break;
								case Z:
									zOff = 0.5f + facing.getFrontOffsetZ() / 2.0f; break;
								}
								Botania.proxy.wispFX(tile.getPos().getX() + xOff, tile.getPos().getY() + yOff, tile.getPos().getZ() + zOff, 0 / 255F, 234 / 255F, 255 / 255F, 0.15F, 0);
							}
					}
				}
			}) {
				public void registerRecipe() {
					super.registerRecipe();
					BotaniaAPI.registerManaInfusionRecipe(new ItemStack(this, 1), "gearIron", 12333);
				}
			});
			if (ConfigHandler.terrasteel) MysticalGears.items.add(new ItemGear("Terrasteel", new GearBehaviorRegular(0, 1) {
				@Override
				public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
					super.visualUpdate(tile, facing, gear);
					if(facing != null && tile.getWorld().isRemote && tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing)) {
						IMechCapability capability = tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing);
						double power = capability.getPower(facing);
						int particles = Math.min((int)Math.ceil(power / (maxPower / 3)), 5);
						if(power >= maxPower / 3)
							for(int i = 0; i < particles; i++) {
								float xOff = 0.1f + random.nextFloat() * 0.8f;
								float yOff = 0.1f + random.nextFloat() * 0.8f;
								float zOff = 0.1f + random.nextFloat() * 0.8f;
								switch (facing.getAxis()) {
								case X:
									xOff = 0.5f + facing.getFrontOffsetX() / 2.0f; break;
								case Y:
									yOff = 0.5f + facing.getFrontOffsetY() / 2.0f; break;
								case Z:
									zOff = 0.5f + facing.getFrontOffsetZ() / 2.0f; break;
								}
								Botania.proxy.wispFX(tile.getPos().getX() + xOff, tile.getPos().getY() + yOff, tile.getPos().getZ() + zOff, 32 / 255F, 255 / 255F, 32 / 255F, 0.15F, 0);
							}
					}
				}
			}));
			if (ConfigHandler.elementium) MysticalGears.items.add(new ItemGear("ElvenElementium", new GearBehaviorRegular(0, 1) {
				@Override
				public void visualUpdate(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear) {
					super.visualUpdate(tile, facing, gear);
					if(facing != null && tile.getWorld().isRemote && tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing)) {
						IMechCapability capability = tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing);
						double power = capability.getPower(facing);
						int particles = Math.min((int)Math.ceil(power / (maxPower / 3)), 5);
						if(power >= maxPower / 3)
							for(int i = 0; i < particles; i++) {
								float xOff = 0.1f + random.nextFloat() * 0.8f;
								float yOff = 0.1f + random.nextFloat() * 0.8f;
								float zOff = 0.1f + random.nextFloat() * 0.8f;
								switch (facing.getAxis()) {
								case X:
									xOff = 0.5f + facing.getFrontOffsetX() / 2.0f; break;
								case Y:
									yOff = 0.5f + facing.getFrontOffsetY() / 2.0f; break;
								case Z:
									zOff = 0.5f + facing.getFrontOffsetZ() / 2.0f; break;
								}
								Botania.proxy.wispFX(tile.getPos().getX() + xOff, tile.getPos().getY() + yOff, tile.getPos().getZ() + zOff, 255 / 255F, 33 / 255F, 158 / 255F, 0.15F, 0);
							}
					}
				}
			}) {
				public void registerRecipe() {
					super.registerRecipe();
					BotaniaAPI.registerElvenTradeRecipe(new ItemStack(this, 1), "gearManasteel", "gearManasteel");
				}
			});
			BotaniaCompat.preInit();
		}
		if (ConfigHandler.thaumcraft) {
			if (ConfigHandler.brass) MysticalGears.items.add(new ItemGear("Brass"));
			if (ConfigHandler.thaumium) MysticalGears.items.add(new ItemGear("Thaumium"));
			if (ConfigHandler.voidmetal) MysticalGears.items.add(new ItemGear("Void"));
		}
		if (ConfigHandler.dynamo) {
			dynamo = new ItemBlock(new BlockRedstoneDynamo());
			MysticalGears.blocks.add((ItemBlock) dynamo.setRegistryName(dynamo.getBlock().getRegistryName()));
		}
	}

	public void init(FMLInitializationEvent event) {
		for (ItemGear item : MysticalGears.items) {
			item.registerRecipe();
			item.registerGear();
		}

		for (String name : OreDictionary.getOreNames()) {
			if (name.startsWith("gear")) {
				for (String entry : ConfigHandler.gearStats) {
					String[] entries = entry.split(":");
					if (name.equals("gear" + entries[0])) {
						ResourceLocation oldLocation = null;
						IGearBehavior oldBehavior = null;
						for (ItemStack stack : OreDictionary.getOres(name)) {
							IGearBehavior currentBehavior1 = MysticalMechanicsAPI.IMPL.getGearBehavior(stack);
							if (currentBehavior1 != IGearBehavior.NO_BEHAVIOR) {
								for (ResourceLocation currentlocation : MysticalMechanicsAPI.IMPL.getGearKeys()) {
									IGearBehavior currentBehavior2 = MysticalMechanicsAPI.IMPL.getGearBehavior(currentlocation);
									if(currentBehavior1.equals(currentBehavior2)) {
										oldLocation = currentlocation;
										break;
									}
								}
								if(oldLocation != null) {
									oldBehavior = currentBehavior1;
									break;
								}
							}
						}
						if(oldLocation != null) {
							if (oldBehavior instanceof GearBehaviorRegular) {
								((GearBehaviorRegular) oldBehavior).maxPower = Double.parseDouble(entries[1]);
								((GearBehaviorRegular) oldBehavior).powerTransfer = Double.parseDouble(entries[2]);
							} else {
								MysticalMechanicsAPI.IMPL.unregisterGear(oldLocation);
								MysticalMechanicsAPI.IMPL.registerGear(oldLocation, new OreIngredient(name), new GearBehaviorRegular(Double.parseDouble(entries[1]), Double.parseDouble(entries[2])).setBase(oldBehavior));
							}
						} else
							MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation("ore", name), new OreIngredient(name), new GearBehaviorRegular(Double.parseDouble(entries[1]), Double.parseDouble(entries[2])));
					}
				}
			}
		}

		MysticalMechanicsAPI.IMPL.unregisterGear(new ResourceLocation("mysticalmechanics", "gear_gold_on"));
		MysticalMechanicsAPI.IMPL.unregisterGear(new ResourceLocation("mysticalmechanics", "gear_gold_off"));

		IGearBehavior goldBehavior = MysticalMechanicsAPI.IMPL.getGearBehavior(new ResourceLocation("mysticalmechanics", "gear_gold"));
		Double goldMax = 320D;
		Double goldTransfer = 1D;
		if (goldBehavior instanceof GearBehaviorRegular) {
			goldMax = ((GearBehaviorRegular) goldBehavior).maxPower;
			goldTransfer = ((GearBehaviorRegular) goldBehavior).powerTransfer;
		}

		MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation("mysticalmechanics", "gear_gold_on"), Ingredient.fromItem(Item.REGISTRY.getObject(new ResourceLocation("mysticalmechanics", "gear_gold_on"))), new GearBehaviorRegular(goldMax, goldTransfer) {
			@Override
			public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
				boolean powered = tile.getWorld().isBlockPowered(tile.getPos());
				return !powered ? super.transformPower(tile, facing, gear, power) : 0;
			}
		});
		MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation("mysticalmechanics", "gear_gold_off"), Ingredient.fromItem(Item.REGISTRY.getObject(new ResourceLocation("mysticalmechanics", "gear_gold_off"))), new GearBehaviorRegular(goldMax, goldTransfer) {
			@Override
			public double transformPower(TileEntity tile, @Nullable EnumFacing facing, ItemStack gear, double power) {
				boolean powered = tile.getWorld().isBlockPowered(tile.getPos());
				return powered ? super.transformPower(tile, facing, gear, power) : 0;
			}
		});

		if (ConfigHandler.tab) {
			RegistryHandler.CREATIVE_MECH_SOURCE.setCreativeTab(MysticalGears.tab);
			RegistryHandler.GEARBOX_FRAME.setCreativeTab(MysticalGears.tab);
			RegistryHandler.GOLD_GEAR.setCreativeTab(MysticalGears.tab);
			RegistryHandler.GOLD_GEAR_OFF.setCreativeTab(MysticalGears.tab);
			RegistryHandler.GOLD_GEAR_ON.setCreativeTab(MysticalGears.tab);
			RegistryHandler.IRON_AXLE.setCreativeTab(MysticalGears.tab);
			RegistryHandler.IRON_GEAR.setCreativeTab(MysticalGears.tab);
			RegistryHandler.MERGEBOX_FRAME.setCreativeTab(MysticalGears.tab);
		}

		if (ConfigHandler.botania)
			BotaniaCompat.init();

		if (ConfigHandler.dynamo) {
			GameRegistry.registerTileEntity(TileEntityRedstoneDynamo.class, new ResourceLocation(MysticalGears.MODID, "redstone_dynamo"));
			GameRegistry.addShapedRecipe(new ResourceLocation(MysticalGears.MODID, "recipe_redstone_dynamo"), ItemGear.group, new ItemStack(dynamo), new Object[]{"INI", "AGR", "INI", 'R', "blockRedstone", 'I', "ingotIron", 'N', "nuggetGold", 'A', new ItemStack(RegistryHandler.IRON_AXLE), 'G', new ItemStack(RegistryHandler.GOLD_GEAR_OFF)});
		}
	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public void registerBlocks(RegistryEvent.Register<Block> event) {
		for (ItemBlock block : MysticalGears.blocks) {
			event.getRegistry().register(block.getBlock());
		}
	}

	public void registerItems(RegistryEvent.Register<Item> event) {
		for (ItemGear item : MysticalGears.items) {
			event.getRegistry().register(item);
			item.registerOredict();
		}
		for (ItemBlock block : MysticalGears.blocks) {
			event.getRegistry().register(block);
		}
	}
}
