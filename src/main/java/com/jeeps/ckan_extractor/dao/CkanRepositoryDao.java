package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.CkanRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CkanRepositoryDao extends CrudRepository<CkanRepository, Long> {
    CkanRepository findByUrl(String url);
}
