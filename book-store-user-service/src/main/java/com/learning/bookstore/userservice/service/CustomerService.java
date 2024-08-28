package com.learning.bookstore.userservice.service;

import com.learning.bookstore.common.constants.DatabaseStructure;
import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.dto.CustomerDTO;
import com.learning.bookstore.common.entity.Customer;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.util.Util;
import com.learning.bookstore.userservice.repository.CustomerRepository;
import com.learning.bookstore.userservice.request.UserRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        customerRepository.save(Util.map(modelMapper, customerDTO, Customer.class));
        return customerDTO;
    }


    public void deleteCustomer(String username, String email) {
        customerRepository.deleteByUsernameOrEmail(username, email);
    }

    public CustomerDTO updateCustomer(String username, UserRequest userRequest) throws ApplicationException {
        var customer = customerRepository.getCustomerByUsername(username);
        if (customer == null)
            throw new ApplicationException("No customer found", ErrorCodes.NO_DATA_FOUND);
        customer.setEmail(userRequest.getEmail());
        customer.setPassword(userRequest.getPassword());
        customer.setFullName(userRequest.getFullName());

        customer = customerRepository.save(customer);
        return Util.map(modelMapper, customer, CustomerDTO.class);
    }

    public CustomerDTO getCustomerByUsername(String username) {
        var customer = customerRepository.getCustomerByUsername(username);
        return Util.map(modelMapper, customer, CustomerDTO.class);
    }

    public CustomerDTO getCustomerByEmail(String email) {
        var customer = customerRepository.getCustomerByEmail(email);
        return Util.map(modelMapper, customer, CustomerDTO.class);
    }

    public CustomerDTO getCustomerByUsernameOrEmail(String username, String email) throws ApplicationException, ValidationException {

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Customer> criteriaQuery = criteriaBuilder.createQuery(Customer.class);

            Root<Customer> userRoot = criteriaQuery.from(Customer.class);

            List<Predicate> predicates = new ArrayList<>();

            if (username != null) {
                var userPredicate = criteriaBuilder.equal(userRoot.get(DatabaseStructure.CUSTOMER.USER_NAME), username);
                predicates.add(userPredicate);
            }
            if (email != null) {
                var emailPredicate = criteriaBuilder.equal(userRoot.get(DatabaseStructure.CUSTOMER.EMAIL), email);
                predicates.add(emailPredicate);
            }

            if (!predicates.isEmpty())
                criteriaQuery.where(criteriaBuilder.or(predicates.toArray(predicates.toArray(new Predicate[0]))));
            else
                throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);


            TypedQuery<Customer> query = entityManager.createQuery(criteriaQuery);
            var customer = query.getSingleResult();
            return Util.map(modelMapper, customer, CustomerDTO.class);

        } catch (NoResultException exception) {
            throw new ApplicationException("No data found", ErrorCodes.NO_DATA_FOUND);
        }
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> Util.map(modelMapper, customer, CustomerDTO.class)).toList();
    }

}
