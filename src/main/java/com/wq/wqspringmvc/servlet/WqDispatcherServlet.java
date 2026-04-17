package com.wq.wqspringmvc.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wq.wqspringmvc.annotation.Controller;
import com.wq.wqspringmvc.annotation.RequestMapping;
import com.wq.wqspringmvc.annotation.RequestParam;
import com.wq.wqspringmvc.annotation.ResponseBody;
import com.wq.wqspringmvc.context.WqWebApplicationContext;
import com.wq.wqspringmvc.handler.WqHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//本质就是一个servlet 充当DispatchServlet
//要在web中配置DispatchServlet
public class WqDispatcherServlet extends HttpServlet {
    //定义属性 保存wqhandler
    private List<WqHandler> wqHandlerList
            = new ArrayList<WqHandler>();

    WqWebApplicationContext wqWebApplicationContext = null;


    private ServletContext servletContext;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

        String configLocation =
                servletConfig.getInitParameter("contextConfigLocation");
        //得到<param-value>classpath:applicationContext-mvc.xml</param-value>


//        创建自己的容器
        wqWebApplicationContext
                = new WqWebApplicationContext(configLocation);
        try {
            wqWebApplicationContext.init();
            //完成控制器方法的映射
            super.init(servletConfig); // 必须先调用，让父类保存config
            this.servletContext =servletConfig.getServletContext(); // 直接从config获取（100%非null）
            initHandlerMapping();
            System.out.println("handlerlist初始化的结果"+wqHandlerList);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        System.out.println("WqDispatcherServlet doPost被调用");
        System.out.println("调用方法 完成请求转发");
        executeDispatcher(req, resp);

    }

    //开发一个方法 完成url和控制器方法的映射
    private void initHandlerMapping(){
        if (wqWebApplicationContext.ioc.isEmpty()){
            //判断ioc是否为空
            return;
        }
        //遍历ioc的bean对象
        //map的遍历
        for (Map.Entry<String,Object> entry:wqWebApplicationContext.ioc.entrySet()){
            //取出Object这个类
            Class<?> clazz = entry.getValue().getClass();
            //这个类是否有注解
            if (clazz.isAnnotationPresent(Controller.class)){
                //取出所有方法
                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    //判断该方法是否有@RequestMapping
                    if (declaredMethod.isAnnotationPresent(RequestMapping.class)){

                        RequestMapping requestMappingAnnotation
                                = declaredMethod.getAnnotation(RequestMapping.class);

                        //这里把工程路径拼接上 getServletContext().getContextPath()
                        String url = getServletContext().getContextPath()+requestMappingAnnotation.value();
                        System.out.println("该方法的路径url为："+url);
                        //创建handler对象
                        WqHandler wqHandler = new WqHandler(url, entry.getValue(), declaredMethod);
                        //放入到集合中
                        wqHandlerList.add(wqHandler);


                    }
                }
            }


        }
    }

