package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.jenaTripleParser;
import org.apache.jena.base.Sys;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class ManchesterServiceImpl implements ManchesterService {
    @Override
    public OWLAxiom parseStringToAxiom(String owlOntologyFilePath, String url, String ontologyContentBased64, String input) throws OWLOntologyCreationException {
        OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
        OWLOntology owlOntology = owlManager.loadOntology(IRI.create(url));

        OWLDataFactory dataFactory = owlManager.getOWLDataFactory();
        ShortFormProvider sfp =
                new AnnotationValueShortFormProvider(Arrays.asList(dataFactory.getRDFSLabel()),
                        Collections.<OWLAnnotationProperty, List<String>>emptyMap(), owlManager);
        BidirectionalShortFormProvider shortFormProvider =
                new BidirectionalShortFormProviderAdapter(owlManager.getOntologies(), sfp);

        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setDefaultOntology(owlOntology);

        parser.setStringToParse(input);

        ShortFormEntityChecker checker = new ShortFormEntityChecker(shortFormProvider);

        parser.setOWLEntityChecker(checker);

        OWLAxiom axiom;

        try {
            axiom = parser.parseAxiom();
            return axiom;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public ArrayList<jenaTripleParser> getTriples(OWLAxiom axiom) {
        return null;
    }

    private BidirectionalShortFormProvider getShortFormProvider(OWLOntologyManager owlManager, OWLOntology owlOntology) {

        Set<OWLOntology> ontologies = owlManager.getOntologies();
        ShortFormProvider sfp = new ManchesterOWLSyntaxPrefixNameShortFormProvider(
                owlManager.getOntologyFormat(owlOntology));
        BidirectionalShortFormProvider shortFormProvider = new BidirectionalShortFormProviderAdapter(
                ontologies, sfp);
        return shortFormProvider;
    }
}
