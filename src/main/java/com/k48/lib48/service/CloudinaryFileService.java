package com.k48.lib48.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@Primary // Cette annotation dit à Spring d'utiliser Cloudinary au lieu du stockage local
public class CloudinaryFileService implements FileServiceInt {
	
	private final Cloudinary cloudinary;
	
	public CloudinaryFileService(
		@Value("${cloudinary.cloud_name}") String name,
		@Value("${cloudinary.api_key}") String key,
		@Value("${cloudinary.api_secret}") String secret) {
		
		this.cloudinary = new Cloudinary(ObjectUtils.asMap(
			"cloud_name", name,
			"api_key", key,
			"api_secret", secret));
	}
	
	@Override
	public String uploadFile(String path, MultipartFile file) throws IOException {
		// Envoi direct à Cloudinary
		Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
		// On retourne l'URL publique de l'image
		return uploadResult.get("url").toString();
	}
	
	@Override
	public InputStream getResourceFile(String path, String filename) throws IOException {
		// Avec Cloudinary, on utilise directement l'URL, plus besoin de flux local
		return null;
	}
	
	@Override
	public boolean fileExists(String path, String filename) {
		return filename != null && filename.startsWith("http");
	}
	
	@Override
	public void deleteFile(String path, String filename) throws IOException {
		// Logique pour supprimer via l'ID public (optionnel pour la V1)
	}
}