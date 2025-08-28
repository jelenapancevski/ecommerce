package com.bosch.miniecommerce.specifications;

import com.bosch.miniecommerce.entities.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    public static Specification<Product> containsName(String name){
        return ((root, query, criteriaBuilder) -> name == null? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+name.toLowerCase()+"%"));

    }

    public static Specification<Product> isType(String type){
        return ((root, query, criteriaBuilder) -> type == null? null : criteriaBuilder.equal(root.get("type"), type));

    }

    public static Specification<Product> priceMin(Double minPrice){
        return ((root, query, criteriaBuilder) -> minPrice == null? null : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));

    }

    public static Specification<Product> priceMax(Double maxPrice){
        return ((root, query, criteriaBuilder) -> maxPrice == null? null : criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));

    }
}
