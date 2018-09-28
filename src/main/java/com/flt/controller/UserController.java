package com.flt.controller;

import com.flt.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author fenglingtong
 * @date 2017/12/5
 */
@Controller
//@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(Model model){
        Person person = new Person("于乐乐", 20);
        model.addAttribute("person",person);
        return "index";
    }
}
