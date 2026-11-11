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

/**
 * Fully custom Null trooper geometry. This is deliberately not a vanilla
 * skeleton/player model: the silhouette is built from angular armor plates,
 * a sealed helmet, shoulder pauldrons, a chest core and plated greaves.
 */
public final class NullSoldierModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart head, body, leftArm, rightArm, leftLeg, rightLeg;
    private final ModelPart leftShoulder, rightShoulder, backPlate, crest;
    private final ModelPart visor, chestCore, waist, leftKnee, rightKnee;
    private final ModelPart leftShin, rightShin, leftSpike, rightSpike;

    public NullSoldierModel(ModelPart root) {
        super(root);
        head = root.getChild("head");
        body = root.getChild("body");
        leftArm = root.getChild("left_arm");
        rightArm = root.getChild("right_arm");
        leftLeg = root.getChild("left_leg");
        rightLeg = root.getChild("right_leg");
        leftShoulder = root.getChild("left_shoulder");
        rightShoulder = root.getChild("right_shoulder");
        backPlate = root.getChild("back_plate");
        crest = root.getChild("crest");
        visor = root.getChild("visor");
        chestCore = root.getChild("chest_core");
        waist = root.getChild("waist");
        leftKnee = root.getChild("left_knee");
        rightKnee = root.getChild("right_knee");
        leftShin = root.getChild("left_shin");
        rightShin = root.getChild("right_shin");
        leftSpike = root.getChild("left_spike");
        rightSpike = root.getChild("right_spike");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
            CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4.5f, -6, -2.5f, 9, 12, 5)
                .texOffs(16, 34).addBox(-5, -5, -3, 10, 3, 6, new CubeDeformation(0.18f))
                .texOffs(40, 36).addBox(-4, 1, -2.8f, 8, 4, 5, new CubeDeformation(0.12f)),
            PartPose.offset(0, 12, 0));

        // Angular enclosed helmet with brow ridge, cheek guards and a narrow face slit.
        root.addOrReplaceChild("head",
            CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4, -8, -4, 8, 8, 8, new CubeDeformation(-0.15f))
                .texOffs(32, 0).addBox(-4.5f, -8.8f, -4.5f, 9, 2, 9, new CubeDeformation(0.12f))
                .texOffs(0, 48).addBox(-4.5f, -3, -4.6f, 9, 3, 1, new CubeDeformation(0.05f)),
            PartPose.offset(0, 2, 0));

        root.addOrReplaceChild("visor",
            CubeListBuilder.create().texOffs(32, 12)
                .addBox(-3, -5.2f, -4.18f, 6, 1, 0.5f),
            PartPose.offset(0, 2, 0));

        root.addOrReplaceChild("crest",
            CubeListBuilder.create().texOffs(0, 48)
                .addBox(-1, -11, -1, 2, 4, 2)
                .texOffs(8, 48).addBox(-3, -10, -0.5f, 6, 2, 1),
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

        root.addOrReplaceChild("left_shoulder",
            CubeListBuilder.create().texOffs(32, 48)
                .addBox(-2.8f, -2.5f, -3, 6, 5, 6, new CubeDeformation(0.18f)),
            PartPose.offset(5, 4, 0));
        root.addOrReplaceChild("right_shoulder",
            CubeListBuilder.create().texOffs(32, 48)
                .addBox(-3.2f, -2.5f, -3, 6, 5, 6, new CubeDeformation(0.18f)),
            PartPose.offset(-5, 4, 0));

        root.addOrReplaceChild("left_spike",
            CubeListBuilder.create().texOffs(52, 0)
                .addBox(-1, -5, -1, 2, 4, 2, new CubeDeformation(-0.15f)),
            PartPose.offset(6, 4, 0));
        root.addOrReplaceChild("right_spike",
            CubeListBuilder.create().texOffs(52, 0)
                .addBox(-1, -5, -1, 2, 4, 2, new CubeDeformation(-0.15f)),
            PartPose.offset(-6, 4, 0));

        root.addOrReplaceChild("back_plate",
            CubeListBuilder.create().texOffs(48, 32)
                .addBox(-4, -5, 0, 8, 10, 2, new CubeDeformation(0.12f))
                .texOffs(48, 44).addBox(-5, -4, 1, 10, 2, 2, new CubeDeformation(0.12f)),
            PartPose.offset(0, 12, 2.5f));

        root.addOrReplaceChild("chest_core",
            CubeListBuilder.create().texOffs(24, 48)
                .addBox(-1.5f, -2, -3.15f, 3, 4, 1, new CubeDeformation(0.02f)),
            PartPose.offset(0, 12, 0));
        root.addOrReplaceChild("waist",
            CubeListBuilder.create().texOffs(0, 56)
                .addBox(-4.5f, -1, -2.8f, 9, 3, 5, new CubeDeformation(0.08f)),
            PartPose.offset(0, 17, 0));

        root.addOrReplaceChild("left_knee",
            CubeListBuilder.create().texOffs(40, 52)
                .addBox(-2.5f, -1, -2.5f, 5, 4, 5, new CubeDeformation(0.12f)),
            PartPose.offset(2, 18, 0));
        root.addOrReplaceChild("right_knee",
            CubeListBuilder.create().texOffs(40, 52)
                .addBox(-2.5f, -1, -2.5f, 5, 4, 5, new CubeDeformation(0.12f)),
            PartPose.offset(-2, 18, 0));
        root.addOrReplaceChild("left_shin",
            CubeListBuilder.create().texOffs(0, 40)
                .addBox(-2.5f, 0, -2.6f, 5, 6, 5, new CubeDeformation(0.1f)),
            PartPose.offset(2, 20, 0));
        root.addOrReplaceChild("right_shin",
            CubeListBuilder.create().texOffs(0, 40)
                .addBox(-2.5f, 0, -2.6f, 5, 6, 5, new CubeDeformation(0.1f)),
            PartPose.offset(-2, 20, 0));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(LivingEntityRenderState state) {
        super.setupAnim(state);
        head.xRot = state.xRot * Mth.DEG_TO_RAD;
        head.yRot = state.yRot * Mth.DEG_TO_RAD;
        visor.yRot = head.yRot;
        crest.yRot = head.yRot;

        float swing = state.walkAnimationPos * 0.6662f;
        float amount = state.walkAnimationSpeed;
        leftLeg.xRot = Mth.cos(swing) * 1.25f * amount;
        rightLeg.xRot = Mth.cos(swing + Mth.PI) * 1.25f * amount;
        leftArm.xRot = Mth.cos(swing + Mth.PI) * 0.8f * amount;
        rightArm.xRot = Mth.cos(swing) * 0.8f * amount;
        leftShoulder.xRot = leftArm.xRot * 0.3f;
        rightShoulder.xRot = rightArm.xRot * 0.3f;
        leftSpike.xRot = leftShoulder.xRot;
        rightSpike.xRot = rightShoulder.xRot;
        leftKnee.xRot = leftLeg.xRot * 0.25f;
        rightKnee.xRot = rightLeg.xRot * 0.25f;
        leftShin.xRot = leftLeg.xRot * 0.15f;
        rightShin.xRot = rightLeg.xRot * 0.15f;
        backPlate.yRot = Mth.sin(swing) * 0.035f * amount;
    }
}
