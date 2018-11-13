package org.semanticweb.clipper.alch.Types;

import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by bato on 1/14/18.
 */
public class DBReasoner {
    long t_start;
    String sql;
    Connection conn;
    Statement stmt;
    ShortFormProvider sfp;
    boolean incremental;
    String ontologyFilename;

    public DBReasoner(Connection prmConn, boolean prmIncremaental, String prmOntologyFilename) {
        conn=prmConn;
        incremental=prmIncremaental;
        ontologyFilename=prmOntologyFilename;
        stmt= null;
        sfp= new SimpleShortFormProvider();
        try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void extractProfiles() throws SQLException {
        initializeTypesPerEachIndividual();
        computeDetPlusTypes();

                String procedure="extract_profiles";
                String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
                System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        t_start=System.currentTimeMillis();

        sql = "SELECT extract_profiles()";
        stmt.execute(sql);
        insertSubProcessTime(ontologyFilename,"6- extract profiles",stmt,t_start,System.currentTimeMillis());

        //insert the number of extracted profiles, as well as those existing from previous load
        sql = "SELECT gather_statistics_for_extract_profiles()";
        stmt.execute(sql);
    }

    /*important for incremental reasoning*/
    public void matchProfiles() throws SQLException {
                String procedure="match_profiles";
                String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
                System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        t_start=System.currentTimeMillis();

        sql = "SELECT match_profiles()";
        stmt.execute(sql);
        insertSubProcessTime(ontologyFilename,"7- match profiles",stmt,t_start,System.currentTimeMillis());

        sql = "SELECT gather_statistics_for_profiles_to_process_after_matching()";
        stmt.execute(sql);
    }

    /*important for incremental reasoning*/
    public void storeNewProfiles() throws SQLException {
                String procedure="store_new_profiles";
                String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
                System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        t_start=System.currentTimeMillis();

        String sql = "SELECT store_new_profiles()";
        stmt.execute(sql);
        insertSubProcessTime(ontologyFilename,"8- store new profiles",stmt,t_start,System.currentTimeMillis());

        sql="SELECT COUNT(*) FROM ";
    }

    /*important for incremental reasoning*/
    public void initializeBaseTypeComputation() throws SQLException {
                String procedure="initialize_base_type_computation";
                String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
                System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT initialise_base_type_computation()";
        stmt.execute(sql);
    }

    /*this procedure stores the link between the computed base types and their respective profiles*/
    public void storeLinkBetweenProfilesAndBaseTypes() throws SQLException {
                String procedure="storeLinkBetweenProfilesAndBaseTypes";
                String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
                System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        t_start=System.currentTimeMillis();

        sql = "SELECT store_link_profiles_ini_types()";
        stmt.execute(sql);
        insertSubProcessTime(ontologyFilename,"12- Store the link between Profiles and Candidate Types",stmt,t_start,System.currentTimeMillis());

        sql = "SELECT gather_statistics_after_candidate_types_are_generated()";
        stmt.execute(sql);
    }


    public int checkMaxGuesses() throws SQLException {
        int max_guesses=0;
        //check the number of guesses (if above 2^9 then abort)
        String sql ="SELECT value FROM ontology_metadata WHERE parameter='max_guesses';";

        ResultSet resultSet = stmt.executeQuery(sql);
        if(resultSet.next()) {
            max_guesses = resultSet.getInt("value");
        }
        return max_guesses;
    }


    private void initializeTypesPerEachIndividual() throws SQLException {
        t_start=System.currentTimeMillis();
        System.out.println("");
        String sql ="SELECT initialize_types()";
        stmt.execute(sql);
        System.out.println("Initialize Types -TIME: " + (System.currentTimeMillis() - t_start)/1000);
        insertSubProcessTime(ontologyFilename,"3- Initialize Types per each Indvidual",stmt,t_start,System.currentTimeMillis());
    }

    /*removes redundant guesses that allready are infered deterministically*/
    private void dedupGuessesPerType() throws SQLException{
        String procedure="deduplicate_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_dedup_guesses_per_type_table()";
        stmt.execute(sql);
    }


    private void eliminateContradictoryGuessesPerType() throws SQLException{
        String procedure="eliminate_contradictory_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_eliminate_contradictory_guesses_per_type()";
        stmt.execute(sql);
    }


    //generates Abox types, which come from the list of guesses per each type in table guesses_per_type
    private void generateRelevantAboxTypes() throws SQLException{
        boolean restart=false;
        String procedure="generate_relevant_Abox_types_based_on_guesses";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:" + timeStamp + "\t Process:guesses \tProcedure:" + procedure + "\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_generate_relevant_Abox_types_based_on_guesses()";
        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";

        ResultSet resultSet;

        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart)
            generateRelevantAboxTypes();
    }

