package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.CkanPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface CkanPackageService {
    Page<CkanPackage> findAll(Pageable pageable);
    CkanPackage findOne(Long id);
    void save(CkanPackage ckanPackage);
    List<String> getOriginUrls();
    Integer countDistinctByOriginUrl(String url);
    Integer countAllByOriginUrl(String url);
    Collection<CkanPackage> findAllByOriginUrl(String url);
    Boolean existsByOriginUrl(String url);
    void deleteAllByOriginUrl(String url);
    Page<CkanPackage> findPackagesWithPaging(int pageNumber, int totalPages);
    List<String> search(String term);
    List<String> findIdsByOriginUrl(String url);
    CkanPackage findByTitle(String title);

    Page<CkanPackage> findAllByTitleContaining(String q, Pageable pageable);
    Page<CkanPackage> findAllByTitleContainsAndOriginUrlContains(String title, String originUrl, Pageable pageable);
    Page<CkanPackage> findAllByTitleContainsAndOriginUrlContainsAndPackageTagsEquals(String title, String originUrl, String tag, Pageable pageable);
    Page<CkanPackage> findAllByTitleContainsAndOriginUrlContainsAndPackageGroupsEquals(String title, String originUrl, String group, Pageable pageable);
    Page<CkanPackage> findAllByPackageTagsEquals(String tag, Pageable pageable);
    Page<CkanPackage> findAllByPackageGroupsEquals(String group, Pageable pageable);
    CkanPackage findByName(String packageName);
}
