package com.lolzorrior.supernaturalmod;

import com.lolzorrior.supernaturalmod.capabilities.*;
import com.lolzorrior.supernaturalmod.networking.PowerUpdatePacket;
import com.lolzorrior.supernaturalmod.networking.supernaturalPacketHndler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.lolzorrior.supernaturalmod.capabilities.SupernaturalClassProvider.SUPERNATURAL_CLASS;

@Mod.EventBusSubscriber(modid = SupernaturalMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ISupernaturalClass.class, new SupernaturalClassStorage(), SupernaturalClass::new);
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if ((event.getObject().world.isRemote())) {return;}
        if (!(event.getObject() instanceof PlayerEntity)) {return;}
        PlayerEntity player = (PlayerEntity) event.getObject();
        if (!(player.getCapability(SupernaturalClassProvider.SUPERNATURAL_CLASS).isPresent())){
            event.addCapability(SupernaturalMod.SUPER_CLASS, new SupernaturalClassProvider());
        }
        LOGGER.info("Capabilities attached");
    }


    @SubscribeEvent
    public static void onPlayerLogsIn(PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();

        LazyOptional<ISupernaturalClass> sclassCapability = player.getCapability(SUPERNATURAL_CLASS);
        ISupernaturalClass supernaturalClass = sclassCapability.orElseThrow(NullPointerException::new);

        player.sendMessage(new StringTextComponent("Welcome, your power is " + (supernaturalClass.getPower())));
        supernaturalClass.fill(50);
        player.sendMessage(new StringTextComponent("Updated Power: " + (supernaturalClass.getPower())));
        player.sendMessage(new StringTextComponent("Your class is " + supernaturalClass.getSupernaturalClass()));
        player.sendMessage(new StringTextComponent("The world is " + event.getPlayer().world.isRemote()));
        supernaturalPacketHndler.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), supernaturalClass);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        LazyOptional<ISupernaturalClass> sclassCapability = player.getCapability(SUPERNATURAL_CLASS);
        ISupernaturalClass supernaturalClass = sclassCapability.orElseThrow(NullPointerException::new);
        supernaturalPacketHndler.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), supernaturalClass);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity player = event.getPlayer();
        LazyOptional<ISupernaturalClass> sclassCapability = player.getCapability(SUPERNATURAL_CLASS);
        ISupernaturalClass supernaturalClass = sclassCapability.orElseThrow(NullPointerException::new);
        supernaturalPacketHndler.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), supernaturalClass);
    }

    @SubscribeEvent
    public void onPlayerRespawns(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        LazyOptional<ISupernaturalClass> sclassCapability = player.getCapability(SUPERNATURAL_CLASS);
        ISupernaturalClass supernaturalClass = sclassCapability.orElseThrow(NullPointerException::new);

        supernaturalPacketHndler.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), supernaturalClass);
    }

    @SubscribeEvent
    public void onPlayerEatsFlesh(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }
        if (!event.getEntityLiving().isHandActive()){
            return;
        }
        int powerToAdd = 50;
        supernaturalPacketHndler.channel.sendToServer(new PowerUpdatePacket(powerToAdd));
    }
}
