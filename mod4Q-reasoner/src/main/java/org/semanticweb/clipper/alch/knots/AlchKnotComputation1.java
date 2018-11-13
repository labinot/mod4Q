package org.semanticweb.clipper.alch.knots;

import org.semanticweb.clipper.alch.profile.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by bato on 6/8/2016.
 */
public class AlchKnotComputation1 {
    ALCH_Normalizer normalizer;
    OWLOntologyManager manager;
    //OWLDataFactory factory;
    //ALCH_Profile profile;                                  //probably not needed

    //ArrayList<Knot> knots;//this array holds the knots of previous step
    ArrayList<Knot> knots;                                  //the array that will be iterated in the algorithm and updated at the end of each iteration
                                                            //,at the end of the process will contain the list of knots
    ArrayList<Knot> nextKnots;                              //serves as temporary list which is reinitailized in each iteration of the algorithm and that
                                                            //holds the elements of the array knots (only those not droped during the iteration)+
                                                            //new elements created from nendeterminism and introducing successors
    HashMap<String,ArrayList<String>> knotKeys;                        //holds the initialization concepts of every knot that was ever created

    ArrayList<ALCH_ClassAxiom> deterministicAxioms;         //list of deterministic Axioms from normalizedOntology
    ArrayList<ALCH_ClassAxiom> nondeterminsticAxioms;       //list of non-deterministic Axioms from normalizedOntology
    ArrayList<ALCH_ClassAxiom> existentialAxioms;           //list of Axioms that have existentials on the right from normalizedOntology
    ArrayList<ALCH_ClassAxiom> universalAxioms;             //list of Axioms that have universals on the right from normalizedOntology
    HashMap<String,ArrayList<String>> roleHierarchy;        //a map of roles and their consequences

    boolean computed=false;                                 //signifies if the computation of knots is completed
    //when the set is initialized, it is set to false, when the computKnots is called it is set to true upon completion of computation

    int verboseLevel=0;                                     //can be set to 0,1,2. 2 highest level of debugging level
    int rpMainAlgorithIterations=0;                         //report field for expressing the number of iterations of an algorithm

    public AlchKnotComputation1() {
        normalizer = new ALCH_Normalizer();
        manager = OWLManager.createOWLOntologyManager();
        knots=new ArrayList<Knot>();
        nextKnots=new ArrayList<>();
        knotKeys=new HashMap<String,ArrayList<String>>();
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

        ALCH_Normalizer1 normalizer1 = new ALCH_Normalizer1();
        //temporarily set to Normalizer1
        OWLOntology normalizedOnt = normalizer1.normalize(inputOntology);

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
            Collections.sort(iniTypes);
            this.knots.add(k);                          //add the initial knots
            this.knotKeys.put(iniTypes.toString(), new ArrayList<String>());  //add the initialization key of the knot, to make sure later that no new knot with the same key will be added
                                                        // to the list of knots
        }

