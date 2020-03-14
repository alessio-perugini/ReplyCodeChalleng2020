package com.ok;

import java.util.ArrayList;

public class Main {

    static String[] INPUT_FILES =
            {"a_solar.txt",
                    "b_dream.txt",
                    "c_soup.txt",
                    "d_maelstrom.txt",
                    "e_igloos.txt",
                    "f_glitch.txt"
            };

    public static void main(String[] args) {
        String path = INPUT_FILES[0];
        Office office = new Utils().readMapFile(path);
        office.populateDevsSkillsHT();
        float cutoff = office.getCutOff();
        office.setCutoff(cutoff);
        office.getMyFreePlaces();
        office.iniziaAPiazzare();
        office.printOtuput();

        //Se una skill è > di cutoff allora è comune else rara

    }

}
