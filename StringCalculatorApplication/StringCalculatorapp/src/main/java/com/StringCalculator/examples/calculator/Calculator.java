
package com.StringCalculator.examples.calculator;

import static com.StringCalculator.Scanners.isChar;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import com.StringCalculator.OperatorTable;
import com.StringCalculator.Parser;
import com.StringCalculator.Scanners;

/**
 * The main calculator parser.
 * 
 * @author Rohit Amgaonkar
 */
public final class Calculator {
	public static int evaluate(String source) {
		System.out.print(parser().parse(source));
		return parser().parse(source);
	}

	static final Parser<Integer> NUMBER = Scanners.INTEGER.map(Integer::valueOf);

	static final BinaryOperator<Integer> PLUS = (a, b) -> a + b;

	static final BinaryOperator<Integer> MINUS = (a, b) -> a - b;

	static final BinaryOperator<Integer> MUL = (a, b) -> a * b;

	static final BinaryOperator<Integer> DIV = (a, b) -> a / b;

	static final BinaryOperator<Integer> MOD = (a, b) -> a % b;

	static final UnaryOperator<Integer> NEG = a -> -a;

	static final BinaryOperator<Integer> SQUR = (a, b) -> (int) Math.pow(a, b);

	private static <T> Parser<T> op(char ch, T value) {
		return isChar(ch).retn(value);
	}

	static Parser<Integer> parser() {
		Parser.Reference<Integer> ref = Parser.newReference();
		Parser<Integer> term = ref.lazy().between(isChar('('), isChar(')')).or(NUMBER);
		Parser<Integer> parser = new OperatorTable<Integer>().prefix(op('-', NEG), 100).infixl(op('^', SQUR), 100)
				.infixl(op('+', PLUS), 10).infixl(op('-', MINUS), 10).infixl(op('*', MUL), 20).infixl(op('/', DIV), 20)
				.infixl(op('%', MOD), 20).build(term);
		ref.set(parser);
		return parser;
	}
}
