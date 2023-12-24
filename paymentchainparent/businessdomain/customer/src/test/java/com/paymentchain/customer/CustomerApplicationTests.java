package com.paymentchain.customer;

import com.paymentchain.com.paymentchain.customer.CustomerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CustomerApplication.class) // Reemplaza con tu clase principal de la aplicación
class CustomerApplicationTests {

    @Test
    void contextLoads() {
        // Tu lógica de prueba
    }
}
