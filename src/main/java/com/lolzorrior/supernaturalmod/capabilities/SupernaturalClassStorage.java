package com.lolzorrior.supernaturalmod.capabilities;

import net.minecraft.nbt.CompoundNBT;
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
        final CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putString("class", instance.getSupernaturalClass());
        compoundNBT.putInt("power", instance.getPower());
        return compoundNBT;
    }

    @Override
    public void readNBT(Capability<ISupernaturalClass> capability, ISupernaturalClass instance, Direction side, INBT nbt) {
        final CompoundNBT compoundNBT = (CompoundNBT) nbt;
        instance.setSupernaturalClass(compoundNBT.getString("class"));
        instance.set(compoundNBT.getInt("power"));
    }
}
