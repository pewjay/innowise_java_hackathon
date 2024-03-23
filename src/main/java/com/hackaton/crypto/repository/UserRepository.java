package com.hackaton.crypto.repository;

import com.hackaton.crypto.model.TelegramUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<TelegramUser, String> {
}
