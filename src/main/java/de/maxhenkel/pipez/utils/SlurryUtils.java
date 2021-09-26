package de.maxhenkel.pipez.utils;

import de.maxhenkel.corelib.tag.NamedTagWrapper;
import de.maxhenkel.corelib.tag.SingleElementTag;
import de.maxhenkel.pipez.capabilities.ModCapabilities;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.chemical.slurry.ISlurryHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class SlurryUtils {

    public static final ITag.INamedTag<Slurry> EMPTY_SLURRY_TAG = new SingleElementTag<>(SlurryStack.EMPTY.getType());

    @Nullable
    public static ITag.INamedTag<Slurry> getSlurry(String name, boolean nullIfNotExists) {
        ResourceLocation id;
        if (name.startsWith("#")) {
            id = new ResourceLocation(name.substring(1));
            ITag<Slurry> tag = ChemicalTags.SLURRY.getCollection().getTag(id);
            if (tag == null) {
                return nullIfNotExists ? null : EMPTY_SLURRY_TAG;
            } else {
                return new NamedTagWrapper<>(tag, id);
            }
        } else {
            id = new ResourceLocation(name);
            if (!MekanismAPI.slurryRegistry().containsKey(id)) {
                return nullIfNotExists ? null : EMPTY_SLURRY_TAG;
            } else {
                Slurry slurry = MekanismAPI.slurryRegistry().getValue(new ResourceLocation(name));
                if (slurry == null) {
                    return nullIfNotExists ? null : EMPTY_SLURRY_TAG;
                } else {
                    return new SingleElementTag<>(slurry);
                }
            }
        }
    }

    @Nullable
    public static SlurryStack getSlurryContained(ItemStack stack) {
        LazyOptional<ISlurryHandler> c = stack.getCapability(ModCapabilities.SLURRY_HANDLER_CAPABILITY);
        ISlurryHandler handler = c.orElse(null);
        if (handler == null) {
            return null;
        }
        if (handler.getTanks() <= 0) {
            return null;
        }
        return handler.getChemicalInTank(0).copy();
    }

}
