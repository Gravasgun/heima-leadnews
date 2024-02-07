package com.heima.freemarker.test;

import com.heima.freemarker.FreemarkerDemoApplication;
import com.heima.freemarker.beans.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = FreemarkerDemoApplication.class)
@RunWith(SpringRunner.class)
public class FreemarkerTest {
    @Autowired
    private Configuration configuration;

    @Test
    public void test() throws IOException, TemplateException {
        Template template = configuration.getTemplate("01-basic.ftl");
        template.process(getData(), new FileWriter("d:/01-basic.html"));
    }

    private Map getData() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "小刘");
        Student student = new Student();
        student.setName("小李");
        student.setAge(18);
        map.put("stu", student);
        return map;
    }
}
