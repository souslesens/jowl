package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.reasonerConsistency;
import com.souslesens.Jowl.model.reasonerExtractTriples;
import com.souslesens.Jowl.model.reasonerUnsatisfaisability;

public interface ReasonerService {
    String getUnsatisfaisableClasses(String filePath, String operation) throws Exception;
}
