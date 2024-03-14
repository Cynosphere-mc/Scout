package pm.c7.scout.client.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public class SatchelModel<T extends LivingEntity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
	private final ModelPart satchel;
	private final ModelPart strap;

	public SatchelModel(ModelPart root) {
		super();
		this.root = root;
		this.satchel = root.getChild("satchel");
		this.strap = this.satchel.getChild("strap");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();

		ModelPartData satchel = root.addChild("satchel", ModelPartBuilder.create().uv(10, 0).cuboid(-6.0F, -12.0F, -2.5F, 2.0F, 3.0F, 5.0F, new Dilation(0.275F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData strap = satchel.addChild("strap", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -13.0F, -2.0F, 1.0F, 14.0F, 4.0F, new Dilation(0.275F)), ModelTransform.of(-3.0F, -13.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		satchel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}