        if(verboseLevel>=1) System.out.println("\nInitial number of knots:"+knots.size());
    }

    /*Closes deterministically the roots of knots, as well as introduces new knots as neccessary*/
    public void computeKnots(){
        boolean finished=true;

        rpMainAlgorithIterations++;
        if(verboseLevel>=2) System.out.println("Iteration:"+rpMainAlgorithIterations);
        if(verboseLevel>=2) System.out.println("========================================");

        int i;
        int rpDroppedKnots=0;

        //main loop of the algorithm
        Mainloop:
        for(i=0;i<this.knots.size();i++){
            if(verboseLevel>=3) System.out.println("Processing knot:"+(i+1));

            if(this.knots.get(i).isProccessForDeterministicConsequences()) {
                if(verboseLevel>=3) System.out.println("Calling Deterministic closure");
                if(verboseLevel>=3) System.out.println("*No.Concepts before: "+this.knots.get(i).root.getConcepts().size());
                detConsequncesOfRoot(this.knots.get(i));
                if(verboseLevel>=3) System.out.println("*No.Concepts after: " + this.knots.get(i).root.getConcepts().size());
                finished=false;
            }
            if(this.knots.get(i).isProccessForNonDeterministicConsequences()) {
                if(verboseLevel>=3) System.out.println("Calling NonDeterministic closure");
                nondetConsequencesOfRoot(this.knots.get(i));
                finished=false;
            }
            if(this.knots.get(i).isProccessSuccessors()) {
                if(verboseLevel>=3) System.out.println("Calling Introduce Successors");
                if(verboseLevel>=3) System.out.println("*Children before the call: " + this.knots.get(i).successors.size());
                introduceKnotSuccessors(this.knots.get(i));
                if(verboseLevel>=3) System.out.println("*Children after the call: " + this.knots.get(i).successors.size());
                finished=false;
            }
            if(verboseLevel>=3) System.out.println("Knots for the next loop:"+ this.nextKnots.size());
            if(verboseLevel>=3) System.out.println("Knots left in this loop:"+(this.knots.size()-i));
            if(verboseLevel>=3) System.out.println("Knots dropped so far:"+rpDroppedKnots);
            if(verboseLevel>=3) System.out.println("-------------------------");

            //if the knot is not droped after processing, add it for the next iteration of main loop
            if(!this.knots.get(i).getDroped()) {
                this.nextKnots.add(this.knots.get(i));
            }else{
                rpDroppedKnots++;//only for reporting purposes
            }
        }
        if(verboseLevel>=2) System.out.println("Knot array size after iteration "+rpMainAlgorithIterations+":"+this.nextKnots.size());
        if(verboseLevel>=2) System.out.println("Dropped knots for iteration "+rpMainAlgorithIterations+":"+rpDroppedKnots);
        if(verboseLevel>=2) System.out.println("Map size after iteration "+rpMainAlgorithIterations+":"+this.knotKeys.size());
        if(verboseLevel>=2) System.out.println("========================================");

        //rinitialize the knots for the next iteration, set the nextKnot to newly created fresh array
        this.knots=(ArrayList)this.nextKnots.clone();
        this.nextKnots=new ArrayList<Knot>();

        if(!finished)
            computeKnots();
        else
        if(verboseLevel>=0) System.out.println("Number of Knots:"+(this.knots.size()-rpDroppedKnots)+"\n\n");

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

                    Collections.sort(t.getIniConcepts());

                    if(this.knotKeys.get(t.getIniConcepts().toString())==null){//if a knot with this inicialisation is not allready found in the set then add it
                        this.nextKnots.add(new Knot(t));                        //add the new knot to the set of knots to be considered in the next iteration
                        this.knotKeys.put(t.getIniConcepts().toString(), new ArrayList<String>());    //add the key of the newely created knot
                        rpKnotsCreated++;
                    }
                }
                knot.setDroped(true); //mark this knot for removing, since it does not contain any of the nondeterministic consequences (
                knot.setProccessForDeterministicConsequences(false);
                knot.setProccessForNonDeterministicConsequences(false);
                knot.setProccessSuccessors(false);
            }
        }
        if(verboseLevel>=3) System.out.println("Knots created: " + rpKnotsCreated);
        knot.setProccessForNonDeterministicConsequences(false);
        //if the knot is not droped, add it for the next iteration
    }

    /*add the successor to the knot set and additionaly creates a successor knot for the successr
      if a knot with identical root as the successor does not allready exist*/
    public void introduceKnotSuccessors(Knot knot){
        if(knot.getDroped()) return;

        int rpChildrenCreated=0;    //reporting variable, holds the number of created children
        int rpKnotsCreated=0;    //reporting variable, holds the number of created children

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
                    rpChildrenCreated++;
                }

                //if no knot with the same root (same concept initialization) as the successor exists in knot-set then
                //create a new knot with the same type as
                Collections.sort(t.getIniConcepts());

                if(this.knotKeys.get(t.getIniConcepts().toString())==null){//if a knot with this inicialisation is not allready found in the set then add it
                    this.nextKnots.add(new Knot(t));                        //add the new knot to the set of knots to be considered in the next iteration
                    this.knotKeys.put(t.getIniConcepts().toString(), new ArrayList<String>());    //add the key of the newely created knot
                    rpKnotsCreated++;
                }
            }
        }

        if(verboseLevel==3) System.out.println("No of created children:"+rpChildrenCreated);
        if(verboseLevel==3) System.out.println("No of created knots:"+rpKnotsCreated);

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

                    //remove all the elements from this array that are found in the value list
                    newconsequences.removeAll(entry.getValue());
                    newconsequences.remove(entry.getKey());

                    //now add the remaining elements to the value list
                    entry.getValue().addAll(newconsequences);
                }
            }
            //finalize closure by adding the role itself (key) to it, and sort the closure list
            entry.getValue().add(entry.getKey());
            Collections.sort(entry.getValue());
        }
        return roleHierarchy;
    }

    /*Returns GCI axioms from a normalized as a Set
    * Important: input normalized ALCH ontology*/
    public Set<ALCH_ClassAxiom> getGciAxioms(OWLOntology ontology){

        Set<ALCH_ClassAxiom> gciAxioms= new HashSet<ALCH_ClassAxiom>();

        int i=0;

        for(OWLAxiom ax: ontology.getTBoxAxioms(false)){
            if(ax.getAxiomType()== AxiomType.SUBCLASS_OF){
                OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom)ax;
                gciAxioms.add(new ALCH_ClassAxiom(axiom));
                i++;
            }
        }
        System.out.println("GCI Loop executed :"+i+" times.");

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