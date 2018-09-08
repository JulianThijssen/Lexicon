package lexicon;

import java.util.ArrayList;
import java.util.List;

public class State {
	public static int globalID = 0;
	
	public int id;
	public boolean isFinal;
	
	public List<Transition> transitions = new ArrayList<Transition>();
	
	public State() {
		this.id = globalID++;
	}
	
	public void addTransition(Transition transition) {
		transitions.add(transition);
	}
	
	@Override
	public String toString() {
		if (isFinal)
			return "((" + id + "))";
		else
			return "(" + id + ")";
	}
}
