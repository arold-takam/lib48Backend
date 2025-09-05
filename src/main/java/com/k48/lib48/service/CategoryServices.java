package com.k48.lib48.service;

import com.k48.lib48.dto.CategoryRequestDTO;
import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.Category;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.CategoryRepositories;
import com.k48.lib48.repository.UserRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryServices {
	private final CategoryRepositories categoryRepo;
	private final UserRepositories userRepositories;
	private final HistoryService historyService;
	
	public CategoryServices(CategoryRepositories categoryRepo, UserRepositories userRepositories, HistoryService historyService) {
		this.categoryRepo = categoryRepo;
		this.userRepositories = userRepositories;
		this.historyService = historyService;
	}
 
 
//    ------------------------------------------------------------------------------------------------------------------------------------------------------------
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
		return categoryRepo.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not found"));
	}
	
	public Category createCategory(CategoryRequestDTO categoryRequestDTO) {
		if (categoryRequestDTO == null) {
			User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book ID: " + null, TypeOpperation.AJOUT_CATEGORIE, EtatOpperation.ECHEC, "Categorie non ajoutee.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This category is invalid , try again");
		}
		
		Category existingCategory = new Category();
		
		existingCategory.setNom(categoryRequestDTO.nom());
		existingCategory.setDescription(categoryRequestDTO.description());
		
		User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book ID: " + null, TypeOpperation.AJOUT_CATEGORIE, EtatOpperation.SUCCES, "Categorie ajoutee.");
		historyService.addToHistory(historyRequestDTO);
		
		return categoryRepo.save(existingCategory);
	}
	
	public Category updateCategory(long id, CategoryRequestDTO categoryRequestDTO) {
		
		Category updatedCategory = categoryRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Category not found"));
		
		updatedCategory.setNom(categoryRequestDTO.nom());
		updatedCategory.setDescription(categoryRequestDTO.description());
		
		User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book ID: " + null, TypeOpperation.MODIFICATION_CATEGORIE, EtatOpperation.SUCCES, "Categorie modifiee.");
		historyService.addToHistory(historyRequestDTO);
		
		return categoryRepo.save(updatedCategory);
	}
	
	public void deleteCategory(long categoryId) {
		Category deletedCategory = categoryRepo.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category not found"));
		
		User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book ID: " + null, TypeOpperation.SUPPRESSION_CATEGORIE, EtatOpperation.SUCCES, "Categorie supprimee.");
		historyService.addToHistory(historyRequestDTO);
		
		categoryRepo.delete(deletedCategory);
	}
	
}
