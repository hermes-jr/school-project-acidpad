import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.net.*;
import java.io.*;

class HelpWindow extends JPanel implements HyperlinkListener {
	JEditorPane htmlPane = new JEditorPane();
	Toolkit tk = Toolkit.getDefaultToolkit();
	LanguagePack dict; LanguageChanger langChanger;
	JLabel status;

	public HelpWindow(LanguagePack dict, LanguageChanger langChanger) {
		this.dict = dict; this.langChanger = langChanger;
		status = new JLabel();
		//--------------------- html pane... ---------------------
		htmlPane.setMargin(new Insets(0, 0, 0, 0));
		htmlPane.setEditable(false);
		htmlPane.addHyperlinkListener(this);
		try { htmlPane.setPage(getFileUrl("./helps/" + dict.get("name") + "/index.html"));
		} catch (IOException e) { status.setText(dict.get("dialogs_help_status_error")); }
		JScrollPane scroll = new JScrollPane(htmlPane,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setLayout(new BorderLayout());
		this.add("South", status); this.add("Center", scroll);
		status.setText(dict.get("dialogs_help_status_default"));
	}

	public void setLanguage(LanguagePack dict) {
		this.dict = dict;
		try { htmlPane.setPage(getFileUrl("./helps/" + dict.get("name") + "/index.html"));
		} catch (IOException e) { status.setText(dict.get("dialogs_help_status_error")); }
	}

	//--------------------- private methods... ---------------------
	private URL getFileUrl(String s) throws MalformedURLException {
		File f = new File(s);
		return f.toURL();
	}

	//--------------------- event listeners... ---------------------
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				htmlPane.setPage(evt.getURL());
			} catch (IOException e) { status.setText(dict.get("dialogs_help_status_error")); }
		}
	}
}

class ImagePreview extends JPanel {
	private String imageToShow;
	private Toolkit kit = Toolkit.getDefaultToolkit();
	private MediaTracker tracker; Image image;

	public ImagePreview(String imageToShow) {
		this.imageToShow = imageToShow; this.repaint(); this.setLayout(null); this.setSize(230, 190);
		this.setPreferredSize(new Dimension(230, 190)); this.setMinimumSize(new Dimension(230, 190));
	}

	public void setImage(String imageToShow) { this.imageToShow = imageToShow; this.repaint(); }

	public void paint(Graphics g) {
		g.setColor(this.getBackground());
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		image = kit.getImage(imageToShow);
		tracker = new MediaTracker(this); tracker.addImage(image, 0);
		try { tracker.waitForID(0);
		} catch (InterruptedException e) { }
		if (tracker.statusID(0, false) == MediaTracker.COMPLETE) { g.drawImage(image, 0, 0, this); }
	}
}

class JColorSelector extends JPanel implements ActionListener {
	protected Color color = new Color(0, 0, 0);
	protected GridBagLayout gridbag;
	protected GridBagConstraints constructor;
	protected JPanel demo = new JPanel();
	protected JButton picker;
	protected LanguagePack dict;

	public JColorSelector(JLabel label, LanguagePack dict) { this.dict = dict; createPane(label); }

	public JColorSelector(JLabel label, Color color, LanguagePack dict) {
		this.dict = dict; this.color = color; createPane(label);
	}

