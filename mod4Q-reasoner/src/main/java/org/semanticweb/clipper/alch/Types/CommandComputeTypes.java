package org.semanticweb.clipper.alch.Types;

        import com.beust.jcommander.JCommander;
        import com.beust.jcommander.Parameters;
        import org.semanticweb.clipper.dllog.DLLogLexer;
        import org.semanticweb.owlapi.model.*;

        import java.sql.Connection;
        import java.sql.SQLException;
        import java.sql.Statement;
        import java.sql.ResultSet;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;

@Parameters(commandNames = { "gen" }, separators = "=", commandDescription = "Generate types")
public class CommandComputeTypes extends DBCommandBase {

    private Connection conn;

    public CommandComputeTypes(JCommander jc) {
        super(jc);
    }

    @Override
    boolean validate() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    void exec() {
        long t1 = System.currentTimeMillis();
        conn = createConnection();

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try {
                //new algorithm
                loadCoreTables(stmt);                           //load the core tables from staging tables
                loadArrayTables(stmt);                          //load the list tables from core tables
                initializeTypes(stmt);                          //initialise hierarchical tables and initial types
                computeDetPlusTypes(stmt);                      //compute types with main algorithm
                computeTypesWithGuessing(stmt);                  //compute types with guessing algorithm

                String sql = "SELECT temporary_stat4()";
                stmt.execute(sql);


                //old algorithm

                /*TODO uncomment this(main Algorithm)
                loadCoreTables(stmt);           //rollback dlitisation of ontology
                loadArrayTables(stmt);          //rollback dlitisation of ontology
                initializeTypes(stmt);          //initialise hierarchical tables and initial types
                computeTypes(stmt, false);      //compute types with main algorithm
                archiveTypes(stmt, false);      //archive the types as a result of main computation


                //to be deleted---------------------------------------------------------------------------------------------------------------
                cleanComputationTables(stmt);   //remove data from computation tables types and successors as well as intermidiary tables
                initializeTypes(stmt);          //initialise hierarchical tables and initial types
                computeDetTypes(stmt);          //compute types with main algorithm
                archiveDetTypes(stmt);          //archive the types as a result of main computation

                cleanComputationTables(stmt);   //remove data from computation tables types and successors as well as intermidiary tables
                initializeTypes(stmt);          //initialise hierarchical tables and initial types
                computeDetPlusTypes(stmt);      //compute types with main algorithm
                archiveDetPlusTypes(stmt);      //archive the types as a result of main computation

                //----------------------------------------------------------------------------------------------------------------------------
                /*TODO: uncomment this(DL-Litisation)
                loadCoreTables(stmt);           //load the core tables from staging tables
                loadArrayTables(stmt);          //load the list tables from core tables
                dlitisationOfOntology(stmt);    //affects only core tables (staging tables are preserved so that we can rollback this process later)
                initializeTypes(stmt);          //initialise hierarchical tables and initial types
                computeTypes(stmt, true);       //compute Dlitisation types
                archiveTypes(stmt, true);       //archive the Dlitisation types

                String sql = "SELECT temporary_stat()";
                stmt.execute(sql);
                */

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("TIME: " + (t2 - t1));

    }

    private void loadCoreTables(Statement stmt) throws SQLException {
        String sql = "SELECT load_core_tables()";
        stmt.execute(sql);
    }

    private void loadArrayTables(Statement stmt) throws SQLException {
        String sql = "SELECT load_list_tables()";
        stmt.execute(sql);
    }

    private void cleanComputationTables(Statement stmt) throws SQLException {
        String sql = "SELECT clean_computation_tables()";
        stmt.execute(sql);
    }

    private void dlitisationOfOntology(Statement stmt) throws SQLException {
        String sql = "SELECT ontology_dlitisation()";
        stmt.execute(sql);
    }

    private void initializeTypes(Statement stmt) throws SQLException {

        System.out.println("");

        String sql ="SELECT initialize_types()";

        stmt.execute(sql);
    }

    private void archiveTypes(Statement stmt,boolean Dlitisation) throws SQLException {

        if(Dlitisation){
            //store abox types that come from DLitisation algorithm
            //first clean the table from rows added from previous runs
            String sql="TRUNCATE TABLE stat_abox_types_from_dlitisation";
            stmt.execute(sql);

            sql ="INSERT INTO stat_abox_types_from_dlitisation SELECT * FROM types WHERE abox_type";
            stmt.execute(sql);
        }
        else {
            //store abox types that come from main computation algorithm
            //first clean the table from rows added from previous runs
            String sql = "TRUNCATE TABLE stat_abox_types_from_main";
            stmt.execute(sql);

            sql = "INSERT INTO stat_abox_types_from_main SELECT * FROM types WHERE abox_type";
            stmt.execute(sql);
        }
    }

    //to be deleted
    private void archiveDetTypes(Statement stmt) throws SQLException {
        //store abox types that come from deterministic consequences
        //axioms of the form A->B, and(A1,..,An)->B
        String sql="TRUNCATE TABLE stat_abox_types_from_det";
        stmt.execute(sql);

        sql ="INSERT INTO stat_abox_types_from_det SELECT * FROM types WHERE abox_type";
        stmt.execute(sql);
    }

    //to be deleted
    private void archiveDetPlusTypes(Statement stmt) throws SQLException {
        //store abox types that come from deterministic consequences
        //axioms of the form A->B, and(A1,..,An)->B
        //including consequnces from domain and range
        String sql="TRUNCATE TABLE stat_abox_types_from_det_plus";
        stmt.execute(sql);

        sql ="INSERT INTO stat_abox_types_from_det_plus SELECT * FROM types WHERE abox_type";
        stmt.execute(sql);
    }

    private void computeGuessesPerType(Statement stmt) throws SQLException{
        String procedure="compute_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        //compute the list of potential guesses for each type in the ABox
        //deduplificate the list of identical types, link the individuals with the list of unique types
        String sql="TRUNCATE TABLE guesses_per_type";
        stmt.execute(sql);

        sql = "SELECT gs_compute_guesses_per_type()";
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
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

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

    //Consider deleting it
    private void computeDetTypes(Statement stmt) throws SQLException {
        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";
        String timeStamp = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        System.out.println("=================================================================================");
        System.out.println("Compute Deterministic Types");

        //close  under simple axioms A->B
        consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, false);
        //close under axioms and(A1,..,An)->B
        consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, false);
        //close under axioms A->(U)R.B
    }

