package com.k48.lib48.service;


import com.k48.lib48.dto.BorrowRequestDTO;
import com.k48.lib48.dto.BorrowResponseDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.models.BorrowBook;
import com.k48.lib48.models.CarteAbonnement;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.BorrowBookRepository;
import com.k48.lib48.repository.UserRepositories;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowBookService {
	private final UserRepositories userRepositories;
	private final BorrowBookRepository borrowBookRepository;
	private final BookRespositories bookRespositories;
	
	private final BookServices bookServices;
	
	public BorrowBookService(UserRepositories userRepositories, BorrowBookRepository borrowBookRepository, BookRespositories bookRespositories, BookServices bookServices) {
		this.userRepositories = userRepositories;
		this.borrowBookRepository = borrowBookRepository;
		this.bookRespositories = bookRespositories;
		this.bookServices = bookServices;
	}
	
	
//	BORROWING MANAGEMENT-----------------------------------------------------------------------------------------------------------------
	public void makeBorrow(int gerantID, BorrowRequestDTO borrowRequestDTO) {
		Optional<User>optionalGerant = userRepositories.findById(gerantID);
		if (optionalGerant.isEmpty() || !optionalGerant.get().getRoleName().equals(Role.GERANT)){
			throw new IllegalArgumentException("Gerant not found with the ID: "+gerantID);
		}
		
		User gerant = optionalGerant.get();
		
		Optional<User>abonneOptional = userRepositories.findById(borrowRequestDTO.abonneID());
		if (abonneOptional.isEmpty() || !abonneOptional.get().getRoleName().equals(Role.ABONNE)){
			throw new IllegalArgumentException("Abonne not found with the ID: "+borrowRequestDTO.abonneID());
		}
		
		User abonne = abonneOptional.get();
		
		Optional<Book> livreAEmprunterOpt = bookRespositories.findById(borrowRequestDTO.bookID());
		if (livreAEmprunterOpt.isEmpty()) {
			throw new IllegalArgumentException("Book not found with the ID: " + borrowRequestDTO.bookID());
		}
		
		Book livreAEmprunter = livreAEmprunterOpt.get();
		if (!livreAEmprunter.isEstDisponible()) {
			throw new IllegalArgumentException("This book is not available.");
		}
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		
		if (carteAbonnement == null){
			throw new IllegalArgumentException("This abonne has no card yet.");
		}
		
		if (!carteAbonnement.isAvailable()){
			throw new IllegalArgumentException("This card is not available.");
		}
		
		Book bookToBorrow = bookServices.getBookId(borrowRequestDTO.bookID());
		
		if (!bookToBorrow.isEstDisponible()){
			throw  new IllegalArgumentException("This book is not available.");
		}
		
		bookToBorrow.setEstDisponible(false);
		bookRespositories.save(bookToBorrow);
		
		BorrowBook borrowBook = new BorrowBook();
		
		borrowBook.setGerant(gerant);
		borrowBook.setAbonne(abonne);
		borrowBook.setBook(bookToBorrow);
		borrowBook.setDateEmprunt(LocalDate.now());
		borrowBook.setDelaiEmprunt(borrowRequestDTO.delaiEmprunt());
		
		borrowBookRepository.save(borrowBook);
		
	}
	
	public BorrowResponseDTO getBorrowByID(int gerantID, int abonneID){
		Optional<BorrowBook>optionalBorrowBook = borrowBookRepository.findByGerant_IdAndAbonne_Id(gerantID, abonneID);
		
		if (optionalBorrowBook.isEmpty()){
			throw new IllegalArgumentException("No borrow for this user yet");
		}
		
		BorrowBook borrowBook = optionalBorrowBook.get();
		
		if (borrowBook.getGerant().getId() != gerantID){
			throw new IllegalArgumentException("This operation is only for Gerant.");
		}
		
		return new BorrowResponseDTO(
			borrowBook.getId(),
			borrowBook.getGerant().getName(),
			borrowBook.getAbonne().getName(),
			borrowBook.getBook().getTitre(),
			borrowBook.getDateEmprunt(),
			borrowBook.getDelaiEmprunt()
		);
	}
	
	public List<BorrowResponseDTO>getAllBorrows(int gerantID){
	
		if (userRepositories.existsById(gerantID) && userRepositories.findById(gerantID).get().getRoleName() != Role.GERANT){
			throw new IllegalArgumentException("This operation is only for Gerant.");
		}
		
		List<BorrowBook>borrowBookList = borrowBookRepository.findAllByGerant_Id(gerantID);
		
		List<BorrowResponseDTO>borrowResponseDTOList = new ArrayList<>();
		
		for (BorrowBook borrow : borrowBookList){
			borrowResponseDTOList.add(
				new BorrowResponseDTO(
					borrow.getId(),
					borrow.getGerant().getName(),
					borrow.getAbonne().getName(),
					borrow.getBook().getTitre(),
					borrow.getDateEmprunt(),
					borrow.getDelaiEmprunt()
				)
			);
		}
		
		return borrowResponseDTOList;
	
	}
}
