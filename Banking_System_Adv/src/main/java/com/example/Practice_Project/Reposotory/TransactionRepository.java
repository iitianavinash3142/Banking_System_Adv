package com.example.Practice_Project.Reposotory;

import com.example.Practice_Project.Entity.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    public List<Transaction> findByUserMobileNo(String mobileNo);
}
