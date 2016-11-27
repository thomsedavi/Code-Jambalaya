import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;

import javax.swing.JFileChooser;

/**
 * The parser and interpreter. The top level parse function, a main method for
 * testing, and several utility methods are provided. You need to implement
 * parseProgram and all the rest of the parser.
 */

public class Parser {

	/**
	 * Top level parse method, called by the World
	 */
	static RobotProgramNode parseFile(File code) {
		Scanner scan = null;
		try {
			scan = new Scanner(code);

			// the only time tokens can be next to each other is
			// when one of them is one of (){},;
			scan.useDelimiter("\\s*(?=[(),])|(?<=[(),])\\s*| ");

			RobotProgramNode n = parseProgram(scan); // You need to implement
														// this!!!

			scan.close();
			return n;
		} catch (FileNotFoundException e) {
			System.out.println("Robot program source file not found");
		} catch (ParserFailureException e) {
			System.out.println("Parser error:");
			System.out.println(e.getMessage());
			scan.close();
		}
		return null;
	}

	/** For testing the parser without requiring the world */

	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				File f = new File(arg);
				if (f.exists()) {
					System.out.println("Parsing '" + f + "'");
					RobotProgramNode prog = parseFile(f);
					System.out.println("Parsing completed ");
					if (prog != null) {
						System.out.println("================\nProgram:");
						System.out.println(prog);
					}
					System.out.println("=================");
				} else {
					System.out.println("Can't find file '" + f + "'");
				}
			}
		} else {
			while (true) {
				JFileChooser chooser = new JFileChooser(".");// System.getProperty("user.dir"));
				int res = chooser.showOpenDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					break;
				}
				RobotProgramNode prog = parseFile(chooser.getSelectedFile());
				System.out.println("Parsing completed");
				if (prog != null) {
					System.out.println("Program: \n" + prog);
				}
				System.out.println("=================");
			}
		}
		System.out.println("Done");
	}

	// Useful Patterns

	private static Pattern NUMPAT = Pattern.compile("-?\\d+"); // ("-?(0|[1-9][0-9]*)");
	private static Pattern OPENPAREN = Pattern.compile("\\(");
	private static Pattern CLOSEPAREN = Pattern.compile("\\)");
	private static Pattern OPENBRACE = Pattern.compile("\\{");
	private static Pattern CLOSEBRACE = Pattern.compile("\\}");

	/**
	 * PROG ::= STMT+ Creates a scanner with all lines on one line and passes it
	 * to parseScanner method
	 */
	static ProgNode parseProgram(Scanner s) {

		String test = "";

		while (s.hasNext()) {
			test = test + s.nextLine() + " ";
		}

		Scanner scan = new Scanner(test);
		scan.useDelimiter("\\s*(?=[(),])|(?<=[(),])\\s*| ");

		return parseScanner(scan, null, 0);
	}

	/**
	 *
	 * @param scan
	 *            scanner being parsed
	 * @param progNode
	 *            passes outer node containing variable map to be copied into
	 *            inner block
	 * @param level
	 *            0 for main blocks, 1 or more for any internal blocks
	 * @return
	 */

	static ProgNode parseScanner(Scanner scan, ProgNode progNode, int level) {

		ProgNode prog = new ProgNode();
		IfElifElseNode ifNode = null; // When an if statement is encountered the
										// node goes here,
		// subsequent elif or else nodes are added to it until a new if
		// statement is encountered
		// and replaces it.

		// Transfer variables from outer Variable map to inner maps
		if (progNode != null) {
			for (String str : progNode.getVariables().keySet()) {
				prog.putVariable(str, progNode.getVariable(str));

			}
		}

		while (scan.hasNext()) {
			String nextWord = scan.next().trim();
			boolean fail = true; // if reaches end of block without finding
									// anything useful, fails.
			switch (nextWord) {
			case "if":
				if (!scan.next().equals("("))
					fail("Should be an open bracket: " + nextWord, scan);
				RobotConditionNode a = parseCondition(scan, prog);
				if (!scan.next().equals(")"))
					fail("Should be a close bracket: " + nextWord, scan);
				if (!scan.next().equals("{"))
					fail("Should be an open curly brace: " + nextWord, scan);
				ifNode = new IfElifElseNode(a, parseScanner(scan, prog,
						level + 1));
				prog.addNode(ifNode);
				fail = false;
				break;
			case "elif":
				if (!scan.next().equals("("))
					fail("Should be an open bracket: " + nextWord, scan);
				RobotConditionNode b = parseCondition(scan, prog);
				if (!scan.next().equals(")"))
					fail("Should be a close bracket: " + nextWord, scan);
				if (!scan.next().equals("{"))
					fail("Should be an open curly brace: " + nextWord, scan);
				ifNode.addIfNode(new IfElifElseNode(b, parseScanner(scan, prog,
						level + 1)));
				fail = false;
				break;
			case "else{":
				ifNode.addIfNode(new IfElifElseNode(new TrueNode(),
						parseScanner(scan, prog, level + 1)));
				fail = false;
				break;
			case "while":
				if (!scan.next().equals("("))
					fail("Should be an open bracket: " + nextWord, scan);
				RobotConditionNode d = parseCondition(scan, prog);
				if (!scan.next().equals(")"))
					fail("Should be a close bracket: " + nextWord, scan);
				if (!scan.next().equals("{"))
					fail("Should be an open curly brace: " + nextWord, scan);
				prog.addNode(new WhileNode(d, parseScanner(scan, prog,
						level + 1)));
				fail = false;
				break;
			case "loop{":
				prog.addNode(new LoopNode(parseScanner(scan, prog, level + 1)));
				fail = false;
				break;
			case "}":
				fail = false;
				return prog;
			case "move;":
				prog.addNode(new MoveNode());
				fail = false;
				break;
			case "move":
				prog.addNode(new MoveNode(parseExpression(scan, prog)));
				fail = false;
				break;
			case "turnL;":
				prog.addNode(new TurnLNode());
				fail = false;
				break;
			case "turnR;":
				prog.addNode(new TurnRNode());
				fail = false;
				break;
			case "turnAround;":
				prog.addNode(new TurnAroundNode());
				fail = false;
				break;
			case "shieldOn;":
				prog.addNode(new ShieldOnNode());
				fail = false;
				break;
			case "shieldOff;":
				prog.addNode(new ShieldOffNode());
				fail = false;
				break;
			case "takeFuel;":
				prog.addNode(new TakeFuelNode());
				fail = false;
				break;
			case "wait;":
				prog.addNode(new WaitNode());
				fail = false;
				break;
			case "wait":
				prog.addNode(new WaitNode(parseExpression(scan, prog)));
				fail = false;
				break;
			case ";":
				fail = false;
				break;
			}
			if (nextWord.matches("\\$[A-Za-z][A-Za-z0-9]*")) {
				if (!scan.next().trim().equals("=")) {
					fail("Not an equals sign: " + nextWord, scan);
				}
				VariableExeNode temp = new VariableExeNode(nextWord,
						parseExpression(scan, prog));
				prog.addNode(temp);
				prog.putVariable(nextWord, temp);
				fail = false;
			}

			if (nextWord.length() == 0)
				fail = false;
			if (fail)
				fail("Invalid code thing: (" + nextWord + ")", scan);
		}

		if (level == 0) {
			return prog;
		} else {
			fail("Invalid code thing :(", scan);
			return null;
		}
	}

	static RobotConditionNode parseCondition(Scanner scan, ProgNode prog) {

		String next = scan.next().trim();
		RobotConditionNode cond = null;

		switch (next) {
		case "and":
			if (!scan.next().equals("("))
				fail("Should be an open bracket: " + next, scan);
			RobotConditionNode a = parseCondition(scan, prog);
			if (!scan.next().equals(","))
				fail("Should be a comma: " + next, scan);
			RobotConditionNode b = parseCondition(scan, prog);
			if (!scan.next().equals(")"))
				fail("Should be a close bracket: " + next, scan);
			cond = new AndNode(a, b);
			break;
		case "or":
			if (!scan.next().equals("("))
				fail("Should be an open bracket: " + next, scan);
			RobotConditionNode c = parseCondition(scan, prog);
			if (!scan.next().equals(","))
				fail("Should be a comma: " + next, scan);
			RobotConditionNode d = parseCondition(scan, prog);
			if (!scan.next().equals(")"))
				fail("Should be a close bracket: " + next, scan);
			cond = new OrNode(c, d);
			break;
		case "not":
			if (!scan.next().equals("("))
				fail("Should be an open bracket: " + next, scan);
			RobotConditionNode e = parseCondition(scan, prog);
			if (!scan.next().equals(")"))
				fail("Should be a close bracket: " + next, scan);
			cond = new NotNode(e);
			break;
		case "lt":
			if (!scan.next().equals("("))
				fail("Should be an open bracket: " + next, scan);
			RobotIntegerNode g = parseExpression(scan, prog);
			RobotIntegerNode h = parseExpression(scan, prog);
			cond = new LTNode(g, h);
			break;
		case "gt":
			if (!scan.next().equals("("))
				fail("Should be an open bracket: " + next, scan);
			RobotIntegerNode i = parseExpression(scan, prog);
			RobotIntegerNode j = parseExpression(scan, prog);
			cond = new GTNode(i, j);
			break;
		case "eq":
			if (!scan.next().equals("("))
				fail("Should be an open bracket: " + next, scan);
			RobotIntegerNode k = parseExpression(scan, prog);
			RobotIntegerNode l = parseExpression(scan, prog);
			cond = new EQNode(k, l);
			break;
		}

		if (cond == null)
			fail("Bad condition, bad: " + next, scan);

		return cond;
	}

	static RobotIntegerNode parseExpression(Scanner scan, ProgNode prog) {

		RobotIntegerNode exp = null;

		String next = scan.next().trim();

		// Skip all of that.
		while (next.equals(",") || next.equals(")") || next.equals("(")) {
			next = scan.next();
		}

		switch (next) {
		case "add":
			RobotIntegerNode a = parseExpression(scan, prog);
			RobotIntegerNode b = parseExpression(scan, prog);
			exp = new AddNode(a, b);
			break;
		case "sub":
			RobotIntegerNode c = parseExpression(scan, prog);
			RobotIntegerNode d = parseExpression(scan, prog);
			exp = new SubNode(c, d);
			break;
		case "mul":
			RobotIntegerNode e = parseExpression(scan, prog);
			RobotIntegerNode f = parseExpression(scan, prog);
			exp = new MulNode(e, f);
			break;
		case "div":
			RobotIntegerNode g = parseExpression(scan, prog);
			RobotIntegerNode h = parseExpression(scan, prog);
			exp = new DivNode(g, h);
			break;
		case "fuelLeft":
		case "fuelLeft;":
			exp = new FuelLeftNode();
			break;
		case "oppLR":
		case "oppLR;":
			exp = new OppLRNode();
			break;
		case "oppFB":
		case "oppFB;":
			exp = new OppFBNode();
			break;
		case "numBarrels":
		case "numBarrels;":
			exp = new NumBarrelsNode();
			break;
		case "barrelLR":
		case "barrelLR;":
			exp = new BarrelLRNode();
			break;
		case "barrelFB":
		case "barrelFB;":
			exp = new BarrelFBNode();
			break;
		case "wallDist":
		case "wallDist;":
			exp = new WallDistNode();
			break;
		}

		if (next.matches("\\$[A-Za-z][A-Za-z0-9]*")) {
			// if (prog.getVariable(next) == null) //TODO uncomment for
			// challenge thing
			// fail("Trying to use variables that have not been declared: "
			// + next, scan);
			exp = prog.getVariable(next).getRIN();
			if (next.equals(")") || next.equals(",")) {
				return exp;
			}
		}

		if (exp != null && next.endsWith(";"))
			return exp;

		if (exp == null && next.endsWith(";")) {
			next = next.substring(0, next.length() - 1);
			if (next.matches("-?[1-9][0-9]*|0")) {
				exp = new NumNode(Integer.parseInt(next));
				return exp;
			}
		}

		if (next.matches("-?[1-9][0-9]*|0")) {
			exp = new NumNode(Integer.parseInt(next));
		}

		if (exp == null) {
			fail("Not a valid expression: " + next, scan);
		}

		next = scan.next();

		if (exp.getClass() == BarrelFBNode.class) {
			if (next.equals("(")) {
				exp = new BarrelFBNode(parseExpression(scan, prog));
				next = scan.next();
				if (next.equals(",") || next.equals(")"))
					return exp;
			} else if (next.equals(",") || next.equals(")")) {
				return exp;
			}
		} else if (exp.getClass() == BarrelLRNode.class) {
			if (next.equals("(")) {
				exp = new BarrelLRNode(parseExpression(scan, prog));
				next = scan.next();
				if (next.equals(",") || next.equals(")"))
					return exp;
			} else if (next.equals(",") || next.equals(")")) {
				return exp;
			}
		} else if (next.equals(")") || next.equals(",") || next.equals(";")) {
			return exp;
		} else
			fail("Invalid " + next, scan);
		return null;
	}

	// utility methods for the parser
	/**
	 * Report a failure in the parser.
	 */
	static void fail(String message, Scanner s) {
		String msg = message + "\n   @ ...";
		for (int i = 0; i < 5 && s.hasNext(); i++) {
			msg += " " + s.next();
		}
		throw new ParserFailureException(msg + "...");
	}

	/**
	 * If the next token in the scanner matches the specified pattern, consume
	 * the token and return true. Otherwise return false without consuming
	 * anything. Useful for dealing with the syntactic elements of the language
	 * which do not have semantic content, and are there only to make the
	 * language parsable.
	 */
	static boolean gobble(String p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

	static boolean gobble(Pattern p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

}

// You could add the node classes here, as long as they are not declared public
// (or private)
