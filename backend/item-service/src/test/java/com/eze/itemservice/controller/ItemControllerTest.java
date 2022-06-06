package com.eze.itemservice.controller;

import static org.mockito.Mockito.*;

import com.eze.itemservice.domain.Category;
import com.eze.itemservice.domain.Item;
import com.eze.itemservice.exception.ApiException;
import com.eze.itemservice.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

@WebMvcTest
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    private Item item0;
    private List<Item> items;
    private static String BASE_URI;
    private static ObjectMapper mapper;
    private static MultiValueMap<String, String> headers;

    @BeforeAll
    static void setupAll() {
        mapper = new ObjectMapper();
        headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add("X-auth-username", "randomUsername");
        headers.add("X-auth-role", "ROLE_ADMIN");
        BASE_URI = "/api/v1/items";
    }

    @BeforeEach
    void setup() {
        Category category = new Category("C1", "KEY");
        item0 = new Item("itemCode0", BigInteger.valueOf(100), BigInteger.valueOf(100), "description0", category, false);
        Item item1 = new Item("itemCode1", BigInteger.valueOf(200), BigInteger.valueOf(200), "description1", category, false);
        Item item2 = new Item("itemCode2", BigInteger.valueOf(100), BigInteger.valueOf(100), "description2", category, true);
        items = List.of(item0, item1, item2);
    }

    @DisplayName("fetch Items with Items present returns 200 OK with items")
    @Test
    @WithMockUser(roles = "USER")
    void getAllItems_withItemsPresent_returnItems() throws Exception {
        when(itemService.findItems()).thenReturn(items);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(items)));
    }

    @DisplayName("fetch Item with valid ItemCode returns 200 OK with Item")
    @Test
    @WithMockUser(roles = "USER")
    void getItem_withValidItemCode_returnsOkWithItem() throws Exception {
        String validItemCode = item0.getItemCode();
        when(itemService.findItem(validItemCode)).thenReturn(item0);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/" + validItemCode)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(item0)));
    }

    @DisplayName("fetch Item with invalid ItemCode returns 404 NOT FOUND")
    @Test
    @WithMockUser(roles = "USER")
    void getItem_withInvalidItemCode_returnsNotFound() throws Exception {
        String invalidItemCode = item0.getItemCode();
        when(itemService.findItem(invalidItemCode)).thenThrow(new ApiException("Item not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URI + "/" + invalidItemCode)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("create new Item using account role USER and returns 403 FORBIDDEN")
    @Test
    @WithMockUser(roles = "USER")
    void createItem_usingUser_returnForbidden() throws Exception {
        when(itemService.createItem(item0)).thenReturn(item0);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI)
                        .content(mapper.writeValueAsString(item0))
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @DisplayName("create new Item using account role not USER and returns 201 CREATED with Item")
    @Test
    @WithMockUser(roles = "ADMIN")
    void createItem_withNewItemUsingNotUser_returnCreatedWithItem() throws Exception {
        when(itemService.createItem(item0)).thenReturn(item0);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI)
                        .content(mapper.writeValueAsString(item0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(item0)));
    }

    @DisplayName("create existing Item using account role not USER and returns 400 BAD REQUEST")
    @Test
    @WithMockUser(roles = "ADMIN")
    void createItem_withExistingItemUsingNotUser_returnBadRequest() throws Exception {
        when(itemService.createItem(item0)).thenThrow(new ApiException("Item already exist", HttpStatus.BAD_REQUEST));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URI)
                        .content(mapper.writeValueAsString(item0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("update Item using account role USER and returns 403 FORBIDDEN")
    @Test
    @WithMockUser(roles = "USER")
    void updateItem_usingUser_returnForbidden() throws Exception {
        when(itemService.updateItem(item0)).thenReturn(item0);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URI)
                        .content(mapper.writeValueAsString(item0))
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @DisplayName("update existing Item using account NOT User and returns 200 OK")
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateItem_withExistingItemUsingNotUser_returnsOk() throws Exception{
        when(itemService.updateItem(item0)).thenReturn(item0);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URI)
                        .content(mapper.writeValueAsString(item0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("update non-existing Item using account NOT User and returns 404 NOT FOUND")
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateItem_withNonExistingItemUsingNotUser_returnsNotFound() throws Exception{
        when(itemService.updateItem(item0)).thenThrow(new ApiException("Item not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URI)
                        .content(mapper.writeValueAsString(item0))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("delete Item using account role USER and returns 403 FORBIDDEN")
    @Test
    @WithMockUser(roles = "USER")
    void deleteItem_usingUser_returnForbidden() throws Exception {
        when(itemService.deleteItem(item0.getItemCode())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + "/" + item0.getItemCode())
                        .content(mapper.writeValueAsString(item0))
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @DisplayName("delete existing Item using account not USER and returns 200 OK")
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_withExistingItemUsingNotUser_returnOk() throws Exception {
        when(itemService.deleteItem(item0.getItemCode())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + "/" + item0.getItemCode())
                        .content(mapper.writeValueAsString(item0))
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @DisplayName("delete non-existing Item using account not USER and returns 404 NOT FOUND")
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteItem_withNonExistingItemUsingNotUser_returnNotFound() throws Exception {
        when(itemService.deleteItem(item0.getItemCode())).thenThrow(new ApiException("Item not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI + "/" + item0.getItemCode())
                        .content(mapper.writeValueAsString(item0))
                        .headers(HttpHeaders.readOnlyHttpHeaders(headers)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
