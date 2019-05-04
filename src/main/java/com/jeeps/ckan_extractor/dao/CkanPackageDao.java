package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.CkanPackage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CkanPackageDao extends PagingAndSortingRepository<CkanPackage, Long> {
    @Query("SELECT DISTINCT originUrl from CkanPackage")
    List<String> findDistinctOriginUrl();

    Integer countDistinctByOriginUrl(String url);
    Collection<CkanPackage> findAllByOriginUrl(String url);
    Boolean existsDistinctByOriginUrl(String url);
    void deleteAllByOriginUrl(String url);
    @Query("SELECT title FROM CkanPackage where title like %:keyword%")
    List<String> search(@Param("keyword") String keyword);
}
