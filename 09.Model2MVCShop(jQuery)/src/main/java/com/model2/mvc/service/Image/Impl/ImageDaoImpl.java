package com.model2.mvc.service.Image.Impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.model2.mvc.common.Image;
import com.model2.mvc.service.Image.ImageDao;

@Repository("imageDaoImpl")
public class ImageDaoImpl implements ImageDao {

	@Autowired
	@Qualifier("sqlSessionTemplate")
	private SqlSession sqlSession;
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	
	public ImageDaoImpl() {
		System.out.println(this.getClass());
	}

	@Override
	public void addImage(Image image) throws Exception {
		sqlSession.insert("ImageMapper.addImage", image);
	}


	@Override
	public List<Image> getImages(String fileKey) throws Exception {
		return sqlSession.selectList("ImageMapper.getImages" ,fileKey);
	}

}
