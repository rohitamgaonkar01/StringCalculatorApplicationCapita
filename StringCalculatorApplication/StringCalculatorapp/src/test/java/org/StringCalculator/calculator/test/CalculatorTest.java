
package org.StringCalculator.calculator.test;

import org.junit.Test;

import com.StringCalculator.examples.calculator.Calculator;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {String Calculator}.
 * 
 * @author Rohit Amgaonkar
 */
public class CalculatorTest {

	@Test
	public void test1() {

		assertResult(1, 158, "7+(6*5^2+3-4/2)");
	}

	@Test
	public void test2() {

		assertResult(2, 7511, "7+(67(56*2))");

	}

	@Test
	public void test3() {
		assertResult(3, 1, "8*+7");
	}

	@Test
	public void test4() {
		assertResult(4, -3, "(8*5/8)-(3/1)-5");
	}

	private static void assertResult(int casenumber, int expected, String source) {
		System.out.print("Case #" + casenumber + " : ");
		assertEquals(expected, Calculator.evaluate(source));
		System.out.print("\n");

	}
}
