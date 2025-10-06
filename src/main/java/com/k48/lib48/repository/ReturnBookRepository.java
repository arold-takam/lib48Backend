package com.k48.lib48.repository;

import com.k48.lib48.models.BorrowBook;
import com.k48.lib48.models.ReturnBook;
import com.k48.lib48.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnBookRepository extends JpaRepository<ReturnBook, Integer> {

	List<ReturnBook> findByBorrowBookConcerned_Abonne(User abonne);
	
	List<ReturnBook> findAllByDateRetour(LocalDate dateRetour);
	
	Optional<ReturnBook> findByBorrowBookConcerned(BorrowBook borrowBookConcerned);
	
	void deleteAllByBorrowBookConcerned_Abonne(User abonne);
}
