package com.k48.lib48.repository;

import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepositories extends JpaRepository<User, Integer> {
	
	User findByNameIgnoreCaseAndRoleName(String name, Role role);
	
	List<User>findAllByRoleName(Role role);

}
