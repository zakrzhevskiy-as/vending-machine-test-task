package com.sbt.pprb.qa.test_task.controller.mvc;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@AllArgsConstructor
public class RegisterController {

    private UserService userService;

    @RequestMapping(value = "/register")
    public String register() {
        return "register";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public RedirectView register(@RequestParam String username,
                                 @RequestParam String password,
                                 HttpServletRequest request)
    {
        AppUser newUser = userService.createUser(username, password);
        log.info("Created user: {}", newUser);

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new RedirectView("login");
    }
}