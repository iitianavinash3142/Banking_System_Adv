package com.example.Practice_Project.Reposotory;

import com.example.Practice_Project.Entity.TransactionInES;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TransactionRepositoryInES extends ElasticsearchRepository<TransactionInES, Integer> {
    Iterable<TransactionInES> findByUserMobileNo(String mobileNo);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"user.mobileNo\": \"?0\"}}]}}")
    Page<TransactionInES> findMiniStatement(String name, Pageable pageable);
}