    private void divideTypesBasedOnContradictoryGuesses() throws SQLException{
        String procedure="divide_types_based_on_contradictory_guesses";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql= "SELECT gs_divide_types_based_on_contradictory_guesses()";
        stmt.execute(sql);
    }


    private void eliminateDeterministicGuessesPerType() throws SQLException{
        String procedure="eliminate_deterministic_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_eliminate_deterministic_guesses_per_type()";
        stmt.execute(sql);
    }


    private void computeDetPlusTypes() throws SQLException {
        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";
        t_start=System.currentTimeMillis();

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        System.out.println("=================================================================================");
        System.out.println("Compute Deterministic Types plus consequnces from Doman and Range");

        //close types under domain restriction
        consequences_from_domain_restrictions(executionID, callingLevel, childTaskID, callingProcedure);
        //close types under range restriction
        consequences_from_range_restrictions(executionID, callingLevel, childTaskID, callingProcedure);
        //close  under simple axioms A->B & and(A1,..,An)->B
        rule_det(executionID, callingLevel, childTaskID, callingProcedure);

        insertSubProcessTime(ontologyFilename,"5- Det Closure of Types of Individuals",stmt,t_start,System.currentTimeMillis());
        System.out.println("Compute Det Plus Types -TIME: " + (System.currentTimeMillis() - t_start)/1000);
    }


    /*this method implement the core calling mechanism for the main algorithm as well as Dlitisation process*/
    public void precomputeBaseTypes() throws SQLException {
        //here the computation of profiles starts (since there is no more optimizations for this step left)
        t_start=System.currentTimeMillis();

        int executionID=0;

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        //TODO: add here the execution id

        System.out.println("Execution id:" + executionID);
        System.out.println("=================================================================================");

        //for each individual in abox get's the list of guesses
        initializeBaseTypeComputation();
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType();
        //eleminates contradictory guesses (those that violate disjointness axioms when joined with concept_list)
        eliminateContradictoryGuessesPerType();
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType();
        //eleminates contradictory guesses s.t. G1 and G1 fire some disjointness axiom
        divideTypesBasedOnContradictoryGuesses();
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType();
        //eleminates guesses that can be derived deterministically
        eliminateDeterministicGuessesPerType();
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType();
        //second run after having derived more deterministic concepts in previous step
        eliminateContradictoryGuessesPerType();
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType();

        insertSubProcessTime(ontologyFilename,"9- precompute base types",stmt,t_start,System.currentTimeMillis());

        //collects stats some stats
        sql = "select gather_statisitcs_after_precomputation_of_candidate_types();";
        stmt.execute(sql);

        System.out.println("Precompute base types -TIME: " + (System.currentTimeMillis() - t_start)/1000);
    }


    /*this method implements candidate (initial) type generation based on guesses (after the optimizations are carried out)*/
    public void generateBaseTypesToAlgorithm() throws SQLException{
        t_start=System.currentTimeMillis();

        generateRelevantAboxTypes();

        insertSubProcessTime(ontologyFilename,"11- generate types",stmt,t_start,System.currentTimeMillis());
    }


