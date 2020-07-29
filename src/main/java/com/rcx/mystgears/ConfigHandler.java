package com.rcx.mystgears;

import java.io.File;

import com.rcx.mystgears.block.TileEntityAuraEngine;
import com.rcx.mystgears.block.TileEntityDrill;
import com.rcx.mystgears.block.TileEntityDrillDiamond;
import com.rcx.mystgears.block.TileEntityEssentiaMotor;
import com.rcx.mystgears.block.TileEntityGatlingGunEmber;
import com.rcx.mystgears.block.TileEntityGearanium;
import com.rcx.mystgears.block.TileEntityRedstoneDynamo;
import com.rcx.mystgears.block.TileEntityVisMotor;
import com.rcx.mystgears.block.TileEntityWindupBox;
import com.rcx.mystgears.compatibility.RustichromiaCompat;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import teamroots.embers.ConfigManager;

public class ConfigHandler {

	public static Configuration config;

	// Categories
	public static String general = "General settings";
	public static String gears = "Gears";
	public static String gearsOverrides = "Gears Overrides";
	public static String compat = "Compatibility";
	public static String machine = "Machines";
	public static String misc = "Miscellaneous";

	// Options
	public static Boolean smoke = true;
	public static Boolean tooltips = true;

	public static Boolean embers = true;
	public static Boolean soot = true;
	public static Boolean aetherworks = true;
	public static Boolean avaritia = true;
	public static Boolean botania = true;
	public static Boolean thaumcraft = true;
	public static Boolean naturesAura = true;
	public static Boolean pyrotech = false;
	public static Boolean rustichromia = true;

	public static Boolean wood = true;
	public static Boolean stone = true;
	public static Boolean diamond = true;
	public static Boolean lead = true;
	public static Boolean copper = true;
	public static Boolean aluminium = true;
	public static Boolean tin = true;
	public static Boolean bronze = true;
	public static Boolean nickel = true;
	public static Boolean silver = true;
	public static Boolean electrum = true;
	public static Boolean antimony = true;
	public static Boolean aetherium = true;
	public static Boolean crystalmatrix = true;
	public static Boolean neutronium = true;
	public static Boolean infinity = true;
	public static Boolean manasteel = true;
	public static Boolean terrasteel = true;
	public static Boolean elementium = true;
	public static Boolean brass = true;
	public static Boolean thaumium = true;
	public static Boolean voidmetal = true;
	public static Boolean infusedIron = true;

	public static Boolean blackHole = true;
	public static Boolean googlyEyes = true;
	public static Boolean flywheel = true;
	public static Boolean gearboxCover = true;
	public static Boolean windupBox = true;
	public static Boolean dynamo = true;
	public static Boolean mechDial = true;
	public static Boolean drill = true;
	public static Boolean turret = true;
	public static Boolean infiniteMechRecipe = true;
	public static Boolean emberGatlingGun = true;
	public static Boolean gearanium = true;
	public static Boolean bellows = true;
	public static Boolean visMotor = true;
	public static Boolean essentiaMotor = true;
	public static Boolean mechanicalCrafter = true;
	public static Boolean auraEngine = true;
	public static Boolean poweredBellows = false;
	
	public static double lightFlywheelAcceleration = 5.0;
	public static double heavyFlywheelAcceleration = 1.0;

