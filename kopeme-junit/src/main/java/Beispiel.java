import org.apache.logging.log4j.core.lookup.MainMapLookup;

public class Beispiel {
	public void callerAB() {
		System.out.println("AB");
		methodA();
		methodB();
		Thread r3 = new Thread() {

			@Override
			public void start() {
				System.out.println("Run R3");

			}
		};
		r3.start();
		
		class MyStuff2{
			public void doMyStuff5(){ System.out.println("stuff 1");}
			public void doMyStuff6(){ System.out.println("stuff A");}
		}
		
		new MyStuff2().doMyStuff5();
	}

	public static void main(String[] args) {
		Beispiel bsp = new Beispiel();

		bsp.callerCD();

		bsp.callerAB();

		Runnable r3 = new Runnable() {

			@Override
			public void run() {
				System.out.println("Run R3");

			}
		};
		r3.run();

		Object o = new Object();

		Beispiel2.v.getX();
		
		new MyStuff().doMyStuff1();
		
		 class MyStuff2{
			public void doMyStuff3(){ System.out.println("stuff 1");}
			public void doMyStuff4(){ System.out.println("stuff A");}
		}
		 
		 new MyStuff2().doMyStuff3();
	}

	public void methodA() {
		System.out.println("A");
	}

	public void methodB() {
		System.out.println("B");
	}

	public void callerCD() {
		System.out.println("CD");
		methodC();
		methodD();
		((Runnable) r1).run();
		((Runnable) r2).run();
	}

	public void methodC() {
		System.out.println("C");
	}

	public void methodD() {
		System.out.println("D");
	}

	static Object r1 = new Runnable() {

		@Override
		public void run() {
			System.out.println("R1");

		}
	};

	static Object r2 = new Runnable() {

		@Override
		public void run() {
			System.out.println("r2");
		}
	};
	
	static class MyStuff{
		public void doMyStuff1(){ System.out.println("stuff 1");}
		public void doMyStuff2(){ System.out.println("stuff A");}
	}
}
