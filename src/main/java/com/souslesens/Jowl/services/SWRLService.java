package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.SWRLTypeEntityVariable;
import java.util.List;

public interface SWRLService {

  String SWRLruleReclassification(String filePath, String Url, String[] reqBodies, String[] reqHead)
      throws Exception;

  String SWRLruleReclassificationB64(
      String ontologyContentDecoded64, String[] reqBodies, String[] reqHead) throws Exception;

  String SWRLruleVAB64(
      String ontologyContentDecoded64,
      List<SWRLTypeEntityVariable> reqBodies,
      List<SWRLTypeEntityVariable> reqHead)
      throws Exception;

  String SWRLruleVABUF(
      String filePath,
      String url,
      List<SWRLTypeEntityVariable> reqBodies,
      List<SWRLTypeEntityVariable> reqHead)
      throws Exception;
}