	public static String[] gearStats;
	private static String[] gearStatsDefaults = {
			"Wood:60:0.6",
			"Stone:50:0.7",
			"Iron:80:1",
			"Gold:320:1",
			"Diamond:580:1",
			"FlywheelLight:400:1",
			"FlywheelHeavy:600:1",
			"Lead:80:0.8",
			"Copper:65:1",
			"Aluminium:70:1",
			"Tin:85:0.9",
			"Bronze:85:1",
			"Nickel:200:1",
			"Silver:430:0.9",
			"Electrum:430:1",
			"Dawnstone:540:1",
			"Antimony:830:1",
			"Aether:910:1",
			"CrystalMatrix:0:1",
			"CosmicNeutronium:1000:1.02",
			"Infinity:0:2",
			"Manasteel:120:1",
			"Terrasteel:740:1",
			"ElvenElementium:620:1",
			"Brass:85:1",
			"Thaumium:160:1",
			"Void:40:1.01",
			"InfusedIron:120:1.0",
			"Coal:70:0.8",
			"Lapis:110:0.9",
			"Obsidian:580:0.9",
			"Emerald:600:1",
			"Quartz:210:0.9",
			"Prismarine:80:0.9",
			"Amber:100:0.9",
			"Platinum:620:1",
			"Iridium:710:1",
			"Mithril:560:1",
			"Steel:340:1",
			"Invar:240:1",
			"Constantan:210:1",
			"Signalum:420:1",
			"Lumium:420:1",
			"Enderium:740:1",
			"SoulforgedSteel:340:1",
			"Ferramic:160:1",
			"Zorrasteel:450:1",
			"Energion:600:0.9",
			"Alubrass:80:1",
			"Pigiron:190:1",
			"Knightslime:250:1",
			"Ardite:690:0.9",
			"Cobalt:560:1",
			"Manyullyn:690:1",
			"Uranium:238:0.9",
			"HOPGraphite:200:1",
			"IronInfinity:120:1",
			"Energized:410:1",
			"Vibrant:490:1",
			"Dark:500:1",
			"ElectricalSteel:120:1",
			"RedstoneAlloy:220:1",
			"ConductiveIron:280:1",
			"PulsatingIron:350:1",
			"Soularium:410:0.9",
			"EnergeticAlloy:410:1",
			"VibrantAlloy:490:1",
			"DarkSteel:580:1",
			"EndSteel:640:1"
	};

	public static void init(File file) {
		config = new Configuration(file);
		syncConfig();
	}

