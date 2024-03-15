package pm.c7.scout.mixin;

import org.objectweb.asm.tree.*;
import org.quiltmc.loader.api.QuiltLoader;

import pm.c7.scout.mixinsupport.ClassNodeTransformer;

import static org.objectweb.asm.Opcodes.*;

public class ScreenHandlerTransformer implements ClassNodeTransformer {
	//private static final Logger LOGGER = LoggerFactory.getLogger("Scout:ScreenHandlerTransformer");

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
		//var LDefaultedList = L(DefaultedList);

		//var slots = resolver.mapFieldName(namespace, name, "field_7761", LDefaultedList);

		int ordinal = 0;

		for (var mn : node.methods) {
			if (mn.name.equals(internalOnSlotClick)) {
				for (var insn : mn.instructions) {
					if (insn instanceof VarInsnNode vin) {
						if (vin.getOpcode() == ILOAD && vin.var == 1) {
							if (insn.getNext() instanceof JumpInsnNode nextInsn && nextInsn.getOpcode() == IFGE) {
								// `if (slotIndex < 0) return` -> `if (slotIndex < 0 && !isBagSlot(slotIndex)) return`
								var jumpTo = nextInsn.label;
								mn.instructions.insert(nextInsn, insns(
									ILOAD(1),
									INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
									IFNE(jumpTo)
								));
							} else if (insn.getPrevious() instanceof JumpInsnNode prevInsn && prevInsn.getOpcode() == IFEQ && insn.getNext() instanceof JumpInsnNode nextInsn && nextInsn.getOpcode() == IFLT) {
								// skip creative duping, it uses same signature and i dont feel like overcomplicating the check
								if (ordinal != 1) {
									ordinal++;
									continue;
								}

								// fix dropping from bags not working
								LabelNode Lcheck = new LabelNode();
								nextInsn.label = Lcheck;
								nextInsn.setOpcode(IFGE);
								mn.instructions.insert(nextInsn, insns(
									ILOAD(1),
									INVOKESTATIC("pm/c7/scout/ScoutUtil", "isBagSlot", "(I)Z"),
									IFNE(Lcheck),
									RETURN(),
									Lcheck
								));
							}
						} else if (vin.getOpcode() == ASTORE && (vin.var == 6 || vin.var == 7)) {
							// fix most but not all calls to `slots.get`
							if (vin.getPrevious() instanceof TypeInsnNode prevInsn && prevInsn.getOpcode() == CHECKCAST && prevInsn.desc.equals(Slot)) {
								if (prevInsn.getPrevious() instanceof MethodInsnNode prevPrevInsn && prevPrevInsn.getOpcode() == INVOKEVIRTUAL) {
									if(prevPrevInsn.owner.equals(DefaultedList)) {
										var insertPoint = prevPrevInsn.getPrevious();

										if (insertPoint.getOpcode() == ILOAD) {
											var beforeInsert = insertPoint.getPrevious();

											if (beforeInsert != null && beforeInsert.getPrevious() != null){
												if (beforeInsert.getOpcode() == GETFIELD && beforeInsert.getPrevious().getOpcode() == ALOAD) {
													insertPoint = beforeInsert.getPrevious();
												} else {
													continue;
												}
											}

											LabelNode LnotBag = new LabelNode();
											LabelNode Lend = (LabelNode) vin.getNext();

											mn.instructions.insertBefore(insertPoint, insns(
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
										}
									}
								}
							}
						}
					}
				}
			} else if (mn.name.endsWith("debugify$handleCtrlQCrafting")) { // ughghghhghghghghgh
				for (var insn : mn.instructions) {
					if (insn instanceof VarInsnNode vin && vin.getOpcode() == ASTORE && vin.var == 6) {
						if (vin.getPrevious() instanceof TypeInsnNode prevInsn && prevInsn.getOpcode() == CHECKCAST && prevInsn.desc.equals(Slot)) {
							if (prevInsn.getPrevious() instanceof MethodInsnNode prevPrevInsn && prevPrevInsn.getOpcode() == INVOKEVIRTUAL) {
								if(prevPrevInsn.owner.equals(DefaultedList)) {
									var insertPoint = prevPrevInsn.getPrevious();

									if (insertPoint.getOpcode() == ILOAD) {
										var beforeInsert = insertPoint.getPrevious();

										if (beforeInsert != null && beforeInsert.getPrevious() != null && beforeInsert.getOpcode() == GETFIELD && beforeInsert.getPrevious().getOpcode() == ALOAD) {
											insertPoint = beforeInsert.getPrevious();
										}

										LabelNode LnotBag = new LabelNode();
										LabelNode Lend = (LabelNode) vin.getNext();

										mn.instructions.insertBefore(insertPoint, insns(
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
									}
								}
							}
						}
					}
				}
			}
		}
	}

	// debug, keeping here for future use
	/*private static List<Field> allOpcodes = Arrays.asList(Opcodes.class.getFields());

	private String getOpcodeName(int v) {
		Optional<Field> opcode = allOpcodes.stream()
		.filter(f -> f.getType() == int.class)
		.filter(f -> {
			if (f.getName().startsWith("F_")) {
				return f.getName().equals("F_NEW");
			} else {
				return !f.getName().startsWith("V")
				&& !f.getName().startsWith("ASM")
				&& !f.getName().startsWith("SOURCE_")
				&& !f.getName().startsWith("ACC_")
				&& !f.getName().startsWith("H_")
				&& !f.getName().startsWith("T_");
			}
		})
		.filter(f -> {
			try {
				var field = f.get(Opcodes.class);
				//LOGGER.info("{} {} | {}", f.getName(), f.getType(), field);
				return field.equals(v);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		})
		.findFirst();

		if (opcode.isEmpty()) {
			return "<unknown: " + String.valueOf(v) + ">";
		}

		return opcode.get().getName();
	}

	private void printNode(AbstractInsnNode node) {
		var name = getOpcodeName(node.getOpcode());
		String val = "";

		if (node instanceof VarInsnNode vin) {
			val = String.valueOf(vin.var);
		} else if(node instanceof FieldInsnNode fin) {
			val = fin.owner + "." + fin.name + ":" + fin.desc;
		} else if(node instanceof MethodInsnNode min) {
			val = min.owner + "." + min.name + ":" + min.desc;
		} else if (node instanceof TypeInsnNode tin) {
			val = tin.desc;
		} else if (node instanceof JumpInsnNode jin) {
			val = jin.label.toString();
		} else if (node instanceof LabelNode label) {
			name = "L";
			val = label.toString();
		}

		LOGGER.info("{} {}", name, val);
	}

	private void dumpInstructions(InsnList insns, int start, int end) {
		for (var i = start; i < end + 1; i++) {
			printNode(insns.get(i));
		}
	}*/

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
	private static TypeInsnNode CHECKCAST(String desc) {
		return new TypeInsnNode(CHECKCAST, desc);
	}
	private static JumpInsnNode GOTO(LabelNode v) {
		return new JumpInsnNode(GOTO, v);
	}
	private static InsnNode RETURN() {
		return new InsnNode(RETURN);
	}
}
