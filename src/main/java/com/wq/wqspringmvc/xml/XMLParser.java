package com.wq.wqspringmvc.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

//用于解析spring配置文件
public class XMLParser {

    public static  String getBasepackage(String xmlFile){

        SAXReader saxReader = new SAXReader();
        //通过得到类的加载路径 对应的资源流
        InputStream inputStream =
                XMLParser.class.getClassLoader().getResourceAsStream(xmlFile);

        try {
            //得到配置文件xmlFile的文档
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            Element componentscanelement = rootElement.element("component-scan");

            Attribute attribute = componentscanelement.attribute("base-package");
            String basePackage = attribute.getText();
            return basePackage;


        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";
    }
}
