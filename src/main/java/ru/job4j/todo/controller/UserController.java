package ru.job4j.todo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.SimpleUserService;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final SimpleUserService userService;

    public UserController(SimpleUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        try {
            userService.save(user);
            return "redirect:/users/login";
        } catch (Exception exception) {
            log.error("Failed to register new user: {}", user, exception);
            model.addAttribute("message", exception.getMessage());
            return "errors/500";
        }
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, HttpSession session) {
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest request) {
        try {
            var userOptional = userService.findByLoginAndPassword(user.getLogin(),
                    user.getPassword());
            if (userOptional.isEmpty()) {
                model.addAttribute("error", "Incorrect login or password.");
                return "/users/login";
            }
            var session = request.getSession();
            session.setAttribute("user", userOptional.get());
        } catch (Exception exception) {
            log.error("Error during user login, id: {}", user, exception);
            model.addAttribute("message", exception.getMessage());
            return "errors/500";
        }
        return "redirect:/tasks/list";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }
}
