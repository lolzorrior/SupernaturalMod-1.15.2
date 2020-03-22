package com.lolzorrior.supernaturalmod.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SuperCapsProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
    @CapabilityInject(ISuperCaps.class)
    static Capability<ISuperCaps> SUPER_CAPS = null;
    private static LazyOptional<ISuperCaps> instance = LazyOptional.of(SUPER_CAPS.getDefaultInstance());

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SUPER_CAPS ? instance.cast(), LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
