package com.model2.mvc.service.Image.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.model2.mvc.common.Image;
import com.model2.mvc.service.Image.ImageDao;
import com.model2.mvc.service.Image.ImageService;

@Service("imageServiceImpl")
public class ImageServiceImpl implements ImageService {
	
	@Autowired
	@Qualifier("imageDaoImpl")
	private ImageDao imageDao;
	public void setImageDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}
	

	public ImageServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addImage(Image image) throws Exception {
		imageDao.addImage(image);
	}

}
