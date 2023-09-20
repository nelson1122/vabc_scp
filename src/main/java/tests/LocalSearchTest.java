package main.java.tests;

import main.java.BeeColony;
import main.java.LocalSearch;
import main.java.Problem;
import main.java.utils.CommonUtils;
import main.java.variables.AbcVars;

import java.util.Arrays;
import java.util.BitSet;

public class LocalSearchTest {

    private final static int MAX_CICLE = 200;

    public static void main(String[] args) {
        try {
            Problem.read("src/main/resources/scpnrf5.txt");
            AbcVars vr = new AbcVars(140);
            BeeColony bee = new BeeColony(vr);
            bee.initial();

            CommonUtils cUtils = new CommonUtils(vr);
            LocalSearch ls = new LocalSearch(vr);
            int[] s = {0, 1, 2, 3, 6, 7, 9, 13, 14, 15, 17, 25, 28, 32, 34, 36, 44, 51, 57};

            BitSet test = new BitSet();
            Arrays.stream(s).forEach(test::set);
            ls.setFoodNumber(0);

            int t = 0;
            while (t < MAX_CICLE) {
                test = ls.apply(test);

                System.out.println(test);
                System.out.println(cUtils.calculateFitnessOne(test));

                t++;
            }


        } catch (Exception ex) {
            System.out.println("ERROR");
        }

    }
}
