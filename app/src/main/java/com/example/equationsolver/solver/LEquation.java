package com.example.equationsolver.solver;

public class LEquation
{


    public static String solveLinearEQ(String eqn)
    {
        float ans = 0;
        float coeffSum = 0;
        float constSum = 0;
        float coeffx[] = new float[100];
        float[] constant = new float[100];
        eqn += "\n";

        for (int i = 0, j = 0, k = 0; i < eqn.length() - 1;)
        {
            if (eqn.charAt(i + 1) == 'x' && i < eqn.indexOf("="))
            {
                if (i != 0 && eqn.charAt(i - 1) == '-')
                {
                    String x = eqn.substring(i, i + 1);
                    if (x != "+" && x != "-")
                    {
                        int n = -(Integer.parseInt(x, 10));
                        coeffx[j++] = n;
                    }
                } else
                {
                    String x = eqn.substring(i, i + 1);
                    if (x != "+" && x != "-")
                    {
                        int n = Integer.parseInt(x, 10);
                        coeffx[j++] = n;
                    }
                }
                i += 3;
            }
            if (eqn.charAt(i + 1) == 'x' && i > eqn.indexOf("="))
            {
                if (eqn.charAt(i - 1) == '-')
                {
                    String x = eqn.substring(i, i + 1);
                    if (x != "+" && x != "-")
                    {
                        int n = Integer.parseInt(x, 10);
                        coeffx[j++] = n;
                    }
                } else
                {
                    String x = eqn.substring(i, i + 1);
                    if (x != "+" && x != "-")
                    {
                        int n = -(Integer.parseInt(x, 10));
                        coeffx[j++] = n;
                    }
                }
                i += 3;
            }
            if (eqn.charAt(i + 1) != 'x' && i < eqn.indexOf("="))
            {
                if (eqn.charAt(i - 1) == '-')
                {
                    String x = eqn.substring(i, i + 1);
                    if (x != "+" && x != "-")
                    {
                        int n = -(Integer.parseInt(x, 10));
                        constant[k++] = n;
                    }
                } else
                {
                    String x = eqn.substring(i, i + 1);
                    if (x != "+" && x != "-")
                    {
                        int n = Integer.parseInt(x, 10);
                        constant[k++] = n;
                    }
                }
                i += 2;
            }
            if (eqn.charAt(i + 1) != 'x' && i > eqn.indexOf("="))
            {
                if (eqn.charAt(i - 1) == '-')
                {
                    String x = eqn.substring(i, i + 1);
                    if (x != "+" && x != "-")
                    {
                        int n = Integer.parseInt(x, 10);
                        constant[k++] = n;
                    }
                } else
                {
                    String x = eqn.substring(i, i + 1);
                    if (x != "+" && x != "-")
                    {
                        int n = -(Integer.parseInt(x, 10));
                        constant[k++] = n;
                    }
                }
                i += 2;
            }

        }
        for (int i = 0; i < coeffx.length; i++)
            coeffSum += coeffx[i];
        for (int i = 0; i < constant.length; i++)
            constSum += constant[i];
        ans = constSum / coeffSum;

        return "x = " + (-ans);

    }

}
