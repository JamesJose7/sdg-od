package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.User;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Entity;

@Entity
public interface UserDao extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
