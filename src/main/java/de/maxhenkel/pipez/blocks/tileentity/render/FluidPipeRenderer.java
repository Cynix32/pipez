package de.maxhenkel.pipez.blocks.tileentity.render;

import de.maxhenkel.pipez.ModelRegistry.Model;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class FluidPipeRenderer extends PipeRenderer {

    public FluidPipeRenderer(BlockEntityRendererProvider.Context renderer) {
        super(renderer);
    }

    @Override
    Model getModel() {
        return Model.FLUID_PIPE_EXTRACT;
    }
}
