package com.k48.lib48.service;

import com.k48.lib48.models.User;
import com.k48.lib48.repository.UserRepositories;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepositories userRepositories;
	
	public CustomUserDetailsService(UserRepositories userRepositories) {
		this.userRepositories = userRepositories;
	}
	
	
//	-----------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
//		NOTICE: UserName there has been replaced by mail for our business logic need.
		
		User user = userRepositories.findByMailIgnoreCase(mail);
		
		if (!userRepositories.existsByMailIgnoreCase(mail)){
			throw new UsernameNotFoundException("User not found with email: "+mail);
		}
		
		return user;
	
	}
}
