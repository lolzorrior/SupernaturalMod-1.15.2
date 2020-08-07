package com.lolzorrior.supernaturalmod;

import com.lolzorrior.supernaturalmod.capabilities.*;
import com.lolzorrior.supernaturalmod.networking.PowerUpdatePacket;
import com.lolzorrior.supernaturalmod.networking.supernaturalPacketHndler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
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
        ISupernaturalClass oSupernaturalClass = event.getOriginal().getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new);
        supernaturalPacketHndler.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)event.getPlayer()), oSupernaturalClass);
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

        if (!event.getEntityLiving().world.isRemote()) {
            return;
        }
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }
        if (!(event.getItem().getItem() == Items.ROTTEN_FLESH)) {
            return;
        }
        int powerToAdd = 50;
        String setClass = "Zombie";
        supernaturalPacketHndler.channel.sendToServer(new PowerUpdatePacket(powerToAdd, setClass));
    }


    @SubscribeEvent
    public void onPlayerKillsMob(LivingDeathEvent event) {
        if (!(event.getEntityLiving().getAttackingEntity() instanceof PlayerEntity)) {
            return;
        }
        LivingEntity player = event.getEntityLiving().getAttackingEntity();
        int powerToAdd = 50;
        if (!(event.getEntityLiving() instanceof IMob)) {
            return;
        }
        if (!(event.getEntityLiving().getLastDamageSource().isProjectile())) {
            player.sendMessage(new StringTextComponent("Not a projectile!"));
            return;
        }
        if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Witch Hunter")) {
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
            player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
            return;
        }
        else if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Human")) {
            String setClass = "Witch Hunter";
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).setSupernaturalClass(setClass);
            player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
            player.sendMessage(new StringTextComponent("Your class is " + player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass()));
        }
    }

    @SubscribeEvent
    public void onPlayerClicksWithArrow(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer().getHeldItemMainhand().getItem() == Items.ARROW)) {
            return;
        }
        if (!(event.getResult() == Event.Result.DEFAULT )) {
            event.getPlayer().sendMessage(new StringTextComponent("Event result is " + event.getResult().toString()));
            return;
        }
        PlayerEntity player = event.getPlayer();
        String setClass = "Human";
        player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).setSupernaturalClass(setClass);
        player.sendMessage(new StringTextComponent("Your class is " + player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass()));
    }

    @SubscribeEvent
    public void onPlayerBecomesDemon(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof  PlayerEntity)) {
            return;
        }
        if (!(event.getSource().isFireDamage())) {
            return;
        }
        LivingEntity player = event.getEntityLiving();
        if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Demon")) {
            event.setCanceled(true);
            return;
        }
        if (!(event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST).isItemEqual(new ItemStack(Items.LEATHER_CHESTPLATE)))) {
            return;
        }
        else if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Human")) {
            int powerToAdd = 50;
            String setClass = "Demon";
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).setSupernaturalClass(setClass);
            player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
            player.sendMessage(new StringTextComponent("Your class is " + player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass()));
            player.sendMessage(new StringTextComponent("The world is " + event.getEntityLiving().world.isRemote()));
        }
    }

    @SubscribeEvent
    public void onPlayerBecomesWerewolf(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof  PlayerEntity)) {
            return;
        }
        if (!(event.getSource().getTrueSource().getType() == EntityType.WOLF)) {
            event.getEntityLiving().sendMessage(new StringTextComponent("Not a wolf!"));
            return;
        }
        LivingEntity player = event.getEntityLiving();
        if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Werewolf")) {
            event.setCanceled(true);
            return;
        }
        else if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Human")) {
            int powerToAdd = 50;
            String setClass = "Werewolf";
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).setSupernaturalClass(setClass);
            player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
            player.sendMessage(new StringTextComponent("Your class is " + player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass()));
        }
    }

    @SubscribeEvent
    public void onPlayerBecomesMage(PlayerInteractEvent.RightClickItem event) {
        PlayerEntity player = event.getPlayer();
        int powerToAdd = 50;
        if (event.getPlayer().getHeldItemMainhand().getItem() == Items.LAPIS_BLOCK) {
            if (event.getSide() == LogicalSide.SERVER) {
                if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Mage")) {
                    player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
                    player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
                    event.getPlayer().inventory.decrStackSize(event.getPlayer().inventory.currentItem, 1);
                } else if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Human")) {
                    String setClass = "Mage";
                    player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
                    player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).setSupernaturalClass(setClass);
                    player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
                    player.sendMessage(new StringTextComponent("Your class is " + player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass()));
                    event.getPlayer().inventory.decrStackSize(event.getPlayer().inventory.currentItem, 1);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerBecomesWarlock(PlayerInteractEvent.RightClickItem event) {
        PlayerEntity player = event.getPlayer();
        int powerToAdd = 50;
        if (event.getPlayer().getHeldItemMainhand().getItem() == Items.REDSTONE_BLOCK) {
            if (event.getSide() == LogicalSide.SERVER) {
                if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Warlock")) {
                    player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
                    player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
                    event.getPlayer().inventory.decrStackSize(event.getPlayer().inventory.currentItem, 1);
                } else if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Human")) {
                    String setClass = "Warlock";
                    player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
                    player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).setSupernaturalClass(setClass);
                    player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
                    player.sendMessage(new StringTextComponent("Your class is " + player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass()));
                    event.getPlayer().inventory.decrStackSize(event.getPlayer().inventory.currentItem, 1);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerBecomesMonk(LivingDeathEvent event) {
        if (!(event.getEntityLiving().getAttackingEntity() instanceof PlayerEntity)) {
            return;
        }
        LivingEntity player = event.getEntityLiving().getAttackingEntity();
        int powerToAdd = 50;
        if (!(event.getEntityLiving().getAttackingEntity().getActiveItemStack().isEmpty())) {
            player.sendMessage(new StringTextComponent("Hand isn't empty."));
            return;
        }
        if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Monk")) {
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
            player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
            return;
        } else if (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Human")) {
            String setClass = "Monk";
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).fill(powerToAdd);
            player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).setSupernaturalClass(setClass);
            player.sendMessage(new StringTextComponent("Updated Power: " + (player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getPower())));
            player.sendMessage(new StringTextComponent("Your class is " + player.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass()));
        }
    }

    @SubscribeEvent
    public void wolfTargetsWerewolf(LivingAttackEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }
        if (!(event.getEntityLiving().getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Werewolf"))) {
            return;
        }
        if (!(event.getSource().getTrueSource().getType() == EntityType.WOLF)) {
            return;
        }
        event.setCanceled(true);
        ((WolfEntity) event.getEntityLiving()).setAngry(false);
        /*if(!((WolfEntity) event.getSource().getTrueSource()).isTamed()) {
            ((WolfEntity) event.getSource().getTrueSource()).setTamed(true);
            ((WolfEntity) event.getSource().getTrueSource()).setTamedBy((PlayerEntity) event.getEntityLiving());

        }*/
    }

    @SubscribeEvent
    public void wolfTargetingEvent(LivingSetAttackTargetEvent event) {
        if (!(event.getEntity() instanceof WolfEntity)) {
            return;
        }
        if (!(event.getTarget() instanceof PlayerEntity)) {
            return;
        }
        if (!(event.getTarget().getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).getSupernaturalClass().equals("Werewolf"))) {
            return;
        }
        event.getEntityLiving().setRevengeTarget(null);
        ((WolfEntity) event.getEntityLiving()).setAttackTarget(null);

        /*if (event.getTarget() instanceof PlayerEntity) {
            if (event.getTarget().getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new).equals("Werewolf")) {
                 event.getEntityLiving().setRevengeTarget(null);
                ((WolfEntity) event.getEntityLiving()).setAttackTarget(null);
            }
        }*/
    }
}
