package com.example.equationsolver.solver;

import android.util.Log;
import android.widget.Switch;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solver {

    private static final Pattern QEQN = Pattern.compile("([+-]?\\d+)?([A-Za-z]2)?\\s*([+-]?\\d*)?([A-Za-z])?\\s*([+-]\\d+)?\\s*=\\s*([+-]?\\d+)");
    private static final Pattern RMD = Pattern.compile("([+-]?\\d+)\\s*[%]\\s*([+-]?\\d+)");
    private static final Pattern CEQN = Pattern.compile("([+-]?\\d+)?([A-Za-z]3)?\\s*([+-]?\\d*)?([A-Za-z]2)?\\s*([+-]?\\d*)?([A-Za-z])?\\s*([+-]\\d+)?\\s*=\\s*([+-]?\\d+)");

    public static String parseString(final String eq) {

        System.out.println("eq = " + eq);

        try {
            return LEquation.solveLinearEQ(eq);
        }catch (Exception e){
            Log.e("Solver" , "wrong");
        }

        if (QEQN.matcher(eq).matches()) {

            return QuadraticEQ.solveQuadratic(QEQN.matcher(eq));

        } else if (RMD.matcher(eq).matches()) {

            Matcher matcher = RMD.matcher(eq);
            matcher.find();
            final int a = Integer.parseInt(matcher.group(1) == null ? "1" : matcher.group(1));
            final int b = Integer.parseInt(matcher.group(2) == null ? "1" : matcher.group(2));

            return (a + " reminder " + b + " equal " + (a % b));

        } else if (CEQN.matcher(eq).matches()) {

            return solveCubic(CEQN.matcher(eq));

        } else {

            Expression e = new Expression(eq);
            double d = e.calculate();

            if (Double.isNaN(d))
                return "Wrong expression";
            else
                return ("The solution is " + d);

        }

    }

    private static String solveCubic(Matcher matcher) {

        String result = "";

        matcher.find();

        int a = Integer.parseInt(matcher.group(1) == null ? "1" : matcher.group(1));

        int b;

        if(matcher.group(3).equals("-")){
            b = -1;
        }else if(matcher.group(3).equals("+")){
            b = 1;
        }else if(matcher.group(3).equals("")){
            b =1;
        }else{
            b = Integer.parseInt(matcher.group(3));
        }

        int c;

        if(matcher.group(5).equals("-")){
            c = -1;
        }else if(matcher.group(5).equals("+")){
            c = 1;
        }else if(matcher.group(5).equals("")){
            c =1;
        }else{
            c = Integer.parseInt(matcher.group(5));
        }

        int d = Integer.parseInt(matcher.group(7) == null ? "0" : matcher.group(7));


        if (matcher.group(2) == null) {
            a = 0;
        }
        if (matcher.group(6) == null && matcher.group(4) == null) {
            d = b;
        }

        if (matcher.group(6) == null && matcher.group(4) != null) {
            d = c;
        }

        if (matcher.group(4) == null) {
            b = 0;
        }
        if (matcher.group(6) == null) {
            c = 0;
        }


        int f = Integer.parseInt(matcher.group(8));
        d=d-f;


        System.out.println("Cubic");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);
        System.out.println("d = " + d);
        System.out.println("f = " + f);

        Cubic cubic = new Cubic();
        cubic.solve(a, b, c, d);
        result += "x1 = " + Math.round(cubic.x1) + "\n";
        if (cubic.nRoots == 3) {
            result += "x2 = " + Math.round(cubic.x2) + "\n";
            result += "x3 = " + Math.round(cubic.x3) + "\n";
        }

        return (result);

    }
}