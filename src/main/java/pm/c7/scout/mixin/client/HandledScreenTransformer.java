package pm.c7.scout.mixin.client;

import org.objectweb.asm.tree.*;
import org.quiltmc.loader.api.QuiltLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import static org.objectweb.asm.Opcodes.*;

public class HandledScreenTransformer implements ClassNodeTransformer {
	private static Logger LOGGER = LoggerFactory.getLogger("Scout:HandledScreenTransformer");
	@Override
	public void transform(String name, ClassNode node) {
		var drawSlot = "m_zioswvnu";
		var Slot = "net/minecraft/unmapped/C_nhvqfffd";
		var y = "f_tttqoodj";

		if (QuiltLoader.isDevelopmentEnvironment()) {
			drawSlot = "drawSlot";
			Slot = "net/minecraft/screen/slot/Slot";
			y = "y";
		}

		for (var mn : node.methods) {
			if (mn.name.equals(drawSlot)) {
				for (var insn : mn.instructions) {
					if (insn instanceof FieldInsnNode fin) {
						if (fin.getOpcode() == GETFIELD) {
							if(fin.owner.equals(Slot) && fin.name.equals(y)) {
								if (fin.getNext() instanceof VarInsnNode vin && vin.getOpcode() == ISTORE) {
									LabelNode LnotBag = new LabelNode();
									int SAFE_REGISTER = 20;
									mn.instructions.insert(vin, insns(
											ALOAD(2),
											INSTANCEOF("pm/c7/scout/screen/BagSlot"),
											IFEQ(LnotBag),
											ALOAD(2),
											CHECKCAST("pm/c7/scout/screen/BagSlot"),
											ASTORE(SAFE_REGISTER),
											ALOAD(SAFE_REGISTER),
											INVOKEVIRTUAL("pm/c7/scout/screen/BagSlot", "getX", "()I"),
											ISTORE(vin.var - 1),
											ALOAD(SAFE_REGISTER),
											INVOKEVIRTUAL("pm/c7/scout/screen/BagSlot", "getY", "()I"),
											ISTORE(vin.var),
											LnotBag
									));
								}
							}
						}
					}
				}
			}
		}
	}

	private InsnList insns(AbstractInsnNode... insns) {
		var li = new InsnList();
		for (var i : insns) li.add(i);
		return li;
	}
	private static JumpInsnNode IFEQ(LabelNode var) {
		return new JumpInsnNode(IFEQ, var);
	}
	private static VarInsnNode ALOAD(int var) {
		return new VarInsnNode(ALOAD, var);
	}
	private static VarInsnNode ASTORE(int var) {
		return new VarInsnNode(ASTORE, var);
	}
	private static TypeInsnNode INSTANCEOF(String desc) {
		return new TypeInsnNode(INSTANCEOF, desc);
	}
	private static TypeInsnNode CHECKCAST(String desc) {
		return new TypeInsnNode(CHECKCAST, desc);
	}
	private static MethodInsnNode INVOKEVIRTUAL(String owner, String name, String desc) {
		return new MethodInsnNode(INVOKEVIRTUAL, owner, name, desc);
	}
	private static VarInsnNode ISTORE(int var) {
		return new VarInsnNode(ISTORE, var);
	}
}
