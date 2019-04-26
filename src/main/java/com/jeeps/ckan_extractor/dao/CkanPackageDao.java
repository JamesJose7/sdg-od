package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.CkanPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CkanPackageDao extends CrudRepository<CkanPackage, Long> {
}
