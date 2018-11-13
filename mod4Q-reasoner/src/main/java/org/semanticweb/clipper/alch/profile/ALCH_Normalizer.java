/**
 * Created by bato on 5/8/2016.
 */
package org.semanticweb.clipper.alch.profile;

    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.Iterator;
    import java.util.Set;

    import org.semanticweb.owlapi.model.*;
    import org.semanticweb.owlapi.util.OWLObjectPropertyManager;

public class ALCH_Normalizer implements OWLAxiomVisitorEx<Object> {

    OWLOntology normalizedOnt;

    int freshClassCounter = 0;

    OWLOntologyManager manager;

    private OWLDataFactory factory;

    ALCH_Profile profile;

    private ALCH_Sub_ClassExpressionChecker sub;

    public ALCH_Normalizer() {
    }

    public OWLOntology normalize(OWLOntology ontology) {
        this.profile = new ALCH_Profile();
        this.manager = ontology.getOWLOntologyManager();
        this.factory = manager.getOWLDataFactory();
        profile.setPropertyManager(new OWLObjectPropertyManager(manager, ontology));

        //run firs normalization procedure
        ontology=this.normalizePhaze1(ontology);

        //run second normalization procedure
        ontology=this.normalizePhaze2(ontology);

        //run third normalization procedure
        ontology=this.normalizePhaze3(ontology);

        return ontology;
    }


