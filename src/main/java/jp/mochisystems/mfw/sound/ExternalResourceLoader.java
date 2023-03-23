package jp.mochisystems.mfw.sound;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ExternalResourceLoader implements IResourcePack{

	String domain;
	String BasePath;
	
    public ExternalResourceLoader(String domain, String BasePath)
    {
    	this.domain = domain;
    	this.BasePath = BasePath;
    }
    
    @Override
    public InputStream getInputStream(ResourceLocation rl) throws IOException
    {
    	String path = BasePath + rl.getResourcePath().replaceAll("(.mcmeta|sounds/)", "");
    	return new BufferedInputStream(new FileInputStream(new File(path)));
    }
 
    @Override
    public boolean resourceExists(ResourceLocation resource) {
        return true;
    }
 
    @Override
    @SuppressWarnings("rawtypes")
    public Set getResourceDomains() {
        return ImmutableSet.of(domain);
    }

    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }
 
    @Override
    public String getPackName() {
        return "externalresourceloader";
    }
 
}