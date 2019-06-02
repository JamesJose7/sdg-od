package com.jeeps.ckan_extractor.dao;

import com.jeeps.ckan_extractor.model.ExtractionHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtractionHistoryDao extends CrudRepository<ExtractionHistory, Long> {
    List<ExtractionHistory> findAllByUrlContains(String url);
}
