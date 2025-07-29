package com.k48.lib48.repository;

import com.k48.lib48.models.CarteAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CarteAbonnementRepository extends JpaRepository<CarteAbonnement, Integer> {

}
