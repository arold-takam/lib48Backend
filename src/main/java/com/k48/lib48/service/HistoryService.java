package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryDTO;
import com.k48.lib48.models.History;
import com.k48.lib48.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(  HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }


    public void createHistory(HistoryDTO historyDTO) {
        if(historyDTO == null) {
            throw new IllegalArgumentException("historyDTO cannot be null");
        }

        try {
            History history = new History();
            history.setUser(historyDTO.users());
            history.setBookTitle(historyDTO.bookTitle());
            history.setOperation(historyDTO.operation());
            history.setEtatOperation(historyDTO.etatOperation());
            history.setDateTime(historyDTO.dateTime());
            history.setDetails(historyDTO.details());
            historyRepository.save(history);
        }catch (Exception e){
            System.err.println("Erreur lors de la création de l'historique de l'abonnée: ");
            throw new RuntimeException("Erreur lors de la création de l'historique: " + e.getMessage());
        }
    }
    private HistoryDTO toDTO(History history) {
        return new HistoryDTO(
                history.getUser(),
                history.getBookTitle(),
                history.getOperation(),
                history.getEtatOperation(),
                history.getDateTime(),
                history.getDetails()
        );
    }

    public List<HistoryDTO> getAllHistory() {
        return historyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public HistoryDTO getHistoryById(Long id) {
        Optional<History> history = historyRepository.findById(id);
        if(history.isEmpty()) {
            throw new NoSuchElementException("L'historique n'existe pas");
        }
        return toDTO(history.orElse(null));
    }

    public List<HistoryDTO> getEtatOperation(String etatOperation) {
        List<HistoryDTO> histories = historyRepository.findByEtatOperation(etatOperation).stream().map(this::toDTO).collect(Collectors.toList());
        return histories;
    }

    public List<History> getBookTitle(String bookTitle) {
        List<History> history =historyRepository.findByBookTitle(bookTitle);
        return history;
    }
    public List<History> getOperation(String operation) {
        return (List<History>) historyRepository.findByOperation(operation);
    }

    public List<History> getUser(String users) {
        return (List<History>) historyRepository.findByUsers(users);
    }

    public void deleteHistory(Long id) {
       Optional< History >history = historyRepository.findById(id);
       if (history.isPresent()) {
           historyRepository.delete(history.get());
       }else{
           throw new NoSuchElementException("History not found");
       }
    }

}
