package com.angubaidullin.repository;

import com.angubaidullin.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a where a.name = :name")
    public List<Account> findByUsername(@Param("name") String name);

}
