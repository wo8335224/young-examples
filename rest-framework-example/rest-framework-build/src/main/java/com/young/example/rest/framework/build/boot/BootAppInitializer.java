package com.young.example.rest.framework.build.boot;

import com.young.example.rest.framework.build.plugin.ExecuteCostPlugin;
import com.young.example.rest.framework.build.plugin.FileUploadPlugin;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;

public class BootAppInitializer implements WebApplicationInitializer {

    private static final String CONFIG_LOCATION = "com.young.example.rest.framework.build.config";
    private static final String MAPPING_URL = "/";

    public void onStartup(ServletContext servletContext)
            throws ServletException {
        WebApplicationContext context = getContext();
        // 监听地址
        servletContext.addListener(new ContextLoaderListener(context));
        servletContext.setInitParameter("log4jConfigLocation", "/WEB-INF/classes/log4j.properties");
        servletContext.addListener(org.springframework.web.util.Log4jConfigListener.class);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "DispatcherServlet", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping(MAPPING_URL);

        //设置转发
        ServletRegistration.Dynamic forward = servletContext.addServlet("ForwardServlet",new FileUploadPlugin());
        forward.setLoadOnStartup(2);
        forward.addMapping("/file/forward");


        String mapping_url = MAPPING_URL+"*";
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");

        addFilter(servletContext,characterEncodingFilter,mapping_url);

        addFilter(servletContext,new ExecuteCostPlugin(),mapping_url);



    }

    private void addFilter(ServletContext servletContext,Filter filter,String mapping_url){
        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(
                filter.getClass().getName(), filter);
        filterRegistration.addMappingForUrlPatterns(null, false, mapping_url);
    }

    /**
     * 加载注解程序路径
     *
     * @return
     */
    private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        return context;
    }

}
