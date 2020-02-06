package com.lolzorrior.supernaturalmod;

import com.lolzorrior.supernaturalmod.capabilities.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
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
    public static void onCommonSetup(FMLCommonSetupEvent event)
    {
        //Register our Capability
        CapabilityManager.INSTANCE.register(ISupernaturalPower.class, new PowerStorage(), SupernaturalPower::new);
        CapabilityManager.INSTANCE.register(ISupernaturalClass.class, new SupernaturalClassStorage(), SupernaturalClass::new);
        LOGGER.info("Capabilities registered");
    }

    @SubscribeEvent
    public static void onPlayerLogsIn(PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        LazyOptional<ISupernaturalPower> spowerCapability = player.getCapability(PowerProvider.POWER_CAP);
        ISupernaturalPower power = spowerCapability.orElse(new SupernaturalPower());

        LazyOptional<ISupernaturalClass> sclassCapability = player.getCapability(SupernaturalClassProvider.SUPERNATURAL_CLASS);
        ISupernaturalClass supernaturalClass = sclassCapability.orElse(SupernaturalClass.getDefaultClass());

        player.sendMessage(new StringTextComponent("Welcome, your power is " + (power.getPower())));
        power.fill(50);
        player.sendMessage(new StringTextComponent("Updated Power: " + (power.getPower())));
        player.sendMessage(new StringTextComponent("Your class is " + supernaturalClass.getSupernaturalClass()));

    }

    @SubscribeEvent
    public static void onPlayerEatsFlesh(Finish event)
    {
        ISupernaturalClass playersClass = SupernaturalClass.getDefaultClass();

        LivingEntity currentPlayer = event.getEntityLiving();
        LazyOptional<ISupernaturalClass> optional = currentPlayer.getCapability(SupernaturalClassProvider.SUPERNATURAL_CLASS, null);
        if (optional.isPresent()) {
            playersClass = optional.orElse(SupernaturalClass.getDefaultClass());
        }

        if (new ItemStack(Items.ROTTEN_FLESH).equals(event.getItem()) && "Human".equals(playersClass.getSupernaturalClass())) {
            playersClass.setSupernaturalClass("Zombie");
        }
    }
}
