package com.springboot.app.util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.springboot.app.entity.OrderPaid;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class JsonService {

    private final ObjectMapper objectMapper;
    private final Gson gson;
    private final String databasePath = "temp_database.json";

    public JsonService() {
        this.objectMapper = new ObjectMapper();
        this.gson = new Gson();
    }

    public List<OrderPaid> readUsersFromJson() throws IOException {
        ClassPathResource resource = new ClassPathResource(databasePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<OrderPaid>>() {});
        }
    }

    // Ghi dữ liệu sang JSON
    public void writeUsersToJson(List<OrderPaid> users) throws IOException {
        String json = gson.toJson(users);
        File file = resourceFile();
        try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
            writer.write(json);
        }
    }
    private File resourceFile() throws IOException {
        ClassPathResource resource = new ClassPathResource(databasePath);
        return resource.getFile();
    }

    // Cập nhật hoặc thêm mới người dùng
    public void updateUser(OrderPaid newUser) throws IOException {
        List<OrderPaid> users = readUsersFromJson();
        boolean userExists = false;
        for (int i = 0; i < users.size(); i++) {
            OrderPaid user = users.get(i);
            if (user.getOrderCode() == (newUser.getOrderCode())) {
                users.set(i, newUser); // Ghi đè người dùng
                userExists = true;
                break;
            }
        }
        if (!userExists) {
            users.add(newUser); // Thêm mới người dùng
        }
        writeUsersToJson(users);
    }
}