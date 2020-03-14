package com.ok;

import java.util.*;

public class Office {
    int width;
    int height;
    char[][] map;

    float cutoff;

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }

    ArrayList<Developer> lsDevelopers = new ArrayList<>();
    ArrayList<ProjectManager> lsPM = new ArrayList<>();

    HashMap<String, ArrayList<Developer>> devsWithSkill = new HashMap<>();
    HashMap<String, Float> percentageSkills = new HashMap<>();
    ArrayList<int[]> coordFreeSpace = new ArrayList<>();

    public void populateDevsSkillsHT() {
        for (int i = 0; i < lsDevelopers.size(); i++) {
            Developer dev = lsDevelopers.get(i);
            for (int j = 0; j < dev.skillsName.length; j++) {
                String skillName = dev.skillsName[j];
                ArrayList<Developer> htLsDevs = devsWithSkill.get(skillName);

                if (htLsDevs == null) { //Se non esiste nella HashMap creo la chiave e lo ficco dentro
                    ArrayList<Developer> lsDev = new ArrayList<Developer>();
                    lsDev.add(dev);
                    ArrayList<Developer> devs = devsWithSkill.put(skillName, lsDev);
                    percentageSkills.put(skillName, 0.f);
                } else {
                    htLsDevs.add(dev);
                }
            }
        }

    }

    public float getCutOff() {
        Set<String> setKeys = devsWithSkill.keySet();
        String[] keys = setKeys.toArray(new String[setKeys.size()]);

        int devTotali = 0;//lsDevelopers.size();

        for (int i = 0; i < keys.length; i++) {
            String currentKey = keys[i];
            devTotali += devsWithSkill.get(currentKey).size();
        }

        float max = -1;

        for (int i = 0; i < keys.length; i++) {
            String currentKey = keys[i];
            ArrayList<Developer> lsDevs = devsWithSkill.get(currentKey);
            int devOfCurrSkill = lsDevs.size();
            float percentage = (float) devOfCurrSkill / (float) devTotali;

            if (max < percentage) {
                max = percentage;
            }

            percentageSkills.put(currentKey, percentage);
        }

        return max / keys.length;
    }

    public boolean isRare(String keySkill) {
        return (percentageSkills.get(keySkill) < cutoff);
    }

    public float getPotential(Replyer r) {
        float cutOff=getCutOff();
        int sc=0, sr=0;
        if (r instanceof Developer) {
            for (String skill :((Developer) r).skillsName) {
                if(percentageSkills.get(skill)>cutOff){
                    sc++;
                }
                else{
                    sr++;
                }
            }
        }
        return (sc * sr) + r.bonus;
    }

    public Replyer getBest(){
        float max=-1;
        boolean dev;
        int index=-1;
        ArrayList<Replyer> replyers=new ArrayList<>();
        replyers.addAll(lsDevelopers);
        replyers.addAll(lsPM);
        for(int i=0; i<replyers.size(); i++){
            Replyer r=replyers.get(i);
            if(!r.isChosen()) {
                float p = getPotential(r);
                if (p > max) max = p;
                index = i;
                if (r instanceof Developer) dev = true;
                else dev = false;
            }
        }

        return replyers.get(index);
    }

    public Replyer getBestCompain(Replyer rr){
        float max=-1;
        int index=-1;
        ArrayList<Replyer> replyers=new ArrayList<>();
        replyers.addAll(lsDevelopers);
        replyers.addAll(lsPM);
        for(int i=0; i<replyers.size(); i++){
            Replyer r=replyers.get(i);
            if(!rr.equals(r) && !r.isChosen()) {
                float TP=getTP(rr, r) + getBP(rr, r);
                if(TP>max) {
                    max=TP;
                    index=i;
                }
            }
        }
        return replyers.get(index);
    }

    /*
    false=PM
    true=dev
     */
    public Replyer getBestCompain(Replyer rr, Class<?> c){
        float max=-1;
        int index=-1;
        ArrayList<Replyer> replyers=new ArrayList<>();
        replyers.addAll(lsDevelopers);
        replyers.addAll(lsPM);
        for(int i=0; i<replyers.size(); i++){
            Replyer r=replyers.get(i);
            if(!rr.equals(r) && !r.isChosen() && rr.getClass() == c ) {
                float TP=getTP(rr, r) + getBP(rr, r);
                if(TP>max) {
                    max=TP;
                    index=i;
                }
            }
        }
        return replyers.get(index);
    }

    public float getTP(Replyer r1, Replyer r2){
        float wp=getWP(r1, r2);
        float bp=getBP(r1, r2);
        return wp+bp;
    }

    public float getWP(Replyer r1, Replyer r2){
        if(r1 instanceof Developer && r2 instanceof Developer){
            int shared=0;
            int unshared=0;
            ArrayList<String> r1Skills= ((Developer) r1).getSkills();
            ArrayList<String> r2Skills= ((Developer) r2).getSkills();
            for (String s:r1Skills) {
                if(r2Skills.contains(s)){
                    shared++;
                }
            }
            unshared=(((Developer) r1).getSkills().size() - shared)+(((Developer) r2).getSkills().size() - shared);
            return shared*unshared;
        }
        return 0;
    }

    public float getBP(Replyer r1, Replyer r2){
        float b1=r1.getBonus();
        float b2=r2.getBonus();
        if(r1.getCompany().equals(r2.getCompany())) {
            return b1 * b2;
        }
        return 0;
    }

    //se dev true allora developer else projectManager
    public boolean siPuoMettere(int x, int y, Replyer user) {
        char cella = map[x][y];
        if (user instanceof Developer && cella == '_')
            return true;
        if (user instanceof ProjectManager && cella == 'M')
            return true;

        return false;
    }

    //TODO definire come scegliere il posto adiacente tra quelli disponibili
    public int[] prendiAdiacente(int x, int y) {   //Se sbaglio cordinate
        if (x < 0 || y < 0 || x >= height || y >= width) return null;
        int[] coord = new int[2];
        char cella = map[x][y];

        if (y > 0 && cella != '#') {
            return setCoord(x, y - 1); //LEFT MOVE
        }
        if (y < width - 1 && cella != '#') {
            return setCoord(x, y + 1); //RIGHT MOVE
        }
        if (x > 0 && cella != '#') {
            return setCoord(x - 1, y); //UP MOVE
        }
        if (x < height - 1 && cella != '#') {
            return setCoord(x + 1, y); //DOWN MOVE
        }

        return null;
    }

    public void placeReplyer(int x, int y, Replyer user) {
        if (user instanceof Developer && siPuoMettere(x, y, user)) {
            map[x][y] = '#';
            ((Developer) user).setCoordinateXY(setCoord(x, y));
            ((Developer) user).chosen = true;
        }
        if (user instanceof ProjectManager && siPuoMettere(x, y, user)) {
            map[x][y] = '#';
            ((ProjectManager) user).setCoordinateXY(setCoord(x, y));
            ((ProjectManager) user).chosen = true;
        }
    }

    public void getMyFreePlaces() {
        coordFreeSpace.clear();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char cella = map[i][j];
                if (cella != '#') {
                    int nAdiacenti = numAdiacenti(i, j);
                    int[] info = new int[3];
                    info[0] = i; //x
                    info[1] = j; //y
                    info[2] = nAdiacenti;
                    coordFreeSpace.add(info);
                }
            }
        }

        coordFreeSpace.sort(Comparator.comparingInt(x -> x[2]));
        Collections.reverse(coordFreeSpace);
    }
    public Replyer getBestPlaced()
    {
        float max=-1;
        boolean dev;
        int index=-1;
        ArrayList<Replyer> replyers=new ArrayList<>();
        replyers.addAll(lsDevelopers);
        replyers.addAll(lsPM);
        for(int i=0; i<replyers.size(); i++){
            Replyer r=replyers.get(i);
            if(r.isChosen()) {
                float p = getPotential(r);
                if (p > max) max = p;
                index = i;
                if (r instanceof Developer) dev = true;
                else dev = false;
            }
        }

        return replyers.get(index);

    }

    public void iniziaAPiazzare()
    {
        Replyer best = getBest();
        boolean trovato = false;
        int i=0;
        int[] coordinates = new int[2];
        while(!trovato && i<coordFreeSpace.size())
        {
            if(siPuoMettere(coordFreeSpace.get(i)[0],coordFreeSpace.get(i)[1],best))
            {
                placeReplyer(coordFreeSpace.get(i)[0],coordFreeSpace.get(i)[1],best);
                trovato = true;
                System.out.println(coordFreeSpace.get(i)[0]+":"+coordFreeSpace.get(i)[1]);
                coordinates[0]=coordFreeSpace.get(i)[0];
                coordinates[1]=coordFreeSpace.get(i)[1];
                getMyFreePlaces();
            }
            i++;
        }

        Replyer compaign = getBestCompain(best);
        ArrayList<int[]> adiacenti = adiacenti(coordinates[0], coordinates[1]);
        i=0;
        while(coordFreeSpace.size()>0)
        {

            adiacenti = adiacenti(coordinates[0], coordinates[1]);
            while (adiacenti.size()>0)
            {
                boolean messo = false;
                for (int[] c :adiacenti) {
                    if(siPuoMettere(c[0],c[1], compaign))
                    {
                        placeReplyer(c[0],c[1],compaign);
                        getMyFreePlaces();
                        System.out.println(coordFreeSpace.get(i)[0]+":"+coordFreeSpace.get(i)[1]);
                        coordinates[0]=coordFreeSpace.get(i)[0];
                        coordinates[1]=coordFreeSpace.get(i)[1];
                        messo=true;
                        break;
                    }
                }
                if(!messo)
                {
                    compaign = getBestCompain(best,(compaign.getClass()==Developer.class)?ProjectManager.class: Developer.class);
                    coordinates[0]=coordFreeSpace.get(i)[0];
                    coordinates[1]=coordFreeSpace.get(i)[1];
                }

            }
            best = getBestPlaced();
            i++;
        }



        /*for(int i=0; i<coordFreeSpace.size();i++)
        {

        }*/
    }

    public int numAdiacenti(int x, int y) {   //Se sbaglio cordinate
        if (x < 0 || y < 0 || x >= height || y >= width) return 0;
        char cella = map[x][y];
        int postiAdiacenti = 0;

        if (y > 0 && cella != '#') {
            postiAdiacenti++; // MOVE
        }
        if (y < width - 1 && cella != '#') {
            postiAdiacenti++; // MOVE
        }
        if (x > 0 && cella != '#') {
            postiAdiacenti++; // MOVE
        }
        if (x < height - 1 && cella != '#') {
            postiAdiacenti++; // MOVE
        }


        return postiAdiacenti;
    }
    public ArrayList<int[]> adiacenti (int x, int y)
    {
        char cella = map[x][y];
        ArrayList<int[]> adiacenti = new ArrayList<>();
        int postiAdiacenti = 0;
        if (y > 0 && cella != '#') {
            adiacenti.add(new int[]{x,y-1});
        }
        if (y < width - 1 && cella != '#') {
            adiacenti.add(new int[]{x,y+1});
        }
        if (x > 0 && cella != '#') {
            adiacenti.add(new int[]{x-1,y});
        }
        if (x < height - 1 && cella != '#') {
            adiacenti.add(new int[]{x+1,y});
        }
        return adiacenti;
    }


    public void printOtuput() {
        //Print DEVS
        for (int i = 0; i < lsDevelopers.size(); i++) {
            Developer dev = lsDevelopers.get(i);
            if (!dev.chosen) {
                System.out.println("X");
            } else {
                int[] coord = dev.getCoordinateXY();
                System.out.println(coord[0] + " " + coord[1]);
            }
        }

        //print PM
        for(int i = 0; i < lsPM.size(); i++){
            ProjectManager pm = lsPM.get(i);
            if(!pm.chosen){
                System.out.println("X");
            }else{
                int[] coord =  pm.getCoordinateXY();
                System.out.println(coord[0] + " " + coord[1]);
            }
        }
    }

    private int[] setCoord(int x, int y) {
        int[] coord = new int[2];
        coord[0] = x;
        coord[1] = y;
        return coord;
    }
}
