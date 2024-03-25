package com.model2.mvc.web.product;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;

@Controller
@RequestMapping("/product/*")
public class ProductController {

	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	
	public ProductController() {
		System.out.println(this.getClass());
	}
	
	@RequestMapping(value="/addProduct", method=RequestMethod.POST)
	public String addProduct( @RequestParam String prodName,
			@RequestParam String prodDetail,
			@RequestParam String manuDate,
			@RequestParam int price,
			@RequestParam MultipartFile file) throws Exception {
									
		Product product = new Product();
		
		product.setProdName(prodName);
		product.setProdDetail(prodDetail);
		product.setManuDate(manuDate);
		product.setPrice(price);
		
		System.out.println(product);
		
		
		if(product.getManuDate().contains("-")) {
			String[] md = product.getManuDate().split("-");
			product.setManuDate(md[0]+md[1]+md[2]);
		}
		/*
		MultipartFile file = product.getUploadFile();
		
		String originalFileName = file.getOriginalFilename();
		String FileName = file.getName();
		
		System.out.println("original : "+originalFileName);
		System.out.println("getName : "+FileName);
		*/
		
		productService.addProduct(product);
		
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
		
		model.addAttribute("product",product);
		
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
