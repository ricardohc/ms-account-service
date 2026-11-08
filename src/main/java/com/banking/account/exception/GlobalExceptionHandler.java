package com.banking.account.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler, Ordered {

    private final ObjectMapper objectMapper;

    @Override
    public int getOrder() {
        return -2;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String code;
        String message;

        if (ex instanceof DomainException domainException) {
            status = domainException.getStatus();
            code = domainException.getCode();
            message = domainException.getMessage();

            if (status.is5xxServerError()) {
                log.error("Dependencia o error de servidor en {}: {} - {}",
                        exchange.getRequest().getPath(), code, message);
            } else {
                log.warn("Regla de negocio rechazada en {}: {} - {}",
                        exchange.getRequest().getPath(), code, message);
            }
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = "INTERNAL_ERROR";
            // No exponer ex.getMessage() al cliente en errores inesperados.
            message = "Unexpected error";
            log.error("Error inesperado procesando {}", exchange.getRequest().getPath(), ex);
        }

        String correlationId = exchange.getRequest().getHeaders()
                .getFirst("X-Correlation-ID");

        ErrorResponse error = ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(Instant.now())
                .correlationId(correlationId)
                .details(List.of())
                .build();

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(error);
        } catch (Exception serializationError) {
            log.error("No se pudo serializar ErrorResponse", serializationError);
            bytes = ("{\"code\":\"" + code + "\",\"message\":\""
                    + escape(message) + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}