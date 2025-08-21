package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.DishHandler;
import com.plazoleta.microservicio_plazoleta.application.handler.impl.RestaurantHandler;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "security.jwt.secret=clavesecretaparamicroserviciosdeplazoletadedecomidas",
})
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantHandler restaurantHandler;

    @MockitoBean
    private DishHandler dishHandler;

    private Key key;
    private ObjectMapper om;

    @BeforeEach
    void setUp() {
        byte[] secret = "clavesecretaparamicroserviciosdeplazoletadedecomidas".getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(secret);
        om = new ObjectMapper();
        Mockito.doNothing().when(restaurantHandler).saveRestaurant(Mockito.any());
        Mockito.doNothing().when(dishHandler).saveDish(Mockito.any());
        Mockito.doNothing().when(dishHandler).updateDish(Mockito.any());
    }


    private String jwt(String email, Long uid, List<String> roles, long minutesValid) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(email)
                .claim("uid", uid)
                .claim("roles", roles)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + minutesValid * 60_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void crearRestaurante_sinToken_ret_401() throws Exception {
        mockMvc.perform(post("/api/v1/restaurants/saveRestaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(minRestaurantReq())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearRestaurante_conRolCliente_ret_403() throws Exception {
        String token = jwt("cli@mail.com", 10L, List.of("ROLE_CLIENTE"), 10);
        mockMvc.perform(post("/api/v1/restaurants/saveRestaurant")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(minRestaurantReq())))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearRestaurante_conRolAdmin_ret_201() throws Exception {
        String token = jwt("admin@mail.com", 1L, List.of("ROLE_ADMINISTRADOR"), 10);
        mockMvc.perform(post("/api/v1/restaurants/saveRestaurant")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(minRestaurantReq())))
                .andExpect(status().isCreated());
    }

    @Test
    void crearPlato_sinToken_ret_401() throws Exception {
        mockMvc.perform(post("/api/v1/dishes/saveDish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(minDishReq())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearPlato_conRolAdmin_ret_403() throws Exception {
        String token = jwt("admin@mail.com", 1L, List.of("ROLE_ADMINISTRADOR"), 10);
        mockMvc.perform(post("/api/v1/dishes/saveDish")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(minDishReq())))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearPlato_conRolOwner_ret_201() throws Exception {
        String token = jwt("owner@mail.com", 5L, List.of("ROLE_PROPIETARIO"), 10);
        mockMvc.perform(post("/api/v1/dishes/saveDish")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(minDishReq())))
                .andExpect(status().isCreated());
    }

    @Test
    void actualizarPlato_conRolOwner_ret_201() throws Exception {
        String token = jwt("owner@mail.com", 5L, List.of("ROLE_PROPIETARIO"), 10);
        Map<String, Object> patch = Map.of(
                "price", 12345,
                "description", "Nuevo texto"
        );
        mockMvc.perform(patch("/api/v1/dishes/updateDish/{id}", 99)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(patch)))
                .andExpect(status().isCreated());
    }

    private Map<String, Object> minRestaurantReq() {
        return Map.of(
                "name", "La Esquina",
                "address", "Calle 123",
                "idOwner", 5,
                "phone", "+573001112233",
                "urlLogo", "http://logo.png",
                "nit", "900123456"
        );
    }

    private Map<String, Object> minDishReq() {
        return Map.of(
                "name", "Bandeja",
                "description", "Grande",
                "price", 25000,
                "urlImage", "http://img.png",
                "restaurantId", 1,
                "categoryId", 2
        );
    }
}

