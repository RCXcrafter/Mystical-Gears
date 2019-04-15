package com.rcx.mystgears;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import teamroots.embers.ConfigManager;

import java.io.File;

public class ConfigHandler {

	public static Configuration config;

	// Categories
	public static String general = "General settings";
	public static String gears = "Gears";
	public static String compat = "Compatibility";
	public static String misc = "Miscellaneous";

	// Options
	public static Boolean smoke = true;
	public static Boolean tooltips = true;

	public static Boolean embers = true;
	public static Boolean soot = true;
	public static Boolean avaritia = true;
	public static Boolean botania = true;
	public static Boolean thaumcraft = true;

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
	public static Boolean crystalmatrix = true;
	public static Boolean neutronium = true;
	public static Boolean infinity = true;
	public static Boolean manasteel = true;
	public static Boolean terrasteel = true;
	public static Boolean elementium = true;
	public static Boolean brass = true;
	public static Boolean thaumium = true;
	public static Boolean voidmetal = true;

	public static Boolean tab = true;
	public static Boolean dynamo = true;
	public static Boolean gearanium = true;
	public static Boolean bellows = true;
	public static Boolean visMotor = true;

	public static String[] gearStats;
	private static String[] gearStatsDefaults = {
			"Wood:60:0.6",
			"Stone:50:0.7",
			"Iron:80:1",
			"Gold:320:1",
			"Diamond:580:1",
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
			"CrystalMatrix:0:1",
			"CosmicNeutronium:1000:1.1",
			"Infinity:0:4",
			"Manasteel:120:1",
			"Terrasteel:740:1",
			"ElvenElementium:620:1",
			"Brass:85:1",
			"Thaumium:160:1",
			"Void:70:1.1",
			"Emerald:600:1",
			"Obsidian:580:0.9",
			"Platinum:620:1",
			"Iridium:710:1",
			"Mithril:560:1",
			"Steel:340:1",
			"Invar:240:1",
			"Constantan:210:1",
			"Signalum:420:1",
			"Lumium:420:1",
			"Enderium:740:1",
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

		avaritia = Loader.isModLoaded("avaritia") && config.getBoolean("avaritia", compat, avaritia, "Whether compatibility for Avaritia should be loaded.");

		botania = Loader.isModLoaded("botania") && config.getBoolean("botania", compat, botania, "Whether compatibility for Botania should be loaded.");

		thaumcraft = Loader.isModLoaded("thaumcraft") && config.getBoolean("thaumcraft", compat, thaumcraft, "Whether compatibility for Thaumcraft should be loaded.");
		
		config.setCategoryComment(misc, "Not gear features that can also be disabled");

		tab = config.getBoolean("tab", misc, tab, "Enable/disable the Mystical Mechanics creative tab.");

		dynamo = config.getBoolean("dynamo", misc, dynamo, "Enable/disable the redstone dynamo.");

		gearanium = botania && config.getBoolean("gearanium", misc, gearanium, "Enable/disable the gearanium flower.");

		bellows = botania && config.getBoolean("bellows", misc, bellows, "Enable/disable the mechanical bellows.");

		visMotor = thaumcraft && config.getBoolean("visMotor", misc, visMotor, "Enable/disable the vis motor.");
		
		config.setCategoryComment(gears, "Settings to disable specific gears added by this mod.");

		wood = config.getBoolean("wood", gears, wood, "Enable/disable the wooden gear.");
		stone = config.getBoolean("stone", gears, stone, "Enable/disable the stone gear.");
		diamond = config.getBoolean("diamond", gears, diamond, "Enable/disable the diamond gear.");
		if (embers) {
			lead = config.getBoolean("lead", gears, lead, "Enable/disable the lead gear.");
			copper = config.getBoolean("copper", gears, copper, "Enable/disable the copper gear.");
			aluminium = ConfigManager.enableAluminum && config.getBoolean("aluminium", gears, aluminium, "Enable/disable the aluminium gear.");
			tin = ConfigManager.enableTin && config.getBoolean("tin", gears, tin, "Enable/disable the tin gear.");
			bronze = ConfigManager.enableBronze && config.getBoolean("bronze", gears, bronze, "Enable/disable the bronze gear.");
			nickel = ConfigManager.enableNickel && config.getBoolean("nickel", gears, nickel, "Enable/disable the nickel gear.");
			silver = config.getBoolean("silver", gears, silver, "Enable/disable the silver gear.");
			electrum = ConfigManager.enableElectrum && config.getBoolean("electrum", gears, electrum, "Enable/disable the electrum gear.");
		}
		if (soot)
			antimony = config.getBoolean("antimony", gears, antimony, "Enable/disable the antimony gear.");
		if (avaritia) {
			crystalmatrix = config.getBoolean("crystalmatrix", gears, crystalmatrix, "Enable/disable the crystal matrix gear.");
			neutronium = config.getBoolean("neutronium", gears, neutronium, "Enable/disable the neutronium gear.");
			infinity = config.getBoolean("infinity", gears, infinity, "Enable/disable the infinity gear.");
		}
		if (botania) {
			manasteel = config.getBoolean("manasteel", gears, manasteel, "Enable/disable the manasteel gear.");
			terrasteel = config.getBoolean("terrasteel", gears, terrasteel, "Enable/disable the terrasteel gear.");
			elementium = config.getBoolean("elementium", gears, elementium, "Enable/disable the elementium gear.");
		}
		if (thaumcraft) {
			brass = !Loader.isModLoaded("thaumicperiphery") && config.getBoolean("brass", gears, brass, "Enable/disable the brass gear.");
			thaumium = config.getBoolean("thaumium", gears, thaumium, "Enable/disable the thaumium gear.");
			voidmetal = config.getBoolean("voidmetal", gears, voidmetal, "Enable/disable the void metal gear.");
		}

		if(config.hasChanged())
			config.save();
	}
}
