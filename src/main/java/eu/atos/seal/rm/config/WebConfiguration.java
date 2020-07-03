package eu.atos.seal.rm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
public class WebConfiguration extends WebMvcConfigurerAdapter
{
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
//  @Override
//  public void addResourceHandlers(ResourceHandlerRegistry registry)
//  {
//      //registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//      //registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
//      //registry.
//	  registry.addResourceHandler("/templates/**").addResourceLocations("")
//      super.addResourceHandlers(registry);
//  }
//  
	
//  @Bean
//  public InternalResourceViewResolver internalResourceViewResolver()
//  {
//	  InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
//	  //internalResourceViewResolver.setViewClass(JstlView.class);
//	  internalResourceViewResolver.setPrefix("/templates/");
//	  internalResourceViewResolver.setSuffix(".html");
//	  return internalResourceViewResolver;
//  }
	
	
//  @Override
//  public void configureViewResolvers (ViewResolverRegistry registry) {
//      //by default prefix = "/WEB-INF/" and  suffix = ".jsp"
//	  registry.jsp().prefix("/templates/").suffix(".html");
//   
//  }
  
//  @Bean
//  public InternalResourceViewResolver resolver() {
//      InternalResourceViewResolver vr = new InternalResourceViewResolver();
//      vr.setPrefix("/templates/");
//      vr.setSuffix(".html");
//      return vr;
//  }
}