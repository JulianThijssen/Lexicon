package lexicon;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Alphabet {
	private Set<Character> alphabet = new HashSet<Character>();
	
	public Set<Character> getCharacters()
	{
		return alphabet;
	}
	
	public void parse(String rule) {
		Scanner reader = new Scanner(rule);
		reader.useDelimiter("");
		
		while (reader.hasNext()) {
			char c = reader.next().charAt(0);
			
			if (Character.isAlphabetic(c)) {
				alphabet.add(c);
			}
		}
		
		reader.close();
	}
	
	public void parse(Automaton automaton) {		
		Set<State> stateSet = automaton.getAutomatonStates();

		for (State state: stateSet) {
			for (Transition t: state.transitions) {
				alphabet.add(t.symbol);
			}
		}
		
		System.out.println("Alphabet: ");
		for (Character c: alphabet) {
			System.out.println(c);
		}
	}
}
