package com.k48.lib48.service;

import com.k48.lib48.dto.CategoryRequestDTO;
import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.Category;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.CategoryRepository;
import com.k48.lib48.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryServices {
	private final CategoryRepository categoryRepo;
	private final UserRepository userRepository;
	private final HistoryService historyService;
	
	public CategoryServices(CategoryRepository categoryRepo, UserRepository userRepository, HistoryService historyService) {
		this.categoryRepo = categoryRepo;
		this.userRepository = userRepository;
		this.historyService = historyService;
	}
 
 
//    ------------------------------------------------------------------------------------------------------------------------------------------------------------
	public Category createCategory(CategoryRequestDTO categoryRequestDTO) {
	validateCategoryRequest(categoryRequestDTO);
	
	Category existingCategory = new Category();
	
	existingCategory.setNom(categoryRequestDTO.nom());
	existingCategory.setDescription(categoryRequestDTO.description());
	
	User gerant = userRepository.findAllByRoleName(Role.GERANT).getFirst();
	HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book ID: " + null, TypeOpperation.AJOUT_CATEGORIE, EtatOpperation.SUCCES, "Categorie ajoutee.");
	historyService.addToHistory(historyRequestDTO);
	
	return categoryRepo.save(existingCategory);
}
	public Category getCategoryByName(String categoryName) {
		Category category = categoryRepo.findByNomIgnoreCase(categoryName);
		if (category == null) {
			throw new NoSuchElementException("Category not found");
		}
		
		return category;
	}
	
	public List<Category> getAllCategories() {
		return categoryRepo.findAll();
	}
	
	public Category getCategoryById(long categoryId) {
		return categoryRepo.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category not found"));
	}
	
	public Category updateCategory(long id, CategoryRequestDTO categoryRequestDTO) {
		
		Category updatedCategory = categoryRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Category not found"));
		validateCategoryRequest(categoryRequestDTO);
		
		updatedCategory.setNom(categoryRequestDTO.nom());
		updatedCategory.setDescription(categoryRequestDTO.description());
		
		User gerant = userRepository.findAllByRoleName(Role.GERANT).getFirst();
		logHistory(gerant.getName(), "Book ID: " + null, TypeOpperation.MODIFICATION_CATEGORIE, EtatOpperation.SUCCES, "Categorie modifiee.");
		
		return categoryRepo.save(updatedCategory);
	}
	
	public void deleteCategory(long categoryId) {
		Category deletedCategory = categoryRepo.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category not found"));
		
		User gerant = userRepository.findAllByRoleName(Role.GERANT).getFirst();
		logHistory(gerant.getName(), "Book ID: " + null, TypeOpperation.SUPPRESSION_CATEGORIE, EtatOpperation.SUCCES, "Categorie supprimee.");
		
		categoryRepo.delete(deletedCategory);
	}
	
//	UTILITIES METHODS-----------------------------------------------------------------------------------------------------------------------------------
	private void logHistory(String userName, String bookRef, TypeOpperation type, EtatOpperation etat, String message) {
		historyService.addToHistory(new HistoryRequestDTO(userName, bookRef, type, etat, message));
	}
	
	private void validateCategoryRequest(CategoryRequestDTO dto) {
		if (dto == null || dto.nom() == null || dto.nom().isBlank()) {
			logHistory("Gerant_Name: N/A", "Book ID: N/A", TypeOpperation.AJOUT_CATEGORIE, EtatOpperation.ECHEC, "Catégorie invalide");
			throw new IllegalArgumentException("La catégorie est invalide");
		}
	}
	
}
