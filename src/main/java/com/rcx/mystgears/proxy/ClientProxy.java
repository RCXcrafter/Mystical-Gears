package com.rcx.mystgears.proxy;

import com.rcx.mystgears.ConfigHandler;
import com.rcx.mystgears.MysticalGears;
import com.rcx.mystgears.GearBehaviorRegular;
import com.rcx.mystgears.item.ItemGear;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.api.BotaniaAPIClient;

public class ClientProxy extends CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);
		if (ConfigHandler.tooltips)
			MinecraftForge.EVENT_BUS.register(new TooltipHandler());
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	public void registerBlocks(RegistryEvent.Register<Block> event) {
		super.registerBlocks(event);
	}

	public void registerItems(RegistryEvent.Register<Item> event) {
		super.registerItems(event);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		for (ItemGear item : MysticalGears.items) {
			item.registerModel();
		}
		for (ItemBlock item : MysticalGears.blocks) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
		//BotaniaAPIClient.registerSubtileModel(SubTileGearanium.class, new ModelResourceLocation(MysticalGears.MODID + ":" + SubTileGearanium.SUBTILE_GEARANIUM));
	}

	public static class TooltipHandler {
		@SubscribeEvent
		public void tooltipEvent(ItemTooltipEvent event) {
			if (MysticalMechanicsAPI.IMPL.isValidGear(event.getItemStack())) {
				IGearBehavior behavior = MysticalMechanicsAPI.IMPL.getGearBehavior(event.getItemStack());

				event.getToolTip().add(I18n.translateToLocal("desc.gear.name"));

				if (behavior instanceof GearBehaviorRegular) {
					GearBehaviorRegular regularBehavior = (GearBehaviorRegular) behavior;

					if (regularBehavior.extraLore != null) {
						event.getToolTip().add(I18n.translateToLocal(regularBehavior.extraLore));
					}

					if (regularBehavior.maxPower == 0)
						event.getToolTip().add(I18n.translateToLocal("desc.gear.max.name").replace("@power", "\u221E"));
					else
						event.getToolTip().add(I18n.translateToLocal("desc.gear.max.name").replace("@power", regularBehavior.maxPower + ""));
					event.getToolTip().add(I18n.translateToLocal("desc.gear.transfer.name").replace("@transfer", regularBehavior.powerTransfer + ""));
				}
			}
		}
	}
}
