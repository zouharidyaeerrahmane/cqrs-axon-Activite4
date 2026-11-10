package com.zouhari.demoescqrsaxon.query.repository;

import com.zouhari.demoescqrsaxon.query.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperationRepository extends JpaRepository<AccountOperation, Long> {
    @Query("select o from AccountOperation o where o.account.id = :accountId order by o.date, o.id")
    List<AccountOperation> findByAccountId(String accountId);
}
