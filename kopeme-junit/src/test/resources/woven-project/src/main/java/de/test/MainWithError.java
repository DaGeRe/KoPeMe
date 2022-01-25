package de.test;

class FinalFieldConstructorExample {

	private final Integer parameters = 5;

	public Integer getParameters() {
		return parameters;
	}

}

public class MainWithError {
	public static void main(String[] args) {
		FinalFieldConstructorExample example = new FinalFieldConstructorExample();
		System.out.println(example.getParameters());
	}
}
