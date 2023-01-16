package com.estudo.app.product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.estudo.app.product.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}
