package de.test;

class FinalFieldConstructorExample {
	// final fields may cause AspectJ errors in java 11
	private final Integer parameters = 5;

	public Integer getParameters() {
		return parameters;
	}

	public void throwSomething(){
		throw new RuntimeException();
	}

	public void catchSomething(){
		try{
			throwSomething();
		}catch (RuntimeException e){
			System.out.println("Caught!");
		}
	}

}

public class MainWithError {
	public static void main(String[] args) {
		FinalFieldConstructorExample example = new FinalFieldConstructorExample();
		System.out.println(example.getParameters());
		example.catchSomething();
	}
}
