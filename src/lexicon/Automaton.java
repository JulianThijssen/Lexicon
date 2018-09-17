package lexicon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Automaton {
	private static final char EPSILON = 'ε';
	private static final char KLEENE_STAR = '*';
	private static final char LEFT_PAREN = '(';
	private static final char RIGHT_PAREN = ')';
	
	private State initialState;
	private State finalState;
	
	public void setInitialState(State state) {
		initialState = state;
	}
	
	public void setFinalState(State state) {
		finalState = state;
	}
	
	/**
	 * Returns all the reachable states of this automaton.
	 * 
	 * @return The reachable states of this automaton
	 */
	public Set<State> getAutomatonStates() {
		Set<State> states = new HashSet<State>();
		traverseAutomaton(initialState, states);
		return states;
	}
	
	/**
	 * Parses the grammar rule into a automaton that accepts all phrases that satisfy this rule.
	 * 
	 * @param exp
	 * @return
	 */
	public static Automaton parse(String rule) {
		CharacterStream stream = new CharacterStream(rule);
		
		if (rule.length() == 1) {
			Automaton automaton = new Automaton();
			State initState = new State();
			State finalState = new State();
			automaton.setInitialState(initState);
			automaton.setFinalState(finalState);
			initState.addTransition(new Transition(stream.get(), finalState));

			return automaton;
		}
		
		Stack<Automaton> automata = new Stack<Automaton>();

		String buffer = "";
		
		int depth = 0;
		while (!stream.isEmpty()) {
			char c = stream.get();

			if (c == LEFT_PAREN) {
				depth++;
				if (depth == 1) {
					buffer = "";
					continue;
				}
			}
			if (c == RIGHT_PAREN) {
				depth--;
				if (depth == 0) {
					automata.push(parse(buffer));
					continue;
				}
			}
			if (depth > 0) {
				buffer += c;
				continue;
			}
			if (Character.isAlphabetic(c)) {
				Automaton a = parse("" + c);
				automata.push(a);

				continue;
			}
			if (c == KLEENE_STAR) {
				Automaton a = automata.pop();
				automata.push(kleene(a));
			}
		}

		// Concatenate automata
		Automaton tail = automata.pop();
		while (!automata.isEmpty()) {
			Automaton head = automata.pop();
			head.finalState.addTransition(new Transition(EPSILON, tail.initialState));
			
			tail.setInitialState(head.initialState);
		}
		
		// FIXME This might not always be true
		
		tail.finalState.isFinal = true;
		
		tail.removeEpsilonTransitions();
		System.out.println(tail.finalState);
		return tail;
	}
	
	/**
	 * Check whether the given string is accepted by the automaton.
	 * 
	 * @param s the string to consider for acceptance
	 * @return true if the automaton accepts the given string
	 */
	public boolean accepts(String s) {
		CharacterStream stream = new CharacterStream(s);

		return stateAccepts(initialState, stream);
	}
	
	private boolean stateAccepts(State state, CharacterStream stream) {
		boolean accepted = false;
		
		if (!stream.isEmpty()) {
			char c = stream.get();
			
			Set<State> acceptingStates = getAcceptingStates(state, c);
			System.out.println("Number of accepting states: " + acceptingStates.size());
			for (State transitionState: acceptingStates) {
				accepted |= stateAccepts(transitionState, stream);
			}
		}
		else {
			return state.isFinal;
		}
		
		return accepted;
	}
	
	private Set<State> getAcceptingStates(State currentState, char c) {
		Set<State> transitionStates = new HashSet<State>();
		
		for (Transition transition: currentState.transitions) {
			if (transition.valid(c)) {
				transitionStates.add(transition.state);
			}
		}
		
		return transitionStates;
	}
	
	private void traverseAutomaton(State state, Set<State> seenStates) {
		// Don't process an already seen state
		if (seenStates.contains(state)) {
			return;
		}
		
		// Add this state to the seen states to avoid recursing back into this state
		seenStates.add(state);
		
		// Follow all transitions to new states and recursively traverse them
		for (Transition t: state.transitions) {
			traverseAutomaton(t.state, seenStates);
		}
	}
	
	private void removeEpsilonTransitions() {
		Set<State> allStates = getAutomatonStates();
		
		for (State state: allStates) {
			removeEpsilon(state);
		}
	}
	
	private void removeEpsilon(State currentState) {
		// Replace all epsilon transitions of this state
		Flag touchedFinal = new Flag(false);
		List<Transition> transitions = findNonEpsilonTransitions(currentState, touchedFinal);
		currentState.transitions = transitions;
		currentState.isFinal = currentState.isFinal || touchedFinal.value;
		
		return;
	}
	
	private List<Transition> findNonEpsilonTransitions(State state, Flag touchedFinal) {
		List<Transition> nonEpsilonTransitions = new ArrayList<Transition>();
		for (Transition t: state.transitions) {
			if (t.symbol == EPSILON) {
				if (t.state.isFinal) { System.out.println("Touched final"); touchedFinal.value = true; }
				
				List<Transition> transitions = findNonEpsilonTransitions(t.state, touchedFinal);
				nonEpsilonTransitions.addAll(transitions);
			}
			else {
				nonEpsilonTransitions.add(t);
			}
		}
		return nonEpsilonTransitions;
	}
	
	private static Automaton kleene(Automaton kfa) {
		Automaton automaton = new Automaton();
		
		State initState = new State();
		State finalState = new State();
		
		automaton.setInitialState(initState);
		automaton.setFinalState(finalState);
		
		initState.addTransition(new Transition(EPSILON, kfa.initialState));
		kfa.finalState.addTransition(new Transition(EPSILON, finalState));
		initState.addTransition(new Transition(EPSILON, finalState));
		kfa.finalState.addTransition(new Transition(EPSILON, kfa.initialState));
		
		return automaton;
	}
	
	public void print() {
		System.out.println("Printing..");
		StringBuilder sb = new StringBuilder();
		
		Set<State> stateSet = getAutomatonStates();

		for (State state: stateSet) {
			for (Transition t: state.transitions) {
				sb.append(state + t.toString() + t.state + "\n");
			}
		}
		
		System.out.println(sb);
	}
	
	public static void main(String[] args) {
		Automaton automaton = Automaton.parse("b(ab)*");
		automaton.finalState.isFinal = true;
		automaton.print();
		System.out.println("Removing epsilon transitions");
		automaton.removeEpsilonTransitions();
		automaton.print();
		System.out.println(automaton.accepts("bab"));
		System.out.println(automaton.accepts("abab"));
		System.out.println(automaton.accepts("b"));
		System.out.println(automaton.accepts("bababab"));
	}
}
