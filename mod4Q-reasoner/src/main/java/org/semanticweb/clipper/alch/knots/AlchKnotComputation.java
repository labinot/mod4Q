package org.semanticweb.clipper.alch.knots;

        import org.semanticweb.clipper.alch.profile.ALCH_ClassAxiom;
        import org.semanticweb.clipper.alch.profile.ALCH_Normalizer;
        import org.semanticweb.clipper.alch.profile.ALCH_Profile;
        import org.semanticweb.clipper.alch.profile.ALCH_RoleAxiom;
        import org.semanticweb.owlapi.apibinding.OWLManager;
        import org.semanticweb.owlapi.model.*;

        import java.io.File;
        import java.util.*;
        import java.util.Map.Entry;

/**
 * Created by bato on 6/8/2016.
 */
public class AlchKnotComputation {
    ALCH_Normalizer normalizer;
    OWLOntologyManager manager;
    //OWLDataFactory factory;
    //ALCH_Profile profile;                                  //probably not needed

    ArrayList<Knot> knots;//this array holds the knots of previous step

    ArrayList<ALCH_ClassAxiom> deterministicAxioms;       //list of deterministic Axioms from normalizedOntology
    ArrayList<ALCH_ClassAxiom> nondeterminsticAxioms;     //list of non-deterministic Axioms from normalizedOntology
    ArrayList<ALCH_ClassAxiom> existentialAxioms;         //list of Axioms that have existentials on the right from normalizedOntology
    ArrayList<ALCH_ClassAxiom> universalAxioms;           //list of Axioms that have universals on the right from normalizedOntology
    HashMap<String,ArrayList<String>> roleHierarchy;       //a map of roles and their consequences

    boolean computed=false;                               //signifies if the computation of knots is completed
                                                          //when the set is initialized, it is set to false, when the computKnots is called it is set to true upon completion of computation

    int verboseLevel=0;                                   //can be set to 0,1,2. 2 highest level of debugging level
    int rpMainAlgorithIterations=0;                         //report field for expressing the number of iterations of an algorithm

    public AlchKnotComputation() {
        normalizer = new ALCH_Normalizer();
        manager = OWLManager.createOWLOntologyManager();
        knots=new ArrayList<Knot>();
    }

    /* This method sets the class axioms.
    As a convention, for now existential si recognised with (E) and universal with (U)
    followed with the rolename up to delimiter "."
    Also "." is not allowed in class names
     */
    private void categorizeAxioms(Set<ALCH_ClassAxiom> Axioms) {

        this.deterministicAxioms = new ArrayList<ALCH_ClassAxiom>();
        this.nondeterminsticAxioms = new ArrayList<ALCH_ClassAxiom>();
        this.existentialAxioms = new ArrayList<ALCH_ClassAxiom>();
        this.universalAxioms = new ArrayList<ALCH_ClassAxiom>();

        String strHelper;

        for (ALCH_ClassAxiom axiom : Axioms) {
            //broad categorisation of axioms in deterministic and nonderterministic
            if (axiom.getRight().size() == 1)
                this.deterministicAxioms.add(axiom);
            else if (axiom.getRight().size() > 1)
                this.nondeterminsticAxioms.add(axiom);
            else
                throw new IllegalArgumentException(axiom.toString());

            //it helps to have the existential axioms and unviersal axioms in one place
            strHelper = axiom.getRight().get(0);

            if (strHelper.length() >= 3 && strHelper.substring(0, 4).equals("Some"))
                this.existentialAxioms.add(axiom);

            if (strHelper.length() >= 3 && strHelper.substring(0, 3).equals("All"))
                this.universalAxioms.add(axiom);
        }
    }

