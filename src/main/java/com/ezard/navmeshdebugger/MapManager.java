package com.ezard.navmeshdebugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapManager {
	public static int[][] solidityMap;

	public static void loadMap(InputStream in) {
		try {
			String temp;
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while ((temp = reader.readLine()) != null) {
				if (temp.equals("BEGIN METADATA")) readInMetaData(reader);
				if (temp.equals("BEGIN BLOCKS")) readInBlocks(reader);
				if (temp.equals("BEGIN DOORS")) readInDoors(reader);
			}
		} catch (IOException ignored) {
		}

		NavMesh.init(solidityMap);
	}

	private static void readInMetaData(BufferedReader reader) throws IOException {
		int width = 0;
		int height = 0;
		String temp;
		while ((temp = reader.readLine()) != null && !temp.equals("END METADATA")) {
			String[] split = temp.split("=");
			if (split[0].equals("map_width")) {
				width = Integer.parseInt(split[1]);
			} else if (split[0].equals("map_height")) {
				height = Integer.parseInt(split[1]);
			}
		}
		solidityMap = new int[width][height];
	}

	private static void readInBlocks(BufferedReader reader) throws IOException {
		String temp;
		while ((temp = reader.readLine()) != null && !temp.equals("END BLOCKS")) {
			String[] split = temp.split(",");
			float x = Float.valueOf(split[0]) * 3.125f;
			float y = Float.valueOf(split[1]) * 3.125f;
			solidityMap[((int) (x / 100))][((int) (y / 100))] = Boolean.valueOf(split[5]) ? 1 : 0;
		}
	}

	private static void readInDoors(BufferedReader reader) throws IOException {
		String temp;
		while ((temp = reader.readLine()) != null && !temp.equals("END DOORS")) {
			String[] split = temp.split(",");
			float x = Float.valueOf(split[0]) * 3.125f;
			float y = Float.valueOf(split[1]) * 3.125f;
			solidityMap[((int) (x / 100))][((int) (y / 100))] = 1;
			solidityMap[((int) (x / 100)) + 1][((int) (y / 100))] = 1;
			solidityMap[((int) (x / 100))][((int) (y / 100)) + 4] = 1;
			solidityMap[((int) (x / 100)) + 1][((int) (y / 100)) + 4] = 1;
		}
	}
}
