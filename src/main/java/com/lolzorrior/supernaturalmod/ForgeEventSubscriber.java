package com.lolzorrior.supernaturalmod;

import com.lolzorrior.supernaturalmod.capabilities.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
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
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Capabilities registered");
    }

    @SubscribeEvent
    public static void onPlayerLogsIn(PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();

    }

    //This method seems to be accessing and writing to a new SupernaturalClass and SupernaturalPower instance
    @SubscribeEvent
    public static void onPlayerEatsFlesh(LivingEntityUseItemEvent.Finish event) {
        if (new ItemStack(Items.ROTTEN_FLESH).isItemEqual(event.getItem()) && event.getEntityLiving() instanceof PlayerEntity) {
            LivingEntity currentPlayer = event.getEntityLiving();
        }
    }

}
