package org.semanticweb.clipper.alch.knots;

import org.semanticweb.clipper.alch.profile.ALCH_ClassAxiom1;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer1;
import org.semanticweb.clipper.alch.profile.ALCH_RoleAxiom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.*;

/**
 * Created by bato on 7/25/2016.
 */
public class AlchTypeComputation {

    //private ALCH_Normalizer1 normalizer;
    private AlchKnotComputation2 knotComputor;
    private ALCH_Normalizer1 normalizer;
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private OWLOntology normalizedOnt;

    ArrayList<ALCH_ClassAxiom1> deterministicAxioms;        //list of deterministic Axioms from normalizedOntology
    ArrayList<ALCH_ClassAxiom1> existentialOnLHSAxioms;     //list of Axioms that have existentials on the right from normalizedOntology
    ArrayList<ALCH_ClassAxiom1> universalOnRHSAxioms;       //list of Axioms that have universals on the right from normalizedOntology
    HashMap<String,ArrayList<String>> roleHierarchy;        //a map of roles and their consequences

    int verboseLevel=0;                                     //can be set to 0,1,2. 2 highest level of debugging level
    int rpMainAlgorithIterations=0;                         //report field for expressing the number of iterations of an algorithm

    public AlchTypeComputation() {
        knotComputor = new AlchKnotComputation2();
        normalizer = new ALCH_Normalizer1();
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
    }

    /* This method implements the initialization procedure for the input ontology
    ===============================================================================
    * normalizes the ontology
    * parses the ontology to ALCH_Class_Axioms and ALCH_Role_Axioms
    * extracts gci and ria axioms are extracted from the ontoloy's tbox
    * and types are extracted from abox assertions*/
    public void initializeTypes(OWLOntology inputOntology) {
        normalizeOntology(inputOntology);

        //parse the normalized ontology into arraylist of roleaxioms and GciAxioms
        this.parseRiaAxioms();
        this.parseGciAxioms(); //also categorizes the axioms
        this.saturateABox();
    }

    /*Is called only after ontology has been normalized.
    * Caller: Initialize Ontology*/
    private void parseRiaAxioms(){
        //for(OWLAxiom)
        Set<ALCH_RoleAxiom> riaAxioms = knotComputor.getRiaAxioms(this.normalizedOnt);
        //get the role closure
        ArrayList<ALCH_RoleAxiom> arrRiaAxioms = new ArrayList<ALCH_RoleAxiom>(riaAxioms);
        this.roleHierarchy = knotComputor.getRoleHierarchy(arrRiaAxioms,this.normalizedOnt);
    }

    /*Parses the (normalized)ontology into class axioms and also normalizes the axioms
      for each
            r->r1, and
            Some(r,A)->B
      adds
            Some(r1,A)->B to the axioms
     Is called only after ontology has been normalized.
    * Caller: InitializeTypes*/
    private void parseGciAxioms(){
        //parse the normalized onotology into classaxiom and also
        Set<ALCH_ClassAxiom1> gciAxioms = knotComputor.getGciAxioms(this.normalizedOnt);
        //categorizes the TBox GCI axiom into initialized members
        //deterministicAxioms, nondeterministicAxioms, existentialAxioms, universalAxioms
        this.categorizeAxioms(gciAxioms);
    }


    private void normalizeOntology(OWLOntology ontology){
        this.normalizedOnt = this.normalizer.normalize(ontology);
    }

    private void statsGuessesFromLHSExistentials(OWLOntology ontology){

    }


    /* This method sets the class axioms.
    As a convention, for now existential si recognised with Some(role|conceptname) and universal with All(rolename|conceptname)
    followed with the rolename up to delimiter "."
    Also "." is not allowed in class names
   */
    private void saturateABox(){
        //for(OWLAxiom ax:ontology.get)
        saturateABoxExistentialLHS(this.normalizedOnt);
        saturateABoxUniversalRHS(this.normalizedOnt);
    }

    //with this we will complete the ABox for all rules that have existentials on the left with Top
    //and universals on the right
    private void saturateABoxExistentialLHS(OWLOntology ontology){
        for(OWLNamedIndividual ind:ontology.getIndividualsInSignature()){
            for(Map.Entry<OWLObjectPropertyExpression,Set<OWLIndividual>> entry: ind.getObjectPropertyValues(ontology).entrySet()){
                //saturate for existential on the left
                for(ALCH_ClassAxiom1 ax:this.existentialOnLHSAxioms){
                    if(ax.getLeft().get(0).substring(5,ax.getLeft().get(0).indexOf("|"))==entry.getKey().toString()){

                    }
                }
            }
        }
    }

    private void saturateABoxUniversalRHS(OWLOntology ontology){
    }



    private void categorizeAxioms(Set<ALCH_ClassAxiom1> Axioms) {

        this.deterministicAxioms = new ArrayList<ALCH_ClassAxiom1>();
        this.existentialOnLHSAxioms = new ArrayList<ALCH_ClassAxiom1>();
        this.universalOnRHSAxioms = new ArrayList<ALCH_ClassAxiom1>();

        String strHelper;

        for (ALCH_ClassAxiom1 axiom : Axioms) {
            //broad categorisation of axioms in deterministic and nonderterministic
            if (axiom.getRight().size() == 1)
                this.deterministicAxioms.add(axiom);
            else{}
                //donothing;

            //it helps to have the existential axioms and unviersal axioms in one place
            strHelper = axiom.getLeft().get(0);

            if (strHelper.length() >= 3 && strHelper.substring(0, 4).equals("Some"))
                this.existentialOnLHSAxioms.add(axiom);

            //it helps to have the existential axioms and unviersal axioms in one place
            strHelper = axiom.getRight().get(0);

            if (strHelper.length() >= 3 && strHelper.substring(0, 3).equals("All"))
                this.universalOnRHSAxioms.add(axiom);
        }
    }

}
