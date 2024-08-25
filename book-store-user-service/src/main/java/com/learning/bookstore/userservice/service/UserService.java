package com.learning.bookstore.userservice.service;

import com.learning.bookstore.common.constants.ErrorCodes;
import com.learning.bookstore.common.exception.ApplicationException;
import com.learning.bookstore.common.exception.ValidationException;
import com.learning.bookstore.common.util.Util;
import com.learning.bookstore.userservice.common.DatabaseStructure;
import com.learning.bookstore.userservice.dto.UserDTO;
import com.learning.bookstore.userservice.entity.User;
import com.learning.bookstore.userservice.repository.UserRepository;
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
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    public UserDTO addUser(UserDTO userDTO) {
        userRepository.save(Util.map(modelMapper, userDTO, User.class));
        return userDTO;
    }


    public void deleteUser(String username, String email) {
        userRepository.deleteByUsernameOrEmail(username, email);
    }

    public UserDTO updateUser(String username, UserRequest userRequest) throws ApplicationException {
        var user = userRepository.getUserByUsername(username);
        if (user == null)
            throw new ApplicationException("No user found", ErrorCodes.NO_DATA_FOUND);
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setFullName(userRequest.getFullName());

        user = userRepository.save(user);
        return Util.map(modelMapper, user, UserDTO.class);
    }

    public UserDTO getUserByUsername(String username) {
        var user = userRepository.getUserByUsername(username);
        return Util.map(modelMapper, user, UserDTO.class);
    }

    public UserDTO getUserByEmail(String email) {
        var user = userRepository.getUserByEmail(email);
        return Util.map(modelMapper, user, UserDTO.class);
    }

    public UserDTO getUserByUsernameOrEmail(String username, String email) throws ApplicationException, ValidationException {

        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

            Root<User> userRoot = criteriaQuery.from(User.class);

            List<Predicate> predicates = new ArrayList<>();

            if (username != null) {
                var userPredicate = criteriaBuilder.equal(userRoot.get(DatabaseStructure.USER.USER_NAME), username);
                predicates.add(userPredicate);
            }
            if (email != null) {
                var emailPredicate = criteriaBuilder.equal(userRoot.get(DatabaseStructure.USER.EMAIL), email);
                predicates.add(emailPredicate);
            }

            if (!predicates.isEmpty())
                criteriaQuery.where(criteriaBuilder.or(predicates.toArray(predicates.toArray(new Predicate[0]))));
            else
                throw new ValidationException("Mandatory parameter is missing", ErrorCodes.MANDATORY_PARAMETER_IS_MISSING);


            TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
            var user = query.getSingleResult();
            return Util.map(modelMapper, user, UserDTO.class);

        } catch (NoResultException exception) {
            throw new ApplicationException("No data found", ErrorCodes.NO_DATA_FOUND);
        }
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> Util.map(modelMapper, user, UserDTO.class)).toList();
    }

}
