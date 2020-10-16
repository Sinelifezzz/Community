package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    // @ResponseBody 注解用于将Controller的方法返回的对象，根据HTTP Request Header的Accept的内容,通过适当的HttpMessageConverter转换为指定格式后，写入到Response对象的body数据区。
    // 返回的数据不是html标签的页面，而是其他某种格式的数据时（如json、xml等）使用
    public String sayHello(){
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // 获取请求数据
        System.out.println(request.getMethod());    // 请求方式

        System.out.println(request.getServletPath());   // 请求路径

        Enumeration<String> enumeration=request.getHeaderNames();   // 请求行（key-value）
        while(enumeration.hasMoreElements()){
            String name =enumeration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name+": "+value);
        }

        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset-utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write("<h1>nowcoder</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get
    // 请求形式为 /student?current=1&limit=20 时，获取参数
    @RequestMapping(path="/students",method= RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            @RequestParam(name="current",required = false,defaultValue = "1") int current,
            @RequestParam(name="limit",required = false,defaultValue = "10") int limit){

        System.out.println(current);
        System.out.println(limit);
        return "soome students";
    }

    // 请求形式为 /student/123 时，获取参数
    @RequestMapping(path="/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    // Post
    @RequestMapping(path="/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "Success";
    }

    // 响应HTML数据
    // 方式1
    @RequestMapping(path="/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","zhangsan");
        mav.addObject("age",30);
        mav.setViewName("/demo/view");
        return mav;
    }

    // 方式2
    @RequestMapping(path="/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","Beijing University");
        model.addAttribute("age",80);
        return "/demo/view";
    }

    // 响应JSON数据（异步请求）
    // Java对象 --> JSON对象 --> JS对象
    @RequestMapping(path="/emp",method =RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp=new HashMap<>();
        emp.put("name","zhangsan");
        emp.put("age",23);
        emp.put("salary",80000.00);
        return emp;
    }

    @RequestMapping(path="/emps",method =RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list=new ArrayList<>();

        Map<String,Object> emp=new HashMap<>();
        emp.put("name","zhangsan");
        emp.put("age",23);
        emp.put("salary",80000.00);
        list.add(emp);

        emp=new HashMap<>();
        emp.put("name","lisi");
        emp.put("age",24);
        emp.put("salary",90000.00);
        list.add(emp);

        return list;
    }
}
