package com.jeeps.ckan_extractor.service;

import com.jeeps.ckan_extractor.model.ExtractionHistory;

import java.util.List;
import java.util.Optional;

public interface ExtractionHistoryService {
    Iterable<ExtractionHistory> findAll();
    Optional<ExtractionHistory> findById(Long id);
    List<ExtractionHistory> findAllByUrl(String url);
    void save(ExtractionHistory extractionHistory);
}
