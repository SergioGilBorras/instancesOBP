/*
 * Copyright (c) 2025 Sergio Gil Borrás
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to use
 * the Software for non-commercial research purposes only, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.instancesobp.evaluationResults.graphics;

import java.awt.*;
import java.awt.Graphics;

/**
 * Utility class for drawing graphical elements on a panel.
 * This class provides static methods to draw lines, strings, rectangles, and
 * measurement lines (both horizontal and vertical) with support for different
 * graphic orientations.
 *
 * @author Sergio Gil Borrás
 * @version 1.0
 */
public class Utils {

    /**
     * Private constructor to prevent instantiation.
     */
    private Utils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Constant representing horizontal graphic orientation.
     */
    public static final int HORIZONTAL_GRAPHIC_POSITION = 0;
    /**
     * Constant representing vertical graphic orientation.
     */
    public static final int VERTICAL_GRAPHIC_POSITION = 1;

    /**
     * Default font used for drawing text.
     */
    public static Font defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    /**
     * Default color used for drawing routes.
     */
    public static Color routesColor = new Color(250, 0, 0);

    /**
     * Default color used for measures.
     */
    public static Color measuresColor = new Color(0, 0, 255);

    /**
     * Default color used for foreground labels.
     */
    public static Color labelsColor = new Color(10, 10, 10);

    /**
     * Draws a line on the graphics context.
     *
     * @param g               The graphics context to draw on.
     * @param x1              The starting x-coordinate of the line.
     * @param y1              The starting y-coordinate of the line.
     * @param x2              The ending x-coordinate of the line.
     * @param y2              The ending y-coordinate of the line.
     * @param graphicPosition The orientation of the graphic (horizontal or vertical).
     */
    public static void drawLine(Graphics g, int x1, int y1, int x2, int y2, int graphicPosition) {
        if (graphicPosition == HORIZONTAL_GRAPHIC_POSITION) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            g.drawLine(y1, x1, y2, x2);
        }
    }

    /**
     * Draws a string on the graphics context.
     *
     * @param g               The graphics context to draw on.
     * @param text            The text to be drawn.
     * @param x1              The x-coordinate of the text.
     * @param y1              The y-coordinate of the text.
     * @param graphicPosition The orientation of the graphic (horizontal or vertical).
     */
    public static void drawString(Graphics g, String text, int x1, int y1, int graphicPosition) {
        if (graphicPosition == HORIZONTAL_GRAPHIC_POSITION) {
            g.drawString(text, x1, y1);
        } else {
            g.drawString(text, y1, x1);
        }
    }

    /**
     * Draws a rectangle on the graphics context.
     *
     * @param g               The graphics context to draw on.
     * @param x1              The x-coordinate of the top-left corner of the rectangle.
     * @param y1              The y-coordinate of the top-left corner of the rectangle.
     * @param x2              The width of the rectangle.
     * @param y2              The height of the rectangle.
     * @param graphicPosition The orientation of the graphic (horizontal or vertical).
     */
    public static void drawRect(Graphics g, int x1, int y1, int x2, int y2, int graphicPosition) {
        if (graphicPosition == HORIZONTAL_GRAPHIC_POSITION) {
            g.drawRect(x1, y1, x2, y2);
        } else {
            g.drawRect(y1, x1, y2, x2);
        }
    }

    /**
     * Fills a rectangle on the graphics context.
     *
     * @param g               The graphics context to draw on.
     * @param x1              The x-coordinate of the top-left corner of the rectangle.
     * @param y1              The y-coordinate of the top-left corner of the rectangle.
     * @param x2              The width of the rectangle.
     * @param y2              The height of the rectangle.
     * @param graphicPosition The orientation of the graphic (horizontal or vertical).
     */
    public static void fillRect(Graphics g, int x1, int y1, int x2, int y2, int graphicPosition) {
        if (graphicPosition == HORIZONTAL_GRAPHIC_POSITION) {
            g.fillRect(x1, y1, x2, y2);
        } else {
            g.fillRect(y1, x1, y2, x2);
        }
    }

    /**
     * Draws a horizontal measurement line with markers and a label.
     *
     * @param g               The graphics context to draw on.
     * @param text            The label to display on the measurement line.
     * @param x1              The starting x-coordinate of the line.
     * @param x2              The ending x-coordinate of the line.
     * @param y               The y-coordinate of the line.
     * @param graphicPosition The orientation of the graphic (horizontal or vertical).
     */
    public static void drawLineMeasureH(Graphics g, String text, int x1, int x2, int y, int graphicPosition) {
        drawLine(g, x1, y, x2, y, graphicPosition);
        drawLine(g, x1, y + 5, x1, y - 5, graphicPosition);
        drawLine(g, x2, y + 5, x2, y - 5, graphicPosition);
        g.setFont(defaultFont);
        int midPoint = ((x2 - x1) / 2) + x1;
        drawString(g, text, Math.abs(midPoint), y - 2, graphicPosition);
    }

    /**
     * Draws a vertical measurement line with markers and a label.
     *
     * @param g               The graphics context to draw on.
     * @param text            The label to display on the measurement line.
     * @param y1              The starting y-coordinate of the line.
     * @param y2              The ending y-coordinate of the line.
     * @param x               The x-coordinate of the line.
     * @param graphicPosition The orientation of the graphic (horizontal or vertical).
     */
    public static void drawLineMeasureV(Graphics g, String text, int y1, int y2, int x, int graphicPosition) {
        drawLine(g, x, y1, x, y2, graphicPosition);
        drawLine(g, x + 5, y1, x - 5, y1, graphicPosition);
        drawLine(g, x + 5, y2, x - 5, y2, graphicPosition);
        g.setFont(defaultFont);
        int midPoint = ((y2 - y1) / 2) + y1;
        drawString(g, text, x - 25, Math.abs(midPoint), graphicPosition);
    }


    /**
     * Draws a vertical measurement line with markers and a label for front Aisle.
     *
     * @param g               The graphics context to draw on.
     * @param text            The label to display on the measurement line.
     * @param y1              The starting y-coordinate of the line.
     * @param y2              The ending y-coordinate of the line.
     * @param x               The x-coordinate of the line.
     * @param graphicPosition The orientation of the graphic (horizontal or vertical).
     */
    public static void drawLineMeasureVL(Graphics g, String text, int y1, int y2, int x, int graphicPosition) {
        drawLine(g, x, y1, x, y2, graphicPosition);
        drawLine(g, x + 5, y1, x - 5, y1, graphicPosition);
        drawLine(g, x + 5, y2, x - 5, y2, graphicPosition);
        g.setFont(defaultFont);
        int midPoint = ((y2 - y1) / 2) + y1;
        drawString(g, text, x, Math.abs(midPoint), graphicPosition);
    }


    /**
     * Formats a double value into a string with two decimal places.
     *
     * @param value The double value to format.
     * @return A formatted string representing the value.
     */
    public static String formatString(double value) {
        return String.format("%.2f", value);
    }
}