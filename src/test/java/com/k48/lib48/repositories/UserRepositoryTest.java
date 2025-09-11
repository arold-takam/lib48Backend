package com.k48.lib48.repositories;


import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.UserRepositories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
public class UserRepositoryTest {

    @Autowired
    private UserRepositories userRepositories;

    @Test
    void findUserByName() {

        //Given
        User user = new User();
        user.setName("paul");
        user.setMail("paul@gmail.com");
        user.setPassword("password");
        user.setRoleName(Role.GERANT);
        userRepositories.save(user);

        //When
        User user1 = this.userRepositories.findByNameIgnoreCase(user.getName());

        //Then
        //verifier que user1 n'est pas null
        assert user1 != null;

        // On certifie que le nom du user recherché est le nom que celui qu'on a inséré
        assertThat(user1.getName()).isEqualTo(user.getName());

        // On certifie que le mail du user recherché est le nom que celui qu'on a inséré
        assertThat(user1.getMail()).isEqualTo(user.getMail());

        // On certifie que le mot de passe du user recherché est le nom que celui qu'on a inséré
        assertThat(user1.getPassword()).isEqualTo(user.getPassword());

        // On certifie que le role du user recherché est le nom que celui qu'on a inséré
        assertThat(user1.getRoleName()).isEqualTo(Role.GERANT);
    }

    @Test
    void itShouldNotSaveUserWhenNameIsNullAndMailIsNotUnique() {
        //Given
        User user = new User();
        user.setMail("paul@gmail.com");
        user.setPassword("password");
        user.setRoleName(Role.ABONNE);
        // When
        // Then
        // En essayant d'insérer ce user, on certifie que l'exception DataIntegrityViolation
        //est bel et bien levée
        assertThatThrownBy(()->userRepositories.save(user)).isInstanceOf(DataIntegrityViolationException.class);

    }
}
