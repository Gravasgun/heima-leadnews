package com.heima.freemarker.controller;

import com.heima.freemarker.beans.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("/basic")
    public String hello(Model model) {
        //name
        model.addAttribute("name","freemarker");
        //student
        Student student=new Student();
        student.setAge(18);
        student.setName("小明");
        model.addAttribute("stu",student);
        return "01-basic";
    }
}