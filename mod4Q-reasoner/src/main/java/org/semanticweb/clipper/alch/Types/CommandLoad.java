package org.semanticweb.clipper.alch.Types;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer2;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.util.*;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Parameters(commandNames = { "load" }, separators = "=", commandDescription = "Load ABox facts to Database")
public class CommandLoad extends DBCommandBase {

	private Connection conn;

	public CommandLoad(JCommander jc) {
		super(jc);
	}

	@Override
	boolean validate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	void exec() {
		long t1 = System.currentTimeMillis();//time of starting the main process
		long d1;//time of end of load onotlogy from OWLAPI (d1-t1) gives the runtime of load ontolgy
		long d2;//time of end of normalization (d2-d1) gives the runtime of normalization
		long d3;//time of end of loading the tables - gives the runtime of loadin process to DB

		conn = createConnection();
		String sql="";

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		ShortFormProvider sfp = new SimpleShortFormProvider();

		OWLOntology ontology = null;

		//OWLOntology normOntology= null;
		ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();

		try {
			for (String ontologyFilePath : this.getOntologyFiles()) {

				File ontologyFile = new File(ontologyFilePath);
				String ontologyFilename = ontologyFile.getName().substring(0,ontologyFile.getName().lastIndexOf("."));

				ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);

					d1=System.currentTimeMillis();

				sql =String
						.format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'load ontology OWLAPI');",
								ontologyFilename, d1-t1);

				stmt.execute(sql);

				//

				ontology = normalizer.normalize(ontology);

				d2=System.currentTimeMillis();

