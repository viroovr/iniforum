//package com.forum.project.infrastructure.elasticsearch;
//
//
//import org.junit.jupiter.api.Test;
//
//public class ElasticSearchTest {
//    @Test
//    public void testElasticsearch() {
//        try (ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.9.0")) {
//            elasticsearch.start();
//
//            String httpHostAddress = elasticsearch.getHttpHostAddress();
//            System.out.println("Elasticsearch is running at: " + httpHostAddress);
//
//            // Elasticsearch Rest Client를 사용해 테스트 수행
//        }
//    }
//}
