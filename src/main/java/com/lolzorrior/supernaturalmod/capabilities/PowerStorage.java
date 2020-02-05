package com.lolzorrior.supernaturalmod.capabilities;

import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PowerStorage implements Capability.IStorage<ISupernaturalPower> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<ISupernaturalPower> capability, ISupernaturalPower instance, Direction side) {
        return IntNBT.func_229692_a_(instance.getPower());
    }

    @Override
    public void readNBT(Capability<ISupernaturalPower> capability, ISupernaturalPower instance, Direction side, INBT nbt) {
    instance.set(((IntNBT) nbt).getInt());
    }
}
