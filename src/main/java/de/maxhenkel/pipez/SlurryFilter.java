package de.maxhenkel.pipez;

import de.maxhenkel.corelib.tag.SingleElementTag;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.slurry.Slurry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class SlurryFilter extends Filter<Slurry> {

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        if (tag != null) {
            if (tag instanceof SingleElementTag) {
                ResourceLocation key = MekanismAPI.slurryRegistry().getKey(((SingleElementTag<Slurry>) tag).getElement());
                if (key != null) {
                    compound.putString("Slurry", key.toString());
                }
            } else {
                compound.putString("Tag", tag.getName().toString());
            }
        }
        if (destination != null) {
            compound.put("Destination", destination.serializeNBT());
        }
        if (invert) {
            compound.putBoolean("Invert", true);
        }
        compound.putUUID("ID", id);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        tag = null;
        if (compound.contains("Slurry", Constants.NBT.TAG_STRING)) {
            Slurry slurry = MekanismAPI.slurryRegistry().getValue(new ResourceLocation(compound.getString("Slurry")));
            if (slurry != null) {
                tag = new SingleElementTag<>(slurry);
            }
        }
        if (compound.contains("Tag", Constants.NBT.TAG_STRING)) {
            tag = ChemicalTags.SLURRY.tag(new ResourceLocation(compound.getString("Tag")));
        }

        metadata = null;
        exactMetadata = false;

        if (compound.contains("Destination", Constants.NBT.TAG_COMPOUND)) {
            destination = new DirectionalPosition();
            destination.deserializeNBT(compound.getCompound("Destination"));
        } else {
            destination = null;
        }

        if (compound.contains("Invert", Constants.NBT.TAG_BYTE)) {
            invert = compound.getBoolean("Invert");
        } else {
            invert = false;
        }

        if (compound.contains("ID", Constants.NBT.TAG_INT_ARRAY)) {
            id = compound.getUUID("ID");
        } else {
            id = UUID.randomUUID();
        }
    }

}
