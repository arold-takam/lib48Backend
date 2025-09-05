package com.k48.lib48.repository;


import com.k48.lib48.models.History;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.TypeOpperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer>{
	List<History> findByTypeOpperation(TypeOpperation operation);

	List<History> findByEtatOperation(EtatOpperation etatOpperation);
	
	List<History> findByUserName(String userName);
}
