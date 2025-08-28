package com.bosch.miniecommerce.controllers;

import com.bosch.miniecommerce.entities.Product;
import com.bosch.miniecommerce.exceptions.ResourceNotFoundException;
import com.bosch.miniecommerce.repositories.ProductRepository;
import com.bosch.miniecommerce.specifications.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
enum OrderBy {
    asc, desc;
}
enum SortBy {
    id("id"),
    name("name"),
    type("type"),
    price("price"),
    description("description");
    private final String field;

    SortBy(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}

@RestController
public class ProductController {

    private static final int  DEFAULT_PAGE = 0;
    private static final int  DEFAULT_SIZE = 5;
    private final ProductRepository productRepository;


    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //List all products (pagination, sorting, filtering)
    @GetMapping("/api/products")
    public Page<Product> products(
            @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = "id") SortBy sortBy,
            @RequestParam(defaultValue = "asc") OrderBy order,
            @RequestParam(required = false) String filterByName,
            @RequestParam(required = false) String filterByType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        if (page <0) page = 0;
        if (size <=0) size = 5;
        Sort sort = order == OrderBy.asc? Sort.by(sortBy.getField()).ascending() : Sort.by(sortBy.getField()).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Product> specification = Specification.allOf(
                ProductSpecification.containsName(filterByName),
                ProductSpecification.isType(filterByType),
                ProductSpecification.priceMin(minPrice),
                ProductSpecification.priceMax(maxPrice)
        );
        return productRepository.findAll(specification, pageable);
    }

    //Get single product
    @GetMapping("/api/products/{id}")
    public ResponseEntity<Product> product(@PathVariable Long id){
        Product product = productRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Non-existing product with the given id"));
        return ResponseEntity.ok(product);

    }


}
