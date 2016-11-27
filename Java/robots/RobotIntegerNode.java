/**
 * Interface for all nodes that can be executed, including the top level program
 * node
 */

interface RobotIntegerNode {
	public int execute(Robot robot);
}

class FuelLeftNode implements RobotIntegerNode {

	@Override
	public int execute(Robot robot) {
		return robot.getFuel();
	}

	public String toString() {
		return "fuelLeft";
	}
}

class OppLRNode implements RobotIntegerNode {

	@Override
	public int execute(Robot robot) {
		return robot.getOpponentLR();
	}

	public String toString() {
		return "OppLR";
	}
}

class OppFBNode implements RobotIntegerNode {

	@Override
	public int execute(Robot robot) {
		return robot.getOpponentFB();
	}

	public String toString() {
		return "OppFB";
	}
}

class NumBarrelsNode implements RobotIntegerNode {

	@Override
	public int execute(Robot robot) {
		return robot.numBarrels();
	}

	public String toString() {
		return "numBarrels";
	}
}

class BarrelLRNode implements RobotIntegerNode {

	RobotIntegerNode exp;

	BarrelLRNode() {
	}

	BarrelLRNode(RobotIntegerNode exp) {
		this.exp = exp;
	}

	@Override
	public int execute(Robot robot) {
		if (exp == null) {
			return robot.getClosestBarrelLR();
		} else {
			return robot.getBarrelLR(exp.execute(robot));
		}
	}

	public String toString() {
		if (exp == null) {
			return "barrelLR";
		} else {
			return "barrelLR(" + exp.toString() + ")";
		}
	}
}

class BarrelFBNode implements RobotIntegerNode {

	RobotIntegerNode exp;

	BarrelFBNode() {
	}

	BarrelFBNode(RobotIntegerNode exp) {
		this.exp = exp;
	}

	@Override
	public int execute(Robot robot) {
		if (exp == null) {
			return robot.getClosestBarrelFB();
		} else {
			return robot.getBarrelFB(exp.execute(robot));
		}
	}

	public String toString() {
		if (exp == null) {
			return "barrelFB";
		} else {
			return "barrelFB(" + exp.toString() + ")";
		}
	}
}

class WallDistNode implements RobotIntegerNode {

	@Override
	public int execute(Robot robot) {
		return robot.getDistanceToWall();
	}

	public String toString() {
		return "wallDist";
	}
}

class AddNode implements RobotIntegerNode {

	RobotIntegerNode A, B;

	public AddNode(RobotIntegerNode A, RobotIntegerNode B) {
		this.A = A;
		this.B = B;
	}

	@Override
	public int execute(Robot robot) {
		return A.execute(robot) + B.execute(robot);
	}

	public String toString() {
		return "add(" + A.toString() + "," + B.toString() + ")";
	}
}

class SubNode implements RobotIntegerNode {

	RobotIntegerNode A, B;

	public SubNode(RobotIntegerNode A, RobotIntegerNode B) {
		this.A = A;
		this.B = B;
	}

	@Override
	public int execute(Robot robot) {
		return A.execute(robot) - B.execute(robot);
	}

	public String toString() {
		return "sub(" + A.toString() + "," + B.toString() + ")";
	}
}

class MulNode implements RobotIntegerNode {

	RobotIntegerNode A, B;

	public MulNode(RobotIntegerNode A, RobotIntegerNode B) {
		this.A = A;
		this.B = B;
	}

	@Override
	public int execute(Robot robot) {
		return A.execute(robot) * B.execute(robot);
	}

	public String toString() {
		return "mul(" + A.toString() + "," + B.toString() + ")";
	}
}

class DivNode implements RobotIntegerNode {

	RobotIntegerNode A, B;

	public DivNode(RobotIntegerNode A, RobotIntegerNode B) {
		this.A = A;
		this.B = B;
	}

	@Override
	public int execute(Robot robot) {
		return A.execute(robot) / B.execute(robot);
	}

	public String toString() {
		return "div(" + A.toString() + "," + B.toString() + ")";
	}
}

class NumNode implements RobotIntegerNode {

	private int num;

	public NumNode(Integer num) {
		this.num = num;
	}

	@Override
	public int execute(Robot robot) {
		return num;
	}

	public String toString() {
		return String.valueOf(num);
	}
}

class VariableValueNode implements RobotIntegerNode {

	String name;
	RobotIntegerNode exp;

	public VariableValueNode(String name, RobotIntegerNode exp) {
		this.name = name;
		this.exp = exp;
	}

	@Override
	public int execute(Robot robot) {
		return exp.execute(robot);
	}

	public String toString() {
		return name;
	}

}