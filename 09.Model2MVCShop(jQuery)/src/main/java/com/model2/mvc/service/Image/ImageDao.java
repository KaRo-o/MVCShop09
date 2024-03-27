package com.model2.mvc.service.Image;

import java.util.List;

import com.model2.mvc.common.Image;

public interface ImageDao {

	public void addImage(Image image) throws Exception;
	
	public List<Image> getImages(String fileKey) throws Exception;
	
}
