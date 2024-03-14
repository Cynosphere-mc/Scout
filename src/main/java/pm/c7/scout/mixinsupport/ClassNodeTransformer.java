package pm.c7.scout.mixinsupport;

import org.objectweb.asm.tree.ClassNode;

public interface ClassNodeTransformer {
	void transform(String name, ClassNode node);
}

