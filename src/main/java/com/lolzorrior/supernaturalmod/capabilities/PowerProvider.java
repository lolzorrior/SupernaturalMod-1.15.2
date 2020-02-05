package com.lolzorrior.supernaturalmod.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PowerProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(ISupernaturalPower.class)
    public static final Capability<ISupernaturalPower> POWER_CAP = null;
    private LazyOptional<ISupernaturalPower> instance = LazyOptional.of(POWER_CAP::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == POWER_CAP ? instance.cast() : LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return getCapability(cap, null);
    }

    @Override
    public INBT serializeNBT() {
        return POWER_CAP.getStorage().writeNBT(POWER_CAP, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional is empty")),null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        POWER_CAP.getStorage().readNBT(POWER_CAP, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional is empty")),null, nbt);
    }
}
