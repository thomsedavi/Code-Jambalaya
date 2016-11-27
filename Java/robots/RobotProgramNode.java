import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Interface for all nodes that can be executed, including the top level program
 * node
 */

interface RobotProgramNode {

	public void execute(Robot robot);

	public String tabString(String tab);
}

class ProgNode implements RobotProgramNode {

	private List<RobotProgramNode> prog;
	Map<String, VariableExeNode> variables = new HashMap<String, VariableExeNode>();

	public ProgNode() {
		prog = new LinkedList<RobotProgramNode>();
	}

	public void addNode(RobotProgramNode rpn) {
		prog.add(rpn);
	}

	@Override
	public void execute(Robot robot) {
		for (RobotProgramNode rpn : prog) {
			rpn.execute(robot);
		}
	}

	public String toString() {
		String str = "";
		for (RobotProgramNode rpn : prog) {
			str = str + rpn.tabString("") + "\n";
		}
		return str;
	}

	public String tabString(String tab) {
		String result = "";
		for (RobotProgramNode rpn : prog) {
			result = result + rpn.tabString(tab) + "\n";
		}
		return result;
	}

	public void putVariable(String str, VariableExeNode rin) {
		variables.put(str, rin);
	}

	public VariableExeNode getVariable(String str) {
		if (!variables.containsKey(str))
			 return new VariableExeNode(str, new NumNode(0)); //TODO comment for completion.
			//return null; //TODO uncomment for completion
		else
			return variables.get(str);
	}

	public Map<String, VariableExeNode> getVariables() {
		return variables;
	}
}

class MoveNode implements RobotProgramNode {

	RobotIntegerNode num;

	public MoveNode() {
	}

	public MoveNode(RobotIntegerNode num) {
		this.num = num;
	}

	@Override
	public void execute(Robot robot) {
		if (num != null) {
			int y = num.execute(robot);
			for (int x = 0; x < y; x++) {
				robot.move();
			}

		} else {
			robot.move();
		}
	}

	public String toString() {
		if (num != null) {
			return "move(" + num.toString() + ");";
		} else {
			return "move;";
		}
	}

	public String tabString(String tab) {
		if (num != null) {
			return tab + "move(" + num.toString() + ");";
		} else {
			return tab + "move;";
		}
	}
}

class TurnLNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.turnLeft();
	}

	public String toString() {
		return "turnL;";
	}

	public String tabString(String tab) {
		return tab + "turnL;";
	}
}

class TurnRNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.turnRight();
	}

	public String toString() {
		return "turnR;";
	}

	public String tabString(String tab) {
		return tab + "turnR;";
	}
}

class TurnAroundNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.turnAround();
	}

	public String toString() {
		return "turnAround;";
	}

	public String tabString(String tab) {
		return tab + "turnAround;";
	}
}

class ShieldOnNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.setShield(true);
	}

	public String toString() {
		return "shieldOn;";
	}

	public String tabString(String tab) {
		return tab + "shieldOn;";
	}
}

class ShieldOffNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.setShield(false);
	}

	public String toString() {
		return "shieldOff;";
	}

	public String tabString(String tab) {
		return tab + "shieldOff;";
	}
}

class WaitNode implements RobotProgramNode {

	RobotIntegerNode num;

	public WaitNode() {
	}

	public WaitNode(RobotIntegerNode num) {
		this.num = num;
	}

	@Override
	public void execute(Robot robot) {
		if (num != null) {
			int y = num.execute(robot);
			for (int x = 0; x < y; x++) {
				robot.idleWait();
			}

		} else {
			robot.idleWait();
		}
	}

	public String toString() {
		if (num != null) {
			return "wait(" + num.toString() + ");";
		} else {
			return "wait;";
		}
	}

	public String tabString(String tab) {
		if (num != null) {
			return tab + "wait(" + num.toString() + ");";
		} else {
			return tab + "wait;";
		}
	}
}

