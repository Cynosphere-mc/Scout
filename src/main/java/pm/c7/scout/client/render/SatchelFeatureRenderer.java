package pm.c7.scout.client.render;

import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import pm.c7.scout.ScoutUtil;
import pm.c7.scout.client.model.SatchelModel;
import pm.c7.scout.item.BaseBagItem;

public class SatchelFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SATCHEL_TEXTURE = new Identifier(ScoutUtil.MOD_ID, "textures/entity/satchel.png");
	private static final Identifier UPGRADED_SATCHEL_TEXTURE = new Identifier(ScoutUtil.MOD_ID, "textures/entity/upgraded_satchel.png");

	private final SatchelModel<T> satchel;

	public SatchelFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
		TexturedModelData modelData = SatchelModel.getTexturedModelData();
		this.satchel = new SatchelModel<>(modelData.createModel());
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		var satchel = ScoutUtil.findBagItem((PlayerEntity) entity, BaseBagItem.BagType.SATCHEL, false);

		if (!satchel.isEmpty()) {
			BaseBagItem satchelItem = (BaseBagItem) satchel.getItem();
			var texture = SATCHEL_TEXTURE;
			if (satchelItem.getSlotCount() == ScoutUtil.MAX_SATCHEL_SLOTS)
				texture = UPGRADED_SATCHEL_TEXTURE;

			matrices.push();
			((PlayerEntityModel<?>) this.getContextModel()).body.rotate(matrices);
			this.getContextModel().copyStateTo(this.satchel);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(
					vertexConsumers, RenderLayer.getArmorCutoutNoCull(texture), false, satchel.hasGlint()
			);
			this.satchel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
			matrices.pop();
		}
	}
}
