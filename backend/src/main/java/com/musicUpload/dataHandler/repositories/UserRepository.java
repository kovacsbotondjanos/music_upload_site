package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * " +
            "FROM USER " +
            "WHERE username LIKE CONCAT('%', :name, '%') " +
            "ORDER BY " +
            "CASE " +
            "WHEN username LIKE CONCAT(:name, '%') THEN 1 " +
            "ELSE 2 " +
            "END, " +
            "username",
            countQuery = "SELECT COUNT(*) " +
                    "FROM USER " +
                    "WHERE username LIKE CONCAT('%', :name, '%')",
            nativeQuery = true)
    List<User> findByNameLike(@Param("name") String name, Pageable pageable);
}
