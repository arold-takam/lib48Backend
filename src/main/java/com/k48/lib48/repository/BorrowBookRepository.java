package com.k48.lib48.repository;

import com.k48.lib48.models.BorrowBook;
import com.k48.lib48.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowBookRepository extends JpaRepository<BorrowBook, Integer> {
	
	Optional<BorrowBook> findByAbonne_Id(int abonneID);
	
	Optional<BorrowBook>findByGerant_IdAndAbonne_Id(int gerantID, int abonneID);
	
	List<BorrowBook>findAllByGerant_Id(int gerantID);
}
