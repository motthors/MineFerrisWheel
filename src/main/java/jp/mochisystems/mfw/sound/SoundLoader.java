package jp.mochisystems.mfw.sound;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import jp.mochisystems.mfw._mc._core.MFW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.io.IOUtils;

public class SoundLoader {

	public static SoundLoader Instance = new SoundLoader();

	public static final String soundDomain = MFW.MODID + "soundManager";
	public ExternalResourceLoader soundloader;
	public ArrayList<String> sounds = new ArrayList<>();
	public ArrayList<SoundEvent> events = new ArrayList<>();

	public void Load() {
		sounds.clear();
		events.clear();
		sounds.add("(stop)");
		ResourcePackRepository rpr = Minecraft.getMinecraft().getResourcePackRepository();
		List<ResourcePackRepository.Entry> repos = rpr.getRepositoryEntries();
		for (ResourcePackRepository.Entry repo : repos) {
			InputStream stream = null;
			try {
				IResourcePack pack = repo.getResourcePack();
				boolean isPack = pack.resourceExists(new ResourceLocation("mfwsound", "sounds.json"));
				if (!isPack) continue;
				stream = pack.getInputStream(new ResourceLocation("mfwsound", "sounds.json"));
//				BufferedInputStream bis = new BufferedInputStream(stream);
//				ByteArrayOutputStream buf = new ByteArrayOutputStream();
//				for (int result = bis.read(); result != -1; result = bis.read()) {
//					buf.write((byte) result);
//				}
//				String jsonStr = buf.toString("UTF-8");
//				Gson gson = new Gson();
//				JsonSoundList list = gson.fromJson(jsonStr, JsonSoundList.class);

				Gson gson = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
				Map raw = JsonUtils.fromJson(gson, new InputStreamReader(stream, StandardCharsets.UTF_8), TYPE);
				Map<String, SoundList> list = (Map<String, SoundList>)raw;
				if(list == null) continue;
				sounds.addAll(Arrays.asList(list.keySet().toArray(new String[0])));
				for(String sound : list.keySet()){
					events.add(new SoundEvent(new ResourceLocation("mfwsound:"+sound)).setRegistryName(new ResourceLocation("mfwsound", sound)));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				if(stream!=null) IOUtils.closeQuietly(stream);
			}
		}


	}

	public String getSoundDomain(int idx)
	{
		if(idx >= sounds.size())idx = 0;
		if(idx < 0)idx = 0;
		return sounds.get(idx);
	}
	public SoundEvent getSoundEvent(int idx)
	{
		if(idx >= events.size())idx = 1;
		if(idx < 1) idx = 1;
		return events.get(idx-1);
	}


//	private final Gson gson = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
//    public void AddExternalSoundLoad(SoundHandler soundhandler)
//	{
//		try {
//			soundloader = new ExternalResourceLoader(soundDomain, Minecraft.getMinecraft().mcDataDir.getPath());
//
////					ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "mcResourceManager", "field_110451_am");
////	        resourcemanager.reloadResources(soundloader);//todo dousiyo
//
//			Map map = (Map)gson.fromJson(new InputStreamReader(new FileInputStream("./MFWSounds/sounds.json")), paramtype);
//            Iterator iterator2 = map.entrySet().iterator();
//
//
//            while (iterator2.hasNext())
//            {
//                Entry entry = (Entry)iterator2.next();
////                this.loadSoundResource(new ResourceLocation(s, (String)entry.getKey()), (SoundList)entry.getValue());
////                Method m = soundhandler.getClass().getDeclaredMethod("loadSoundResource", ResourceLocation.class, SoundList.class);
//                Method m = soundhandler.getClass().getDeclaredMethod("func_147693_a", ResourceLocation.class, SoundList.class);
//                m.setAccessible(true);
//    			m.invoke(soundhandler, new ResourceLocation(soundDomain, (String)entry.getKey()), (SoundList)entry.getValue());
//            }
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
    
//    private static final ParameterizedType paramtype = new ParameterizedType()
//    {
//        private static final String __OBFID = "CL_00001148";
//        public Type[] getActualTypeArguments()
//        {
//            return new Type[] {String.class, SoundList.class};
//        }
//        public Type getRawType()
//        {
//            return Map.class;
//        }
//        public Type getOwnerType()
//        {
//            return null;
//        }
//    };
	private static final ParameterizedType TYPE = new ParameterizedType()
	{
		public Type[] getActualTypeArguments()
		{
			return new Type[] {String.class, SoundList.class};
		}
		public Type getRawType()
		{
			return Map.class;
		}
		public Type getOwnerType()
		{
			return null;
		}
	};
	static class JsonSoundList{
		public Map<String, SoundList> sounds;
	}
}
