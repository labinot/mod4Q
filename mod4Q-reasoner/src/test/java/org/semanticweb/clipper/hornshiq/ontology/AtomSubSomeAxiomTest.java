package org.semanticweb.clipper.hornshiq.ontology;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.semanticweb.clipper.hornshiq.ontology.ClipperAtomSubSomeAxiom;

public class AtomSubSomeAxiomTest {
	@Test
	public void test001() {
		ClipperAtomSubSomeAxiom axiom = new ClipperAtomSubSomeAxiom(0, 2, 3);
		assertEquals("0 SubClassOf 2 some 3", axiom.toString());
	}
	
	@Test
	public void test002() {
		ClipperAtomSubSomeAxiom axiom = new ClipperAtomSubSomeAxiom(0, 1, 3);
		assertEquals("0 SubClassOf inv(0) some 3", axiom.toString());
	}

}
