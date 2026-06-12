package com.example.demo;

import com.google.gson.annotations.SerializedName;

public class Customer {

    private int id;
    private String firstName;
    private String lastName;
    @SerializedName("email_address")
    private String email;
    private boolean active;
    private Address address;

    public Customer(int id, String firstName, String lastName,
                    String email, boolean active, Address address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.address = address;
    }

    public int getId()           { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getEmail()     { return email; }
    public boolean isActive()    { return active; }
    public Address getAddress()  { return address; }
}
