package com.akshadip.helios.config;

import io.etcd.jetcd.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdConfig {
    @Bean
    public Client etcdClient(@Value("${helios.etcd.endpoint}") String etcdEndpoint) {
        return Client.builder().endpoints(etcdEndpoint).build();
    }
}