	//--------------------- private methods... ---------------------
	private void createPane(JLabel label) {
		JPanel freePane;
		gridbag = new GridBagLayout(); constructor = new GridBagConstraints();
		this.setLayout(gridbag);
		//new component...
		buildGrid(constructor, 0, 0, 1, 1, 100, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.CENTER;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		this.add(freePane);
		//new component...
		buildGrid(constructor, 1, 0, 1, 1, 0, 100, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(label, constructor);
		this.add(label);
		//new component...
		buildGrid(constructor, 2, 0, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		demo.setBackground(color); demo.setBorder(new LineBorder(new Color(0, 0, 0), 1, false));
		demo.setSize(40, 20); demo.setMinimumSize(new Dimension(40, 20)); demo.setPreferredSize(new Dimension(40, 20));
		gridbag.setConstraints(demo, constructor);
		this.add(demo);
		//new component...
		buildGrid(constructor, 3, 0, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		picker = new JButton(new ImageIcon("images/icons/picker.gif")); picker.setSize(22, 22);
		picker.setMinimumSize(new Dimension(22, 22)); picker.setPreferredSize(new Dimension(22, 22));
		picker.addActionListener(JColorSelector.this);
		gridbag.setConstraints(picker, constructor);
		this.add(picker);
	}

	public void setColor(Color color) { this.color = color; demo.setBackground(color); demo.repaint(); }
	public Color getColor() { return color; }

	//--------------------- event listeners... ---------------------
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if(source == picker) {
			JColorChooser cch = new JColorChooser(color);
			Color c = cch.showDialog(JColorSelector.this.getParent().getParent(), dict.get("dialogs_choosecolor_title"), color);
			if(c != null) { color = c; }
			demo.setBackground(color);
		}
		repaint();
	}

	void buildGrid(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy, Insets ins) {
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbc.insets = ins;
	}
}

class DirFilter implements FilenameFilter {
	String extension;

	public DirFilter(String extension) { this.extension = extension;	}
	public boolean accept(File dir, String name) { return name.indexOf(extension) != -1; }
}

class AcidFileFilter extends javax.swing.filechooser.FileFilter {
	String extension;
	String description;

	public AcidFileFilter(String extension, String description) {
		this.extension = extension;
		this.description = description;
	}

	public boolean accept(File file) {
		if(file.isDirectory()) { return true;
		} else { return file.getName().indexOf(extension) != -1; }
	}

	public String getDescription() { return description; }
}

class LanguagePack {
	protected Properties properties = new Properties();
	final static String PATH = "./setup/languages/";
	protected String langname, encoding;

	public LanguagePack(String langfile) {
		try { properties.load(new FileInputStream(PATH + langfile)); } catch (IOException e) { }
		encoding = properties.getProperty("encoding");
		try {
			langname = new String(properties.getProperty("description").toString().getBytes(), encoding);
		} catch (UnsupportedEncodingException e) { langname = properties.getProperty("description").toString(); }
	}

	public String get(String param) {
		try { return new String(properties.getProperty(param).toString().getBytes(), encoding);
		} catch (UnsupportedEncodingException e) { return properties.getProperty(param).toString(); }
	}
	public String getLanguageName() { return langname; }
	public String getEncoding() { return encoding; }
	public boolean containsKey(String key) { return properties.containsKey(key); }
}

class LanguageChanger implements Runnable {
	protected LanguagePack language = null;
	protected Thread runner = null;

	protected Hashtable abstractButtonsTexts = new Hashtable(0);
	protected Hashtable abstractButtonsTooltips = new Hashtable(0);
	protected Hashtable textComponentsTexts = new Hashtable(0);
	protected Hashtable textComponentsTooltips = new Hashtable(0);
	protected Hashtable labelsTexts = new Hashtable(0);
	protected Hashtable labelsTooltips = new Hashtable(0);
	protected Hashtable tabpanesTexts = new Hashtable(0);
	protected Hashtable tabpanesTooltips = new Hashtable(0);
	protected Hashtable framesTitles = new Hashtable(0);
	protected Hashtable dialogsTitles = new Hashtable(0);
	protected Hashtable nonSimpleElements = new Hashtable(0);

	public void add(AbstractButton abToAdd, String langKey) { abstractButtonsTexts.put(abToAdd, langKey); }
	public void addTooltip(AbstractButton abToAdd, String langKey) { abstractButtonsTooltips.put(abToAdd, langKey); }
	public void add(AbstractButton abToAdd, String langKey, boolean cont) {
		String whatToPut = "false";
		if(cont) { whatToPut = "true"; }
		abstractButtonsTexts.put(abToAdd, langKey); nonSimpleElements.put(abToAdd, whatToPut);
	}
	public void addTooltip(AbstractButton abToAdd, String langKey, boolean cont) {
		String whatToPut = "false";
		if(cont) { whatToPut = "true"; }
		abstractButtonsTooltips.put(abToAdd, langKey); nonSimpleElements.put(abToAdd, whatToPut);
	}
	public void add(JTextComponent tcToAdd, String langKey) { textComponentsTexts.put(tcToAdd, langKey); }
	public void addTooltip(JTextComponent tcToAdd, String langKey) { textComponentsTooltips.put(tcToAdd, langKey); }
	public void add(JLabel lbToAdd, String langKey) { labelsTexts.put(lbToAdd, langKey); }
	public void addTooltip(JLabel lbToAdd, String langKey) { labelsTooltips.put(lbToAdd, langKey); }
	public void add(JTabbedPane tpToAdd, String[] langKey) { tabpanesTexts.put(tpToAdd, langKey); }
	public void addTooltip(JTabbedPane tpToAdd, String[] langKey) { tabpanesTooltips.put(tpToAdd, langKey); }
	public void add(JFrame jfToAdd, String langKey) { framesTitles.put(jfToAdd, langKey); }
	public void add(JDialog jdToAdd, String langKey) { dialogsTitles.put(jdToAdd, langKey); }

	public void changeLanguage(LanguagePack language) {
		this.language = language;
		if(runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}

	public void run() {
		Enumeration keys;
		if(runner != null) {
			//texts...
			keys = abstractButtonsTexts.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				if(nonSimpleElements.containsKey(id) && (nonSimpleElements.get(id).toString().equals("true"))) {
					((AbstractButton) id).setText(language.get(abstractButtonsTexts.get(id).toString()) + "...");
					((AbstractButton) id).repaint();
				} else {
					((AbstractButton) id).setText(language.get(abstractButtonsTexts.get(id).toString()));
					((AbstractButton) id).repaint();
				}
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
			keys = textComponentsTexts.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				((JTextComponent) id).setText(language.get(textComponentsTexts.get(id).toString()));
				((JTextComponent) id).repaint();
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
			keys = labelsTexts.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				((JLabel) id).setText(language.get(labelsTexts.get(id).toString()));
				((JLabel) id).repaint();
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
			keys = tabpanesTexts.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				for(int i = 0; i < ((String[])tabpanesTexts.get(id)).length; i++) {
					((JTabbedPane) id).setTitleAt(i, language.get(((String[])tabpanesTexts.get(id))[i].toString()));
				}
				((JTabbedPane) id).repaint();
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
			keys = dialogsTitles.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				((JDialog) id).setTitle(language.get(dialogsTitles.get(id).toString()));
				((JDialog) id).repaint();
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
			keys = framesTitles.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				((JFrame) id).setTitle(language.get(framesTitles.get(id).toString()));
				((JFrame) id).repaint();
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
			//tooltips...
			keys = abstractButtonsTooltips.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				if(nonSimpleElements.containsKey(id) && (nonSimpleElements.get(id).toString().equals("true"))) {
					((AbstractButton) id).setToolTipText(language.get(abstractButtonsTooltips.get(id).toString()) + "...");
					((AbstractButton) id).repaint();
				} else {
					((AbstractButton) id).setToolTipText(language.get(abstractButtonsTooltips.get(id).toString()));
					((AbstractButton) id).repaint();
				}
			}
			keys = textComponentsTooltips.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				((JTextComponent) id).setToolTipText(language.get(textComponentsTooltips.get(id).toString()));
				((JTextComponent) id).repaint();
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
			keys = labelsTooltips.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				((JLabel) id).setToolTipText(language.get(labelsTooltips.get(id).toString()));
				((JLabel) id).repaint();
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
			keys = tabpanesTooltips.keys();
			while(keys.hasMoreElements()) {
				Object id = keys.nextElement();
				for(int i = 0; i < ((String[])tabpanesTooltips.get(id)).length; i++) {
					((JTabbedPane) id).setToolTipTextAt(i, language.get(((String[])tabpanesTooltips.get(id))[i].toString()));
				}
				((JTabbedPane) id).repaint();
				try { runner.sleep(0); } catch (InterruptedException e) { }
			}
		}
		runner = null;
	}
}

class JDownloader implements ActionListener, WindowListener, Runnable {
	protected JTextField url, sicl, sict, sidt, sipb, siex, silm, sisr;
	protected JTextField[] fields = {sicl, sict, sidt, sipb, siex, silm, sisr};
	protected String[] headers = {"Content-Length", "Content-Type", "Date", "Public", "Expires", "Last-Modified", "Server"};
	protected JLabel status;
	protected JPanel tf, downloadDialogPane;
	protected JButton get, cancel;
	protected JProgressBar pb;
	protected URL page;
	protected String source = "";
	protected Thread runner = null;
	protected JDialog downloadDialog;
	int ret; LanguagePack dict;
	public static final int SOMETHING_DOWNLOADED = 0;
	public static final int NOTHING_DOWNLOADED = 1;

	public JDownloader(LanguagePack dict) {
		this.dict = dict;
		//setting up components...
		url = new JTextField(); url.addActionListener(JDownloader.this);
		for(int i = 0; i < fields.length; i++) {
			fields[i] = new JTextField();
		}
		status = new JLabel(dict.get("dialogs_download_status_default"));
		pb = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		get = new JButton(dict.get("dialogs_download_buttons_get")); get.addActionListener(JDownloader.this);
		cancel = new JButton(dict.get("dialogs_download_buttons_close")); cancel.addActionListener(JDownloader.this);

		downloadDialogPane = new JPanel();
		JPanel pane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constr = new GridBagConstraints();
		pane.setLayout(gridbag);
		downloadDialogPane.setLayout(new BorderLayout());

		JPanel infos = new JPanel();
		infos.setBorder(new TitledBorder(dict.get("dialogs_download_about")));
		infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
		for(int i = 0; i < fields.length; i++) {
			infos.add(titledField(headers[i] + ":", fields[i]));
		}

		//new component...
		buildGrid(constr, 0, 0, 3, 1, 0, 0, new Insets(2, 2, 2, 2));
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.NORTH;
		tf = titledField(dict.get("dialogs_download_labels_url"), url);
		gridbag.setConstraints(tf, constr);
		pane.add(tf);
		//new component...
		buildGrid(constr, 0, 1, 1, 1, 100, 0, new Insets(0, 0, 0, 0));
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.NORTH;
		JPanel freePane = new JPanel();
		gridbag.setConstraints(freePane, constr);
		pane.add(freePane);
		//new component...
		buildGrid(constr, 1, 1, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(get, constr);
		pane.add(get);
		//new component...
		buildGrid(constr, 2, 1, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(cancel, constr);
		pane.add(cancel);
		//new component...
		buildGrid(constr, 0, 2, 3, 1, 0, 0, new Insets(2, 2, 2, 2));
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(pb, constr);
		pane.add(pb);
		//new component...
		buildGrid(constr, 0, 3, 3, 1, 100, 100, new Insets(0, 0, 0, 0));
		constr.fill = GridBagConstraints.BOTH;
		constr.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(infos, constr);
		pane.add(infos);

		downloadDialogPane.add("Center", pane);
		downloadDialogPane.add("South", status);
		pb.setStringPainted(true);
		url.setText("http://"); url.setCaretPosition(url.getText().length());
	}

	public void run() {
		URLConnection con = null;
		InputStreamReader in;
		BufferedReader data;
		String line = null;
		StringBuffer buf = new StringBuffer();
		try {
			pb.setValue(0);
			//connecting to source...
			con = page.openConnection();
			con.connect();
			//getting source info...
			status.setText(dict.get("dialogs_download_status_conn_opened"));
			try { runner.sleep(0); } catch(InterruptedException e) { }
			for(int i = 0; i < fields.length; i++) {
				fields[i].setText(con.getHeaderField(headers[i]));
			}
			try { runner.sleep(0); } catch(InterruptedException e) { }
			if (fields[0].getText().length() != 0) {
				pb.setMaximum(Integer.parseInt(con.getHeaderField("Content-Length")));
			}
			//getting source body...
			in = new InputStreamReader(con.getInputStream()); data = new BufferedReader(in);
			status.setText(dict.get("dialogs_download_status_loading"));
			try { runner.sleep(0); } catch(InterruptedException e) { }
			while(((line = data.readLine()) != null) && (runner != null)) {
				buf.append(line + "\n");
				pb.setValue(buf.toString().length()); downloadDialogPane.repaint();
				try { runner.sleep(0); } catch(InterruptedException e) { }
			}
			if(runner != null) { pb.setValue(pb.getMaximum()); }
			//safe reading...
			if(buf.toString().length() <= 1) { source = buf.toString();
			} else { source = buf.toString().substring(0, buf.toString().length() - 1); }
			status.setText(dict.get("dialogs_download_status_done"));
			try { runner.sleep(0); } catch(InterruptedException e) { }
			con = null; runner = null;
		} catch (IOException e) { status.setText(dict.get("dialogs_download_status_conn_failed")); runner = null; }
	}

	public String getSource() {
		return source;
	}

	public int showDownloadDialog(JFrame parent) {
		downloadDialog = new JDialog(parent); downloadDialog.setSize(450, 350);
		downloadDialog.setResizable(true); downloadDialog.setModal(true);
		downloadDialog.setTitle(dict.get("dialogs_download_title"));
		downloadDialog.setLocationRelativeTo(parent); downloadDialog.addWindowListener(JDownloader.this);
		downloadDialog.setContentPane(downloadDialogPane); downloadDialog.setVisible(true);
		return ret;
	}

	//--------------------- event listeners... ---------------------
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if((source == get) || (source == url)) {
			runner = null;
			for(int i = 0; i < fields.length; i++) { fields[i].setText(""); }
			try {
				page = new URL(url.getText());
				if(runner == null) {
					runner = new Thread(JDownloader.this);
					runner.start();
					runner.setPriority(Thread.MAX_PRIORITY);
				}
			} catch (MalformedURLException e) { status.setText(dict.get("dialogs_download_status_badurl") + " " + url.getText()); }
		}
		else if(source == cancel) {
			if (runner == null) {
				downloadDialog.dispose();
				if (pb.getValue() != pb.getMaximum()) { ret = NOTHING_DOWNLOADED; } else { ret = SOMETHING_DOWNLOADED; }
			}
			else { runner = null; }
		}
		downloadDialog.repaint();
	}

	public void windowOpened(WindowEvent evt) { }
	public void windowClosed(WindowEvent evt) { }
	public void windowClosing(WindowEvent evt) {
	Object source = evt.getSource();
		if(source == downloadDialog) {
			downloadDialog.dispose();
			if (pb.getValue() != pb.getMaximum()) { ret = NOTHING_DOWNLOADED; } else { ret = SOMETHING_DOWNLOADED; }
		}
		downloadDialog.repaint();
	}
	public void windowIconified(WindowEvent evt) { }
	public void windowDeiconified(WindowEvent evt) { }
	public void windowActivated(WindowEvent evt) { }
	public void windowDeactivated(WindowEvent evt) { }

	//--------------------- private methods... ---------------------
	private JPanel titledField(String title, JTextField field) {
		JPanel output = new JPanel();
		JLabel lab = new JLabel(title);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constr = new GridBagConstraints();
		output.setLayout(gridbag);

		buildGrid(constr, 0, 0, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constr.fill = GridBagConstraints.NONE;
		constr.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(lab, constr);
		output.add(lab);
		buildGrid(constr, 1, 0, 1, 1, 100, 0, new Insets(2, 2, 2, 2));
		constr.fill = GridBagConstraints.HORIZONTAL;
		constr.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(field, constr);
		output.add(field);
		return output;
	}

	void buildGrid(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy, Insets ins) {
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbc.insets = ins;
	}
}

class About extends JPanel {
	private HeadLines hl;
	private Toolkit kit = Toolkit.getDefaultToolkit();
	private MediaTracker tracker; Image back;

	public About() {
		this.setBackground(Color.black); this.setSize(350, 250);
		this.setPreferredSize(new Dimension(350, 250)); this.setMinimumSize(new Dimension(350, 250));
		this.setLayout(null);
		hl = new HeadLines(); this.add(hl); hl.setBounds(0, 120, 170, 130);
		back = kit.getImage("./images/aback.jpg");
		tracker = new MediaTracker(this); tracker.addImage(back, 0);
		try { tracker.waitForID(0);
		} catch (InterruptedException e) { }
	}

	public void setHeadlines(String[] headlines) { hl.setLines(headlines); hl.start(); }
	public void stop() { hl.stop(); }

	public void paint(Graphics g) {
		if (tracker.statusID(0, false) == MediaTracker.COMPLETE) { g.drawImage(back, 0, 0, this); }
	}
}

class HeadLines extends JPanel implements Runnable {
	private Thread runner;
	private Image back;
	private MediaTracker tracker;

	String[] headlines = {"Sorry, no information..."};
	int y = getSize().height + 15;

	public HeadLines() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		back = kit.getImage("images/abackt.jpg");
		tracker = new MediaTracker(this);
		tracker.addImage(back, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) { }
	}

	public void start() {
		if(runner == null) { runner = new Thread(this); runner.start(); }
	}

	public void run() {
		while(true) {
			y = y - 1;
			if(y < -(headlines.length * 15)) { y = getSize().height + 15; }
			repaint();
			try { Thread.sleep(100);
			} catch(InterruptedException e){}
		}
	}

	public void stop() { runner = null; y = getSize().height + 15; }
	public void setLines(String[] headlines) { this.headlines = headlines; }

	public void paint(Graphics g) {
		if (tracker.statusID(0, false) == MediaTracker.COMPLETE) { g.drawImage(back, 0, 0, this); }
		g.setColor(Color.white);
		for(int i = 0; i < headlines.length; i++) { g.drawString(headlines[i], 5, y + (15 * i)); }
	}
}
