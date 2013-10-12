import java.util.Hashtable;
import java.util.HashMap;
import java.lang.Runnable;
import java.lang.Thread;

interface TTActionListener {
	public abstract void actionPerformed();
}

public class TT {

	private class TTThread implements Runnable {
		public void run() {
			int i = 100;
			while (i-- > 0) {
				try {
					Thread.sleep(100);
					System.out.println(i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private TTThread t = new TTThread();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TT obj = new TT();
		Thread thr = new Thread(obj.t);
		thr.run();
	}

}
