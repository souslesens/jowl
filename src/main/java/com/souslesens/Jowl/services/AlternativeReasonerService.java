package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.reasonerExtractTriples;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AlternativeReasonerService {

  // Alternative
  String getUnsatisfaisableClassesAlt(String filePath, String Url, MultipartFile ontologyFile)
      throws Exception;

  String getConsistencyAlt(String filePath, String Url, MultipartFile ontologyFile)
      throws Exception;

  List<reasonerExtractTriples> getInferencesAlt(
      String filePath, String Url, MultipartFile ontologyFile) throws Exception;
}
