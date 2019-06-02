package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.CkanPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Integer countAllByOriginUrl(String url);
    Collection<CkanPackage> findAllByOriginUrl(String url);
    Boolean existsDistinctByOriginUrl(String url);
    void deleteAllByOriginUrl(String url);
    @Query("SELECT title FROM CkanPackage where title like %:keyword%")
    List<String> search(@Param("keyword") String keyword);
    @Query("SELECT name from CkanPackage where originUrl like %:url%")
    List<String> findIdsByOriginUrl(@Param("url") String url);
    CkanPackage findByTitle(String title);
    // Filtering
    Page<CkanPackage> findAllByTitleContains(String q, Pageable pageable);
    Page<CkanPackage> findAllByTitleContainsAndOriginUrlContains(String title, String originUrl, Pageable pageable);
    Page<CkanPackage> findAllByTitleContainsAndOriginUrlContainsAndPackageTagsEquals(String title, String originUrl, String tag, Pageable pageable);
    Page<CkanPackage> findAllByTitleContainsAndOriginUrlContainsAndPackageGroupsEquals(String title, String originUrl, String group, Pageable pageable);
    Page<CkanPackage> findAllByPackageTagsEquals(String tag, Pageable pageable);
    Page<CkanPackage> findAllByPackageGroupsEquals(String group, Pageable pageable);
    CkanPackage findByName(String packageName);
}
