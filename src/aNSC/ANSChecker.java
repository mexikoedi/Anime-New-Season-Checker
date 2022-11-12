package aNSC;

/**
 * Imports used.
 */
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.SwingConstants;
import java.awt.Color;

public class ANSChecker {

	/**
	 * Attributes used.
	 */
	private JFrame frmAnimeNewSeason;
	private int numberOfFields = animeTitles();
	private String[] counter = new String[numberOfFields];
	private JTextField[] tfAnimeName = new JTextField[numberOfFields];
	private JTextField[] tfAnimeSeason = new JTextField[numberOfFields];
	private JTextField[] tfAnimeRun = new JTextField[numberOfFields];
	private String[] animeName = new String[numberOfFields];
	private String[] animeSeason = new String[numberOfFields];
	private String[] animeRun = new String[numberOfFields];
	private JTextField tfAnimeHeadline;
	private JTextField tfAnimeSeasonHeadline;
	private JTextField tfAnimeRunHeadline;
	private JTextField tfControls;
	private JTextField tfSeason;
	private JButton btnSeason;
	private JTextField tfAnimeCount;
	private JTextField tfDuration;
	private long startTime = 0;
	private long duration = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ANSChecker window = new ANSChecker();
					window.frmAnimeNewSeason.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public ANSChecker() throws Exception {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws Exception
	 */
	private void initialize() throws Exception {

		/**
		 * Frame set.
		 */
		frmAnimeNewSeason = new JFrame();
		frmAnimeNewSeason.setResizable(false);
		frmAnimeNewSeason.setTitle("Anime New Season Checker");
		frmAnimeNewSeason.setBounds(100, 100, 1415, 655);
		frmAnimeNewSeason.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/**
		 * Program icon set.
		 */
		Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Icon.png"));
		frmAnimeNewSeason.setIconImage(icon);

		/**
		 * Used DragLayout for absolute layout and scroll bar support.
		 */
		DragLayout dl_dragPanel = new DragLayout();
		dl_dragPanel.setUsePreferredSize(false);
		JPanel dragPanel = new JPanel(dl_dragPanel);
		frmAnimeNewSeason.getContentPane().add(new JScrollPane(dragPanel));

		/**
		 * Now all elements in the JFrame are being set and added to the DragLayout.
		 */
		tfAnimeHeadline = new JTextField();
		tfAnimeHeadline.setBackground(new Color(0, 255, 128));
		tfAnimeHeadline.setHorizontalAlignment(SwingConstants.CENTER);
		tfAnimeHeadline.setBounds(10, 10, 502, 44);
		tfAnimeHeadline.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
		tfAnimeHeadline.setEditable(false);
		tfAnimeHeadline.setText("Anime:");
		tfAnimeHeadline.setColumns(10);
		dragPanel.add(tfAnimeHeadline);

		tfAnimeSeasonHeadline = new JTextField();
		tfAnimeSeasonHeadline.setBackground(new Color(0, 255, 128));
		tfAnimeSeasonHeadline.setHorizontalAlignment(SwingConstants.CENTER);
		tfAnimeSeasonHeadline.setBounds(522, 10, 160, 44);
		tfAnimeSeasonHeadline.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
		tfAnimeSeasonHeadline.setText("Newest season?");
		tfAnimeSeasonHeadline.setEditable(false);
		tfAnimeSeasonHeadline.setColumns(10);
		dragPanel.add(tfAnimeSeasonHeadline);

		tfAnimeRunHeadline = new JTextField();
		tfAnimeRunHeadline.setBackground(new Color(0, 255, 128));
		tfAnimeRunHeadline.setHorizontalAlignment(SwingConstants.CENTER);
		tfAnimeRunHeadline.setBounds(692, 10, 400, 44);
		tfAnimeRunHeadline.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
		tfAnimeRunHeadline.setText("Anime runtime:");
		tfAnimeRunHeadline.setEditable(false);
		tfAnimeRunHeadline.setColumns(10);
		dragPanel.add(tfAnimeRunHeadline);

		tfControls = new JTextField();
		tfControls.setBackground(new Color(0, 255, 128));
		tfControls.setHorizontalAlignment(SwingConstants.CENTER);
		tfControls.setText("Controls:");
		tfControls.setFont(new Font("Segoe UI Black", Font.BOLD, 16));
		tfControls.setEditable(false);
		tfControls.setColumns(10);
		tfControls.setBounds(1102, 10, 274, 44);
		dragPanel.add(tfControls);

		tfSeason = new JTextField();
		tfSeason.setBackground(new Color(255, 255, 128));
		tfSeason.setFont(new Font("Tahoma", Font.ITALIC, 15));
		tfSeason.setHorizontalAlignment(SwingConstants.CENTER);
		tfSeason.setBounds(1102, 64, 274, 44);
		tfSeason.setText("Enter season number");
		tfSeason.setColumns(10);
		dragPanel.add(tfSeason);

		btnSeason = new JButton("Confirm season");
		btnSeason.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSeason.setBounds(1102, 114, 274, 44);
		btnSeason.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfSeason.setText(tfSeason.getText());
			}
		});
		dragPanel.add(btnSeason);

		/**
		 * Icons which are used in the JFrame are being set and pic1 added to the
		 * DragLayout (visible now).
		 */
		Image icon1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Loading.png"));
		JLabel pic1 = new JLabel(new ImageIcon(icon1));
		Image icon2 = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("Loaded.png"));
		JLabel pic2 = new JLabel(new ImageIcon(icon2));
		pic1.setBounds(1102, 314, 274, 293);
		dragPanel.add(pic1);

		/**
		 * Search button triggers the check() function and adds the content to the
		 * JTextFields if it is successful.
		 */
		JButton btnSearch = new JButton("Search for new seasons");
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSearch.setBounds(1102, 164, 274, 44);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					check();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Error: Check failed!");
					e1.printStackTrace();
				}

				/**
				 * pic1 removed from the DragLayout (not visible now) and pic2 added instead
				 * (visible now).
				 */
				dragPanel.remove(pic1);
				pic2.setBounds(1102, 314, 274, 293);
				dragPanel.add(pic2);
				dragPanel.repaint();

				for (int j = 0; j < tfAnimeName.length; j++) {
					tfAnimeName[j].setText(animeName[j]);
				}

				for (int j = 0; j < tfAnimeSeason.length; j++) {
					tfAnimeSeason[j].setText(animeSeason[j]);
				}

				for (int j = 0; j < tfAnimeRun.length; j++) {
					tfAnimeRun[j].setText(animeRun[j]);
				}

			}
		});
		dragPanel.add(btnSearch);

		tfAnimeCount = new JTextField();
		tfAnimeCount.setBackground(new Color(128, 255, 255));
		tfAnimeCount.setHorizontalAlignment(SwingConstants.CENTER);
		tfAnimeCount.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tfAnimeCount.setEditable(false);
		tfAnimeCount.setBounds(1102, 214, 274, 44);
		tfAnimeCount.setText("You have " + numberOfFields + " anime titles listed!");
		dragPanel.add(tfAnimeCount);
		tfAnimeCount.setColumns(10);

		tfDuration = new JTextField();
		tfDuration.setBackground(new Color(128, 255, 255));
		tfDuration.setHorizontalAlignment(SwingConstants.CENTER);
		tfDuration.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tfDuration.setEditable(false);
		tfDuration.setBounds(1102, 264, 274, 44);
		dragPanel.add(tfDuration);
		tfDuration.setColumns(10);

		/**
		 * JTextFields are being created and added to DragLayout for all anime titles
		 * which are in the .txt file.
		 */
		int i = 0;
		for (int j = 0; j < tfAnimeName.length; j++) {
			tfAnimeName[j] = new JTextField();
			tfAnimeName[j].setBackground(new Color(210, 210, 210));
			tfAnimeName[j].setFont(new Font("Tahoma", Font.BOLD, 13));
			tfAnimeName[j].setEditable(false);
			tfAnimeName[j].setBounds(10, 64 + i, 502, 44);
			dragPanel.add(tfAnimeName[j]);
			tfAnimeName[j].setColumns(10);
			i += 50;
		}

		int k = 0;
		for (int j = 0; j < tfAnimeSeason.length; j++) {
			tfAnimeSeason[j] = new JTextField();
			tfAnimeSeason[j].setBackground(new Color(210, 210, 210));
			tfAnimeSeason[j].setFont(new Font("Tahoma", Font.BOLD, 13));
			tfAnimeSeason[j].setEditable(false);
			tfAnimeSeason[j].setBounds(522, 64 + k, 160, 44);
			dragPanel.add(tfAnimeSeason[j]);
			tfAnimeSeason[j].setColumns(10);
			k += 50;
		}

		int l = 0;
		for (int j = 0; j < tfAnimeRun.length; j++) {
			tfAnimeRun[j] = new JTextField();
			tfAnimeRun[j].setBackground(new Color(210, 210, 210));
			tfAnimeRun[j].setFont(new Font("Tahoma", Font.BOLD, 13));
			tfAnimeRun[j].setEditable(false);
			tfAnimeRun[j].setBounds(692, 64 + l, 400, 44);
			dragPanel.add(tfAnimeRun[j]);
			tfAnimeRun[j].setColumns(10);
			l += 50;
		}
	}

	/**
	 * Internal check which searches for new anime titles from the .txt file and
	 * gets all the data (seasons/years) from the IMDB website with the help of the
	 * external JSoup library. Runtime length in nanoseconds is also recorded here
	 * and later converted to seconds.
	 * 
	 * @throws Exception
	 */
	private void check() throws Exception {
		startTime = System.nanoTime();

		if (tfSeason.getText().isBlank() || !tfSeason.getText().matches("\\d+")) {
			JOptionPane.showMessageDialog(null, "Error: Please enter the season number!");
			throw new Exception();
		} else {
			File file = new File("list.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;
			int f = 0;

			while ((st = br.readLine()) != null && f < counter.length) {
				if (st.lastIndexOf("anime:") >= 0) {
					animeName[f] = st.substring(st.lastIndexOf(":") + 1);
				}

				if (st.lastIndexOf("source:") >= 0) {
					String address = st.substring(st.lastIndexOf(":") + 1);
					String addressCorrection = "https:" + address;
					Document website = Jsoup.connect(addressCorrection).get();
					String seasonData = null;
					String yearData = null;

					if (website.select(".ipc-simple-select__label").text().contains(" seasons")) {
						seasonData = website.select(".ipc-simple-select__label").text();
					} else {
						seasonData = website.select("a.ipc-button div.ipc-button__text").text();
					}

					if (website.select("#browse-episodes-year.ipc-simple-select__input").text().contains("See all")) {
						yearData = website.select("#browse-episodes-year.ipc-simple-select__input").text();
						yearData = yearData.replaceAll("[^0-9]", "");
					} else {
						yearData = website.select("a.ipc-button div.ipc-button__text").text();
						yearData = yearData.replaceAll("[^0-9]", "");
						yearData = yearData.substring(yearData.length() - 4);
					}

					if (seasonData.contains(tfSeason.getText() + " Season")) {
						animeSeason[f] = "True: Season " + tfSeason.getText() + " newest!";
					} else if (seasonData.contains(1 + " Season")) {
						animeSeason[f] = "False: Only 1 season!";
					}

					if (seasonData.contains(tfSeason.getText() + " seasons")) {
						animeSeason[f] = "True: Season " + tfSeason.getText() + " newest!";
					} else if (seasonData.contains(" seasons")) {
						if (seasonData.contains(2 + " seasons")) {
							animeSeason[f] = "False: 2 seasons!";
						} else if (seasonData.contains(3 + " seasons")) {
							animeSeason[f] = "False: 3 seasons!";
						} else if (seasonData.contains(4 + " seasons")) {
							animeSeason[f] = "False: 4 seasons!";
						} else if (seasonData.contains(5 + " seasons")) {
							animeSeason[f] = "False: 5 seasons!";
						} else if (seasonData.contains(6 + " seasons")) {
							animeSeason[f] = "False: 6 seasons!";
						} else if (seasonData.contains(7 + " seasons")) {
							animeSeason[f] = "False: 7 seasons!";
						} else if (seasonData.contains(8 + " seasons")) {
							animeSeason[f] = "False: 8 seasons!";
						} else if (seasonData.contains(9 + " seasons")) {
							animeSeason[f] = "False: 9 seasons!";
						} else if (seasonData.contains(10 + " seasons")) {
							animeSeason[f] = "False: 10 seasons!";
						} else {
							animeSeason[f] = "More than 10 seasons!";
						}
					}

					String runtime = yearData.replaceAll("(.{" + 4 + "})", "$1 ").trim();
					animeRun[f] = runtime;
				}
				if (st.lastIndexOf("source:") >= 0) {
					f++;
				}
			}
			br.close();
		}
		duration = System.nanoTime() - startTime;
		long convert = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
		String sDuration = convert + "";
		tfDuration.setText("It took " + sDuration + " seconds!");
	}

	/**
	 * Internal check which searches for all anime titles from the .txt file to get
	 * the amount as a number.
	 * 
	 * @throws Exception
	 */
	private int animeTitles() throws Exception {
		File file = new File("list.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		int i = 0;

		while ((st = br.readLine()) != null) {
			if (st.lastIndexOf("anime:") >= 0) {
				i += 1;
			}
		}
		br.close();
		return i;
	}
}