    private void computeDetPlusTypes(Statement stmt) throws SQLException {
        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";
        String timeStamp = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        System.out.println("=================================================================================");
        System.out.println("Compute Deterministic Types plus consequnces from Doman and Range");

        //close types under domain restriction
        consequences_from_axioms_with_unqualified_exist_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, false);
        //close types under range restriction
        consequences_from_axioms_with_universals_on_rhs_top(stmt, executionID, callingLevel, childTaskID, callingProcedure, false);
        //close  under simple axioms A->B
        consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, false);
        //close under axioms and(A1,..,An)->B
        consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, false);
        //close under axioms A->(U)R.B
    }

    /*this method implement the core calling mechanism for the main algorithm as well as Dlitisation process*/
    private void computeTypesWithGuessing(Statement stmt) throws SQLException {

        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";
        String timeStamp = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

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
        //generates Abox types, which come from the list of guesses per each type in table guesses_per_type
        generateRelevantAboxTypes(stmt);
        //initialize guessed ABox types
        intializeGuessedAboxTypes(stmt);

        //close types under domain restriction
        gs_consequences_from_axioms_with_unqualified_exist_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close types under range restriction
        gs_consequences_from_axioms_with_universals_on_rhs_top(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close  under simple axioms A->B
        gs_consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close under axioms and(A1,..,An)->B
        gs_consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close under axioms A->(U)R.B
        gs_consequences_from_axioms_with_universals_on_rhs(stmt, executionID, callingLevel, childTaskID, callingProcedure);
         //close under axioms (E)R.B->A
        gs_consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close under axioms A->or(B1,..,Bn)
        gs_consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close under axioms Top->or(B1,..,Bn)
        gs_consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure);

        //TODO:close under axioms (U)R.B->A
        //consequences_from_axioms_with_qualified_universals_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
        //introduce first layer of abox_successors
        gs_introduce_abox_successors(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //introduce successors whose parents are anonymous
        gs_introduce_anonumous_successors(stmt, executionID, callingLevel, childTaskID, callingProcedure);

        sql = "select insert_statistics_for_version_with_guessing();";
        stmt.execute(sql);
    }


    /*this method implement the core calling mechanism for the main algorithm as well as Dlitisation process*/
    private void computeTypes(Statement stmt, boolean DLitisation) throws SQLException {

        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";
        String timeStamp = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        //TODO: add here the execution id

        System.out.println("Execution id:" + executionID);
        System.out.println("=================================================================================");

        //close types under domain restriction
        consequences_from_axioms_with_unqualified_exist_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
        //close types under range restriction
        consequences_from_axioms_with_universals_on_rhs_top(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
        //close  under simple axioms A->B
        consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
        //close under axioms and(A1,..,An)->B
        consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
        //close under axioms A->(U)R.B
        consequences_from_axioms_with_universals_on_rhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);

        if(!DLitisation){
            //close under axioms (E)R.B->A
            consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
            //close under axioms A->or(B1,..,Bn)
            consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
            //close under axioms Top->or(B1,..,Bn)
            consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
        }

        //TODO:close under axioms (U)R.B->A
        //consequences_from_axioms_with_qualified_universals_on_lhs(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
        //introduce first layer of abox_successors
        introduce_abox_successors(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);
        //introduce successors whose parents are anonymous
        introduce_anonumous_successors(stmt, executionID, callingLevel, childTaskID, callingProcedure, DLitisation);

        if(!DLitisation) {
            sql = "select insert_statistics();";
            stmt.execute(sql);
        }
    }





    /*this method computes the consequnces that come as a result of firing simple axioms*/
    private void consequences_from_axioms_with_simple_concepts_on_bhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {

        String procedure="cons_simpl_con";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation\t Procedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \tcallingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }
        else{
            System.out.println("Time:" + timeStamp + "\t Process:main\t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \tcallingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }

        String sql =String
                .format("SELECT consequences_from_simple_concepts_on_bhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequnces that come as a result of firing axioms with conjunctions on lhs*/
    private void consequences_from_axioms_with_conjunction_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {

        String procedure="cons_conj_lhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        if(forDlitisation) {
            System.out.println("Time:"+timeStamp+"\t Process:DLitisation\t Procedure:"+procedure+"\t\t\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);
        }
        else{
            System.out.println("Time:"+timeStamp+"\t Process:main\t\tProcedure:"+procedure+"\t\t\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);
        }

        String sql =String
                .format("SELECT consequences_from_conjunction_on_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequnces that come as a result of firing axioms with disjunctions on rhs*/
    private void consequences_from_axioms_with_disjunctions_on_rhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {

        if(forDlitisation)
            System.err.println("consequences_from_axioms_with_disjunctions_on_rhs shouldn't have been called in DLitisation context");

        int level=callingLevel+1;
        String procedure="cons_disj_rhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart=false;

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation\t Procedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \tcallingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }
        else {
            System.out.println("Time:" + timeStamp + "\t Process:main\t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \tcallingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }

        String sql =String
                .format("SELECT consequences_from_disjunctions_on_rhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart){
            childTaskId=1;
            consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
        }
    }

    /*this method computes the consequnces that come as a result of firing axioms with disjunctions on rhs, whith top on lhs*/
    private void consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {

        if(forDlitisation)
            System.err.println("consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs shouldn't have been called in DLitisation context");

        int level=callingLevel+1;
        String procedure="cons_disj_rhs_top";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart=false;

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation Procedure:" + procedure + "\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }else {
            System.out.println("Time:" + timeStamp + "\t Process:main\tProcedure:" + procedure + "\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }

        String sql =String
                .format("SELECT consequences_from_disjunctions_on_rhs_top_on_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart){
            childTaskId=1;
            consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            if(!forDlitisation) {
                childTaskId++;
                consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
                childTaskId++;
                consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
                childTaskId++;
                consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            }
        }
    }

    /*this method computes the consequnces that come as a result of firing axioms with universals on rhs*/
    private void consequences_from_axioms_with_universals_on_rhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {

        int level=callingLevel+1;
        String procedure="cons_univ_on_rhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation \tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }
        else {
            System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }

        String sql =String
                .format("SELECT consequences_from_universals_on_rhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if (restart){
            childTaskId=1;
            consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
        }
    }

    /*this method computes the consequences that come from range restrictions*/
    private void consequences_from_axioms_with_universals_on_rhs_top(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {

        int level=callingLevel+1;
        String procedure="cons_univ_on_rhs_T_lhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean changed = false;

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation \tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }else {
            System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }

        String sql =String
                .format("SELECT consequences_from_universals_on_rhs_top_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method introduces the successors that come from applying rules with Existentials on rhs to abox individuals*/
    private void introduce_abox_successors(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {
        int level=callingLevel+1;

        String procedure="intro_abox_succ";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation \tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }
        else{
                System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }

        String sql =String
                .format("SELECT introduce_abox_successors( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())

        if (restart){
            childTaskId=1;
            consequences_from_axioms_with_unqualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_universals_on_rhs_top(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            if(!forDlitisation) {
                consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
                childTaskId++;
                consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
                childTaskId++;
                consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
                childTaskId++;
            }
            introduce_abox_successors(stmt, executionID, level, childTaskId, procedure, forDlitisation);
        }
    }

    /*this method introduces the successors that come from applying rules with Existentials on rhs to anonymous individuals*/
    private void introduce_anonumous_successors(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {
        int level=callingLevel+1;

        String procedure="intro_anon_succ";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation \tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }
        else {
            System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }

        String sql =String
                .format("SELECT introduce_anonumous_successors( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart){
            childTaskId=1;
            consequences_from_axioms_with_unqualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_universals_on_rhs_top(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            if(!forDlitisation) {
                consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
                childTaskId++;
                consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
                childTaskId++;
                consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
                childTaskId++;
            }
            introduce_abox_successors(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            introduce_anonumous_successors(stmt, executionID, level, childTaskId, procedure, forDlitisation);
        }
    }

    /*this method computes the consequences that come as a result of firing axioms with the existentials on the lhs*/
    private void consequences_from_axioms_with_qualified_exist_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {

        if(forDlitisation)
            System.err.println("consequences_from_axioms_with_qualified_exist_on_lhs shouldn't have been called in DLitisation context");

        int level=callingLevel+1;

        String procedure="cons_qual_exist_on_lhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation \tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }
        else{
            System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }

        String sql =String
                .format("SELECT consequences_from_qualified_exist_on_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if (restart) {
            childTaskId=1;
            consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            childTaskId++;
            if (!forDlitisation) {
                consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure, forDlitisation);
            }
        }
    }

    /*this method computes the consequences that come from domain restrictions*/
    private void consequences_from_axioms_with_unqualified_exist_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure,boolean forDlitisation) throws SQLException {
        String procedure="cons_unqual_exist_lhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        if(forDlitisation) {
            System.out.println("Time:" + timeStamp + "\t Process:DLitisation \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \tcallingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);
        }else{
            System.out.println("Time:"+timeStamp+"\t Process:main \t\tProcedure:"+procedure+"\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);
        }

        String sql =String
                .format("SELECT consequences_from_unqualified_exist_on_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequences that come as a result of firing axioms with the universals on the lhs*/
    private void consequences_from_axioms_with_qualified_universals_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure){
        //TODO:implement it on postgresQL
    }




    /*this method computes the consequnces that come as a result of firing simple axioms*/
    private void gs_consequences_from_axioms_with_simple_concepts_on_bhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        String procedure="cons_simpl_con";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

            System.out.println("Time:" + timeStamp + "\t Process:main\t\tProcedure:" + procedure + "\t\t\t\t\t\t\t\t Calling Procedure:" + callingProcedure);// + " \tcallingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT gs_consequences_from_simple_concepts_on_bhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequnces that come as a result of firing axioms with conjunctions on lhs*/
    private void gs_consequences_from_axioms_with_conjunction_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        String procedure="cons_conj_lhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

            System.out.println("Time:"+timeStamp+"\t Process:main\t\tProcedure:"+procedure+"\t\t\t\t\t\t\t\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);

        String sql =String
                .format("SELECT gs_consequences_from_conjunction_on_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequnces that come as a result of firing axioms with disjunctions on rhs*/
    private void gs_consequences_from_axioms_with_disjunctions_on_rhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;
        String procedure="cons_disj_rhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart=false;

            System.out.println("Time:" + timeStamp + "\t Process:main\t\tProcedure:" + procedure + "\t\t\t\t\t\t\t\t\t Calling Procedure:" + callingProcedure);// + " \tcallingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT gs_consequences_from_disjunctions_on_rhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart){
            childTaskId=1;
            gs_consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequnces that come as a result of firing axioms with disjunctions on rhs, whith top on lhs*/
    private void gs_consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;
        String procedure="cons_disj_rhs_top";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart=false;

        System.out.println("Time:" + timeStamp + "\t Process:main\t\tProcedure:" + procedure + "\t\t\t\t\t\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT gs_consequences_from_disjunctions_on_rhs_top_on_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart){
            childTaskId=1;
            gs_consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequnces that come as a result of firing axioms with universals on rhs*/
    private void gs_consequences_from_axioms_with_universals_on_rhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;
        String procedure="cons_univ_on_rhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t\t\t\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT gs_consequences_from_universals_on_rhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if (restart){
            childTaskId=1;
            gs_consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequences that come from range restrictions*/
    private void gs_consequences_from_axioms_with_universals_on_rhs_top(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;
        String procedure="cons_univ_on_rhs_T_lhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean changed = false;

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT gs_consequences_from_universals_on_rhs_top_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method introduces the successors that come from applying rules with Existentials on rhs to abox individuals*/
    private void gs_introduce_abox_successors(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        int level=callingLevel+1;

        String procedure="intro_abox_succ";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t\t\t\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT gs_introduce_abox_successors( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())

            if (restart){
                childTaskId=1;
                gs_consequences_from_axioms_with_unqualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure);
                childTaskId++;
                gs_consequences_from_axioms_with_universals_on_rhs_top(stmt, executionID, level, childTaskId, procedure);
                childTaskId++;
                gs_consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure);
                childTaskId++;
                gs_consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure);
                childTaskId++;
                gs_consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure);
                childTaskId++;
                gs_consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure);
                childTaskId++;
                gs_consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, level, childTaskId, procedure);
                childTaskId++;
                gs_consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(stmt, executionID, level, childTaskId, procedure);
                childTaskId++;
                gs_introduce_abox_successors(stmt, executionID, level, childTaskId, procedure);
            }
    }

    /*this method introduces the successors that come from applying rules with Existentials on rhs to anonymous individuals*/
    private void gs_introduce_anonumous_successors(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        int level=callingLevel+1;

        String procedure="intro_anon_succ";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t\t\t\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT gs_introduce_anonumous_successors( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart){
            childTaskId=1;
            gs_consequences_from_axioms_with_unqualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_universals_on_rhs_top(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_disjunctions_on_rhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_disjunctions_on_rhs_Top_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_introduce_abox_successors(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_introduce_anonumous_successors(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequences that come as a result of firing axioms with the existentials on the lhs*/
    private void gs_consequences_from_axioms_with_qualified_exist_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        int level=callingLevel+1;

        String procedure="cons_qual_exist_on_lhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        ResultSet resultSet;
        boolean restart = false;

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t\t\t\t\t\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT gs_consequences_from_qualified_exist_on_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if (restart) {
            childTaskId=1;
            gs_consequences_from_axioms_with_simple_concepts_on_bhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_conjunction_on_lhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_universals_on_rhs(stmt, executionID, level, childTaskId, procedure);
            childTaskId++;
            gs_consequences_from_axioms_with_qualified_exist_on_lhs(stmt, executionID, level, childTaskId, procedure);
        }
    }

    /*this method computes the consequences that come from domain restrictions*/
    private void gs_consequences_from_axioms_with_unqualified_exist_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="cons_unqual_exist_lhs";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:"+timeStamp+"\t Process:main \t\tProcedure:"+procedure+"\t\t\t\t\t\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);

        String sql =String
                .format("SELECT gs_consequences_from_unqualified_exist_on_lhs( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequences that come as a result of firing axioms with the universals on the lhs*/
    private void gs_consequences_from_axioms_with_qualified_universals_on_lhs(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure){
        //TODO:implement it on postgresQL
    }

}
