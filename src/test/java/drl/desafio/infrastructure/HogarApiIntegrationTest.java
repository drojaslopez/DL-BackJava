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

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HogarApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void createsUser() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Felipe Gomez", "email": "felipe@hogar.cl"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Felipe Gomez"))
                .andExpect(jsonPath("$.email").value("felipe@hogar.cl"));
    }

    @Test
    @Order(2)
    void registersPurchaseInThreeInstallments() throws Exception {
        MvcResult user = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Ana S.", "email": "ana@hogar.cl"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String userId = objectMapper.readTree(user.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "%s",
                                  "totalAmount": 150000,
                                  "purchaseDate": "2026-03-01",
                                  "paymentMethod": "CREDIT_CARD",
                                  "financialInstitution": "BANCO_DE_CHILE",
                                  "installmentCount": 3,
                                  "expenseType": "VARIABLE",
                                  "scope": "HOME",
                                  "category": "ELECTRODOMESTICOS"
                                }
                                """.formatted(userId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(150000))
                .andExpect(jsonPath("$.installmentCount").value(3))
                .andExpect(jsonPath("$.installments", hasSize(3)))
                .andExpect(jsonPath("$.installments[0].amount").value(50000))
                .andExpect(jsonPath("$.installments[2].period").value("2026-05"));
    }

    @Test
    @Order(3)
    void queriesMonthlyDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/reports/dashboard").param("month", "3").param("year", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").value("2026-03"))
                .andExpect(jsonPath("$.monthTotal").exists());
    }

    @Test
    @Order(4)
    void queriesFutureProjection() throws Exception {
        MvcResult user = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Luis M.", "email": "luis@hogar.cl"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String userId = objectMapper.readTree(user.getResponse().getContentAsString()).get("id").asText();

        YearMonth current = YearMonth.now();
        String date = current.atDay(1).toString();
        String secondMonth = current.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "%s",
                                  "totalAmount": 90000,
                                  "purchaseDate": "%s",
                                  "paymentMethod": "TRANSFER",
                                  "financialInstitution": "BCI",
                                  "installmentCount": 2,
                                  "expenseType": "FIXED",
                                  "scope": "PERSONAL",
                                  "category": "SALUD"
                                }
                                """.formatted(userId, date)))
                .andExpect(status().isCreated());

        String body = mockMvc.perform(get("/api/v1/reports/projection").param("months", "6"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode projections = objectMapper.readTree(body).get("projections");

        boolean containsSecondMonth = false;
        for (JsonNode p : projections) {
            if (p.get("period").asText().equals(secondMonth)) {
                containsSecondMonth = true;
                break;
            }
        }
        org.hamcrest.MatcherAssert.assertThat("The projection must include the second month",
                containsSecondMonth, org.hamcrest.Matchers.is(true));
    }

    @Test
    @Order(5)
    void rejectsPurchaseFromNonexistentUser() throws Exception {
        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "00000000-0000-0000-0000-000000000000",
                                  "totalAmount": 100,
                                  "purchaseDate": "2026-01-01",
                                  "paymentMethod": "CASH",
                                  "financialInstitution": "CASH",
                                  "installmentCount": 1,
                                  "expenseType": "VARIABLE",
                                  "scope": "HOME",
                                  "category": "X"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
