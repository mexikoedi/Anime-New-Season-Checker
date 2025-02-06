package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AnimeListReader {
	File animeListFile;
	List<Anime> animeList;

	/*
	 * The AnimeListReader class reads the anime list from a file and stores it in a
	 * list.
	 * 
	 * @param animeListFile The file containing the anime list
	 * @throws IOException if the file can't be read or parsed correctly
	 */
	public AnimeListReader(File animeListFile) throws IOException {
		this.animeListFile = animeListFile;
		this.animeList = this.parseAnimeList();
	}

	/*
	 * This method returns the anime list as a list of Anime objects.
	 * 
	 * @return The anime list as a list of Anime objects
	 */
	public List<Anime> getAnimeList() {
		return animeList;
	}

	/*
	 * This method reads the anime list from the file and returns it as a list of
	 * Anime objects.
	 * 
	 * @return The anime list as a list of Anime objects
	 * @throws IOException if the file can't be read or parsed correctly
	 */
	private List<Anime> parseAnimeList() throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(animeListFile))) {
			List<Anime> result = new ArrayList<>();
			String textLine;

			while ((textLine = br.readLine()) != null) {
				String animeName;
				String animeURL;

				if (textLine.lastIndexOf("anime:") >= 0) {
					animeName = textLine.substring(textLine.indexOf(":") + 1);
					textLine = br.readLine();

					if (textLine != null && textLine.lastIndexOf("source:") >= 0) {
						animeURL = textLine.substring(textLine.indexOf(":") + 1);
					} else {
						throw new IOException("ERROR: File can't be parsed!");
					}

					result.add(new Anime(animeName, animeURL));
				}
			}

			return result;
		}
	}
}