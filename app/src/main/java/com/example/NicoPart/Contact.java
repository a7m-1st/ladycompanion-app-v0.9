package com.example.NicoPart;

import java.io.Serializable;
import java.util.Objects;

public class Contact implements Serializable {
    private String name;
    private String phoneNumber;
    private boolean isSelected;

    public Contact(String name, String phoneNumber, boolean isSelected) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(phoneNumber, contact.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }
}
