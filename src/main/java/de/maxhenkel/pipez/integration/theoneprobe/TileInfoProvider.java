package de.maxhenkel.pipez.integration.theoneprobe;

import de.maxhenkel.pipez.Main;
import de.maxhenkel.pipez.blocks.PipeBlock;
import de.maxhenkel.pipez.blocks.tileentity.PipeLogicTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class TileInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Main.MODID + ":probeinfoprovider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo info, PlayerEntity player, World world, BlockState state, IProbeHitData hitData) {
        TileEntity te = world.getTileEntity(hitData.getPos());

        if (state.getBlock() instanceof PipeBlock) {
            PipeBlock pipe = (PipeBlock) state.getBlock();
            Direction selectedSide = pipe.getSelection(state, world, hitData.getPos(), player).getKey();
            if (selectedSide == null) {
                return;
            }
            if (!(te instanceof PipeLogicTileEntity)) {
                return;
            }

            PipeLogicTileEntity pipeTile = (PipeLogicTileEntity) te;

            if (!pipeTile.isExtracting(selectedSide)) {
                return;
            }

            ItemStack upgrade = pipeTile.getUpgradeItem(selectedSide);

            IProbeInfo i;
            if (upgrade.isEmpty()) {
                i = info.text(new TranslationTextComponent("tooltip.pipez.no_upgrade"));
            } else {
                i = info.horizontal()
                        .item(upgrade)
                        .vertical()
                        .itemLabel(upgrade);
            }
            for (PipeType<?> type : pipeTile.getPipeTypes()) {
                if (pipeTile.isEnabled(selectedSide, type)) {
                    i = i.text(type.getTransferText(pipeTile.getUpgrade(selectedSide)));
                }
            }
        }
    }
}