    /*this method implements type generation based on guesses (after the optimizations are carried out)*/
    public void inputBaseTypesToAlgorithm() throws SQLException{
                String procedure = "input_candidate_types2algorithm";
                String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
                System.out.println("Time:" + timeStamp + "\t Process:guesses \tProcedure:" + procedure + "\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

            t_start=System.currentTimeMillis();

            String sql = "SELECT input_candidate_types()";
            stmt.execute(sql);

            insertSubProcessTime(ontologyFilename,"13- input candidate types into algorithm",stmt,t_start,System.currentTimeMillis());
            System.out.println("Input Candidate Types into the Algorithm-TIME: " + (System.currentTimeMillis() - t_start)/1000);
    }


    /*this method implement the core calling mechanism for the main algorithm as well as Dlitisation process*/
    public void computeKnots() throws SQLException, IOException, InterruptedException {

        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";
        t_start=System.currentTimeMillis();

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        //TODO: add here the execution id

        System.out.println("Execution id:" + executionID);
        System.out.println("=================================================================================");

        //close types under domain restriction
        rule_det(executionID, callingLevel, childTaskID, callingProcedure);
        //close types under range restriction
        rule_nondet(executionID, callingLevel, childTaskID, callingProcedure);
        //close  under simple axioms A->B
        rule_add_succ(executionID, callingLevel, childTaskID, callingProcedure);
        //close under axioms and(A1,..,An)->B
        rule_forw(executionID, callingLevel, childTaskID, callingProcedure);
        //close under axioms A->(U)R.B
        rule_back(executionID, callingLevel, childTaskID, callingProcedure);
        //mark badknots
        mark_bad_knots(executionID, callingLevel, childTaskID, callingProcedure);

        insertSubProcessTime(ontologyFilename,"14- compute knots",stmt,t_start,System.currentTimeMillis());

        System.out.println("Knot Algorithm runtime-TIME: " + (System.currentTimeMillis()-t_start)/1000);

        t_start=System.currentTimeMillis();
        //fill dl enconding representation tables
        encode_into_datalog_representation(stmt, executionID, callingLevel, childTaskID, callingProcedure);

        insertSubProcessTime(ontologyFilename,"15- encode into datalog",stmt,t_start,System.currentTimeMillis());

        //gather statistics regarding the number of good types, including good_types per profile and at_most_gt_per_profile
        sql ="select gather_final_stats_about_good_types();";
        stmt.execute(sql);
    }


    /*this method computes the consequnces that come as a result of firing axioms with conjunctions on lhs*/
    private void rule_det(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        String procedure="rule_det";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:"+timeStamp+"\t Process:main\t\tProcedure:"+procedure+"\t\t\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);

        String sql =String
                .format("SELECT rule_det( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }


    /*this method computes the consequnces that come as a result of firing axioms with disjunctions on rhs*/
    private void rule_nondet(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;
        String procedure="rule_nondet";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart=false;

        System.out.println("Time:" + timeStamp + "\t Process:main\t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \tcallingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT rule_nondet( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart){
            childTaskId=1;
            rule_det(executionID, level, childTaskId, procedure);
            childTaskId++;
            mark_bad_types(executionID, level, childTaskId, procedure);
            //childTaskId++;
            //mark_bad_knots(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_nondet(executionID, level, childTaskId, procedure);
        }
    }


    /*this method computes the consequnces that come as a result of firing axioms with universals on rhs*/
    private void rule_add_succ(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;
        String procedure="rule_add_succ";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT rule_add_succ( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if (restart){
            childTaskId=1;
            rule_det(executionID, level, childTaskId, procedure);
            childTaskId++;
            mark_bad_types(executionID, level, childTaskId, procedure);
            //childTaskId++;
            //mark_bad_knots(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_nondet(executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_add_succ(executionID, level, childTaskId, procedure);
        }
    }


    /*this method computes the consequnces that come as a result of firing axioms with universals on rhs*/
    private void rule_forw(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;
        String procedure="rule_forw";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT rule_forw( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if (restart){
            childTaskId=1;
            rule_det(executionID, level, childTaskId, procedure);
            childTaskId++;
            mark_bad_types(executionID, level, childTaskId, procedure);
            //childTaskId++;
            //mark_bad_knots(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_nondet(executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_add_succ(executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_forw(executionID, level, childTaskId, procedure);
        }
    }


    /*this method computes the consequnces that come as a result of firing axioms with universals on rhs*/
    private void rule_back(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;
        String procedure="rule_back";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT rule_back( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if (restart){
            childTaskId=1;
            rule_det(executionID, level, childTaskId, procedure);
            childTaskId++;
            mark_bad_types(executionID, level, childTaskId, procedure);
            //childTaskId++;
            //mark_bad_knots(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_nondet(executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_add_succ(executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_forw(executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_back(executionID, level, childTaskId, procedure);
        }
    }


    /*this method computes the consequences that come from domain restrictions*/
    private void consequences_from_domain_restrictions(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="consequences_from_domain_restrictions";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:"+timeStamp+"\t Process:main \t\tProcedure:"+procedure+"\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);

        String sql =String
                .format("SELECT consequences_from_domain_restrictions( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }


    /*this method computes the consequences that come from range restrictions*/
    private void consequences_from_range_restrictions(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="consequences_from_range_restrictions";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT consequences_from_range_restrictions( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }


    /*this method computes the consequences that come from range restrictions*/
    private void mark_bad_types(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="mark_bad_types";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT mark_bad_types( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }


    /*this method computes the consequences that come from range restrictions*/
    private void mark_bad_knots(int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="mark_bad_knots";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT mark_bad_knots( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*archive types (needed for incremental reasoning)*/
    public void storeExistingTypes() throws SQLException {
                String procedure="store_existing_types";
                String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
                System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + "computeKnots");// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        t_start=System.currentTimeMillis();

        sql ="TRUNCATE TABLE existing_types";

        stmt.execute(sql);
        sql ="INSERT INTO existing_types SELECT * FROM types";
        stmt.execute(sql);

        sql ="TRUNCATE TABLE existing_knots";
        stmt.execute(sql);

        sql ="INSERT INTO existing_knots SELECT * FROM knots";
        stmt.execute(sql);

        insertSubProcessTime(ontologyFilename,"16- store existing types",stmt,t_start,System.currentTimeMillis());
    }


    /*this method computes the dl representation and stores it in the database*/
    private void encode_into_datalog_representation(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="encode_into_datalog";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql ="SELECT encode_into_datalog_new();";

        stmt.execute(sql);
    }


    private void saveDatalogEncoding(Statement stmt) throws SQLException, FileNotFoundException {
        String      InputDatalogFile="";
        String      OutputDatalogFile="";
        ResultSet   resultSet;
        String      sql;

        sql ="select value filename from ontology_metadata where parameter='Filename'";

        resultSet=stmt.executeQuery(sql);

        if(resultSet.next()) {
            InputDatalogFile = "TestData/knots/input_" + resultSet.getString("filename") + ".lp";
            OutputDatalogFile = "TestData/knots/output_" + resultSet.getString("filename") + ".lp";
        }
        DatalogProgram program = new DatalogProgram();
        program.setTranslation2DataLogFile(InputDatalogFile);
        program.setAnswers2DataLogFile(OutputDatalogFile);

        //program.generateDatalogFacts(stmt);
        program.generateDatalogRules(stmt);
    }


    //datalog rewritting with Mantas'es rewritting -explicit constraints for each type and concept
    private void saveDatalogEncoding2(Statement stmt) throws SQLException, FileNotFoundException {
        String      InputDatalogFile="";
        String      OutputDatalogFile="";
        ResultSet   resultSet;
        String      sql;

        sql ="select value filename from ontology_metadata where parameter='Filename'";

        resultSet=stmt.executeQuery(sql);

        if(resultSet.next()) {
            InputDatalogFile = "TestData/knots/input2_" + resultSet.getString("filename") + ".lp";
            OutputDatalogFile = "TestData/knots/output_" + resultSet.getString("filename") + ".lp";
        }
        DatalogProgram program = new DatalogProgram();
        program.setTranslation2DataLogFile(InputDatalogFile);
        program.setAnswers2DataLogFile(OutputDatalogFile);

        //program.generateDatalogFacts(stmt);
        program.generateDatalogRules2(stmt);
    }


    //evaluateDatalog encoding for each concept name
    public void runDatalogEncoding4EachConcept() throws SQLException, FileNotFoundException, InterruptedException {
        String      InputDatalogFile="";
        String      OutputDatalogFile="";
        ResultSet   resultSet;
        String      sql;

        t_start=System.currentTimeMillis();
        sql ="select value filename from ontology_metadata where parameter='Filename'";

        resultSet=stmt.executeQuery(sql);

        if(resultSet.next()) {
            InputDatalogFile = "TestData/knots/ijcai18/trans_iq_" + resultSet.getString("filename") + ".lp";
            OutputDatalogFile = "TestData/knots/ijcai18/answers_" + resultSet.getString("filename") + ".lp";
        }
        DatalogProgram program = new DatalogProgram();
        program.setTranslation2DataLogFile(InputDatalogFile);
        program.setAnswers2DataLogFile(OutputDatalogFile);

        try {
            program.generateDatalog4EachConcept(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        insertSubProcessTime(ontologyFilename,"17-Total runtime of encoding per each IQ concept name",stmt,t_start,System.currentTimeMillis());

    }

    //evaluateDatalog encoding for each concept name
    public void runReachabilityQueries() throws SQLException, FileNotFoundException, InterruptedException {
        String      InputDatalogFile="";
        String      OutputDatalogFile="";
        ResultSet   resultSet;
        String      sql;

        t_start=System.currentTimeMillis();
        sql ="select value filename from ontology_metadata where parameter='Filename'";

        resultSet=stmt.executeQuery(sql);

        if(resultSet.next()) {
            InputDatalogFile = "TestData/knots/ijcai18/trans_iq_" + resultSet.getString("filename") + ".lp";
            OutputDatalogFile = "TestData/knots/ijcai18/answers_" + resultSet.getString("filename") + ".lp";
        }
        DatalogProgram program = new DatalogProgram();
        program.setTranslation2DataLogFile(InputDatalogFile);
        program.setAnswers2DataLogFile(OutputDatalogFile);

        try {
            program.generateDatalog4EachReachabilityQuery(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        insertSubProcessTime(ontologyFilename,"17-Total runtime of encoding per each IQ concept name",stmt,t_start,System.currentTimeMillis());

    }



    private void recordAllAnswers2IQ4Concepts(Statement stmt) throws SQLException, FileNotFoundException {
        String      InputDatalogFile="";
        String      OutputDatalogFile="";
        String      filename="";
        ResultSet   resultSet;
        String      sql;

        sql ="select substr(value,65,9) filename from ontology_metadata where parameter='Filename'";

        resultSet=stmt.executeQuery(sql);

        if(resultSet.next()) {
            filename = resultSet.getString("filename");
            InputDatalogFile = "TestData/knots/input_" + resultSet.getString("filename") + ".dl";
            OutputDatalogFile = "TestData/knots/output_" + resultSet.getString("filename") + ".dl";
        }
        DatalogProgram program = new DatalogProgram();
        program.setTranslation2DataLogFile(InputDatalogFile);
        program.setAnswers2DataLogFile(OutputDatalogFile);

        //program.generateDatalogFacts(stmt);
        program.generateDatalogRules(stmt);

        sql =String.
                format("delete from answers where ontology_filename='%1$s' "
                        ,filename);

        stmt.execute(sql);

        sql ="select id from concepts";

        resultSet=stmt.executeQuery(sql);

        //the string that will hold the concept instance query
        int int_concept;
        String str_individual;

        ArrayList<String> answers2IQ = new ArrayList<String>();
        ArrayList<Integer> conceptList = new ArrayList<Integer>();

        while(resultSet.next()) {
            conceptList.add(resultSet.getInt("id"));
        }

        for(Integer int_conc:conceptList) {
            answers2IQ=program.execInstanceQuery("conc_" + int_conc);

            if(answers2IQ.size()==0){
                sql = String
                        .format("INSERT INTO answers (ontology_filename,"
                                        + "concept_name,"
                                        + "individual_name) "
                                        + "SELECT '%1$s','%2$d', NULL ",
                                filename, int_conc);

                stmt.execute(sql);
            }else{
                for(String str :answers2IQ){
                    str_individual=str.substring(str.indexOf("(")+1,str.indexOf(")"));
                    sql = String
                            .format("INSERT INTO answers (ontology_filename,"
                                            + "concept_name,"
                                            + "individual_name) "
                                            + "SELECT '%1$s','%2$d','%3$s' ",
                                    filename, int_conc, str_individual);

                    stmt.execute(sql);
                }
            }
        }


        sql = String
                .format("insert into answers_decoded "
                                +"select a.ontology_filename,c.concept,i.individual from answers a "
                                +"left join concepts c on a.concept_name = c.id "
                                +"left join individuals i on a.individual_name = i.id "
                                +"where ontology_filename='%1$s'"
                        ,filename);

        stmt.execute(sql);
    }


    private void insertSubProcessTime(String ontologyFilename, String process, Statement stmt, long t_start, long t_end) {
        String sql =String
                .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'%3$s');",
                        ontologyFilename, System.currentTimeMillis()-t_start,process);

        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void insertNominalStats(String ontologyFilename, String metric, Statement stmt, int cnt) {
        String sql =String
                .format("INSERT INTO stat_nominal(filename,value,metric) VALUES ( '%1$s', %2$d,'%3$s');",
                        ontologyFilename, cnt,metric);

        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
