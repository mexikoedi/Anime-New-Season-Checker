package model;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnimeListReader {

    File animeListFile;
    List<Anime> animeList;

    public AnimeListReader(File animeListFile) throws IOException {
        this.animeListFile = animeListFile;
        this.animeList = this.parseAnimeList();
    }

    private List<Anime> parseAnimeList() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(animeListFile));
        List<Anime> result = new ArrayList<>();

        String textLine;

        while ((textLine = br.readLine()) != null) {
            String animeName;
            String animeURL;

            if (textLine.lastIndexOf("anime:") >= 0) {
                animeName = textLine.substring(textLine.lastIndexOf(":") + 1);

                textLine = br.readLine();
                if (textLine.lastIndexOf("source:") >= 0) {
                    String address = textLine.substring(textLine.lastIndexOf(":") + 1);
                    animeURL = "https:" + address;
                } else {
                    throw new IOException("ERROR: File can't be parsed!");
                }

                result.add(new Anime(animeName, animeURL));
            }
        }

        return result;
    }

    public List<Anime> getAnimeList() {
        return animeList;
    }
}