//    编写方法 根据requst获取wqhandler对象
    private WqHandler getWqHandler(HttpServletRequest request){
//        获取用户请求的uri
        String requestURI = request.getRequestURI();
        System.out.println("获取用户请求的uri="+requestURI);
        System.out.println("准备遍历......");
//        遍历handelerlist 看是否有
        for (WqHandler wqHandler : wqHandlerList) {
            if (requestURI.equals(wqHandler.getUrl())){
                return wqHandler;
            }
        }
        return null;

    }

    //编写方法 完成分发请求
    private void executeDispatcher(HttpServletRequest request,
                                   HttpServletResponse response){
        System.out.println("正在进行分发请求....");
        WqHandler wqHandler = getWqHandler(request);
        try {
            if (wqHandler == null){
                response.getWriter().print("<h1>404 not found</h1>");
            }else {
                //匹配成功 反射调用控制器的方法
                //但是针对的目标方法 形参 是有限制的 是多种多样的 所以把实参 封装到参数数组 在反射调用给目标方法
                //先得到目标方法的所有形参信息
                //形参数组 parameterTypes 作用：告诉代码 “目标方法需要 2 个参数，第 0 位是 String，第 1 位是 int”；
                Class<?>[] parameterTypes =
                        wqHandler.getMethod().getParameterTypes();
//                创建一个参数数组  对应实参数组
                Object[] params =
                        new Object[parameterTypes.length];

                //遍历形参 根据形参数组 将实参 填充到实参数组中
                for (int i = 0; i < parameterTypes.length; i++) {
                    //取出每一个形参数据和类型
                    Class<?> parameterType = parameterTypes[i];
                    if ("HttpServletRequest".equals(parameterType.getSimpleName())){

                        params[i] = request;

                    }else if ("HttpServletResponse".equals(parameterType.getSimpleName())){
                        params[i] = response;
                    }
                }
                //将http请求参数 封装到params实参数组中 注意实参的顺序问题
                // 获取http请求参数
                // Map<String, String[]>   String表示http请求的参数名 String[]表示http请求的参数值 value =“xxx"
                request.setCharacterEncoding("UTF-8");
                Map<String, String[]> parameterMap
                        = request.getParameterMap();
                for (Map.Entry<String,String[]> entry : parameterMap.entrySet()){
                    String paraname = entry.getKey();//请求的参数名value
                    String paravalue = entry.getValue()[0];//请求的参数值xxx
                    //不考虑前端是提交多个数值的情况 不考虑checkbox

                    //请求的参数 是 对应目标方法的 第几个参数
                    int indexRequestParameterIndex =
                            getIndexRequestParameterIndex(wqHandler.getMethod(),paraname);

                    if (indexRequestParameterIndex != -1){
                        params[indexRequestParameterIndex] = paravalue;

                    }else {
                        //没有找到注解对应的参数  使用默认的机制
//                        得到目标方法的所有变量名 遍历 寻找匹配
                        List<String> parameterNames =
                                getParameterNames(wqHandler.getMethod());
                        for (int i = 0; i < parameterNames.size(); i++) {
                            if (paraname.equals(parameterNames.get(i))){
                                params[i]= paravalue;
                                break;
                            }
                        }
                    }



                }

                Object result = wqHandler.getMethod().invoke(wqHandler.getController(),
                        params);
                //对结果进行解析
                if (result instanceof String){
                    String viewName = (String) result;
                    if (viewName.contains(":")){
                        String viewType = viewName.split(":")[0];
                        String viewPage = viewName.split(":")[1];
                        if ("forward".equals(viewType)){
                            //请求转发
                            request.getRequestDispatcher(viewPage).forward(request, response);
                        } else if ("redirect".equals(viewType)) {
//                            重定向
                            response.sendRedirect(request.getContextPath()+viewPage);
                        }
                    }else {
                        //默认请求转发
                        request.getRequestDispatcher(viewName).forward(request, response);
                    }
                } else if (result instanceof ArrayList) {
                    Method method = wqHandler.getMethod();
                    if (method.isAnnotationPresent(ResponseBody.class)){
                        //Arraylist转成json数据
                        //使用jackson包下的工具类
                        ObjectMapper objectMapper = new ObjectMapper();
                        String json =
                                objectMapper.writeValueAsString(result);

                        response.setContentType("text/json;charset=utf-8");
                        PrintWriter writer = response.getWriter();
                        writer.write(json);
                        writer.flush();
                        writer.close();


                    }
                }


            }
        } catch (Exception e) {
           e.printStackTrace();
        }


    }

    //编写方法  返回请求参数是目标方法的第几个形参  value=xxx "value"
    public int getIndexRequestParameterIndex(Method method,String name){
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            //取出当前的形参 参数
            Parameter parameter = parameters[i];
            //判断 parameter是否有@requestparam修饰
            boolean annotationPresent = parameter.isAnnotationPresent(RequestParam.class);
            if (annotationPresent){
                //存在修饰
                RequestParam requestannotation
                        = parameter.getAnnotation(RequestParam.class);

                String value = requestannotation.value();
                //开始匹配位置
                //name是入参
                if (name.equals(value)){
                    return i;
                }

            }
        }
        return -1;

    }

    //编写方法 得到所有形参的名称 并且都放到集合中
    public List<String> getParameterNames(Method method){
        List<String> parametersList = new ArrayList<>();
        //但是  默认情况下 parameter.getName()  不是形参真正的名字
        Parameter[] parameters = method.getParameters();
        //遍历
        for (Parameter parameter : parameters) {
            parametersList.add(parameter.getName());
        }
        System.out.println("目标方法的形参列表"+parametersList);
        System.out.println("githup提交完成");
        System.out.println("第三次提交");
        return parametersList;

    }

}
