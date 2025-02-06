package model;

public class Anime {
	String name;
	String season;
	String run;
	String url;

	/*
	 * The Anime class represents an anime with its name and URL.
	 * 
	 * @param name The name of the anime
	 * @param url The URL of the anime
	 */
	public Anime(String name, String url) {
		this.name = name;
		this.url = url;
	}

	/*
	 * The getName method returns the name of the anime.
	 * 
	 * @return The name of the anime
	 */
	public String getName() {
		return name;
	}

	/*
	 * The setName method sets the name of the anime.
	 * 
	 * @param name The name of the anime
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * The getSeason method returns the season of the anime.
	 * 
	 * @return The season of the anime
	 */
	public String getSeason() {
		return season;
	}

	/*
	 * The setSeason method sets the season of the anime.
	 * 
	 * @param season The season of the anime
	 */
	public void setSeason(String season) {
		this.season = season;
	}

	/*
	 * The getRun method returns the run of the anime.
	 * 
	 * @return The run of the anime
	 */
	public String getRun() {
		return run;
	}

	/*
	 * The setRun method sets the run of the anime.
	 * 
	 * @param run The run of the anime
	 */
	public void setRun(String run) {
		this.run = run;
	}

	/*
	 * The getUrl method returns the URL of the anime.
	 * 
	 * @return The URL of the anime
	 */
	public String getUrl() {
		return url;
	}

	/*
	 * The setUrl method sets the URL of the anime.
	 * 
	 * @param url The URL of the anime
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}