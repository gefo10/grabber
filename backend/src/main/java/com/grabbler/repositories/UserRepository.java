package com.grabbler.repositories;

import com.grabbler.models.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.addresses a WHERE a.addressId = ?1")
    List<User> findByAddress(Long addressId);

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = { "addresses", "cart", "roles" })
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithDetails(@Param("email") String email);

    Optional<User> findByUserId(Long userId);
}
