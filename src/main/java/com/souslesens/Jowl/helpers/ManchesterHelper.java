package com.souslesens.Jowl.helpers;

import java.util.Set;

import com.github.owlcs.ontapi.OntManagers;
import com.github.owlcs.ontapi.Ontology;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class ManchesterHelper {

    public void getTripleFromOwlAxiom(OWLAxiom axiom) throws OWLOntologyCreationException {

        Ontology owl2 = OntManagers.createManager().createOntology();

        owl2.addAxiom(axiom);

        Model jena = owl2.asGraphModel();

        Set<Statement> statements = jena.listStatements().toSet();

        System.out.println("Statements: ");
        for (Statement statement : statements) {
            System.out.println(statement);
        }
    }

}
