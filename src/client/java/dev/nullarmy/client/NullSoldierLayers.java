package dev.nullarmy.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.Identifier;

public final class NullSoldierLayers {
    public static final ModelLayerLocation NULL_SOLDIER = new ModelLayerLocation(
        Identifier.fromNamespaceAndPath("null_army", "null_soldier"), "main");

    private NullSoldierLayers() {}

    public static void register() {
        net.minecraft.client.model.geom.EntityModelLayerRegistry.registerLayerDefinition(
            NULL_SOLDIER, NullSoldierModel::createLayer);
    }
}