    /* This method implements the core of the computeKnots algorithm from paper XXX
    ===============================================================================
      this methods calulates the knots of a given ontology,
    * gci and ria axioms are extracted from the ontoloy's tbox
    * and types are extracted from abox assertions*/
    public void initializeKnots(OWLOntology inputOntology) {

        OWLOntology normalizedOnt = normalizer.normalize(inputOntology);

        //parse the normalized ontology into arraylist classaxiom and arraylist roleaxiom
        //for(OWLAxiom)
        Set<ALCH_ClassAxiom> gciAxioms = getGciAxioms(normalizedOnt);
        Set<ALCH_RoleAxiom> riaAxioms = getRiaAxioms(normalizedOnt);

        Set<ArrayList<String>> types = getInitializationTypes(normalizedOnt);

        if(verboseLevel>=1) System.out.println("Number of GCI Axioms:"+gciAxioms.size());
        if(verboseLevel>=1) System.out.println("Number of RIA Axioms:" + riaAxioms.size());
        if(verboseLevel>=1) System.out.println("Number of initial types:"+types.size());

        //categorizes the TBox GCI axiom into initialized members
        //deterministicAxioms, nondeterministicAxioms, existentialAxioms, universalAxioms
        this.categorizeAxioms(gciAxioms);

        if(verboseLevel>=1) System.out.println("Number of deterministic axioms:" + deterministicAxioms.size());
        if(verboseLevel>=1) System.out.println("of which existential axioms:" + existentialAxioms.size());
        if(verboseLevel>=1) System.out.println("of which universal axioms:"+universalAxioms.size());
        if(verboseLevel>=1) System.out.println("Number of nondeterministic axioms:" + nondeterminsticAxioms.size());

        //get the role closure
        ArrayList<ALCH_RoleAxiom> arrRiaAxioms = new ArrayList<ALCH_RoleAxiom>(riaAxioms);
        this.roleHierarchy = this.getRoleHierarchy(arrRiaAxioms,normalizedOnt);

        if(verboseLevel>=2) System.out.println("\nStructure of role hierarchy");
        if(verboseLevel>=2) System.out.println("===========================");
        for(Map.Entry<String, ArrayList<String>> entry : roleHierarchy.entrySet()){
            if(verboseLevel>=2) System.out.println("Role:"+entry.getKey()+",Closure size"+entry.getValue().size()+",Roles in closure"+entry.getValue().toString());
        }

        //initialize the knot set from the given set of types i.e. a knot per type where type is the root of the knot
        for (ArrayList<String> iniTypes : types) {
            Knot k = new Knot(new Type(iniTypes));
            this.knots.add(k);
        }

        if(verboseLevel>=1) System.out.println("\nInitial number of knots:"+knots.size());
    }

    /*Closes deterministically the roots of knots, as well as introduces new knots as neccessary*/
    public void computeKnots(){
        boolean finished=true;

        rpMainAlgorithIterations++;
        if(verboseLevel>=2) System.out.println("Iteration:"+rpMainAlgorithIterations+"\n");
        if(verboseLevel>=2) System.out.println("========================================");

        int i;
        int rpDroppedKnots=0;
        for(i=0;i<this.knots.size();i++){
            if(verboseLevel>=2) System.out.println("Processing knot:"+(i+1));

            if(this.knots.get(i).isProccessForDeterministicConsequences()) {
                if(verboseLevel>=2) System.out.println("Calling Deterministic closure");
                if(verboseLevel>=2) System.out.println("*No.Concepts before: "+this.knots.get(i).root.getConcepts().size());
                detConsequncesOfRoot(this.knots.get(i));
                if(verboseLevel>=2) System.out.println("*No.Concepts after: " + this.knots.get(i).root.getConcepts().size());
                finished=false;
            }
            if(this.knots.get(i).isProccessForNonDeterministicConsequences()) {
                if(verboseLevel>=2) System.out.println("Calling NonDeterministic closure");
                nondetConsequencesOfRoot(this.knots.get(i));
                finished=false;
            }
            if(this.knots.get(i).isProccessSuccessors()) {
                if(verboseLevel>=2) System.out.println("Calling Introduce Successors");
                if(verboseLevel>=2) System.out.println("*Children before the call: " + this.knots.get(i).successors.size());
                introduceKnotSuccessors(this.knots.get(i));
                if(verboseLevel>=2) System.out.println("*Children after the call: " + this.knots.get(i).successors.size());
                finished=false;
            }
            if(verboseLevel>=2) System.out.println("Knot dropped: "+ this.knots.get(i).getDroped());
            if(verboseLevel>=2) System.out.println("Knot array size: "+ this.knots.size());
            if(verboseLevel>=2) System.out.println("Number of Knots:"+(this.knots.size()-rpDroppedKnots));
            if(verboseLevel>=2) System.out.println("-------------------------\n");
            if(this.knots.get(i).getDroped()) rpDroppedKnots++;
        }
        if(verboseLevel>=2) System.out.println("Iteration:"+rpMainAlgorithIterations+"\n");
        if(verboseLevel>=2) System.out.println("Iteration:"+rpMainAlgorithIterations+"\n");
        if(verboseLevel>=2) System.out.println("Knot array size for iteration:"+rpMainAlgorithIterations+" is "+this.knots.size());
        if(verboseLevel>=2) System.out.println("Number of Knots for iteration:"+rpMainAlgorithIterations+" is "+(this.knots.size()-rpDroppedKnots));
        if(verboseLevel>=2) System.out.println("========================================");

        if(!finished)
            computeKnots();
        else
        if(verboseLevel>=0) System.out.println("Number of Knots:"+(this.knots.size()-rpDroppedKnots)+"\n\n");

    }

