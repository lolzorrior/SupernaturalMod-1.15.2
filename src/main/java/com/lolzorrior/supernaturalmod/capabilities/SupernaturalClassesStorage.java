package com.lolzorrior.supernaturalmod.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SupernaturalClassesStorage implements Capability.IStorage<ISupernaturalClasses> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<ISupernaturalClasses> capability, ISupernaturalClasses instance, Direction side) {
        return StringNBT.func_229705_a_(instance.get());
    }

    @Override
    public void readNBT(Capability<ISupernaturalClasses> capability, ISupernaturalClasses instance, Direction side, INBT nbt) {
        instance.set((nbt).getString());
    }
}
