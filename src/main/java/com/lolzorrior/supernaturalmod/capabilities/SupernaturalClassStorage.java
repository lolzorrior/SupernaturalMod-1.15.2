package com.lolzorrior.supernaturalmod.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nullable;

public class SupernaturalClassStorage implements IStorage<ISupernaturalClass> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<ISupernaturalClass> capability, ISupernaturalClass instance, Direction side) {
        return StringNBT.func_229705_a_(instance.getSupernaturalClass());
    }

    @Override
    public void readNBT(Capability<ISupernaturalClass> capability, ISupernaturalClass instance, Direction side, INBT nbt) {
        instance.setSupernaturalClass((nbt).getString());
    }
}
