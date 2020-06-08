package eu.atos.seal.rm;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
//import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = { "eu.atos.seal.rm", "eu.atos.seal.rm" , "eu.atos.seal.rm.config"})
public class ReqMngr2SpringBoot extends org.springframework.boot.web.servlet.support.SpringBootServletInitializer {


//    @Override
//    public void run(String... arg0) throws Exception {
//        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
//            throw new ExitException();
//        }
//    }

    public static void main(String[] args) throws Exception {
        new SpringApplication(ReqMngr2SpringBoot.class).run(args);
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }
    
    @Value("${server.port.http}") 
    int httpPort;

    @Value("${server.port}") 
    int httpsPort;
    
    private Connector redirectConnector() {
        //Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    	Connector connector = new Connector(
                TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    	connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(httpsPort);
        return connector;
    }
    
    
}
