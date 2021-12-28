package eu.hansolo.toolboxfx;

import eu.hansolo.toolbox.Helper;
import eu.hansolo.toolbox.Statistics;
import eu.hansolo.toolbox.tuples.Pair;
import eu.hansolo.toolboxfx.geom.Bounds;
import eu.hansolo.toolboxfx.geom.CardinalDirection;
import eu.hansolo.toolboxfx.geom.CatmullRom;
import eu.hansolo.toolboxfx.geom.CornerRadii;
import eu.hansolo.toolboxfx.geom.Dimension;
import eu.hansolo.toolboxfx.geom.Point;
import eu.hansolo.toolboxfx.geom.Position;
import eu.hansolo.toolboxfx.geom.QuickHull;
import eu.hansolo.toolboxfx.geom.Rectangle;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static eu.hansolo.toolbox.Constants.EPSILON;
import static eu.hansolo.toolbox.Helper.clamp;
import static eu.hansolo.toolbox.Helper.round;


public class HelperFX {

    public static final double nearest(final double smaller, final double value, final double larger) {
        return (value - smaller) < (larger - value) ? smaller : larger;
    }

    public static final double[] calcAutoScale(final double minValue, final double maxValue) {
        return calcAutoScale(minValue, maxValue, 10, 10);
    }
    public static final double[] calcAutoScale(final double minValue, final double maxValue, final double maxNoOfMajorTicks, final double maxNoOfMinorTicks) {
        final double niceRange      = (calcNiceNumber((maxValue - minValue), false));
        final double majorTickSpace = calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true);
        final double niceMinValue   = (Math.floor(minValue / majorTickSpace) * majorTickSpace);
        final double niceMaxValue   = (Math.ceil(maxValue / majorTickSpace) * majorTickSpace);
        final double minorTickSpace = calcNiceNumber(majorTickSpace / (maxNoOfMinorTicks - 1), true);
        return new double[]{ niceMinValue, niceMaxValue, majorTickSpace, minorTickSpace };
    }

    /**
     * Can be used to implement discrete steps e.g. on a slider.
     * @param minValue          The min value of the range
     * @param maxValue          The max value of the range
     * @param value             The value to snap
     * @param newMinorTickCount The number of ticks between 2 major tick marks
     * @param newMajorTickUnit  The distance between 2 major tick marks
     * @return The value snapped to the next tick mark defined by the given parameters
     */
    public static final double snapToTicks(final double minValue, final double maxValue, final double value, final int newMinorTickCount, final double newMajorTickUnit) {
        double v = value;

        final int    minorTickCount = clamp(0, 10, newMinorTickCount);
        final double majorTickUnit  = Double.compare(newMajorTickUnit, 0.0) <= 0 ? 0.25 : newMajorTickUnit;
        final double tickSpacing    = minorTickCount == 0 ? majorTickUnit : majorTickUnit / (Math.max(minorTickCount, 0) + 1);
        final int    prevTick       = (int) ((v - minValue) / tickSpacing);
        final double prevTickValue  = prevTick * tickSpacing + minValue;
        final double nextTickValue  = (prevTick + 1) * tickSpacing + minValue;

        v = nearest(prevTickValue, v, nextTickValue);

        return clamp(minValue, maxValue, v);
    }

    /**
     * Returns a "niceScaling" number approximately equal to the range.
     * Rounds the number if ROUND == true.
     * Takes the ceiling if ROUND = false.
     *
     * @param range the value range (maxValue - minValue)
     * @param round whether to round the result or ceil
     * @return a "niceScaling" number to be used for the value range
     */
    public static final double calcNiceNumber(final double range, final boolean round) {
        double niceFraction;
        final double exponent = Math.floor(Math.log10(range));   // exponent of range
        final double fraction = range / Math.pow(10, exponent);  // fractional part of range

        if (round) {
            if (Double.compare(fraction, 1.5) < 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 3)  < 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 7) < 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (Double.compare(fraction, 1) <= 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 2) <= 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 5) <= 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    public static final List<Point> subdividePoints(final List<Point> points, final int subDevisions) {
        return Arrays.asList(subdividePoints(points.toArray(new Point[0]), subDevisions));
    }
    public static final Point[] subdividePoints(final Point[] points, final int subDevisions) {
        assert points != null;
        assert points.length >= 3;
        int    noOfPoints = points.length;

        Point[] subdividedPoints = new Point[((noOfPoints - 1) * subDevisions) + 1];

        double increments = 1.0 / (double) subDevisions;

        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            Point p0 = i == 0 ? points[i] : points[i - 1];
            Point p1 = points[i];
            Point p2 = points[i + 1];
            Point p3 = (i+2 == noOfPoints) ? points[i + 1] : points[i + 2];

            CatmullRom crs = new CatmullRom(p0, p1, p2, p3);

            for (int j = 0; j <= subDevisions; j++) {
                subdividedPoints[(i * subDevisions) + j] = crs.q(j * increments);
            }
        }

        return subdividedPoints;
    }

    public static final List<Point> subdividePointsRadial(final List<Point> points, final int subDevisions) {
        return Arrays.asList(subdividePointsRadial(points.toArray(new Point[0]), subDevisions));
    }
    public static final Point[] subdividePointsRadial(final Point[] points, final int subDivisions){
        assert points != null;
        assert points.length >= 3;
        int    noOfPoints = points.length;

        Point[] subdividedPoints = new Point[((noOfPoints - 1) * subDivisions) + 1];

        double increments = 1.0 / (double) subDivisions;

        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            Point p0 = i == 0 ? points[noOfPoints - 2] : points[i - 1];
            Point p1 = points[i];
            Point p2 = points[i + 1];
            Point p3 = (i == (noOfPoints - 2)) ? points[1] : points[i + 2];

            CatmullRom<Point> crs = new CatmullRom<>(p0, p1, p2, p3);

            for (int j = 0 ; j <= subDivisions ; j++) {
                subdividedPoints[(i * subDivisions) + j] = crs.q(j * increments);
            }
        }

        return subdividedPoints;
    }

    public static final List<Point> subdividePointsLinear(final List<Point> points, final int subDevisions) {
        return Arrays.asList(subdividePointsLinear(points.toArray(new Point[0]), subDevisions));
    }
    public static final Point[] subdividePointsLinear(final Point[] points, final int subDivisions) {
        assert  points != null;
        assert  points.length >= 3;

        final int     noOfPoints       = points.length;
        final Point[] subdividedPoints = new Point[((noOfPoints - 1) * subDivisions) + 1];
        final double  stepSize         = (points[1].getX() - points[0].getX()) / subDivisions;
        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            for (int j = 0 ; j <= subDivisions ; j++) {
                subdividedPoints[(i * subDivisions) + j] = calcIntermediatePoint(points[i], points[i+1], stepSize * j);
            }
        }
        return subdividedPoints;
    }

    public static final Point calcIntermediatePoint(final Point leftPoint, final Point rightPoint, final double intervalX) {
        double m = (rightPoint.getY() - leftPoint.getY()) / (rightPoint.getX() - leftPoint.getX());
        double x = intervalX;
        double y = m * x;
        return new Point(leftPoint.getX() + x, leftPoint.getY() + y);
    }

    public static final Point calcIntersectionOfTwoLines(Point A, Point B, Point C, Point D) {
        return calcIntersectionOfTwoLines(A.getX(), A.getY(), B.getX(), B.getY(), C.getX(), C.getY(), D.getX(), D.getY());
    }
    public static final Point calcIntersectionOfTwoLines(final double X1, final double Y1, final double X2, final double Y2,
                                                         final double X3, final double Y3, final double X4, final double Y4) {

        // Line AB represented as a1x + b1y = c1
        double a1 = Y2 - Y1;
        double b1 = X1 - X2;
        double c1 = a1 * X1 + b1 * Y1;

        // Line CD represented as a2x + b2y = c2
        double a2 = Y4 - Y3;
        double b2 = X3 - X4;
        double c2 = a2 * X3 + b2 * Y3;

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) { // Lines are parallel
            return new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return new Point(x, y);
        }
    }

    public static final Point calcIntersectionPoint(final Point leftPoint, final Point rightPoint, final double intersectionY) {
        double[] xy = calculateIntersectionPoint(leftPoint.getX(), leftPoint.getY(), rightPoint.getX(), rightPoint.getY(), intersectionY);
        return new Point(xy[0], xy[1]);
    }
    public static final double[] calculateIntersectionPoint(final Point leftPoint, final Point rightPoint, final double intersectionY) {
        return calculateIntersectionPoint(leftPoint.getX(), leftPoint.getY(), rightPoint.getX(), rightPoint.getY(), intersectionY);
    }
    public static final double[] calculateIntersectionPoint(final double x1, final double y1, final double x2, final double y2, final double intersectionY) {
        double m = (y2 - y1) / (x2 - x1);
        double interSectionX = (intersectionY - y1) / m;
        return new double[] { x1 + interSectionX, intersectionY };
    }

    public static final Point[] smoothSparkLine(final List<Double> dataList, final double minValue, final double maxValue, final Rectangle graphBounds, final int noOfDatapoints) {
        int     size   = dataList.size();
        Point[] points = new Point[size];

        double low  = Statistics.getMin(dataList);
        double high = Statistics.getMax(dataList);
        if (Helper.equals(low, high)) {
            low  = minValue;
            high = maxValue;
        }
        double range = high - low;

        double minX  = graphBounds.getX();
        double maxX  = minX + graphBounds.getWidth();
        double minY  = graphBounds.getY();
        double maxY  = minY + graphBounds.getHeight();
        double stepX = graphBounds.getWidth() / (noOfDatapoints - 1);
        double stepY = graphBounds.getHeight() / range;

        for (int i = 0 ; i < size ; i++) {
            points[i] = new Point(minX + i * stepX, maxY - Math.abs(low - dataList.get(i)) * stepY);
        }

        return subdividePoints(points, 16);
    }

    public static final boolean isInRectangle(final double x, final double y,
                                              final double minX, final double minY,
                                              final double maxX, final double maxY) {
        return (Double.compare(x, minX) >= 0 &&
                Double.compare(y, minY) >= 0 &&
                Double.compare(x, maxX) <= 0 &&
                Double.compare(y, maxY) <= 0);
    }

    public static final boolean isInCircle(final double x, final double y, final double centerX, final double centerY, final double radius) {
        double deltaX = centerX - x;
        double deltaY = centerY - y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) < radius;
    }

    public static final boolean isInEllipse(final double x, final double y,
                                            final double centerX, final double centerY,
                                            final double radiusX, final double radiusY) {
        return Double.compare(((((x - centerX) * (x - centerX)) / (radiusX * radiusX)) +
                               (((y - centerY) * (y - centerY)) / (radiusY * radiusY))), 1) <= 0.0;
    }

    public static final boolean isInPolygon(final double x, final double y, final List<Point> pointsOfPolygon) {
        int noOfPointsInPolygon = pointsOfPolygon.size();
        double[] pointsX = new double[noOfPointsInPolygon];
        double[] pointsY = new double[noOfPointsInPolygon];
        for ( int i = 0 ; i < noOfPointsInPolygon ; i++) {
            Point p = pointsOfPolygon.get(i);
            pointsX[i] = p.getX();
            pointsY[i] = p.getY();
        }
        return isInPolygon(x, y, noOfPointsInPolygon, pointsX, pointsY);
    }
    public static final boolean isInPolygon(final double x, final double y, final int noOfPointsInPolygon, final double[] pointsX, final double[] pointsY) {
        if (noOfPointsInPolygon != pointsX.length || noOfPointsInPolygon != pointsY.length) { return false; }
        boolean inside = false;
        for (int i = 0, j = noOfPointsInPolygon - 1; i < noOfPointsInPolygon ; j = i++) {
            if (((pointsY[i] > y) != (pointsY[j] > y)) && (x < (pointsX[j] - pointsX[i]) * (y - pointsY[i]) / (pointsY[j] - pointsY[i]) + pointsX[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static final <T extends Point> boolean isPointInPolygon(final T p, final List<T> points) {
        boolean inside     = false;
        int     noOfPoints = points.size();
        double  x          = p.getX();
        double  y          = p.getY();

        for (int i = 0, j = noOfPoints - 1 ; i < noOfPoints ; j = i++) {
            if ((points.get(i).getY() > y) != (points.get(j).getY() > y) &&
                (x < (points.get(j).getX() - points.get(i).getX()) * (y - points.get(i).getY()) / (points.get(j).getY() - points.get(i).getY()) + points.get(i).getX())) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static final boolean isInSector(final double x, final double y, final double centerX, final double centerY, final double radius, final double startAngle, final double segmentAngle) {
        return isInRingSegment(x, y, centerX, centerY, radius, 0, startAngle, segmentAngle);
    }

    public static final boolean isInRingSegment(final double x, final double y,
                                                final double centerX, final double centerY,
                                                final double outerRadius, final double innerRadius,
                                                final double newStartAngle, final double segmentAngle) {
        double angleOffset = 90.0;
        double pointRadius = Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
        double pointAngle  = getAngleFromXY(x, y, centerX, centerY, angleOffset);
        double startAngle  = angleOffset - newStartAngle;
        double endAngle    = startAngle + segmentAngle;

        return (Double.compare(pointRadius, innerRadius) >= 0 &&
                Double.compare(pointRadius, outerRadius) <= 0 &&
                Double.compare(pointAngle, startAngle) >= 0 &&
                Double.compare(pointAngle, endAngle) <= 0);
    }

    public static final boolean isPointOnLine(final Point p, final Point p1, final Point p2) {
        return (distanceFromPointToLine(p, p1, p2) < EPSILON);
    }

    public static final double distanceFromPointToLine(final Point p, final Point p1, final Point p2) {
        double A = p.getX() - p1.getX();
        double B = p.getY() - p1.getY();
        double C = p2.getX() - p1.getX();
        double D = p2.getY() - p1.getY();

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = dot / len_sq;

        double xx, yy;

        if (param < 0 || (p1.getX() == p2.getX() && p1.getY() == p2.getY())) {
            xx = p1.getX();
            yy = p1.getY();
        } else if (param > 1) {
            xx = p2.getX();
            yy = p2.getY();
        } else {
            xx = p1.getX() + param * C;
            yy = p1.getY() + param * D;
        }

        double dx = p.getX() - xx;
        double dy = p.getY() - yy;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public static final <T extends Point> double squareDistance(final T p1, final T p2) {
        return squareDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    public static final double squareDistance(final double x1, final double y1, final double x2, final double y2) {
        double deltaX = (x1 - x2);
        double deltaY = (y1 - y2);
        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public static final double distance(final Point p1, final Point p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);
    }
    public static final double distance(final double p1X, final double p1Y, final double p2X, final double p2Y) {
        return Math.sqrt((p2X - p1X) * (p2X - p1X) + (p2Y - p1Y) * (p2Y - p1Y));
    }

    public static final double euclideanDistance(final Point p1, final Point p2) { return euclideanDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY()); }
    public static final double euclideanDistance(final double x1, final double y1, final double x2, final double y2) {
        double deltaX = (x2 - x1);
        double deltaY = (y2 - y1);
        return (deltaX * deltaX) + (deltaY * deltaY);
    }

    public static final Point pointOnLine(final double p1X, final double p1Y, final double p2X, final double p2Y, final double distanceToP2) {
        double distanceP1P2 = distance(p1X, p1Y, p2X, p2Y);
        double t = distanceToP2 / distanceP1P2;
        return new Point((1 - t) * p1X + t * p2X, (1 - t) * p1Y + t * p2Y);
    }

    public static final int checkLineCircleCollision(final Point p1, final Point p2, final double centerX, final double centerY, final double radius) {
        return checkLineCircleCollision(p1.x, p1.y, p2.x, p2.y, centerX, centerY, radius);
    }
    public static final int checkLineCircleCollision(final double p1X, final double p1Y, final double p2X, final double p2Y, final double centerX, final double centerY, final double radius) {
        double A = (p1Y - p2Y);
        double B = (p2X - p1X);
        double C = (p1X * p2Y - p2X * p1Y);

        return checkCollision(A, B, C, centerX, centerY, radius);
    }
    public static final int checkCollision(final double a, final double b, final double c, final double centerX, final double centerY, final double radius) {
        // Finding the distance of line from center.
        double dist = (Math.abs(a * centerX + b * centerY + c)) / Math.sqrt(a * a + b * b);
        dist = round(dist, 1);
        if (radius > dist) {
            return 1;  // intersect
        } else if (radius < dist) {
            return -1; // outside
        } else {
            return 0;  // touch
        }
    }

    public static final double getAngleFromXY(final double x, final double y, final double centerX, final double centerY) {
        return getAngleFromXY(x, y, centerX, centerY, 90.0);
    }
    public static final double getAngleFromXY(final double x, final double y, final double centerX, final double centerY, final double angleOffset) {
        // For ANGLE_OFFSET =  0 -> Angle of 0 is at 3 o'clock
        // For ANGLE_OFFSET = 90  ->Angle of 0 is at 12 o'clock
        double deltaX      = x - centerX;
        double deltaY      = y - centerY;
        double radius      = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx          = deltaX / radius;
        double ny          = deltaY / radius;
        double theta       = Math.atan2(ny, nx);
        theta              = Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
        double angle       = (theta + angleOffset) % 360;
        return angle;
    }

    public static final double[] rotatePointAroundRotationCenter(final double x, final double y, final double rX, final double rY, final double angleDeg) {
        final double rad = Math.toRadians(angleDeg);
        final double sin = Math.sin(rad);
        final double cos = Math.cos(rad);
        final double nX  = rX + (x - rX) * cos - (y - rY) * sin;
        final double nY  = rY + (x - rX) * sin + (y - rY) * cos;
        return new double[] { nX, nY };
    }

    public static final void rotateCtx(final GraphicsContext ctx, final double x, final double y, final double angle) {
        ctx.translate(x, y);
        ctx.rotate(angle);
        ctx.translate(-x, -y);
    }

    public static final Point getPointBetweenP1AndP2(final Point p1, final Point p2) {
        double[] xy = getPointBetweenP1AndP2(p1.x, p1.y, p2.x, p2.y);
        return new Point(xy[0], xy[1]);
    }
    public static final double[] getPointBetweenP1AndP2(final double p1X, final double p1Y, final double p2X, final double p2Y) {
        return new double[] { (p1X + p2X) * 0.5, (p1Y + p2Y) * 0.5 };
    }

    public static final <T extends Point> List<T> createConvexHull_OLD(final List<T> points) {
        List<T> convexHull = new ArrayList<>();
        if (points.size() < 3) { return new ArrayList<T>(points); }

        int minDataPoint = -1;
        int maxDataPoint = -1;
        int minX         = Integer.MAX_VALUE;
        int maxX         = Integer.MIN_VALUE;

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getX() < minX) {
                minX     = (int) points.get(i).getX();
                minDataPoint = i;
            }
            if (points.get(i).getX() > maxX) {
                maxX     = (int) points.get(i).getX();
                maxDataPoint = i;
            }
        }
        T minPoint = points.get(minDataPoint);
        T maxPoint = points.get(maxDataPoint);
        convexHull.add(minPoint);
        convexHull.add(maxPoint);
        points.remove(minPoint);
        points.remove(maxPoint);

        List<T> leftSet  = new ArrayList<>();
        List<T> rightSet = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            T p = points.get(i);
            if (pointLocation(minPoint, maxPoint, p) == -1) { leftSet.add(p); } else if (pointLocation(minPoint, maxPoint, p) == 1) rightSet.add(p);
        }
        hullSet(minPoint, maxPoint, rightSet, convexHull);
        hullSet(maxPoint, minPoint, leftSet, convexHull);

        // Add last point which is the same as first point
        convexHull.add((T) new Point(convexHull.get(0).getX(), convexHull.get(0).getY()));

        return convexHull;
    }
    public static final List<Point> createConvexHull(final List<Point> points) {
        return QuickHull.quickHull(points);
    }

    public static final List<Point> createSmoothedConvexHull(final List<Point> points, final int subDivisions) {
        List<Point> hullPolygon = createConvexHull(points);
        return subdividePoints(hullPolygon, subDivisions);
    }

    private static final <T extends Point> double distance(final T p1, final T p2, final T p3) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double num = deltaX * (p1.getY() - p3.getY()) - deltaY * (p1.getX() - p3.getX());
        return Math.abs(num);
    }
    private static final <T extends Point> void hullSet(final T p1, final T p2, final List<T> points, final List<T> hull) {
        int insertPosition = hull.indexOf(p2);

        if (points.size() == 0) { return; }

        if (points.size() == 1) {
            T point = points.get(0);
            points.remove(point);
            hull.add(insertPosition, point);
            return;
        }

        int dist              = Integer.MIN_VALUE;
        int furthestDataPoint = -1;
        for (int i = 0; i < points.size(); i++) {
            T point    = points.get(i);
            double distance = distance(p1, p2, point);
            if (distance > dist) {
                dist          = (int) distance;
                furthestDataPoint = i;
            }
        }
        T point = points.get(furthestDataPoint);
        points.remove(furthestDataPoint);
        hull.add(insertPosition, point);

        // Determine who's to the left of AP
        ArrayList<T> leftSetAP = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            T M = points.get(i);
            if (pointLocation(p1, point, M) == 1) { leftSetAP.add(M); }
        }

        // Determine who's to the left of PB
        ArrayList<T> leftSetPB = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            T M = points.get(i);
            if (pointLocation(point, p2, M) == 1) { leftSetPB.add(M); }
        }
        hullSet(p1, point, leftSetAP, hull);
        hullSet(point, p2, leftSetPB, hull);
    }
    private static final <T extends Point> int pointLocation(final T p1, final T p2, final T p3) {
        double cp1 = (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
        return cp1 > 0 ? 1 : Double.compare(cp1, 0) == 0 ? 0 : -1;
    }

    public static final List<Point> reduceHull(final List<Point> points, final List<Point> hullPoints) {
        int noOfAttempts = 0;
        //List<Point> pointsToCheck = removePointsOnConvexHull(points, hullPoints);
        List<Point> pointsToCheck = new ArrayList<>(points);
        while(noOfAttempts < 1_000_000 && noOfDiagonalEdges(hullPoints) != 0) {
            List<Point> pointsToRemove = new ArrayList<>();
            for (int i = 0, size = hullPoints.size() ; i < size - 1 ; i++) {
                Point p1 = hullPoints.get(i);
                Point p2 = hullPoints.get(i + 1);

                if (isHorizontal(p1, p2) || isVertical(p1, p2)) { continue; }

                Optional<Point> newPoint = pointsToCheck.stream()
                                                        .min(Comparator.comparingDouble(p -> distanceFromPointToLine(p, p1, p2)));
                if (newPoint.isPresent()) {
                    Point np = newPoint.get();
                    hullPoints.add(i + 1, np);
                    pointsToRemove.add(np);
                }
            }
            pointsToCheck.removeAll(pointsToRemove);
            noOfAttempts++;
        }
        return hullPoints;
    }
    public static final List<Point> removePointsOnConvexHull(final List<Point> points, final List<Point> convexHull) {
        List<Point> pointsNotOnHullCurve = new ArrayList<>(points);
        List<Point> pointsToRemove       = new ArrayList<>();
        int         pointsInPolygon      = convexHull.size();
        for (int i = 0 ; i < pointsInPolygon - 1 ; i++) {
            Point p1 = convexHull.get(i);
            Point p2 = convexHull.get(i + 1);
            pointsNotOnHullCurve.forEach(p -> {
                if (isPointOnLine(p, p1, p2)) { pointsToRemove.add(p); }
            });
        }
        pointsNotOnHullCurve.removeAll(pointsToRemove);
        return pointsNotOnHullCurve;
    }
    public static final int noOfDiagonalEdges(final List<Point> polygonPoints) {
        int noOfDiagonalEdges = 0;
        int pointsInPolygon = polygonPoints.size();
        for (int i = 0 ; i < pointsInPolygon - 1 ; i++) {
            Point p1 = polygonPoints.get(i);
            Point p2 = polygonPoints.get(i + 1);
            if (isHorizontal(p1, p2) || isVertical(p1, p2)) { continue; }
            noOfDiagonalEdges++;
        }
        if (!isHorizontal(polygonPoints.get(pointsInPolygon - 1), polygonPoints.get(0)) &&
            !isVertical(polygonPoints.get(pointsInPolygon - 1), polygonPoints.get(0))) {
            noOfDiagonalEdges++;
        }
        return noOfDiagonalEdges;
    }
    public static final boolean isHorizontal(final Point p1, final Point p2) { return Math.abs(p1.getY() - p2.getY()) < EPSILON; }
    public static final boolean isVertical(final Point p1, final Point p2)   { return Math.abs(p1.getX() - p2.getX()) < EPSILON; }


    /**
     * Check the given hull curve for diagonals and collect all points that are in the rectangle
     * that will be defined by the two points of each diagonal.
     * This points can then be used to reduce the convex hull curve of a polygon to a rectangular
     * hull curve
     * @param points points to check
     * @param hullCurvePoints points on hull curve
     * @return list of points that can be used to reduce the diagonals in a convex hull curve
     */
    public static final List<Point> getPointsToCheck(final List<Point> points, final List<Point> hullCurvePoints) {
        List<Point[]> diagonals = new ArrayList<>();
        int pointsInPolygon = hullCurvePoints.size();
        for (int i = 0 ; i < pointsInPolygon - 1 ; i++) {
            Point p1 = hullCurvePoints.get(i);
            Point p2 = hullCurvePoints.get(i + 1);
            if (isHorizontal(p1, p2) || isVertical(p1, p2)) { continue; }
            diagonals.add(new Point[]{p1, p2});
        }

        List<Bounds> areas = new ArrayList<>();
        for (Point[] d : diagonals) {
            double x = Math.min(d[0].x, d[1].x);
            double y = Math.min(d[0].y, d[1].y);
            double w = Math.abs(d[1].x - d[0].x);
            double h = Math.abs(d[1].y - d[0].y);
            areas.add(new Bounds(x, y, w, h));
        }

        List<Point> pointsToCheck = new ArrayList<>();
        for (Point p : points) {
            for (Bounds area : areas) {
                if (isInRectangle(p.getX(), p.getY(), area.getX(), area.getY(), area.getX() + area.getWidth(), area.getY() + area.getHeight())) { pointsToCheck.add(p); }
            }
        }

        return pointsToCheck;
    }

    /**
     * Add points from given points to curvePoints if points are on the polygon defined by curvePoints
     * @param curvePoints list of points on curve
     * @param points list of points to add
     * @return list of points incl. the added ones
     */
    public static final List<Point> addPointsOnCurve(final List<Point> curvePoints, final List<Point> points) {
        List<Point> result   = new ArrayList<>();
        List<Point> polygonPoints = new ArrayList<>(curvePoints);
        List<Point> pointsToCheck = new ArrayList<>(points);
        pointsToCheck.removeAll(curvePoints);

        int noOfPointsToCheck   = pointsToCheck.size();
        int noOfPointsOnPolygon = polygonPoints.size();
        for (int i = 0 ; i < noOfPointsOnPolygon - 1 ; i++) {
            Point p1 = polygonPoints.get(i);
            Point p2 = polygonPoints.get(i + 1);

            if (!result.contains(p1)) { result.add(p1); }
            for (int j = 0 ; j < noOfPointsToCheck ; j++) {
                Point p = pointsToCheck.get(j);
                if (isPointOnLine(p, p1, p2)) { result.add(p); }
            }
        }
        result.add(polygonPoints.get(noOfPointsOnPolygon - 1));
        return result;
    }

    /**
     * Curve points should be ordered counterclockwise along the curve
     * @param curvePoints points that define the curve sorted counterclockwise
     * @param width width of the box that will be checked for next point
     * @param height height of the box that will be checked for next point
     * @param points points to check
     * @return list of point pairs that define the start and end points of gaps
     */
    public static final List<Point> findGaps(final List<Point> curvePoints, final double width, final double height, final List<Point> points) {
        List<Point> startEndPoints      = new ArrayList<>();
        List<Point> pointsToCheck       = new ArrayList<>(points);
        int         noOfPointsOnPolygon = curvePoints.size();
        for (int i = 0 ; i < noOfPointsOnPolygon - 1 ; i++) {
            Point    p1  = curvePoints.get(i);
            Point    p2  = curvePoints.get(i + 1);
            Position pos = Position.UNDEFINED;
            if (isHorizontal(p1, p2)) {
                if (p1.getX() < p2.getX()) {
                    pos = Position.BOTTOM;
                } else if (p1.getX() > p2.getX()) {
                    pos = Position.TOP;
                }
            } else if (isVertical(p1, p2)) {
                if (p1.getY() < p2.getY()) {
                    pos = Position.LEFT;
                } else if (p1.getY() > p2.getY()) {
                    pos = Position.RIGHT;
                }
            }

            switch(pos) {
                case TOP:
                    if (!isInRectangle(p2.getX(), p2.getY(), p1.getX() - width, p1.getY() - height, p1.getX(), p1.getY())) {
                        startEndPoints.add(p1);
                        startEndPoints.add(p2);
                        double dX = Math.abs(p1.getX() - p2.getX());
                        // Search for next point in vertical direction
                        for (Point p : pointsToCheck) {
                            if(isInRectangle(p.getX(), p.getY(), p1.getX() - width / 2, p1.getY() - height, p1.getX(), p1.getY())) {
                                if (isVertical(p, p1)) {
                                    //System.out.println("Find next vertical point in gap");
                                }
                            }
                        }
                    }
                    break;
                case LEFT:
                    if (!isInRectangle(p2.getX(), p2.getY(), p1.getX(), p1.getY(), p1.getX() + width, p1.getY() + height)) {
                        startEndPoints.add(p1);
                        startEndPoints.add(p2);
                        double dY = Math.abs(p1.getY() - p2.getY());
                    }
                    break;
                case BOTTOM:
                    if (!isInRectangle(p2.getX(), p2.getY(), p1.getX(), p1.getY() - height, p1.getX() + width, p1.getY())) {
                        startEndPoints.add(p1);
                        startEndPoints.add(p2);
                        double dX = Math.abs(p1.getX() - p2.getX());
                        // Search for next point in vertical direction
                        for (Point p : pointsToCheck) {
                            if(isInRectangle(p.getX(), p.getY(), p1.getX() - width / 2, p1.getY() - height, p1.getX() + width, p1.getY())) {
                                if (isVertical(p, p1)) {
                                    //System.out.println("Find next vertical point in gap");
                                }
                            }
                        }
                    }
                    break;
                case RIGHT:
                    if (!isInRectangle(p2.getX(), p2.getY(), p1.getX() - width, p1.getY() - height, p1.getX(), p1.getY())) {
                        startEndPoints.add(p1);
                        startEndPoints.add(p2);
                        double dY = Math.abs(p1.getY() - p2.getY());
                    }
                    break;
            }
        }
        return startEndPoints;
    }

    public static final double[] getPointsXFromPoints(final List<Point> points) {
        int size = points.size();
        double[] pointsX = new double[size];
        for (int i = 0 ; i < size ; i++) { pointsX[i] = points.get(i).getX(); }
        return pointsX;
    }
    public static final double[] getPointsYFromPoints(final List<Point> points) {
        int size = points.size();
        double[] pointsY = new double[size];
        for (int i = 0 ; i < size ; i++) { pointsY[i] = points.get(i).getY(); }
        return pointsY;
    }

    public static final double[] getDoubleArrayFromPoints(final List<Point> points) {
        int size = points.size();
        double[] pointsArray = new double[size * 2];
        int counter = 0;
        for (int i = 0 ; i < size ; i++) {
            pointsArray[counter]     = points.get(i).getX();
            pointsArray[counter + 1] = points.get(i).getY();
            counter += 2;
        }
        return pointsArray;
    }

    public static final void sortXY(final List<Point> points) {
        Collections.sort(points, Comparator.comparingDouble(Point::getX).thenComparingDouble(Point::getY));
    }

    /**
     * Sort a list of points by it's distance from each other. The algorithm starts with the point closest to
     * 0,0 and from there always adds the point closest to the last point
     * @param points list of points to sort
     * @return list of points sorted by it's distance from each other
     */
    public static final List<Point> sortByDistance(final List<Point> points) {
        return sortByDistance(points, true);
    }
    public static final List<Point> sortByDistance(final List<Point> points, final boolean counterClockWise) {
        if (points.isEmpty()) { return points; }
        List<Point> output = new ArrayList<>();
        output.add(points.get(nearestPoint(new Point(0, 0), points)));
        points.remove(output.get(0));
        int x = 0;
        for (int i = 0; i < points.size() + x; i++) {
            output.add(points.get(nearestPoint(output.get(output.size() - 1), points)));
            points.remove(output.get(output.size() - 1));
            x++;
        }
        if (counterClockWise) { Collections.reverse(output); }
        return output;
    }
    public static final int nearestPoint(final Point p, final List<Point> points) {
        Pair<Double, Integer> smallestDistance = new Pair<>(0d, 0);
        for (int i = 0; i < points.size(); i++) {
            double distance = distance(p.getX(), p.getY(), points.get(i).getX(), points.get(i).getY());
            if (i == 0) {
                smallestDistance = new Pair<>(distance, i);
            } else {
                if (distance < smallestDistance.getA()) {
                    smallestDistance = new Pair<>(distance, i);
                }
            }
        }
        return smallestDistance.getB();
    }

    public static final String padLeft(final String text, final String filler, final int n) {
        return String.format("%" + n + "s", text).replace(" ", filler);
    }
    public static final String padRight(final String text, final String filler, final int n) {
        return String.format("%-" + n + "s", text).replace(" ", filler);
    }

    public static final List<Character> splitStringInCharacters(final String text) {
        return text.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    }

    public static final List<Character> splitNumberInDigits(final double number) {
        return splitStringInCharacters(Double.toString(number));
    }


    public static final List<Point> removeDuplicatePoints(final List<Point> points, final double tolerance) {
        final double tol  = tolerance < 0 ? 0 : tolerance;
        final int    size = points.size();

        List<Point> reducedPoints  = new ArrayList<>(points);
        Set<Point>  pointsToRemove = new HashSet<>();

        for (int i = 0 ; i < size - 2 ; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);

            double distP1P2 = distance(p1, p2);

            // Remove duplicates
            if (distP1P2 <= tol) { pointsToRemove.add(p2); }
        }
        reducedPoints.removeAll(pointsToRemove);

        return reducedPoints;
    }

    public static final List<Point> simplify(final List<Point> points, final double angleTolerance, final double minDistance) {
        final double tolerance = angleTolerance < 0 ? 0.5 : angleTolerance / 2.0;
        final double distance  = minDistance < 0 ? 1.0 : minDistance;

        final int    size = points.size();
        if (size <= 4) { return points; }

        List<Point> reducedPoints  = new ArrayList<>(removeDuplicatePoints(points, 1));
        Set<Point>  pointsToRemove = new HashSet<>();

        for (int i = 0 ; i < size - 3 ; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            Point p3 = points.get(i + 2);

            double bearingP1P2  = bearing(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            double bearingP1P3  = bearing(p1.getX(), p1.getY(), p3.getX(), p3.getY());
            double bearingP2P3  = bearing(p2.getX(), p2.getY(), p3.getX(), p3.getY());
            double deltaBearing = Math.abs(bearingP1P2 - bearingP2P3);

            if (deltaBearing < 0.5) {
                pointsToRemove.add(p2); // Points are on same line -> remove p2
            } else if (deltaBearing % 90 == 0) {
                // Keep corner point 90 deg
            } else if (deltaBearing > 80 && deltaBearing < 90) {
                // Keep probable corner point between 80-90 deg
            } else if (bearingP1P3 > bearingP1P2 - tolerance && bearingP1P3 < bearingP1P2 + tolerance) {
                pointsToRemove.add(p2);
            } else if (distance(p1, p2) < distance) {
                // Remove points within distance
                pointsToRemove.add(p2);
            }
        }

        // Check
        Point lastPoint       = points.get(size - 1);
        Point secondLastPoint = points.get(size - 2);
        Point thirdLastPoint  = points.get(size - 3);
        Point fourthLastPoint = points.get(size - 4);

        if (removeP2(fourthLastPoint, thirdLastPoint, secondLastPoint, lastPoint, tolerance, distance)) {
            pointsToRemove.add(secondLastPoint);
        }

        reducedPoints.removeAll(pointsToRemove);

        return reducedPoints;
    }
    private static final boolean removeP2(final Point p0, final Point p1, final Point p2, final Point p3, final double tolerance, final double distance) {
        double bearingP1P2  = bearing(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        double bearingP1P3  = bearing(p1.getX(), p1.getY(), p3.getX(), p3.getY());
        double bearingP2P3  = bearing(p2.getX(), p2.getY(), p3.getX(), p3.getY());
        double deltaBearing = Math.abs(bearingP1P2 - bearingP2P3);

        if (deltaBearing < 0.5) {
            return true;
        } else if (deltaBearing % 90 == 0) {
            return false;
        } else if (deltaBearing > 80 && deltaBearing < 90) {
            return false;
        } else if (bearingP1P3 > bearingP1P2 - tolerance && bearingP1P3 < bearingP1P2 + tolerance) {
            return true;
        } else if (distance(p1, p2) < distance) {
            return true;
        }
        return false;
    }

    public static final double bearing(final Point p1, final Point p2) {
        return bearing(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    public static final double bearing(final double x1, final double y1, final double x2, final double y2) {
        double bearing = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 90;
        if (bearing < 0) { bearing += 360.0; }
        return bearing;
    }

    public static final String getCardinalDirectionFromBearing(final double brng) {
        double bearing = brng % 360.0;
        if (0 == bearing || 360 == bearing || (bearing > CardinalDirection.N.from && bearing < 360)) {
            return CardinalDirection.N.direction;
        } else if (90 == bearing) {
            return CardinalDirection.E.direction;
        } else if (180 == bearing) {
            return CardinalDirection.S.direction;
        } else if (270 == bearing) {
            return CardinalDirection.W.direction;
        } else {
            for (CardinalDirection cardinalDirection : CardinalDirection.values()) {
                if (bearing >= cardinalDirection.from && bearing <= cardinalDirection.to) {
                    return cardinalDirection.direction;
                }
            }
        }
        return "";
    }


    public static final double[] toHSL(final Color color) {
        return rgbToHSL(color.getRed(), color.getGreen(), color.getBlue());
    }
    public static final double[] rgbToHSL(final double red, final double green, final double blue) {
        //	Minimum and Maximum RGB values are used in the HSL calculations
        double min = Math.min(red, Math.min(green, blue));
        double max = Math.max(red, Math.max(green, blue));

        //  Calculate the Hue
        double hue = 0;

        if (max == min) {
            hue = 0;
        } else if (max == red) {
            hue = ((60 * (green - blue) / (max - min)) + 360) % 360;
        } else if (max == green) {
            hue = (60 * (blue - red) / (max - min)) + 120;
        } else if (max == blue) {
            hue = (60 * (red - green) / (max - min)) + 240;
        }

        //  Calculate the Luminance
        double luminance = (max + min) / 2;

        //  Calculate the Saturation
        double saturation = 0;
        if (Double.compare(max, min)  == 0) {
            saturation = 0;
        } else if (luminance <= .5) {
            saturation = (max - min) / (max + min);
        } else {
            saturation = (max - min) / (2 - max - min);
        }

        return new double[] { hue, saturation, luminance};
    }

    public static final Color hslToRGB(double hue, double saturation, double luminance) {
        return hslToRGB(hue, saturation, luminance, 1);
    }
    public static final Color hslToRGB(double hue, double saturation, double luminance, double opacity) {
        saturation = clamp(0, 1, saturation);
        luminance  = clamp(0, 1, luminance);
        opacity    = clamp(0, 1, opacity);

        hue = hue % 360.0;
        hue /= 360;

        double q = luminance < 0.5 ? luminance * (1 + saturation) : (luminance + saturation) - (saturation * luminance);
        double p = 2 * luminance - q;

        double r = clamp(0, 1, hueToRGB(p, q, hue + (1.0/3.0)));
        double g = clamp(0, 1, hueToRGB(p, q, hue));
        double b = clamp(0, 1, hueToRGB(p, q, hue - (1.0/3.0)));

        return Color.color(r, g, b, opacity);
    }
    private static final double hueToRGB(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (6 * t < 1) { return p + ((q - p) * 6 * t); }
        if (2 * t < 1) { return q; }
        if (3 * t < 2) { return p + ((q - p) * 6 * ((2.0/3.0) - t)); }
        return p;
    }

    public static final String colorToRGB(final Color color) {
        String hex      = color.toString().replace("0x", "");
        String hexRed   = hex.substring(0, 2).toUpperCase();
        String hexGreen = hex.substring(2, 4).toUpperCase();
        String hexBlue  = hex.substring(4, 6).toUpperCase();

        String intRed   = Integer.toString(Integer.parseInt(hexRed, 16));
        String intGreen = Integer.toString(Integer.parseInt(hexGreen, 16));
        String intBlue  = Integer.toString(Integer.parseInt(hexBlue, 16));

        return String.join("", "colorToRGB(", intRed, ", ", intGreen, ", ", intBlue, ")");
    }

    public static final String colorToRGBA(final Color color) { return colorToRGBA(color, color.getOpacity()); }
    public static final String colorToRGBA(final Color color, final double alpha) {
        String hex      = color.toString().replace("0x", "");
        String hexRed   = hex.substring(0, 2).toUpperCase();
        String hexGreen = hex.substring(2, 4).toUpperCase();
        String hexBlue  = hex.substring(4, 6).toUpperCase();

        String intRed   = Integer.toString(Integer.parseInt(hexRed, 16));
        String intGreen = Integer.toString(Integer.parseInt(hexGreen, 16));
        String intBlue  = Integer.toString(Integer.parseInt(hexBlue, 16));
        String alph     = String.format(Locale.US, "%.3f", clamp(0, 1, alpha));

        return String.join("", "colorToRGBA(", intRed, ", ", intGreen, ", ", intBlue, ",", alph, ")");
    }

    public static final String colorToWeb(final Color color) { return color.toString().replace("0x", "#").substring(0, 7); }

    public static final String colorToCss(final Color color) {
        return color.toString().replace("0x", "#");
    }

    public static final boolean isMonochrome(final Color color) {
        return Double.compare(color.getRed(), color.getGreen()) == 0 && Double.compare(color.getGreen(), color.getBlue()) == 0;
    }

    public static final double colorDistance(final Color color1, final Color color2) {
        final double deltaR = (color2.getRed()   - color1.getRed());
        final double deltaG = (color2.getGreen() - color1.getGreen());
        final double deltaB = (color2.getBlue()  - color1.getBlue());

        return Math.sqrt(deltaR * deltaR + deltaG * deltaG + deltaB * deltaB);
    }

    public static final double[] colorToYUV(final Color color) {
        final double weightFactorRed   = 0.299;
        final double weightFactorGreen = 0.587;
        final double weightFactorBlue  = 0.144;
        final double uMax              = 0.436;
        final double vMax              = 0.615;
        double y = clamp(0, 1, weightFactorRed * color.getRed() + weightFactorGreen * color.getGreen() + weightFactorBlue * color.getBlue());
        double u = clamp(-uMax, uMax, uMax * ((color.getBlue() - y) / (1 - weightFactorBlue)));
        double v = clamp(-vMax, vMax, vMax * ((color.getRed() - y) / (1 - weightFactorRed)));
        return new double[] { y, u, v };
    }

    public static final boolean isBright(final Color color) { return Double.compare(colorToYUV(color)[0], 0.5) >= 0.0; }
    public static final boolean isDark(final Color color) { return colorToYUV(color)[0] < 0.5; }

    public static final Color getContrastColor(final Color color) {
        return color.getBrightness() > 0.5 ? Color.BLACK : Color.WHITE;
    }

    public static final Color getColorWithOpacity(final Color color, final double opacity) {
        return Color.color(color.getRed(), color.getGreen(), color.getBlue(), clamp(0.0, 1.0, opacity));
    }

    public static final List<Color> createColorPalette(final Color fromColor, final Color toColor, final int noOfColors) {
        int    steps        = clamp(1, 12, noOfColors) - 1;
        double step         = 1.0 / steps;
        double deltaRed     = (toColor.getRed()     - fromColor.getRed())     * step;
        double deltaGreen   = (toColor.getGreen()   - fromColor.getGreen())   * step;
        double deltaBlue    = (toColor.getBlue()    - fromColor.getBlue())    * step;
        double deltaOpacity = (toColor.getOpacity() - fromColor.getOpacity()) * step;

        List<Color> palette      = new ArrayList<>(noOfColors);
        Color       currentColor = fromColor;
        palette.add(currentColor);
        for (int i = 0 ; i < steps ; i++) {
            double red     = clamp(0d, 1d, (currentColor.getRed()     + deltaRed));
            double green   = clamp(0d, 1d, (currentColor.getGreen()   + deltaGreen));
            double blue    = clamp(0d, 1d, (currentColor.getBlue()    + deltaBlue));
            double opacity = clamp(0d, 1d, (currentColor.getOpacity() + deltaOpacity));
            currentColor   = Color.color(red, green, blue, opacity);
            palette.add(currentColor);
        }
        return palette;
    }

    public static final Color[] createColorVariations(final Color color, final int newNoOfColors) {
        int    noOfColors = clamp(1, 12, newNoOfColors);
        double step       = 0.8 / noOfColors;
        double hue        = color.getHue();
        double brg        = color.getBrightness();
        Color[] colors = new Color[noOfColors];
        for (int i = 0 ; i < noOfColors ; i++) { colors[i] = Color.hsb(hue, 0.2 + i * step, brg); }
        return colors;
    }

    public static final Color getColorAt(final List<Stop> stopList, final double positionOfColor) {
        Map<Double, Stop> STOPS = new TreeMap<>();
        for (Stop stop : stopList) { STOPS.put(stop.getOffset(), stop); }

        if (STOPS.isEmpty()) return Color.BLACK;

        double minFraction = Collections.min(STOPS.keySet());
        double maxFraction = Collections.max(STOPS.keySet());

        if (Double.compare(minFraction, 0d) > 0) { STOPS.put(0.0, new Stop(0.0, STOPS.get(minFraction).getColor())); }
        if (Double.compare(maxFraction, 1d) < 0) { STOPS.put(1.0, new Stop(1.0, STOPS.get(maxFraction).getColor())); }

        final double POSITION = clamp(0d, 1d, positionOfColor);
        final Color COLOR;
        if (STOPS.size() == 1) {
            final Map<Double, Color> ONE_ENTRY = (Map<Double, Color>) STOPS.entrySet().iterator().next();
            COLOR = STOPS.get(ONE_ENTRY.keySet().iterator().next()).getColor();
        } else {
            Stop lowerBound = STOPS.get(0.0);
            Stop upperBound = STOPS.get(1.0);
            for (Double fraction : STOPS.keySet()) {
                if (Double.compare(fraction,POSITION) < 0) {
                    lowerBound = STOPS.get(fraction);
                }
                if (Double.compare(fraction, POSITION) > 0) {
                    upperBound = STOPS.get(fraction);
                    break;
                }
            }
            COLOR = interpolateColor(lowerBound, upperBound, POSITION);
        }
        return COLOR;
    }
    public static final Color interpolateColor(final Stop lowerBound, final Stop upperBound, final double position) {
        final double POS  = (position - lowerBound.getOffset()) / (upperBound.getOffset() - lowerBound.getOffset());

        final double DELTA_RED     = (upperBound.getColor().getRed()     - lowerBound.getColor().getRed())     * POS;
        final double DELTA_GREEN   = (upperBound.getColor().getGreen()   - lowerBound.getColor().getGreen())   * POS;
        final double DELTA_BLUE    = (upperBound.getColor().getBlue()    - lowerBound.getColor().getBlue())    * POS;
        final double DELTA_OPACITY = (upperBound.getColor().getOpacity() - lowerBound.getColor().getOpacity()) * POS;

        double red     = clamp(0, 1, (lowerBound.getColor().getRed()     + DELTA_RED));
        double green   = clamp(0, 1, (lowerBound.getColor().getGreen()   + DELTA_GREEN));
        double blue    = clamp(0, 1, (lowerBound.getColor().getBlue()    + DELTA_BLUE));
        double opacity = clamp(0, 1, (lowerBound.getColor().getOpacity() + DELTA_OPACITY));

        return Color.color(red, green, blue, opacity);
    }

    public static final Color interpolateColor(final Color color1, final Color color2, final double fraction) {
        return interpolateColor(color1, color2, fraction, -1);
    }
    public static final Color getColorAt(final LinearGradient gradient, final double fraction) {
        return getColorWithOpacityAt(gradient, fraction, 1.0);
    }
    public static final Color getColorWithOpacityAt(final LinearGradient gradient, final double fraction, final double targetOpacity) {
        List<Stop> stops     = gradient.getStops();
        double     frac      = fraction < 0f ? 0f : (fraction > 1 ? 1 : fraction);
        Stop       lowerStop = new Stop(0.0, stops.get(0).getColor());
        Stop       upperStop = new Stop(1.0, stops.get(stops.size() - 1).getColor());

        for (Stop stop : stops) {
            double currentFraction = stop.getOffset();
            if (Double.compare(currentFraction, frac) == 0) {
                return stop.getColor();
            } else if (Double.compare(currentFraction, frac) < 0) {
                lowerStop = new Stop(currentFraction, stop.getColor());
            } else {
                upperStop = new Stop(currentFraction, stop.getColor());
                break;
            }
        }

        double interpolationFraction = (frac - lowerStop.getOffset()) / (upperStop.getOffset() - lowerStop.getOffset());
        return interpolateColor(lowerStop.getColor(), upperStop.getColor(), interpolationFraction, targetOpacity);
    }
    public static final Color interpolateColor(final Color color1, final Color color2, final double fraction, final double targetOpacity) {
        double frac           = clamp(0, 1, fraction);
        double targetOpct     = targetOpacity < 0 ? targetOpacity : clamp(0, 1, fraction);

        final double RED1     = color1.getRed();
        final double GREEN1   = color1.getGreen();
        final double BLUE1    = color1.getBlue();
        final double OPACITY1 = color1.getOpacity();

        final double RED2     = color2.getRed();
        final double GREEN2   = color2.getGreen();
        final double BLUE2    = color2.getBlue();
        final double OPACITY2 = color2.getOpacity();

        final double DELTA_RED     = RED2 - RED1;
        final double DELTA_GREEN   = GREEN2 - GREEN1;
        final double DELTA_BLUE    = BLUE2 - BLUE1;
        final double DELTA_OPACITY = OPACITY2 - OPACITY1;

        double red     = RED1 + (DELTA_RED * frac);
        double green   = GREEN1 + (DELTA_GREEN * frac);
        double blue    = BLUE1 + (DELTA_BLUE * frac);
        double opacity = targetOpct < 0 ? OPACITY1 + (DELTA_OPACITY * frac) : targetOpct;

        red     = clamp(0, 1, red);
        green   = clamp(0, 1, green);
        blue    = clamp(0, 1, blue);
        opacity = clamp(0, 1, opacity);

        return Color.color(red, green, blue, opacity);
    }

    public static final void enableNode(final Node node, final boolean enable) {
        node.setManaged(enable);
        node.setVisible(enable);
    }

    public static final void scaleNodeTo(final Node node, final double targetWidth, final double targetHeight) {
        node.setScaleX(targetWidth / node.getLayoutBounds().getWidth());
        node.setScaleY(targetHeight / node.getLayoutBounds().getHeight());
    }

    public static final Point[] smoothSparkLine(final List<Double> dataList, final double minValue, final double maxValue, final javafx.scene.shape.Rectangle graphBounds, final int noOfDatapoints) {
        int     size   = dataList.size();
        Point[] points = new Point[size];

        double low  = Statistics.getMin(dataList);
        double high = Statistics.getMax(dataList);
        if (Helper.equals(low, high)) {
            low  = minValue;
            high = maxValue;
        }
        double range = high - low;

        double minX  = graphBounds.getX();
        double maxX  = minX + graphBounds.getWidth();
        double minY  = graphBounds.getY();
        double maxY  = minY + graphBounds.getHeight();
        double stepX = graphBounds.getWidth() / (noOfDatapoints - 1);
        double stepY = graphBounds.getHeight() / range;

        for (int i = 0 ; i < size ; i++) {
            points[i] = new Point(minX + i * stepX, maxY - Math.abs(low - dataList.get(i)) * stepY);
        }

        return subdividePoints(points, 16);
    }

    public static final void drawRoundedRect(final GraphicsContext ctx, final Bounds bounds, final CornerRadii radii) {
        double x           = bounds.getX();
        double y           = bounds.getY();
        double width       = bounds.getWidth();
        double height      = bounds.getHeight();
        double xPlusWidth  = x + width;
        double yPlusHeight = y + height;

        ctx.beginPath();
        ctx.moveTo(x + radii.getTopLeft(), y);
        ctx.lineTo(xPlusWidth - radii.getTopRight(), y);
        ctx.quadraticCurveTo(xPlusWidth, y, xPlusWidth, y + radii.getTopRight());
        ctx.lineTo(xPlusWidth, yPlusHeight - radii.getBottomRight());
        ctx.quadraticCurveTo(xPlusWidth, yPlusHeight, xPlusWidth - radii.getBottomRight(), yPlusHeight);
        ctx.lineTo(x + radii.getBottomLeft(), yPlusHeight);
        ctx.quadraticCurveTo(x, yPlusHeight, x, yPlusHeight - radii.getBottomLeft());
        ctx.lineTo(x, y + radii.getTopLeft());
        ctx.quadraticCurveTo(x, y, x + radii.getTopLeft(), y);
        ctx.closePath();
    }

    /**
     * @param startPoint
     * @param controlPoint1
     * @param controlPoint2
     * @param endPoint
     * @param distance in % (0-1)
     * @return
     */
    public static final Point getCubicBezierXYatT(final Point startPoint, final Point controlPoint1, final Point controlPoint2, final Point endPoint, final double distance) {
        final double x = cubicN(distance, startPoint.getX(), controlPoint1.getX(), controlPoint2.getX(), endPoint.getX());
        final double y = cubicN(distance, startPoint.getY(), controlPoint1.getY(), controlPoint2.getY(), endPoint.getY());
        return new Point(x, y);
    }
    public static final double[] getCubicBezierXYatT(final double startPointX, final double startPointY,
                                                     final double controlPoint1X, final double controlPoint1Y,
                                                     final double controlPoint2X, final double controlPoint2Y,
                                                     final double endPointX, final double endPointY, final double distance) {
        final double x = cubicN(distance, startPointX, controlPoint1X, controlPoint2X, endPointX);
        final double y = cubicN(distance, startPointY, controlPoint1Y, controlPoint2Y, endPointY);
        return new double[] { x, y };
    }
    private static final double cubicN(final double distance, final double a, final double b, final double c, final double d) {
        final double t2 = distance * distance;
        final double t3 = t2 * distance;
        return a + (-a * 3 + distance * (3 * a - a * distance)) * distance + (3 * b + distance * (-6 * b + b * 3 * distance)) * distance + (c * 3 - c * 3 * distance) * t2 + d * t3;
    }


    // Smooth given path defined by it's list of path elements
    public static final Path smoothPath(final ObservableList<PathElement> elements, final boolean filled) {
        if (elements.isEmpty()) { return new Path(); }
        final Point[] dataPoints = new Point[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            final PathElement element = elements.get(i);
            if (element instanceof MoveTo) {
                MoveTo move   = (MoveTo) element;
                dataPoints[i] = new Point(move.getX(), move.getY());
            } else if (element instanceof LineTo) {
                LineTo line   = (LineTo) element;
                dataPoints[i] = new Point(line.getX(), line.getY());
            }
        }
        double                 zeroY               = ((MoveTo) elements.get(0)).getY();
        List<PathElement>      smoothedElements    = new ArrayList<>();
        Pair<Point[], Point[]> result              = calcCurveControlPoints(dataPoints);
        Point[]                firstControlPoints  = result.getA();
        Point[]                secondControlPoints = result.getB();
        // Start path dependent on filled or not
        if (filled) {
            smoothedElements.add(new MoveTo(dataPoints[0].getX(), zeroY));
            smoothedElements.add(new LineTo(dataPoints[0].getX(), dataPoints[0].getY()));
        } else {
            smoothedElements.add(new MoveTo(dataPoints[0].getX(), dataPoints[0].getY()));
        }
        // Add curves
        for (int i = 2; i < dataPoints.length; i++) {
            final int ci = i - 1;
            smoothedElements.add(new CubicCurveTo(
            firstControlPoints[ci].getX(), firstControlPoints[ci].getY(),
            secondControlPoints[ci].getX(), secondControlPoints[ci].getY(),
            dataPoints[i].getX(), dataPoints[i].getY()));
        }
        // Close the path if filled
        if (filled) {
            smoothedElements.add(new LineTo(dataPoints[dataPoints.length - 1].getX(), zeroY));
            smoothedElements.add(new ClosePath());
        }
        return new Path(smoothedElements);
    }
    private static final Pair<Point[], Point[]> calcCurveControlPoints(Point[] dataPoints) {
        Point[] firstControlPoints;
        Point[] secondControlPoints;
        int n = dataPoints.length - 1;
        if (n == 1) { // Special case: Bezier curve should be a straight line.
            firstControlPoints     = new Point[1];
            firstControlPoints[0]  = new Point((2 * dataPoints[0].getX() + dataPoints[1].getX()) / 3, (2 * dataPoints[0].getY() + dataPoints[1].getY()) / 3);
            secondControlPoints    = new Point[1];
            secondControlPoints[0] = new Point(2 * firstControlPoints[0].getX() - dataPoints[0].getX(), 2 * firstControlPoints[0].getY() - dataPoints[0].getY());
            return new Pair<>(firstControlPoints, secondControlPoints);
        }

        // Calculate first Bezier control points
        // Right hand side vector
        double[] rhs = new double[n];

        // Set right hand side X values
        for (int i = 1; i < n - 1; ++i) {
            rhs[i] = 4 * dataPoints[i].getX() + 2 * dataPoints[i + 1].getX();
        }
        rhs[0]     = dataPoints[0].getX() + 2 * dataPoints[1].getX();
        rhs[n - 1] = (8 * dataPoints[n - 1].getX() + dataPoints[n].getX()) / 2.0;
        // Get first control points X-values
        double[] x = getFirstControlPoints(rhs);

        // Set right hand side Y values
        for (int i = 1; i < n - 1; ++i) {
            rhs[i] = 4 * dataPoints[i].getY() + 2 * dataPoints[i + 1].getY();
        }
        rhs[0]     = dataPoints[0].getY() + 2 * dataPoints[1].getY();
        rhs[n - 1] = (8 * dataPoints[n - 1].getY() + dataPoints[n].getY()) / 2.0;
        // Get first control points Y-values
        double[] y = getFirstControlPoints(rhs);

        // Fill output arrays.
        firstControlPoints  = new Point[n];
        secondControlPoints = new Point[n];
        for (int i = 0; i < n; ++i) {
            // First control point
            firstControlPoints[i] = new Point(x[i], y[i]);
            // Second control point
            if (i < n - 1) {
                secondControlPoints[i] = new Point(2 * dataPoints[i + 1].getX() - x[i + 1], 2 * dataPoints[i + 1].getY() - y[i + 1]);
            } else {
                secondControlPoints[i] = new Point((dataPoints[n].getX() + x[n - 1]) / 2, (dataPoints[n].getY() + y[n - 1]) / 2);
            }
        }
        return new Pair<>(firstControlPoints, secondControlPoints);
    }
    private static final double[] getFirstControlPoints(double[] rhs) {
        int      n   = rhs.length;
        double[] x   = new double[n]; // Solution vector.
        double[] tmp = new double[n]; // Temp workspace.
        double   b   = 2.0;

        x[0] = rhs[0] / b;

        for (int i = 1; i < n; i++) {// Decomposition and forward substitution.
            tmp[i] = 1 / b;
            b      = (i < n - 1 ? 4.0 : 3.5) - tmp[i];
            x[i]   = (rhs[i] - x[i - 1]) / b;
        }
        for (int i = 1; i < n; i++) {
            x[n - i - 1] -= tmp[n - i] * x[n - i]; // Backsubstitution.
        }
        return x;
    }

    public static final boolean isInPolygon(final double x, final double y, final Polygon polygon) {
        List<Double> points              = polygon.getPoints();
        int          size                = points.size();
        int          noOfPointsInPolygon = size / 2;
        double[]     pointsX             = new double[noOfPointsInPolygon];
        double[]     pointsY             = new double[noOfPointsInPolygon];
        int          pointCounter        = 0;

        for (int i = 0 ; i < size - 1 ; i += 2) {
            pointsX[pointCounter] = points.get(i);
            pointsY[pointCounter] = points.get(i + 1);
            pointCounter++;
        }
        return isInPolygon(x, y, noOfPointsInPolygon, pointsX, pointsY);
    }

    public static final int getDegrees(final double decDeg) { return (int) decDeg; }
    public static final int getMinutes(final double decDeg) { return (int) ((decDeg - getDegrees(decDeg)) * 60); }
    public static final double getSeconds(final double decDeg) { return (((decDeg - getDegrees(decDeg)) * 60) - getMinutes(decDeg)) * 60; }

    public static final double getDecimalDeg(final int degrees, final int minutes, final double seconds) {
        return (((seconds / 60) + minutes) / 60) + degrees;
    }

    public static final Node getNodeByColRow(final int col, final int row, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node : childrens) {
            if(GridPane.getRowIndex(node) == row &&
               GridPane.getColumnIndex(node) == col) {
                result = node;
                break;
            }
        }
        return result;
    }

    public static final ColorInput createColorMask(final Image sourceImage, final Color color) { return new ColorInput(0, 0, sourceImage.getWidth(), sourceImage.getHeight(), color); }
    public static final Blend createColorBlend(final Image sourceImage, final Color color) {
        final ColorInput mask  = createColorMask(sourceImage, color);
        final Blend      blend = new Blend(BlendMode.MULTIPLY);
        blend.setTopInput(mask);
        return blend;
    }
    public static final WritableImage getRedChannel(final Image sourceImage)   { return getColorChannel(sourceImage, Color.RED);  }
    public static final WritableImage getGreenChannel(final Image sourceImage) { return getColorChannel(sourceImage, Color.LIME); }
    public static final WritableImage getBlueChannel(final Image sourceImage) { return getColorChannel(sourceImage, Color.BLUE); }
    private static final WritableImage getColorChannel(final Image sourceImage, final Color color) {
        final Node  imageView = new ImageView(sourceImage);
        final Blend blend     = createColorBlend(sourceImage, color);
        imageView.setEffect(blend);

        final SnapshotParameters params = new SnapshotParameters();
        final WritableImage      result = imageView.snapshot(params, null);
        return result;
    }

    public static final Dimension getTextDimension(final String text, final Font font) {
        Text t = new Text(text);
        t.setFont(font);
        double textWidth  = t.getBoundsInLocal().getWidth();
        double textHeight = t.getBoundsInLocal().getHeight();
        t = null;
        Dimension dim = new Dimension(textWidth, textHeight);
        return dim;
    }

    public static final ZoneOffset getZoneOffset() { return getZoneOffset(ZoneId.systemDefault()); }
    public static final ZoneOffset getZoneOffset(final ZoneId zoneId) { return zoneId.getRules().getOffset(Instant.now()); }

    public static final long toMillis(final LocalDateTime dateTime, final ZoneOffset zoneOffset) { return toSeconds(dateTime, zoneOffset) * 1000; }
    public static final long toSeconds(final LocalDateTime dateTime, final ZoneOffset zoneOffset) { return dateTime.toEpochSecond(zoneOffset); }

    public static final double toNumericValue(final LocalDateTime date) { return toNumericValue(date, ZoneId.systemDefault()); }
    public static final double toNumericValue(final LocalDateTime date, final ZoneId zoneId) { return toSeconds(date, getZoneOffset(zoneId)); }

    public static final LocalDateTime toRealValue(final double value) { return secondsToLocalDateTime((long) value); }
    public static final LocalDateTime toRealValue(final double value, final ZoneId zoneId) { return secondsToLocalDateTime((long) value, zoneId); }

    public static final LocalDateTime secondsToLocalDateTime(final long seconds) { return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault()); }
    public static final LocalDateTime secondsToLocalDateTime(final long seconds, final ZoneId zoneId) { return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), zoneId); }

    public static final void saveAsPng(final Node node, final String fileName) {
        final WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
        final String        name     = fileName.replace("\\.[a-zA-Z]{3,4}", "");
        final File          file     = new File(name + ".png");

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (IOException exception) {
            // handle exception here
        }
    }
}
