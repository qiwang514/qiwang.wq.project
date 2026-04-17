package com.wq.controller;

import com.wq.entity.Monster;
import com.wq.service.MonsterService;
import com.wq.service.impl.MonsterServiceImpl;
import com.wq.wqspringmvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MonsterController {

    @AutoWired
    private MonsterService monsterService;

//
//    // 新增：手动初始化（仅用于测试）
//    public MonsterController() {
//        this.monsterService = new MonsterServiceImpl();
//    }

//    编写方法 可以列出妖怪列表
    @RequestMapping(value = "/monster/list")
    public void listMonster(HttpServletRequest request,
                            HttpServletResponse response) {

        response.setContentType("text/html;charset=utf-8");
        //获取返回信息

        StringBuilder content = new StringBuilder("<h1>妖怪列表信息</h1>");
//        调用monsterservice
        List<Monster> monsters = monsterService.listMonster();
        content.append("<table border='1px' width='500px' style='border-collapse:collapse'>");
        for (Monster monster : monsters) {
            content.append("<tr><td>"+monster.getId()
                    +"</td><td>"+monster.getName()
                    + "</td><td>"+monster.getSkill()
                    +"</td><td>"+monster.getAge()
                    +"</td></tr>");

        }
        content.append("</table>");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    //通过名字返回对应的集合
    @RequestMapping(value = "/monster/find")
    public void findMonsterByName(HttpServletRequest request,
                            HttpServletResponse response,
                           String name) {

        response.setContentType("text/html;charset=utf-8");
        //获取返回信息
        System.out.println("接收到的参数name"+name);

        StringBuilder content = new StringBuilder("<h1>妖怪列表信息</h1>");
//        调用monsterservice
        List<Monster> monsters = monsterService.findMonsterByName(name);
        content.append("<table border='1px' width='400px' style='border-collapse:collapse'>");
        for (Monster monster : monsters) {
            content.append("<tr><td>"+monster.getId()
                    +"</td><td>"+monster.getName()
                    + "</td><td>"+monster.getSkill()
                    +"</td><td>"+monster.getAge()
                    +"</td></tr>");

        }
        content.append("</table>");
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

//    处理妖怪登录的方法
    @RequestMapping("/monster/login")
    public String login(HttpServletRequest request,
                        HttpServletResponse response,
                        String mName){


        System.out.println("接收到的mName="+mName);
        //将mName设置到request域
        request.setAttribute("mName=", mName);


        boolean b = monsterService.login(mName);
        if(b){
            return "/login_ok.jsp";
        }else{
            return "forward:/login_error.jsp";
        }

    }

    //编写方法 返回json格式的数据
    @RequestMapping("/monster/list/json")
    @ResponseBody
    public List<Monster> listMonsterByJson(HttpServletRequest request,
                                                HttpServletResponse response){

        List<Monster> monsters = monsterService.listMonster();
        return monsters;

    }

}
