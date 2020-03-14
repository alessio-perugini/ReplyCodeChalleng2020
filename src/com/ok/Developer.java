package com.ok;

import javax.swing.text.DefaultEditorKit;
import java.util.ArrayList;

public class Developer extends Replyer {
    String[] skillsName;


    public Developer() {
        chosen = false;
    }

    public ArrayList<String> getSkills(){
        ArrayList<String> skills=new ArrayList<>();
        for(String s: skillsName){
            skills.add(s);
        }
        return skills;
    }
}