    public OWLOntology normalizePhaze1(OWLOntology ontology) {
        this.profile = new ALCH_Profile();
        this.manager = ontology.getOWLOntologyManager();
        this.factory = manager.getOWLDataFactory();
        profile.setPropertyManager(new OWLObjectPropertyManager(manager, ontology));

        sub = profile.getSubClassExpressionChecker();

        try {
            normalizedOnt = manager.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        for (OWLAxiom axiom : ontology.getAxioms()) {
            axiom.getNNF().accept(this);
        }

        return normalizedOnt;
    }

    /*  PHAZE 3 from mantas thesis
        in case the normalized axiom from phaze 1 and 2 of the thesis has
        and existential or universal on the left than we rewirite them,
        otherwise we simply add the axioms as they are to the ontology */
    public OWLOntology normalizePhaze2(OWLOntology ontology) {
        this.profile = new ALCH_Profile();
        this.manager = ontology.getOWLOntologyManager();
        this.factory = manager.getOWLDataFactory();
        profile.setPropertyManager(new OWLObjectPropertyManager(manager, ontology));

        sub = profile.getSubClassExpressionChecker();

        try {
            normalizedOnt = manager.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        for (OWLAxiom axiom : ontology.getAxioms()) {
            //Some(r,B)->D
            if(axiom.getAxiomType()== AxiomType.SUBCLASS_OF){
                OWLSubClassOfAxiom ax =(OWLSubClassOfAxiom)axiom;
                //OWLClassExpression subClass = axiom.getSubClass();
                OWLClassExpression subClass = ax.getSubClass().getNNF();
                //OWLClassExpression superClass = axiom.getSuperClass();
                OWLClassExpression superClass = ax.getSuperClass().getNNF();
                if(subClass.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                    OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom)subClass;
                    OWLClass fresh1=getFreshForAllClass(some.getProperty().toString(),"(neg)"+some.getFiller().toString());
                    OWLClass fresh2=getFreshClass();
                    //Top-> or(Fresh1,D)
                    manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(
                                                                                factory.getOWLThing(),
                                                                                factory.getOWLObjectUnionOf(fresh1, superClass)));
                    //Fresh1-> All(r,Fresh2)
                    manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(
                                                                                fresh1,
                                                                                factory.getOWLObjectAllValuesFrom(some.getProperty(),fresh2)));
                    //and(Fresh2,B)-> Bottom
                    manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(
                                                                                factory.getOWLObjectIntersectionOf(fresh2,some.getFiller()),
                                                                                factory.getOWLNothing()));
                }else if(subClass.getClassExpressionType()==ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                    OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom)subClass;
                    OWLClass fresh1=getFreshSomeClass(all.getProperty().toString(), "(neg)" + all.getFiller().toString());
                    OWLClass fresh2=getFreshClass();
                    //Top-> or(Fresh1,D)
                    manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(
                                                                                factory.getOWLThing(),
                                                                                factory.getOWLObjectUnionOf(fresh1, superClass)));
                    //Fresh1-> Some(r,Fresh2)
                    manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(
                                                                                fresh1,
                                                                                factory.getOWLObjectSomeValuesFrom(all.getProperty(),fresh2)));
                    //and(Fresh2,B)-> Bottom
                    manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(
                                                                                factory.getOWLObjectIntersectionOf(fresh2,all.getFiller()),
                                                                                factory.getOWLNothing()));
                }else {//else add the axiom as is
                    manager.addAxiom(normalizedOnt,axiom);
                }
            }else{
                manager.addAxiom(normalizedOnt,axiom);
            }
        }

        return normalizedOnt;
    }

    /*  PHAZE 4 from Mantas thesis
        drop Top in conjunctions on the left
        drop Bottom in disjuncions on the right
        drop axioms with Top in the disjunctions on the right
        drop axioms with Bottom in the conjunctions on the left
        */
    public OWLOntology normalizePhaze3(OWLOntology ontology) {
        this.profile = new ALCH_Profile();
        this.manager = ontology.getOWLOntologyManager();
        this.factory = manager.getOWLDataFactory();
        profile.setPropertyManager(new OWLObjectPropertyManager(manager, ontology));

        sub = profile.getSubClassExpressionChecker();

        try {
            normalizedOnt = manager.createOntology();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        axiomsLoop:
        for (OWLAxiom axiom : ontology.getAxioms()) {
            //Some(r,B)->D
            if(axiom.getAxiomType()== AxiomType.SUBCLASS_OF){
                OWLSubClassOfAxiom ax =(OWLSubClassOfAxiom)axiom;
                //OWLClassExpression subClass = axiom.getSubClass();
                OWLClassExpression subClass = ax.getSubClass().getNNF();
                //OWLClassExpression superClass = axiom.getSuperClass();
                OWLClassExpression superClass = ax.getSuperClass().getNNF();
                if(subClass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF){
                    OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) subClass;
                    Set<OWLClassExpression> operands = and.getOperands();
                    Set<OWLClassExpression> newop = new HashSet<OWLClassExpression>();
                    for(OWLClassExpression cnj:operands) {
                        //in case we find a conjunct as Bottom then we jump to next axiom (current axiom is thus dropped as trivially satisfiable)
                        //in case we find a conjunct as Top we drop tha conjunct
                        if (cnj.isTopEntity()) {
                            continue;//continue to the next operand (dont add this one to the list of conjuncts - hence remove it)
                        } else if (cnj.isBottomEntity()) {
                            continue axiomsLoop;//continue to the next axiom, (don't add this axiom since it's unsat, hence - remove it)
                        } else {
                            newop.add(cnj);
                        }
                    }
                    //we check the number of conjuncts left before adding the axiom to the normalized ontology
                    if(newop.size()==1){
                        ArrayList<OWLClassExpression> arrNewop = new ArrayList<OWLClassExpression>();
                        arrNewop.addAll(newop);
                        manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(arrNewop.get(0),superClass));
                    }else if(newop.size()>0){
                        manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(newop),superClass));
                    }
                    else{//ignore this axiom all it's conjuncts in the left have been dropped}
                    //add the code to add the axiom with the list of new conjuncts here (newops)
                    }

                }else if(superClass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {
                    OWLObjectUnionOf or = (OWLObjectUnionOf) superClass;
                    Set<OWLClassExpression> operands = or.getOperands();
                    Set<OWLClassExpression> newop = new HashSet<OWLClassExpression>();
                    for (OWLClassExpression cnj : operands) {
                        //in case we find a disjunct as Bottom then we remove it from the set of disjuncts
                        //in case we find a disjunct as Top the we jump to the next axiom ((current axiom is thus dropped as trivially satisfiable)
                        if (cnj.isTopEntity()) {
                            continue axiomsLoop;
                        } else if (cnj.isBottomEntity()) {
                            continue;
                        } else {
                            newop.add(cnj);
                        }
                    }
                    //we check the number of disjuncts left before adding the axiom to the normalized ontology
                    if(newop.size()==1){
                        ArrayList<OWLClassExpression> arrNewop = new ArrayList<OWLClassExpression>();
                        arrNewop.addAll(newop);
                        manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(subClass,arrNewop.get(0)));
                    }else if(newop.size()>0){
                        manager.addAxiom(normalizedOnt,factory.getOWLSubClassOfAxiom(subClass,factory.getOWLObjectUnionOf(newop)));
                    }
                    else{//ignore this axiom all it's conjuncts in the left have been dropped}
                        //add the code to add the axiom with the list of new conjuncts here (newops)
                    }
                }else {//else add the axiom as is
                    manager.addAxiom(normalizedOnt,axiom);
                }
            }else{
                manager.addAxiom(normalizedOnt,axiom);
            }
        }

        return normalizedOnt;
    }


    /*OK*/
    @Override
    public Object visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.semanticweb.owlapi.model.OWLAxiomVisitorEx#visit(org.semanticweb.
     * owlapi.model.OWLSubClassOfAxiom)
     */
    @Override
    public Object visit(OWLSubClassOfAxiom axiom) {

        //OWLClassExpression subClass = axiom.getSubClass();
        OWLClassExpression subClass = axiom.getSubClass().getNNF();
        //OWLClassExpression superClass = axiom.getSuperClass();
        OWLClassExpression superClass = axiom.getSuperClass().getNNF();

        //LABI-TODO
        // here I need to implement the code that checks the subexpressions
        //if they are in ALCH profile
        if (!((axiom.getSubClass().accept(sub) && axiom.getSuperClass().accept(sub)))) {
            //System.err.println("Warning: " + axiom + "is not in ALCH fragment");
        }

        // OK: simple case: A subclass C
        else if ((subClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS)
                && (superClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS)) {
            if (!subClass.isOWLNothing() && !superClass.isOWLThing()) { //ignore trivial axioms Bottom -> A, A->Top
                manager.addAxiom(normalizedOnt, axiom);
            }
        }
        // OK: A' subclass C'
        else if ((subClass.getClassExpressionType() != ClassExpressionType.OWL_CLASS)
                && (superClass.getClassExpressionType() != ClassExpressionType.OWL_CLASS)) {
            if(subClass.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){
                OWLObjectUnionOf union = (OWLObjectUnionOf) subClass;
                Set<OWLClassExpression> operands = union.getOperands();
                for (OWLClassExpression op : operands) {
                        factory.getOWLSubClassOfAxiom(op,superClass).accept(this);
                }
            }else if(superClass.getClassExpressionType()==ClassExpressionType.OBJECT_INTERSECTION_OF){
                OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) superClass;
                Set<OWLClassExpression> operands = and.getOperands();
                for (OWLClassExpression op : operands) {
                    factory.getOWLSubClassOfAxiom(subClass,op).accept(this);
                }
            }else{
                OWLClass freshClass = getFreshClass();
                factory.getOWLSubClassOfAxiom(subClass, freshClass).accept(this);
                factory.getOWLSubClassOfAxiom(freshClass, superClass).accept(this);
            }
        }
        // A' subclass C
        else if (superClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
            /*OK*/
            // and(A', B) subclass C
            // and(B, A') subclass C
            // -> {A' subclass D, and(D,C) subclass B}
            if (subClass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
                OWLObjectIntersectionOf inter = (OWLObjectIntersectionOf) subClass;
                Set<OWLClassExpression> operands = inter.getOperands();
                Set<OWLClassExpression> newOps = new HashSet<OWLClassExpression>();
                boolean normalized = true;
                for (OWLClassExpression op : operands) {
                    if (!(op.getClassExpressionType() == ClassExpressionType.OWL_CLASS)) {
                        normalized = false;
                        break;
                    }
                }
                if (normalized) {
                    manager.addAxiom(normalizedOnt, axiom);
                } else {
                    for (OWLClassExpression op : operands) {
                        if (!(op.getClassExpressionType() == ClassExpressionType.OWL_CLASS)) {
                            OWLClass freshClass = getFreshClass();
                            newOps.add(freshClass);
                            // manager.addAxiom(normalizedOnt, factory
                            // .getOWLSubClassOfAxiom(op, freshClass));
                            //
                            factory.getOWLSubClassOfAxiom(op, freshClass).accept(this);
                        } else {
                            newOps.add(op);
                        }
                    }

                    manager.addAxiom(normalizedOnt,
                            factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(newOps), superClass));
                }

            }
            /*OK*/
            // exist(R,A') subclass B
            else if (subClass.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {
                OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) subClass;
                OWLClassExpression filler = some.getFiller();
                if (filler.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                    manager.addAxiom(normalizedOnt, axiom);
                } else {
                    // OWLClass freshClass = getFreshClass("some_");
                    OWLClass freshClass = getFreshClass();
                    factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(some.getProperty(), freshClass),
                            superClass) //
                            .accept(this);
                    factory.getOWLSubClassOfAxiom(filler, freshClass).accept(this);
                }
            }
            /*OK*/
            // all(R,A') subclass B
            else if (subClass.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) subClass;
                OWLClassExpression filler = all.getFiller();
                if (filler.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                    manager.addAxiom(normalizedOnt, axiom);
                } else {
                    // OWLClass freshClass = getFreshClass("some_");
                    OWLClass freshClass = getFreshClass();
                    factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(all.getProperty(), freshClass),
                            superClass) //
                            .accept(this);
                    factory.getOWLSubClassOfAxiom(filler, freshClass).accept(this);
                }
            }

            /*OK*/
            // or(A', B) subclass C
            // or(B, A') subclass C
            else if (subClass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {
                //labi
                OWLObjectUnionOf union = (OWLObjectUnionOf) subClass;
                Set<OWLClassExpression> operands = union.getOperands();
                for (OWLClassExpression op : operands) {
                    if (op.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                        manager.addAxiom(normalizedOnt, factory.getOWLSubClassOfAxiom(op, superClass));
                    } else{
                        factory.getOWLSubClassOfAxiom(op,superClass).accept(this);
                    }
                }
            }
            else if (subClass.getClassExpressionType() == ClassExpressionType.OBJECT_COMPLEMENT_OF) {
                OWLObjectComplementOf c = (OWLObjectComplementOf) subClass;
                OWLClassExpression operand = c.getOperand();
                factory.getOWLSubClassOfAxiom(factory.getOWLThing(),factory.getOWLObjectUnionOf(superClass, operand)) //
                        .accept(this);
            }
        }
        // A subclass C'
        else if (subClass.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
            /*TEST*/
            // (A -> or(B, C)) ~>
            // TODO: need to check
            if (superClass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {

                OWLObjectUnionOf union = (OWLObjectUnionOf) superClass;
                Set<OWLClassExpression> operands = union.getOperands();
                //Iterator<OWLClassExpression> iterator = operands.iterator();
                OWLClass fresh;
                //OWLClassExpression first = iterator.next();

                Set<OWLClassExpression> rhsExpressionSet=new HashSet<OWLClassExpression>();

                for(OWLClassExpression cls:operands)
                {
                    if(cls.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                        rhsExpressionSet.add(cls);
                    }
                    else{
                        fresh = getFreshClass();
                        //TODO: add here the code for the axiom of adding the complex concept
                        //TODO:check check check
                        rhsExpressionSet.add(fresh);
                        //add to the onotology directly
                        factory.getOWLSubClassOfAxiom(fresh, cls).accept(this);
                    }
                }
                //add also this to the ontology
                manager.addAxiom(normalizedOnt, factory.getOWLSubClassOfAxiom(subClass, factory.getOWLObjectUnionOf(rhsExpressionSet)));
            }

            /*OK*/
            // A subclass and(B, C)
            else if (superClass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
                OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) superClass;
                for (OWLClassExpression op : and.getOperands()) {
                    factory.getOWLSubClassOfAxiom(subClass, op).accept(this);
                }
            }
            /*OK*/
            // A subclass exists(R, C') -> {A subclass some(R,D), D subclass C'}
            else if (superClass.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {
                OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) superClass;
                OWLClassExpression filler = some.getFiller();
                if (filler.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                    manager.addAxiom(normalizedOnt, axiom);
                } else {
                    OWLClass freshClass = getFreshClass("SOME_");
                    factory.getOWLSubClassOfAxiom(subClass,
                            factory.getOWLObjectSomeValuesFrom(some.getProperty(), freshClass)) //
                            .accept(this);
                    factory.getOWLSubClassOfAxiom(freshClass, filler).accept(this);
                }
            }
            /*OK*/
            // A subclass all(R, C') -> {A subclass all(R, D), D subclass C'}
            else if (superClass.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) superClass;
                OWLClassExpression filler = all.getFiller();
                if (filler.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                    manager.addAxiom(normalizedOnt, axiom);
                } else {
                    OWLClass freshClass = getFreshClass("ALL_");
                    factory.getOWLSubClassOfAxiom(subClass,
                            factory.getOWLObjectAllValuesFrom(all.getProperty(), freshClass)) //
                            .accept(this);
                    factory.getOWLSubClassOfAxiom(freshClass, filler).accept(this);
                }
            }
            /*OK*/
            // A subclass not(B)
            else if (superClass.getClassExpressionType() == ClassExpressionType.OBJECT_COMPLEMENT_OF) {
                OWLObjectComplementOf c = (OWLObjectComplementOf) superClass;
                OWLClassExpression operand = c.getOperand();
                //OWLClass freshClass = getFreshClass();
                //factory.getOWLSubClassOfAxiom(subClass, freshClass).accept(this);
                factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(subClass, operand),
                        factory.getOWLNothing()) //
                        .accept(this);
            }
        }
        return null;
    }

    private OWLClass getFreshClass() {
        freshClassCounter++;
        return factory.getOWLClass(IRI.create("http://www.example.org/fresh#" + "fresh" + freshClassCounter));
    }

    private OWLClass getFreshClass(String suffix) {
        freshClassCounter++;
        return factory.getOWLClass(IRI.create("http://www.example.org/fresh#" + suffix + "fresh" + freshClassCounter));
    }

    private OWLClass getFreshSomeClass(String role, String concept) {
        return factory.getOWLClass(IRI.create("http://www.example.org/fresh#FreshSome(" + role + "|"+ concept+")"));
    }

    private OWLClass getFreshForAllClass(String role, String concept) {
        return factory.getOWLClass(IRI.create("http://www.example.org/fresh#FreshAll(" + role + "|"+ concept+")"));
    }

    /*OK*/
    @Override
    public Object visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLDisjointClassesAxiom axiom) {
        //in order to get specific elements from the set, a conversion to array was needed
        OWLClassExpression[] arrClassExpressions= axiom.getClassExpressions().toArray( new OWLClassExpression[axiom.getClassExpressions().size()]);
        for(int i=0;i<arrClassExpressions.length-1;i++){
            for(int j=i+1;j<arrClassExpressions.length;j++){
                factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(arrClassExpressions[i],arrClassExpressions[j]),
                        factory.getOWLNothing())//
                        .accept(this);
            }
        }
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLDataPropertyDomainAxiom axiom) {
        //should add a condition for DataSomeValuesFrom in SubClassOf visit method in order for this method to work
        factory.getOWLSubClassOfAxiom(factory.getOWLDataSomeValuesFrom(axiom.getProperty(), factory.getTopDatatype()),
                axiom.getDomain()) //
                .accept(this);
        return null;
    }

    /*OK*/
    // domain(r) = C ~> some(r, T) subclass C
    @Override
    public Object visit(OWLObjectPropertyDomainAxiom axiom) {
        factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(axiom.getProperty(), factory.getOWLThing()),
                axiom.getDomain()) //
                .accept(this);
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
            ax.accept(this);
        }
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return null;
    }

    /*
    * Since we assume UNA,
    * we will ignore axioms of this type
    * */
    @Override
    public Object visit(OWLDifferentIndividualsAxiom axiom) {
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLDisjointDataPropertiesAxiom axiom) {
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLDisjointObjectPropertiesAxiom axiom) {
        return null;
    }

    // gohui:range(r) = C ~> T subclass all(r, C)
    /*OK*/
    @Override
    public Object visit(OWLObjectPropertyRangeAxiom axiom) {
        factory.getOWLSubClassOfAxiom(factory.getOWLThing(),
                factory.getOWLObjectAllValuesFrom(axiom.getProperty(), axiom.getRange())) //
                .accept(this);
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLObjectPropertyAssertionAxiom axiom) {
        manager.addAxiom(normalizedOnt, axiom);
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return null;
    }

    /* OK for now since there are no ontologies using this type of axioms
    * but has to be treated in the future
    * */
    @Override
    public Object visit(OWLSubObjectPropertyOfAxiom axiom) {
        manager.addAxiom(normalizedOnt, axiom);
        return null;
    }

    /* OK - reconsider treating this type of axioms by adding two axioms
    *  DisjointUnion(D,A,B,C) translate to
    *  --------------------------------------------
    *  A and B -> bottom, A and C -> bottom, B and C -> bottom
    *  D-> A or B or C
    *  for now we are going to ignore this type of axioms, since only two ontologies have used them in total 3 times
    * */
    @Override
    public Object visit(OWLDisjointUnionAxiom axiom) {

        //in order to get specific elements from the set, a conversion to array was needed
        OWLClassExpression[] arrClassExpressions= axiom.getClassExpressions().toArray( new OWLClassExpression[axiom.getClassExpressions().size()]);
        //add the disjoint classes axioms
        for(int i=0;i<arrClassExpressions.length-1;i++){
            for(int j=i+1;j<arrClassExpressions.length;j++){
                factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(arrClassExpressions[i],arrClassExpressions[j]),
                        factory.getOWLNothing())//
                        .accept(this);
            }
        }
        //add the union axiom A->or(B,C,D)
        factory.getOWLSubClassOfAxiom(axiom.getOWLClass(),factory.getOWLObjectUnionOf(axiom.getClassExpressions()))
                .accept(this);

        return null;
    }

    /* OK */
    @Override
    public Object visit(OWLDeclarationAxiom axiom) {
        manager.addAxiom(normalizedOnt, axiom);
        return null;

    }

    /* OK -
    *  irrelevant for TBox reasoning*/
    @Override
    public Object visit(OWLAnnotationAssertionAxiom axiom) {
        manager.addAxiom(normalizedOnt, axiom);
        return null;

    }

    /*OK*/
    @Override
    public Object visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return null;
    }

    /* OK reconsider
    * maybee we shoulc treat it the same as Data Domain axioms
    * */
    @Override
    public Object visit(OWLDataPropertyRangeAxiom axiom) {
        // TODO Check
        // throw new IllegalArgumentException(axiom.toString());
        //manager.addAxiom(normalizedOnt, axiom);
        return null;
    }

    /* OK*/
    @Override
    public Object visit(OWLFunctionalDataPropertyAxiom axiom) {
        return null;
    }

    //TODO:FIX IT L.B.
    /*Not important for now since we will not be
    * doing TBox reasoning*/
    @Override
    public Object visit(OWLEquivalentDataPropertiesAxiom axiom) {
        //throw new IllegalArgumentException(axiom.toString());
        return null;
    }

    /* Check if it works correctly
    * have to add the code that adds the complex concept to the TBox
    * and simplifies the concept in the class assertion so e.g.
    * ClassAssertion(individual, A and B) rewrite to:
    * ----------------------------------------------
    * ClassAssertion(individual, fresh1)
    * subclassof(fresh1,A and B)
    * */
    @Override
    public Object visit(OWLClassAssertionAxiom axiom) {
        /* if the class is complex, than add an GCI axiom to represent the complex class expression in TBox
         * moreover, add a class assertion with the simplified class*/
        if(!(axiom.getClassExpression().getClassExpressionType() == ClassExpressionType.OWL_CLASS)){
            OWLClass cl = getFreshClass();
            factory.getOWLSubClassOfAxiom(cl,axiom.getClassExpression()).accept(this);
            factory.getOWLClassAssertionAxiom(cl, axiom.getIndividual()).accept(this);
        }else{
            manager.addAxiom(normalizedOnt, axiom);
        }
        return null;
    }

    //TODO:FIX IT L.B.
    @Override
    public Object visit(OWLEquivalentClassesAxiom axiom) {
/*
        Set<OWLClassExpression> classes = axiom.getClassExpressions();

        for (OWLClassExpression cls1 : classes) {
            for (OWLClassExpression cls2 : classes) {
                if (cls1.equals(cls2))
                    continue;
                factory.getOWLSubClassOfAxiom(cls1, cls2).accept(this);
            }
        }*/
        return null;
    }

    /*OK
    * not needed for TBox reasoning*/
    @Override
    public Object visit(OWLDataPropertyAssertionAxiom axiom) {
        manager.addAxiom(normalizedOnt, axiom);
        return null;
    }

    /*OK
    * removed for ALCH,ALCHI*/
    @Override
    public Object visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return null;
    }

    /*OK
    * removed for ALCH,ALCHI*/
    @Override
    public Object visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return null;
    }

    /*OK - reconsider
    * no axioms of this type are found in oxford repositories,
    * but non the less I have to add the code to treat it for
    * ontologies that might use it
    * */
    @Override
    public Object visit(OWLSubDataPropertyOfAxiom axiom) {
        // TODO: FIXIT
        return null;
//		throw new IllegalArgumentException(axiom.toString());

    }

    /*OK - reconsider
    * removed for ALCH
    * has to be treated for ALCHI */
    @Override
    public Object visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
