package main.java.tests;

import main.java.BeeColony;
import main.java.LocalSearch;
import main.java.Problem;
import main.java.utils.CommonUtils;
import main.java.variables.AbcVars;

import java.util.Arrays;
import java.util.BitSet;

public class LocalSearchTest {

    private final static int MAX_CICLE = 20;

    public static void main(String[] args) {
        try {
            Problem.read("src/main/resources/scpnrg2.txt");
            AbcVars vr = new AbcVars(140);
            BeeColony bee = new BeeColony(vr);
            bee.initial();

            CommonUtils cUtils = new CommonUtils(vr);
            LocalSearch ls = new LocalSearch(vr);
            int[] s1 = {0, 2, 3, 6, 7, 9, 13, 14, 15, 17, 25, 28, 32, 34, 36, 44, 51, 57}; //19
            int[] s2 = {0, 1, 2, 3, 5, 7, 9, 11, 14, 15, 16, 23, 25, 34, 36, 50, 51}; //17
            int[] s3 = {0, 1, 2, 3, 5, 7, 9, 11, 14, 15, 16, 23, 25, 34, 36, 50}; //16
            int[] s4 = {0, 1, 4, 5, 6, 9, 11, 16, 17, 25, 34, 36, 38, 40, 97}; // 16

            // 156
            int[] s5 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 41, 42, 43, 48, 49, 50, 53, 54, 55, 57, 58, 59, 60, 61, 68, 72, 73, 77, 80, 89, 93, 94, 97, 98, 100, 102, 105, 109, 112, 113, 114, 117, 121, 125, 126, 128, 129, 131, 132, 137, 138, 140, 147, 154, 160, 163, 166, 175, 176, 181, 193, 211, 216, 221, 225, 234, 235, 236, 255, 257, 276, 287, 293, 306, 338, 387, 475};

            int[] s6 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 35, 36, 41, 42, 43, 48, 49, 50, 53, 54, 55, 57, 58, 59, 60, 61, 68, 72, 73, 74, 77, 80, 89, 93, 94, 97, 98, 100, 102, 105, 109, 112, 113, 114, 117, 121, 125, 126, 128, 129, 131, 132, 137, 138, 140, 147, 154, 160, 163, 166, 175, 176, 181, 193, 211, 216, 221, 225, 234, 235, 236, 255, 257, 276, 287, 293, 306, 338, 387, 475};

            BitSet test = new BitSet();

            Arrays.stream(s5).forEach(test::set);
            ls.setFoodNumber(0);


            int t = 0;

            BitSet fs = vr.getFoodSource(28);
            while (t < MAX_CICLE) {
                fs = ls.apply(fs);
                t++;
            }


//
//            System.out.println(fs);
//            System.out.println(cUtils.calculateFitnessOne(test));


        } catch (Exception ex) {
            System.out.println("ERROR");
            ex.printStackTrace();
        }

    }
}
