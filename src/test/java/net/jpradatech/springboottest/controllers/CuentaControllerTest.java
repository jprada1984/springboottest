package net.jpradatech.springboottest.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.jpradatech.springboottest.Datos;
import net.jpradatech.springboottest.models.Cuenta;
import net.jpradatech.springboottest.models.TransaccionDto;
import net.jpradatech.springboottest.services.CuentaService;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private CuentaService cuentaService;

  ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void testDetalle() throws Exception {
    when(cuentaService.findById(1L)).thenReturn(Datos.createCuenta001().orElseThrow());

    mvc.perform(
            MockMvcRequestBuilders.get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpectAll(content().contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(jsonPath("$.persona").value("Jimmy"))
        .andExpectAll(jsonPath("$.saldo").value("1000"));

    verify(cuentaService).findById(1L);
  }

  @Test
  void testTransferir() throws Exception {
    TransaccionDto dto = new TransaccionDto();
    dto.setCuentaOrigenId(1L);
    dto.setCuentaDestinoId(2L);
    dto.setBancoId(1L);
    dto.setMonto(new BigDecimal("100"));

    System.out.println(objectMapper.writeValueAsString(dto));

    Map<String, Object> response = new HashMap<>();
    response.put("date", LocalDate.now().toString());
    response.put("status", "OK");
    response.put("mensaje", "Transferencia realizada con éxito!");
    response.put("transaccion", dto);

    System.out.println(objectMapper.writeValueAsString(response));

    mvc.perform(post("/api/cuentas/transferir")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))

        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
        .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con éxito!"))
        .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(1L))
        .andExpect(content().json(objectMapper.writeValueAsString(response)));
  }

  @Test
  void testFindAll() throws Exception {
    List<Cuenta> cuentas =
        Arrays.asList(Datos.createCuenta001().orElseThrow(), Datos.createCuenta002().orElseThrow());
    when(cuentaService.findAll()).thenReturn(cuentas);

		mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].persona").value("Jimmy"))
				.andExpect(jsonPath("$[1].persona").value("Lorena"))
				.andExpect(jsonPath("$[0].saldo").value("1000"))
				.andExpect(jsonPath("$[1].saldo").value("2000"))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(content().json(objectMapper.writeValueAsString(cuentas)));
		
		verify(cuentaService).findAll();
  }
  
  @Test
  void testGuardar() throws JsonProcessingException, Exception {
	  Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
	  when(cuentaService.save(any())).then(invocation->{
		  Cuenta c = invocation.getArgument(0);
		  c.setId(3L);
		  return c;
	  });
	  
	  mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
			  .content(objectMapper.writeValueAsString(cuenta)))
	  .andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
	  .andExpect(jsonPath("$.id", is(3)))
	  .andExpect(jsonPath("$.persona", is("Pepe"))).andExpect(jsonPath("$.saldo", is(3000)));
	  
	  verify(cuentaService).save(any());
  }
}
