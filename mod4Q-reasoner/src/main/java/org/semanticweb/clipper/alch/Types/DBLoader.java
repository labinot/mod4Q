package org.semanticweb.clipper.alch.Types;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import uk.ac.ox.cs.JRDFox.JRDFoxException;
import uk.ac.ox.cs.JRDFox.Prefixes;
import uk.ac.ox.cs.JRDFox.store.DataStore;
import uk.ac.ox.cs.JRDFox.store.Resource;
import uk.ac.ox.cs.JRDFox.store.TupleIterator;

import java.io.File;
import java.sql.*;
import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Created by bato on 1/14/18.
 */
public class DBLoader {
    Connection conn;
    Statement stmt;
    ShortFormProvider sfp;
    public DBLoader(Connection prmConn) {
        conn=prmConn;
        stmt= null;
        sfp= new SimpleShortFormProvider();
        try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /*Inserts ontology metadata, for now only name of the current loadin ontology is inserted*/
    private void insertOntologyMetadata(OWLOntology ontology, String ontologyFileName) throws SQLException {

        String sql =String
                .format("UPDATE ontology_metadata "
                                + "SET value='%1$s' "
                                + "WHERE parameter='Name'",
                        ontology.getOntologyID().toString());

        stmt.execute(sql);

        sql =String
                .format("UPDATE ontology_metadata "
                                + "SET value='%1$s' "
                                + "WHERE parameter='Filename'",
                        ontologyFileName);

        stmt.execute(sql);
    }


    private void cleanABoxStagingTables() throws SQLException {
        String sql = "SELECT clean_abox_staging_tables()";
        stmt.execute(sql);
    }


    private void cleanTBoxStagingTables() throws SQLException {
        String sql = "SELECT clean_tbox_staging_tables()";
        stmt.execute(sql);
    }


    private void cleanComputationTables(boolean incremental) throws SQLException {
        String sql;
        if(incremental)
            sql = "SELECT clean_computation_tables_for_incremental_reasoning()";
        else
            sql = "SELECT clean_computation_tables()";
        stmt.execute(sql);
    }


    private void cleanStatisticsTables() throws SQLException {
        String sql = "SELECT clean_statistics_tables()";
        stmt.execute(sql);
    }


    private void insertIndividuals(OWLOntology ontology)throws SQLException {
        Set<OWLNamedIndividual> individuals = ontology
                .getIndividualsInSignature();

        Set<OWLAnonymousIndividual> anonymousIndividuals = ontology.getAnonymousIndividuals();

        PreparedStatement insert = conn.prepareStatement("insert into st_individuals values (?,?)");

        int individualId=0;
        //statements processed since the last batch execution
        int cnt=0;
        //number of batch statements to be processed
        int batchSize=10000;

        for (OWLNamedIndividual individual : individuals)
        {
            //String strIndividual = individual.toString();
            //strip the link away from the individual name
            //if(strIndividual.indexOf("#")>-1){
            //	strIndividual = strIndividual.substring(strIndividual.indexOf("#")+1,strIndividual.indexOf(">"));
            //}
            individualId++;
            cnt++;

            insert.setInt(1,individualId);
            insert.setString(2,individual.toString());
            insert.addBatch();

            //execute the statements and reset the counter
            if (cnt % batchSize == 0) { insert.executeBatch(); cnt=0;}
        }
        //execute the leftover statements
        insert.executeBatch();

        for (OWLAnonymousIndividual individual : anonymousIndividuals)
        {
            //String strIndividual = individual.toString();
            //strip the link away from the individual name
            //if(strIndividual.indexOf("#")>-1){
            //	strIndividual = strIndividual.substring(strIndividual.indexOf("#")+1,strIndividual.indexOf(">"));
            //}
            individualId++;
            cnt++;

            insert.setInt(1,individualId);
            insert.setString(2,individual.toString());
            insert.addBatch();

            //execute the statements and reset the counter
            if (cnt % batchSize == 0) { insert.executeBatch(); cnt=0;}
        }
        //execute the leftover statements
        insert.executeBatch();

    }


    private void insertConcepts(OWLOntology ontology)throws SQLException {
        Set<OWLClass> classes = ontology.getClassesInSignature(true);

        //insert bottom and top with integer values 0 respectively 1
        String sql=String.format("INSERT INTO st_concepts (id,concept) VALUES (0,'owl:Nothing')");
        stmt.execute(sql);

        sql=String.format("INSERT INTO st_concepts (id,concept) VALUES (1,'owl:Thing')");
        stmt.execute(sql);

        PreparedStatement insert = conn.prepareStatement("insert into st_concepts values (?,?)");

        int classId=1;
        //statements processed since the last batch execution
        int cnt=0;
        //number of batch statements to be processed
        int batchSize=10000;

        for (OWLClass cls : classes)
        {
            //since allready inserted manually, skip top and bottom class
            if(cls.isOWLNothing()||cls.isOWLThing())
                continue;

            //String clsName = sfp.getShortForm(cls);
            classId++;
            cnt++;

            insert.setInt(1,classId);
            insert.setString(2,cls.toString());
            insert.addBatch();

            //execute the statements and reset the counter
            if (cnt % batchSize == 0) { insert.executeBatch(); cnt=0;}
        }
        //execute the leftover statements
        insert.executeBatch();
    }


    private void insertObjectRoles(OWLOntology ontology)throws SQLException {
        Set<OWLObjectProperty> objectProperties = ontology
                .getObjectPropertiesInSignature(true);

        PreparedStatement insert = conn.prepareStatement("insert into st_properties values (?,?)");

        int propertyID=0;
        //statements processed since the last batch execution
        int cnt=0;
        //number of batch statements to be processed
        int batchSize=10000;

        for (OWLObjectProperty property : objectProperties)
        {
            //String propertyName = sfp.getShortForm(property);

            propertyID++;

            cnt++;

            insert.setInt(1,propertyID);
            insert.setString(2,property.toString());
            insert.addBatch();

            //execute the statements and reset the counter
            if (cnt % batchSize == 0) { insert.executeBatch(); cnt=0;}
        }
        //execute the leftover statements
        insert.executeBatch();
    }


    private void insertConceptAssertions(OWLOntology ontology)throws SQLException {

        //get classAssertionAxioms
        Set<OWLClassAssertionAxiom> classAssertionAxioms = ontology.getAxioms(AxiomType.CLASS_ASSERTION, false);

        PreparedStatement insert = conn.prepareStatement("insert into st_concept_assertions values (?,?)");

        //statements processed since the last batch execution
        int cnt=0;
        //number of batch statements to be processed
        int batchSize=10000;

        for (OWLClassAssertionAxiom axiom : classAssertionAxioms)
        {
            cnt++;
            OWLClassExpression classExpression = axiom.getClassExpression();
            //String className = sfp.getShortForm(classExpression.asOWLClass());
            String individual = axiom.getIndividual().toString();

            //strip the link away from the individual name
            //if(individual.indexOf("#")>-1){
            //	individual = individual.substring(individual.indexOf("#")+1,individual.indexOf(">"));
            //}

            insert.setString(1,individual);
            insert.setString(2,classExpression.asOWLClass().toString());
            insert.addBatch();

            //execute the statements and reset the counter
            if (cnt % batchSize == 0) { insert.executeBatch(); cnt=0;}
        }
        //execute the leftover statements
        insert.executeBatch();

    }


    private void insertObjectRoleAssertions(OWLOntology ontology) throws SQLException {

        Set<OWLObjectPropertyAssertionAxiom> objectRoleAssertionAxioms = ontology
                .getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, false);

        PreparedStatement insert = conn.prepareStatement("insert into st_property_assertions values (?,?,?)");

        //statements processed since the last batch execution
        int cnt=0;
        //number of batch statements to be processed
        int batchSize=10000;

        for (OWLObjectPropertyAssertionAxiom axiom : objectRoleAssertionAxioms)
        {

            OWLObjectProperty property = axiom.getProperty().asOWLObjectProperty();

            //String strProperty = sfp.getShortForm(property);

            String strIndividualInDomain=axiom.getSubject().toString();
            String strIndividualInRange=axiom.getObject().toString();

            //strip the link away
            //if(strIndividualInDomain.indexOf("#")>-1){
            //	strIndividualInDomain = strIndividualInDomain.substring(strIndividualInDomain.indexOf("#")+1,strIndividualInDomain.indexOf(">"));
            //}

            //strip the link away
            //if(strIndividualInRange.indexOf("#")>-1){
            //	strIndividualInRange = strIndividualInRange.substring(strIndividualInRange.indexOf("#")+1,strIndividualInRange.indexOf(">"));
            //}

            cnt++;
            insert.setString(1,property.toString());
            insert.setString(2,strIndividualInDomain);
            insert.setString(3,strIndividualInRange);
            insert.addBatch();

            //execute the statements and reset the counter
            if (cnt % batchSize == 0) { insert.executeBatch(); cnt=0;}
        }
        //execute the leftover statements
        insert.executeBatch();

    }