    /*closes deterministically the roots of existing knots*/
    public void detConsequncesOfRoot(){
        for(int i=0;i<this.knots.size();i++){
            if(this.knots.get(i).isProccessForDeterministicConsequences())
                detConsequncesOfRoot(this.knots.get(i));
        }
    }

    /*introduces nondeterministically many knots as per nondeterministic axioms that fire*/
    public void nondetConsequencesOfRoot(){
        for(int i=0;i<this.knots.size();i++){
            if(this.knots.get(i).isProccessForNonDeterministicConsequences())
                nondetConsequencesOfRoot(this.knots.get(i));
        }
    }

    /*introduces the successors knots and adds new knots ass neccessary*/
    public void introduceKnotSuccessors(){
        for(int i=0;i<this.knots.size();i++){
            if(this.knots.get(i).isProccessSuccessors())
                introduceKnotSuccessors(this.knots.get(i));
        }
    }

    /*completes the root of an individual knot*/
    public void detConsequncesOfRoot(Knot knot){
        if(knot.getDroped()) {return;}
        ArrayList<String> arrHelper; //container to hold the values of an array (in order to pass values by value)
        //deterministic completion of the root
        for (ALCH_ClassAxiom ax : deterministicAxioms) {
            arrHelper = (ArrayList)knot.root.getConcepts().clone();//get a copy of the array
            arrHelper.retainAll(ax.getLeft());                      //intersection
            //in order to decide if elements in the LHS of ax is contained in the root of the knot
            // ask if the number of elements in the intersection is the same as in LHS of axiom
            if (arrHelper.size() == ax.getLeft().size()) {
                if(!knot.root.getConcepts().contains(ax.getRight().get(0))) { //if the element isn't allready in the set of concepts then add it
                    //knot.root.getDerConcepts().add(ax.getRight().get(0));
                    knot.root.getConcepts().add(ax.getRight().get(0));
                }
            }
        }
        //no need to process this knot for deterministic consequences again
        knot.setProccessForDeterministicConsequences(false);
        knot.setProccessForNonDeterministicConsequences(true);  //check if there are more nondeterministic consequences to be added from new deterministic consequences
        knot.setProccessSuccessors(true);                       //check if there are more children to be added from new deterministic consequnces
    }

