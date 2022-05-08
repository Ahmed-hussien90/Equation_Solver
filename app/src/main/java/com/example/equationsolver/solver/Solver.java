package com.example.equationsolver.solver;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solver {

    private static final Pattern EQN = Pattern.compile("([+-]?\\d+)?([A-Za-z]2)?\\s*([+-]?\\d+)?([A-Za-z])?\\s*([+-]?\\d+)?\\s*=\\s*0");
    private static final Pattern RMD = Pattern.compile("([+-]?\\d+)\\s*[%]\\s*([+-]?\\d+)");


    public static String parseString(final String eq) {

        System.out.println("eq = " + eq);

        Matcher matcher = EQN.matcher(eq);


        if (!matcher.matches()) {

            matcher = RMD.matcher(eq);

            if (matcher.matches()) {

                final int a = Integer.parseInt(matcher.group(1) == null ? "1" : matcher.group(1));
                final int b = Integer.parseInt(matcher.group(2) == null ? "1" : matcher.group(2));

                return (a + " reminder " + b + " equal " + (a % b));

            }

            Expression e = new Expression(eq);
            double d = e.calculate();

            if (Double.isNaN(d))
                return "Wrong expression";
            else
                return ("The solution is " + d);

        }

        int a = Integer.parseInt(matcher.group(1) == null ? "1" : matcher.group(1));
        int b = Integer.parseInt(matcher.group(3) == null ? "1" : matcher.group(3));
        int c = Integer.parseInt(matcher.group(5) == null ? "0" : matcher.group(5));

        if (matcher.group(2) == null) {
            b = a;
            a = 0;
        }
        if (matcher.group(4) == null) {
            c = b;
            b = 0;
        }

        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        if (a == 0) {
            return ("The roots is " + (-1 * c));
        }

        double d = b * b - 4.0 * a * c;

        System.out.println("d = " + d);

        if (d > 0.0) {
            double r1 = (-b + Math.pow(d, 0.5)) / (2.0 * a);
            double r2 = (-b - Math.pow(d, 0.5)) / (2.0 * a);
            return ("The roots are " + r1 + " and " + r2);
        } else if (d == 0.0) {
            double r1 = -b / (2.0 * a);
            return ("The root is " + r1);
        } else {
            return ("Roots are not real.");
        }
    }
}
