package com.hackaton.crypto.repository;

import com.hackaton.crypto.model.Cryptocurrency;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CryptoRepository extends MongoRepository<Cryptocurrency, String> {
}