    public void nondetConsequencesOfRoot(Knot knot){
        if(knot.getDroped()) {return;}

        int rpKnotsCreated=0;//only for reporting

        ArrayList<String> arrHelper; //container to hold the values of an array (in order to pass values by value)
        ArrayList<String> arrHelper2;
        //non-deterministic completion of the root
        for (ALCH_ClassAxiom ax : nondeterminsticAxioms) {

            arrHelper = (ArrayList)knot.root.getConcepts().clone();     //get a copy of the array
            arrHelper.retainAll(ax.getLeft());                          //intersection

            arrHelper2 = (ArrayList)knot.root.getConcepts().clone();     //get a copy of the array
            arrHelper2.retainAll(ax.getRight());                          //intersection


            //in order to decide if elements in the LHS of ax is contained in the root of the knot
            // ask if the number of elements in the intersection is the same as in LHS of axiom
            // and there are no elements from RHS of axiom allready in the root
            if (arrHelper.size() == ax.getLeft().size() && arrHelper2.size()==0) {
                //get the cloned
                //for each element e in RHS of ax
                // create a new knot containing the concepts in root of knot + e
                for (String cl : ax.getRight()) {
                    //create the type of the root by passing a copy of concepts found in the inicialization concepts of the root of the knot
                    Type t = new Type((ArrayList) knot.root.getIniConcepts().clone());
                    //t.setIniConcepts((ArrayList) knot.root.getIniConcepts().clone());
                    //t.setConcepts((ArrayList) knot.root.getConcepts().clone());

                    //if the consequence is not allready in the set of initializes concepts then add it
                    if(!t.getIniConcepts().contains(cl)){
                        t.getIniConcepts().add(cl);
                        t.getConcepts().add(cl);
                    }

                    if(!knotInSet(new Knot(t))) {//if a knot with this inicialisation is not allready found in the set then add it
                        this.knots.add(new Knot(t));
                        rpKnotsCreated++;
                    }
                }
                knot.setDroped(true); //mark this knot for removing, since it does not contain any of the nondeterministic consequences (
                knot.setProccessForDeterministicConsequences(false);
                knot.setProccessForNonDeterministicConsequences(false);
                knot.setProccessSuccessors(false);
            }
        }
        if(verboseLevel>=2) System.out.println("Knots created: " + rpKnotsCreated);
        knot.setProccessForNonDeterministicConsequences(false);
    }

    public void introduceKnotSuccessors(Knot knot){
        if(knot.getDroped()) return;

        ArrayList<String> arrHelper; //container to hold the values of an array (in order to pass values by value)
        //add successors one by one (for each existential axiom)
        for (String concept:knot.root.getConcepts()){
                //ALCH_ClassAxiom ax : existentialAxioms) {
            if(concept.indexOf("Some(")==0) {
                //create the type for the successor
                Type t = new Type();

                //get the (range)concept of the existential concept, which is found immediately after |, up until the character the )
                String strRange = concept.substring(concept.indexOf("|") + 1, concept.length() - 1);

                //get the role of the existential concept, which is found immediately after Some(, up until the character the |
                String strRole = concept.substring(5, concept.indexOf("|"));

                //add the range of the existential concept to initial type of the successor
                t.getIniConcepts().add(strRange);
                t.getConcepts().add(strRange);

                //get an arraylist of the role closure of the role found in the existential axiom
                ArrayList<String> successorRoleClosure = (ArrayList) this.roleHierarchy.get(strRole).clone();
                successorRoleClosure.add(strRole);

                //get the list of universal concepts from root
                for (String str:knot.root.getConcepts()) {
                    if (str.indexOf("All(") == 0//starts with All( and check if the role of universal concept is found in the roleclosure of successor
                            && successorRoleClosure.contains(str.substring(4, str.indexOf("|")))
                            ) {   //if the range of the universal concepts is not allready in the initial concepts then add it
                        if (!t.getIniConcepts().contains(str.substring(str.indexOf("|")+1, str.length() - 1))) {
                            t.getIniConcepts().add(str.substring(str.indexOf("|")+1, str.length() - 1));
                            t.getConcepts().add(str.substring(str.indexOf("|")+1, str.length() - 1));
                        }
                    }
                }

                Successor s = new Successor(t, successorRoleClosure);
                //if no such successor exists then add it to the successor list
                if (!successorExists(s, knot)) {
                    knot.successors.add(s);
                }

                //if no knot with the same root (same concept initialization) as the successor exists in knot-set then
                //create a new knot with the same type as
                Knot k = new Knot(t);
                if (!knotInSet(k)) {
                    knots.add(k);
                }
            }
        }
        //no need to process this knot for children again
        knot.setProccessSuccessors(false);
    }




