package ansc;

// Imports
import model.Anime;
import model.AnimeListReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDate;

public class ANSChecker {
	// Maps
	private final Map<String, JTextField> tfAnimeSeasonMap = new HashMap<>();
	private final Map<String, JTextField> tfAnimeRunMap = new HashMap<>();
	// Constants
	private final Color COLOR_GREEN = new Color(0, 255, 128);
	private final Color COLOR_CYAN = new Color(128, 255, 255);
	private final Color COLOR_LIGHTGREY = new Color(210, 210, 210);
	private final Font FONT_TEXTFIELD = new Font("Segoe UI Black", Font.BOLD, 16);
	private final Font FONT_BUTTON = new Font("Tahoma", Font.PLAIN, 15);
	private final Font FONT_ANIME_TF = new Font("Tahoma", Font.BOLD, 13);
	private final String ANIME_LIST_PATH = "list.txt";
	private final String ANIME_EXPORT_DIR = "ansc_export";
	// GUI elements
	private JFrame frmAnimeNewSeason;
	private JPanel dragPanel;
	private JTextField tfSeason;
	private JButton btnSearch;
	private JButton btnDataExport;
	private JProgressBar progressBar;
	private JTextField tfDuration;
	private JLabel picLoading;
	private JLabel picLoaded;
	// Data structure
	private List<Anime> animeList;
	// Class variables
	private long startTime = 0;
	private long duration = 0;
	private boolean hasExecutedOnce = false;

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
				JOptionPane.showMessageDialog(null, "Check failed! Text file missing?", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initStandardGuiElements() {
		// UI set
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			showDialog("Unexpected error in UI adjustment! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		// Frame set
		this.frmAnimeNewSeason = new JFrame();
		this.frmAnimeNewSeason.setResizable(false);
		this.frmAnimeNewSeason.setTitle("Anime New Season Checker");
		this.frmAnimeNewSeason.setBounds(100, 100, 1415, 655);
		this.frmAnimeNewSeason.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Program icon set
		Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Icon.png"));
		this.frmAnimeNewSeason.setIconImage(icon);
		// Used DragLayout for absolute layout and scroll bar support
		DragLayout dl_dragPanel = new DragLayout();
		dl_dragPanel.setUsePreferredSize(false);
		this.dragPanel = new JPanel(dl_dragPanel);
		JScrollPane scrollPane = new JScrollPane(dragPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		this.frmAnimeNewSeason.getContentPane().add(scrollPane);
		// Now all elements in the JFrame are being set and added to the DragLayout
		initTextField(dragPanel, COLOR_GREEN, SwingConstants.CENTER, 10, 10, 502, 44, FONT_TEXTFIELD, false, false,
				"Anime:", 10);
		initTextField(dragPanel, COLOR_GREEN, SwingConstants.CENTER, 522, 10, 160, 44, FONT_TEXTFIELD, false, false,
				"Newest season?", 10);
		initTextField(dragPanel, COLOR_GREEN, SwingConstants.CENTER, 692, 10, 400, 44, FONT_TEXTFIELD, false, false,
				"Anime runtime:", 10);
		initTextField(dragPanel, COLOR_GREEN, SwingConstants.CENTER, 1102, 10, 274, 44, FONT_TEXTFIELD, false, false,
				"Controls:", 10);
		this.tfSeason = initTextField(dragPanel, new Color(255, 255, 128), SwingConstants.CENTER, 1102, 64, 274, 44,
				new Font("Tahoma", Font.ITALIC, 15), true, true, "Enter season number (optional)", 10);
		/*
		 * Search button triggers the check() function and adds the content to the
		 * JTextFields if it is successful.
		 */
		btnSearch = new JButton("Search for new seasons");
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSearch.setBounds(1102, 114, 274, 44);
		btnSearch.addActionListener(_ -> checkButtonPressed());
		this.dragPanel.add(btnSearch);
		/*
		 * Export button triggers the exportData() function and saves the data in a JSON
		 * file.
		 */
		btnDataExport = new JButton("Export data");
		btnDataExport.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnDataExport.setEnabled(false);
		btnDataExport.addActionListener(_ -> exportData(this.animeList));
		btnDataExport.setBounds(1102, 164, 274, 44);
		this.dragPanel.add(btnDataExport);
		/*
		 * Progress bar is not implemented yet. It will be used to show the progress of
		 * the program.
		 */
		progressBar = new JProgressBar(0, 100);
		progressBar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		progressBar.setStringPainted(true);
		progressBar.setBounds(1102, 214, 274, 44);
		this.dragPanel.add(progressBar);
		/*
		 * TextFields for the total amount of anime titles, the runtime of the program
		 * and the copyright notice are being set.
		 */
		LocalDate currentDate = LocalDate.now();
		int currentYear = currentDate.getYear();
		initTextField(dragPanel, COLOR_CYAN, SwingConstants.CENTER, 1102, 264, 274, 44, FONT_BUTTON, false, false,
				"You have " + this.animeList.size() + " anime titles listed!", 10);
		this.tfDuration = initTextField(dragPanel, COLOR_CYAN, SwingConstants.CENTER, 1102, 314, 274, 44, FONT_BUTTON,
				false, false, null, 10);
		initTextField(dragPanel, COLOR_CYAN, SwingConstants.CENTER, 1102, 364, 274, 44, FONT_BUTTON, false, false,
				"© 2022-" + currentYear + " mexikoedi", 10);
		/*
		 * Icons which are used in the JFrame are being set and picLoading added to the
		 * DragLayout (visible now).
		 */
		Image icon1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Loading.png"));
		Image resizedIcon1 = icon1.getScaledInstance(274, 193, Image.SCALE_SMOOTH);
		this.picLoading = new JLabel(new ImageIcon(resizedIcon1));
		this.picLoading.setBounds(1102, 414, 274, 193);
		this.dragPanel.add(picLoading);
		Image icon2 = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Loaded.png"));
		Image resizedIcon2 = icon2.getScaledInstance(274, 193, Image.SCALE_SMOOTH);
		this.picLoaded = new JLabel(new ImageIcon(resizedIcon2));
		/*
		 * The initAnimeListGuiElements() function is being called to create the
		 * textfields for the anime titles.
		 */
		this.initAnimeListGuiElements();
	}

	/**
	 * JTextFields are being created and added to DragLayout for all anime titles
	 * which are in the .txt file.
	 */
	private void initAnimeListGuiElements() {
		int i = 0;
		int spacing = 50; // Space between elements
		int extraSpace = 5; // Extra space at the bottom

		for (Anime anime : this.animeList) {
			int yPosition = 64 + i;
			initTextField(this.dragPanel, COLOR_LIGHTGREY, null, 10, yPosition, 502, 44, FONT_ANIME_TF, false, true,
					anime.getName(), 10);
			this.tfAnimeSeasonMap.put(anime.getName(), initTextField(this.dragPanel, COLOR_LIGHTGREY, null, 522,
					yPosition, 160, 44, FONT_ANIME_TF, false, true, null, 10));
			this.tfAnimeRunMap.put(anime.getName(), initTextField(this.dragPanel, COLOR_LIGHTGREY, null, 692, yPosition,
					400, 44, FONT_ANIME_TF, false, true, null, 10));
			i += spacing;
		}

		// Adjust the panel size to add extra space at the bottom
		Dimension panelSize = this.dragPanel.getPreferredSize();
		panelSize.height += extraSpace;
		this.dragPanel.setPreferredSize(panelSize);
		this.dragPanel.revalidate();
	}

	/**
	 * Initialize the textfields.
	 * 
	 * @param dragPanel           The panel where the textfield is added.
	 * @param backgroundColor     The background color of the textfield.
	 * @param horizontalAlignment The horizontal alignment of the textfield.
	 * @param boundX              The x-coordinate of the textfield.
	 * @param boundY              The y-coordinate of the textfield.
	 * @param boundWidth          The width of the textfield.
	 * @param boundHeight         The height of the textfield.
	 * @param font                The font of the textfield.
	 * @param editable            The editable state of the textfield.
	 * @param focusable           The focusable state of the textfield.
	 * @param text                The text of the textfield.
	 * @param numberOfColumns     The number of columns of the textfield.
	 * @return The created textfield.
	 */
	private JTextField initTextField(JPanel dragPanel, Color backgroundColor, Integer horizontalAlignment,
			Integer boundX, Integer boundY, Integer boundWidth, Integer boundHeight, Font font, Boolean editable,
			Boolean focusable, String text, Integer numberOfColumns) {
		JTextField textField = new JTextField();
		textField.setBackground(backgroundColor);

		if (horizontalAlignment != null) {
			textField.setHorizontalAlignment(horizontalAlignment);
		}

		textField.setBounds(boundX, boundY, boundWidth, boundHeight);
		textField.setFont(font);
		textField.setEditable(editable);
		textField.setFocusable(focusable);

		if (text != null) {
			textField.setText(text);
		}

		textField.setColumns(numberOfColumns);
		dragPanel.add(textField);

		return textField;
	}

	/**
	 * Logic for the ButtonPressed event.
	 */
	private void checkButtonPressed() {
		/*
		 * Season number is optional if the textfield is blank. If the default text is
		 * still there clear the textfield to make it blank. Otherwise only numbers are
		 * accepted. If it's something else an error will be displayed.
		 */
		if (tfSeason.getText().equals("Enter season number (optional)")) {
			tfSeason.setText("");
		}

		if (!tfSeason.getText().isEmpty() && !tfSeason.getText().matches("\\d")) {
			showDialog("Please enter a valid season number!", "Warning", JOptionPane.INFORMATION_MESSAGE);

			return;
		}

		// Run check logic which modifies the data structure
		this.check();
	}

	/**
	 * Internal check which searches for new anime titles from the .txt file and
	 * gets all the data (seasons/years) from the IMDB website with the help of the
	 * external JSoup library. It also uses a maximum amount of requests and a
	 * random delay to avoid issues like for example Error 503. Runtime length in
	 * milliseconds is also recorded here and later converted to seconds if needed.
	 * The SwingWorker is used to process the long task in the background and
	 * publish the results in Swing. A progress bar is used to show the progress of
	 * the program. Buttons are disabled and the image is updated accordingly.
	 */
	private void check() {
		/*
		 * SwingWorker is an abstract class used to process long tasks in the background
		 * and publish the results in Swing. It is generic with two types: return value
		 * and intermediate results (progress updates). SwingWorker implements
		 * doInBackground() for the background task and done() for the result
		 * publication. It uses the Event Dispatch Thread (EDT) to safely update the
		 * Swing components.
		 */
		SwingWorker<Void, Integer> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() {
				startTime = System.currentTimeMillis();
				// Disable the buttons while the task is running
				btnSearch.setEnabled(false);

				// Only disable export button/update image if the task has executed once
				if (hasExecutedOnce) {
					btnDataExport.setEnabled(false);
					dragPanel.remove(picLoaded);
					picLoaded.setBounds(1102, 414, 274, 193);
					dragPanel.add(picLoading);
					dragPanel.repaint();
				}

				// Maximum amount of requests before pause
				final int batchSize = 25;
				// Random delay between 5 and 8 seconds used for the pause
				final int delayMillis = (int) Math.floor(Math.random() * (8000 - 5000 + 1) + 5000);
				/*
				 * AtomicInteger is used to safely count requests across multiple threads. This
				 * ensures that each thread increments the counter without race conditions.
				 */
				AtomicInteger requestCounter = new AtomicInteger(0);
				AtomicInteger progress = new AtomicInteger(0);
				int totalAnimeTitles = animeList.size();
				resetProgressBar();

				animeList = animeList.parallelStream().peek(anime -> {
					Document website = null;
					String url = anime.getUrl();
					String name = anime.getName();

					try {
						// Synchronize the delay to avoid multiple threads pausing at the same time
						synchronized (requestCounter) {
							// Check if the request counter has reached the batch size, then pause
							if (requestCounter.incrementAndGet() % batchSize == 0) {
								try {
									Thread.sleep(delayMillis);
								} catch (InterruptedException e) {
									// Restore the interrupted status
									Thread.currentThread().interrupt();
								}
							}
						}

						if ((url == null || url.isEmpty()) && (name == null || name.isEmpty())) {
							showDialog("No URL and name found!", "Error", JOptionPane.ERROR_MESSAGE);

							return;
						} else if (url == null || url.isEmpty()) {
							showDialog("No URL for " + name + " found!", "Error", JOptionPane.ERROR_MESSAGE);

							return;
						} else if (name == null || name.isEmpty()) {
							showDialog("No name for " + url + " found!", "Error", JOptionPane.ERROR_MESSAGE);

							return;
						}

						// Web request
						website = Jsoup.connect(anime.getUrl()).get();
					} catch (IOException e) {
						showDialog(anime.getName() + " not available! Check URL or try again later?", "Error",
								JOptionPane.ERROR_MESSAGE);

						return;
					}

					// Process the website to extract the data
					processAnimeData(anime, website);
					// Progress update and conversion to percentage
					int currentProgress = progress.incrementAndGet();
					int progressPercentage = (int) ((currentProgress / (double) totalAnimeTitles) * 100);
					// Publish progress and send to GUI (SwingWorker)
					publish(progressPercentage);
				}).toList();

				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				// Update the progress bar
				for (int progress : chunks) {
					updateProgressBar(progress);
				}
			}

			@Override
			protected void done() {
				/*
				 * The picLoading removed from the DragLayout (not visible now) and picLoaded
				 * added instead (visible now) and the GUI elements for each anime are being
				 * updated.
				 */
				SwingUtilities.invokeLater(() -> {
					dragPanel.remove(picLoading);
					picLoaded.setBounds(1102, 414, 274, 193);
					dragPanel.add(picLoaded);
					dragPanel.repaint();

					animeList.forEach(anime -> {
						tfAnimeSeasonMap.get(anime.getName()).setText(anime.getSeason());
						tfAnimeRunMap.get(anime.getName()).setText(anime.getRun());
					});

					// Enable the buttons again and show the duration
					btnSearch.setEnabled(true);
					btnDataExport.setEnabled(true);
					long endTime = System.currentTimeMillis();
					duration = endTime - startTime;

					if (duration == 1) {
						tfDuration.setText("It took " + duration + " millisecond.");
					} else if (duration < 1000) {
						tfDuration.setText("It took " + duration + " milliseconds.");
					} else if (duration == 1000) {
						tfDuration.setText("It took " + (duration / 1000.0) + " second.");
					} else if (duration < 60000) {
						tfDuration.setText("It took " + (duration / 1000.0) + " seconds.");
					} else if (duration == 60000) {
						tfDuration.setText(
								"It took " + String.format(Locale.US, "%.1f", duration / 60000.0) + " minute.");
					} else {
						tfDuration.setText(
								"It took " + String.format(Locale.US, "%.1f", duration / 60000.0) + " minutes.");
					}

					hasExecutedOnce = true;
				});
			}
		};

		worker.execute();
	}

	/**
	 * Helper function to process and extract the anime data from the website.
	 * 
	 * @param anime   The anime object to store the data.
	 * @param website The website document to extract the data from.
	 */
	private void processAnimeData(Anime anime, Document website) {
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
		Integer expectedNumberOfSeasons = null;

		if (!this.tfSeason.getText().isEmpty()) {
			expectedNumberOfSeasons = Integer.valueOf(this.tfSeason.getText());
		}

		String seasonText;

		if (expectedNumberOfSeasons == null || numberOfSeasons.equals(expectedNumberOfSeasons)) {
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
	}

	/**
	 * This method is used to save the data in a JSON file.
	 * 
	 * @param animeList The list of anime objects to be exported.
	 */
	private void exportData(List<Anime> animeList) {
		File exportDir = new File(ANIME_EXPORT_DIR);

		if (!exportDir.exists()) {
			exportDir.mkdir();
		}

		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("[\n");

		for (int i = 0; i < animeList.size(); i++) {
			Anime anime = animeList.get(i);
			jsonBuilder.append("  {\n");
			jsonBuilder.append("    \"title\": \"").append(anime.getName().replace("\"", "\\\"")).append("\",\n");
			jsonBuilder.append("    \"newestSeason\": \"").append(anime.getSeason().replace("\"", "\\\""))
					.append("\",\n");
			jsonBuilder.append("    \"runtime\": \"").append(anime.getRun().replace("\"", "\\\"")).append("\"\n");
			jsonBuilder.append("  }");

			if (i < animeList.size() - 1) {
				jsonBuilder.append(",");
			}

			jsonBuilder.append("\n");
		}

		jsonBuilder.append("]\n");
		String jsonData = jsonBuilder.toString();
		String exportFileName = ANIME_EXPORT_DIR + "/anime_data.json";
		File exportFile = new File(exportFileName);
		boolean exportSuccess = false;
		boolean exportFailed = false;
		boolean alreadyExported = false;

		if (exportFile.exists()) {
			try {
				String existingData = new String(Files.readAllBytes(Paths.get(exportFileName)));

				if (jsonData.equals(existingData)) {
					alreadyExported = true;
				}
			} catch (IOException e) {
				exportFailed = true;
			}
		}

		if (!alreadyExported && !exportFailed) {
			try (FileWriter writer = new FileWriter(exportFileName)) {
				writer.write(jsonData);
				exportSuccess = true;
			} catch (IOException e) {
				exportFailed = true;
			}
		}

		if (exportSuccess) {
			showDialog("The data has been successfully exported!", "Information", JOptionPane.INFORMATION_MESSAGE);
		} else if (exportFailed) {
			showDialog("An error occurred while exporting the data!", "Error", JOptionPane.ERROR_MESSAGE);
		} else if (alreadyExported) {
			showDialog("The data has already been exported and has not changed!", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * This method resets the progress bar to 0.
	 */
	private void resetProgressBar() {
		EventQueue.invokeLater(() -> {
			progressBar.setValue(0);
		});
	}

	/**
	 * This method updates the progress bar with the given value.
	 * 
	 * @param progress The value to update the progress bar with.
	 */
	private void updateProgressBar(int progress) {
		EventQueue.invokeLater(() -> {
			progressBar.setValue(progress);
		});
	}

	/**
	 * Helper function to display a dialog message without blocking the Thread.
	 * 
	 * @param message     The message to be displayed.
	 * @param title       The title of the dialog.
	 * @param messageType The type of the dialog.
	 */
	private void showDialog(String message, String title, int messageType) {
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message, title, messageType));
	}
}