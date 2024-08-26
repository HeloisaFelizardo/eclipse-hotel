package akross.eclipsehotel.controller;

import akross.eclipsehotel.dto.CustomerDTO;
import akross.eclipsehotel.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customer Controller", description = "RESTful API for managing customers.")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all registered customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful")
    })
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        var customers = customerService.findAll();
        var customersDto = customers.stream().map(CustomerDTO::new).toList();
        return ResponseEntity.ok(customersDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by ID", description = "Retrieve a specific customer based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
       var customer = customerService.findById(id);
        return ResponseEntity.ok(new CustomerDTO(customer));
    }

    @PostMapping
    @Operation(summary = "Create a new customer", description = "Create a new customer and return the created customers's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "422", description = "Invalid user data provided")
    })
    public ResponseEntity<CustomerDTO> create(@RequestBody CustomerDTO customerDTO) {
        var customer = customerService.create(customerDTO.toModel());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();
        return ResponseEntity.created(location).body(new CustomerDTO(customer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a customer", description = "Update the data of an existing customer based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "422", description = "Invalid user data provided")
    })
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        var customer = customerService.update(id, customerDTO.toModel());
        return ResponseEntity.ok(new CustomerDTO(customer));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer", description = "Delete an existing customer based on its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
