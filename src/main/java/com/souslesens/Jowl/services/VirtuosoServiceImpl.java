package com.souslesens.Jowl.services;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import com.github.owlcs.ontapi.OntologyManager;
import com.souslesens.Jowl.model.exceptions.NoVirtuosoTriplesException;
import org.apache.jena.base.Sys;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.update.UpdateAction;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.manchester.cs.owl.owlapi.OWLAxiomImpl;

import javax.swing.plaf.nimbus.State;
import java.util.HashSet;
import java.util.Set;

@Service
public class VirtuosoServiceImpl implements VirtuosoService {

    @Value("${VIRTUOSO_ENDPOINT_URL}")
    private String virtuosoEndpointUrl;

    @Override
    public OWLOntology readOntologyFromVirtuoso(String graphName) throws OWLOntologyCreationException, OWLOntologyStorageException, NoVirtuosoTriplesException {

        String query = "SELECT ?subject ?predicate ?object WHERE { GRAPH <" + graphName + "> { ?subject ?predicate ?object . FILTER(isIRI(?object) || isBlank(?object)) } }"; // Update with your ontology graph URI

        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService(virtuosoEndpointUrl, query)) {
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

        return null;
    }



}

