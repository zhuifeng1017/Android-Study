package test;

public class Node {

	public int key;
	public Node left = null;
	public Node right = null;

	public Node(int key) {
		this.key = key;
	}

	public String toString() {
		return "\t" + key;
	}

}
