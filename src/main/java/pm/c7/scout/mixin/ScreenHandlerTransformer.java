package pm.c7.scout.mixin;

import org.objectweb.asm.tree.*;
import org.quiltmc.loader.api.QuiltLoader;

import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import static org.objectweb.asm.Opcodes.*;

public class ScreenHandlerTransformer implements ClassNodeTransformer {
	@Override
	public void transform(String name, ClassNode node) {
		var resolver = QuiltLoader.getMappingResolver();
		var namespace = "intermediary";

		var internalOnSlotClick = resolver.mapMethodName(namespace, name, "method_30010", "(IILnet/minecraft/class_1713;Lnet/minecraft/class_1657;)V");

		var PlayerEntity = resolver.mapClassName(namespace, "net.minecraft.class_1657");
		var PlayerScreenHandler = slash(resolver.mapClassName(namespace, "net.minecraft.class_1723"));
		var LPlayerScreenHandler = L(PlayerScreenHandler);
		String playerScreenHandler = resolver.mapFieldName(namespace, PlayerEntity, "field_7498", LPlayerScreenHandler);
		PlayerEntity = slash(PlayerEntity);

		var Slot = slash(resolver.mapClassName(namespace, "net.minecraft.class_1735"));
		var LSlot = L(Slot);

		var DefaultedList = slash(resolver.mapClassName(namespace, "net.minecraft.class_2371"));

		for (var mn : node.methods) {
			if (mn.name.equals(internalOnSlotClick)) {
				for (var insn : mn.instructions) {
					if (insn instanceof VarInsnNode vin) {
						if (vin.getOpcode() == ILOAD && vin.var == 1) {
							if (insn.getNext() instanceof JumpInsnNode nextInsn && nextInsn.getOpcode() == IFGE) {
								var jumpTo = nextInsn.label;
								mn.instructions.insert(nextInsn, insns(
									ILOAD(1),
									INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
									IFNE(jumpTo)
								));
							}
						} else if (vin.getOpcode() == ASTORE && vin.var == 7) {
							if (vin.getPrevious() instanceof TypeInsnNode prevInsn && prevInsn.getOpcode() == CHECKCAST && prevInsn.desc.equals(Slot)) {
								if (prevInsn.getPrevious() instanceof MethodInsnNode prevPrevInsn && prevPrevInsn.getOpcode() == INVOKEVIRTUAL) {
									if(prevPrevInsn.owner.equals(DefaultedList)) {
										LabelNode LnotBag = new LabelNode();
										LabelNode Lend = new LabelNode();
										mn.instructions.insertBefore(prevPrevInsn.getPrevious().getPrevious().getPrevious(), insns(
											ILOAD(1),
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
											IFEQ(LnotBag),
											ILOAD(1),
											ALOAD(4),
											GETFIELD(PlayerEntity, playerScreenHandler, LPlayerScreenHandler),
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
	private static JumpInsnNode IFNE(LabelNode v) {
		return new JumpInsnNode(IFNE, v);
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
	private static FieldInsnNode GETFIELD(String owner, String name, String desc) {
		return new FieldInsnNode(GETFIELD, owner, name, desc);
	}
	private static JumpInsnNode IFNULL(LabelNode v) {
		return new JumpInsnNode(IFNULL, v);
	}
	private static TypeInsnNode CHECKCAST(String desc) {
		return new TypeInsnNode(CHECKCAST, desc);
	}
	private static JumpInsnNode GOTO(LabelNode v) {
		return new JumpInsnNode(GOTO, v);
	}
}
