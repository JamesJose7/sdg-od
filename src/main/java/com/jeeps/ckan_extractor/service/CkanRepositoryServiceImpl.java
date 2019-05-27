package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.dao.CkanRepositoryDao;
import com.jeeps.ckan_extractor.model.CkanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CkanRepositoryServiceImpl implements CkanRepositoryService {
    @Autowired
    private CkanRepositoryDao ckanRepositoryDao;

    @Override
    public void delete(CkanRepository ckanRepository) {
        ckanRepositoryDao.delete(ckanRepository);
    }

    @Override
    public void save(CkanRepository ckanRepository) {
        ckanRepositoryDao.save(ckanRepository);
    }

    @Override
    public Iterable<CkanRepository> findAll() {
        return ckanRepositoryDao.findAll();
    }

    @Override
    public CkanRepository findByUrl(String url) {
        return ckanRepositoryDao.findByUrl(url);
    }
}