				sql =String
						.format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'normalization');",
								ontologyFilename, d2-d1);

				stmt.execute(sql);

							//Reasoner hermit=new Reasoner(ontology);
							// Finally, we output whether the ontology is consistent.
							//System.out.println(ontologyFilename);
							//System.out.println(hermit.isConsistent());

				insertOntologyMetadata(stmt, ontology, ontologyFilename);

				cleanStagingTables(stmt);

				cleanComputationTables(stmt);

				cleanStatisticsTables(stmt);

				insertConcepts(stmt, sfp, ontology);

				insertObjectRoles(stmt, sfp, ontology);

				insertIndividuals(stmt, ontology);

				insertConceptAssertions(stmt, sfp, ontology);

				insertObjectRoleAssertions(stmt, sfp, ontology);

				insertAxioms(stmt, sfp, ontology);

				manager.removeOntology(ontology);

				d3=System.currentTimeMillis();

				sql =String
						.format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'load to DB');",
								ontologyFilename, d3-d2);

				stmt.execute(sql);

				System.err.println(ontologyFile + " loaded!");
			}
			stmt.close();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long t2 = System.currentTimeMillis();
		System.out.println("TIME: " + (t2 - t1));

	}

	private void insertOntologyMetadata(Statement stmt,OWLOntology ontology,String ontologyFileName) throws SQLException {

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

	private void cleanStagingTables(Statement stmt) throws SQLException {
		String sql = "SELECT clean_staging_tables()";
		stmt.execute(sql);
	}

	private void cleanComputationTables(Statement stmt) throws SQLException {
		String sql = "SELECT clean_computation_tables()";
		stmt.execute(sql);
	}

	private void cleanStatisticsTables(Statement stmt) throws SQLException {
		String sql = "SELECT clean_statistics_tables()";
		stmt.execute(sql);
	}

/*	private void insertIndividuals(Statement stmt, OWLOntology ontology)throws SQLException {
		Set<OWLNamedIndividual> individuals = ontology
				.getIndividualsInSignature();

		int individualId=0;
		for (OWLNamedIndividual individual : individuals) {
			String strIndividual = individual.toString();
			individualId++;
			//strip the link away from the individual name
			if(strIndividual.indexOf("#")>-1){
				strIndividual = strIndividual.substring(strIndividual.indexOf("#")+1,strIndividual.indexOf(">"));
			}

			String sql =String
					.format("INSERT INTO st_individuals (id,individual) "
									+ "SELECT '%1$d','%2$s' ",
									//+ "WHERE NOT EXISTS (SELECT * FROM st_individuals WHERE individual='%2$s' )",
							individualId, strIndividual);

			stmt.execute(sql);
		}
	}*/

	private void insertIndividuals(Statement stmt, OWLOntology ontology)throws SQLException {
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


/*	private void insertConcepts(Statement stmt, ShortFormProvider sfp, OWLOntology ontology)throws SQLException {
		Set<OWLClass> classes = ontology.getClassesInSignature(true);

		String sql=String.format("INSERT INTO st_concepts (id,concept) VALUES (0,'Nothing')");

		stmt.execute(sql);

		sql=String.format("INSERT INTO st_concepts (id,concept) VALUES (1,'Thing')");

		stmt.execute(sql);

		int classId=1;

		for (OWLClass cls : classes) {

			if(cls.isOWLThing()||cls.isOWLNothing())
				continue;

			classId++;

			String clsName = sfp.getShortForm(cls);

				sql=String.format("INSERT INTO st_concepts (id,concept) "
							+ "SELECT '%1$d','%2$s' ",
							//+ "WHERE NOT EXISTS (SELECT * FROM st_concepts WHERE lower(concept)=lower('%2$s'))  ",
					classId, clsName);

			 stmt.execute(sql);
		}
	}*/

	private void insertConcepts(Statement stmt, ShortFormProvider sfp, OWLOntology ontology)throws SQLException {
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


/*	private void insertObjectRoles(Statement stmt, ShortFormProvider sfp, OWLOntology ontology)throws SQLException {

		Set<OWLObjectProperty> objectProperties = ontology
				.getObjectPropertiesInSignature(true);

		int propertyID=0;

		for (OWLObjectProperty property : objectProperties) {
			propertyID++;
			String propertyName = sfp.getShortForm(property);
			String sql = String
					.format("INSERT INTO st_properties (id,rolename) "
									+ "SELECT '%1$d','%2$s' ",
									//+ "WHERE NOT EXISTS (SELECT * FROM st_properties WHERE rolename='%2$s')  ",
							propertyID, propertyName);

			stmt.execute(sql);
		}
	}*/


	private void insertObjectRoles(Statement stmt, ShortFormProvider sfp, OWLOntology ontology)throws SQLException {
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


//	private void insertConceptAssertions(Statement stmt, ShortFormProvider sfp,OWLOntology ontology)throws SQLException {
//
//		 Set<OWLClassAssertionAxiom> classAssertionAxioms = ontology.getAxioms(AxiomType.CLASS_ASSERTION, false);
//
//		 for (OWLClassAssertionAxiom axiom : classAssertionAxioms) {
//			 OWLClassExpression classExpression = axiom.getClassExpression();
//			 String className = sfp.getShortForm(classExpression.asOWLClass());
//			 String individual = axiom.getIndividual().toString();
//
//			 //strip the link away from the individual name
//			 if(individual.indexOf("#")>-1){
//				 individual = individual.substring(individual.indexOf("#")+1,individual.indexOf(">"));
//			 }
//
//			String sql =String
//					.format("INSERT INTO st_concept_assertions "
//									+ "SELECT '%1$s','%2$s' ",
//									//+ "WHERE NOT EXISTS (SELECT * FROM st_concept_assertions WHERE individual='%1$s' and concept='%2$s')",
//							individual, className);
//
//			 stmt.execute(sql);
//	 	}
//	 }


	private void insertConceptAssertions(Statement stmt, ShortFormProvider sfp,OWLOntology ontology)throws SQLException {

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

/*	private void insertObjectRoleAssertions(Statement stmt,ShortFormProvider sfp, OWLOntology ontology) throws SQLException {

		Set<OWLObjectPropertyAssertionAxiom> objectRoleAssertionAxioms = ontology
				.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION, false);

		for (OWLObjectPropertyAssertionAxiom axiom : objectRoleAssertionAxioms) {
			OWLObjectProperty property = axiom.getProperty()
					.asOWLObjectProperty();

			String strProperty = sfp.getShortForm(property);

			String strIndividualInDomain=axiom.getSubject().toString();
			String strIndividualInRange=axiom.getObject().toString();

			//strip the link away
			if(strIndividualInDomain.indexOf("#")>-1){
				strIndividualInDomain = strIndividualInDomain.substring(strIndividualInDomain.indexOf("#")+1,strIndividualInDomain.indexOf(">"));
			}

			//strip the link away
			if(strIndividualInRange.indexOf("#")>-1){
				strIndividualInRange = strIndividualInRange.substring(strIndividualInRange.indexOf("#")+1,strIndividualInRange.indexOf(">"));
			}

			String sql =String.format("INSERT INTO st_property_assertions "
							+ "SELECT '%1$s','%2$s','%3$s' ",
							//+ "WHERE NOT EXISTS (SELECT * FROM st_property_assertions WHERE role='%1$s' and individual_in_domain='%2$s' and individual_in_range='%3$s')",
					strProperty, strIndividualInDomain, strIndividualInRange);

			stmt.execute(sql);
		}
	}*/

	private void insertObjectRoleAssertions(Statement stmt,ShortFormProvider sfp, OWLOntology ontology) throws SQLException {

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


	private void insertAxioms(Statement stmt,ShortFormProvider sfp, OWLOntology ontology) throws SQLException {

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

/*
				sql =String.format("INSERT INTO st_axiom_with_universal_in_lhs "
								+ "SELECT '%1$d','%2$s','%3$s','%4$s' "
								+ "WHERE NOT EXISTS (SELECT * FROM st_axiom_with_universal_in_lhs WHERE role='%2$s' and concept_in_range='%3$s' and concept_in_rhs='%4$s')",
						axiomId,propertyName, range, conept_in_rhs);

				stmt.execute(sql);
*/

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
}
