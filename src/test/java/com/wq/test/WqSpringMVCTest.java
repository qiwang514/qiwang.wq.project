package com.wq.test;

import com.wq.wqspringmvc.xml.XMLParser;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.jupiter.api.Test;

public class WqSpringMVCTest {
    @Test
    public void readXML(){
        String basepackage =
                XMLParser.getBasepackage("wqspringmvc.xml");
        System.out.println("解读获取到的包basepackage:"+basepackage);
    }
}
