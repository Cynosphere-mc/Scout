package pm.c7.scout.mixin;

import org.objectweb.asm.tree.*;
import org.quiltmc.loader.api.QuiltLoader;
import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import static org.objectweb.asm.Opcodes.*;

public class ScreenHandlerTransformer implements ClassNodeTransformer {
	@Override
	public void transform(String name, ClassNode node) {
		var internalOnSlotClick = "m_nqfgpzfl";
		var insertItem = "m_jpjdgbxy";

		var PlayerEntity = "net/minecraft/unmapped/C_jzrpycqo";
		var playerScreenHandler = "f_xvlfpipb";

		var PlayerScreenHandler = "net/minecraft/unmapped/C_wgehrbdx";
		var Slot = "net/minecraft/unmapped/C_nhvqfffd";
		var DefaultedList = "net/minecraft/unmapped/C_rnrfftze";

		if (QuiltLoader.isDevelopmentEnvironment()) {
			internalOnSlotClick = "internalOnSlotClick";
			insertItem = "insertItem";

			PlayerEntity = "net/minecraft/entity/player/PlayerEntity";
			playerScreenHandler = "playerScreenHandler";

			PlayerScreenHandler = "net/minecraft/screen/PlayerScreenHandler";
			Slot = "net/minecraft/screen/slot/Slot";
			DefaultedList = "net/minecraft/util/collection/DefaultedList";
		}

		var LPlayerScreenHandler = L(PlayerScreenHandler);
		var LSlot = L(Slot);

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
						} else if (vin.getOpcode() == ASTORE && (vin.var == 6 || vin.var == 7)) {
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
			}/* else if (mn.name.equals(insertItem)) {
				for (var insn : mn.instructions) {
					if (insn instanceof VarInsnNode vin && vin.getOpcode() == ASTORE && vin.var == 7) {
						if (vin.getPrevious() instanceof TypeInsnNode prevInsn && prevInsn.getOpcode() == CHECKCAST && prevInsn.desc.equals(Slot)) {
							if (prevInsn.getPrevious() instanceof MethodInsnNode prevPrevInsn && prevPrevInsn.getOpcode() == INVOKEVIRTUAL) {
								if(prevPrevInsn.owner.equals(DefaultedList)) {
									LabelNode LnotBag = new LabelNode();
									LabelNode LpastSlot = new LabelNode();
									mn.instructions.insertBefore(prevPrevInsn.getPrevious().getPrevious().getPrevious(), insns(
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "getPlayerScreenHandler", "()" + LPlayerScreenHandler),
											ASTORE(20),
											ALOAD(20),
											IFNULL(LpastSlot),
											ILOAD(6),
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
											IFEQ(LnotBag),
											ILOAD(6),
											ALOAD(20),
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "getBagSlot", "(I" + LPlayerScreenHandler + ")" + LSlot),
											CHECKCAST(Slot),
											ASTORE(vin.var),
											LnotBag,
											ILOAD(6),
											INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
											IFNE(LpastSlot)
									));
									mn.instructions.insert(vin, insns(
											LpastSlot
									));
								}
							}
						}
					}
				}
			}*/
		}
	}

	private String L(String clazz) {
		return "L" + clazz + ";";
	}

	private InsnList insns(AbstractInsnNode... insns) {
		var li = new InsnList();
		for (var i : insns) li.add(i);
		return li;
	}
	private static VarInsnNode ILOAD(int var) {
		return new VarInsnNode(ILOAD, var);
	}
	private static MethodInsnNode INVOKESTATIC(String owner, String name, String desc) {
		return new MethodInsnNode(INVOKESTATIC, owner, name, desc);
	}
	private static JumpInsnNode IFNE(LabelNode var) {
		return new JumpInsnNode(IFNE, var);
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
	private static FieldInsnNode GETFIELD(String owner, String name, String desc) {
		return new FieldInsnNode(GETFIELD, owner, name, desc);
	}
	private static JumpInsnNode IFNULL(LabelNode var) {
		return new JumpInsnNode(IFNULL, var);
	}
	private static TypeInsnNode CHECKCAST(String desc) {
		return new TypeInsnNode(CHECKCAST, desc);
	}
	private static JumpInsnNode GOTO(LabelNode var) {
		return new JumpInsnNode(GOTO, var);
	}
}
