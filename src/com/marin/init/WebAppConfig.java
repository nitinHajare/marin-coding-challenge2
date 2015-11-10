package com.marin.init;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan("com.malimilan")
public class WebAppConfig extends WebMvcConfigurationSupport {

	// modify a configuration class to make resources available
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("/");
	}

	@Override
	public void configureContentNegotiation(
			ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(true).useJaf(false)
				.ignoreAcceptHeader(true)
				.mediaType("html", MediaType.TEXT_HTML)
				.mediaType("json", MediaType.APPLICATION_JSON)
				.defaultContentType(MediaType.TEXT_HTML);
	}

	@Resource
	private Environment env;

	@Override
	public void configureMessageConverters(
			List<HttpMessageConverter<?>> converters) {
		converters.add(converter());
		addDefaultHttpMessageConverters(converters);
	}

	@Bean
	MappingJackson2HttpMessageConverter converter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		return converter;
	}
	@Bean
	public ViewResolver contentNegotiatingViewResolver(
			ContentNegotiationManager manager) {

		List<ViewResolver> resolvers = new ArrayList<ViewResolver>();

		InternalResourceViewResolver pagesResolver = new InternalResourceViewResolver();
		pagesResolver.setPrefix("/views/");
		pagesResolver.setSuffix(".jsp");
		pagesResolver.setViewClass(JstlView.class);
		resolvers.add(pagesResolver);

		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setViewResolvers(resolvers);
		resolver.setContentNegotiationManager(manager);
		return resolver;

	}	
}