	public static void syncConfig() {
		config.setCategoryComment(general, "");

		gearStats = config.getStringList("gearStats", general, gearStatsDefaults, "Here you can change or add stats for gears."
				+ "\nThe syntax is: Name:MaxSpeed:SpeedTransfer"
				+ "\nName: The ore dictionary name."
				+ "\nMaxSpeed: The maximum speed for this gear, above this speed, the lost speed increases exponentially."
				+ "\nSpeedTransfer: A multiplier for the amount of power this gear will transfer.");

		smoke = config.getBoolean("smoke", general, smoke, "Whether gears should emit smoke if they spin above their max speed.");

		tooltips = config.getBoolean("tooltips", general, tooltips, "Enable/disable adding tooltips to gears to show their stats.");


		config.setCategoryComment(compat, "settings for compatibility with other mods.");

		embers = Loader.isModLoaded("embers") && config.getBoolean("embers", compat, embers, "Whether compatibility for Embers should be loaded.");

		soot = Loader.isModLoaded("soot") && config.getBoolean("soot", compat, soot, "Whether compatibility for Soot should be loaded.");

		aetherworks = Loader.isModLoaded("aetherworks") && config.getBoolean("aetherworks", compat, soot, "Whether compatibility for Aetherworks should be loaded.");

		avaritia = Loader.isModLoaded("avaritia") && config.getBoolean("avaritia", compat, avaritia, "Whether compatibility for Avaritia should be loaded.");

		botania = Loader.isModLoaded("botania") && config.getBoolean("botania", compat, botania, "Whether compatibility for Botania should be loaded.");

		thaumcraft = Loader.isModLoaded("thaumcraft") && config.getBoolean("thaumcraft", compat, thaumcraft, "Whether compatibility for Thaumcraft should be loaded.");

		naturesAura = Loader.isModLoaded("naturesaura") && config.getBoolean("naturesAura", compat, naturesAura, "Whether compatibility for Nature's Aura should be loaded.");

		//pyrotech = Loader.isModLoaded("pyrotech") && config.getBoolean("pyrotech", compat, pyrotech, "Whether compatibility for Pyrotech should be loaded.");

		rustichromia = Loader.isModLoaded("rustichromia") && config.getBoolean("rustichromia", compat, rustichromia, "Whether compatibility for Heterorustichromia should be loaded.");

		config.setCategoryComment(misc, "Not gear features that can also be disabled");

		blackHole = config.getBoolean("blackHole", misc, blackHole, "Enable/disable the black hole gear.");

		googlyEyes = config.getBoolean("googlyEyes", misc, googlyEyes, "Enable/disable the googly eyes.");

		flywheel = config.getBoolean("flywheel", misc, flywheel, "Enable/disable the flywheel.");

		gearboxCover = config.getBoolean("gearboxCover", misc, gearboxCover, "Enable/disable the black gearbox cover.");

		windupBox = config.getBoolean("windupBox", misc, windupBox, "Enable/disable the windup box.");

		dynamo = config.getBoolean("dynamo", misc, dynamo, "Enable/disable the redstone dynamo.");

		mechDial = config.getBoolean("mechDial", misc, mechDial, "Enable/disable the mechanical dial.");

		drill = config.getBoolean("drill", misc, drill, "Enable/disable the drill.");

		turret = config.getBoolean("turret", misc, turret, "Enable/disable the turret.");

		infiniteMechRecipe = config.getBoolean("infiniteMechRecipe", misc, infiniteMechRecipe, "Enable/disable the the recipe for the creative mechanical source.");

		emberGatlingGun = embers && config.getBoolean("emberGatlingGun", misc, emberGatlingGun, "Enable/disable the ember gatling gun.");

		gearanium = botania && config.getBoolean("gearanium", misc, gearanium, "Enable/disable the gearanium flower.");

		bellows = botania && config.getBoolean("bellows", misc, bellows, "Enable/disable the mechanical bellows.");

		visMotor = thaumcraft && config.getBoolean("visMotor", misc, visMotor, "Enable/disable the vis motor.");

		essentiaMotor = thaumcraft && config.getBoolean("essentiaMotor", misc, essentiaMotor, "Enable/disable the esentia motor.");

		mechanicalCrafter = thaumcraft && config.getBoolean("mechanicalCrafter", misc, mechanicalCrafter, "Enable/disable the mechanical crafter.");

		auraEngine = naturesAura && config.getBoolean("auraEngine", misc, auraEngine, "Enable/disable the aura engine.");

		//poweredBellows = pyrotech && config.getBoolean("poweredBellows", misc, poweredBellows, "Enable/disable the powered bellows.");

		if (rustichromia)
			RustichromiaCompat.stoneDiskLimitMultiplier = config.getFloat("stoneDiskLimitMultiplier", misc, RustichromiaCompat.stoneDiskLimitMultiplier, 0, 1000000, "The maximum amount of power that stone disks can transfer will be the limit of the disk multiplied by this number. Set to -1 to disable");

		config.setCategoryComment(gears, "Settings to disable specific gears added by this mod.");

		wood = config.getBoolean("wood", gears, wood, "Enable/disable the wooden gear.");
		stone = config.getBoolean("stone", gears, stone, "Enable/disable the stone gear.");
		diamond = config.getBoolean("diamond", gears, diamond, "Enable/disable the diamond gear.");

		lead = embers && config.getBoolean("lead", gears, lead, "Enable/disable the lead gear.");
		copper =  embers &&config.getBoolean("copper", gears, copper, "Enable/disable the copper gear.");
		aluminium = embers && ConfigManager.enableAluminum && config.getBoolean("aluminium", gears, aluminium, "Enable/disable the aluminium gear.");
		tin = embers && ConfigManager.enableTin && config.getBoolean("tin", gears, tin, "Enable/disable the tin gear.");
		bronze = embers && ConfigManager.enableBronze && config.getBoolean("bronze", gears, bronze, "Enable/disable the bronze gear.");
		nickel = embers && ConfigManager.enableNickel && config.getBoolean("nickel", gears, nickel, "Enable/disable the nickel gear.");
		silver = embers && config.getBoolean("silver", gears, silver, "Enable/disable the silver gear.");
		electrum = embers && ConfigManager.enableElectrum && config.getBoolean("electrum", gears, electrum, "Enable/disable the electrum gear.");

		antimony = soot && config.getBoolean("antimony", gears, antimony, "Enable/disable the antimony gear.");

		aetherium = aetherworks && config.getBoolean("aetherium", gears, aetherium, "Enable/disable the aetherium gear.");

		crystalmatrix = avaritia && config.getBoolean("crystalmatrix", gears, crystalmatrix, "Enable/disable the crystal matrix gear.");
		neutronium = avaritia && config.getBoolean("neutronium", gears, neutronium, "Enable/disable the neutronium gear.");
		infinity = avaritia && config.getBoolean("infinity", gears, infinity, "Enable/disable the infinity gear.");

		manasteel = botania && config.getBoolean("manasteel", gears, manasteel, "Enable/disable the manasteel gear.");
		terrasteel = botania && config.getBoolean("terrasteel", gears, terrasteel, "Enable/disable the terrasteel gear.");
		elementium = botania && config.getBoolean("elementium", gears, elementium, "Enable/disable the elementium gear.");

		brass = thaumcraft && !Loader.isModLoaded("thaumicperiphery") && config.getBoolean("brass", gears, brass, "Enable/disable the brass gear.");
		thaumium = thaumcraft && config.getBoolean("thaumium", gears, thaumium, "Enable/disable the thaumium gear.");
		voidmetal = thaumcraft && config.getBoolean("voidmetal", gears, voidmetal, "Enable/disable the void metal gear.");

		infusedIron = naturesAura && config.getBoolean("infusedIron", gears, infusedIron, "Enable/disable the infused iron gear.");

		config.setCategoryComment(gearsOverrides, "Settings to force certain gears to be enabled.");

		lead = lead || config.getBoolean("leadOverride", gearsOverrides, false, "Force enable the lead gear.");
		copper = copper || config.getBoolean("copperOverride", gearsOverrides, false, "Force enable the copper gear.");
		aluminium = aluminium || config.getBoolean("aluminiumOverride", gearsOverrides, false, "Force enable the aluminium gear.");
		tin = tin || config.getBoolean("tinOverride", gearsOverrides, false, "Force enable the tin gear.");
		bronze = bronze || config.getBoolean("bronzeOverride", gearsOverrides, false, "Force enable the bronze gear.");
		nickel = nickel || config.getBoolean("nickelOverride", gearsOverrides, false, "Force enable the nickel gear.");
		silver = silver || config.getBoolean("silverOverride", gearsOverrides, false, "Force enable the silver gear.");
		electrum = electrum || config.getBoolean("electrumOverride", gearsOverrides, false, "Force enable the electrum gear.");
		brass = brass || config.getBoolean("brassOverride", gearsOverrides, false, "Force enable the brass gear.");

		config.setCategoryComment(machine, "Settings for tweaking machines added by this mod.");

		if (flywheel) {
			lightFlywheelAcceleration = config.getFloat("lightFlywheelAcceleration", machine, (float) lightFlywheelAcceleration, 0, 100000000, "The rate at which the light flywheel speeds up and slows down.");
			heavyFlywheelAcceleration = config.getFloat("heavyFlywheelAcceleration", machine, (float) heavyFlywheelAcceleration, 0, 100000000, "The rate at which the heavy flywheel speeds up and slows down.");
		}
		if (windupBox) {
			TileEntityWindupBox.maxPower = config.getFloat("windupPowerCapacity", machine, (float) TileEntityWindupBox.maxPower, 0, 100000000, "The amount of power the windup box can store.");
			TileEntityWindupBox.output = config.getFloat("windupOutputPower", machine, (float) TileEntityWindupBox.output, 0, 100000, "The amount of power outputted by the windup box.");
		}
		if (dynamo) {
			TileEntityRedstoneDynamo.FE_CONVERSION_RATE = config.getFloat("dynamoCerversionRate", machine, (float) TileEntityRedstoneDynamo.FE_CONVERSION_RATE, 0, 10, "The conversion rate from mechanical power to FE.");
			TileEntityRedstoneDynamo.MAX_FE = config.getInt("dynamoPowerCapacity", machine, TileEntityRedstoneDynamo.MAX_FE, 0, 100000000, "The amount of power the dynamo can store.");
		}
		if (drill) {
			TileEntityDrill.drillSpeed = config.getFloat("drillSpeed", machine, TileEntityDrill.drillSpeed, 0, 100, "The mining speed of the drill.");
			TileEntityDrill.drillLevel = config.getInt("drillLevel", machine, TileEntityDrill.drillLevel, 0, 100, "The mining level of the drill.");
			TileEntityDrillDiamond.diamondDrillSpeed = config.getFloat("diamondDrillSpeed", machine, TileEntityDrillDiamond.diamondDrillSpeed, 0, 100, "The mining speed of the diamond drill.");
			TileEntityDrillDiamond.diamondDrillLevel = config.getInt("diamondDrillLevel", machine, TileEntityDrillDiamond.diamondDrillLevel, 0, 100, "The mining level of the diamond drill.");
		}
		if (emberGatlingGun) {
			TileEntityGatlingGunEmber.maxPower = config.getFloat("emberGatlingGunMaxPower", machine, (float) TileEntityGatlingGunEmber.maxPower, 0, 100000000, "The amount of power the gatling gun takes to fire at max speed.");
			TileEntityGatlingGunEmber.damage = config.getFloat("emberGatlingGunDamage", machine, TileEntityGatlingGunEmber.damage, 0, 100000, "The amount of damage each bullet does.");
		}
		if (gearanium) {
			TileEntityGearanium.MANA_USAGE = config.getInt("gearaniumManaUsage", machine, TileEntityGearanium.MANA_USAGE, 0, 100, "The amount of mana the gearanium uses per tick.");
			TileEntityGearanium.ROTATION_POWER = config.getInt("gearaniumRotationPower", machine, TileEntityGearanium.ROTATION_POWER, 0, 100000, "The amount of power outputted by the gearanium.");
		}
		if (visMotor) {
			TileEntityVisMotor.maxTime = config.getInt("visMotorTimePerVis", machine, TileEntityVisMotor.maxTime, 0, 1000000, "The amount of ticks the motor runs before consuming one more vis.");
			TileEntityVisMotor.outputPower = config.getFloat("visMotorOutputPower", machine, (float) TileEntityVisMotor.outputPower, 0, 100000, "The amount of power outputted by the motor.");
		}
		if (essentiaMotor) {
			TileEntityEssentiaMotor.maxEssentia = config.getFloat("essentiaMotorEssentiaCapacity", machine, (float) TileEntityEssentiaMotor.maxEssentia, 0, 1000000, "The amount of essentia the motor can store.");
			TileEntityEssentiaMotor.energyOutput = config.getFloat("essentiaMotorPotentiaPower", machine, (float) TileEntityEssentiaMotor.energyOutput, 0, 100000, "The amount of power outputted when running on potentia essentia.");
			TileEntityEssentiaMotor.mechanismOutput = config.getFloat("essentiaMotorMachinaPower", machine, (float) TileEntityEssentiaMotor.mechanismOutput, 0, 100000, "The amount of power outputted when running on machina essentia.");
			TileEntityEssentiaMotor.motionOutput = config.getFloat("essentiaMotorMotusPower", machine, (float) TileEntityEssentiaMotor.motionOutput, 0, 100000, "The amount of power outputted when running on motus essentia.");
			TileEntityEssentiaMotor.energyUsage = config.getFloat("essentiaMotorPotentiaUsage", machine, (float) TileEntityEssentiaMotor.energyUsage, 0, 100, "The amount of potentia essentia consumed per tick.");
			TileEntityEssentiaMotor.mechanismUsage = config.getFloat("essentiaMotorMachinaUsage", machine, (float) TileEntityEssentiaMotor.mechanismUsage, 0, 100, "The amount of machina essentia consumed per tick.");
			TileEntityEssentiaMotor.motionUsage = config.getFloat("essentiaMotorMotusUsage", machine, (float) TileEntityEssentiaMotor.motionUsage, 0, 100, "The amount of motus essentia consumed per tick.");
		}
		if (auraEngine) {
			TileEntityAuraEngine.auraConsumption = config.getInt("auraEngineConsumption", machine, TileEntityAuraEngine.auraConsumption, 0, 1000000, "The amount of aura consumed by the engine each tick.");
			TileEntityAuraEngine.outputPower = config.getFloat("auraEngineOutputPower", machine, (float) TileEntityAuraEngine.outputPower, 0, 100000, "The amount of power outputted by the aura engine.");
		}

		if(config.hasChanged())
			config.save();
	}
}
