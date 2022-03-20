package com.sbt.pprb.qa.test_task.controller.mvc;

import com.sbt.pprb.qa.test_task.model.dto.AppUser;
import com.sbt.pprb.qa.test_task.model.exception.UsernameAlreadyTakenException;
import com.sbt.pprb.qa.test_task.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.REGISTER;

@Slf4j
@Controller
@AllArgsConstructor
public class RegisterController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @RequestMapping(value = REGISTER)
    public String register() {
        return "register";
    }

    @RequestMapping(method = RequestMethod.POST, value = REGISTER)
    public Object register(@RequestParam String username,
                                 @RequestParam String password,
                                 HttpServletRequest request)
    {
        AppUser newUser = new AppUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEnabled(true);
        newUser.setAuthority("USER");

        try {
            AppUser createdUser = userService.createUser(newUser);
            log.info("Created user: {}", createdUser);

            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return new RedirectView("login");
        } catch (UsernameAlreadyTakenException e) {
            ModelAndView mav = new ModelAndView();
            mav.addObject("usernameTaken", e.getMessage());
            mav.setViewName("register");
            return mav;
        }
    }
}
