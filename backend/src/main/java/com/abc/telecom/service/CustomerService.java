package com.abc.telecom.service;

import com.abc.telecom.model.Customer;
import com.abc.telecom.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> findById(Long id) { return customerRepository.findById(id); }

    public Customer save(Customer c) { return customerRepository.save(c); }

    public void delete(Long id) { customerRepository.deleteById(id); }
}
