package ansc;

/**
 * Imports used.
 */

import model.Anime;
import model.AnimeListReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;

public class ANSChecker {

	private final Map<String, JTextField> tfAnimeSeasonMap = new HashMap<>();
	private final Map<String, JTextField> tfAnimeRunMap = new HashMap<>();
	/**
	 * Consts
	 */
	private final Color COLOR_GREEN = new Color(0, 255, 128);
	private final Color COLOR_CYAN = new Color(128, 255, 255);
	private final Color COLOR_LIGHTGREY = new Color(210, 210, 210);
	private final Font FONT_TEXTFIELD = new Font("Segoe UI Black", Font.BOLD, 16);
	private final Font FONT_BUTTON = new Font("Tahoma", Font.PLAIN, 15);
	private final Font FONT_ANIME_TF = new Font("Tahoma", Font.BOLD, 13);
	private final String ANIME_LIST_PATH = "list.txt";
	/**
	 * GUI elements
	 */
	private JFrame frmAnimeNewSeason;
	private JPanel dragPanel;
	private JTextField tfAnimeHeadline;
	private JTextField tfAnimeSeasonHeadline;
	private JTextField tfAnimeRunHeadline;
	private JTextField tfControls;
	private JTextField tfSeason;
	private JTextField tfAnimeCount;
	private JTextField tfDuration;
	private JTextField tfDate;
	private JLabel picLoading;
	private JLabel picLoaded;
	/**
	 * Data structure
	 */
	private List<Anime> animeList;
	/**
	 * Timer
	 */
	private long startTime = 0;
	private long duration = 0;
	/**
	 * Date
	 */
	private LocalDate currentDate = LocalDate.now();
	private int currentYear = currentDate.getYear();