class TakeFuelNode implements RobotProgramNode {

	@Override
	public void execute(Robot robot) {
		robot.takeFuel();
	}

	public String toString() {
		return "takeFuel;";
	}

	public String tabString(String tab) {
		return tab + "takeFuel;";
	}
}

class LoopNode implements RobotProgramNode {

	private RobotProgramNode block;

	public LoopNode(RobotProgramNode block) {
		this.block = block;
	}

	public void execute(Robot robot) {
		do {
			block.execute(robot);
		} while (true);
	}

	public String toString() {
		return "loop{\n" + block.toString() + "}\n";
	}

	public String tabString(String tab) {
		return tab + "loop{\n" + block.tabString("  " + tab) + "}\n";
	}
}

class IfElifElseNode implements RobotProgramNode {

	private RobotConditionNode cond;
	private ProgNode block;
	List<IfElifElseNode> ifNodes;

	public IfElifElseNode(RobotConditionNode cond, ProgNode block) {
		this.cond = cond;
		this.block = block;
		ifNodes = new LinkedList<IfElifElseNode>();
	}

	public void addIfNode(IfElifElseNode ifNode) {
		ifNodes.add(ifNode);
	}

	@Override
	public void execute(Robot robot) {
		if (cond.execute(robot)) {
			block.execute(robot);
		} else {
			boolean pass = false;
			int x = 0;
			while (pass == false && x < ifNodes.size()) {
				if (ifNodes.get(x).isTrue(robot)) {
					ifNodes.get(x).execute(robot);
					pass = true;
				}
				x++;
			}
		}
	}

	public boolean isTrue(Robot robot) {
		return cond.execute(robot);
	}

	public String toString() {
		String result = "";
		if (cond.getClass() == TrueNode.class) {
			result = "{\n" + block.tabString("  ") + "}";
		} else {
			result = "if(" + cond.toString() + "){\n" + block.tabString("  ")
					+ "}";
		}
		for (int x = 0; x < ifNodes.size(); x++) {
			result = result + "else\n" + ifNodes.get(x).toString();
		}
		return result;
	}

	public String tabString(String tab) {
		String result = "";
		if (cond.getClass() == TrueNode.class) {
			result = tab + "{\n" + block.tabString("  " + tab) + tab + "}";
		} else {
			result = tab + "if(" + cond.toString() + "){\n"
					+ block.tabString("  " + tab) + tab + "}";
		}
		for (int x = 0; x < ifNodes.size(); x++)
			result = result + "else\n" + ifNodes.get(x).tabString("  " + tab);
		return result;
	}
}

class WhileNode implements RobotProgramNode {

	private RobotConditionNode cond;
	private RobotProgramNode block;

	public WhileNode(RobotConditionNode cond, RobotProgramNode block) {
		this.cond = cond;
		this.block = block;
	}

	@Override
	public void execute(Robot robot) {
		while (cond.execute(robot)) {
			block.execute(robot);
		}
	}

	public String toString() {
		return "while(" + cond.toString() + "{\n" + block.tabString("  ") + "}";
	}

	public String tabString(String tab) {
		if (cond == null) {
		}
		return tab + "while(" + cond.toString() + "{\n"
				+ block.tabString("  " + tab) + tab + "}";
	}
}

class VariableExeNode implements RobotProgramNode {

	String name;
	RobotIntegerNode exp;
	ProgNode prog;

	public VariableExeNode(String name, RobotIntegerNode exp) {
		this.name = name;
		this.exp = exp;
	}

	@Override
	public void execute(Robot robot) {
		exp = new NumNode(exp.execute(robot));
	}

	public RobotIntegerNode getRIN() {
		return new VariableValueNode(name, exp);
	}

	public String toString() {
		return name + " = " + exp.toString();
	}

	@Override
	public String tabString(String tab) {
		return tab + name + " = " + exp.toString();
	}

}
