package com.souslesens.Jowl.services;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;
import com.souslesens.Jowl.config.AppConfig;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class VirtuosoServiceImpl implements VirtuosoService {

    @Autowired
    private AppConfig appConfig;

    @Override
    public OWLOntology readOntologyFromVirtuoso(String graphName) throws OWLOntologyCreationException, OWLOntologyStorageException, NoVirtuosoTriplesException {

        String query = "SELECT ?subject ?predicate ?object WHERE { GRAPH <" + graphName + "> { ?subject ?predicate ?object . FILTER(isIRI(?object) || isBlank(?object)) } }"; // Update with your ontology graph URI

        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService(appConfig.getVirtuosoEndpoint(), query)) {
            ResultSet results = queryExecution.execSelect();

            if (!results.hasNext()) {
                throw new NoVirtuosoTriplesException("No triples found in the graph " + graphName);
            }

            OntologyManager m = OntManagers.createManager();
            Ontology o = m.createOntology(IRI.create(graphName));

            while (results.hasNext()) {
                QuerySolution solution = results.nextSolution();
                Resource subject = solution.getResource("subject");
                Property predicate = ResourceFactory.createProperty(solution.get("predicate").toString());
                RDFNode object = solution.get("object");
                if ( predicate.getURI().toString().equals("http://www.w3.org/2002/07/owl#imports") ) {
                    if (object.isResource()) {
                        Ontology importedOntology = m.loadOntology(IRI.create(((Resource) object).getURI()));
                        o.asGraphModel().add(importedOntology.asGraphModel());
                    }
                    }
                o.asGraphModel().add(subject, predicate, object);
            }

            if (o.axioms().findAny().isPresent()) {
                return o;
            }

        }

        System.out.println("Error reading ontology from Virtuoso");
        return null;
    }



}

