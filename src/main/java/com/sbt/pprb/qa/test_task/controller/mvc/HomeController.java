package com.sbt.pprb.qa.test_task.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.sbt.pprb.qa.test_task.controller.EndpointPaths.INDEX;

@Controller
public class HomeController {

    @RequestMapping(value = INDEX)
    public String index() {
        return "index";
    }
}