/*        OWLObjectPropertyExpression property = axiom.getProperty();
        factory.getOWLSubClassOfAxiom(factory.getOWLThing(),
                factory.getOWLObjectMaxCardinality(1, property.getInverseProperty()))//
                .accept(this);*/
        return null;
    }

    /*OK*/
    @Override
    public Object visit(OWLSameIndividualAxiom axiom) {
        return null;
    }

    /*OK - no need to fix anything*/
    @Override
    public Object visit(OWLSubPropertyChainOfAxiom axiom) {
        // FIXME: check how far we can go
        return null;
    }

    /*OK - removed for ALCH
    *      has to be added back for ALCHI
    * */
    @Override
    public Object visit(OWLInverseObjectPropertiesAxiom axiom) {
        //manager.addAxiom(normalizedOnt, axiom);
        return null;
    }

    /*OK - reconsider
    * consider simply returning null
    * */
    @Override
    public Object visit(OWLHasKeyAxiom axiom) {
        throw new IllegalArgumentException(axiom.toString());
        //return null;
    }

    /*OK - reconsider
    * consider simply returning null
    * */
    @Override
    public Object visit(OWLDatatypeDefinitionAxiom axiom) {
        throw new IllegalArgumentException(axiom.toString());
        //return null;
    }

    /*OK - reconsider
    * consider simply returning null
    * */
    @Override
    public Object visit(SWRLRule rule) {
        //throw new IllegalArgumentException(axiom.toString()); commented out by L.B.
        return null;
    }

}
