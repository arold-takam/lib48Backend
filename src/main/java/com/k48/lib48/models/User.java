package com.k48.lib48.models;

import com.k48.lib48.myEnum.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "mail", unique = true)
	private String mail;
	
	@Column(name = "password")
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "user_role")
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
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public CarteAbonnement getCarteAbonnement() {
		return carteAbonnement;
	}
	
	public void setCarteAbonnement(CarteAbonnement carteAbonnement) {
		this.carteAbonnement = carteAbonnement;
	}
	
//	----------------FOR SECURITY------------------------------------------------------------------------------------------------------------
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(roleName.name()));
	}
	
	public String getUsername() {
		return mail;
	}
	
	public String getPassword() {
		return password;
	}
	
	
// -----------Les méthodes suivantes sont requises par UserDetails------------------------------------------
	public boolean isAccountNonExpired() {
		return true;
	}
	
	public boolean isAccountNonLocked() {
		return true;
	}
	
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	public boolean isEnabled() {
		return true;
	}
}
