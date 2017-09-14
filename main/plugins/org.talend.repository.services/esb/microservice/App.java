package ${package};

import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ImportResource({ "classpath:META-INF/cxf/cxf.xml" })
public class App extends ${parent} implements ApplicationRunner {

    public App() {
        setRunInTalendEsbRuntimeContainer(true);
        setRunInDaemonMode(false);
        runJobInTOS(mainArgs);
    }

    @Autowired
    private ApplicationContext applicationContext;

    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public org.apache.cxf.endpoint.Server restServer() {
        
        Thread4RestServiceProviderEndpoint thread4RestServiceProviderEndpoint = new Thread4RestServiceProviderEndpoint(this,
                restEndpoint);
        JAXRSServerFactoryBean sf = thread4RestServiceProviderEndpoint.getJAXRSServerFactoryBean();
        sf.setBus((Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID));
        
        thread4RestServiceProviderEndpoint.run();
        return thread4RestServiceProviderEndpoint.getServer();
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(ApplicationContext context) {
        return new ServletRegistrationBean(new CXFServlet(), "/services/*");
    }

    private String[] mainArgs = new String[] {};

    public void run(ApplicationArguments args) throws Exception {
        String[] ma = args.getSourceArgs();
        mainArgs = ma;
    }
    
    
}
