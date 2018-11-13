package org.semanticweb.clipper.alch.Types;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;

@Parameters(commandNames = { "knots" }, separators = "=", commandDescription = "Generate knots")
public class CommandComputeKnots extends DBCommandBase {

    long t1 = System.currentTimeMillis();
    long t2; //end time of load core tables
    long t3; //end time of profile extraction
    long t4; //end time of computing for cand types
    long t5; //end time of computing the algorithm
    long t6; //end time of encoding to datalog

    long delta1;//save the time point to extract the incremental time in the next step
    long delta2;//save the time point to extract the incremental time in the next step

    int max_guesses=0;
    String filename="";


    private Connection conn;

    public CommandComputeKnots(JCommander jc) {
        super(jc);
    }

    @Override
    boolean validate() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    void exec() {


        conn = createConnection();
        ResultSet resultSet;

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try {
            String sql="SELECT value FROM ontology_metadata WHERE parameter='Filename';";
            resultSet=stmt.executeQuery(sql);
            if(resultSet.next()) {
                filename = resultSet.getString("value");
            }

            //new algorithm
            insertQueryAtoms(stmt);                         //inserts the query atoms for Hybrid KB's (for now input is done manually directly in this file)
            loadCoreTables(stmt);                           //load the core tables from staging tables
            loadArrayTables(stmt);                          //load the list tables from core tables
            t2=System.currentTimeMillis();
                System.out.println("Load Core Array Tables -TIME: " + (t2 - t1)/1000);

            sql =String
                    .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'load core');",
                            filename, t2-t1);

            stmt.execute(sql);

            initializeTypes(stmt);                          //initialise hierarchical tables and initial types
            delta1=System.currentTimeMillis();

            System.out.println("Initialize Types -TIME: " + (delta1 - t1)/1000);

            computeDetPlusTypes(stmt);                      //compute types with main algorithm
            delta2=System.currentTimeMillis();

            System.out.println("Compute Det Plus Types -TIME: " + (delta2 - delta1)/1000);

            computeTypesWithGuessing(stmt);                  //compute types with guessing algorithm

                    //check the number of guesses (if above 2^9 then abort)
                    sql ="SELECT value FROM ontology_metadata WHERE parameter='max_guesses';";

                    resultSet=stmt.executeQuery(sql);
                    if(resultSet.next()) {
                        max_guesses = resultSet.getInt("value");
                    }

