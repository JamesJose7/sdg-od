package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.CkanResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CkanResourceDao extends CrudRepository<CkanResource, Long> {
}
