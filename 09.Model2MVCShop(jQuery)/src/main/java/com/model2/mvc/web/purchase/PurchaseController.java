package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;
import com.model2.mvc.service.purchase.PurchaseService;


@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {

	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	@RequestMapping(value="/addPurchaseView", method=RequestMethod.GET)
	public String AddPurchase(@RequestParam("prodNo") int prodNo
										,HttpServletRequest req, Model model
										) throws Exception {
		
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute("user");
		
		Product product = productService.getProduct(prodNo);
		
		Purchase purchase = new Purchase();
		
		purchase.setBuyer(user);
		purchase.setPurchaseProd(product);
		
		model.addAttribute("purchase", purchase);
		
		
		return "forward:/purchase/addPurchase.jsp";
	}
	
	@RequestMapping(value="/addPurchase", method=RequestMethod.POST)
	public String AddPurchaseResult(@ModelAttribute("purchase") Purchase purchase
												) throws Exception {
		
		System.out.println("purchase : "+ purchase);
		
		purchase.setTranCode("2");
		
		purchaseService.addPurchase(purchase);
		
		
		return "forward:/purchase/addPurchaseView.jsp";
	}
	
	@RequestMapping(value="/listPurchase", method=RequestMethod.GET)
	public String ListPurchase( @ModelAttribute("search") Search search
										, Model model, HttpServletRequest req
										) throws Exception {
		HttpSession session = req.getSession();
		
		User user = (User)session.getAttribute("user");
		
		search.setUserId(user.getUserId());
		
		if(search.getCurrentPage() == 0) {
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		Map<String, Object> map = purchaseService.getPurchaseList(search);
		
		Page resultPage = new Page(search.getCurrentPage(), 
				((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);

		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage",resultPage);
		model.addAttribute("search",search);
		
		
		return "forward:/purchase/listPurchase.jsp";
	}
	
	@RequestMapping(value="/getPurchase", method=RequestMethod.GET)
	public String GetPurchase(@RequestParam("tranNo") int tranNo
										,Model model
										) throws Exception {
		
		
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		
		model.addAttribute("purchase",purchase);
		
		return "forward:/purchase/getPurchase.jsp";
	}
	
	@RequestMapping(value="/updatePurchaseView",method=RequestMethod.GET)
	public String updatePurchaseView(@RequestParam("tranNo") int tranNo 
											,Model model
											) throws Exception{
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		model.addAttribute("purchase",purchase);
		
		
		return "forward:/purchase/updatePurchaseView.jsp";
	}
	
	@RequestMapping(value="/updatePurchase", method=RequestMethod.POST)
	public String updatePurchase(@ModelAttribute("purchase") Purchase purchase
											,@RequestParam("tranNo") int tranNo
											)throws Exception{
		
		purchaseService.updatePurchase(purchase);
		
		
		
		return "redirect:/purchase/getPurchase?tranNo="+tranNo;
	}
	
	@RequestMapping(value="/updateTranCode",method=RequestMethod.GET)
	public String updateTranCode(@RequestParam("tranCode") String tranCode
											,@RequestParam("tranNo") int tranNo
											) throws Exception{
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		purchase.setTranCode(tranCode);
		purchaseService.updateTranCode(purchase);
		
		return "redirect:/purchase/listPurchase";
	}
	
	@RequestMapping(value="/updateTranCodeByProd",method=RequestMethod.GET)
	public String updateTranCodeByProd(@RequestParam("tranCode") String tranCode
											,@RequestParam("tranNo") int tranNo
											) throws Exception{
		
		Purchase purchase = purchaseService.getPurchase(tranNo);
		purchase.setTranCode(tranCode);
		purchaseService.updateTranCode(purchase);
		
		return "redirect:/product/listProduct?menu=manage";
	}
	
	
}
