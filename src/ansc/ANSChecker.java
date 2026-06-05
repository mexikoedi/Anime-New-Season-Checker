package ansc;

// Imports
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import model.Anime;
import model.AnimeListReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
  private final String ANIME_CACHE_DIR = "ansc_cache";
  private final long ANIME_CACHED_TIME = 24 * 60 * 60 * 1000;
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

  /** Launch the application. */
  public static void main(String[] args) {
    EventQueue.invokeLater(
        () -> {
          try {
            ANSChecker window = new ANSChecker();
            window.frmAnimeNewSeason.setVisible(true);
          } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null, "Check failed! Text file missing?", "Error", JOptionPane.ERROR_MESSAGE);
          }
        });
  }

  /** Initialize the contents of the frame. */
  private void initStandardGuiElements() {
    // UI set
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      showDialog(
          "Unexpected error in UI adjustment! Please try again.",
          "Error",
          JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    // Frame set
    this.frmAnimeNewSeason = new JFrame();
    this.frmAnimeNewSeason.setResizable(false);
    this.frmAnimeNewSeason.setTitle("Anime New Season Checker");
    this.frmAnimeNewSeason.setBounds(100, 100, 1415, 655);
    this.frmAnimeNewSeason.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // Program icon set
    Image icon =
        Toolkit.getDefaultToolkit()
            .getImage(this.getClass().getClassLoader().getResource("Icon.png"));
    this.frmAnimeNewSeason.setIconImage(icon);
    // Used DragLayout for absolute layout and scroll bar support
    DragLayout dl_dragPanel = new DragLayout();
    dl_dragPanel.setUsePreferredSize(false);
    this.dragPanel = new JPanel(dl_dragPanel);
    JScrollPane scrollPane = new JScrollPane(dragPanel);
    scrollPane.getVerticalScrollBar().setUnitIncrement(20);
    this.frmAnimeNewSeason.getContentPane().add(scrollPane);
    // Now all elements in the JFrame are being set and added to the DragLayout
    initTextField(
        dragPanel,
        COLOR_GREEN,
        SwingConstants.CENTER,
        10,
        10,
        502,
        44,
        FONT_TEXTFIELD,
        false,
        false,
        "Anime:",
        10);
    initTextField(
        dragPanel,
        COLOR_GREEN,
        SwingConstants.CENTER,
        522,
        10,
        160,
        44,
        FONT_TEXTFIELD,
        false,
        false,
        "Newest season?",
        10);
    initTextField(
        dragPanel,
        COLOR_GREEN,
        SwingConstants.CENTER,
        692,
        10,
        400,
        44,
        FONT_TEXTFIELD,
        false,
        false,
        "Anime runtime:",
        10);
    initTextField(
        dragPanel,
        COLOR_GREEN,
        SwingConstants.CENTER,
        1102,
        10,
        274,
        44,
        FONT_TEXTFIELD,
        false,
        false,
        "Controls:",
        10);
    this.tfSeason =
        initTextField(
            dragPanel,
            new Color(255, 255, 128),
            SwingConstants.CENTER,
            1102,
            64,
            274,
            44,
            new Font("Tahoma", Font.ITALIC, 15),
            true,
            true,
            "Enter season number (optional)",
            10);
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
    initTextField(
        dragPanel,
        COLOR_CYAN,
        SwingConstants.CENTER,
        1102,
        264,
        274,
        44,
        FONT_BUTTON,
        false,
        false,
        "You have " + this.animeList.size() + " anime titles listed!",
        10);
    this.tfDuration =
        initTextField(
            dragPanel,
            COLOR_CYAN,
            SwingConstants.CENTER,
            1102,
            314,
            274,
            44,
            FONT_BUTTON,
            false,
            false,
            null,
            10);
    initTextField(
        dragPanel,
        COLOR_CYAN,
        SwingConstants.CENTER,
        1102,
        364,
        274,
        44,
        FONT_BUTTON,
        false,
        false,
        "© 2022-" + currentYear + " mexikoedi",
        10);
    /*
     * Icons which are used in the JFrame are being set and picLoading added to the
     * DragLayout (visible now).
     */
    Image icon1 =
        Toolkit.getDefaultToolkit()
            .getImage(this.getClass().getClassLoader().getResource("Loading.png"));
    Image resizedIcon1 = icon1.getScaledInstance(274, 193, Image.SCALE_SMOOTH);
    this.picLoading = new JLabel(new ImageIcon(resizedIcon1));
    this.picLoading.setBounds(1102, 414, 274, 193);
    this.dragPanel.add(picLoading);
    Image icon2 =
        Toolkit.getDefaultToolkit()
            .getImage(this.getClass().getClassLoader().getResource("Loaded.png"));
    Image resizedIcon2 = icon2.getScaledInstance(274, 193, Image.SCALE_SMOOTH);
    this.picLoaded = new JLabel(new ImageIcon(resizedIcon2));
    /*
     * The initAnimeListGuiElements() function is being called to create the
     * textfields for the anime titles.
     */
    this.initAnimeListGuiElements();
  }

  /**
   * JTextFields are being created and added to DragLayout for all anime titles which are in the
   * .txt file.
   */
  private void initAnimeListGuiElements() {
    int i = 0;
    int spacing = 50; // Space between elements
    int extraSpace = 5; // Extra space at the bottom

    for (Anime anime : this.animeList) {
      int yPosition = 64 + i;
      initTextField(
          this.dragPanel,
          COLOR_LIGHTGREY,
          null,
          10,
          yPosition,
          502,
          44,
          FONT_ANIME_TF,
          false,
          true,
          anime.getName(),
          10);
      this.tfAnimeSeasonMap.put(
          anime.getName(),
          initTextField(
              this.dragPanel,
              COLOR_LIGHTGREY,
              null,
              522,
              yPosition,
              160,
              44,
              FONT_ANIME_TF,
              false,
              true,
              null,
              10));
      this.tfAnimeRunMap.put(
          anime.getName(),
          initTextField(
              this.dragPanel,
              COLOR_LIGHTGREY,
              null,
              692,
              yPosition,
              400,
              44,
              FONT_ANIME_TF,
              false,
              true,
              null,
              10));
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
   * @param dragPanel The panel where the textfield is added.
   * @param backgroundColor The background color of the textfield.
   * @param horizontalAlignment The horizontal alignment of the textfield.
   * @param boundX The x-coordinate of the textfield.
   * @param boundY The y-coordinate of the textfield.
   * @param boundWidth The width of the textfield.
   * @param boundHeight The height of the textfield.
   * @param font The font of the textfield.
   * @param editable The editable state of the textfield.
   * @param focusable The focusable state of the textfield.
   * @param text The text of the textfield.
   * @param numberOfColumns The number of columns of the textfield.
   * @return The created textfield.
   */
  private JTextField initTextField(
      JPanel dragPanel,
      Color backgroundColor,
      Integer horizontalAlignment,
      Integer boundX,
      Integer boundY,
      Integer boundWidth,
      Integer boundHeight,
      Font font,
      Boolean editable,
      Boolean focusable,
      String text,
      Integer numberOfColumns) {
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

  /** Logic for the ButtonPressed event. */
  private void checkButtonPressed() {
    /*
     * Season number is optional if the textfield is blank. If the default text is
     * still there clear the textfield to make it blank. Otherwise only numbers are
     * accepted. If it's something else an error will be displayed.
     */
    if (tfSeason.getText().equals("Enter season number (optional)")) {
      tfSeason.setText("");
    }

    // 1) Check if all anime are cached
    boolean allCached = true;

    for (Anime anime : animeList) {
      String cacheFile = getCacheFileName(anime.getName());

      if (!isCacheValid(cacheFile, ANIME_CACHED_TIME)) {
        allCached = false;

        break;
      }
    }

    // 2) If all cached AND not executed once yet -> Load from cache + fill GUI -> Skip scraping
    if (allCached && !hasExecutedOnce) {
      for (Anime anime : animeList) {
        String cacheFile = getCacheFileName(anime.getName());
        loadFromCache(cacheFile, anime);
      }

      // Update GUI
      animeList.forEach(
          a -> {
            tfAnimeSeasonMap.get(a.getName()).setText(a.getSeason());
            tfAnimeRunMap.get(a.getName()).setText(a.getRun());
          });

      hasExecutedOnce = true;

      return;
    }

    // 3) If all cached AND already executed once -> Block scraping and show dialog that cache is
    // still valid
    if (allCached && hasExecutedOnce) {
      showDialog("Cache is not expired yet!", "Information", JOptionPane.INFORMATION_MESSAGE);

      return;
    }

    // Season number check
    if (!tfSeason.getText().isEmpty() && !tfSeason.getText().matches("\\d")) {
      showDialog(
          "Please enter a valid season number!", "Information", JOptionPane.INFORMATION_MESSAGE);

      return;
    }

    // Run check logic which modifies the data structure
    this.check();
  }

  /**
   * Internal check which searches for new anime titles from the .txt file and gets all the data
   * (seasons/years) from the TMDB website with the help of the external JSoup library. It also uses
   * a maximum amount of requests and a random delay to avoid issues like for example Error 503.
   * Runtime length in milliseconds is also recorded here and later converted to seconds if needed.
   * The SwingWorker is used to process the long task in the background and publish the results in
   * Swing. A progress bar is used to show the progress of the program. Buttons are disabled and the
   * image is updated accordingly.
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
    SwingWorker<Void, Integer> worker =
        new SwingWorker<>() {
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
            final int batchSize = 5;
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

            animeList =
                animeList.parallelStream()
                    .peek(
                        anime -> {
                          Document website = null;
                          String url = anime.getUrl();
                          String name = anime.getName();

                          try {
                            // Synchronize the delay to avoid multiple threads pausing at the same
                            // time
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

                            if ((url == null || url.isEmpty())
                                && (name == null || name.isEmpty())) {
                              showDialog(
                                  "No URL and name found!", "Error", JOptionPane.ERROR_MESSAGE);

                              return;
                            } else if (url == null || url.isEmpty()) {
                              showDialog(
                                  "No URL for " + name + " found!",
                                  "Error",
                                  JOptionPane.ERROR_MESSAGE);

                              return;
                            } else if (name == null || name.isEmpty()) {
                              showDialog(
                                  "No name for " + url + " found!",
                                  "Error",
                                  JOptionPane.ERROR_MESSAGE);

                              return;
                            }

                            // Check cache before making the web request to avoid unnecessary
                            // requests
                            String cacheFileName = getCacheFileName(name);
                            boolean cached = isCacheValid(cacheFileName, ANIME_CACHED_TIME);

                            if (cached) {
                              loadFromCache(cacheFileName, anime);
                            } else {
                              website = Jsoup.connect(anime.getUrl()).get();
                              processAnimeData(anime, website);
                              saveToCache(cacheFileName, anime);
                            }
                          } catch (IOException e) {
                            showDialog(
                                anime.getName() + " not available! Check URL or try again later?",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);

                            return;
                          }

                          // Process the website to extract the data
                          processAnimeData(anime, website);
                          // Progress update and conversion to percentage
                          int currentProgress = progress.incrementAndGet();
                          int progressPercentage =
                              (int) ((currentProgress / (double) totalAnimeTitles) * 100);
                          // Publish progress and send to GUI (SwingWorker)
                          publish(progressPercentage);
                        })
                    .toList();

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
            SwingUtilities.invokeLater(
                () -> {
                  dragPanel.remove(picLoading);
                  picLoaded.setBounds(1102, 414, 274, 193);
                  dragPanel.add(picLoaded);
                  dragPanel.repaint();

                  animeList.forEach(
                      anime -> {
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
                        "It took "
                            + String.format(Locale.US, "%.1f", duration / 60000.0)
                            + " minute.");
                  } else {
                    tfDuration.setText(
                        "It took "
                            + String.format(Locale.US, "%.1f", duration / 60000.0)
                            + " minutes.");
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
   * @param anime The anime object to store the data.
   * @param website The website document to extract the data from.
   */
  private void processAnimeData(Anime anime, Document website) {
    // All seasons (including specials) are selected
    Elements rawBlocks = website.select("div.season_wrapper section.panel div.season div.content");
    List<Element> seasonBlocks = new ArrayList<>();
    Pattern numPattern = Pattern.compile("(\\d+)");
    // Season or Staffel, number in group 2, case insensitive
    Pattern seasonInOverview =
        Pattern.compile("(Season|Staffel)\\s+(\\d+)", Pattern.CASE_INSENSITIVE);

    for (Element block : rawBlocks) {
      int seasonNumber = -1;
      // 1) Try: Number from the title (h2 > a)
      Element h2a = block.selectFirst("h2 > a");

      if (h2a != null) {
        Matcher m = numPattern.matcher(h2a.text());

        if (m.find()) {
          seasonNumber = Integer.parseInt(m.group(1));
        }
      }

      // 2) Fallback: Number from the description (div.season_overview > p)
      if (seasonNumber == -1) {
        Element overviewP = block.selectFirst("div.season_overview > p");

        if (overviewP != null) {
          String overviewText = overviewP.text().trim();
          Matcher m = seasonInOverview.matcher(overviewText);

          if (m.find()) {
            // Group 2 is the number after Season/Staffel
            seasonNumber = Integer.parseInt(m.group(2));
          }
        }
      }

      // Specials (Season/Staffel 0) and everything without a valid number is ignored
      if (seasonNumber >= 1) {
        seasonBlocks.add(block);
      }
    }

    int numberOfSeasons = seasonBlocks.size();
    Integer expectedNumberOfSeasons = null;

    if (!this.tfSeason.getText().isEmpty()) {
      expectedNumberOfSeasons = Integer.valueOf(this.tfSeason.getText());
    }

    String seasonText;

    if (expectedNumberOfSeasons == null || numberOfSeasons == expectedNumberOfSeasons) {
      seasonText = "Season " + numberOfSeasons + " newest!";
    } else {
      seasonText = numberOfSeasons == 1 ? "1 season!" : numberOfSeasons + " seasons!";
    }

    anime.setSeason(seasonText);
    // Extract years from the season blocks
    List<String> years = new ArrayList<>();
    Pattern yearPattern = Pattern.compile("(\\d{4}|—|-)");

    for (Element block : seasonBlocks) {
      Element h4 = block.selectFirst("h4");
      if (h4 == null) continue;
      // Only the direct text in h4, without the rating div
      String text = h4.ownText().trim(); // e.g. "2015 • 13 Episoden" -> "2015"
      Matcher ym = yearPattern.matcher(text);

      if (ym.find()) {
        years.add(ym.group(1));
      }
    }

    String runtime = String.join(" ", years);
    anime.setRun(runtime);
  }

  /**
   * This method is used to save the data in a JSON file.
   *
   * @param animeList The list of anime objects to be exported.
   */
  private void exportData(List<Anime> animeList) {
    boolean cacheValid = true;

    // Check if all anime are cached and the cache is still valid before exporting
    for (Anime anime : animeList) {
      String cacheFileName = getCacheFileName(anime.getName());

      if (!isCacheValid(cacheFileName, ANIME_CACHED_TIME)) {
        cacheValid = false;

        break;
      }
    }

    if (!cacheValid) {
      showDialog(
          "The cache has expired. Please scrape the data again before exporting!",
          "Information",
          JOptionPane.INFORMATION_MESSAGE);

      return;
    }

    File exportDir = new File(ANIME_EXPORT_DIR);

    if (!exportDir.exists()) {
      exportDir.mkdir();
    }

    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("[\n");

    for (int i = 0; i < animeList.size(); i++) {
      Anime anime = animeList.get(i);
      jsonBuilder.append("  {\n");
      jsonBuilder
          .append("    \"title\": \"")
          .append(anime.getName().replace("\"", "\\\""))
          .append("\",\n");
      jsonBuilder
          .append("    \"newestSeason\": \"")
          .append(anime.getSeason().replace("\"", "\\\""))
          .append("\",\n");
      jsonBuilder
          .append("    \"runtime\": \"")
          .append(anime.getRun().replace("\"", "\\\""))
          .append("\"\n");
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
      showDialog(
          "The data has been successfully exported!",
          "Information",
          JOptionPane.INFORMATION_MESSAGE);
    } else if (exportFailed) {
      showDialog("An error occurred while exporting the data!", "Error", JOptionPane.ERROR_MESSAGE);
    } else if (alreadyExported) {
      showDialog(
          "The data has already been exported and has not changed!",
          "Information",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * This method generates the cache file name for a given anime name by replacing all
   * non-alphanumeric characters with underscores and appending the .cache extension. The cache
   * files are stored in the ANIME_CACHE_DIR directory.
   *
   * @param animeName The name of the anime for which the cache file name is to be generated.
   * @return The generated cache file name for the given anime name.
   */
  private String getCacheFileName(String animeName) {
    return ANIME_CACHE_DIR + "/" + animeName.replaceAll("[^a-zA-Z0-9]", "_") + ".cache";
  }

  /**
   * This method checks if the cache file for a given anime is valid by verifying its existence and
   * comparing the timestamp in the cache file with the current time to determine if it has expired
   * based on the specified cache duration.
   *
   * @param cacheFileName The name of the cache file to be checked for validity.
   * @param cacheDurationMillis The duration in milliseconds for which the cache is considered
   *     valid.
   * @return true if the cache is valid (exists and not expired), false otherwise.
   */
  private boolean isCacheValid(String cacheFileName, long cacheDurationMillis) {
    File cacheFile = new File(cacheFileName);

    if (!cacheFile.exists()) {
      return false;
    }

    try {
      List<String> lines = Files.readAllLines(Paths.get(cacheFileName));
      long timestamp = Long.parseLong(lines.get(0));
      long currentTime = System.currentTimeMillis();

      return (currentTime - timestamp) < cacheDurationMillis;
    } catch (IOException | NumberFormatException e) {
      showDialog(
          "Error checking cache for " + cacheFileName + "!", "Error", JOptionPane.ERROR_MESSAGE);

      return false;
    }
  }

  /**
   * This method saves the anime data (season and runtime) to a cache file with the specified name.
   *
   * @param cacheFileName The name of the cache file where the anime data will be saved.
   * @param anime The anime object containing the data (season and runtime) that will be saved to
   *     the cache file.
   */
  private void saveToCache(String cacheFileName, Anime anime) {
    File cacheDir = new File(ANIME_CACHE_DIR);

    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }

    try (FileWriter writer = new FileWriter(cacheFileName)) {
      writer.write(System.currentTimeMillis() + System.lineSeparator());
      writer.write(anime.getSeason() + System.lineSeparator());
      writer.write(anime.getRun() + System.lineSeparator());
    } catch (IOException e) {
      showDialog(
          "Error saving cache for " + cacheFileName + "!", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * This method loads the anime data (season and runtime) from a cache file with the specified
   * name.
   *
   * @param cacheFileName The name of the cache file from which the anime data will be loaded.
   * @param anime The anime object where the loaded data (season and runtime) will be set.
   * @return The anime object with the loaded data from the cache file.
   */
  private Anime loadFromCache(String cacheFileName, Anime anime) {
    try {
      List<String> lines = Files.readAllLines(Paths.get(cacheFileName));
      lines.remove(0);
      anime.setSeason(lines.get(0));
      anime.setRun(lines.get(1));

      return anime;
    } catch (IOException e) {
      showDialog(
          "Error loading cache for " + cacheFileName + "!", "Error", JOptionPane.ERROR_MESSAGE);

      return anime;
    }
  }

  /** This method resets the progress bar to 0. */
  private void resetProgressBar() {
    EventQueue.invokeLater(
        () -> {
          progressBar.setValue(0);
        });
  }

  /**
   * This method updates the progress bar with the given value.
   *
   * @param progress The value to update the progress bar with.
   */
  private void updateProgressBar(int progress) {
    EventQueue.invokeLater(
        () -> {
          progressBar.setValue(progress);
        });
  }

  /**
   * Helper function to display a dialog message without blocking the Thread.
   *
   * @param message The message to be displayed.
   * @param title The title of the dialog.
   * @param messageType The type of the dialog.
   */
  private void showDialog(String message, String title, int messageType) {
    SwingUtilities.invokeLater(
        () -> JOptionPane.showMessageDialog(null, message, title, messageType));
  }
}
