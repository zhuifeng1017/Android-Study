public class Test {
	static {
		System.loadLibrary("src/JDll");
	}

	public native static void showVoidContent();

	public native static String showReturnContent();

	public native static String getContent(String bookName, int bookId,
			long bookTime, double bookPrice);

	public native static String getOneContent(String one, String two);

	public native static double getTwoDouble(double one, double two,
			String three);

	public static void main(String[] args) {
		Test tt = new Test();
		String str = Test.getOneContent("¸ã·É»ú", "¸ã´óÂé");
		System.out.println(str);

	}
}
