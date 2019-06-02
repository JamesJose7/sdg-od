package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.dao.ExtractionHistoryDao;
import com.jeeps.ckan_extractor.model.ExtractionHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtractionHistoryServiceImpl implements ExtractionHistoryService {
    @Autowired
    private ExtractionHistoryDao historyDao;

    @Override
    public Iterable<ExtractionHistory> findAll() {
        return historyDao.findAll();
    }

    @Override
    public List<ExtractionHistory> findAllByUrl(String url) {
        return historyDao.findAllByUrlContains(url);
    }

    @Override
    public void save(ExtractionHistory extractionHistory) {
        historyDao.save(extractionHistory);
    }
}
