package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.CkanPackage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CkanPackageDao extends CrudRepository<CkanPackage, Long> {
    @Query("SELECT DISTINCT originUrl from CkanPackage")
    List<String> findDistinctOriginUrl();

    Integer countDistinctByOriginUrl(String url);
    Collection<CkanPackage> findAllByOriginUrl(String url);
    Boolean existsDistinctByOriginUrl(String url);
    void deleteAllByOriginUrl(String url);
}
