package com.lolzorrior.supernaturalmod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.sun.jmx.mbeanserver.Util.cast;

@Mod.EventBusSubscriber(modid = SupernaturalMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event)
    {
        //Register our Capability
        CapabilityManager.INSTANCE.register(ISupernaturalPower.class, new PowerStorage(), SupernaturalPower::new);
        LOGGER.info("Capabilities registered");
    }
    @SubscribeEvent
    public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        event.getPlayer().getCapability(PowerProvider.POWER_CAP).ifPresent(power -> player.sendMessage(new StringTextComponent("Welcome, your power is " + (power.getPower()))));

        player.getCapability(PowerProvider.POWER_CAP).ifPresent(power -> power.fill(50));
        event.getPlayer().getCapability(PowerProvider.POWER_CAP).ifPresent(power -> player.sendMessage(new StringTextComponent("Updated Power: " + (power.getPower()))));
    }
}
