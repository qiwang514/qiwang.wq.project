package com.wq.wqspringmvc.context;


import com.wq.wqspringmvc.annotation.AutoWired;
import com.wq.wqspringmvc.annotation.Controller;
import com.wq.wqspringmvc.annotation.Service;
import com.wq.wqspringmvc.xml.XMLParser;

import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//WqWebApplicationContext表示自己的spring容器
public class WqWebApplicationContext {

    //保存扫描包 和 子包的类的全路径
    private List<String> classFullPathList
            = new ArrayList<String>();

//    无参构造器
    public WqWebApplicationContext() {
    }


    private String configLocation;
    public WqWebApplicationContext(String configLocation) {
        this.configLocation = configLocation;


    }

    //定义属性 存放反射后生成的对象
    public ConcurrentHashMap<String, Object> ioc = new ConcurrentHashMap<>();

    //编写方法 完成对ioc的初始化
    public void init() throws UnsupportedEncodingException {
        //String basepackage = XMLParser.getBasepackage("wqspringmvc.xml");
        //动态获取配置文件
        String basepackage = XMLParser.getBasepackage(configLocation.split(":")[1]);
        String[] basepackages = basepackage.split(",");
        //遍历 扫描
        if (basepackages.length > 0) {
            for (String pack : basepackages) {

                scanPackage(pack);
            }
        }
        System.out.println("扫描后的得到的class类的全路径="+classFullPathList);

        //把扫描到的类 反射到容器中
        executeInstance();
        System.out.println("扫描后的iov容器"+ioc);
        //完成注入的bean对象属性的装配
        executeAutowired();
        System.out.println("装配后ioc容器的情况ioc="+ioc);

    }



    public void scanPackage(String pack) throws UnsupportedEncodingException {
        // pack就是xml文件的包com.wq.controller,com.wq.service
        //得到包所在的工作路径 （绝对路径） 比如
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        URL url = this.getClass().getClassLoader().
                getResource("/" + pack.replace(".", "/"));
        //D:\idea_java_projects\springmvc\wq-springmvc\wq-springmvc\target\classes\com\wq\controller\MonsterController.class
        System.out.println("得到的包所在的绝对路径为："+url);
        String path = url.getFile();//去掉前缀
        File file = new File(path);//在io中 目录也被视为一个文件
        //遍历目录  既有文件class  也可能有子目录  引出递归
        for (File f : file.listFiles()) {
            if (f.isDirectory()){
                //如果是一个目录 需要递归处理
                scanPackage(pack+"."+f.getName());
            }else {
                //这时 得到的文件 可能是class文件 也可能是其他 还需要判断是否需要注入到的问题
                //先把文件都保存到集合中 然后再处理
                String classFullpath = pack + "." + f.getName().replaceAll(".class","");
                classFullPathList.add(classFullpath);
            }


        }

    }



    //将扫描到的类  满足条件的情况下 反射到ioc容器中
    public void executeInstance(){
        if (classFullPathList.size()==0){
            return;
        }
        //遍历 反射
        try {
            for (String classfullpath : classFullPathList) {
                //得到的是class类
                Class<?> aClass = Class.forName(classfullpath);
                //说明当前这个类 标识了这个注解
                if (aClass.isAnnotationPresent(Controller.class)){
                    // 得到类名 首字母小写
                    String beanName =
                            aClass.getSimpleName().substring(0,1).toLowerCase()+
                                    aClass.getSimpleName().substring(1);
                    //aClass.getSimpleName()就是得到类名
                    ioc.put(beanName,aClass.newInstance());
                } else if (aClass.isAnnotationPresent(Service.class)) {
                    //先获取到service的value值
                    Service serviceAnnotation
                            = aClass.getAnnotation(Service.class);
                    String beanName = serviceAnnotation.value();
                    if ("".equals(beanName)){
                        //没有指定value  使用默认机制 类名或者接口名 注入 ioc容器
                        //得到所有接口名称  反射
                        Class<?>[] interfaces = aClass.getInterfaces();
                        Object instance = aClass.newInstance();
                        //遍历接口
                        for (Class<?> aninterface : interfaces) {
                            //首字母小写
                            String beanName2 = aninterface.getSimpleName().substring(0,1).toLowerCase()+
                                    aninterface.getSimpleName().substring(1);
                            ioc.put(beanName2,instance);
                        }
                    }else {
                        ioc.put(beanName,aClass.newInstance());
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //编写方法完成属性的自动装配
    public void executeAutowired(){
        //判断ioc是否有装配的对象
        if (ioc.isEmpty()){
            return;
        }
        // 遍历ioc中所有的bean对象  然后获取到所有字段 再判断是否需要装配
        for (Map.Entry<String,Object> entry:ioc.entrySet()){
            String key = entry.getKey();
            Object bean = entry.getValue();
            System.out.println("【AutoWired】处理Bean：" + key + "（类型：" + bean.getClass().getName() + "）");
            //获取bean的所有字段
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                //判断当前字段是否有AUtowired注解
                System.out.println("打印出当前遍历到的字段："+declaredField.getName());
                if (declaredField.isAnnotationPresent(AutoWired.class)){
                    //得到当前字段的Autowired
                    AutoWired autoWiredannotation
                            = declaredField.getAnnotation(AutoWired.class);
                    String beanName3 = autoWiredannotation.value();
                    if ("".equals(beanName3)){
                        Class<?> type = declaredField.getType();
                        beanName3 =
                                type.getSimpleName().substring(0,1).toLowerCase()+
                                        type.getSimpleName().substring(1);

                    }
                    if (null ==ioc.get(beanName3)){
                        throw new RuntimeException("ioc不存在要装配的bean");
                    }
                    //防止属性是private  暴力破解
                    declaredField.setAccessible(true);
                    //设置装配属性
                    try {
                        declaredField.set(bean,ioc.get(beanName3));
                        System.out.println("123456");
                        //把装配的beanName3对象 补充到现在的bean 先有 beanName3（存入 IOC），再有@AutoWired触发查找，最后用beanName3找到这个 Bean
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }
            }



        }


    }
}
