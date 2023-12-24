package com.paymentchain.com.paymentchain.customer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.com.paymentchain.customer.entities.Customer;
import com.paymentchain.com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.com.paymentchain.customer.repositories.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    private final WebClient.Builder webClientBuilder;

    public CustomerController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
            connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });
            
   
    @GetMapping()

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable("id") long id) {
        return customerRepository.findById(id).get();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable long id, @RequestBody Customer input) {
        Customer save = customerRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) {
        input.getProduct().forEach(customer -> customer.setCustomer(input)); //por cada product se le asigne el customer
        Customer save = customerRepository.save(input);
        return ResponseEntity.ok(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        Optional<Customer> findById = customerRepository.findById(id);
        if (findById.get() != null) {
            customerRepository.delete(findById.get());
        }
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/complete")
    public Customer getByCode(@RequestParam String code) {
        Customer customer = customerRepository.findByCode(code);
        List<CustomerProduct> products = customer.getProduct();
        products.forEach(x -> {
            String productName = getProductName(x.getId());
            x.setProductName(productName);
        });
        return customer;
    }
    
    private String getProductName(long id) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8083/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8083/product"))
                .build();
        JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();
        String name = block.get("name").asText();
        return name;
    }

}
