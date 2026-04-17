package com.wq.controller;

import com.wq.wqspringmvc.annotation.Controller;
import com.wq.wqspringmvc.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class OrderController {

    @RequestMapping(value = "/order/list")
    public void listMonster2(HttpServletRequest request,
                            HttpServletResponse response) {

        response.setContentType("text/html;charset=utf-8");
        //获取返回信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write("<h1>订单列表的信息</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    @RequestMapping(value = "/order/add")
    public void listMonster3(HttpServletRequest request,
                            HttpServletResponse response) {

        response.setContentType("text/html;charset=utf-8");
        //获取返回信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write("<h1>添加订单列表的信息</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
