package mfw.asm;

import mochisystems._mc._1_7_10.easyasm.*;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class MFWTransMap extends EasyAsm{
	
	@Override
	protected void MakeMap()
	{
		try {
	//		add(new tp_EntityRenderer());
	//		add(new tp_EntityRenderer());
	//		add(new tp_EntityRenderer());
	//		add(new tp_EntityRenderer());
	//		add(new tp_EntityRenderer());
			System.out.println("test");
			
//			add(new ClassAdapter("net.minecraft.client.renderer.EntityRenderer"))
//				.add(new MethodAdapter_Logger())
//					.add(new MethodAdapter("func_78471_a", "renderWorld", "(FJ)V"));

			isMapped = true;
		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {MFWTransMap.class.getName()};
	}
	@Override
	public String getModContainerClass() {
		return modContainer.class.getName();
	}

	@Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        classLoader.registerTransformer(MFWTransMap.class.getName());
    }
}
