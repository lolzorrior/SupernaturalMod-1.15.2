package com.lolzorrior.supernaturalmod.networking;

import com.lolzorrior.supernaturalmod.capabilities.ISupernaturalClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import static com.lolzorrior.supernaturalmod.capabilities.SupernaturalClassProvider.SUPERNATURAL_CLASS;

public class PowerUpdatePacket {
    private static final Logger LOGGER = LogManager.getLogger();
    int power = 0;
    String sClass = "";

    public PowerUpdatePacket(){}

    public PowerUpdatePacket(int input, String classInput) {
        power = input;
        sClass = classInput;
    }

    public static void encode(PowerUpdatePacket msg, PacketBuffer buf) {
        buf.writeInt(msg.power);
        buf.writeString(msg.sClass);
        LOGGER.info("Encoding Power: " + msg.power);
    }

    public static PowerUpdatePacket decode(PacketBuffer buf) {
        LOGGER.info("Decoding Power");
        return new PowerUpdatePacket(buf.readInt(), buf.readString());
    }

    public static void handle(PowerUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            LogicalSide sideRecieved = ctx.get().getDirection().getReceptionSide();
            ServerPlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
            // do stuff
            if (sideRecieved != LogicalSide.SERVER) {
                LOGGER.info("This isn't the server!");
                return;
            }
            ISupernaturalClass sclass = sender.getCapability(SUPERNATURAL_CLASS).orElseThrow(NullPointerException::new);
            String stringClass = sclass.getSupernaturalClass();
            if (stringClass.equals("Human")) {
                sclass.setSupernaturalClass(msg.sClass);
                sender.sendMessage(new StringTextComponent("Your class is now: " + sclass.getSupernaturalClass()));
            }
            else if (stringClass.equals(msg.sClass)) {
                sclass.fill(msg.power);
                sender.sendMessage(new StringTextComponent("Your power is now: " + sclass.getPower()));
            }
            LOGGER.info("Power Updated: " + sclass.getPower());
        });
        ctx.get().setPacketHandled(true);
    }
}
