package com.example.equationsolver.solver;


import java.util.regex.Matcher;

public class QuadraticEQ {

    public static String solveQuadratic(Matcher matcher) {

        matcher.find();

        int a = Integer.parseInt(matcher.group(1) == null ? "1" : matcher.group(1));
        int b ;

        if(matcher.group(3).equals("-")){
            b = -1;
        }else if(matcher.group(3).equals("+")){
            b = 1;
        }else if(matcher.group(3).equals("")){
            b =1;
        }
        else{
            b = Integer.parseInt(matcher.group(3));
        }
        int c = Integer.parseInt(matcher.group(5) == null ? "0" : matcher.group(5));

        if (matcher.group(2) == null) {
            a = 0;
        }
        if (matcher.group(4) == null) {
            c=b;
            b = 0;
        }

        int f = Integer.parseInt(matcher.group(6));
        c=c-f;

        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);
        System.out.println("f = " + f);



        double d = b * b - 4.0 * a * c;

        System.out.println("d = " + d);

        if(a==0){
            return "The root is x = " + ((-1*c)/b);
        }

        if(b==0){
            return "The roots are " + -1 * Math.sqrt(-c/a)  +" and " + Math.sqrt(-c/a);
        }

        if (d > 0.0) {
            double r1 = (-b + Math.pow(d, 0.5)) / (2.0 * a);
            double r2 = (-b - Math.pow(d, 0.5)) / (2.0 * a);
            return ("The roots are \n  x = " + r1 + "\n OR  x = " + r2);
        } else if (d == 0.0) {
            double r1 = -b / (2.0 * a);
            return ("The root is  x = " + r1);
        } else {
            return ("Roots are not real.");
        }

    }
}
