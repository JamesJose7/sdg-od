package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.dao.CkanPackageDao;
import com.jeeps.ckan_extractor.model.CkanPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CkanPackageServiceImpl implements CkanPackageService {
    @Autowired
    private CkanPackageDao ckanPackageDao;

    @Override
    public Page<CkanPackage> findAll(Pageable pageable) {
        return ckanPackageDao.findAll(pageable);
    }

    @Override
    public CkanPackage findOne(Long id) {
        return ckanPackageDao.findById(id).orElse(null);
    }

    @Override
    public void save(CkanPackage ckanPackage) {
        ckanPackageDao.save(ckanPackage);
    }

    @Override
    public List<String> getOriginUrls() {
        return ckanPackageDao.findDistinctOriginUrl();
    }

    @Override
    public Integer countDistinctByOriginUrl(String url) {
        return ckanPackageDao.countDistinctByOriginUrl(url);
    }

    @Override
    public Collection<CkanPackage> findAllByOriginUrl(String url) {
        return ckanPackageDao.findAllByOriginUrl(url);
    }

    @Override
    public Boolean existsByOriginUrl(String url) {
        return ckanPackageDao.existsDistinctByOriginUrl(url);
    }

    @Override
    public void deleteAllByOriginUrl(String url) {
        ckanPackageDao.deleteAllByOriginUrl(url);
    }

    public Page<CkanPackage> findPackagesWithPaging(int pageNumber, int totalPages) {
        Pageable pageable = PageRequest.of(pageNumber, totalPages);
        return ckanPackageDao.findAll(pageable);
    }
}