    /*  Returns true if a knot with identic initialisation concepts already exists
        in the set of knots, otherwise returns false
     */
    public boolean knotInSet(Knot paramKnot) {
        boolean found=false;
        //get the sorted inicialization concept list of passed knot
        ArrayList<String> knotRoot = (ArrayList)paramKnot.root.getIniConcepts().clone();
        Collections.sort(knotRoot);
        //iterate through each knot in the set
        for(Knot k:this.knots){
            if(k.getDroped()==false) {
                //get the sorted initialization concept list of current knot in the loop
                ArrayList<String> kRoot = (ArrayList) k.root.getIniConcepts().clone();
                Collections.sort(kRoot);
                //compare if the lists match
                if (kRoot.toString().equals(knotRoot.toString())) {
                    found = true;
                }
            }
        }
        return found;
    }

    /* Returns true if successor is allready present in the set of knots*/
    public boolean successorExists(Successor suc,Knot knot) {
        boolean found=false;
        for(Successor s:knot.successors){
            if(s.getTypeSet().toString()==suc.getTypeSet().toString()){
                found=true;
            }
        }
        return found;
    }

    /*Returns a HashMap where each key represents a role included at least once in the lhs of some RIA
    * and, the value as an arraylist of strings represnting each role that is found in rhs of some axiom
    * which's lhs equals the key of map*/
    public HashMap<String,ArrayList<String>> getRoleHierarchy(ArrayList<ALCH_RoleAxiom> rboxes,OWLOntology ontology){

        //create a map of Key -> Value, where Key represents the a role found in LHS of rbox
        //where as the Values represents all the roles that where found on RHS of that Key in rboxes
        HashMap<String,ArrayList<String>> roleHierarchy = new HashMap<String,ArrayList<String>>();

        //get the roles found in an ontology
        Set<String> roles = getProperties(ontology);

        /*Create map key from roles found in the onotlogy*/
        for(String role:roles){
            if (!roleHierarchy.containsKey(role)) {
                roleHierarchy.put(role, new ArrayList<String>());   //create new element in the map
            }
        }

        //foreach axiom in rbox, add all nonexistsing keys to rolehierarch (LHS of axiom)
        //and add the RHS to values (consequences) of that role
        for(ALCH_RoleAxiom ax : rboxes){
            if (!roleHierarchy.containsKey(ax.getLeft())) {
                roleHierarchy.put(ax.getLeft(), new ArrayList<String>());   //create new element in the map
                roleHierarchy.get(ax.getLeft()).add(ax.getRight());         //add the consequence of role found in RHS
            }
            else{   //we add the RHS to values of that axiom (assuming no duplicate axioms (A->B) and (A->B) in rboxes,
                // this doesn't produce dublicates in values
                roleHierarchy.get(ax.getLeft()).add(ax.getRight());         //simply add the consequence of role found in RHS
            }
        }

        //foreach element in the map, add the consequences by looking up the consequences that follow for each value in the rhs of the current element
        for(Map.Entry<String, ArrayList<String>> entry : roleHierarchy.entrySet()){
            //iterate throught the valuelist in the array found in value of the element
            for(int i=0; i<entry.getValue().size();i++){
                //checks if the current element on the value list is found in the keys list
                if(roleHierarchy.containsKey(entry.getValue().get(i))) {

                    //add in an array all the consequences found under the key that matched the element on the value list
                    ArrayList<String> newconsequences = (ArrayList)roleHierarchy.get(entry.getValue().get(i)).clone();

                    //remove all the elements from this array that are found in the valuel list
                    newconsequences.removeAll(entry.getValue());
                    newconsequences.remove(entry.getKey());

                    //now add the remaining elements to the value list
                    entry.getValue().addAll(newconsequences);
                }
            }
        }
        return roleHierarchy;
    }

