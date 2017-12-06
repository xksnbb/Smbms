package smbms.control;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import smbms.pojo.Role;
import smbms.pojo.User;
import smbms.service.RoleService;
import smbms.service.UserService;

import com.github.pagehelper.Page;

@Controller
@RequestMapping("/user")
// URL·�������user�����userʵ��ҵ����ʵ��
public class UserControl {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	// �û���¼
	@RequestMapping(value = "/dologin.html")
	public ModelAndView userLogin(@Valid User loginuser, BindingResult result) {

		ModelAndView mv = new ModelAndView();

		// BindingResult����洢���Ǳ?��֤�Ľ��
		if (result.hasErrors() == true) { // ��֤����
			mv.setViewName("login"); // ��ת���?ҳ��
			return mv;
		}

		// ����Service����ʵ�ֵ�¼��֤
		User user = userService.login(loginuser.getUsercode(),
				loginuser.getUserpassword());
		// User user = null;
		// ���user��Ϊnull����¼�ɹ�
		if (user != null) {
			mv.setViewName("frame");
		} else {
			mv.addObject("error", "用户名或密码错误 ");
			mv.setViewName("login");
		}
		return mv;
	}

	// �û�ע��(REST���)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView userRegist(@Valid User reguser, BindingResult result,
			MultipartFile idpicpath, HttpServletRequest request)
			throws Exception {
		ModelAndView mv = new ModelAndView();
		// if(result.hasErrors()){
		// mv.setViewName("useradd");
		// }else{
		// ��ȡWebContent����Ŀ¼���·��
		String path = request.getServletContext().getRealPath("/upload");

		// �����ϴ���ͼƬ�ļ�
		String fileName = idpicpath.getOriginalFilename(); // ԭʼ�ļ���
		// �ļ��� = "����ַ������" + ".��׺��"
		String ext = FilenameUtils.getExtension(fileName);
		fileName = UUID.randomUUID().toString() + "." + ext;
		// System.currentTimeMillis()
		// Math.random() * 100000

		File file = new File(path + "/" + fileName);
		idpicpath.transferTo(file); // "���Ϊ"ָ��Ŀ���ļ�

		reguser.setIdpicpath("upload/" + fileName); // �ļ����·��(д����ݿ�)

		int count = userService.insertUser(reguser);
		if (count > 0) {
			// ע��ɹ�����ת���û��б�ҳ��
			// �ض��� response.sendRedirect("/user");
			mv.setViewName("redirect:/user");
		} else {
			mv.setViewName("useradd");
		}
		// }
		return mv;
	}

	// �û��б�(REST���)
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView userList(String pageIndex, String queryname,
			String queryUserRole) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		int pageno = 1;
		if (pageIndex != null) {
			pageno = Integer.parseInt(pageIndex);
		}
		map.put("start", pageno);
		map.put("size", 5);
		map.put("userName", queryname);
		if (queryUserRole != null && !queryUserRole.equals("0")) {
			map.put("userRole", queryUserRole);
		}
		Page<User> userList = (Page<User>) userService.getUserListByMap(map);
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("userList", userList);
		model.put("totalCount", userList.getTotal());
		model.put("currentPageNo", pageno);
		model.put("totalPageCount", userList.getPages());
		model.put("queryUserName", queryname);
		model.put("queryUserRole", queryUserRole);
		List<Role> list = roleService.getRoleListByMap(null);
		model.put("roleList", list);
		// ����service������ѯ�û��б�

		// ������Model������ת��userlist.jspҳ��
		return new ModelAndView("userlist", model);
	}

}
