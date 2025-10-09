package com.k48.lib48.service;

import com.k48.lib48.models.User;
import com.k48.lib48.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
//	-----------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
		User user = userRepository.findByMailIgnoreCase(mail);
		
		if (user == null){
			throw new UsernameNotFoundException("User not found with email: "+mail);
		}
		
		return user;
	
	}
}
