package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.dao.CkanResourceDao;
import com.jeeps.ckan_extractor.model.CkanResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CkanResourceServiceImpl implements CkanResourceService {
    @Autowired
    private CkanResourceDao ckanResourceDao;

    @Override
    public Iterable<CkanResource> findAll() {
        return ckanResourceDao.findAll();
    }

    @Override
    public CkanResource findOne(Long id) {
        return ckanResourceDao.findById(id).orElse(null);
    }

    @Override
    public void save(CkanResource ckanResource) {
        ckanResourceDao.save(ckanResource);
    }
}
