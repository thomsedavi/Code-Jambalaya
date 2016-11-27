interface RobotConditionNode {
	public boolean execute(Robot robot);
}

class AndNode implements RobotConditionNode {

	private RobotConditionNode A, B;

	public AndNode(RobotConditionNode A, RobotConditionNode B) {
		this.A = A;
		this.B = B;
	}

	@Override
	public boolean execute(Robot robot) {
		return A.execute(robot) && B.execute(robot);
	}

	public String toString() {
		return "and(" + A.toString() + "," + B.toString() + ")";
	}

	public String tabString(String tab) {
		return "and(" + A.toString() + "," + B.toString() + ")";
	}
}

class OrNode implements RobotConditionNode {

	private RobotConditionNode A, B;

	public OrNode(RobotConditionNode A, RobotConditionNode B) {
		this.A = A;
		this.B = B;
	}

	@Override
	public boolean execute(Robot robot) {
		return A.execute(robot) || B.execute(robot);
	}

	public String toString() {
		return "or(" + A.toString() + "," + B.toString() + ")";
	}

	public String tabString(String tab) {
		return "or(" + A.toString() + "," + B.toString() + ")";
	}
}

class NotNode implements RobotConditionNode {

	private RobotConditionNode A;

	public NotNode(RobotConditionNode A) {
		this.A = A;
	}

	@Override
	public boolean execute(Robot robot) {
		return !A.execute(robot);
	}

	public String toString() {
		return "not(" + A.toString() + ")";
	}

	public String tabString(String tab) {
		return "not(" + A.toString() + ")";
	}
}

class LTNode implements RobotConditionNode {

	private RobotIntegerNode sensor, value;

	public LTNode(RobotIntegerNode sensor, RobotIntegerNode value) {
		this.sensor = sensor;
		this.value = value;
	}

	@Override
	public boolean execute(Robot robot) {
		return sensor.execute(robot) < value.execute(robot);
	}

	public String toString() {
		return "lt(" + sensor.toString() + "," + value.toString() + ")";
	}

	public String tabString(String tab) {
		return "lt(" + sensor.toString() + "," + value.toString() + ")";
	}
}

class GTNode implements RobotConditionNode {

	private RobotIntegerNode sensor, value;

	public GTNode(RobotIntegerNode sensor, RobotIntegerNode value) {
		this.sensor = sensor;
		this.value = value;
	}

	@Override
	public boolean execute(Robot robot) {
		return sensor.execute(robot) > value.execute(robot);
	}

	public String toString() {
		return "gt(" + sensor.toString() + "," + value.toString() + ")";
	}

	public String tabString(String tab) {
		return "gt(" + sensor.toString() + "," + value.toString() + ")";
	}
}

class EQNode implements RobotConditionNode {

	private RobotIntegerNode sensor, value;

	public EQNode(RobotIntegerNode sensor, RobotIntegerNode value) {
		this.sensor = sensor;
		this.value = value;
	}

	@Override
	public boolean execute(Robot robot) {
		return sensor.execute(robot) == value.execute(robot);
	}

	public String toString() {
		return "eq(" + sensor.toString() + "," + value.toString() + ")";
	}

	public String tabString(String tab) {
		return "eq(" + sensor.toString() + "," + value.toString() + ")";
	}
}

class TrueNode implements RobotConditionNode {

	@Override
	public boolean execute(Robot robot) {
		return true;
	}

	public String toString() {
		return "true";
	}
}