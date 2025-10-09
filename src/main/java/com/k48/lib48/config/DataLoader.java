package com.k48.lib48.config;

import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	private static final String MAIL_ADMIN = "toto@gmail.com";
	private static final String NAME_ADMIN = "toto";
	private static final String PASSWORD_ADMIN = "toto237";
	
	public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	
//	---------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void run(String... args) throws Exception{
		if (userRepository.findByMailIgnoreCase(MAIL_ADMIN) == null){
			User gerant = new User();
			gerant.setName(NAME_ADMIN);
			gerant.setMail(MAIL_ADMIN);
			gerant.setPassword(passwordEncoder.encode(PASSWORD_ADMIN));
			gerant.setRoleName(Role.GERANT);
			
			userRepository.save(gerant);
			System.out.println("✅ Admin loaded successfully ! ✅");
		}
	}
}
