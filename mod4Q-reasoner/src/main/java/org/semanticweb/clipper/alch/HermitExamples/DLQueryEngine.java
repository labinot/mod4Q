
package org.semanticweb.clipper.alch.HermitExamples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/**
 * Created by bato on 12/19/2016.
 */

class DLQueryEngine {
    private final DLQueryParser parser;
    private final Reasoner hermiT;

    public DLQueryEngine(OWLOntology ontology,Reasoner hermit,ShortFormProvider shortFormProvider) {
        parser = new DLQueryParser(ontology, shortFormProvider);
        this.hermiT = hermit;
    }
        //how to reason with HermiT
        //hermiT.precomputeInferences();
        //hermiT.getObjectPropertyInstances();


    public Set<OWLNamedIndividual> getClassInstances(String classExpressionString,
                                                boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);

        NodeSet<OWLNamedIndividual> individuals =hermiT.getInstances(classExpression,
                direct);


        return individuals.getFlattened();


    }

    //not tested properly
    public Map<OWLNamedIndividual,Set<OWLNamedIndividual>> getObjectPropertyInstances(OWLObjectProperty property) {
        return null;
    }
}
