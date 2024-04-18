package com.example.ecommerceapplication.repositories;

import com.example.ecommerceapplication.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(collectionResourceRel = "countries", path = "countries")
@Repository
public interface CountryRepository extends JpaRepository<Country,Integer> {

}
