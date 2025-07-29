package com.k48.lib48.models;

import com.k48.lib48.myEnum.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "mail", unique = true)
	private String mail;
	
	@Column(name = "password")
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role roleName;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "carte_abonnement", referencedColumnName = "id")
	private CarteAbonnement carteAbonnement;
	
	public User() {
	}
	
	public User(String name, String mail, String password, Role roleName, CarteAbonnement carteAbonnement) {
		this.name = name;
		this.mail = mail;
		this.password = password;
		this.roleName = roleName;
		this.carteAbonnement = carteAbonnement;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public Role getRoleName() {
		return roleName;
	}
	
	public void setRoleName(Role roleName) {
		this.roleName = roleName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public CarteAbonnement getCarteAbonnement() {
		return carteAbonnement;
	}
	
	public void setCarteAbonnement(CarteAbonnement carteAbonnement) {
		this.carteAbonnement = carteAbonnement;
	}
}
