package mfw.asm;

import static org.objectweb.asm.Opcodes.ASM5;

import mfw.asm.transpack.TransPack;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class _2_classAdapter extends ClassVisitor {
	
	private TransPack pack;
	
	public _2_classAdapter(ClassVisitor cv, TransPack pack)
	{
		super(ASM5, cv);
		this.pack = pack;
	}

	@Override
	public void visit(int version, int access, String name, String signature, 
			String superName, String[] interfaces)
	{
		//if(name.contains("Minecraft"))
//		{
//			FMLRelaunchLog.info("visit : "+version+" : "+access
//					+" : "+name+" : "+signature
//					+" : "+superName+" : "+interfaces);
//		}
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		FieldVisitor fv = super.visitField(access, name, desc, signature, value);
		return pack.FieldAdapt(fv, access, name, desc, value);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		//FMLRelaunchLog.info("MFWTransformLog : visitMethod : name'%s%s'", name, desc);
		
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		return pack.MethodAdapt(mv, name, desc);
	}
	
	public void visitEnd()
	{
		//if(cv != null && cv instanceof ClassWriter)pack.addMethod((ClassWriter)cv);
		super.visitEnd();
	}
}