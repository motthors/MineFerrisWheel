//package mfw.asm.transpack;
//
//import org.objectweb.asm.MethodVisitor;
//import org.objectweb.asm.Opcodes;
//
//import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
//
//public class tp_EntityRenderer extends TransPack {
//
//	/////////////////////////////////////////////////////////////
//	// ���������Ώ̃N���X���ݒ�
//	public static final String TARGET_CLASS = "net.minecraft.client.renderer.EntityRenderer";
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
////		FMLRelaunchLog.info("TransformLog : method list up : "+name+desc);
//
//		/////////////////////////////////////////////////////////////
//		// ���������Ώ̃��\�b�h���ݒ�
//		/////////////////////////////////////////////////////////////
//		String s = "Ljava/lang/String;";
//		String w = "Lnet/minecraft/world/WorldSettings;";
//		if (check("func_78471_a", "renderWorld", "(FJ)V"))
//		{
//			/////////////////////////////////////////////////////////////
//			// ���������������e��ǉ�����MethodVisitor���b�v�N���X��Ԃ�
//			/////////////////////////////////////////////////////////////
//			return new MethodVisitor(ASM5, mv){
//				public int MethodCount = 0;
//				private static final String TARGET_CLASS_NAME = "net/minecraft/client/renderer/RenderGlobal";//bma
//				private static final String TARGET_TRANSFORMED_NAME = "renderAllSortedRenderers";
//				private static final String TARGET_Orginal_NAME = "sortAndRender";
//				private static final String TARGET_DESC = "(Lnet/minecraft/entity/EntityLivingBase;ID)I";
//				private static final String TARGET_DESC_T = "(ID)I";
//				@Override
//				public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
//			    {
////					FMLRelaunchLog.info("MFWTransformLog : visitMethodInsn : name'%s.%s%s'", owner, mapMethodName(owner, name, desc), desc);
//					boolean ownerflag = false;
//					boolean nameflag = false;
//					ownerflag |= TARGET_CLASS_NAME.equals(owner);
//					ownerflag |= "bma".equals(owner);
//					nameflag |= TARGET_TRANSFORMED_NAME.equals(mapMethodName(owner, name, desc));
//					nameflag |= TARGET_Orginal_NAME.equals(mapMethodName(owner, name, desc));
//					if (ownerflag
//							&& nameflag
//							&& (TARGET_DESC.equals(desc) || (TARGET_DESC_T.equals(desc))))
//					{
//						MethodCount += 1;
//						switch(MethodCount)
//						{
//						case 2:
//						case 3:
//						case 4:
//
////							File file = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()+"/MFWFiles/config.txt");
////							if(file.exists()==false)
////							{
////								try {
////									file.createNewFile();
////								} catch (IOException e1) {}
////							}
////							try {
////								FileReader reader = new FileReader(file);
////								BufferedReader br = new BufferedReader(reader);
////								List<String> lines = new ArrayList<String>();
////							} catch (FileNotFoundException e) {
////
////							}
//
//
////							FMLRelaunchLog.info("TransformLog : OK");
//							this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
////							if(Loader.isModLoaded("shadersmod"))
//								//this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "shadersmodcore/client/Shaders", "beginWater", "()V", false);
//							this.mv.visitVarInsn(Opcodes.FLOAD, 1);
//							this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "mfw/asm/renderPass1Hook", "draw", "(F)V", false);
////							if(Loader.isModLoaded("shadersmod"))
//								//this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "shadersmodcore/client/Shaders", "endWater", "()V", false);
//							//FMLRelaunchLog.info("MFWTransformLog : succeed transforming");
//							return;
//						}
//					}
//					super.visitMethodInsn(opcode, owner, name, desc, itf);
//			    }
//			};
//		}
//
//		if (check("func_78479_a", "setupCameraTransform", "(FI)V"))
//		{
//			return new MethodVisitor(ASM5, mv){
//				public int MethodCount = 0;
//
//				@Override
//				public void visitFieldInsn(int opcode, String owner, String name, String desc) {
//					//FMLRelaunchLog.info("MFWTransformLog : renderdist : visitFieldInsn : "+name +".."+mapFieldName(owner, name, desc)+desc);
//					boolean flag = false;
//					flag |= "farPlaneDistance".equals(mapFieldName(owner, name, desc));
//					flag |= "field_78530_s".equals(mapFieldName(owner, name, desc));
//					if(flag && opcode == Opcodes.PUTFIELD && MethodCount==0)
//					{
////						FMLRelaunchLog.info("TransformLog : OK");
//						MethodCount += 1;
//						mv.visitFieldInsn(Opcodes.GETSTATIC, "mfw/_mc/_1_7_10/_core/MFW_Command", "renderDistRatio", "F");
//						mv.visitInsn(Opcodes.FMUL);
//	//					FMLRelaunchLog.info("MFWTransformLog : renderdist : visitFieldInsn : transform renderdist");
//					}
//					super.visitFieldInsn(opcode, owner, name, desc);
//				}
//			};
//		}
//
//		return mv;
//	}
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
