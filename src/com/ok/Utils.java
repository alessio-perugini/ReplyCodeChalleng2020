package com.ok;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {
    public Office readMapFile(String path) {
        if (path == null || path.equals("")) throw new IllegalArgumentException();

        try {//Controlla se esiste il file (path)
            FileChannel.open(Paths.get(path), StandardOpenOption.READ);
        } catch (IOException fe) {//Se non esiste crea il file (path), con dentro un json vuoto
            fe.printStackTrace();
        }

        Office office = new Office();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) { //creo l'out su dove scrivere i byte letti
            String line = reader.readLine();
            int i = 0; // utilizzato per capire quale info devo ricordare
            int k = 0; //utilizzato per capire da dove inzia le info della mappa
            int nDeveloper = -1;
            int nPM = -1;

            while (line != null) {
                if (i == 0) { //office info
                    String[] elments = line.split(" ");
                    office.width = Integer.parseInt(elments[0]);
                    office.height = Integer.parseInt(elments[1]);
                    office.map = new char[office.height][office.width];
                } else if (i <= office.height) { //leggo i piani dell'office
                    char[] floor = line.toCharArray();
                    System.arraycopy(floor, 0, office.map[k], 0, line.length());
                    k++;
                } else if (i == office.height + 1) {
                    nDeveloper = Integer.parseInt(line);
                } else if (nDeveloper != 0) {
                    String[] devInfo = line.split(" ");
                    Developer dev = new Developer();
                    dev.company = devInfo[0];
                    dev.bonus = Integer.parseInt(devInfo[1]);
                    int nSkills = Integer.parseInt(devInfo[2]);
                    int offset = 3;
                    dev.skillsName = new String[nSkills];
                    System.arraycopy(devInfo, offset, dev.skillsName, 0, nSkills);
                    office.lsDevelopers.add(dev);
                    nDeveloper--;
                } else if (nDeveloper == 0 && nPM == -1) {
                    nPM = Integer.parseInt(line);
                } else if (nPM != 0) {
                    ProjectManager pm = new ProjectManager();
                    String[] pmInfo = line.split(" ");
                    pm.company = pmInfo[0];
                    pm.bonus = Integer.parseInt(pmInfo[1]);
                    office.lsPM.add(pm);
                    nPM--;
                }

                line = reader.readLine();
                i++;
            }

            return office;
        } catch (Exception ec) {
            ec.printStackTrace();
        }

        return null;
    }


}
