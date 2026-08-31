package drl.desafio.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CrudIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void crudCategories() throws Exception {
        MvcResult create = mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Supermercado"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SUPERMERCADO"))
                .andReturn();
        String id = objectMapper.readTree(create.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/v1/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPERMERCADO"));

        mockMvc.perform(put("/api/v1/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Alquiler"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ALQUILER"));

        mockMvc.perform(delete("/api/v1/categories/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/categories/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void crudUsers() throws Exception {
        MvcResult create = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Carla D.", "email": "carla@hogar.cl"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String id = objectMapper.readTree(create.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/v1/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(put("/api/v1/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Carla D. U.", "email": "carla.2@hogar.cl"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("carla.2@hogar.cl"));

        mockMvc.perform(delete("/api/v1/users/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @Order(3)
    void crudPurchases() throws Exception {
        MvcResult user = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Pedro S.", "email": "pedro@hogar.cl"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String userId = objectMapper.readTree(user.getResponse().getContentAsString()).get("id").asText();

        MvcResult purchase = mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "%s",
                                  "totalAmount": 90000,
                                  "purchaseDate": "2026-04-10",
                                  "paymentMethod": "DEBIT_CARD",
                                  "financialInstitution": "BANCO_DE_CHILE",
                                  "installmentCount": 3,
                                  "expenseType": "VARIABLE",
                                  "scope": "HOME",
                                  "category": "ELECTRODOMESTICOS"
                                }
                                """.formatted(userId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.installments", hasSize(3)))
                .andReturn();
        String purchaseId = objectMapper.readTree(purchase.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(get("/api/v1/purchases").param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/v1/purchases/{id}", purchaseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installments", hasSize(3)));

        mockMvc.perform(put("/api/v1/purchases/{id}", purchaseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "totalAmount": 120000,
                                  "purchaseDate": "2026-04-10",
                                  "paymentMethod": "CREDIT_CARD",
                                  "financialInstitution": "BCI",
                                  "installmentCount": 2,
                                  "expenseType": "VARIABLE",
                                  "scope": "HOME",
                                  "category": "ELECTRODOMESTICOS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(120000))
                .andExpect(jsonPath("$.installments", hasSize(2)));

        mockMvc.perform(delete("/api/v1/purchases/{id}", purchaseId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/purchases/{id}", purchaseId))
                .andExpect(status().isNotFound());
    }
}
