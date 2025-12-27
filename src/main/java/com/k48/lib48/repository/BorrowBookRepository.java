package com.k48.lib48.repository;

import com.k48.lib48.models.BorrowBook;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowBookRepository extends JpaRepository<BorrowBook, Integer> {

	Optional<BorrowBook> findByIdAndAbonne_Id(int borrowId, int abonneId);
	
	List<BorrowBook>findAllByGerant_Id(int gerantID);

	List<BorrowBook> findAllByAbonne_Id(int abonneId);
	
	List<BorrowBook> findAllByStatus(BorrowStatus status);
	
	void deleteAllByAbonne(User abonne);
}
