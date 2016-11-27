package assignment3;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

public class Main extends GUI {

	Vector3D lightSource, originalLightSource;
	Set<Polygon> polygonSet;
	float scale;
	Vector3D centreVector;
	Transform transform, newLightSource;
	Set<Vector3D> lightSources;

	@Override
	protected void onLoad(File file) {
		transform = Transform.identity();
		lightSources = new HashSet<Vector3D>();

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			String line = fileReader.readLine();

			String[] details = line.split(" ");

			// Standardise light source distance
			originalLightSource = new Vector3D(Float.parseFloat(details[0]),
					Float.parseFloat(details[1]), Float.parseFloat(details[2]));
			lightSource = new Vector3D(originalLightSource.x, originalLightSource.y, originalLightSource.z);

			lightSources.add(lightSource);
			newLightSource = Transform.identity();

			polygonSet = new HashSet<Polygon>();
			Color color;
			Vector3D a, b, c;
			centreMag centre = new centreMag();
			while ((line = fileReader.readLine()) != null) {
				details = line.split(" ");
				a = new Vector3D(Float.parseFloat(details[0]),
						Float.parseFloat(details[1]),
						Float.parseFloat(details[2]));
				b = new Vector3D(Float.parseFloat(details[3]),
						Float.parseFloat(details[4]),
						Float.parseFloat(details[5]));
				c = new Vector3D(Float.parseFloat(details[6]),
						Float.parseFloat(details[7]),
						Float.parseFloat(details[8]));
				color = new Color(Integer.parseInt(details[9]),
						Integer.parseInt(details[10]),
						Integer.parseInt(details[11]));
				centre.addVector(a);
				centre.addVector(b);
				centre.addVector(c);
				polygonSet.add(new Polygon(a, b, c, color, null));
			}
			fileReader.close();
			centreVector = centre.getCentre();
			scale = centre.getMaxFromCentre();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onKeyPress(KeyEvent ev) {

		Transform rotate;
		switch (ev.getKeyCode()) {
		case 27:
			transform = Transform.identity();
		case 37:
		case 65:
			rotate = Transform.newYRotation(0.1f);
			transform = transform.compose(rotate);
			rotate = Transform.newYRotation(-0.1f);
			newLightSource = newLightSource.compose(rotate);
			break;
		case 39:
		case 68:
			rotate = Transform.newYRotation(-0.1f);
			transform = transform.compose(rotate);
			rotate = Transform.newYRotation(0.1f);
			newLightSource = newLightSource.compose(rotate);
			break;
		case 69:
			rotate = Transform.newZRotation(0.1f);
			transform = transform.compose(rotate);
			rotate = Transform.newZRotation(-0.1f);
			newLightSource = newLightSource.compose(rotate);
			break;
		case 81:
			rotate = Transform.newZRotation(-0.1f);
			transform = transform.compose(rotate);
			rotate = Transform.newZRotation(0.1f);
			newLightSource = newLightSource.compose(rotate);
			break;
		case 40:
		case 83:
			rotate = Transform.newXRotation(0.1f);
			transform = transform.compose(rotate);
			rotate = Transform.newXRotation(-0.1f);
			newLightSource = newLightSource.compose(rotate);
			break;
		case 38:
		case 87:
			rotate = Transform.newXRotation(-0.1f);
			transform = transform.compose(rotate);
			rotate = Transform.newXRotation(0.1f);
			newLightSource = newLightSource.compose(rotate);
			break;
		}
	}

	@Override
	protected BufferedImage render() {

		int ambientRed = getAmbientLight()[0];
		int ambientGreen = getAmbientLight()[1];
		int ambientBlue = getAmbientLight()[2];


		Set<Vector3D>  spotLight = new HashSet<Vector3D>();
		if (lightSource != null) {
			for (Vector3D v : lightSources) {
				spotLight.add(transform.multiply(v));
			}
		}
		else {
			spotLight.add(new Vector3D(0.0f, 0.0f, 0.0f));
		}

		if (polygonSet != null) {
			centreMag step1CentreMag = new centreMag();

			// Find the centre
			for (Polygon p : polygonSet) {
				step1CentreMag.addVector(p.a);
				step1CentreMag.addVector(p.b);
				step1CentreMag.addVector(p.c);
			}

			// Move everything onto the canvas.
			Set<Polygon> step2Set = new HashSet<Polygon>();

			Vector3D step1Centre = step1CentreMag.getCentre();
			centreMag step2CentreMag = new centreMag();
			for (Polygon p : polygonSet) {
				Vector3D a = new Vector3D(p.a.x + (-step1Centre.x), p.a.y
						+ (-step1Centre.y), p.a.z + (-step1Centre.z));
				Vector3D b = new Vector3D(p.b.x + (-step1Centre.x), p.b.y
						+ (-step1Centre.y), p.b.z + (-step1Centre.z));
				Vector3D c = new Vector3D(p.c.x + (-step1Centre.x), p.c.y
						+ (-step1Centre.y), p.c.z + (-step1Centre.z));

				step2Set.add(new Polygon(transform.multiply(a), transform
						.multiply(b), transform.multiply(c), p.color, p
						.getNormal()));

				step2CentreMag.addVector(a);
				step2CentreMag.addVector(b);
				step2CentreMag.addVector(c);
			}

			// Step 2b remove vectors facing away from kamerah
			Set<Polygon> step2bSet = new HashSet<Polygon>();

			for (Polygon p : step2Set) {
				Vector3D A = new Vector3D(p.b.x - p.a.x, p.b.y - p.a.y, p.b.z
						- p.a.z);
				Vector3D B = new Vector3D(p.c.x - p.a.x, p.c.y - p.a.y, p.c.z
						- p.a.z);

				Float x = (A.y * B.z) - (A.z * B.y);
				Float y = (A.z * B.x) - (A.x * B.z);
				Float z = (A.x * B.y) - (A.y * B.x);

				Vector3D normal = new Vector3D(x, y, z);
				//Vector3D normal = p.b.minus(p.a).crossProduct(p.c.minus(p.b));

				p.setNormal(normal);
				if (normal.z < 0) {
					step2bSet.add(p);
				}

			}

			// Rescale everything so fits on canvas.
			Set<Polygon> step3Set = new HashSet<Polygon>();
			centreMag step3CentreMag = new centreMag();
			Float scale = step2CentreMag.getMaxFromCentre();

			Float costhScale = 0.0f;

			for (Polygon p : step2bSet) {
				Vector3D a = new Vector3D(p.a.x / scale * 400 + 300, p.a.y
						/ scale * 400 + 300, p.a.z / scale * 400 + 300);
				Vector3D b = new Vector3D(p.b.x / scale * 400 + 300, p.b.y
						/ scale * 400 + 300, p.b.z / scale * 400 + 300);
				Vector3D c = new Vector3D(p.c.x / scale * 400 + 300, p.c.y
						/ scale * 400 + 300, p.c.z / scale * 400 + 300);

				step3Set.add(new Polygon(a, b, c, p.color, p.getNormal()));

				step3CentreMag.addVector(a);
				step3CentreMag.addVector(b);
				step3CentreMag.addVector(c);



				Float costh = 0.0f;

				for (Vector3D v : spotLight) {
					costh += p.getNormal().x * v.x + p.getNormal().y
							* v.y + p.getNormal().z * v.z;
				}


				if (Math.abs(costh) > costhScale) {
					costhScale = Math.abs(costh);
				}

			}

			// initialise the canvas

			colorDistance[][] canvas = new colorDistance[600][600];

			for (int x = 0; x < 600; x++) {
				for (int y = 0; y < 600; y++) {
					canvas[x][y] = new colorDistance(Float.MAX_VALUE,
							new Color((ambientRed - 128) / 2 + 128,
									(ambientGreen - 128) / 2 + 128,
									(ambientBlue - 128) / 2 + 128));
				}
			}

			// Paint the canvas
			for (Polygon p : step3Set) {

				Color color;

				Float costh = 0.0f;
				int count = 0;

				for (Vector3D v : spotLight) {
					count++;
					costh += (((p.getNormal().x * v.x
							+ p.getNormal().y * v.y + p.getNormal().z
							* v.z) / costhScale) + 3) / 4;
				}

				costh /= count;

				int red = (int) (p.color.getRed() * costh) + ambientRed - 128;
				int green = (int) (p.color.getGreen() * costh) + ambientGreen
						- 128;
				int blue = (int) (p.color.getBlue() * costh) + ambientBlue
						- 128;
				if (red > 255)
					red = 255;
				if (green > 255)
					green = 255;
				if (blue > 255)
					blue = 255;
				if (red < 0)
					red = 0;
				if (green < 0)
					green = 0;
				if (blue < 0)
					blue = 0;

				color = new Color(red, green, blue);

				int yMin = Math.min((int) p.a.y, (int) p.b.y);
				int yMax = Math.max((int) p.a.y, (int) p.b.y);
				yMin = Math.min(yMin, (int) p.c.y);
				yMax = Math.max(yMax, (int) p.c.y);

				xScan[] yRange = new xScan[600];
				for (int x = 0; x < yRange.length; x++) {
					yRange[x] = new xScan();
				}

				findEdge(p.a, p.b, yRange, canvas);
				findEdge(p.a, p.c, yRange, canvas);
				findEdge(p.b, p.c, yRange, canvas);

				for (int y = yMin; y < yMax; y++) {
					for (int x = (int) yRange[y].getxMin(); x < (int) yRange[y]
							.getxMax(); x++) {
						Float tempZ;

						if ((int) yRange[y].getxMin() + 1 == (int) yRange[y]
								.getxMax()) {
							tempZ = yRange[y].getzMax();
						} else {

							tempZ = yRange[y].getzMin()
									+ (yRange[y].getzMax() - yRange[y]
											.getzMin())
									* (x - yRange[y].getxMin())
									/ (yRange[y].getxMax()
											- yRange[y].getxMin() - 1);
						}

						if (tempZ < canvas[x][y].getZ()) {
							canvas[x][y] = new colorDistance(tempZ, color);

						}

					}
				}
			}

			return convertBitmapToImage(canvas);

		}
		return null;
	}

	private void findEdge(Vector3D p_a, Vector3D p_b, xScan[] yRange,
			colorDistance[][] canvas) {

		float maxY = Math.max(p_a.y, p_b.y);
		float maxX = Math.max(p_a.x, p_b.x);
		float minY = Math.min(p_a.y, p_b.y);
		float minX = Math.min(p_a.x, p_b.x);
		float angle = ((maxX - minX) / (maxY - minY));
		float zDiff = p_b.z - p_a.z;

		for (int y = 0; y < (int) maxY - (int) minY; y++) {
			int t = y + (int) minY;
			int x, z;
			if (p_a.y < p_b.y) {
				if (p_a.x > p_b.x) {
					z = (int) (p_a.z + zDiff * (y / (maxY - minY))); // fine
					x = (int) maxX - (int) (y * angle); // fine
				} else {
					z = (int) (p_a.z + zDiff * (y / (maxY - minY))); // fine
					x = (int) minX + (int) (y * angle); // fine
				}
			} else {
				if (p_a.x > p_b.x) {
					z = (int) (p_b.z - zDiff * (y / (maxY - minY)));
					x = (int) minX + (int) (y * angle); // fine
				} else {
					z = (int) (p_b.z - zDiff * (y / (maxY - minY)));
					x = (int) maxX - (int) (y * angle); // fine
				}

			}

			if (x >= 0 && x <= 599) {
				if (t >= 0 && t <= 599) {
					if (x < yRange[t].getxMin()) {
						yRange[t].setxMin(x);
						yRange[t].setzMin(z);
					}
					if (x > yRange[t].getxMax()) {
						yRange[t].setxMax(x);
						yRange[t].setzMax(z);
					}

				}
			}
		}

	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage.
	 */
	private BufferedImage convertBitmapToImage(colorDistance[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getColor().getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Main();
	}

	@Override
	protected void onAddLightSource() {

		lightSources.add(newLightSource.multiply(new Vector3D(0.0f, 0.0f, -1.0f)));
		System.out.println(lightSources.toString());
		redraw();

	}

	@Override
	protected void resetLightSource() {
		lightSources.clear();
		lightSources.add(originalLightSource);
		redraw();
	}

}
