package com.lolzorrior.supernaturalmod;

import com.lolzorrior.supernaturalmod.capabilities.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = SupernaturalMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event)
    {
        //Register our Capability
        CapabilityManager.INSTANCE.register(ISupernaturalPower.class, new PowerStorage(), SupernaturalPower::new);
        CapabilityManager.INSTANCE.register(ISupernaturalClasses.class, new SupernaturalClassesStorage(), SupernaturalClasses::new);
        LOGGER.info("Capabilities registered");
    }
    @SubscribeEvent
    public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        event.getPlayer().getCapability(PowerProvider.POWER_CAP).ifPresent(power -> player.sendMessage(new StringTextComponent("Welcome, your power is " + (power.getPower()))));
        player.getCapability(PowerProvider.POWER_CAP).ifPresent(power -> power.fill(50));
        event.getPlayer().getCapability(PowerProvider.POWER_CAP).ifPresent(power -> player.sendMessage(new StringTextComponent("Updated Power: " + (power.getPower()))));
        event.getPlayer().getCapability(SupernaturalClassesProvider.SUPERNATURAL_CLASS).ifPresent(supernaturalClasses -> supernaturalClasses.set("Human"));
        event.getPlayer().getCapability(SupernaturalClassesProvider.SUPERNATURAL_CLASS).ifPresent(supernaturalClasses -> player.sendMessage(new StringTextComponent("Your class is " + supernaturalClasses.get())));
    }

    @SubscribeEvent
    public static void onPlayerEatsFlesh(LivingEntityUseItemEvent.Finish event)
    {
        String modClass;
        event.getEntityLiving().getCapability(SupernaturalClassesProvider.SUPERNATURAL_CLASS).ifPresent(supernaturalClasses -> modClasses = supernaturalClasses.get());
        if (event.getItem() == new ItemStack(Items.ROTTEN_FLESH) && modClass == "Human")
        {
            event.getEntityLiving().getCapability(SupernaturalClassesProvider.SUPERNATURAL_CLASS).ifPresent(supernaturalClasses -> supernaturalClasses.set("Zombie"));
        }
    }
}