            if(max_guesses<10) {
                generateCandidateTypes(stmt);                   //generate candidate types based on guessing


                        //insert the number of candidate types
                        sql =String
                                .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) " +
                                                "SELECT '%1$s', COUNT(*), 'no cand types' FROM types WHERE NOT bad;",
                                        filename);

                        stmt.execute(sql);


                computeKnots(stmt);

                        //insert the number of types
                        sql =String
                                .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) " +
                                                "SELECT '%1$s', COUNT(*), 'no types in the end' FROM types;",
                                        filename);

                        stmt.execute(sql);


                        //insert the number of good types
                        sql =String
                                .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) " +
                                                "SELECT '%1$s', COUNT(*), 'no good types in the end' FROM types WHERE NOT bad;",
                                        filename);

                        stmt.execute(sql);


                //new rewritting save to file
                //saveDatalogEncoding(stmt);

                //invoke the reasoner for IQ
                runDatalogEncoding4EachConcept(stmt);
            }else{
                System.err.println("Number of guesses to high:"+max_guesses+" aborting further processing for ontology "+filename);
            }
            //invoke datalog program and initialize it with datalog representation from DB
            //recordAllAnswers2IQ4Concepts(stmt);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (/*FileNotFoundException*/Exception e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Overall running time: " + (t2 - t1));
    }


    private void insertQueryAtoms(Statement stmt) throws SQLException {
        //TODO:don't forget to update class (unary) atoms used in ASP query (only those found in the signature of the ontology)
        //String sql = "INSERT INTO st_query_atoms (name,class_atom) VALUES ('Hotel',true),('Bar',true),('Club',true)";
        //String sql = "INSERT INTO st_query_atoms (name,class_atom) VALUES ('Hotel',true),('RegionalRestaurant',true)";
        //stmt.execute(sql);

        //TODO:don't forget to update atoms used in ASP query (only those found in the signature of the ontology)
        //sql = "INSERT INTO st_query_atoms (name,class_atom) VALUES ('isLocatedNext',false),('nextStation',false),('serves',false)";
        //stmt.execute(sql);
    }

    private void loadCoreTables(Statement stmt) throws SQLException {
        String sql = "SELECT load_core_tables()";
        stmt.execute(sql);
    }

    private void loadArrayTables(Statement stmt) throws SQLException {
        String sql = "SELECT load_list_tables()";
        stmt.execute(sql);
    }

    private void initializeTypes(Statement stmt) throws SQLException {

        System.out.println("");

        String sql ="SELECT initialize_types()";

        stmt.execute(sql);
    }

    private void computeGuessesPerType(Statement stmt) throws SQLException{
        String procedure="compute_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        //compute the list of potential guesses for each type in the ABox
        //deduplificate the list of identical types, link the individuals with the list of unique types
        String sql = "SELECT gs_compute_guesses_per_concept_profile()";
        stmt.execute(sql);    }

    private void dedupGuessesPerType(Statement stmt) throws SQLException{
        String procedure="deduplicate_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_dedup_guesses_per_type_table()";
        stmt.execute(sql);
    }

    private void eliminateContradictoryGuessesPerType(Statement stmt) throws SQLException{
        String procedure="eliminate_contradictory_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_eliminate_contradictory_guesses_per_type()";
        stmt.execute(sql);
    }

    //generates Abox types, which come from the list of guesses per each type in table guesses_per_type
    private void generateRelevantAboxTypes(Statement stmt) throws SQLException{
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
            generateRelevantAboxTypes(stmt);
    }

    private void intializeGuessedAboxTypes(Statement stmt) throws SQLException {
        String procedure = "initialize_abox_individuals";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:" + timeStamp + "\t Process:guesses \tProcedure:" + procedure + "\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_initialize_abox_individuals()";
        stmt.execute(sql);

        //here the computation of profiles ends (since there is no more optimizations for this step left)
        t4=System.currentTimeMillis();

        System.out.println("Candidate Types Generation-TIME: " + (t4 - t3)/1000);

        sql =String
                .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'candidate types generation');",
                        filename, t4-t3);

        stmt.execute(sql);

    }

    private void divideTypesBasedOnContradictoryGuesses(Statement stmt) throws SQLException{
        String procedure="divide_types_based_on_contradictory_guesses";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql= "SELECT gs_divide_types_based_on_contradictory_guesses()";
        stmt.execute(sql);
    }

    private void eliminateDeterministicGuessesPerType(Statement stmt) throws SQLException{
        String procedure="eliminate_deterministic_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_eliminate_deterministic_guesses_per_type()";
        stmt.execute(sql);
    }

    private void computeDetPlusTypes(Statement stmt) throws SQLException {
        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        System.out.println("=================================================================================");
        System.out.println("Compute Deterministic Types plus consequnces from Doman and Range");

        //close types under domain restriction
        consequences_from_domain_restrictions(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close types under range restriction
        consequences_from_range_restrictions(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close  under simple axioms A->B & and(A1,..,An)->B
        rule_det(stmt, executionID, callingLevel, childTaskID, callingProcedure);
    }

    /*this method implement the core calling mechanism for the main algorithm as well as Dlitisation process*/
    private void computeTypesWithGuessing(Statement stmt) throws SQLException {

        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        //TODO: add here the execution id

        System.out.println("Execution id:" + executionID);
        System.out.println("=================================================================================");

        //for each individual in abox get's the list of guesses
        computeGuessesPerType(stmt);
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType(stmt);
        //eleminates contradictory guesses (those that violate disjointness axioms when joined with concept_list)
        eliminateContradictoryGuessesPerType(stmt);
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType(stmt);
        //eleminates contradictory guesses s.t. G1 and G1 fire some disjointness axiom
        divideTypesBasedOnContradictoryGuesses(stmt);
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType(stmt);
        //eleminates guesses that can be derived deterministically
        eliminateDeterministicGuessesPerType(stmt);
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType(stmt);
        //second run after having derived more deterministic concepts in previous step
        eliminateContradictoryGuessesPerType(stmt);
        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        dedupGuessesPerType(stmt);

                //here the computation of profiles ends (since there is no more optimizations for this step left)
                t3=System.currentTimeMillis();

                System.out.println("Compute Profiles -TIME: " + (t3 - t2)/1000);

                sql =String
                        .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'profile extraction');",
                                filename, t3-t2);

                stmt.execute(sql);

                //collects stats some stats
                sql = "select insert_statistics();";
                stmt.execute(sql);
    }

    /*this method implements type generation based on guesses (after the optimizations are carried out)*/
    private void generateCandidateTypes(Statement stmt) throws SQLException{
        //generates Abox types, which come from the list of guesses per each type in table guesses_per_type
        generateRelevantAboxTypes(stmt);
        //initialize guessed ABox types
        intializeGuessedAboxTypes(stmt);
    }


    /*this method implement the core calling mechanism for the main algorithm as well as Dlitisation process*/
    private void computeKnots(Statement stmt) throws SQLException, IOException, InterruptedException {

        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";


        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        //TODO: add here the execution id

        System.out.println("Execution id:" + executionID);
        System.out.println("=================================================================================");

        //close types under domain restriction
        rule_det(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close types under range restriction
        rule_nondet(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close  under simple axioms A->B
        rule_add_succ(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close under axioms and(A1,..,An)->B
        rule_forw(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close under axioms A->(U)R.B
        rule_back(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //mark badknots
        mark_bad_knots(stmt, executionID, callingLevel, childTaskID, callingProcedure);

                    //here the computation of types ends
                    t5=System.currentTimeMillis();

                    System.out.println("Knot Algorithm runtime-TIME: " + (t5 - t4)/1000);

                    sql =String
                            .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'knot algorithm runtime');",
                                    filename, t5-t4);

                    stmt.execute(sql);

        //fill dl enconding representation tables
        encode_into_datalog_representation(stmt, executionID, callingLevel, childTaskID, callingProcedure);

                    //here the computation of profiles ends (since there is no more optimizations for this step left)
                    t6=System.currentTimeMillis();

                    System.out.println("Encoding to datalog-TIME: " + (t6 - t5)/1000);

                    sql =String
                            .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'encoding to datalog');",
                                    filename, t6-t5);

                    stmt.execute(sql);
    }

    /*this method computes the consequnces that come as a result of firing axioms with conjunctions on lhs*/
    private void rule_det(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        String procedure="rule_det";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:"+timeStamp+"\t Process:main\t\tProcedure:"+procedure+"\t\t\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);

        String sql =String
                .format("SELECT rule_det( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequnces that come as a result of firing axioms with disjunctions on rhs*/
    private void rule_nondet(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

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
            rule_det(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            mark_bad_types(stmt, executionID, level, childTaskId, procedure);
            //childTaskId++;
            //mark_bad_knots(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_nondet(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequnces that come as a result of firing axioms with universals on rhs*/
    private void rule_add_succ(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

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
            rule_det(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            mark_bad_types(stmt, executionID, level, childTaskId, procedure);
            //childTaskId++;
            //mark_bad_knots(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_nondet(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_add_succ(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequnces that come as a result of firing axioms with universals on rhs*/
    private void rule_forw(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

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
            rule_det(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            mark_bad_types(stmt, executionID, level, childTaskId, procedure);
            //childTaskId++;
            //mark_bad_knots(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_nondet(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_add_succ(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_forw(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequnces that come as a result of firing axioms with universals on rhs*/
    private void rule_back(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

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
            rule_det(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            mark_bad_types(stmt, executionID, level, childTaskId, procedure);
            //childTaskId++;
            //mark_bad_knots(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_nondet(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_add_succ(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_forw(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            rule_back(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequences that come from domain restrictions*/
    private void consequences_from_domain_restrictions(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="consequences_from_domain_restrictions";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:"+timeStamp+"\t Process:main \t\tProcedure:"+procedure+"\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);

        String sql =String
                .format("SELECT consequences_from_domain_restrictions( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequences that come from range restrictions*/
    private void consequences_from_range_restrictions(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="consequences_from_range_restrictions";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT consequences_from_range_restrictions( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequences that come from range restrictions*/
    private void mark_bad_types(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="mark_bad_types";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT mark_bad_types( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequences that come from range restrictions*/
    private void mark_bad_knots(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="mark_bad_knots";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT mark_bad_knots( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the dl representation and stores it in the database*/
    private void encode_into_datalog_representation(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="encode_into_datalog";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql ="SELECT encode_into_datalog();";

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
    private void runDatalogEncoding4EachConcept(Statement stmt) throws SQLException, FileNotFoundException, InterruptedException {
        String      InputDatalogFile="";
        String      OutputDatalogFile="";
        ResultSet   resultSet;
        String      sql;

        sql ="select value filename from ontology_metadata where parameter='Filename'";

        resultSet=stmt.executeQuery(sql);

        if(resultSet.next()) {
            InputDatalogFile = "TestData/knots/dl2017/trans_iq_" + resultSet.getString("filename") + ".lp";
            OutputDatalogFile = "TestData/knots/dl2017/answers_" + resultSet.getString("filename") + ".lp";
        }
        DatalogProgram program = new DatalogProgram();
        program.setTranslation2DataLogFile(InputDatalogFile);
        program.setAnswers2DataLogFile(OutputDatalogFile);

        try {
            program.generateDatalog4EachConcept(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
