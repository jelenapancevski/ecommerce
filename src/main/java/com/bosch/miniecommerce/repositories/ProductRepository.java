package com.bosch.miniecommerce.repositories;

import com.bosch.miniecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor {
}
