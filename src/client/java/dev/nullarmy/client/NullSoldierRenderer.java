package dev.nullarmy.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public final class NullSoldierRenderer extends MobRenderer<NullSoldierEntity, LivingEntityRenderState, NullSoldierModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        "null_army", "textures/entity/null_soldier.png");

    public NullSoldierRenderer(EntityRendererProvider.Context context) {
        super(context, new NullSoldierModel(context.bakeLayer(NullSoldierLayers.NULL_SOLDIER)), 0.34f);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
