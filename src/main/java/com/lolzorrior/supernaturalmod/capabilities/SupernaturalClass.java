package com.lolzorrior.supernaturalmod.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static com.lolzorrior.supernaturalmod.capabilities.SupernaturalClassProvider.SUPERNATURAL_CLASS;

public class SupernaturalClass implements ISupernaturalClass {
    private String supernaturalClass;
    private int power;

    public SupernaturalClass() {
        this.setSupernaturalClass("Human");
        this.power = 0;
    }

    public SupernaturalClass(String sclass, int power){
        this.setSupernaturalClass(sclass);
        this.set(power);
    }

    @Override
    public void setSupernaturalClass(String classes) {
        this.supernaturalClass = classes;
    }

    @Override
    public String getSupernaturalClass() {
        return supernaturalClass;
    }


    public void consume(int points) {
        this.power -= points;
        if (this.power < 0) this.power = 0;
    }

    @Override
    public void fill(int points) {
        this.power += points;
    }

    public void set(int points) {
        this.power = points;
    }

    public int getPower() {
        return this.power;
    }

    public static void encode(SupernaturalClass msg, PacketBuffer buf){
        buf.writeString(msg.supernaturalClass);
        buf.writeInt(msg.power);
    }

    public static SupernaturalClass decode(PacketBuffer buf){
        return new SupernaturalClass(buf.readString(), buf.readInt());
    }

    public static void handle(SupernaturalClass msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Work that needs to be threadsafe (most work)
            ServerPlayerEntity sender = ctx.get().getSender(); // the client that sent this packet
            // do stuff
        });
        ctx.get().setPacketHandled(true);
    }
}