	/**
	 * Create the application.
	 *
	 * @throws IOException if file on ANIME_LIST_PATH is missing or corrupted
	 */
	public ANSChecker() throws IOException {
		this.animeList = new AnimeListReader(new File(ANIME_LIST_PATH)).getAnimeList();
		this.initStandardGuiElements();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				ANSChecker window = new ANSChecker();
				window.frmAnimeNewSeason.setVisible(true);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error: Check failed!");
				e.printStackTrace();
			}
		});
	}

	/*
	 * Initialize the contents of the frame.
	 */
	private void initStandardGuiElements() {

		/*
		 * Frame set.
		 */
		this.frmAnimeNewSeason = new JFrame();
		this.frmAnimeNewSeason.setResizable(false);
		this.frmAnimeNewSeason.setTitle("Anime New Season Checker");
		this.frmAnimeNewSeason.setBounds(100, 100, 1415, 655);
		this.frmAnimeNewSeason.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/*
		 * Program icon set.
		 */
		Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Icon.png"));
		this.frmAnimeNewSeason.setIconImage(icon);

		/*
		 * Used DragLayout for absolute layout and scroll bar support.
		 */
		DragLayout dl_dragPanel = new DragLayout();
		dl_dragPanel.setUsePreferredSize(false);
		this.dragPanel = new JPanel(dl_dragPanel);
		this.frmAnimeNewSeason.getContentPane().add(new JScrollPane(dragPanel));

		/*
		 * Now all elements in the JFrame are being set and added to the DragLayout.
		 */
		this.tfAnimeHeadline = initTextField(dragPanel, COLOR_GREEN, SwingConstants.CENTER, 10, 10, 502, 44, FONT_TEXTFIELD, false, "Anime:", 10);

		this.tfAnimeSeasonHeadline = initTextField(dragPanel, COLOR_GREEN, SwingConstants.CENTER, 522, 10, 160, 44, FONT_TEXTFIELD, false, "Newest season?", 10);

		this.tfAnimeRunHeadline = initTextField(dragPanel, COLOR_GREEN, SwingConstants.CENTER, 692, 10, 400, 44, FONT_TEXTFIELD, false, "Anime runtime:", 10);

		this.tfControls = initTextField(dragPanel, COLOR_GREEN, SwingConstants.CENTER, 1102, 10, 274, 44, FONT_TEXTFIELD, false, "Controls:", 10);

		this.tfSeason = initTextField(dragPanel, new Color(255, 255, 128), SwingConstants.CENTER, 1102, 64, 274, 44, new Font("Tahoma", Font.ITALIC, 15), true, "Enter season number", 10);

		/*
		 * Icons which are used in the JFrame are being set and picLoading added to the
		 * DragLayout (visible now).
		 */
		Image icon1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Loading.png"));
		this.picLoading = new JLabel(new ImageIcon(icon1));
		this.picLoading.setBounds(1102, 314, 274, 293);
		this.dragPanel.add(picLoading);

		Image icon2 = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Loaded.png"));
		this.picLoaded = new JLabel(new ImageIcon(icon2));

		/*
		 * Search button triggers the check() function and adds the content to the
		 * JTextFields if it is successful.
		 */
		JButton btnSearch = new JButton("Search for new seasons");
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSearch.setBounds(1102, 114, 274, 44);
		btnSearch.addActionListener(e -> checkButtonPressed());
		this.dragPanel.add(btnSearch);

		this.tfAnimeCount = initTextField(dragPanel, COLOR_CYAN, SwingConstants.CENTER, 1102, 164, 274, 44, FONT_BUTTON, false, "You have " + this.animeList.size() + " anime titles listed!", 10);

		this.tfDuration = initTextField(dragPanel, COLOR_CYAN, SwingConstants.CENTER, 1102, 214, 274, 44, FONT_BUTTON, false, null, 10);
		
		this.tfDate = initTextField(dragPanel, COLOR_CYAN, SwingConstants.CENTER, 1102, 264, 274, 44, FONT_BUTTON, false, "© 2022-" + currentYear + " mexikoedi", 10);

		this.initAnimeListGuiElements();
	}

	private void initAnimeListGuiElements() {
		/*
		 * JTextFields are being created and added to DragLayout for all anime titles
		 * which are in the .txt file.
		 */
		// counter is the same, but you only need one instead of one for each
		int i = 0;
		for (Anime anime : this.animeList) {
			initTextField(this.dragPanel, COLOR_LIGHTGREY, null, 10, 64 + i, 502, 44, FONT_ANIME_TF, false, anime.getName(), 10);

			// Maps are <Key, Value>-Pairs, so u can get the TF of an anime with his name
			this.tfAnimeSeasonMap.put(anime.getName(), initTextField(this.dragPanel, COLOR_LIGHTGREY, null, 522, 64 + i, 160, 44, FONT_ANIME_TF, false, null, 10));

			this.tfAnimeRunMap.put(anime.getName(), initTextField(this.dragPanel, COLOR_LIGHTGREY, null, 692, 64 + i, 400, 44, FONT_ANIME_TF, false, null, 10));

			i += 50;
		}
	}

	private JTextField initTextField(JPanel dragPanel, Color backgroundColor, Integer horizontalAlignment, Integer boundX, Integer boundY, Integer boundWidth, Integer boundHeight, Font font, Boolean editable, String text, Integer numberOfColumns) {
		JTextField textField = new JTextField();
		textField.setBackground(backgroundColor);

		if (horizontalAlignment != null) {
			textField.setHorizontalAlignment(horizontalAlignment);
		}

		textField.setBounds(boundX, boundY, boundWidth, boundHeight);
		textField.setFont(font);
		textField.setEditable(editable);

		if (text != null) {
			textField.setText(text);
		}

		textField.setColumns(numberOfColumns);
		dragPanel.add(textField);

		return textField;
	}

	private void checkButtonPressed() {
		if (tfSeason.getText().isEmpty() || !tfSeason.getText().matches("\\d")) {
			JOptionPane.showMessageDialog(null, "Error: Please enter the season number!");
			return;
		}

		// run check logic which modifies the data structure
		this.check();

		/*
		 * picLoading removed from the DragLayout (not visible now) and picLoaded added
		 * instead (visible now).
		 */
		this.dragPanel.remove(this.picLoading);
		this.picLoaded.setBounds(1102, 314, 274, 293);
		this.dragPanel.add(this.picLoaded);
		this.dragPanel.repaint();

		// refresh the gui elements for each anime
		this.animeList.forEach(anime -> {
			this.tfAnimeSeasonMap.get(anime.getName()).setText(anime.getSeason());
			this.tfAnimeRunMap.get(anime.getName()).setText(anime.getRun());
		});
	}

	/**
	 * Internal check which searches for new anime titles from the .txt file and
	 * gets all the data (seasons/years) from the IMDB website with the help of the
	 * external JSoup library. Runtime length in nanoseconds is also recorded here
	 * and later converted to seconds.
	 */
	private void check() {
		this.startTime = System.nanoTime();

		this.animeList = this.animeList.parallelStream().peek(anime -> {
			Document website;
			try {
				website = Jsoup.connect(anime.getUrl()).get();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error: " + anime.getName() + " not available!");
				System.exit(0);
				return;
			}

			// Format: "%d seasons"
			Element seasonElement = website.getElementById("browse-episodes-season");
			String seasonData;

			// When seasonElement is null there is only one season
			if (seasonElement != null) {
				seasonData = seasonElement.attr("aria-label");
			} else {
				seasonData = "1 season";
			}

			Integer numberOfSeasons = Integer.valueOf((seasonData.split(" "))[0]);
			Integer expectedNumberOfSeasons = Integer.valueOf(this.tfSeason.getText());
			String seasonText;

			if (numberOfSeasons.equals(expectedNumberOfSeasons)) {
				seasonText = "Season " + numberOfSeasons + " newest!";
			} else {
				seasonText = numberOfSeasons.equals(1) ? "1 season!" : numberOfSeasons + " seasons!";
			}

			anime.setSeason(seasonText);
			String yearData;

			if (website.select("#browse-episodes-year.ipc-simple-select__input").text().contains("See all")) {
				yearData = website.select("#browse-episodes-year.ipc-simple-select__input").text();
				yearData = yearData.replaceAll("[^0-9]", "");
			} else {
				yearData = website.select("a.ipc-btn span.ipc-btn__text").text();
				yearData = yearData.replaceAll("[^0-9]", "");
				yearData = yearData.substring(yearData.length() - 4);
			}

			String runtime = yearData.replaceAll("(.{" + 4 + "})", "$1 ").trim();
			anime.setRun(runtime);
		}).toList();

		duration = System.nanoTime() - startTime;
		long convert = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
		String sDuration = convert + "";
		tfDuration.setText("It took " + sDuration + " seconds!");
	}
}