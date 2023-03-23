//package mfw.asm.transpack;
//
//import org.objectweb.asm.MethodVisitor;
//
//import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
//
//public class tp_SoundHandler extends TransPack {
//
//	/////////////////////////////////////////////////////////////
//	// ���������Ώ̃N���X���ݒ�
//	public static final String TARGET_CLASS = "net.minecraft.client.audio.SoundHandler";
//	/////////////////////////////////////////////////////////////
//	@Override
//	public String[] getTargetClassName() {
//		return new String[] { TARGET_CLASS };
//	}
//
//	public MethodVisitor MethodAdapt(MethodVisitor mv, String name, String desc)
//	{
//		MethodName = name;
//		MethodDesc = desc;
////		FMLRelaunchLog.info("MFWTransformLog : method list up : "+name+desc);
//
//		/////////////////////////////////////////////////////////////
//		// ���������Ώ̃��\�b�h���ݒ�
//		/////////////////////////////////////////////////////////////
//		String s = "Ljava/lang/String;";
//		String w = "Lnet/minecraft/world/WorldSettings;";
//		if (check("func_110549_a", "onResourceManagerReload", "(Lnet/minecraft/client/resources/IResourceManager;)V")
//			 || check("func_110549_a", "onResourceManagerReload", "(Lbqy;)V"))
//		{
//			/////////////////////////////////////////////////////////////
//			// ���������������e��ǉ�����MethodVisitor���b�v�N���X��Ԃ�
//			/////////////////////////////////////////////////////////////
//			return new MethodVisitor(ASM5, mv){
//
//				@Override
//				public void visitInsn(int opcode)
//			    {
//					if(opcode==RETURN)
//					{
//						super.visitVarInsn(ALOAD, 0);
//						super.visitMethodInsn(INVOKESTATIC, "mfw/sound/SoundManager", "AddExternalSoundLoad", "(Lnet/minecraft/client/audio/SoundHandler;)V", false);
//					}
//					super.visitInsn(opcode);
//			    }
//			};
//		}
//
//		return mv;
//	}
//
//
//	public static String mapMethodName(String owner, String methodName, String desc) {
//		return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(unmapClassName(owner), methodName, desc);
//	}
//	public static String unmapClassName(String name) {
//		return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
//	}
//	public static String mapFieldName(String owner, String methodName, String desc) {
//		return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(unmapClassName(owner), methodName, desc);
//	}
//}
