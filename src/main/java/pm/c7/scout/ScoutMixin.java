package pm.c7.scout;

import com.unascribed.lib39.core.api.AutoMixin;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import java.util.HashMap;
import java.util.Map;

// "you could also embrace chaos" --Una
// https://git.sleeping.town/unascribed/Yttr/src/branch/1.19.2/src/main/java/com/unascribed/yttr/YttrMixin.java
public class ScoutMixin extends AutoMixin {
	private static final Logger LOGGER = LoggerFactory.getLogger("Scout:MixinPlugin");

	public @interface Transformer {
		Class<? extends ClassNodeTransformer> value();
	}

	private final Map<String, ClassNodeTransformer> transformers = new HashMap<>();

	@Override
	protected boolean shouldMixinBeSkipped(String name, ClassNode node) {
		if (name.endsWith("Transformer")) {
			return true;
		}
		return super.shouldMixinBeSkipped(name, node);
	}

	@Override
	protected boolean shouldAnnotationSkipMixin(String name, AnnotationNode an) {
		if (an.desc.equals("Lpm/c7/scout/ScoutMixin$Transformer;")) {
			var params = decodeAnnotationParams(an);
			Type type = (Type)params.get("value");
			try {
				transformers.put(name, (ClassNodeTransformer) Class.forName(type.getClassName()).newInstance());
			} catch (Exception e) {
				LOGGER.error("Transformer class for mixin {} not found", name, e);
			}
		}
		return super.shouldAnnotationSkipMixin(name, an);
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		transformers.getOrDefault(mixinClassName, (s, cn) -> {}).transform(targetClassName, targetClass);
		super.postApply(targetClassName, targetClass, mixinClassName, mixinInfo);
	}
}
