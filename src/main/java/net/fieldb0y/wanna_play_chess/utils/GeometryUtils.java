package net.fieldb0y.wanna_play_chess.utils;

public class GeometryUtils {
    public static boolean isPointInQuadrilateral(double px, double py, double[] xCoords, double[] yCoords) {
        if (xCoords.length != 4 || yCoords.length != 4) {
            throw new IllegalArgumentException("Requires exactly 4 quadrilateral vertices");
        }

        int intersectCount = 0;
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            double x1 = xCoords[i];
            double y1 = yCoords[i];
            double x2 = xCoords[next];
            double y2 = yCoords[next];

            if (rayIntersectsSegment(px, py, x1, y1, x2, y2)) {
                intersectCount++;
            }
        }

        return (intersectCount % 2) == 1;
    }

    private static boolean rayIntersectsSegment(double px, double py, double x1, double y1, double x2, double y2) {
        if (y1 > y2) {
            double tmp = y1;
            y1 = y2;
            y2 = tmp;
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        if (py < y1 || py > y2) return false;
        if (px > Math.max(x1, x2)) return false;

        if (py == y1 || py == y2) {
            py += 0.0001;
        }

        double xIntersect;
        if (x1 == x2) {
            xIntersect = x1;
        } else {
            double slope = (x2 - x1) / (y2 - y1);
            xIntersect = x1 + (py - y1) * slope;
        }

        if (px > xIntersect) return false;
        return !(xIntersect == x1 && py == y1) && !(xIntersect == x2 && py == y2);
    }
}