    /*Returns GCI axioms from a normalized as a Set
    * Important: input normalized ALCH ontology*/
    public Set<ALCH_ClassAxiom> getGciAxioms(OWLOntology ontology){

        Set<ALCH_ClassAxiom> gciAxioms= new HashSet<ALCH_ClassAxiom>();

        for(OWLAxiom ax: ontology.getTBoxAxioms(false)){
            if(ax.getAxiomType()== AxiomType.SUBCLASS_OF){
                OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom)ax;
                gciAxioms.add(new ALCH_ClassAxiom(axiom));
            }
        }
        return gciAxioms;
    }

    /*Returns RIA axioms from a normalized as a Set
    * Important: input normalized ALCH ontology*/
    public Set<ALCH_RoleAxiom> getRiaAxioms(OWLOntology ontology){

        Set<ALCH_RoleAxiom> riaAxioms= new HashSet<ALCH_RoleAxiom>();

        for(OWLAxiom ax: ontology.getRBoxAxioms(true)){
            if(ax.getAxiomType()== AxiomType.SUB_OBJECT_PROPERTY){
                OWLSubObjectPropertyOfAxiom axiom = (OWLSubObjectPropertyOfAxiom)ax;
                riaAxioms.add(new ALCH_RoleAxiom(axiom));
            }
        }
        return riaAxioms;
    }

    /*Returns properties found in an ontology*/
    public Set<String> getProperties(OWLOntology ontology){

        Set<String> roles= new HashSet<String>();

        for(OWLAxiom ax: ontology.getRBoxAxioms(true)){
            if(ax.getAxiomType()== AxiomType.SUB_OBJECT_PROPERTY){
                OWLSubObjectPropertyOfAxiom axiom = (OWLSubObjectPropertyOfAxiom)ax;
                roles.add(axiom.getSubProperty().getNamedProperty().toString());
                roles.add(axiom.getSuperProperty().getNamedProperty().toString());
            }
        }
        for(OWLObjectProperty property:ontology.getObjectPropertiesInSignature(true)){
            roles.add(property.getNamedProperty().toString());
        }
        return roles;
    }

    /*Returns initial types from a normalized ontology as a Set of arraylists of concepts(Strings)
    * Important: input normalized ALCH ontology*/
    public Set<ArrayList<String>> getInitializationTypes(OWLOntology ontology){
        HashMap<String,HashSet<String>> individualConceptsMAP = new HashMap<String,HashSet<String>>();

        for (OWLAxiom a : ontology.getABoxAxioms(false)) {
            if (a.getAxiomType()==AxiomType.CLASS_ASSERTION) {
                OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                String individual;
                HashSet<String> concepts = new HashSet<String>();

                individual = ax.getIndividual().toString();
                if(ax.getClassExpression().getClassExpressionType()==ClassExpressionType.OWL_CLASS)
                    concepts.add(ax.getClassExpression().toString());
                else
                    System.err.print("Raised by AlchKnotComputation.getInitializationTypes ->Complex concepts in class assertion:"+ax.toString());

                if(individualConceptsMAP.containsKey(ax.getIndividual().toString())){
                    individualConceptsMAP.get(individual).add(ax.getClassExpression().toString());
                }
                else {
                    individualConceptsMAP.put(individual,concepts);
                }
            }
        }

        //Hash Set that will hold the types
        Set<ArrayList<String>> types = new HashSet<ArrayList<String>>();

        for (HashSet<String> concepts : individualConceptsMAP.values()) {
            //sort the elements on the list
            //helper array to facilitate sorting
            ArrayList<String> helperArr = new ArrayList(concepts);
            Collections.sort(helperArr);
            //add to the types (duplicates will be ignored this way)
            types.add(helperArr);
        }

        return types;
    }

}