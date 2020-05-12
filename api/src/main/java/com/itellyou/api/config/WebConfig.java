package com.itellyou.api.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.argument.MultiRequestBodyArgumentResolver;
import com.itellyou.util.argument.UserArgumentResolver;
import com.itellyou.util.serialize.config.SerializerFeatureConfig;
import com.itellyou.util.serialize.filter.UnderScoreNameFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final MultiRequestBodyArgumentResolver multiRequestBodyArgumentResolver;

    @Autowired
    public WebConfig( MultiRequestBodyArgumentResolver multiRequestBodyArgumentResolver){
        this.multiRequestBodyArgumentResolver = multiRequestBodyArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserArgumentResolver(UserInfoModel.class));
        resolvers.add(multiRequestBodyArgumentResolver);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.clear();
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);

        converter.setSupportedMediaTypes(supportedMediaTypes);
        converter.setDefaultCharset(StandardCharsets.UTF_8);

        FastJsonConfig config = new FastJsonConfig();
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        config.setSerializeFilters(new UnderScoreNameFilter());
        config.setSerializerFeatures(SerializerFeatureConfig.getDefault());

        converter.setFastJsonConfig(config);
        converters.add(converter);
    }
}
