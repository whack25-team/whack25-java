public class Prodgen {
	public static WorldElement[][] generateWorld(int width,int height) {
		WorldElement[] worldarray = new WorldElement[width][height];
		for (int x = 0; x < width; x++) {
			worldarray[x] = new WorldElement[height];
			for (int y = 0; y < height; y++) {
				worldarray[x][y] = new WorldElement(false,0,0);
			}
		}

		return worldarray;
	}

	public static boolean colliderectwithworldarray(WorldElement[][] worldarray,
			int x,int y,int w,int h) {
		for (int xi = x; xi < x + w; xi++) {
			if (xi >= 0 && xi < worldarray.length) {
				for (int yi = y; yi < yi + height; yi++) {
					if ((yi >= 0 && yi < worldarray[0].length) && worldarray[xi][yi].solid) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
