package net.bunten.enderscape.client.entity.wraith;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class WraithModel extends EntityModel<WraithRenderState> {
    private final ModelPart wraith;
    private final ModelPart vfx;
    private final ModelPart body_rotation;
    private final ModelPart body_root;
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart upper_torso;
    private final ModelPart h_head;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart tail;
    private final ModelPart tail2;

    private final KeyframeAnimation idleAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation rightSlashAnimation;
    private final KeyframeAnimation leftSlashAnimation;
    private final KeyframeAnimation spinSlashAnimation;

    public WraithModel(ModelPart root) {
        super(root);
        this.wraith = root.getChild("wraith");
        this.vfx = this.wraith.getChild("vfx");
        this.body_rotation = this.wraith.getChild("body_rotation");
        this.body_root = this.body_rotation.getChild("body_root");
        this.body = this.body_root.getChild("body");
        this.torso = this.body.getChild("torso");
        this.upper_torso = this.torso.getChild("upper_torso");
        this.h_head = this.upper_torso.getChild("h_head");
        this.left_arm = this.upper_torso.getChild("left_arm");
        this.right_arm = this.upper_torso.getChild("right_arm");
        this.tail = this.torso.getChild("tail");
        this.tail2 = this.tail.getChild("tail2");

        idleAnimation = WraithAnimations.IDLE.bake(root);
        walkAnimation = WraithAnimations.WALK.bake(root);
        rightSlashAnimation = WraithAnimations.RIGHT_SLASH.bake(root);
        leftSlashAnimation = WraithAnimations.LEFT_SLASH.bake(root);
        spinSlashAnimation = WraithAnimations.SPIN_SLASH.bake(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition wraith = root.addOrReplaceChild("wraith", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition vfx = wraith.addOrReplaceChild("vfx", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition slashes = vfx.addOrReplaceChild("slashes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition slashes_rot = slashes.addOrReplaceChild("slashes_rot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition slashes_angle1 = slashes_rot.addOrReplaceChild("slashes_angle1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition slashes_rotator1 = slashes_angle1.addOrReplaceChild("slashes_rotator1", CubeListBuilder.create(), PartPose.offset(0.0F, -18.0F, 0.0F));

        PartDefinition slash_rot1 = slashes_rotator1.addOrReplaceChild("slash_rot1", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition slash_end1 = slash_rot1.addOrReplaceChild("slash_end1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 12.5F));

        CubeListBuilder slashCubes = CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, -1.5F, 20.0F, 0.0F, 3.0F, 25.0F, CubeDeformation.NONE)
                .texOffs(1, 18).addBox(-1.5F, 0.0F, 20.0F, 3.0F, 0.0F, 25.0F, CubeDeformation.NONE);
        slash_end1.addOrReplaceChild("slash1", slashCubes, PartPose.offset(0.0F, 0.0F, -45.0F));

        PartDefinition slash_rot2 = slashes_rotator1.addOrReplaceChild("slash_rot2", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition slash_end2 = slash_rot2.addOrReplaceChild("slash_end2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 12.5F));

        slash_end2.addOrReplaceChild("slash2", CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, -1.5F, 20.0F, 0.0F, 3.0F, 25.0F, CubeDeformation.NONE)
                .texOffs(1, 18).addBox(-1.5F, 0.0F, 20.0F, 3.0F, 0.0F, 25.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -45.0F));

        PartDefinition slash_rot3 = slashes_rotator1.addOrReplaceChild("slash_rot3", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 2.0F));

        PartDefinition slash_end3 = slash_rot3.addOrReplaceChild("slash_end3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 12.5F));

        slash_end3.addOrReplaceChild("slash3", CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, -1.5F, 20.0F, 0.0F, 3.0F, 25.0F, CubeDeformation.NONE)
                .texOffs(1, 18).addBox(-1.5F, 0.0F, 20.0F, 3.0F, 0.0F, 25.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -45.0F));

        PartDefinition slash_rot4 = slashes_rotator1.addOrReplaceChild("slash_rot4", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 6.0F));

        PartDefinition slash_end4 = slash_rot4.addOrReplaceChild("slash_end4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 12.5F));

        slash_end4.addOrReplaceChild("slash4", CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, -1.5F, 20.0F, 0.0F, 3.0F, 25.0F, CubeDeformation.NONE)
                .texOffs(1, 18).addBox(-1.5F, 0.0F, 20.0F, 3.0F, 0.0F, 25.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -45.0F));

        PartDefinition slashes_angle2 = slashes_rot.addOrReplaceChild("slashes_angle2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition slashes_rotator2 = slashes_angle2.addOrReplaceChild("slashes_rotator2", CubeListBuilder.create(), PartPose.offset(0.0F, -18.0F, 0.0F));

        PartDefinition slash_rot5 = slashes_rotator2.addOrReplaceChild("slash_rot5", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition slash_end5 = slash_rot5.addOrReplaceChild("slash_end5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 12.5F));

        slash_end5.addOrReplaceChild("slash5", CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, -1.5F, 20.0F, 0.0F, 3.0F, 25.0F, CubeDeformation.NONE)
                .texOffs(1, 18).addBox(-1.5F, 0.0F, 20.0F, 3.0F, 0.0F, 25.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -45.0F));

        PartDefinition slash_rot6 = slashes_rotator2.addOrReplaceChild("slash_rot6", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition slash_end6 = slash_rot6.addOrReplaceChild("slash_end6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 12.5F));

        slash_end6.addOrReplaceChild("slash6", CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, -1.5F, 20.0F, 0.0F, 3.0F, 25.0F, CubeDeformation.NONE)
                .texOffs(1, 18).addBox(-1.5F, 0.0F, 20.0F, 3.0F, 0.0F, 25.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -45.0F));

        PartDefinition slash_rot7 = slashes_rotator2.addOrReplaceChild("slash_rot7", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 2.0F));

        PartDefinition slash_end7 = slash_rot7.addOrReplaceChild("slash_end7", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 12.5F));

        slash_end7.addOrReplaceChild("slash7", CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, -1.5F, 20.0F, 0.0F, 3.0F, 25.0F, CubeDeformation.NONE)
                .texOffs(1, 18).addBox(-1.5F, 0.0F, 20.0F, 3.0F, 0.0F, 25.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -45.0F));

        PartDefinition slash_rot8 = slashes_rotator2.addOrReplaceChild("slash_rot8", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 6.0F));

        PartDefinition slash_end8 = slash_rot8.addOrReplaceChild("slash_end8", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 12.5F));

        slash_end8.addOrReplaceChild("slash8", CubeListBuilder.create().texOffs(0, 21).addBox(0.0F, -1.5F, 20.0F, 0.0F, 3.0F, 25.0F, CubeDeformation.NONE)
                .texOffs(1, 18).addBox(-1.5F, 0.0F, 20.0F, 3.0F, 0.0F, 25.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -45.0F));

        PartDefinition body_rotation = wraith.addOrReplaceChild("body_rotation", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body_root = body_rotation.addOrReplaceChild("body_root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = body_root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -15.0F, 0.0F));

        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition upper_torso = torso.addOrReplaceChild("upper_torso", CubeListBuilder.create().texOffs(0, 17).addBox(-4.5F, -6.0F, -2.0F, 9.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        upper_torso.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 43).addBox(-4.0F, -3.0F, 0.0F, 4.0F, 3.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.5F, -4.0F, -1.0F, 0.0F, 0.0F, 0.3927F));

        upper_torso.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 43).mirror().addBox(0.0F, -3.0F, 0.0F, 4.0F, 3.0F, 0.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(4.5F, -4.0F, -1.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition h_head = upper_torso.addOrReplaceChild("h_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -6.0F, 0.0F));

        h_head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 22).addBox(0.0F, -2.0F, 0.0F, 0.0F, 8.0F, 11.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, -8.0F, -4.0F, 0.0F, -0.3927F, 0.0F));

        h_head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(32, 22).addBox(0.0F, -2.0F, 0.0F, 0.0F, 8.0F, 11.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, -8.0F, -4.0F, 0.0F, 0.3927F, 0.0F));

        h_head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(40, 44).addBox(0.0F, -4.5F, -0.5F, 0.0F, 8.0F, 12.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -3.5F, 0.5F, 0.0F, -0.3927F, 0.0F));

        h_head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(40, 44).addBox(0.0F, -4.5F, -0.5F, 0.0F, 8.0F, 12.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -3.5F, 0.5F, 0.0F, 0.3927F, 0.0F));

        h_head.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 37).addBox(0.0F, -3.5F, -2.5F, 0.0F, 10.0F, 17.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -9.5F, 0.5F, 0.3927F, 0.0F, 0.0F));

        PartDefinition h_animhead = h_head.addOrReplaceChild("h_animhead", CubeListBuilder.create(), PartPose.offset(0.0F, -5.75F, -5.0F));

        h_animhead.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(32, 17).mirror().addBox(-3.0F, -5.0F, 0.0F, 16.0F, 10.0F, 0.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(5.0F, 0.0F, 0.75F, 0.0F, 0.0F, -0.7854F));

        h_animhead.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(32, 17).addBox(-13.0F, -5.0F, 0.0F, 16.0F, 10.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.75F, 0.0F, 0.0F, 0.7854F));

        PartDefinition h_left_eye = h_head.addOrReplaceChild("h_left_eye", CubeListBuilder.create().texOffs(32, 41).mirror().addBox(-0.2912F, -3.4434F, -0.05F, 10.0F, 4.0F, -0.1F, CubeDeformation.NONE).mirror(false), PartPose.offset(1.5F, -2.5F, -4.5F));

        h_left_eye.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(22, 16).addBox(-1.0F, -1.0F, 0.0F, 5.0F, 2.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.25F, -0.75F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition h_right_eye = h_head.addOrReplaceChild("h_right_eye", CubeListBuilder.create().texOffs(32, 41).addBox(-9.7088F, -3.4434F, -0.05F, 10.0F, 4.0F, -0.1F, CubeDeformation.NONE), PartPose.offset(-1.5F, -2.5F, -4.5F));

        h_right_eye.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(22, 16).mirror().addBox(-4.0F, -1.0F, 0.0F, 5.0F, 2.0F, 0.0F, CubeDeformation.NONE).mirror(false), PartPose.offsetAndRotation(-0.25F, -0.75F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition left_arm = upper_torso.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(36, 0).addBox(0.0F, -11.0F, 0.0F, 14.0F, 17.0F, 0.0F, CubeDeformation.NONE), PartPose.offset(4.5F, -4.0F, 0.0F));

        PartDefinition left_hand = left_arm.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(50, 45).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(3.5F, 2.0F, 0.0F));

        PartDefinition left_index = left_hand.addOrReplaceChild("left_index", CubeListBuilder.create().texOffs(20, 33).addBox(-1.75F, 0.0F, -0.5F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(1.5F, 6.75F, -1.25F));

        left_index.addOrReplaceChild("left_index_joint", CubeListBuilder.create().texOffs(12, 39).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE), PartPose.offset(0.25F, 4.0F, 0.0F));

        PartDefinition left_index2 = left_hand.addOrReplaceChild("left_index2", CubeListBuilder.create().texOffs(20, 27).addBox(-0.5F, 0.0F, -0.25F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-1.5F, 3.75F, -2.25F));

        left_index2.addOrReplaceChild("left_index_joint2", CubeListBuilder.create().texOffs(12, 34).addBox(-0.25F, -1.0F, -1.25F, 0.0F, 7.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.25F, 4.0F, 0.0F));

        PartDefinition left_pinky = left_hand.addOrReplaceChild("left_pinky", CubeListBuilder.create().texOffs(20, 33).addBox(-1.75F, 0.0F, -0.5F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(1.5F, 6.5F, 1.25F));

        left_pinky.addOrReplaceChild("left_pinky_joint", CubeListBuilder.create().texOffs(12, 39).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE), PartPose.offset(0.25F, 4.0F, 0.0F));

        PartDefinition left_middle_finger = left_hand.addOrReplaceChild("left_middle_finger", CubeListBuilder.create().texOffs(20, 33).addBox(-1.75F, 0.0F, -0.5F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(1.5F, 7.0F, 0.0F));

        left_middle_finger.addOrReplaceChild("left_middle_finger_joint", CubeListBuilder.create().texOffs(12, 39).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE), PartPose.offset(0.25F, 4.0F, 0.0F));

        PartDefinition right_arm = upper_torso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(36, 0).mirror().addBox(-14.0F, -11.0F, 0.0F, 14.0F, 17.0F, 0.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-4.5F, -4.0F, 0.0F));

        PartDefinition right_hand = right_arm.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(50, 45).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-3.5F, 2.0F, 0.0F));

        PartDefinition right_index = right_hand.addOrReplaceChild("right_index", CubeListBuilder.create().texOffs(20, 33).mirror().addBox(-0.25F, 0.0F, -0.5F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-1.5F, 6.75F, -1.25F));

        right_index.addOrReplaceChild("right_index_joint", CubeListBuilder.create().texOffs(12, 39).mirror().addBox(-1.0F, -1.0F, 0.0F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-0.25F, 4.0F, 0.0F));

        PartDefinition right_index2 = right_hand.addOrReplaceChild("right_index2", CubeListBuilder.create().texOffs(20, 27).addBox(-0.5F, 0.0F, -0.25F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(1.5F, 3.75F, -2.25F));

        right_index2.addOrReplaceChild("right_index_joint2", CubeListBuilder.create().texOffs(12, 34).addBox(0.25F, -1.0F, -1.25F, 0.0F, 7.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(-0.25F, 4.0F, 0.0F));

        PartDefinition right_pinky = right_hand.addOrReplaceChild("right_pinky", CubeListBuilder.create().texOffs(20, 33).mirror().addBox(-0.25F, 0.0F, -0.5F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-1.5F, 6.5F, 1.25F));

        right_pinky.addOrReplaceChild("right_pinky_joint", CubeListBuilder.create().texOffs(12, 39).mirror().addBox(-1.0F, -1.0F, 0.0F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-0.25F, 4.0F, 0.0F));

        PartDefinition right_middle_finger = right_hand.addOrReplaceChild("right_middle_finger", CubeListBuilder.create().texOffs(20, 33).mirror().addBox(-0.25F, 0.0F, -0.5F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-1.5F, 7.0F, 0.0F));

        right_middle_finger.addOrReplaceChild("right_middle_finger_joint", CubeListBuilder.create().texOffs(12, 39).mirror().addBox(-1.0F, -1.0F, 0.0F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE).mirror(false), PartPose.offset(-0.25F, 4.0F, 0.0F));

        PartDefinition tail = torso.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 27).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, -1.5F));

        tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, 0.0F, 0.0F, 0.0F, 10.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public void setupAnim(WraithRenderState state) {
        super.setupAnim(state);

        float age = state.ageInTicks;

        if (state.deathTime > 0) {
            wraith.xRot = Mth.lerp(0.1F, wraith.xRot, 0);
            wraith.yRot = Mth.lerp(0.1F, wraith.yRot, 0);
            wraith.zRot = Mth.lerp(0.1F, wraith.zRot, 0);
        } else {
            wraith.xRot += (state.xRot * (Mth.PI / 180)) / 2;
            wraith.yRot += (state.yRot * (Mth.PI / 180)) / 2;
        }

        idleAnimation.apply(state.idleAnimationState, age);
        walkAnimation.apply(state.walkAnimationState, age);
        rightSlashAnimation.apply(state.rightSlashAnimationState, age);
        leftSlashAnimation.apply(state.leftSlashAnimationState, age);
        spinSlashAnimation.apply(state.spinSlashAnimationState, age);
    }
}
