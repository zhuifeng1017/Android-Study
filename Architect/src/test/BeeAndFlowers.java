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

// 这是一个对象生成器
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

// 客户测试程序
public class BeeAndFlowers extends TestCase {

	/*
	 * 首先在客户端先获得一个具体的访问者角色 遍历对象结构 对每一个元素调用accept方法， 将具体访问者角色传入 这样就完成了整个过程
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