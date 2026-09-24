package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.jenaTripleParser;
import java.util.List;

public interface JenaService {

  List<jenaTripleParser> getTriples(String filePath, String Url, String ontologyContentEncoded64);
}
