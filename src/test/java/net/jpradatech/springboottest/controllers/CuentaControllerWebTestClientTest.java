package net.jpradatech.springboottest.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import net.jpradatech.springboottest.models.TransaccionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {

  @Autowired
  private WebTestClient client;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void testTransferir() throws JsonProcessingException {
    TransaccionDto dto = new TransaccionDto();
    dto.setCuentaOrigenId(1L);
    dto.setCuentaDestinoId(2L);
    dto.setMonto(new BigDecimal("100"));
    dto.setBancoId(1L);

    Map<String, Object> response = new HashMap<>();
    response.put("date", LocalDate.now().toString());
    response.put("status", "OK");
    response.put("mensaje", "Transferencia realizada con éxito!");
    response.put("transaccion", dto);

    client.post().uri("/api/cuentas/transferir")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(dto)
        .exchange().expectStatus().isOk()
        .expectBody().jsonPath("$.mensaje").isNotEmpty()
        .jsonPath("$.mensaje").value(is("Transferencia realizada con éxito!"))
        .jsonPath("$.mensaje")
        .value(valor -> assertEquals("Transferencia realizada con éxito!", valor))
        .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con éxito!")
        .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(dto.getCuentaOrigenId())
        .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
        .json(objectMapper.writeValueAsString(response));
  }
}