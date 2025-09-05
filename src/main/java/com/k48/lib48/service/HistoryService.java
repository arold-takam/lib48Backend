package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.History;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {
	private HistoryRepository historyRepository;
	
	public HistoryService(HistoryRepository historyRepository) {
		this.historyRepository = historyRepository;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addToHistory(HistoryRequestDTO historyRequestDTO){
		if (historyRequestDTO == null){
			throw new IllegalArgumentException("Something went wrong, we will solve it earlier as possible.");
		}
		
		History history = new History();
		history.setBookTitle(historyRequestDTO.bookTitle());
		history.setTypeOpperation(historyRequestDTO.typeOpperation());
		history.setEtatOperation(historyRequestDTO.etatOpperation());
		history.setDateTime(LocalDateTime.now());
		history.setDetails(historyRequestDTO.details());
		history.setUserName(historyRequestDTO.userName());
		
		historyRepository.save(history);
	}
	
	public History getHistoryByID(int historyID){
		if (!historyRepository.existsById(historyID)){
			throw new IllegalArgumentException("No history found at the ID: "+historyID);
		}
		
		return historyRepository.findById(historyID).get();
	}
	
	public List<History>findByTypeOperations(TypeOpperation typeOpperation){
		return historyRepository.findByTypeOpperation(typeOpperation);
	}
	
	public List<History>findByEtatOperation(EtatOpperation etatOpperation){
		return historyRepository.findByEtatOperation(etatOpperation);
	}
	
	public List<History>findByUserName(String userName){
		return historyRepository.findByUserName(userName);
	}
	
}
