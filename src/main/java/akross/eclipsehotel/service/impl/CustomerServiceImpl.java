package akross.eclipsehotel.service.impl;

import akross.eclipsehotel.exception.BusinessException;
import akross.eclipsehotel.exception.NotFoundException;
import akross.eclipsehotel.model.Customer;
import akross.eclipsehotel.repository.CustomerRepository;
import akross.eclipsehotel.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            logger.info("No customers found in the database.");
        } else {
            logger.info("Retrieved {} customers from the database.", customers.size());
        }
        return customers;
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        logger.info("Searching for customer with ID: {}", id);
        return customerRepository.findById(id).orElseThrow(() -> {
            logger.error("Customer not found with ID: {}", id);
            return new NotFoundException("Customer not found with ID: " + id);
        });
    }

    @Transactional
    public Customer create(Customer customerToCreate) {
        logger.info("Creating a new customer: {}", customerToCreate);

        ofNullable(customerToCreate).orElseThrow(() -> {
            logger.error("Customer to create must not be null.");
            return new BusinessException("Customer to create must not be null.");
        });
        ofNullable(customerToCreate.getName()).orElseThrow(() -> {
            logger.error("Customer name must not be null.");
            return new BusinessException("Customer name must not be null.");
        });
        ofNullable(customerToCreate.getEmail()).orElseThrow(() -> {
            logger.error("Customer email must not be null.");
            return new BusinessException("Customer email must not be null.");
        });
        ofNullable(customerToCreate.getPhone()).orElseThrow(() -> {
            logger.error("Customer phone must not be null.");
            return new BusinessException("Customer phone must not be null.");
        });

        if (customerRepository.existsByEmail(customerToCreate.getEmail())) {
            logger.error("Customer with email {} already exists.", customerToCreate.getEmail());
            throw new BusinessException("This email already exists.");
        }

        Customer createdCustomer = customerRepository.save(customerToCreate);
        logger.info("Customer created successfully with ID: {}", createdCustomer.getId());
        return createdCustomer;
    }

    @Transactional
    public Customer update(Long id, Customer customerToUpdate) {
        logger.info("Updating customer with ID: {}", id);
        Customer dbCustomer = findById(id);

        if (!dbCustomer.getId().equals(customerToUpdate.getId())) {
            logger.error("Update IDs must be the same. Provided ID: {}, Existing ID: {}", customerToUpdate.getId(), dbCustomer.getId());
            throw new BusinessException("Update IDs must be the same.");
        }

        dbCustomer.setName(customerToUpdate.getName());
        dbCustomer.setEmail(customerToUpdate.getEmail());
        dbCustomer.setPhone(customerToUpdate.getPhone());
        dbCustomer.setCreatedAt(customerToUpdate.getCreatedAt());

        Customer updatedCustomer = customerRepository.save(dbCustomer);
        logger.info("Customer with ID: {} updated successfully.", id);
        return updatedCustomer;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Deleting customer with ID: {}", id);
        Customer dbCustomer = findById(id);
        customerRepository.delete(dbCustomer);
        logger.info("Customer with ID: {} deleted successfully.", id);
    }
}
