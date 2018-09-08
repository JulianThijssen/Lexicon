package lexicon;

public class Transition {
	public char symbol;
	public State state;
	
	public Transition(char symbol, State state) {
		this.symbol = symbol;
		this.state = state;
	}
	
	public boolean valid(char c) {
		return symbol == c;
	}
	
	@Override
	public String toString() {
		return " -" + symbol + "->";
	}
}
