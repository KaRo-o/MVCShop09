package com.model2.mvc.service.Image;

import java.util.Map;

import com.model2.mvc.common.Image;

public interface ImageService {

	public void addImage(Image image) throws Exception;
	
	public Map<String, Object> getImages(String fileKey) throws Exception;
	
}
