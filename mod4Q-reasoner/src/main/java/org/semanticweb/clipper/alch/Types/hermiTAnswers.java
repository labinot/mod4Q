package org.semanticweb.clipper.alch.Types;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;

import org.semanticweb.owlapi.model.*;

import org.semanticweb.owlapi.apibinding.OWLManager;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer2;

import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;

import org.semanticweb.owlapi.reasoner.NodeSet;

import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import java.sql.*;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import java.io.File;

/**
 * Created by bato on 12/16/2016.
 */

public class hermiTAnswers {

    private OWLOntology ontology;
    private String strOntologyFilename;
    private OWLOntologyManager manager;
    private ALCH_Normalizer2 normalizer;
    private Reasoner hermiT;
    private ShortFormProvider sfp;
    private Connection conn;
    private Statement stmt;


    private BidirectionalShortFormProvider bidiShortFormProvider;

    hermiTAnswers() {
        conn = createConnection();
        try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        this.normalizer = new ALCH_Normalizer2();
    }

    public void loadOntology(File file) throws OWLOntologyCreationException {
        this.manager = OWLManager.createOWLOntologyManager();
        this.ontology = manager.loadOntologyFromOntologyDocument(file);
        this.strOntologyFilename = file.getName();
        this.ontology = normalizer.normalize(ontology);
        this.hermiT = new Reasoner(ontology);
        this.sfp = new SimpleShortFormProvider();
        Set<OWLOntology> importsClosure = ontology.getImportsClosure();
        this.bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure, sfp);
    }


    public void printForEachClass() {

        for (OWLClassExpression cls : ontology.getClassesInSignature()) {
            System.out.println("Instances for ClassExpression: " + cls.toString());

            String classExpressionString = sfp.getShortForm(cls.asOWLClass()).trim();
            OWLClassExpression classExpression = parseClassExpression(classExpressionString);

            Set<OWLNamedIndividual> indviduals = getInstances(classExpression, false);

            for (OWLNamedIndividual ind : indviduals) {
                System.out.println(ind.toString());
            }
        }
    }

    public void insertAnswersFromHermiT() throws SQLException {
        String sql=String.format("DELETE FROM hermiTanswers " + "" +
                        "WHERE ontology_filename='%1$s'",
                strOntologyFilename);

        stmt.execute(sql);

        for (OWLClassExpression cls : ontology.getClassesInSignature()) {
            String classExpressionString = sfp.getShortForm(cls.asOWLClass()).trim();
            System.out.println("Inserting answers for : " + classExpressionString);

            if(cls.isOWLThing())
                continue;

            OWLClassExpression classExpression = parseClassExpression(classExpressionString);

            Set<OWLNamedIndividual> indviduals = getInstances(classExpression, false);

            insertAnswersPerClassInDB(cls, indviduals);
        }
    }

    public void insertAnswersPerClassInDB(OWLClassExpression cls, Set<OWLNamedIndividual> individuals) throws SQLException{
        String sql;
        String clsName = sfp.getShortForm(cls.asOWLClass()).toLowerCase();
        String indName="";

        if(individuals.size()==0){
            sql=String.format("INSERT INTO hermiTanswers (ontology_filename,concept_name, individual_name) "
                            + "SELECT '%1$s','%2$s',NULL",
                    strOntologyFilename,clsName);

                stmt.execute(sql);
        }else{
            for (OWLNamedIndividual ind : individuals) {
                indName = sfp.getShortForm(ind).toLowerCase();

                sql=String.format("INSERT INTO hermiTanswers (ontology_filename,concept_name, individual_name) "
                                + "SELECT '%1$s','%2$s','%3$s'",
                        strOntologyFilename, clsName, indName);

                stmt.execute(sql);
            }
        }

    }

    public OWLClassExpression parseClassExpression(String classExpressionString) {
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager()
                .getOWLDataFactory();
        ManchesterOWLSyntaxEditorParser parser;
        parser = new ManchesterOWLSyntaxEditorParser(dataFactory, classExpressionString);
        parser.setDefaultOntology(ontology);
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        return parser.parseClassExpression();
    }

    public Set<OWLNamedIndividual> getInstances(OWLClassExpression classExpression,
                                                boolean direct) {
        if (classExpression.toString().trim().length() == 0) {
            return Collections.emptySet();
        }

        NodeSet<OWLNamedIndividual> individuals = hermiT.getInstances(classExpression,
                direct);

        return individuals.getFlattened();
    }


    protected Connection createConnection() {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "12345678");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/knots", props);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return conn;
    }

}