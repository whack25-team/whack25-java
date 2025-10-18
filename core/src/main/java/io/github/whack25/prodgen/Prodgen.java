import we.WorldElement;

public class Prodgen {
	public static WorldElement[][] generateWorld(int width,int height) {
		WorldElement[][] worldarray = new WorldElement[width][height];
		for (int x = 0; x < width; x++) {
			worldarray[x] = new WorldElement[height];
			for (int y = 0; y < height; y++) {
				worldarray[x][y] = new WorldElement(false,0,0);
			}
		}

		return worldarray;
	}

	public static void addrect(WorldElement[][] worldarray,
			int x,int y,int w,int h,
			boolean solid,double windx,double windy) {

		for (int xi = x; xi < x + w; xi++) {
			if (xi >= 0 && xi < worldarray.length) {
				for (int yi = y; yi < yi + h; yi++) {
					if ((yi >= 0 && yi < worldarray[0].length) && worldarray[xi][yi].solid) {
						worldarray[xi][yi].solid = solid;
						worldarray[xi][yi].windx = windx;
						worldarray[xi][yi].windy = windy;
					}
				}
			}
		}
	}

	public static void addrandomrect(WorldElement[][] worldarray,
			int numrects,int minx,int rangex,int miny,int rangey,int minw,int rangew,int minh,int rangeh,
			double solidchance,	// If Math.random() larger than this, is soild
			double minwindx,double rangewindx,double minwindy,double rangewindy) {
		for (int i = 0; i < numrects; i++) {
			addrect(worldarray,minx + (int) ((float) rangex * Math.random()),
					miny + (int) ((float) rangey * Math.random()),
					minw + (int) ((float) rangew * Math.random()),
					minh + (int) ((float) rangeh * Math.random()),
					Math.random() > solidchance,
					minwindx + (int) ((float) rangewindx * Math.random()),
					minwindy + (int) ((float) rangewindy * Math.random()));
		}
	}

	public static boolean colliderectwithworldarray(WorldElement[][] worldarray,
			int x,int y,int w,int h) {
		for (int xi = x; xi < x + w; xi++) {
			if (xi >= 0 && xi < worldarray.length) {
				for (int yi = y; yi < yi + h; yi++) {
					if ((yi >= 0 && yi < worldarray[0].length) && worldarray[xi][yi].solid) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void printsolid(WorldElement[][] worldarray) {
		for (int x = 0; x < worldarray.length; x++) {
			for (int y = 0; y < worldarray[0].length; y++) {
				System.out.print(worldarray[x][y].solid ? "S" : " ");
			}
			System.out.print("\n");
		}
	}
}
