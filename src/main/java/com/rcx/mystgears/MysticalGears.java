package com.rcx.mystgears;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.rcx.mystgears.item.ItemGear;
import com.rcx.mystgears.proxy.CommonProxy;

@Mod(modid = MysticalGears.MODID, name = MysticalGears.NAME, version = MysticalGears.VERSION, dependencies = "required-after:mysticalmechanics;after:embers;after:botania;after:rustichromia")
public class MysticalGears {

	@SidedProxy(clientSide = "com.rcx.mystgears.proxy.ClientProxy", serverSide = "com.rcx.mystgears.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static final String MODID = "mystgears";
	public static final String NAME = "Mystical Gears";
	public static final String VERSION = "1.1.5";

	public static List<ItemGear> items = new ArrayList<ItemGear>();
	public static List<ItemBlock> blocks = new ArrayList<ItemBlock>();

	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		MinecraftForge.EVENT_BUS.register(this);
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@SubscribeEvent
	public void blockRegistry(final RegistryEvent.Register<Block> event) {
		proxy.registerBlocks(event);
	}

	@SubscribeEvent
	public void itemRegistry(final RegistryEvent.Register<Item> event) {
		proxy.registerItems(event);
	}
}
