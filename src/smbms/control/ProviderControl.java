package smbms.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.Page;

import smbms.pojo.Provider;
import smbms.pojo.Role;
import smbms.pojo.User;
import smbms.service.ProviderService;

@Controller
@RequestMapping("/provider")
public class ProviderControl {
	@Autowired
	private ProviderService providerService;

	// 列表
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView listProvider(Integer pageIndex, String queryProCode,
			String queryProName) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		int pageno = 1;
		if (pageIndex != null) {
			pageno = pageIndex.intValue();
		}
		map.put("start", pageno);
		map.put("size", 5);
		map.put("proCode", queryProCode);
		map.put("proName", queryProName);
		Page<Provider> providerList = (Page<Provider>) providerService
				.getProviderListByMap(map);
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("providerList", providerList);
		model.put("totalCount", providerList.getTotal());
		model.put("currentPageNo", pageno);
		model.put("totalPageCount", providerList.getPages());
		model.put("queryProCode", queryProCode);
		model.put("queryProName", queryProName);
		return new ModelAndView("providerlist", model);
	}

	@RequestMapping("/provideradd.html")
	public ModelAndView add() {
		return new ModelAndView("provideradd");
	}

	// 调用service查询方法
	// 结果存入model
	// 返回modelAndView，指定视图名(jsp文件名前缀)

	// 添加
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView addProvider(Provider provider) throws Exception {
		ModelAndView mv = new ModelAndView();
		int count = providerService.insertProvider(provider);
		if (count > 0) {
			mv.setViewName("redirect:/provider");
		} else {
			mv.setViewName("provideradd");
		}
		return mv;
	}

	// 查看详细
	@RequestMapping(value = "/{pid}", method = RequestMethod.GET)
	public ModelAndView getProvider(@PathVariable Integer pid) {
		return null;
	}

}