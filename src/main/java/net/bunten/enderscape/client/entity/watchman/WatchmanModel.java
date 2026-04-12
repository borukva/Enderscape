package net.bunten.enderscape.client.entity.watchman;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class WatchmanModel extends EntityModel<WatchmanRenderState> {

	private final ModelPart modelPart;

	private final KeyframeAnimation idleAnimation;
	private final KeyframeAnimation walkAnimation;
	private final KeyframeAnimation lanternSmackAnimation;
	private final KeyframeAnimation summonWraithsAnimation;
	private final KeyframeAnimation lanternPushAnimation;
	private final KeyframeAnimation deathAnimation;

	public WatchmanModel(ModelPart root) {
		super(root);
		this.modelPart = root.getChild("watchman");

		idleAnimation = WatchmanAnimations.IDLE.bake(root);
		walkAnimation = WatchmanAnimations.WALK.bake(root);
		lanternSmackAnimation = WatchmanAnimations.LANTERN_SMACK.bake(root);
		summonWraithsAnimation = WatchmanAnimations.SUMMON_WRAITHS.bake(root);
		lanternPushAnimation = WatchmanAnimations.LANTERN_PUSH.bake(root);
		deathAnimation = WatchmanAnimations.DEATH.bake(root);
	}

	/**
	 * Blockbench export: many {@link PartDefinition} locals are only used to attach geometry to parents.
	 */
	@SuppressWarnings("unused")
	public static LayerDefinition createLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition watchmanRoot = partdefinition.addOrReplaceChild("watchman", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition vfx = watchmanRoot.addOrReplaceChild("vfx", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystals1 = vfx.addOrReplaceChild("end_crystals1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot1 = end_crystals1.addOrReplaceChild("end_crystal_rot1", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle1 = end_crystal_rot1.addOrReplaceChild("end_crystal_angle1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal1 = end_crystal_angle1.addOrReplaceChild("end_crystal1", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot2 = end_crystals1.addOrReplaceChild("end_crystal_rot2", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle2 = end_crystal_rot2.addOrReplaceChild("end_crystal_angle2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal2 = end_crystal_angle2.addOrReplaceChild("end_crystal2", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot3 = end_crystals1.addOrReplaceChild("end_crystal_rot3", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle3 = end_crystal_rot3.addOrReplaceChild("end_crystal_angle3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal3 = end_crystal_angle3.addOrReplaceChild("end_crystal3", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot4 = end_crystals1.addOrReplaceChild("end_crystal_rot4", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle4 = end_crystal_rot4.addOrReplaceChild("end_crystal_angle4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal4 = end_crystal_angle4.addOrReplaceChild("end_crystal4", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot5 = end_crystals1.addOrReplaceChild("end_crystal_rot5", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle5 = end_crystal_rot5.addOrReplaceChild("end_crystal_angle5", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal5 = end_crystal_angle5.addOrReplaceChild("end_crystal5", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot6 = end_crystals1.addOrReplaceChild("end_crystal_rot6", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle6 = end_crystal_rot6.addOrReplaceChild("end_crystal_angle6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal6 = end_crystal_angle6.addOrReplaceChild("end_crystal6", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot7 = end_crystals1.addOrReplaceChild("end_crystal_rot7", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle7 = end_crystal_rot7.addOrReplaceChild("end_crystal_angle7", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal7 = end_crystal_angle7.addOrReplaceChild("end_crystal7", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot8 = end_crystals1.addOrReplaceChild("end_crystal_rot8", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle8 = end_crystal_rot8.addOrReplaceChild("end_crystal_angle8", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal8 = end_crystal_angle8.addOrReplaceChild("end_crystal8", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystals2 = vfx.addOrReplaceChild("end_crystals2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot9 = end_crystals2.addOrReplaceChild("end_crystal_rot9", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle9 = end_crystal_rot9.addOrReplaceChild("end_crystal_angle9", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal9 = end_crystal_angle9.addOrReplaceChild("end_crystal9", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot10 = end_crystals2.addOrReplaceChild("end_crystal_rot10", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle10 = end_crystal_rot10.addOrReplaceChild("end_crystal_angle10", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal10 = end_crystal_angle10.addOrReplaceChild("end_crystal10", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot11 = end_crystals2.addOrReplaceChild("end_crystal_rot11", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle11 = end_crystal_rot11.addOrReplaceChild("end_crystal_angle11", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal11 = end_crystal_angle11.addOrReplaceChild("end_crystal11", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot12 = end_crystals2.addOrReplaceChild("end_crystal_rot12", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle12 = end_crystal_rot12.addOrReplaceChild("end_crystal_angle12", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal12 = end_crystal_angle12.addOrReplaceChild("end_crystal12", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot13 = end_crystals2.addOrReplaceChild("end_crystal_rot13", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle13 = end_crystal_rot13.addOrReplaceChild("end_crystal_angle13", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal13 = end_crystal_angle13.addOrReplaceChild("end_crystal13", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot14 = end_crystals2.addOrReplaceChild("end_crystal_rot14", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle14 = end_crystal_rot14.addOrReplaceChild("end_crystal_angle14", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal14 = end_crystal_angle14.addOrReplaceChild("end_crystal14", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot15 = end_crystals2.addOrReplaceChild("end_crystal_rot15", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle15 = end_crystal_rot15.addOrReplaceChild("end_crystal_angle15", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal15 = end_crystal_angle15.addOrReplaceChild("end_crystal15", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot16 = end_crystals2.addOrReplaceChild("end_crystal_rot16", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle16 = end_crystal_rot16.addOrReplaceChild("end_crystal_angle16", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal16 = end_crystal_angle16.addOrReplaceChild("end_crystal16", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystals3 = vfx.addOrReplaceChild("end_crystals3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot17 = end_crystals3.addOrReplaceChild("end_crystal_rot17", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle17 = end_crystal_rot17.addOrReplaceChild("end_crystal_angle17", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal17 = end_crystal_angle17.addOrReplaceChild("end_crystal17", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot18 = end_crystals3.addOrReplaceChild("end_crystal_rot18", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle18 = end_crystal_rot18.addOrReplaceChild("end_crystal_angle18", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal18 = end_crystal_angle18.addOrReplaceChild("end_crystal18", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot19 = end_crystals3.addOrReplaceChild("end_crystal_rot19", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle19 = end_crystal_rot19.addOrReplaceChild("end_crystal_angle19", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal19 = end_crystal_angle19.addOrReplaceChild("end_crystal19", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot20 = end_crystals3.addOrReplaceChild("end_crystal_rot20", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle20 = end_crystal_rot20.addOrReplaceChild("end_crystal_angle20", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal20 = end_crystal_angle20.addOrReplaceChild("end_crystal20", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot21 = end_crystals3.addOrReplaceChild("end_crystal_rot21", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle21 = end_crystal_rot21.addOrReplaceChild("end_crystal_angle21", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal21 = end_crystal_angle21.addOrReplaceChild("end_crystal21", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot22 = end_crystals3.addOrReplaceChild("end_crystal_rot22", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle22 = end_crystal_rot22.addOrReplaceChild("end_crystal_angle22", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal22 = end_crystal_angle22.addOrReplaceChild("end_crystal22", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot23 = end_crystals3.addOrReplaceChild("end_crystal_rot23", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle23 = end_crystal_rot23.addOrReplaceChild("end_crystal_angle23", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal23 = end_crystal_angle23.addOrReplaceChild("end_crystal23", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal_rot24 = end_crystals3.addOrReplaceChild("end_crystal_rot24", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

		PartDefinition end_crystal_angle24 = end_crystal_rot24.addOrReplaceChild("end_crystal_angle24", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_crystal24 = end_crystal_angle24.addOrReplaceChild("end_crystal24", CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_flash = vfx.addOrReplaceChild("end_flash", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_flash_rot1 = end_flash.addOrReplaceChild("end_flash_rot1", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 0.0F));

		PartDefinition end_flash1 = end_flash_rot1.addOrReplaceChild("end_flash1", CubeListBuilder.create().texOffs(16, 0).addBox(-1.5F, -8.5F, 0.0F, 3.0F, 17.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(16, -3).addBox(0.0F, -8.5F, -1.5F, 0.0F, 17.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition end_flash_rot2 = end_flash.addOrReplaceChild("end_flash_rot2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition end_flash2 = end_flash_rot2.addOrReplaceChild("end_flash2", CubeListBuilder.create().texOffs(16, 0).addBox(-1.5F, -8.5F, 0.0F, 3.0F, 17.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(16, -3).addBox(0.0F, -8.5F, -1.5F, 0.0F, 17.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition shockwave_pos = vfx.addOrReplaceChild("shockwave_pos", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition shockwave = shockwave_pos.addOrReplaceChild("shockwave", CubeListBuilder.create().texOffs(11, 18).addBox(-3.675F, -8.0F, -1.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).mirror().addBox(8.012F, -3.315F, -1.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(-2, 11).addBox(-3.675F, 7.999F, -1.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(-7.988F, -3.314F, -1.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = shockwave.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 11).addBox(0.001F, -6.627F, -2.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.9887F, -3.3147F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r2 = shockwave.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(-2, 11).addBox(-7.0F, -0.001F, -2.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3034F, 7.9994F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r3 = shockwave.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 11).mirror().addBox(-0.001F, 0.0F, -2.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.0124F, 3.3134F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r4 = shockwave.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(11, 18).addBox(-0.373F, 0.001F, -2.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.325F, -8.001F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition shockwave_pos2 = vfx.addOrReplaceChild("shockwave_pos2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition shockwave2 = shockwave_pos2.addOrReplaceChild("shockwave2", CubeListBuilder.create().texOffs(11, 18).addBox(-3.675F, -8.0F, -1.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).mirror().addBox(8.012F, -3.315F, -1.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(-2, 11).addBox(-3.675F, 7.999F, -1.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(-7.988F, -3.314F, -1.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r5 = shockwave2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 11).addBox(0.001F, -6.627F, -2.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.9887F, -3.3147F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r6 = shockwave2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(-2, 11).addBox(-7.0F, -0.001F, -2.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3034F, 7.9994F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r7 = shockwave2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 11).mirror().addBox(-0.001F, 0.0F, -2.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.0124F, 3.3134F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r8 = shockwave2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(11, 18).addBox(-0.373F, 0.001F, -2.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.325F, -8.001F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition shockwave_pos3 = vfx.addOrReplaceChild("shockwave_pos3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition shockwave3 = shockwave_pos3.addOrReplaceChild("shockwave3", CubeListBuilder.create().texOffs(11, 18).addBox(-3.675F, -8.0F, -1.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).mirror().addBox(8.012F, -3.315F, -1.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(-2, 11).addBox(-3.675F, 7.999F, -1.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(-7.988F, -3.314F, -1.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r9 = shockwave3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 11).addBox(0.001F, -6.627F, -2.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.9887F, -3.3147F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r10 = shockwave3.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(-2, 11).addBox(-7.0F, -0.001F, -2.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3034F, 7.9994F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r11 = shockwave3.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 11).mirror().addBox(-0.001F, 0.0F, -2.0F, 0.0F, 6.627F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(8.0124F, 3.3134F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r12 = shockwave3.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(11, 18).addBox(-0.373F, 0.001F, -2.0F, 7.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.325F, -8.001F, 1.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition body_root = watchmanRoot.addOrReplaceChild("body_root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = body_root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -28.0F, 0.0F));

		PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(44, 50).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition h_head = torso.addOrReplaceChild("h_head", CubeListBuilder.create().texOffs(36, 15).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition cube_r13 = h_head.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(32, 0).addBox(-10.0F, -6.0F, 1.0F, 18.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.6257F, -10.9092F, -3.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r14 = h_head.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(48, 33).mirror().addBox(-8.0F, -2.0F, 0.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, -6.0F, -3.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition cube_r15 = h_head.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-8.0F, -6.0F, 1.0F, 18.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(10.6257F, -10.9092F, -3.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r16 = h_head.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(48, 33).addBox(0.0F, -2.0F, 0.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -6.0F, -3.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition h_left_eyelid = h_head.addOrReplaceChild("h_left_eyelid", CubeListBuilder.create().texOffs(60, 20).addBox(-1.0F, 0.75F, -0.75F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.75F)), PartPose.offset(2.5F, -5.25F, -2.75F));

		PartDefinition h_right_eyelid = h_head.addOrReplaceChild("h_right_eyelid", CubeListBuilder.create().texOffs(60, 20).mirror().addBox(-1.0F, 0.75F, -0.75F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.75F)).mirror(false), PartPose.offset(-2.5F, -5.25F, -2.75F));

		PartDefinition h_left_eye_glare = h_head.addOrReplaceChild("h_left_eye_glare", CubeListBuilder.create().texOffs(28, 15).addBox(0.0F, -2.0F, 0.0F, 8.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -2.0F, -4.025F));

		PartDefinition h_right_eye_glare = h_head.addOrReplaceChild("h_right_eye_glare", CubeListBuilder.create().texOffs(28, 15).mirror().addBox(-8.0F, -2.0F, 0.0F, 8.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, -2.0F, -4.025F));

		PartDefinition left_arm = torso.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(4.0F, -11.0F, 0.0F));

		PartDefinition left_arm_rot = left_arm.addOrReplaceChild("left_arm_rot", CubeListBuilder.create().texOffs(36, 67).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(1.0F, -1.0F, 0.0F));

		PartDefinition left_elbow = left_arm_rot.addOrReplaceChild("left_elbow", CubeListBuilder.create().texOffs(44, 67).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, 1.0F));

		PartDefinition lantern = left_elbow.addOrReplaceChild("lantern", CubeListBuilder.create().texOffs(1, 18).addBox(-1.5F, 2.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.5F, 0.5F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(28, 18).addBox(-1.5F, 7.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(7, 0).addBox(1.5F, 3.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(7, 0).addBox(-2.5F, 3.0F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, -1.0F));

		PartDefinition end_crystal_lantern = lantern.addOrReplaceChild("end_crystal_lantern", CubeListBuilder.create().texOffs(0, 3).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.5F, 0.0F));

		PartDefinition end_crystal_lantern2 = lantern.addOrReplaceChild("end_crystal_lantern2", CubeListBuilder.create().texOffs(0, 7).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.5F, 0.0F));

		PartDefinition right_arm = torso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(36, 67).mirror().addBox(-2.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-4.0F, -11.0F, 0.0F));

		PartDefinition right_elbow = right_arm.addOrReplaceChild("right_elbow", CubeListBuilder.create().texOffs(44, 67).mirror().addBox(-1.0F, 0.0F, -2.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-1.0F, 13.0F, 1.0F));

		PartDefinition left_shoulder_plate = torso.addOrReplaceChild("left_shoulder_plate", CubeListBuilder.create(), PartPose.offset(5.5F, -11.5F, 0.0F));

		PartDefinition cube_r17 = left_shoulder_plate.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(52, 38).mirror().addBox(-1.5F, -2.5F, -2.5F, 3.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition right_shoulder_plate = torso.addOrReplaceChild("right_shoulder_plate", CubeListBuilder.create(), PartPose.offset(-5.5F, -11.5F, 0.0F));

		PartDefinition cube_r18 = right_shoulder_plate.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(52, 38).addBox(-1.5F, -2.5F, -2.5F, 3.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(60, 67).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition right_knee = right_leg.addOrReplaceChild("right_knee", CubeListBuilder.create().texOffs(52, 67).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, -1.0F));

		PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(60, 67).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition left_knee = left_leg.addOrReplaceChild("left_knee", CubeListBuilder.create().texOffs(52, 67).mirror().addBox(-1.0F, 0.0F, 0.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 14.0F, -1.0F));

		PartDefinition capes = body_root.addOrReplaceChild("capes", CubeListBuilder.create(), PartPose.offset(0.0F, -40.0F, 2.0F));

		PartDefinition cape = capes.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(20, 34).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 20.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition lower_cape = cape.addOrReplaceChild("lower_cape", CubeListBuilder.create().texOffs(11, 57).addBox(-5.5F, 0.0F, -0.5F, 11.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition right_cape = capes.addOrReplaceChild("right_cape", CubeListBuilder.create().texOffs(0, 23).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 2.0F, -2.0F));

		PartDefinition right_lower_cape = right_cape.addOrReplaceChild("right_lower_cape", CubeListBuilder.create().texOffs(0, 45).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 20.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, -2.0F));

		PartDefinition left_cape = capes.addOrReplaceChild("left_cape", CubeListBuilder.create().texOffs(0, 23).mirror().addBox(-1.0F, 0.0F, -2.0F, 2.0F, 18.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.0F, 2.0F, -2.0F));

		PartDefinition left_lower_cape = left_cape.addOrReplaceChild("left_lower_cape", CubeListBuilder.create().texOffs(0, 45).mirror().addBox(-0.5F, 0.0F, 0.0F, 1.0F, 20.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 18.0F, -2.0F));

		PartDefinition ground_cape = capes.addOrReplaceChild("ground_cape", CubeListBuilder.create().texOffs(53, 0).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 39.75F, -1.5F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(WatchmanRenderState state) {
		super.setupAnim(state);
		float age = state.ageInTicks;

		if (state.deathTime > 0) {
			modelPart.xRot = Mth.lerp(0.1F, modelPart.xRot, 0);
			modelPart.yRot = Mth.lerp(0.1F, modelPart.yRot, 0);
			modelPart.zRot = Mth.lerp(0.1F, modelPart.zRot, 0);
			deathAnimation.apply(state.deathAnimationState, age);
			return;
		}

		modelPart.xRot += (state.xRot * (Mth.PI / 180)) / 2;
		modelPart.yRot += (state.yRot * (Mth.PI / 180)) / 2;

		idleAnimation.apply(state.idleAnimationState, age);
		walkAnimation.apply(state.walkAnimationState, age);
		lanternSmackAnimation.apply(state.lanternSmackAnimationState, age);
		summonWraithsAnimation.apply(state.summonWraithsAnimationState, age);
		lanternPushAnimation.apply(state.lanternPushAnimationState, age);
	}
}