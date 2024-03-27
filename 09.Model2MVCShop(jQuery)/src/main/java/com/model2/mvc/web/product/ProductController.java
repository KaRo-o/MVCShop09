package com.model2.mvc.web.product;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import com.model2.mvc.common.Image;
import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.Image.ImageService;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;

@Controller
@RequestMapping("/product/*")
public class ProductController {

	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Autowired
	@Qualifier("imageServiceImpl")
	private ImageService imageService;
	
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	
	public ProductController() {
		System.out.println(this.getClass());
	}
	
	@RequestMapping(value="/addProduct", method=RequestMethod.POST)
	public String addProduct(MultipartHttpServletRequest request) throws Exception {
									
		Product product = new Product();
		Image image = new Image();
		
		
		String uploadPath = "C:\\Users\\bitcamp\\git\\MVCShop09\\09.Model2MVCShop(jQuery)\\src\\main\\webapp\\images\\uploadFiles\\";
		//String uploadPath = "C:\\Users\\nghng\\git\\MVCShop09\\09.Model2MVCShop(jQuery)\\src\\main\\webapp\\images\\uploadFiles\\";
		
		List<MultipartFile> file = request.getFiles("fileName");
		
		System.out.println(file.toString());
		
		UUID fileKey = UUID.randomUUID();
		product.setFileName(fileKey.toString());
		image.setFileKey(fileKey.toString());
		
		for(MultipartFile i : file) {
			if(i!=null) {
			String originalFileName = i.getOriginalFilename(); //파일명 받아오기
			UUID uuid = UUID.randomUUID(); // Unique한 값 생성
			
			System.out.println("originalFileName : "+originalFileName + ", UUID : "+uuid);
			
			String fileName = uuid.toString() + "_" + originalFileName; // Unique 한 값고 기존 파일 명 합쳐서 중복되지 않는 이름으로 저장
			
			image.setFileName(fileName);
			imageService.addImage(image);
			i.transferTo(new File(uploadPath+fileName));  // 파일 업로드할 경로에 지정한 파일명으로 업로드
			}
		}
		

		
		product.setProdName(request.getParameter("prodName"));
		product.setProdDetail(request.getParameter("prodDetail"));
		product.setManuDate(request.getParameter("manuDate"));
		product.setPrice(Integer.parseInt(request.getParameter("price")));
		//product.setFileName(fileName);

		if(product.getManuDate().contains("-")) {
			String[] md = product.getManuDate().split("-");
			product.setManuDate(md[0]+md[1]+md[2]);
		}
		System.out.println(product.toString());
		
		productService.addProduct(product);
		request.setAttribute("product", product);
		return "forward:/product/addProductResultView.jsp";
	}
	
	/*
	 * @RequestMapping(value="/addProduct", method=RequestMethod.POST) public String
	 * addProduct( @ModelAttribute("product") Product product) throws Exception {
	 * 
	 * System.out.println(product);
	 * 
	 * if(product.getManuDate().contains("-")) { String[] md =
	 * product.getManuDate().split("-"); product.setManuDate(md[0]+md[1]+md[2]); }
	 * 
	 * productService.addProduct(product);
	 * 
	 * return "forward:/product/addProductResultView.jsp"; }
	 */
	
	@RequestMapping(value = "/getProduct" ,method=RequestMethod.GET )
	public String getProduct( @RequestParam("prodNo") int prodNo, Model model
										, HttpServletRequest request
										, HttpServletResponse response) 
										throws Exception {
		
		Product product = productService.getProduct(prodNo);
		Map<String, Object> map = imageService.getImages(product.getFileName());
		model.addAttribute("product",product);
		model.addAttribute("files", map.get("fileList"));
		
		
		String history = "";
		Cookie[] cookies = request.getCookies();
		if(cookies != null && cookies.length > 0 ) {
			for (Cookie cookie : cookies) {
				if( cookie.getName().equals("history") ) {
					history += cookie.getValue();
				}
			}
		}
		
		String strProdNo = "";
		strProdNo += prodNo;
		
		System.out.println(strProdNo.toString());
		
		if(history == null) {
			Cookie cookie = new Cookie("history", strProdNo);
			cookie.setMaxAge(60*5);
			response.addCookie(cookie);
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(strProdNo).append('_').append(history);
			history = sb.toString();
			Cookie cookie = new Cookie("history", history);
			cookie.setMaxAge(60*5);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
		
		
		
		
		return "forward:/product/getProduct.jsp";
		
	}
	
	@RequestMapping(value="/listProduct")
	public String listProduct( @ModelAttribute("search") Search search, Model model) throws Exception {
		
		if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String, Object> map = productService.getProductList(search);
		
		Page resultPage = new Page(search.getCurrentPage(), 
								((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage",resultPage);
		model.addAttribute("search", search);
		
		
		return "forward:/product/listProduct.jsp";
	}
	
	@RequestMapping(value="/updateProductView", method=RequestMethod.GET)
	public String updateProductView( @RequestParam("prodNo") int prodNo, Model model) 
											throws Exception {
		
		Product product = productService.getProduct(prodNo);
		
		model.addAttribute("product", product);
		
		return "forward:/product/updateProductView.jsp";
	}
	
	@RequestMapping(value="/updateProduct", method=RequestMethod.POST)
	public String updateProduct( @ModelAttribute("product") Product product, 
									Model model) throws Exception {
		
		if(product.getManuDate().contains("-")) {
			String[] md = product.getManuDate().split("-");
			product.setManuDate(md[0]+md[1]+md[2]);
		}
		
		productService.updateProduct(product);
		
		return "redirect:/product/getProduct?prodNo="+product.getProdNo();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
