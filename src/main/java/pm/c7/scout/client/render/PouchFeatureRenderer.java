package pm.c7.scout.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.item.BaseBagItem;

public class PouchFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final HeldItemRenderer heldItemRenderer;

	public PouchFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		var leftPouch = ScoutUtil.findBagItem((PlayerEntity) entity, BaseBagItem.BagType.POUCH, false);
		var rightPouch = ScoutUtil.findBagItem((PlayerEntity) entity, BaseBagItem.BagType.POUCH, true);

		if (!leftPouch.isEmpty()) {
			matrices.push();
			((PlayerEntityModel<?>) this.getContextModel()).leftLeg.rotate(matrices);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
			matrices.scale(0.325F, 0.325F, 0.325F);
			matrices.translate(0F, -0.325F, -0.475F);
			this.heldItemRenderer.renderItem(entity, leftPouch, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light);
			matrices.pop();
		}
		if (!rightPouch.isEmpty()) {
			matrices.push();
			((PlayerEntityModel<?>) this.getContextModel()).rightLeg.rotate(matrices);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
			matrices.scale(0.325F, 0.325F, 0.325F);
			matrices.translate(0F, -0.325F, 0.475F);
			this.heldItemRenderer.renderItem(entity, rightPouch, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light);
			matrices.pop();
		}
	}
}
