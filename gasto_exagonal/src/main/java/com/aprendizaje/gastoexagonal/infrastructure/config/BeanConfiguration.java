package com.aprendizaje.gastoexagonal.infrastructure.config;

import com.aprendizaje.gastoexagonal.application.port.in.ConsultarCategoriasUseCase;
import com.aprendizaje.gastoexagonal.application.port.in.GestionGastosUseCase;
import com.aprendizaje.gastoexagonal.application.port.out.GastoRepository;
import com.aprendizaje.gastoexagonal.application.service.CategoriaService;
import com.aprendizaje.gastoexagonal.application.service.GastoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BeanConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public GestionGastosUseCase gestionGastosUseCase(GastoRepository repository, Clock clock) {
        return new TransactionalGastoUseCase(new GastoService(repository, clock));
    }

    @Bean
    public ConsultarCategoriasUseCase consultarCategoriasUseCase() {
        return new CategoriaService();
    }
}
