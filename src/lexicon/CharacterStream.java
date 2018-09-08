package lexicon;

public class CharacterStream {
	private char[] stream;
	
	private int position = 0;
	
	public CharacterStream(String s) {
		stream = s.toCharArray();
	}
	
	public boolean isEmpty() {
		return position == stream.length;
	}
	
	public char get() {
		return stream[position++];
	}
}
