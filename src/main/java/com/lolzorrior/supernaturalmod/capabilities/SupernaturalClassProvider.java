package com.lolzorrior.supernaturalmod.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SupernaturalClassProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(ISupernaturalClass.class)
    public static final Capability<ISupernaturalClass> SUPERNATURAL_CLASS = null;
    private LazyOptional<ISupernaturalClass> instance = LazyOptional.of(SUPERNATURAL_CLASS::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional <T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SUPERNATURAL_CLASS ? instance.cast() : LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return getCapability(cap, null);
    }

    @Override
    public INBT serializeNBT() {
        return SUPERNATURAL_CLASS.getStorage().writeNBT(SUPERNATURAL_CLASS, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional is empty")),null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        SUPERNATURAL_CLASS.getStorage().readNBT(SUPERNATURAL_CLASS, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional is empty")),null, nbt);
    }
}
