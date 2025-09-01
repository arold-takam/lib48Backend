package com.k48.lib48.repository;


import com.k48.lib48.dto.HistoryDTO;
import com.k48.lib48.models.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History,Long>{
    List<History> findByOperation(String operation);
    List<History> findByBookTitle(String bookTitle);
    List<History> findByEtatOperation(String etatOperation);

    List<History> findByUsers(String users);
}
