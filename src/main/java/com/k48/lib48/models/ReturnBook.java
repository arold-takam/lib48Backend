package com.k48.lib48.models;

import com.k48.lib48.myEnum.EtatLivre;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "return_book")
public class ReturnBook {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "gerant_id")
	private int gerantReturningID;
	
	@Column(name = "etat_livre")
	@Enumerated(EnumType.STRING)
	private EtatLivre nouvelEtatLivre;
	
	@Column(name = "date_retour")
	private LocalDate dateRetour;
	
	@OneToOne
	@JoinColumn(name = "borrow_book_id")
	private BorrowBook borrowBookConcerned;
	
	
	public ReturnBook() {
	}
	
	public ReturnBook(int gerantReturningID, EtatLivre nouvelEtatLivre, LocalDate dateRetour, BorrowBook borrowBookConcerned) {
		this.gerantReturningID = gerantReturningID;
		this.nouvelEtatLivre = nouvelEtatLivre;
		this.dateRetour = dateRetour;
		this.borrowBookConcerned = borrowBookConcerned;
	}
	
	public int getId() {
		return id;
	}
	
	public int getGerantReturningID() {
		return gerantReturningID;
	}
	
	public void setGerantReturningID(int gerantReturningID) {
		this.gerantReturningID = gerantReturningID;
	}
	
	public EtatLivre getNouvelEtatLivre() {
		return nouvelEtatLivre;
	}
	
	public void setNouvelEtatLivre(EtatLivre nouvelEtatLivre) {
		this.nouvelEtatLivre = nouvelEtatLivre;
	}
	
	public LocalDate getDateRetour() {
		return dateRetour;
	}
	
	public void setDateRetour(LocalDate dateRetour) {
		this.dateRetour = dateRetour;
	}
	
	public BorrowBook getBorrowBookConcerned() {
		return borrowBookConcerned;
	}
	
	public void setBorrowBookConcerned(BorrowBook borrowBookConcerned) {
		this.borrowBookConcerned = borrowBookConcerned;
	}
}
