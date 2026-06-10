package com.prode.infrastructure.adapter.inbound.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prode.application.dto.request.UserRequest;
import com.prode.application.service.UserService;

@Controller
@RequestMapping("/users")
public class UserWebController {

    private final UserService userService;

    public UserWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "Users/login"; // Renderiza el login.html
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "Users/form";
    }

    @PostMapping
    public String create(
            @ModelAttribute UserRequest request,
            RedirectAttributes redirect) {

        try {
            userService.register(request);
            redirect.addFlashAttribute(
                    "success",
                    "Usuario registrado exitosamente. Por favor, inicie sesión.");
        } catch (Exception e) {
            redirect.addFlashAttribute(
                    "error",
                    e.getMessage());
            return "redirect:/users/new";
        }

        return "redirect:/users/login";
    }
}