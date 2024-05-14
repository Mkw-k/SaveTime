package com.save.savetime.controller.admin;


import com.save.savetime.model.dto.ResourcesDto;
import com.save.savetime.model.entity.Resources;
import com.save.savetime.model.entity.Role;
import com.save.savetime.security.metadatasource.UrlFilterInvocationSecurityMetadatsSource;
import com.save.savetime.security.service.ResourcesService;
import com.save.savetime.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ResourcesController {
	
	private final ResourcesService resourcesService;

//	@Autowired
//	MethodSecurityService methodSecurityService;
	private final UrlFilterInvocationSecurityMetadatsSource urlFilterInvocationSecurityMetadatsSource;
	private final RoleService roleService;

	@Autowired
	public ResourcesController(ResourcesService resourcesService, UrlFilterInvocationSecurityMetadatsSource urlFilterInvocationSecurityMetadatsSource, RoleService roleService) {
		this.resourcesService = resourcesService;
		this.urlFilterInvocationSecurityMetadatsSource = urlFilterInvocationSecurityMetadatsSource;
		this.roleService = roleService;
	}


	@GetMapping(value="/admin/resources")
	public String getResources(Model model) throws Exception {

		List<Resources> resources = resourcesService.selectResources();
		model.addAttribute("resources", resources);

		return "admin/resource/list";
	}

	@PostMapping(value="/admin/resources")
	public String saveResources(ResourcesDto resourcesDto) throws Exception {

		resourcesService.saveResources(resourcesDto);


		return "redirect:/admin/resources";
	}

	@GetMapping(value="/admin/resources/register")
	public String viewRoles(Model model) throws Exception {

		List<Role> roleList = roleService.getRoles();
		model.addAttribute("roleList", roleList);
		Resources resources = new Resources();
		model.addAttribute("resources", resources);

		return "admin/resource/detail";
	}

	@Transactional
	@GetMapping(value="/admin/resources/{id}")
	public String getResources(@PathVariable String id, Model model) throws Exception {

		List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);
		Resources resources = resourcesService.selectResources(Long.valueOf(id));
		resources.getResourcesRole();
		model.addAttribute("resources", resources);
		
		return "admin/resource/detail";
	}

	@GetMapping(value="/admin/resources/delete/{id}")
	public String removeResources(@PathVariable String id, Model model) throws Exception {

		Resources resources = resourcesService.selectResources(Long.valueOf(id));
		resourcesService.deleteResources(Long.valueOf(id));
//		methodSecurityService.removeMethodSecured(resources.getResourceName());
		urlFilterInvocationSecurityMetadatsSource.reload();

		return "redirect:/admin/resources";
	}
}
