package com.k48.lib48.models;

import com.k48.lib48.myEnum.TypeAbonnement;
import jakarta.persistence.*;

@Entity
@Table(name = "carte_abonnement")
public class CarteAbonnement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "card_number", unique = true)
	private Long cardNumber;
	
	@Enumerated(EnumType.STRING)
	private TypeAbonnement typeAbonnement;
	
	@Column(name = "availability", nullable = false)
	private boolean isAvailable;
	
	@Column(name = "duree")
	private int duree;
	
	public CarteAbonnement() {
	}
	
	public CarteAbonnement(TypeAbonnement typeAbonnement, boolean isAvailable, int duree) {
		this.typeAbonnement = typeAbonnement;
		this.isAvailable = isAvailable;
		this.duree = duree;
	}
	
	public int getId() {
		return id;
	}
	
	public Long getCardNumber() {
		return cardNumber;
	}
	
	public void setCardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public TypeAbonnement getTypeAbonnement() {
		return typeAbonnement;
	}
	
	public void setTypeAbonnement(TypeAbonnement typeAbonnement) {
		this.typeAbonnement = typeAbonnement;
	}
	
	public boolean isAvailable() {
		return isAvailable;
	}
	
	public void setAvailable(boolean available) {
		isAvailable = available;
	}
	
	public int getDuree() {
		return duree;
	}
	
	public void setDuree(int duree) {
		this.duree = duree;
	}
	
}
