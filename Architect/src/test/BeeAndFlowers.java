package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

interface Visitor {
	void visit(Gladiolus g);

	void visit(Runuculus r);

	void visit(Chrysanthemum c);
}

interface Flower {
	void accept(Visitor v);
}

// concrete element
class Gladiolus implements Flower {
	public void accept(Visitor v) {
		v.visit(this);
	}
}

class Runuculus implements Flower {
	public void accept(Visitor v) {
		v.visit(this);
	}
}

class Chrysanthemum implements Flower {
	public void accept(Visitor v) {
		v.visit(this);
	}
}

// concrete visitor
class StringVal implements Visitor {
	String s;

	public String toString() {
		return s;
	}

	public void visit(Gladiolus g) {
		s = "Gladiolus";
	}

	public void visit(Runuculus r) {
		s = "Runuculus";
	}

	public void visit(Chrysanthemum c) {
		s = "Chrysanthemum";
	}
}

// concrete visitor
class Bee implements Visitor {
	public void visit(Gladiolus g) {
		System.out.println("Bee and Gladiolus");
	}

	public void visit(Runuculus r) {
		System.out.println("Bee and Runuculus");
	}

	public void visit(Chrysanthemum c) {
		System.out.println("Bee and Chrysanthemum");
	}
}

// ����һ������������
class FlowerGenerator {
	private static Random rand = new Random();

	public static Flower newFlower() {
		switch (rand.nextInt(3)) {
		default:
		case 0:
			return new Gladiolus();
		case 1:
			return new Runuculus();
		case 2:
			return new Chrysanthemum();
		}
	}
}

// �ͻ����Գ���
public class BeeAndFlowers extends TestCase {

	/*
	 * �����ڿͻ����Ȼ��һ������ķ����߽�ɫ ��������ṹ ��ÿһ��Ԫ�ص���accept������ ����������߽�ɫ���� �������������������
	 */
	List<Flower> flowers = new ArrayList<Flower>();

	public BeeAndFlowers() {
		for (int i = 0; i < 10; i++)
			flowers.add(FlowerGenerator.newFlower());
	}

	Visitor sval;

	public void test() {
		sval = new StringVal();
		Iterator<Flower> it = flowers.iterator();
		while (it.hasNext()) {
			((Flower) it.next()).accept(sval);
			System.out.println(sval);
		}
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(BeeAndFlowers.class);
	}

}