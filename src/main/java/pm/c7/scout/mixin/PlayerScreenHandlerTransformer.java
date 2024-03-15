package pm.c7.scout.mixin;

import org.objectweb.asm.tree.*;
import org.quiltmc.loader.api.QuiltLoader;

import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import static org.objectweb.asm.Opcodes.*;

public class PlayerScreenHandlerTransformer implements ClassNodeTransformer {
	@Override
	public void transform(String name, ClassNode node) {
		var resolver = QuiltLoader.getMappingResolver();
		var namespace = "intermediary";

		var LPlayerScreenHandler = L(slash(name));

		var quickMove = resolver.mapMethodName(namespace, name, "method_7601", "(Lnet/minecraft/class_1657;I)Lnet/minecraft/class_1799;");

		var Slot = slash(resolver.mapClassName(namespace, "net.minecraft.class_1735"));
		var LSlot = L(Slot);

		var DefaultedList = slash(resolver.mapClassName(namespace, "net.minecraft.class_2371"));

		for (var mn : node.methods) {
			// that other comment was a half truth, you can transform mixins :^)
			if (mn.name.endsWith("trinkets$quickMove") || mn.name.equals(quickMove)) {
				for (var insn : mn.instructions) {
					if (insn instanceof VarInsnNode vin) {
						if (vin.getOpcode() == ASTORE && vin.var == 4) {
							if (vin.getPrevious() instanceof TypeInsnNode prevInsn && prevInsn.getOpcode() == CHECKCAST && prevInsn.desc.equals(Slot)) {
								if (prevInsn.getPrevious() instanceof MethodInsnNode prevPrevInsn && prevPrevInsn.getOpcode() == INVOKEVIRTUAL) {
									if(prevPrevInsn.owner.equals(DefaultedList)) {
										LabelNode LnotBag = new LabelNode();
										LabelNode Lend = new LabelNode();
										mn.instructions.insertBefore(prevPrevInsn.getPrevious().getPrevious().getPrevious(), insns(
											ILOAD(2),
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
											IFEQ(LnotBag),
											ILOAD(2),
											ALOAD(0),
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "getBagSlot", "(I" + LPlayerScreenHandler + ")" + LSlot),
											CHECKCAST(Slot),
											ASTORE(vin.var),
											GOTO(Lend),
											LnotBag
										));
										mn.instructions.insert(vin, insns(
											Lend
										));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private String slash(String clazz) {
		return clazz.replaceAll("\\.", "/");
	}

	private String L(String clazz) {
		return "L" + clazz + ";";
	}

	private InsnList insns(AbstractInsnNode... insns) {
		var li = new InsnList();
		for (var i : insns) li.add(i);
		return li;
	}
	private static VarInsnNode ILOAD(int v) {
		return new VarInsnNode(ILOAD, v);
	}
	private static MethodInsnNode INVOKESTATIC(String owner, String name, String desc) {
		return new MethodInsnNode(INVOKESTATIC, owner, name, desc);
	}
	private static JumpInsnNode IFEQ(LabelNode v) {
		return new JumpInsnNode(IFEQ, v);
	}
	private static VarInsnNode ALOAD(int v) {
		return new VarInsnNode(ALOAD, v);
	}
	private static VarInsnNode ASTORE(int v) {
		return new VarInsnNode(ASTORE, v);
	}
	private static TypeInsnNode CHECKCAST(String desc) {
		return new TypeInsnNode(CHECKCAST, desc);
	}
	private static JumpInsnNode GOTO(LabelNode v) {
		return new JumpInsnNode(GOTO, v);
	}
}
