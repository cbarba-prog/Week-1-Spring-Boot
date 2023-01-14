package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Order;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql",
    "classpath:flyway/migrations/V1.1__Jeep_Data.sql"}, config = @SqlConfig(encoding = "utf-8"))
public class CreateOrderTest {

  @LocalServerPort
  private int serverPort;
  
  @Autowired
  private TestRestTemplate restTemplate;
  
  @Test
  void testCreateOrderReturnsSuccess201() {
    String body = createOrderBody();
    String uri = String.format("http://localhost:%d/orders", serverPort);
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    
    HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);
    
    ResponseEntity<Order> response = restTemplate.exchange(
        uri, HttpMethod.POST, bodyEntity, Order.class);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    
    Order order = response.getBody();
    assertThat(order.getCustomer().getCustomerId()).isEqualTo("ROTH_GARTH");
    assertThat(order.getModel().getModelId()).isEqualTo(JeepModel.GLADIATOR);
    assertThat(order.getModel().getTrimLevel()).isEqualTo("Rubicon");
    assertThat(order.getModel().getNumDoors()).isEqualTo(4);
    assertThat(order.getColor().getColorId()).isEqualTo("EXT_LASER_BLUE");
    assertThat(order.getEngine().getEngineId()).isEqualTo("3_6_HYBRID");
    assertThat(order.getTire().getTireId()).isEqualTo("33_FALKEN_MT");
    assertThat(order.getOptions()).hasSize(4);
  }
  
  protected String createOrderBody() {
    return "{\n"
        + "  \"customer\":\"ROTH_GARTH\",\n"
        + "  \"model\":\"GLADIATOR\",\n"
        + "  \"trim\":\"Rubicon\",\n"
        + "  \"doors\":4,\n"
        + "  \"color\":\"EXT_LASER_BLUE\",\n"
        + "  \"engine\":\"3_6_HYBRID\",\n"
        + "  \"tire\":\"33_FALKEN_MT\",\n"
        + "  \"options\":[\n"
        + "    \"DOOR_QUAD_4\",\n"
        + "    \"EXT_TACTIK_FRONT_WINCH\",\n"
        + "    \"EXT_MOPAR_STEP_BLACK\",\n"
        + "    \"EXT_QUAD_RACK\"\n"
        + "  ]\n"
        + "}";
  }
}