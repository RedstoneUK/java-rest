package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class GsonSolution {

    public static void main(String[] args) {
        Gson gson = new Gson();
        Address address = new Address("10 High St", "London", "EC1A 1BB");
        Customer customer = new Customer(1, "Alice", "Smith", "alice@example.com", true, address);

        // Exercise 2 — serialise
        System.out.println(gson.toJson(customer));

        // Exercise 3 — deserialise
        String json = gson.toJson(customer);
        Customer parsed = gson.fromJson(json, Customer.class);
        System.out.println(parsed.getFirstName() + " lives in " + parsed.getAddress().getCity());

        // Exercise 3 — unknown field ignored
        String withExtra = "{\"id\":2,\"firstName\":\"Bob\",\"unknownField\":\"ignored\","
            + "\"lastName\":\"Jones\",\"email_address\":\"bob@example.com\",\"active\":false,"
            + "\"address\":{\"street\":\"5 Main Rd\",\"city\":\"Manchester\",\"postcode\":\"M1 1AE\"}}";
        Customer c2 = gson.fromJson(withExtra, Customer.class);
        System.out.println(c2.getFirstName());

        // Exercise 4 — list
        List<Customer> customers = List.of(
            customer,
            new Customer(2, "Bob", "Jones", "bob@example.com", true,
                         new Address("5 Main Rd", "Manchester", "M1 1AE")),
            new Customer(3, "Carol", "White", "carol@example.com", false,
                         new Address("2 Park Lane", "Leeds", "LS1 1AA"))
        );
        String jsonArray = gson.toJson(customers);
        System.out.println(jsonArray);

        Type listType = new TypeToken<List<Customer>>(){}.getType();
        List<Customer> reparsed = gson.fromJson(jsonArray, listType);
        System.out.println("Count: " + reparsed.size());

        // Exercise 5.1 — pretty printing
        Gson pretty = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(pretty.toJson(customer));

        // Exercise 5.2 — null handling
        Customer noEmail = new Customer(4, "Dave", "Brown", null, true, address);
        System.out.println("Null omitted: " + gson.toJson(noEmail));
        System.out.println("Null included: " + new GsonBuilder().serializeNulls().create().toJson(noEmail));
    }
}
