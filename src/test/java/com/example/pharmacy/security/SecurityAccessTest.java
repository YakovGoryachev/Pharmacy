package com.example.pharmacy.security;

import com.example.pharmacy.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class SecurityAccessTest {

    @Autowired WebApplicationContext context;
    @Autowired UserRepository userRepository;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void cashierRequiresAuth() throws Exception {
        mockMvc.perform(get("/cashier")).andExpect(status().is3xxRedirection());
    }

    @Test
    void pharmacistCanAccessCashier() throws Exception {
        var details = new PharmaUserDetails(userRepository.findByLoginWithDetails("pharm1").orElseThrow());
        mockMvc.perform(get("/cashier").with(user(details))).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanAccessUsers() throws Exception {
        mockMvc.perform(get("/admin/users")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "pharm1", roles = "PHARMACIST")
    void pharmacistCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin/users")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCannotAccessCashier() throws Exception {
        mockMvc.perform(get("/cashier")).andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessNomenclature() throws Exception {
        var details = new PharmaUserDetails(userRepository.findByLoginWithDetails("admin").orElseThrow());
        mockMvc.perform(get("/nomenclature").with(user(details))).andExpect(status().isOk());
        mockMvc.perform(get("/nomenclature/create").with(user(details))).andExpect(status().isOk());
        mockMvc.perform(get("/nomenclature/categories").with(user(details))).andExpect(status().isOk());
        mockMvc.perform(post("/nomenclature/save/close")
                        .with(user(details))
                        .with(csrf())
                        .param("brandName", "TestDrug")
                        .param("mnn", "TestMnn")
                        .param("formOfRelease", "TABLET")
                        .param("dosageUnit", "мг"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void accountantCanAccessReports() throws Exception {
        var details = new PharmaUserDetails(userRepository.findByLoginWithDetails("accountant").orElseThrow());
        mockMvc.perform(get("/reports").with(user(details))).andExpect(status().isOk());
        mockMvc.perform(get("/reports/history").with(user(details))).andExpect(status().isOk());
    }

    @Test
    void managerCanAccessReports() throws Exception {
        var details = new PharmaUserDetails(userRepository.findByLoginWithDetails("manager1").orElseThrow());
        mockMvc.perform(get("/reports").with(user(details))).andExpect(status().isOk());
    }

    @Test
    void testRoleCanAccessAllSections() throws Exception {
        var details = new PharmaUserDetails(userRepository.findByLoginWithDetails("test").orElseThrow());
        mockMvc.perform(get("/dashboard").with(user(details))).andExpect(status().isOk());
        mockMvc.perform(get("/cashier").with(user(details))).andExpect(status().isOk());
        mockMvc.perform(get("/batches").with(user(details))).andExpect(status().isOk());
        mockMvc.perform(get("/admin/users").with(user(details))).andExpect(status().isOk());
    }
}
