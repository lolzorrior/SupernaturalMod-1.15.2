package com.lolzorrior.supernaturalmod;


import com.lolzorrior.supernaturalmod.capabilities.ISupernaturalClass;
import com.lolzorrior.supernaturalmod.capabilities.SupernaturalClass;
import com.lolzorrior.supernaturalmod.capabilities.SupernaturalPowerProvider;
import com.lolzorrior.supernaturalmod.capabilities.SupernaturalClassProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.stream.Collectors;


@Mod(SupernaturalMod.MOD_ID)
public class SupernaturalMod {
    public static final String MOD_ID = "supernaturalmod";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation POWER_CAP = new ResourceLocation(SupernaturalMod.MOD_ID, "power");
    public static final ResourceLocation SUPERNATURAL_CLASS = new ResourceLocation(SupernaturalMod.MOD_ID, "class");

    public SupernaturalMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeEventSubscriber::onCommonSetup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("supernaturalmod", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }


    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }

        @SubscribeEvent
        public static void onPlayerEatsFlesh(LivingEntityUseItemEvent.Finish event) {
            LivingEntity currentPlayer = event.getEntityLiving();
            LazyOptional<ISupernaturalClass> optional = currentPlayer.getCapability(SupernaturalClassProvider.SUPERNATURAL_CLASS, null);
            ISupernaturalClass playersClass = optional.orElse(new SupernaturalClass());

            if (new ItemStack(Items.ROTTEN_FLESH).equals(event.getItem()) && "Human".equals(playersClass.getSupernaturalClass())) {
                playersClass.setSupernaturalClass("Zombie");
                currentPlayer.sendMessage(new StringTextComponent("You feel yourself turn."));
                currentPlayer.sendMessage(new StringTextComponent("You are now a " + playersClass.getSupernaturalClass()));
            }
        }
    }

    @Mod.EventBusSubscriber()
    public static class capabilityAttacher {
        @SubscribeEvent
        public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
            if (!(event.getObject() instanceof PlayerEntity)) return;
            event.addCapability(POWER_CAP, new SupernaturalPowerProvider());
            event.addCapability(SUPERNATURAL_CLASS, new SupernaturalClassProvider());
            LOGGER.info("Capabilities attached");
        }
    }
}
