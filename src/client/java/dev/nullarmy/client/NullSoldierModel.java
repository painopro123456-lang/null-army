package dev.nullarmy.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

public final class NullSoldierModel extends EntityModel<LivingEntityRenderState> {
    public static final String LAYER = "main";
    private final ModelPart head, body, leftArm, rightArm, leftLeg, rightLeg;

    public NullSoldierModel(ModelPart root) {
        super(root);
        head = root.getChild("head");
        body = root.getChild("body");
        leftArm = root.getChild("left_arm");
        rightArm = root.getChild("right_arm");
        leftLeg = root.getChild("left_leg");
        rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body",
            CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4.5f, -6, -2.5f, 9, 12, 5, new CubeDeformation(0.0f))
                .texOffs(16, 34).addBox(-5, -5, -3, 10, 3, 6, new CubeDeformation(0.15f)),
            PartPose.offset(0, 12, 0));
        root.addOrReplaceChild("head",
            CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4, -8, -4, 8, 8, 8)
                .texOffs(32, 0).addBox(-4.5f, -8.8f, -4.5f, 9, 2, 9, new CubeDeformation(0.15f))
                .texOffs(0, 48).addBox(-5, -3, -4.5f, 10, 2, 1, new CubeDeformation(0.0f)),
            PartPose.offset(0, 2, 0));
        root.addOrReplaceChild("left_arm",
            CubeListBuilder.create().texOffs(40, 16)
                .addBox(-1.5f, -2, -2, 4, 12, 4)
                .texOffs(40, 32).addBox(-2, 7, -2.5f, 5, 4, 5, new CubeDeformation(0.12f)),
            PartPose.offset(5, 4, 0));
        root.addOrReplaceChild("right_arm",
            CubeListBuilder.create().texOffs(40, 16)
                .addBox(-2.5f, -2, -2, 4, 12, 4)
                .texOffs(40, 32).addBox(-3, 7, -2.5f, 5, 4, 5, new CubeDeformation(0.12f)),
            PartPose.offset(-5, 4, 0));
        root.addOrReplaceChild("left_leg",
            CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2, 0, -2, 4, 10, 4)
                .texOffs(0, 32).addBox(-2.3f, 7, -2.5f, 5, 4, 5, new CubeDeformation(0.12f)),
            PartPose.offset(2, 12, 0));
        root.addOrReplaceChild("right_leg",
            CubeListBuilder.create().texOffs(0, 16)
                .addBox(-2, 0, -2, 4, 10, 4)
                .texOffs(0, 32).addBox(-2.3f, 7, -2.5f, 5, 4, 5, new CubeDeformation(0.12f)),
            PartPose.offset(-2, 12, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        head.xRot = state.xRot * Mth.DEG_TO_RAD;
        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        float swing = state.walkAnimationPos * 0.6662f;
        float amount = state.walkAnimationSpeed;
        leftLeg.xRot = Mth.cos(swing) * 1.35f * amount;
        rightLeg.xRot = Mth.cos(swing + Mth.PI) * 1.35f * amount;
        leftArm.xRot = Mth.cos(swing + Mth.PI) * 0.85f * amount;
        rightArm.xRot = Mth.cos(swing) * 0.85f * amount;
    }
}
