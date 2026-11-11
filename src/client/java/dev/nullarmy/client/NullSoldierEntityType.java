package dev.nullarmy.client;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class NullSoldierEntityType {
    public static final ResourceKey<EntityType<?>> KEY = ResourceKey.create(
        Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("null_army", "null_soldier"));
    public static final EntityType<NullSoldierEntity> TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        KEY,
        EntityType.Builder.of(NullSoldierEntity::new, MobCategory.MISC)
            .sized(0.62f, 1.92f)
            .clientTrackingRange(10)
            .updateInterval(2)
            .build(KEY)
    );

    private NullSoldierEntityType() {}

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(TYPE, NullSoldierEntity.createAttributes());
    }
}
