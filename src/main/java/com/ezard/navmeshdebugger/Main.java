package com.ezard.navmeshdebugger;

import com.ezard.navmeshdebugger.NavMesh.NavPoint;
import com.ezard.navmeshdebugger.NavMesh.NavPoint.NavPointType;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Main extends JFrame {
	Point mouse = new Point();
	NavPoint[] path = new NavPoint[0];
	static JFrame This;
	NavPoint start;
	String temp = "";

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		This = this;
		MapManager.loadMap(getClass().getClassLoader().getResourceAsStream("map.lsmap"));
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == ' ') {
					repaint();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mouse = e.getPoint();
				for (int x = 0; x < NavMesh.navPoints.length; x++) {
					for (int y = 0; y < NavMesh.navPoints[x].length; y++) {
						if (Vector2.distance(e.getX(), e.getY(), x * 10 + 5, y * 10 + 55) < 5) {
							if (NavMesh.navPoints[x][y].type != NavPointType.NONE) {
								if (start == null) {
									start = NavMesh.navPoints[x][y];
									repaint();
								} else {
									long start = System.currentTimeMillis();
									path = NavMesh.getPath(Main.this.start, NavMesh.navPoints[x][y]);
									System.out.println(System.currentTimeMillis() - start);
									repaint();
									Main.this.start = null;
								}
							}
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {

			}

			@Override
			public void mouseMoved(MouseEvent e) {
				for (int x = 0; x < NavMesh.navPoints.length; x++) {
					for (int y = 0; y < NavMesh.navPoints[x].length; y++) {
						if (Vector2.distance(e.getX(), e.getY(), x * 10 + 5, y * 10 + 55) < 5) {
							if (NavMesh.navPoints[x][y].type != NavPointType.NONE) {
								temp = NavMesh.navPoints[x][y].toString();
							}
						}
					}
				}
				repaint();
			}
		});
		setFocusable(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	BufferedImage image;

	@Override
	public void paint(Graphics graphics) {
		if (image == null) image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		for (int x = 0; x < MapManager.solidityMap.length; x++) {
			for (int y = 0; y < MapManager.solidityMap[x].length; y++) {
				if (MapManager.solidityMap[x][y] == 1) {
					g.fillRect(x * 10, y * 10 + 50, 10, 10);
				}
			}
		}
		for (int x = 0; x < NavMesh.navPoints.length; x++) {
			for (int y = 0; y < NavMesh.navPoints[x].length; y++) {
				if (NavMesh.navPoints[x][y].type != NavPointType.NONE) {
					switch (NavMesh.navPoints[x][y].type) {
						case PLATFORM:
							g.setColor(Color.RED);
							break;
						case LEFT_EDGE:
							g.setColor(Color.PINK);
							break;
						case RIGHT_EDGE:
							g.setColor(Color.PINK);
							break;
						case SOLO:
							g.setColor(Color.ORANGE);
							break;
					}
					if (NavMesh.navPoints[x][y] == start || Vector2.distance(mouse.x, mouse.y, x * 10 + 5, y * 10 + 55) < 5) {
						g.setColor(Color.YELLOW);
					}
					g.fillOval(x * 10, y * 10 + 50, 10, 10);
				}
			}
		}
		for (int x = 0; x < NavMesh.navPoints.length; x++) {
			for (int y = 0; y < NavMesh.navPoints[x].length; y++) {
				for (NavPoint.Link l : NavMesh.navPoints[x][y].links) {
					switch (l.linkType) {
						case WALK:
							g.setColor(Color.GREEN);
							g.drawLine(x * 10 + 5, y * 10 + 55, (int) ((l.target.position.x / 100) * 10 + 5), (int) ((l.target.position.y / 100) * 10 + 55));
							break;
						case FALL:
							g.setColor(Color.BLUE);
							if (l.target.position.y < NavMesh.navPoints[x][y].position.y)
								drawArrow(g, (int) ((l.target.position.x / 100) * 10 + 5), (int) ((l.target.position.y / 100) * 10 + 55), x * 10 + 5, y * 10 + 55);
							break;
						case JUMP:
//							g.setColor(Color.DARK_GRAY);
							g.setColor(Color.BLUE);
							if (l.target.position.y > NavMesh.navPoints[x][y].position.y)
								drawArrow(g, (int) ((l.target.position.x / 100) * 10 + 5), (int) ((l.target.position.y / 100) * 10 + 55), x * 10 + 5, y * 10 + 55);
							break;
					}
				}
			}
		}
		g.setColor(Color.MAGENTA);
		for (int i = 0; i < path.length - 1; i++) {
			if (path[i] != null) g.drawLine((int) ((path[i].position.x / 100) * 10 + 5), (int) ((path[i].position.y / 100) * 10 + 55), (int) ((path[i + 1].position.x / 100) * 10 + 5),
					(int) ((path[i + 1].position.y / 100) * 10 + 55));
		}
		g.setColor(Color.BLACK);
		g.drawString(temp, 50, 50);
		graphics.drawImage(image, 0, 0, this);
	}

	private void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) g1.create();
		g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);

		// Draw horizontal arrow starting in (0, 0)
		g.drawLine(0, 0, len, 0);
		g.fillPolygon(new int[]{len, len - 4, len - 4, len}, new int[]{0, -4, 4, 0}, 4);
	}
}
