package com.k48.lib48.models;

import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.TypeOpperation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "History")
public class History {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "book_title")
	private String bookTitle;
	
	@Column(name = "operation_name")
	private TypeOpperation typeOpperation;
	
	@Column(name = "etat_operation")
	private EtatOpperation etatOperation;
	
	@Column(name = "date_time")
	private LocalDateTime dateTime;
	
	@Column(name = "details")
	private String details;
	
	@Column(name = "user_name")
	private String userName;
	
	public History() {
	}
	
	public History(String bookTitle, TypeOpperation typeOpperation, EtatOpperation etatOperation, LocalDateTime dateTime, String details, String userName) {
		this.bookTitle = bookTitle;
		this.typeOpperation = typeOpperation;
		this.etatOperation = etatOperation;
		this.dateTime = dateTime;
		this.details = details;
		this.userName =userName;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getBookTitle() {
		return bookTitle;
	}
	
	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}
	
	public TypeOpperation getTypeOpperation() {
		return typeOpperation;
	}
	
	public void setTypeOpperation(TypeOpperation typeOpperation) {
		this.typeOpperation = typeOpperation;
	}
	
	public EtatOpperation getEtatOperation() {
		return etatOperation;
	}
	
	public void setEtatOperation(EtatOpperation etatOperation) {
		this.etatOperation = etatOperation;
	}
	
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	
	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
