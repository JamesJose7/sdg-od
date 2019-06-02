package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.ExtractionHistory;

import java.util.List;

public interface ExtractionHistoryService {
    Iterable<ExtractionHistory> findAll();
    List<ExtractionHistory> findAllByUrl(String url);
    void save(ExtractionHistory extractionHistory);
}