    private void insertAxioms(OWLOntology ontology) throws SQLException {

        Set<OWLSubClassOfAxiom> subclassOfAxioms = ontology
                .getAxioms(AxiomType.SUBCLASS_OF, false);

        int axiomId=0;

        String sql="";


        for (OWLSubClassOfAxiom axiom : subclassOfAxioms) {
            axiomId++;
            OWLClassExpression sub = (OWLClassExpression)axiom.getSubClass();
            OWLClassExpression sup = (OWLClassExpression)axiom.getSuperClass();

            //if A->B
            if(sub.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                    && sup.getClassExpressionType()==ClassExpressionType.OWL_CLASS ){
                String strSub=sub.asOWLClass().toString();//sfp.getShortForm(sub.asOWLClass());
                String strSup=sup.asOWLClass().toString();//sfp.getShortForm(sup.asOWLClass());
                sql =String.format("INSERT INTO st_axiom_with_simple_concepts "
                                + "SELECT '%1$d','%2$s','%3$s' ",
                        axiomId,strSub, strSup);

                stmt.execute(sql);
            }else if(sub.getClassExpressionType()==ClassExpressionType.OBJECT_INTERSECTION_OF){
                OWLObjectIntersectionOf and = (OWLObjectIntersectionOf)sub;
                String concept_in_rhs = sup.asOWLClass().toString();//sfp.getShortForm(sup.asOWLClass());

                for(OWLClassExpression op: and.getOperands()){
                    String conjunct = op.asOWLClass().toString();//sfp.getShortForm(op.asOWLClass());
                    sql =String.format("INSERT INTO st_axiom_with_conjunctions_in_lhs "
                                    + "SELECT '%1$d','%2$s','%3$s' ",
                            axiomId, conjunct, concept_in_rhs );

                    stmt.execute(sql);
                }

            }else if(sup.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){
                OWLObjectUnionOf or = (OWLObjectUnionOf)sup;
                String concept_in_lhs = sub.asOWLClass().toString();//sfp.getShortForm(sub.asOWLClass());

                for(OWLClassExpression op: or.getOperands()){
                    String disjunct = op.asOWLClass().toString();//sfp.getShortForm(op.asOWLClass());
                    sql =String.format("INSERT INTO st_axiom_with_disjunctions_in_rhs "
                                    + "SELECT '%1$d','%2$s','%3$s' ",
                            //+ "WHERE NOT EXISTS (SELECT * FROM st_axiom_with_disjunctions_in_rhs WHERE id='%1$d' and concept_in_lhs='%2$s' and disjunct='%3$s')",
                            axiomId, concept_in_lhs, disjunct);

                    stmt.execute(sql);
                }
            }else if(sub.getClassExpressionType()==ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom)sub;
                String propertyName = some.getProperty().asOWLObjectProperty().toString();//sfp.getShortForm(some.getProperty().asOWLObjectProperty());
                String range= some.getFiller().asOWLClass().toString();//sfp.getShortForm(some.getFiller().asOWLClass());
                String conept_in_rhs = sup.asOWLClass().toString();//sfp.getShortForm(sup.asOWLClass());

                sql =String.format("INSERT INTO st_axiom_with_exist_in_lhs "
                                + "SELECT '%1$d','%2$s','%3$s','%4$s' ",
                        axiomId,propertyName, range, conept_in_rhs);
                stmt.execute(sql);

            }else if(sup.getClassExpressionType()==ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom)sup;
                String propertyName = some.getProperty().asOWLObjectProperty().toString();//sfp.getShortForm(some.getProperty().asOWLObjectProperty());
                String range= some.getFiller().asOWLClass().toString();//sfp.getShortForm(some.getFiller().asOWLClass());
                String conept_in_lhs = sub.asOWLClass().toString();//sfp.getShortForm(sub.asOWLClass());

                sql =String.format("INSERT INTO st_axiom_with_exist_in_rhs "
                                + "SELECT '%1$d','%2$s','%3$s','%4$s' ",
                        //+ "WHERE NOT EXISTS (SELECT * FROM st_axiom_with_exist_in_rhs WHERE role='%2$s' and concept_in_range='%3$s' and concept_in_lhs='%4$s')",
                        axiomId,propertyName, range, conept_in_lhs);

                stmt.execute(sql);

            }else if(sub.getClassExpressionType()==ClassExpressionType.OBJECT_ALL_VALUES_FROM){
                OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom)sub;
/*TODO: implement axioms with (All)R.B->D to behave as Top->A or D, A-> (E)R.A',A' and B-> Bottom
				String propertyName = sfp.getShortForm(all.getProperty().asOWLObjectProperty()).toLowerCase();
				String range= sfp.getShortForm(all.getFiller().asOWLClass()).toLowerCase();
				String conept_in_rhs = sfp.getShortForm(sup.asOWLClass()).toLowerCase();
*/

