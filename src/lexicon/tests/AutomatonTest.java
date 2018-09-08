package lexicon.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

import lexicon.Automaton;
import lexicon.State;

class AutomatonTest {
	@Test
	void testConcatenation() {
		Automaton automaton = Automaton.parse("ab");
		automaton.print();
		assertTrue(automaton.accepts("ab"));
	}
	
	@Test
	void testGetAutomatonStates() {
		Automaton automaton = Automaton.parse("b(ab)*");
		
		Set<State> states = automaton.getAutomatonStates();
		
		automaton.print();
		
		assertTrue(states.size() == 4);
	}
}
