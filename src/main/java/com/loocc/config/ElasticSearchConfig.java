package com.loocc.config;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;

/**
 * 高亮功能整合失败，暂时未做。。。。。
 */


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticSearchConfig {
    @Value("${elasticsearch.host}")
    private String host ;
    private ElasticsearchTemplate template;
    private TransportClient client;
    @Bean("elasticsearchTemplate")
    public ElasticsearchTemplate template(Client client){
        template = new ElasticsearchTemplate(client);
        return template;
    }
    @Bean
    public Client client(){
        try {
            Settings settings = Settings.builder().put("cluster.name","my-elasticsearch").build();
            client = new PreBuiltTransportClient(settings);
            TransportAddress node = new TransportAddress(InetAddress.getByName(host),9300);
            client.addTransportAddress(node);
            return client;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
}
//