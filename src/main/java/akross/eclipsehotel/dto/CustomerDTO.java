package akross.eclipsehotel.dto;

import akross.eclipsehotel.model.Customer;

import java.time.LocalDate;

public record CustomerDTO(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate createdAt) {

    public CustomerDTO(Customer model) {
        this(
                model.getId(),
                model.getName(),
                model.getEmail(),
                model.getPhone(),
                model.getCreatedAt()
        );
    }

    public Customer toModel() {
        Customer model = new Customer();
        model.setId(id);
        model.setName(name);
        model.setEmail(email);
        model.setPhone(phone);
        model.setCreatedAt(createdAt);
        return model;
    }
}
