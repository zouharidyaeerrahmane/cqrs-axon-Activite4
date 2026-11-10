package com.zouhari.demoescqrsaxon.query.repository;

import com.zouhari.demoescqrsaxon.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
