package com.example.equationsolver.solver;

import android.app.Application;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuadraticEq {

    private static final Pattern EQN = Pattern.compile("([+-]?\\d+)?[Xx]2\\s*([+-]?\\d+)[Xx]\\s*([+-]?\\d+)\\s*=\\s*0");

    public static String parseString(final String eq) {
        System.out.println("eq = " + eq);

            final Matcher matcher = EQN.matcher(eq);
            if (!matcher.matches()) {
                return "invalid Equation";
            }

            final int a = Integer.parseInt(matcher.group(1) == null? "1" : matcher.group(1));
            final int b = Integer.parseInt(matcher.group(2) == null? "1" : matcher.group(2));
            final int c = Integer.parseInt(matcher.group(3) == null? "1" : matcher.group(3));

            System.out.println("a = " + a);
            System.out.println("b = " + b);
            System.out.println("c = " + c);


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
