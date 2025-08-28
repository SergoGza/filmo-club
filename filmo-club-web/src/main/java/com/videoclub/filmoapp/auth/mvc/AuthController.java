package com.videoclub.filmoapp.auth.mvc;


import com.videoclub.filmoapp.auth.dto.UserMvcDTO;
import com.videoclub.filmoapp.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login () {

        return "videoclub/login";
    }

    @GetMapping("/register")
    public ModelAndView register () {

        UserMvcDTO userMvcDTO = UserMvcDTO.builder().build();

        ModelAndView modelAndView = new ModelAndView("videoclub/register");
        modelAndView.addObject("user", userMvcDTO);
        return modelAndView;

    }


    @PostMapping("/register")
    public Object registerPost(
            @Valid @ModelAttribute ("user") UserMvcDTO userMvcDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
            ) {

        if(bindingResult.hasErrors()) {
            return "videoclub/register";
        }

        userService.registerUser(userMvcDTO);

        redirectAttributes.addAttribute("success", true);
        return "redirect:/login";

    }
}
