package de.maxhenkel.pipez.blocks.tileentity;

import de.maxhenkel.corelib.CachedMap;
import de.maxhenkel.pipez.blocks.tileentity.types.EnergyPipeType;
import de.maxhenkel.pipez.blocks.tileentity.types.FluidPipeType;
import de.maxhenkel.pipez.blocks.tileentity.types.ItemPipeType;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import de.maxhenkel.pipez.utils.DummyFluidHandler;
import de.maxhenkel.pipez.utils.DummyItemHandler;
import de.maxhenkel.pipez.utils.PipeEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PipeLogicTileEntity extends UpgradeTileEntity {

    protected PipeType<?>[] types;
    protected final int[][] rrIndex;
    protected CachedMap<Direction, PipeEnergyStorage> energyCache;

    public PipeLogicTileEntity(BlockEntityType<?> tileEntityTypeIn, PipeType<?>[] types, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        this.types = types;
        rrIndex = new int[Direction.values().length][types.length];
        energyCache = new CachedMap<>();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (remove) {
            return super.getCapability(cap, side);
        }

        if (cap == CapabilityEnergy.ENERGY && hasType(EnergyPipeType.INSTANCE)) {
            if (side != null && isExtracting(side)) {
                return LazyOptional.of(() -> energyCache.get(side, () -> new PipeEnergyStorage(this, side))).cast();
            }
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && hasType(FluidPipeType.INSTANCE)) {
            if (side == null || isExtracting(side)) {
                return LazyOptional.of(() -> DummyFluidHandler.INSTANCE).cast();
            }
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && hasType(ItemPipeType.INSTANCE)) {
            if (side == null || isExtracting(side)) {
                return LazyOptional.of(() -> DummyItemHandler.INSTANCE).cast();
            }
        }

        return super.getCapability(cap, side);
    }

    public boolean hasType(PipeType<?> type) {
        for (PipeType<?> t : types) {
            if (t == type) {
                return true;
            }
        }
        return false;
    }

    public int getRoundRobinIndex(Direction direction, PipeType<?> pipeType) {
        return rrIndex[direction.get3DDataValue()][getIndex(pipeType)];
    }

    public void setRoundRobinIndex(Direction direction, PipeType<?> pipeType, int value) {
        rrIndex[direction.get3DDataValue()][getIndex(pipeType)] = value;
    }

    public boolean isEnabled(Direction side, PipeType<?> pipeType) {
        UpgradeTileEntity.RedstoneMode redstoneMode = getRedstoneMode(side, pipeType);
        return redstoneMode != UpgradeTileEntity.RedstoneMode.ALWAYS_OFF;
    }

    public int getPreferredPipeIndex(Direction side) {
        for (int i = 0; i < types.length; i++) {
            if (isEnabled(side, types[i])) {
                return i;
            }
        }
        return 0;
    }

    public boolean shouldWork(Direction side, PipeType<?> pipeType) {
        RedstoneMode redstoneMode = getRedstoneMode(side, pipeType);
        if (redstoneMode.equals(RedstoneMode.ALWAYS_OFF)) {
            return false;
        } else if (redstoneMode.equals(RedstoneMode.OFF_WHEN_POWERED)) {
            return !isRedstonePowered();
        } else if (redstoneMode.equals(RedstoneMode.ON_WHEN_POWERED)) {
            return isRedstonePowered();
        } else {
            return true;
        }
    }

    public boolean isRedstonePowered() {
        return level.hasNeighborSignal(worldPosition);
    }

    public PipeType<?>[] getPipeTypes() {
        return types;
    }

    public int getIndex(PipeType<?> pipeType) {
        for (int i = 0; i < getPipeTypes().length; i++) {
            PipeType<?> type = getPipeTypes()[i];
            if (type == pipeType) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            return;
        }

        for (PipeType<?> type : getPipeTypes()) {
            type.tick(this);
        }

        if (hasType(EnergyPipeType.INSTANCE)) {
            for (Direction side : Direction.values()) {
                if (isExtracting(side)) {
                    PipeEnergyStorage energyStorage = energyCache.get(side, () -> new PipeEnergyStorage(this, side));
                    energyStorage.tick();
                }
            }

        }
    }

    @Override
    public boolean canInsert(BlockEntity tileEntity, Direction direction) {
        for (PipeType<?> type : types) {
            if (type.canInsert(tileEntity, direction)) {
                return true;
            }
        }
        return false;
    }
}
