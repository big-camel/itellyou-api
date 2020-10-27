package com.itellyou.util;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HttpClient {

    /**
     * 向目的URL发送post请求
     * @param url       目的url
     * @param params    发送的参数
     * @return  AdToutiaoJsonTokenData
     */
    public static String post(String url, MultiValueMap<String, String> params,HttpHeaders headers){
        RestTemplate client = new RestTemplate();
        //新建Http头，add方法可以添加参数
        if(headers == null)
            headers = new HttpHeaders();
        //设置请求发送方式
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>( headers);
        //执行HTTP请求，将返回的结构使用String 类格式化（可设置为对应返回值格式的类）
        ResponseEntity<String> response = client.exchange(url, method, requestEntity,String .class);

        return response.getBody();
    }

    public static String post(String url, MultiValueMap<String, String> params){
        return post(url,params,null);
    }

    /**
     * 向目的URL发送get请求
     * @param url       目的url
     * @param params    发送的参数
     * @param headers   发送的http头，可在外部设置好参数后传入
     * @return  String
     */
    public static String get(String url, MultiValueMap<String, String> params,HttpHeaders headers){
        RestTemplate client = new RestTemplate();

        HttpMethod method = HttpMethod.GET;
        if(headers == null) headers = new HttpHeaders();
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.queryParams(params).build().encode().toUri();
        //执行HTTP请求，将返回的结构使用String 类格式化
        ResponseEntity<String> response = client.exchange(uri, method, requestEntity, String.class);

        return response.getBody();
    }

    public static String get(String url, MultiValueMap<String, String> params){
        return get(url,params,null);
    }
}