                System.err.print("Warning, axiom with universals in lhs encountered:"+axiom.toString());



            }else if(sup.getClassExpressionType()==ClassExpressionType.OBJECT_ALL_VALUES_FROM){
                OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom)sup;
                String propertyName = all.getProperty().asOWLObjectProperty().toString();//sfp.getShortForm(all.getProperty().asOWLObjectProperty());
                String range= all.getFiller().asOWLClass().toString();//sfp.getShortForm(all.getFiller().asOWLClass());
                String conept_in_lhs = sub.asOWLClass().toString();//sfp.getShortForm(sub.asOWLClass());

                sql = String.format("INSERT INTO st_axiom_with_universal_in_rhs "
                                + "SELECT '%1$d','%2$s','%3$s','%4$s' ",
                        axiomId, propertyName, range, conept_in_lhs);

                stmt.execute(sql);
            }
            else {
                System.err.println("Axiom not in NF: "+axiom.toString());
            }
        }

        Set<OWLSubObjectPropertyOfAxiom> subPropertyOfAxioms = ontology
                .getAxioms(AxiomType.SUB_OBJECT_PROPERTY, false);

        axiomId=0;
        for(OWLSubObjectPropertyOfAxiom axiom: subPropertyOfAxioms){
            axiomId++;
            String strSub=axiom.getSubProperty().asOWLObjectProperty().toString();//sfp.getShortForm(axiom.getSubProperty().asOWLObjectProperty());
            String strSup=axiom.getSuperProperty().asOWLObjectProperty().toString();//sfp.getShortForm(axiom.getSuperProperty().asOWLObjectProperty());
            sql =String.format("INSERT INTO st_axiom_with_simple_roles "
                            + "SELECT '%1$d','%2$s','%3$s' ",
                    axiomId,strSub, strSup );

            stmt.execute(sql);
        }
    }

    /*integrated-> indicates if the data loaded in ABox and TBox come from the same file*/
    private void loadCoreTables(boolean integrated) throws SQLException {
        long t_start=System.currentTimeMillis();
        String sql;

        System.out.println("Loading of core tables started");


        sql= "SELECT deduplicate_staging_tables()";
        stmt.execute(sql);

        sql= "SELECT load_core_tables()";
        stmt.execute(sql);

        sql = "SELECT load_list_tables()";
        stmt.execute(sql);

        System.out.println("Loading of core tables finished:"+(System.currentTimeMillis()-t_start));

    }

    private void insertQueryAtoms() throws SQLException {
        //TODO:don't forget to update class (unary) atoms used in ASP query (only those found in the signature of the ontology)
        //String sql = "INSERT INTO st_query_atoms (name,class_atom) VALUES ('Hotel',true),('Bar',true),('Club',true)";
        //String sql = "INSERT INTO st_query_atoms (name,class_atom) VALUES ('Hotel',true),('RegionalRestaurant',true)";
        //stmt.execute(sql);

        //TODO:don't forget to update atoms used in ASP query (only those found in the signature of the ontology)
        //sql = "INSERT INTO st_query_atoms (name,class_atom) VALUES ('isLocatedNext',false),('nextStation',false),('serves',false)";
        //stmt.execute(sql);
    }

    //loads and owl file that contains ABox + TBox
    public void loadIntegratedOntology(OWLOntology ontology,String ontologyFilename, boolean incremental){

        try {
            insertOntologyMetadata(ontology,ontologyFilename);
            cleanTBoxStagingTables();
            cleanABoxStagingTables();
            cleanComputationTables(incremental);
            insertIndividuals(ontology);
            insertConcepts(ontology);
            insertObjectRoles(ontology);
            insertConceptAssertions(ontology);
            insertObjectRoleAssertions(ontology);
            insertAxioms(ontology);
            insertQueryAtoms();
            loadCoreTables(true);
            gatherABoxTBoxStats();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //loads and owl file that contains ABox + TBox
    public void loadSeparatedOntology(OWLOntology tbox,OWLOntology abox,String ontologyFilename, boolean incremental){
        try {
            insertOntologyMetadata(tbox,ontologyFilename);
            cleanTBoxStagingTables();
            cleanABoxStagingTables();
            loadTBox(tbox);
            loadABox(abox,incremental);
            cleanComputationTables(incremental);
            insertQueryAtoms();
            loadCoreTables(true);
            gatherABoxTBoxStats();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //loads and owl file that contains ABox + TBox
    public void loadSeparatedOntology(OWLOntology tbox,String rdf_data,String ontologyFilename, boolean incremental){
        try {
            insertOntologyMetadata(tbox,ontologyFilename);
            cleanTBoxStagingTables();
            cleanABoxStagingTables();
            loadTBox(tbox);//we always load TBox first
            loadABox(rdf_data,incremental);
            cleanComputationTables(incremental);
            insertQueryAtoms();
            loadCoreTables(false);
            gatherABoxTBoxStats();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //loads only ABox in one of the supported formats
    private void loadABox(OWLOntology abox,boolean incremental){
        try {
            cleanABoxStagingTables();
            cleanComputationTables(incremental);
            insertIndividuals(abox);
            insertConceptAssertions(abox);
            insertObjectRoleAssertions(abox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //loads only ABox in one of the supported formats
    private void loadABox(String rdf_data,boolean incremental){
        //TODO:add a check for the format of the file rdf_data
        try {
            cleanABoxStagingTables();
            cleanComputationTables(incremental);
            insertRDFtriples(rdf_data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //loads only TBox in one of the supported formats
    private void loadTBox(OWLOntology tbox){
        try {
            cleanTBoxStagingTables();
            insertConcepts(tbox);
            insertObjectRoles(tbox);
            insertAxioms(tbox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*loads the rdf triple using RDFox as mediator*/
    private void insertRDFtriples(String aboxFile) throws SQLException {
        PreparedStatement insertCA = conn.prepareStatement("insert into st_concept_assertions values (?,?)");
        PreparedStatement insertPA = conn.prepareStatement("insert into st_property_assertions values (?,?,?)");
        PreparedStatement insertIND = conn.prepareStatement("insert into st_individuals values (?,?)");

        // We now create the data store. RDFox supports different types of stores described in DataStore.StoreType,
        // each with the option of native equality reasoning.
        DataStore store = null;
        try {
            store = new DataStore(DataStore.StoreType.ParallelSimpleNN);
            System.out.println("Setting the number of threads...");
            store.setNumberOfThreads(2);

            System.out.println("Importing RDF data...");
            store.importFiles(new File[]{new File(aboxFile)});
            System.out.println("Number of tuples after import: " + store.getTriplesCount());
            //return compute(store);

            Prefixes prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE;

            System.out.println("Retrieving all properties before materialisation.");

            //retreiving concept assertions
            TupleIterator classIterator = store.compileQuery("SELECT DISTINCT ?x ?y WHERE { ?x a ?y }", prefixes);

            //statements processed since the last batch execution
            int cnt=0;
            //number of batch statements to be processed
            int batchSize=10000;

            for (long multiplicity = classIterator.open(); multiplicity != 0; multiplicity = classIterator.advance()) {
                cnt++;
                // We iterate trough the terms of each tuple
                final Resource subject = classIterator.getResource(0);
                final Resource predicate = classIterator.getResource(1);
                //final Resource object = tupleIterator.getResource(2);
                insertCA.setString(1,subject.toString());
                insertCA.setString(2,predicate.toString());
                insertCA.addBatch();

                //execute the statements and reset the counter
                if (cnt % batchSize == 0) { insertCA.executeBatch(); cnt=0;}
            }
            //execute the leftover statements
            insertCA.executeBatch();

            //retreveing property assertions
            TupleIterator propertyAssertionIterator =
                    store.compileQuery("SELECT DISTINCT ?property ?x ?y WHERE { \n" +
                            "    ?x a ?value .\n" +
                            "    ?y a ?othervalue .\n" +
                            "    ?x ?property ?y .\n" +
                            "}", prefixes);

            cnt=0;
            for (long multiplicity = propertyAssertionIterator.open(); multiplicity != 0; multiplicity = propertyAssertionIterator.advance()) {
                cnt++;
                // We iterate trough the terms of each tuple
                final Resource predicate = propertyAssertionIterator.getResource(0);
                final Resource subject = propertyAssertionIterator.getResource(1);
                final Resource object = propertyAssertionIterator.getResource(2);
                insertPA.setString(1,predicate.toString());
                insertPA.setString(2,subject.toString());
                insertPA.setString(3,object.toString());
                insertPA.addBatch();

                //execute the statements and reset the counter
                if (cnt % batchSize == 0) { insertPA.executeBatch(); cnt=0;}
            }
            //execute the leftover statements
            insertPA.executeBatch();

            TupleIterator individualsIterator =
                    store.compileQuery("SELECT DISTINCT ?x WHERE {?x a ?y }", prefixes);

            cnt=0;
            int id=0;
            for (long multiplicity = individualsIterator.open(); multiplicity != 0; multiplicity = individualsIterator.advance()) {
                cnt++;
                id++;
                // We iterate trough the terms of each tuple
                final Resource individual = individualsIterator.getResource(0);
                //final Resource object = tupleIterator.getResource(2);
                insertIND.setInt(1,id);
                insertIND.setString(2,individual.toString());
                insertIND.addBatch();

                //execute the statements and reset the counter
                if (cnt % batchSize == 0) { insertIND.executeBatch(); cnt=0;}
            }
            //execute the leftover statements
            insertIND.executeBatch();

        } catch (JRDFoxException e) {
            throw new RuntimeException(e);
        } finally {
            // When no longer needed, the data store should be disposed so that all related resources are released.
            store.dispose();
        }

    }

    /*split in two methods that do the exact job*/
    private void gatherABoxTBoxStats() throws SQLException {
        String sql = "SELECT gather_abox_tbox_stats()";
        stmt.execute(sql);
    }

    /*split in two methods that do the exact job*/
    private void cleanStagingTables() throws SQLException {
        String sql = "SELECT clean_staging_tables()";
        stmt.execute(sql);
    }


}
