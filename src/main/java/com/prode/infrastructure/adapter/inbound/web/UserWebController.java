package com.prode.infrastructure.adapter.inbound.web;

import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prode.application.dto.request.UserRequest;
import com.prode.application.service.UserService;

@Controller
@RequestMapping("/users")
public class UserWebController {

    private final UserService userService;

    UserWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "users/form";
    }
    @PostMapping
public String create(
        @ModelAttribute UserRequest request,
        RedirectAttributes redirect) {

    try {
        userService.register(request);
        redirect.addFlashAttribute(
                "success",
                "Usuario registrado exitosamente"
        );
    } catch (Exception e) {
        redirect.addFlashAttribute(
                "error",
                e.getMessage()
        );
        return "redirect:/users/new";
    }

    return "redirect:/";
}
    
}