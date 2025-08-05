package com.k48.lib48.models;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "borrow_book")
public class BorrowBook {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "gerant_id")
	private User gerant;
	
	@ManyToOne
	@JoinColumn(name = "abonne_id")
	private User abonne;
	
	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;
	
	@Column(name = "date_emprunt")
	private LocalDate dateEmprunt;
	
	@Column(name = "delai_emprunt")
	private int delaiEmprunt;
	
	public BorrowBook() {
	}
	
	public BorrowBook(User gerant, User abonne, Book book, LocalDate dateEmprunt, int delaiEmprunt) {
		this.gerant = gerant;
		this.abonne = abonne;
		this.book = book;
		this.dateEmprunt = dateEmprunt;
		this.delaiEmprunt = delaiEmprunt;
	}
	
	public int getId() {
		return id;
	}
	
	public User getGerant() {
		return gerant;
	}
	
	public void setGerant(User gerant) {
		this.gerant = gerant;
	}
	
	public User getAbonne() {
		return abonne;
	}
	
	public void setAbonne(User abonne) {
		this.abonne = abonne;
	}
	
	public Book getBook() {
		return book;
	}
	
	public void setBook(Book book) {
		this.book = book;
	}
	
	public LocalDate getDateEmprunt() {
		return dateEmprunt;
	}
	
	public void setDateEmprunt(LocalDate dateEmprunt) {
		this.dateEmprunt = dateEmprunt;
	}
	
	public int getDelaiEmprunt() {
		return delaiEmprunt;
	}
	
	public void setDelaiEmprunt(int delaiEmprunt) {
		this.delaiEmprunt = delaiEmprunt;
	}
}
