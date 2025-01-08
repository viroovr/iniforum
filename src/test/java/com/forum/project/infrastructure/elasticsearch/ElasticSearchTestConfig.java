//package com.forum.project.infrastructure.elasticsearch;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
//
//@TestConfiguration
//public class ElasticSearchTestConfig {
//
//    @Bean
//    public ElasticsearchTemplate elasticsearchTemplate() {
//        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//        return new ElasticsearchTemplate(ElasticsearchClients.create(clientConfiguration));
//    }
//}
