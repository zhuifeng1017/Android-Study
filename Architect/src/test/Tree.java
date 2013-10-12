package test;

public class Tree {

	public Node root;

	public Node findNode(Node node) {
		Node current = root;
		while (current.key != node.key) {
			if (current.key > node.key) {
				current = current.left;
			} else {
				current = current.right;
			}

			if (current == null) {
				return null;
			}
		}

		return current;
	}

	public void insertNode(Node node) {
		if (root == null) {
			root = node;
		} else {
			Node current = root;
			Node parent;

			while (true) {
				parent = current;

				if (current.key > node.key) {
					current = current.left;

					if (current == null) {
						parent.left = node;
						return;
					}
				} else {
					current = current.right;

					if (current == null) {
						parent.right = node;
						return;
					}
				}
			}
		}
	}

	public void inOrder(Node current) {
		if (current != null) {
			inOrder(current.left);
			System.out.println(current);
			inOrder(current.right);
		}
	}

	public void preOrder(Node current) {
		if (current != null) {
			System.out.println(current);
			preOrder(current.left);
			preOrder(current.right);
		}
	}

	public void backOrder(Node current) {
		if (current != null) {
			backOrder(current.left);
			backOrder(current.right);
			System.out.println(current);
		}
	}

	public Node minNode() {
		Node current = root;
		while (current.left != null) {
			current = current.left;
		}
		return current;
	}

	public Node maxNode() {
		Node current = root;
		while (current.right != null) {
			current = current.right;
		}
		return current;
	}

	public boolean deleteNode(Node node) {
		Node current = root;
		Node parent = root;
		boolean isLeft = true;

		// 找到要删除的节点
		while (current.key != node.key) {
			parent = current;

			if (current.key > node.key) {
				isLeft = true;
				current = current.left;
			} else {
				isLeft = false;
				current = current.right;
			}

			if (current == null) {
				return false;
			}
		}

		if (current.left == null && current.right == null) { // 叶子
			if (current.key == root.key) {
				root = null;
			} else if (isLeft) {
				parent.left = null;
			} else {
				parent.right = null;
			}
		} else if (current.right == null) { // 只有左节点
			if (current.key == root.key) {
				root = current.left;
			} else if (isLeft) {
				parent.left = current.left;
			} else {
				parent.right = current.left;
			}
		} else if (current.left == null) { // 只有左节点
			if (current.key == root.key) {
				root = current.right;
			} else if (isLeft) {
				parent.left = current.right;
			} else {
				parent.right = current.right;
			}
		} else {// 删除左右节点都有的节点
			Node successor = getSuccessor(current); // 找到中序后继

			if (current == root) {
				root = successor;
			} else if (isLeft) {
				parent.left = successor;
			} else {
				parent.right = successor;
			}

			successor.left = current.left;
		}

		return true;
	}

	private Node getSuccessor(Node delNode) {
		Node successorParent = delNode;
		Node successor = delNode;
		Node current = delNode.right;

		while (current != null) {
			successorParent = successor;
			successor = current;
			current = current.left;
		}

		if (successor != delNode.right) {
			successorParent.left = successor.right;
			successor.right = delNode.right;
		}

		return successor;
	}

	public static void main(String[] args) {
		Tree tt = new Tree();
		int arr[] = { 9, 4, 7, 1, 2, 5, 0, -1, -9, 8 };
		for (int i = 0; i < arr.length; i++) {
			tt.insertNode(new Node(arr[i]));
		}
		tt.inOrder(tt.root);
	}
}
