package net.bunten.enderscape.client.entity.enderling;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import com.mojang.blaze3d.vertex.PoseStack;

@Environment(EnvType.CLIENT)
public class EnderlingModel extends EntityModel<EnderlingRenderState> implements ArmedModel<EnderlingRenderState> {
    private final ModelPart enderling;
    private final ModelPart bodyRoot;
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation chaseAnimation;
    private final KeyframeAnimation rightAttackAnimation;
    private final KeyframeAnimation leftAttackAnimation;

    public EnderlingModel(ModelPart root) {
        super(root);
        enderling = root.getChild("enderling");
        bodyRoot = enderling.getChild("body_root");
        body = bodyRoot.getChild("body");
        torso = body.getChild("torso");
        head = torso.getChild("h_head");
        rightArm = torso.getChild("right_arm");
        leftArm = torso.getChild("left_arm");

        idleAnimation = EnderlingAnimations.IDLE.bake(root);
        walkAnimation = EnderlingAnimations.WALK.bake(root);
        chaseAnimation = EnderlingAnimations.CHASE.bake(root);
        rightAttackAnimation = EnderlingAnimations.RIGHT_ATTACK.bake(root);
        leftAttackAnimation = EnderlingAnimations.LEFT_ATTACK.bake(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition enderling = partDefinition.addOrReplaceChild("enderling", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition bodyRoot = enderling.addOrReplaceChild("body_root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body = bodyRoot.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));

        body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 16)
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 16).mirror()
                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(2.0F, 0.0F, 0.0F));

        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition head = torso.addOrReplaceChild("h_head", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -12.0F, 0.0F));

        head.addOrReplaceChild("h_left_eyelid", CubeListBuilder.create().texOffs(24, 30)
                .addBox(-1.5F, 0.25F, -2.25F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(2.5F, -3.25F, -1.75F));
        head.addOrReplaceChild("h_right_eyelid", CubeListBuilder.create().texOffs(24, 30)
                .addBox(-1.5F, 0.25F, -2.25F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.5F, -3.25F, -1.75F));
        head.addOrReplaceChild("h_left_eye_glare", CubeListBuilder.create().texOffs(0, 32)
                .addBox(0.0F, -1.0F, 0.0F, 6.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offset(4.0F, -2.0F, -4.0F));
        head.addOrReplaceChild("h_right_eye_glare", CubeListBuilder.create().texOffs(0, 32)
                .addBox(-6.0F, -1.0F, 0.0F, 6.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offset(-4.0F, -2.0F, -4.0F));

        torso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-2.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-4.0F, -11.0F, 0.0F));
        torso.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0).mirror()
                .addBox(0.0F, -1.0F, -1.0F, 2.0F, 12.0F, 2.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(4.0F, -11.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(EnderlingRenderState state) {
        super.setupAnim(state);

        float age = state.ageInTicks;
        if (state.deathTime <= 0) {
            head.xRot += (state.xRot * (Mth.PI / 180)) / 2;
            head.yRot += (state.yRot * (Mth.PI / 180)) / 2;
        }

        if (state.attacking) {
            if (state.rightSlash) {
                rightAttackAnimation.apply(state.rightAttackAnimationState, age);
            } else {
                leftAttackAnimation.apply(state.leftAttackAnimationState, age);
            }
        } else {
            idleAnimation.apply(state.idleAnimationState, age);
            walkAnimation.apply(state.walkAnimationState, age);
            chaseAnimation.apply(state.chaseAnimationState, age);
        }
    }

    @Override
    public void translateToHand(EnderlingRenderState state, HumanoidArm arm, PoseStack poseStack) {
        enderling.translateAndRotate(poseStack);
        bodyRoot.translateAndRotate(poseStack);
        body.translateAndRotate(poseStack);
        torso.translateAndRotate(poseStack);
        if (arm == HumanoidArm.RIGHT) {
            rightArm.translateAndRotate(poseStack);
        } else {
            leftArm.translateAndRotate(poseStack);
        }
    }
}
