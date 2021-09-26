package de.maxhenkel.pipez.capabilities;

import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ModCapabilities {

    @CapabilityInject(IGasHandler.class)
    public static Capability<IGasHandler> GAS_HANDLER_CAPABILITY = null;

    @CapabilityInject(ISlurryHandler.class)
    public static Capability<ISlurryHandler> SLURRY_HANDLER_CAPABILITY = null;

}
