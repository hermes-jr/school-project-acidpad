import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.net.*;
import java.io.*;

public class Acidpad implements WindowListener, ActionListener,
	UndoableEditListener, KeyListener, FocusListener {
	//misc...
	File file;
	String lastField = new String();
	GridBagLayout gridbag; GridBagConstraints constructor;
	LanguagePack dict; LanguagePack[] langs;
	LanguageChanger langChanger = new LanguageChanger();
	ImagePreview shotView;
	Font fontMemory;
	//ultra elements...
	public static final String version = "Acidpad 1.1";
	JFrame mainFrame;
	Properties properties = new Properties();
	Toolkit tk = Toolkit.getDefaultToolkit();
	//elements...
	JTextArea text, textTwo; Document document;
	JPanel splittablePane; JSplitPane splitPane;
	JScrollPane scroll, scrollTwo;
	private final UndoManager history = new UndoManager();
	JDialog settingsDialog, findDialog, replaceDialog, goToDialog, help, aboutDialog;
	About aboutCore; HelpWindow helpCore;
	JLabel status;
	JPanel settingsDialogPane, findDialogPane, replaceDialogPane, goToDialogPane, aboutDialogPane, freePane;
	JButton settingsCancel, settingsApply, settingsOk, findItNow, replaceIt, replaceItAll, goToNow,
		goToCancel, findCancel, replaceCancel, aboutCancel, chooseFont;
	JCheckBox matchCase, matchCaseTwo;
	JTextField findWhat, findRepWhat, findRepWith, goWhere;
	JRadioButton nativeView, metalView, motifView;
	//settings elements...
	JList settingsLangList;
	JColorSelector bgColorSelector, fgColorSelector, seColorSelector, stColorSelector, crColorSelector;
	JFormattedTextField historySteps, tabSizeField;

	//--------------------- menu items and qbuttons... ---------------------
	JMenuBar menubar = new JMenuBar();
	JToolBar toolBar = new JToolBar();
	JMenuItem newfile, open, openWeb, save, saveAs, exit; JButton newfileBut, openBut, saveBut;
	JMenuItem undo, redo, cut, copy, paste, selectall, find, replace, goTo, insertTD;
	JButton undoBut, redoBut, cutBut, copyBut, pasteBut, findBut, replaceBut;
	JMenuItem options;
	JCheckBoxMenuItem splitDoc; JToggleButton splitDocBut;
	JCheckBoxMenuItem helpTopics; JMenuItem about;
	JRadioButtonMenuItem noWrap, wrapWord, wrapLetter;

	public Acidpad(String[] arguments) {
		mainFrame = new JFrame(); mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//loading properties and language...
		try { properties.load(new FileInputStream("./setup/main.conf")); } catch (IOException e) { System.exit(0); }
		dict = new LanguagePack(properties.get("language_file").toString());
		history.discardAllEdits(); history.setLimit(Integer.parseInt(properties.get("undo_redo_limit").toString()));

		JPanel pane = new JPanel(new BorderLayout());
		text = new JTextArea(); text.addKeyListener(Acidpad.this); text.addFocusListener(Acidpad.this);
		status = new JLabel(); status.setText(Acidpad.version);
		scroll = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textTwo = new JTextArea(); textTwo.addKeyListener(Acidpad.this); textTwo.addFocusListener(Acidpad.this);
		scrollTwo = new JScrollPane(textTwo, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		document = text.getDocument(); document.addUndoableEditListener(Acidpad.this);
		text.setDocument(document); textTwo.setDocument(document);
		text.setTabSize(Integer.parseInt(properties.get("tabsize").toString()));
		textTwo.setTabSize(Integer.parseInt(properties.get("tabsize").toString()));
		splittablePane = new JPanel(new BorderLayout());
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		splitPane.setOneTouchExpandable(true); splitPane.setDividerSize(10); splitPane.setDividerLocation(0.5F);
		splittablePane.add("North", toolBar);
		splittablePane.add("Center", scroll);
		pane.add("Center", splittablePane);
		pane.add("South", status);

		//--------------------- menu items and qbuttons... ---------------------
		JMenu fileMenu = new JMenu(); langChanger.add(fileMenu, "mainframe_menu_file");
		newfile = new JMenuItem(); newfile.addActionListener(Acidpad.this);
		langChanger.add(newfile, "mainframe_menu_file_new");
		open = new JMenuItem(); open.addActionListener(Acidpad.this); langChanger.add(open, "mainframe_menu_file_open", true);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		openWeb = new JMenuItem(); openWeb.addActionListener(Acidpad.this);
		langChanger.add(openWeb, "mainframe_menu_file_openweb", true);
		save = new JMenuItem(); save.addActionListener(Acidpad.this); langChanger.add(save, "mainframe_menu_file_save", true);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		saveAs = new JMenuItem(); saveAs.addActionListener(Acidpad.this);
		langChanger.add(saveAs, "mainframe_menu_file_saveas", true);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		exit = new JMenuItem(); exit.addActionListener(Acidpad.this); langChanger.add(exit, "mainframe_menu_file_exit");
		fileMenu.add(newfile); fileMenu.add(open); fileMenu.add(openWeb); fileMenu.addSeparator();
		fileMenu.add(save); fileMenu.add(saveAs); fileMenu.addSeparator();
		fileMenu.add(exit);

		newfileBut = new JButton(new ImageIcon("images/icons/new.gif")); newfileBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(newfileBut, "mainframe_menu_file_new");
		openBut = new JButton(new ImageIcon("images/icons/open.gif")); openBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(openBut, "mainframe_menu_file_open", true);
		saveBut = new JButton(new ImageIcon("images/icons/save.gif")); saveBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(saveBut, "mainframe_menu_file_save");

		JMenu editMenu = new JMenu(); langChanger.add(editMenu, "mainframe_menu_edit");
		undo = new JMenuItem(); undo.addActionListener(Acidpad.this); langChanger.add(undo, "mainframe_menu_edit_undo");
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
		redo = new JMenuItem(); redo.addActionListener(Acidpad.this); langChanger.add(redo, "mainframe_menu_edit_redo");
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		cut = new JMenuItem(); cut.addActionListener(Acidpad.this); langChanger.add(cut, "mainframe_menu_edit_cut");
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
		copy = new JMenuItem(); copy.addActionListener(Acidpad.this); langChanger.add(copy, "mainframe_menu_edit_copy");
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
		paste = new JMenuItem(); paste.addActionListener(Acidpad.this); langChanger.add(paste, "mainframe_menu_edit_paste");
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
		selectall = new JMenuItem(); selectall.addActionListener(Acidpad.this);
		langChanger.add(selectall, "mainframe_menu_edit_selectall");
		selectall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
		find = new JMenuItem(); find.addActionListener(Acidpad.this); langChanger.add(find, "mainframe_menu_edit_find", true);
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
		replace = new JMenuItem(); replace.addActionListener(Acidpad.this);
		langChanger.add(replace, "mainframe_menu_edit_replace", true);
		replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
		goTo = new JMenuItem(); goTo.addActionListener(Acidpad.this);
		goTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_MASK));
		langChanger.add(goTo, "mainframe_menu_edit_gotoline", true);
		insertTD = new JMenuItem(); insertTD.addActionListener(Acidpad.this);
		langChanger.add(insertTD, "mainframe_menu_edit_inserttime");
		insertTD.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		editMenu.add(undo); editMenu.add(redo); editMenu.addSeparator();
		editMenu.add(cut); editMenu.add(copy); editMenu.add(paste); editMenu.add(selectall); editMenu.addSeparator();
		editMenu.add(find); editMenu.add(replace); editMenu.add(goTo); editMenu.addSeparator();
		editMenu.add(insertTD);

		undoBut = new JButton(new ImageIcon("images/icons/undo.gif")); undoBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(undoBut, "mainframe_menu_edit_undo");
		redoBut = new JButton(new ImageIcon("images/icons/redo.gif")); redoBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(redoBut, "mainframe_menu_edit_redo");
		cutBut = new JButton(new ImageIcon("images/icons/cut.gif")); cutBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(cutBut, "mainframe_menu_edit_cut");
		copyBut = new JButton(new ImageIcon("images/icons/copy.gif")); copyBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(copyBut, "mainframe_menu_edit_copy");
		pasteBut = new JButton(new ImageIcon("images/icons/paste.gif")); pasteBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(pasteBut, "mainframe_menu_edit_paste");

		JMenu optsMenu = new JMenu(); langChanger.add(optsMenu, "mainframe_menu_options");
		options = new JMenuItem(); options.addActionListener(Acidpad.this);
		langChanger.add(options, "mainframe_menu_options_settings", true);
		options.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		splitDoc = new JCheckBoxMenuItem("", false); splitDoc.addActionListener(Acidpad.this);
		langChanger.add(splitDoc, "mainframe_menu_options_split");
		//wrapstyle detector...
		JMenu wrapMenu = new JMenu(); langChanger.add(wrapMenu, "mainframe_menu_options_wrap");
		noWrap = new JRadioButtonMenuItem(); noWrap.addActionListener(Acidpad.this);
		langChanger.add(noWrap, "mainframe_menu_options_wrap_nowrap");
		wrapWord = new JRadioButtonMenuItem(); wrapWord.addActionListener(Acidpad.this);
		langChanger.add(wrapWord, "mainframe_menu_options_wrap_wrapw");
		wrapLetter = new JRadioButtonMenuItem(); wrapLetter.addActionListener(Acidpad.this);
		langChanger.add(wrapLetter, "mainframe_menu_options_wrap_wrapl");
		ButtonGroup wraps = new ButtonGroup();
		String currentWrap = properties.get("wrapstyle").toString();
		if(currentWrap.equals("nowrap")) { noWrap.setSelected(true); refreshWrap(); focusAtLast(); }
		else if(currentWrap.equals("wrapword")) { wrapWord.setSelected(true); refreshWrap(); focusAtLast(); }
		else if(currentWrap.equals("wrapletter")) { wrapLetter.setSelected(true); refreshWrap(); focusAtLast(); }
		wraps.add(noWrap); wraps.add(wrapWord); wraps.add(wrapLetter);
		wrapMenu.add(noWrap); wrapMenu.add(wrapWord); wrapMenu.add(wrapLetter);
		optsMenu.add(options); optsMenu.addSeparator();
		optsMenu.add(wrapMenu); optsMenu.add(splitDoc);

		splitDocBut = new JToggleButton(new ImageIcon("images/icons/split.gif"), false);
		splitDocBut.addActionListener(Acidpad.this);
		langChanger.addTooltip(splitDocBut, "mainframe_menu_options_split");

		JMenu helpMenu = new JMenu(); langChanger.add(helpMenu, "mainframe_menu_help");
		helpTopics = new JCheckBoxMenuItem("", false); helpTopics.addActionListener(Acidpad.this);
		langChanger.add(helpTopics, "mainframe_menu_help_topics");
		helpTopics.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		about = new JMenuItem(); about.addActionListener(Acidpad.this);
		langChanger.add(about, "mainframe_menu_help_about");
		helpMenu.add(helpTopics); helpMenu.addSeparator();
		helpMenu.add(about);

		menubar.add(fileMenu); menubar.add(editMenu); menubar.add(optsMenu); menubar.add(helpMenu);
		mainFrame.setJMenuBar(menubar);

		toolBar.add(newfileBut); toolBar.add(openBut); toolBar.add(saveBut); toolBar.addSeparator();
		toolBar.add(undoBut); toolBar.add(redoBut); toolBar.addSeparator();
		toolBar.add(cutBut); toolBar.add(copyBut); toolBar.add(pasteBut); toolBar.addSeparator();
		toolBar.add(splitDocBut);
		toolBar.setRollover(true);

		JLabel infoLab; JPanel buttonSet;
		//--------------------- settings dialog elements setup... ---------------------
		constructor = new GridBagConstraints();
		gridbag = new GridBagLayout();
		JPanel settingsDialogFontPane = new JPanel(gridbag);
		String[] list = new File(dict.PATH).list(new DirFilter(".lang"));
		Arrays.sort(list);
		langs = new LanguagePack[list.length];
		DefaultListModel langListModel = new DefaultListModel();
		settingsLangList = new JList(langListModel); settingsLangList.setSelectionMode(0);
		JScrollPane langListPane = new JScrollPane(settingsLangList);
		for(int i = 0; i < list.length; i++) {
			langs[i] = new LanguagePack(list[i]);
			langListModel.add(i, langs[i].getLanguageName().toString());
		}
		settingsLangList.setSelectedValue(dict.getLanguageName(), true);
		//new component...
		buildGrid(constructor, 0, 0, 1, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_langpanel_info");
		gridbag.setConstraints(infoLab, constructor);
		settingsDialogFontPane.add(infoLab);
		//new component...
		buildGrid(constructor, 0, 1, 1, 1, 100, 100, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.BOTH;
		constructor.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(langListPane, constructor);
		settingsDialogFontPane.add(langListPane);
		//new component...
		buildGrid(constructor, 0, 2, 1, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_langpanel_explanation");
		infoLab.setBorder(new EtchedBorder());
		gridbag.setConstraints(infoLab, constructor);
		settingsDialogFontPane.add(infoLab);

		constructor = new GridBagConstraints();
		gridbag = new GridBagLayout();
		JPanel settingsDialogEditorPane = new JPanel(gridbag);
		//colors pane setup...
		JPanel colors = new JPanel(new GridLayout(3, 2, 0, 0)); colors.setBorder(new EtchedBorder());
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_editorpanel_bgcolor");
		bgColorSelector = new JColorSelector(infoLab, dict);
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_editorpanel_fgcolor");
		fgColorSelector = new JColorSelector(infoLab, dict);
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_editorpanel_secolor");
		seColorSelector = new JColorSelector(infoLab, dict);
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_editorpanel_stcolor");
		stColorSelector = new JColorSelector(infoLab, dict);
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_editorpanel_crcolor");
		crColorSelector = new JColorSelector(infoLab, dict);
		chooseFont = new JButton(); langChanger.add(chooseFont, "dialogs_settings_editorpanel_font", true);
		colors.add(bgColorSelector); colors.add(fgColorSelector); colors.add(seColorSelector);
		chooseFont.addActionListener(Acidpad.this); JPanel fontPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		fontPane.add(chooseFont);
		colors.add(stColorSelector); colors.add(crColorSelector); colors.add(fontPane);
		//new component...
		buildGrid(constructor, 0, 0, 3, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_editorpanel_info");
		gridbag.setConstraints(infoLab, constructor);
		settingsDialogEditorPane.add(infoLab);
		//new component...
		buildGrid(constructor, 0, 1, 3, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(colors, constructor);
		settingsDialogEditorPane.add(colors);
		//new component...
		buildGrid(constructor, 0, 2, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_editorpanel_tabsize");
		gridbag.setConstraints(infoLab, constructor);
		settingsDialogEditorPane.add(infoLab);
		//new component...
		buildGrid(constructor, 1, 2, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		tabSizeField = new JFormattedTextField(new Integer(text.getTabSize())); tabSizeField.setColumns(4);
		gridbag.setConstraints(tabSizeField, constructor);
		settingsDialogEditorPane.add(tabSizeField);
		//new component...
		buildGrid(constructor, 2, 3, 1, 1, 100, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.CENTER;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		settingsDialogEditorPane.add(freePane);
		//new component...
		buildGrid(constructor, 0, 3, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_editorpanel_histeps");
		gridbag.setConstraints(infoLab, constructor);
		settingsDialogEditorPane.add(infoLab);
		//new component...
		buildGrid(constructor, 1, 3, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		historySteps = new JFormattedTextField(new Integer(history.getLimit())); historySteps.setColumns(4);
		gridbag.setConstraints(historySteps, constructor);
		settingsDialogEditorPane.add(historySteps);
		//new component...
		buildGrid(constructor, 2, 3, 1, 1, 100, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.CENTER;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		settingsDialogEditorPane.add(freePane);
		//new component...
		buildGrid(constructor, 0, 4, 3, 1, 100, 100, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.BOTH;
		constructor.anchor = GridBagConstraints.CENTER;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		settingsDialogEditorPane.add(freePane);

		constructor = new GridBagConstraints();
		gridbag = new GridBagLayout();
		JPanel settingsDialogViewPane = new JPanel(gridbag);
		//new component...
		buildGrid(constructor, 0, 0, 2, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_viewpanel_info");
		gridbag.setConstraints(infoLab, constructor);
		settingsDialogViewPane.add(infoLab);
		//new component...
		buildGrid(constructor, 0, 1, 1, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		buttonSet = new JPanel(new GridLayout(3, 1, 0, 4));
		ButtonGroup views = new ButtonGroup();
		nativeView = new JRadioButton(); langChanger.add(nativeView, "dialogs_settings_viewpanel_nat");
		metalView = new JRadioButton(); langChanger.add(metalView, "dialogs_settings_viewpanel_met");
		motifView = new JRadioButton(); langChanger.add(motifView, "dialogs_settings_viewpanel_mot");
		nativeView.addActionListener(Acidpad.this); metalView.addActionListener(Acidpad.this);
		motifView.addActionListener(Acidpad.this);
		buttonSet.add(nativeView); buttonSet.add(metalView); buttonSet.add(motifView);
		views.add(nativeView); views.add(metalView); views.add(motifView);
		gridbag.setConstraints(buttonSet, constructor);
		settingsDialogViewPane.add(buttonSet);
		//new component...
		buildGrid(constructor, 0, 2, 1, 1, 100, 100, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.BOTH;
		constructor.anchor = GridBagConstraints.CENTER;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_settings_viewpanel_explanation");
		infoLab.setBorder(new EtchedBorder());
		gridbag.setConstraints(infoLab, constructor);
		settingsDialogViewPane.add(infoLab);
		//new component...
		buildGrid(constructor, 1, 1, 1, 2, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.NORTH;
		shotView = new ImagePreview("images/metal_shot.gif");
		gridbag.setConstraints(shotView, constructor);
		settingsDialogViewPane.add(shotView);

		//--------------------- settings dialog setup... ---------------------
		constructor = new GridBagConstraints();
		gridbag = new GridBagLayout();
		settingsDialogPane = new JPanel(gridbag);
		JTabbedPane tabPane = new JTabbedPane();
		String tpsc = "dialogs_settings_tabpanel_";
		langChanger.add(tabPane, new String[]{tpsc + "language", tpsc + "editor", tpsc + "view"});
		tabPane.add(settingsDialogFontPane);
		tabPane.add(settingsDialogEditorPane);
		tabPane.add(settingsDialogViewPane);
		//new component...
		buildGrid(constructor, 0, 0, 4, 1, 100, 100, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.BOTH;
		constructor.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(tabPane, constructor);
		settingsDialogPane.add(tabPane);
		//new component...
		buildGrid(constructor, 0, 1, 1, 1, 100, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		settingsDialogPane.add(freePane);
		//new component...
		buildGrid(constructor, 1, 1, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.EAST;
		settingsOk = new JButton("OK"); langChanger.add(settingsOk, "dialogs_settings_buttons_ok");
		settingsOk.addActionListener(Acidpad.this);
		gridbag.setConstraints(settingsOk, constructor);
		settingsDialogPane.add(settingsOk);
		//new component...
		buildGrid(constructor, 2, 1, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.EAST;
		settingsCancel = new JButton("Cancel"); langChanger.add(settingsCancel, "dialogs_settings_buttons_cancel");
		settingsCancel.addActionListener(Acidpad.this);
		gridbag.setConstraints(settingsCancel, constructor);
		settingsDialogPane.add(settingsCancel);
		//new component...
		buildGrid(constructor, 3, 1, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.EAST;
		settingsApply = new JButton(); langChanger.add(settingsApply, "dialogs_settings_buttons_apply");
		settingsApply.addActionListener(Acidpad.this);
		gridbag.setConstraints(settingsApply, constructor);
		settingsDialogPane.add(settingsApply);

		//--------------------- find dialog setup... ---------------------
		constructor = new GridBagConstraints();
		gridbag = new GridBagLayout();
		findDialogPane = new JPanel(gridbag);
		//new component...
		buildGrid(constructor, 0, 0, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_find_findwhat");
		gridbag.setConstraints(infoLab, constructor);
		findDialogPane.add(infoLab);
		//new component...
		buildGrid(constructor, 1, 0, 1, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		findWhat = new JTextField(); findWhat.addActionListener(Acidpad.this);
		gridbag.setConstraints(findWhat, constructor);
		findDialogPane.add(findWhat);
		//new component...
		buttonSet = new JPanel(new GridLayout(2, 1, 0, 4));
		buildGrid(constructor, 2, 0, 1, 2, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.EAST;
		findItNow = new JButton(); langChanger.add(findItNow, "dialogs_find_buttons_find");
		findItNow.addActionListener(Acidpad.this); buttonSet.add(findItNow);
		findCancel = new JButton(); langChanger.add(findCancel, "dialogs_find_buttons_cancel");
		findCancel.addActionListener(Acidpad.this); buttonSet.add(findCancel);
		gridbag.setConstraints(buttonSet, constructor);
		findDialogPane.add(buttonSet);
		//new component...
		buildGrid(constructor, 0, 1, 1, 1, 0, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.EAST;
		matchCase = new JCheckBox("", false); langChanger.add(matchCase, "dialogs_find_buttons_matchcase");
		gridbag.setConstraints(matchCase, constructor);
		findDialogPane.add(matchCase);
		//new component...
		buildGrid(constructor, 1, 1, 1, 1, 100, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.CENTER;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		findDialogPane.add(freePane);

		//--------------------- replace dialog setup... ---------------------
		constructor = new GridBagConstraints();
		gridbag = new GridBagLayout();
		replaceDialogPane = new JPanel(gridbag);
		//new component...
		buildGrid(constructor, 0, 0, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_findrep_findwhat");
		gridbag.setConstraints(infoLab, constructor);
		replaceDialogPane.add(infoLab);
		//new component...
		buildGrid(constructor, 1, 0, 1, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		findRepWhat = new JTextField(); findRepWhat.addActionListener(Acidpad.this);
		gridbag.setConstraints(findRepWhat, constructor);
		replaceDialogPane.add(findRepWhat);
		//new component...
		buildGrid(constructor, 0, 1, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_findrep_replacewith");
		gridbag.setConstraints(infoLab, constructor);
		replaceDialogPane.add(infoLab);
		//new component...
		buildGrid(constructor, 1, 1, 1, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		findRepWith = new JTextField(); findRepWith.addActionListener(Acidpad.this);
		gridbag.setConstraints(findRepWith, constructor);
		replaceDialogPane.add(findRepWith);
		//new component...
		buttonSet = new JPanel(new GridLayout(3, 1, 0, 4));
		buildGrid(constructor, 2, 0, 1, 3, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.EAST;
		replaceIt = new JButton(); langChanger.add(replaceIt, "dialogs_findrep_buttons_replace");
		replaceIt.addActionListener(Acidpad.this); buttonSet.add(replaceIt);
		replaceItAll = new JButton(); langChanger.add(replaceItAll, "dialogs_findrep_buttons_replaceall");
		replaceItAll.addActionListener(Acidpad.this); buttonSet.add(replaceItAll);
		replaceCancel = new JButton(); langChanger.add(replaceCancel, "dialogs_findrep_buttons_cancel");
		replaceCancel.addActionListener(Acidpad.this); buttonSet.add(replaceCancel);
		gridbag.setConstraints(buttonSet, constructor);
		replaceDialogPane.add(buttonSet);
		//new component...
		buildGrid(constructor, 0, 2, 1, 1, 0, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.EAST;
		matchCaseTwo = new JCheckBox("", false); langChanger.add(matchCaseTwo, "dialogs_findrep_buttons_matchcase");
		gridbag.setConstraints(matchCaseTwo, constructor);
		replaceDialogPane.add(matchCaseTwo);
		//new component...
		buildGrid(constructor, 1, 2, 1, 1, 100, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.CENTER;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		replaceDialogPane.add(freePane);

		//--------------------- goto dialog setup... ---------------------
		constructor = new GridBagConstraints();
		gridbag = new GridBagLayout();
		goToDialogPane = new JPanel(gridbag);
		//new component...
		buildGrid(constructor, 0, 0, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		infoLab = new JLabel(); langChanger.add(infoLab, "dialogs_goto_gowhere");
		gridbag.setConstraints(infoLab, constructor);
		goToDialogPane.add(infoLab);
		//new component...
		buildGrid(constructor, 1, 0, 1, 1, 100, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.HORIZONTAL;
		constructor.anchor = GridBagConstraints.WEST;
		goWhere = new JTextField(); goWhere.addActionListener(Acidpad.this);
		gridbag.setConstraints(goWhere, constructor);
		goToDialogPane.add(goWhere);
		//new component...
		buttonSet = new JPanel(new GridLayout(2, 1, 0, 4));
		buildGrid(constructor, 2, 0, 1, 2, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.EAST;
		goToNow = new JButton(); langChanger.add(goToNow, "dialogs_goto_buttons_go");
		goToNow.addActionListener(Acidpad.this); buttonSet.add(goToNow);
		goToCancel = new JButton(); langChanger.add(goToCancel, "dialogs_goto_buttons_cancel");
		goToCancel.addActionListener(Acidpad.this); buttonSet.add(goToCancel);
		gridbag.setConstraints(buttonSet, constructor);
		goToDialogPane.add(buttonSet);
		//new component...
		buildGrid(constructor, 0, 1, 2, 1, 100, 100, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.BOTH;
		constructor.anchor = GridBagConstraints.CENTER;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		goToDialogPane.add(freePane);

		constructor = new GridBagConstraints();
		gridbag = new GridBagLayout();
		aboutDialogPane = new JPanel(gridbag);
		//new component...
		buildGrid(constructor, 0, 0, 2, 1, 100, 100, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.BOTH;
		constructor.anchor = GridBagConstraints.CENTER;
		aboutCore = new About();
		gridbag.setConstraints(aboutCore, constructor);
		aboutDialogPane.add(aboutCore);
		//new component...
		buildGrid(constructor, 0, 1, 1, 1, 100, 0, new Insets(0, 0, 0, 0));
		constructor.fill = GridBagConstraints.BOTH;
		constructor.anchor = GridBagConstraints.CENTER;
		freePane = new JPanel();
		gridbag.setConstraints(freePane, constructor);
		aboutDialogPane.add(freePane);
		//new component...
		buildGrid(constructor, 1, 1, 1, 1, 0, 0, new Insets(2, 2, 2, 2));
		constructor.fill = GridBagConstraints.NONE;
		constructor.anchor = GridBagConstraints.WEST;
		aboutCancel = new JButton(); aboutCancel.addActionListener(Acidpad.this);
		langChanger.add(aboutCancel, "dialogs_about_cancel");
		gridbag.setConstraints(aboutCancel, constructor);
		aboutDialogPane.add(aboutCancel);

		//--------------------- frame setup... ---------------------
		tk.setDynamicLayout(true);
		mainFrame.addWindowListener(Acidpad.this); mainFrame.setContentPane(pane);
		smartStart();
		mainFrame.setIconImage(tk.getImage("./images/acid.gif"));
		mainFrame.setTitle(Acidpad.version);
		if(arguments.length != 0) { open(new File(arguments[0])); }

		settingsDialog = new JDialog(mainFrame, true); langChanger.add(settingsDialog, "dialogs_settings_title");
		settingsDialog.setSize(400, 350); settingsDialog.setResizable(true);
		settingsDialog.setContentPane(settingsDialogPane); settingsDialog.setLocationRelativeTo(mainFrame);

		findDialog = new JDialog(mainFrame, false); langChanger.add(findDialog, "dialogs_find_title");
		findDialog.setSize(450, 120); findDialog.setResizable(false);
		findDialog.setContentPane(findDialogPane); findDialog.setLocationRelativeTo(mainFrame);

		replaceDialog = new JDialog(mainFrame, false); langChanger.add(replaceDialog, "dialogs_findrep_title");
		replaceDialog.setSize(450, 150); replaceDialog.setResizable(false);
		replaceDialog.setContentPane(replaceDialogPane); replaceDialog.setLocationRelativeTo(mainFrame);

		goToDialog = new JDialog(mainFrame, false); langChanger.add(goToDialog, "dialogs_goto_title");
		goToDialog.setResizable(false); goToDialog.setContentPane(goToDialogPane);
		goToDialog.setSize(250, 100); goToDialog.setLocationRelativeTo(mainFrame);

		help = new JDialog(mainFrame, false); langChanger.add(help, "dialogs_help_title");
		help.setSize(350, 400); help.setResizable(true); help.addWindowListener(Acidpad.this);
		helpCore = new HelpWindow(dict, langChanger);
		help.setContentPane(helpCore); help.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		help.setLocationRelativeTo(mainFrame);

		aboutDialog = new JDialog(mainFrame, true); langChanger.add(aboutDialog, "dialogs_about_title");
		aboutDialog.setResizable(false); aboutDialog.setContentPane(aboutDialogPane);
		aboutDialog.pack(); aboutDialog.setLocationRelativeTo(mainFrame);

		text.requestFocus();
		mainFrame.setVisible(true);
		langChanger.changeLanguage(dict); checkCanUndo(); mainFrame.repaint();
	}

	//--------------------- event listeners... ---------------------
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if(source == options) { regetSettings(); settingsDialog.setVisible(true); }
		else if(source == splitDoc) {
			splitDocBut.setSelected(splitDoc.isSelected());
			if(splitDoc.isSelected()) {
				document.removeUndoableEditListener(Acidpad.this);
				splittablePane.remove(scroll); splittablePane.add("Center", splitPane);
				splitPane.setTopComponent(scroll); splitPane.setBottomComponent(scrollTwo);
				splitPane.setDividerLocation(0.5F);
				splittablePane.invalidate(); splittablePane.validate();
				document.addUndoableEditListener(Acidpad.this);
			} else {
				document.removeUndoableEditListener(Acidpad.this);
				splittablePane.remove(splitPane); splittablePane.add("Center", scroll);
				splittablePane.invalidate(); splittablePane.validate();
				document.addUndoableEditListener(Acidpad.this);
			}
		}
		else if(source == splitDocBut) {
			splitDoc.setSelected(splitDocBut.isSelected());
			if(splitDocBut.isSelected()) {
				document.removeUndoableEditListener(Acidpad.this);
				splittablePane.remove(scroll); splittablePane.add("Center", splitPane);
				splitPane.setTopComponent(scroll); splitPane.setBottomComponent(scrollTwo);
				splitPane.setDividerLocation(0.5F);
				splittablePane.invalidate(); splittablePane.validate();
				document.addUndoableEditListener(Acidpad.this);
			} else {
				document.removeUndoableEditListener(Acidpad.this);
				splittablePane.remove(splitPane); splittablePane.add("Center", scroll);
				splittablePane.invalidate(); splittablePane.validate();
				document.addUndoableEditListener(Acidpad.this);
			}
		}
		else if((source == noWrap) || (source == wrapWord) || (source == wrapLetter)) { refreshWrap(); }
		else if((source == undo) || (source == undoBut)) {
			try {
				if(history.canUndo()) { history.undo(); }
			} catch(CannotUndoException e) { status.setText(dict.get("mainframe_statusbar_cannot_undo")); }
		}
		else if((source == redo) || (source == redoBut)) {
			try {
				if(history.canRedo()) { history.redo(); }
			} catch(CannotRedoException e) { status.setText(dict.get("mainframe_statusbar_cannot_redo")); }
		}
		else if((source == newfile) || (source == newfileBut)) {
			file = null;
			text.setText("");
			status.setText(dict.get("mainframe_statusbar_newfile_created"));
			text.setCaretPosition(0); textTwo.setCaretPosition(0); history.discardAllEdits(); focusAtLast();
			mainFrame.setTitle(Acidpad.version);
		}
		else if((source == open) || (source == openBut)) { openAs(); }
		else if(source == openWeb) {
			JDownloader jdl = new JDownloader(dict);
			int a = jdl.showDownloadDialog(mainFrame);
			if(a == JDownloader.SOMETHING_DOWNLOADED) {
				text.setText(jdl.getSource());
				text.setCaretPosition(0); textTwo.setCaretPosition(0); history.discardAllEdits();
				focusAtLast();
			}
		}
		else if((source == save) || (source == saveBut)) { save(); }
		else if(source == saveAs) { saveAs(); }
		else if(source == exit) { saveOnExit(); System.exit(0); }
		else if((source == cut) || (source == cutBut)) {
			if(lastField.equals("text")) { text.cut(); text.requestFocus(); }
			else if(lastField.equals("textTwo")) { textTwo.cut(); textTwo.requestFocus(); }
		}
		else if((source == copy) || (source == copyBut)) {
			if(lastField.equals("text")) { text.copy(); text.requestFocus(); }
			else if(lastField.equals("textTwo")) { textTwo.copy(); textTwo.requestFocus(); }
		}
		else if((source == paste) || (source == pasteBut)) {
			if(lastField.equals("text")) { text.paste(); text.requestFocus(); }
			else if(lastField.equals("textTwo")) { textTwo.paste(); textTwo.requestFocus(); }
		}
		else if(source == selectall) {
			if(lastField.equals("text")) { text.selectAll(); text.requestFocus(); }
			else if(lastField.equals("textTwo")) { textTwo.selectAll(); textTwo.requestFocus(); }
		}
		else if((source == find) || (source == findBut)) { findDialog.setVisible(true); }
		else if((source == findItNow) || (source == findWhat)) { find(findWhat.getText(), matchCase.isSelected()); }
		else if(source == findCancel) { findDialog.dispose(); }
		else if((source == replace) || (source == replaceBut)) { replaceDialog.setVisible(true); }
		else if((source == replaceIt) || (source == findRepWhat) || (source == findRepWith)) {
			replace(findRepWhat.getText(), findRepWith.getText(), matchCaseTwo.isSelected());
		}
		else if(source == replaceItAll) { replaceAll(findRepWhat.getText(), findRepWith.getText(), matchCaseTwo.isSelected()); }
		else if(source == replaceCancel) { replaceDialog.dispose(); }
		else if(source == goTo) { goWhere.setText(""); goToDialog.setVisible(true); }
		else if((source == goToNow) || (source == goWhere)) {
			try {
				int whereYouWant = (Integer.parseInt(goWhere.getText()) - 1);
				if(lastField.equals("text")) { text.setCaretPosition(text.getLineStartOffset(whereYouWant)); }
				else if(lastField.equals("textTwo")) { textTwo.setCaretPosition(textTwo.getLineStartOffset(whereYouWant)); }
			} catch (Exception e) {
				JOptionPane.showMessageDialog(mainFrame, dict.get("dialogs_goto_alert_nopos_message"),
				dict.get("dialogs_goto_alert_nopos_title"), JOptionPane.INFORMATION_MESSAGE);
			}
			goToDialog.dispose(); focusAtLast();
		}
		else if(source == goToCancel) { goToDialog.dispose(); }
		else if(source == insertTD) {
			if(lastField.equals("text")) { text.replaceRange(new GregorianCalendar().getTime().toString(),
				text.getSelectionStart(), text.getSelectionEnd()); }
			if(lastField.equals("textTwo")) { textTwo.replaceRange(new GregorianCalendar().getTime().toString(),
				textTwo.getSelectionStart(), textTwo.getSelectionEnd()); }
		}
		else if(source == helpTopics) {
			helpCore.setLanguage(dict);
			help.setVisible(helpTopics.isSelected()); mainFrame.requestFocus(); focusAtLast();
		}
		else if(source == about) {
			aboutCore.setHeadlines(getHeadlinesForAbout());
			aboutDialog.setVisible(true); aboutDialog.toFront(); aboutDialog.requestFocus();
		}
		else if(source == chooseFont) {
			JFontChooser fch = new JFontChooser();
			int a = fch.showFontDialog(mainFrame, fontMemory, dict);
			if(a == JFontChooser.APPROVE_OPTION) { fontMemory = fch.getCurrentFont(); }
		}
		else if(source == nativeView) { if(nativeView.isSelected()) { shotView.setImage("images/native_shot.gif"); }}
		else if(source == metalView) { if(metalView.isSelected()) { shotView.setImage("images/metal_shot.gif"); }}
		else if(source == motifView) { if(motifView.isSelected()) { shotView.setImage("images/motif_shot.gif"); }}
		else if(source == aboutCancel) { aboutDialog.dispose(); }
		else if(source == settingsOk) { applySettings(); settingsDialog.dispose(); focusAtLast(); }
		else if(source == settingsCancel) { settingsDialog.dispose(); focusAtLast(); }
		else if(source == settingsApply) { applySettings(); }
		checkCanUndo();
		mainFrame.repaint();
	}

	public void undoableEditHappened(UndoableEditEvent evt) { history.addEdit(evt.getEdit()); }

	public void keyPressed(KeyEvent evt) { }
	public void keyReleased(KeyEvent evt) { }
	public void keyTyped(KeyEvent evt) {
		Object source = evt.getSource();
		if(source == text) { checkCanUndo(); }
		else if(source == textTwo) { checkCanUndo(); }
		mainFrame.repaint();
	}

	public void windowOpened(WindowEvent evt) { }
	public void windowClosed(WindowEvent evt) { }
	public void windowIconified(WindowEvent evt) { }
	public void windowDeiconified(WindowEvent evt) { }
	public void windowActivated(WindowEvent evt) { }
	public void windowDeactivated(WindowEvent evt) { }
	public void windowClosing(WindowEvent evt) {
		Object source = evt.getSource();
		if(source == mainFrame) { saveOnExit(); System.exit(0); }
		else if(source == help) { helpTopics.setSelected(false); }
	}

	public void focusGained(FocusEvent evt) {
		Object source = evt.getSource();
		if(!evt.isTemporary()) {
			if(source == text) { lastField = "text"; }
			else if(source == textTwo) { lastField = "textTwo"; }
		}
		mainFrame.repaint();
	}
	public void focusLost(FocusEvent evt) {
		Object source = evt.getSource();
		if(!evt.isTemporary()) {
			if(source == text) { lastField = "text"; }
			else if(source == textTwo) { lastField = "textTwo"; }
		}
		mainFrame.repaint();
	}

	//--------------------- private methods... ---------------------
	private void smartStart() {
		StringTokenizer particles; Dimension screenSize = tk.getScreenSize();
		int width, height, top, left;
		if(!properties.containsKey("mainframe_size")) {
			width = 450; height = 400;
			mainFrame.setSize(width, height); mainFrame.setResizable(true);
		} else {
			particles = new StringTokenizer(properties.get("mainframe_size").toString(), ",");
			width = Integer.parseInt(particles.nextToken());
			height = Integer.parseInt(particles.nextToken());
			if(height > (screenSize.height + 10)) { height = screenSize.height / 2; }
			if(width > (screenSize.width + 10)) { width = screenSize.width / 2; }
			mainFrame.setSize(width, height); mainFrame.setResizable(true);
		}
		if(!properties.containsKey("mainframe_location")) {
			mainFrame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		} else {
			particles = new StringTokenizer(properties.get("mainframe_location").toString(), ",");
			left = Integer.parseInt(particles.nextToken());
			top = Integer.parseInt(particles.nextToken());
			if((left < 0) || (left > (screenSize.width - width))) { left = screenSize.width/10; }
			if((top < 0) || (top > (screenSize.height - height))) { top = screenSize.height/10; }
			mainFrame.setLocation(left, top);
		}
		if(properties.get("mainframe_state").toString().equals("maximized")) { mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else if (properties.get("mainframe_state").toString().equals("normal")) { mainFrame.setExtendedState(JFrame.NORMAL); }
		text.setBackground(parseColor(properties.get("textfield_bg").toString()));
		text.setForeground(parseColor(properties.get("textfield_fg").toString()));
		text.setSelectionColor(parseColor(properties.get("textfield_se").toString()));
		text.setSelectedTextColor(parseColor(properties.get("textfield_st").toString()));
		text.setCaretColor(parseColor(properties.get("textfield_cr").toString()));
		textTwo.setBackground(parseColor(properties.get("textfield_bg").toString()));
		textTwo.setForeground(parseColor(properties.get("textfield_fg").toString()));
		textTwo.setSelectionColor(parseColor(properties.get("textfield_se").toString()));
		textTwo.setSelectedTextColor(parseColor(properties.get("textfield_st").toString()));
		textTwo.setCaretColor(parseColor(properties.get("textfield_cr").toString()));
		text.setFont(new Font(properties.get("font_family").toString(), Integer.parseInt(properties.get("font_style").toString()),
			Integer.parseInt(properties.get("font_size").toString()))); textTwo.setFont(text.getFont());
	}

	private void saveOnExit() {
		try {
			//language...
			properties.setProperty("language_file", dict.get("file_name"));
			//size and position...
			if(mainFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) { properties.setProperty("mainframe_state", "maximized"); }
			else {
				properties.setProperty("mainframe_state", "normal");
				properties.setProperty("mainframe_size", mainFrame.getSize().width + "," + mainFrame.getSize().height);
				properties.setProperty("mainframe_location", mainFrame.getX() + "," + mainFrame.getY());
			}
			//colors...
			Color colorToSave;
			colorToSave = text.getBackground(); properties.setProperty("textfield_bg", colorToSave.getRed() + "," +
				colorToSave.getGreen() + "," + colorToSave.getBlue());
			colorToSave = text.getForeground(); properties.setProperty("textfield_fg", colorToSave.getRed() + "," +
				colorToSave.getGreen() + "," + colorToSave.getBlue());
			colorToSave = text.getSelectionColor(); properties.setProperty("textfield_se", colorToSave.getRed() + "," +
				colorToSave.getGreen() + "," + colorToSave.getBlue());
			colorToSave = text.getSelectedTextColor(); properties.setProperty("textfield_st", colorToSave.getRed() + "," +
				colorToSave.getGreen() + "," + colorToSave.getBlue());
			colorToSave = text.getCaretColor(); properties.setProperty("textfield_cr", colorToSave.getRed() + "," +
				colorToSave.getGreen() + "," + colorToSave.getBlue());
			//fonts...
			Font fontToSave = text.getFont();
			properties.setProperty("font_family", fontToSave.getFamily());
			properties.setProperty("font_style", fontToSave.getStyle() + "");
			properties.setProperty("font_size", fontToSave.getSize() + "");
			//misc...
			properties.setProperty("undo_redo_limit", history.getLimit() + "");
			properties.setProperty("tabsize", text.getTabSize() + "");
			if(nativeView.isSelected()) { properties.setProperty("view_system", "native"); }
			else if(metalView.isSelected()) { properties.setProperty("view_system", "metal"); }
			else if(motifView.isSelected()) { properties.setProperty("view_system", "motif"); }
			if(noWrap.isSelected()) { properties.setProperty("wrapstyle", "nowrap"); }
			else if(wrapWord.isSelected()) { properties.setProperty("wrapstyle", "wrapword"); }
			else if(wrapLetter.isSelected()) { properties.setProperty("wrapstyle", "wrapletter"); }
			//store...
			properties.store(new FileOutputStream("./setup/main.conf"), Acidpad.version);
		} catch (IOException e) { }
	}

	private void focusAtLast() {
		if(lastField.equals("text")) { text.requestFocus(); }
		else if(lastField.equals("textTwo")) { textTwo.requestFocus(); }
	}

	private void refreshWrap() {
		text.setLineWrap(!noWrap.isSelected()); textTwo.setLineWrap(!noWrap.isSelected());
		if(wrapWord.isSelected()) { text.setWrapStyleWord(true); textTwo.setWrapStyleWord(true); }
		else if(wrapLetter.isSelected()) { text.setWrapStyleWord(false); textTwo.setWrapStyleWord(false); }
	}

	private void find(String findStr, boolean caseSensitive) {
		String source = text.getText();
		if(!caseSensitive) { source = source.toUpperCase().toLowerCase(); findStr = findStr.toUpperCase().toLowerCase(); }
		int i = 0;
		if(lastField.equals("text")) { i = source.indexOf(findStr, text.getCaretPosition()); }
		else if(lastField.equals("textTwo")) { i = source.indexOf(findStr, textTwo.getCaretPosition()); }
		if(i != -1) {
			if(lastField.equals("text")) { text.setCaretPosition(i); text.moveCaretPosition(i + findStr.length()); }
			else if(lastField.equals("textTwo")) { textTwo.setCaretPosition(i); textTwo.moveCaretPosition(i + findStr.length()); }}
		else {
			String messageText = dict.get("dialogs_find_alert_cantfind_message") + " \"" + findStr + "\".";
			JOptionPane.showMessageDialog(mainFrame, messageText,
			dict.get("dialogs_find_alert_notfound_title"), JOptionPane.INFORMATION_MESSAGE);
		}
		checkCanUndo();
	}

	private void replace(String findStr, String repStr, boolean caseSensitive) {
		String source = text.getText();
		if(!caseSensitive) { source = source.toUpperCase().toLowerCase(); findStr = findStr.toUpperCase().toLowerCase(); }
		int i = 0;
		if(lastField.equals("text")) { i = source.indexOf(findStr, text.getCaretPosition()); }
		else if(lastField.equals("textTwo")) { i = source.indexOf(findStr, textTwo.getCaretPosition()); }
		if(i != -1) {
			if(lastField.equals("text")) {
				text.setCaretPosition(i); text.moveCaretPosition(i + findStr.length());
				String messageText = dict.get("dialogs_findrep_alert_replace_message") + " \"" + findStr + "\".";
				int repOrNot = JOptionPane.showConfirmDialog(mainFrame, messageText,
				dict.get("dialogs_findrep_alert_replace_title"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(repOrNot == JOptionPane.YES_OPTION) {
					if(lastField.equals("text")) { text.replaceRange(repStr, text.getSelectionStart(), text.getSelectionEnd()); }
					else if(lastField.equals("textTwo")) {
						textTwo.replaceRange(repStr, textTwo.getSelectionStart(), textTwo.getSelectionEnd());
					}
				}
			}
			else if(lastField.equals("textTwo")) { textTwo.setCaretPosition(i); textTwo.moveCaretPosition(i + findStr.length()); }}
		else {
			String messageText = dict.get("dialogs_find_alert_cantfind_message") + " \"" + findStr + "\".";
			JOptionPane.showMessageDialog(mainFrame, messageText,
			dict.get("dialogs_find_alert_notfound_title"), JOptionPane.INFORMATION_MESSAGE);
		}
		checkCanUndo();
	}

	private void replaceAll(String findStr, String repStr, boolean caseSensitive) {
		String source = text.getText();
		if(!caseSensitive) { source = source.toUpperCase().toLowerCase(); findStr = findStr.toUpperCase().toLowerCase(); }
		int i = 0, replacements = 0;
		while(i < source.length()) {
			if(lastField.equals("text")) { i = source.indexOf(findStr, i); }
			else if(lastField.equals("textTwo")) { i = source.indexOf(findStr, i); }
			if(i != -1) {
				if(lastField.equals("text")) { text.replaceRange(repStr, i, i + findStr.length()); }
				else if(lastField.equals("textTwo")) { textTwo.replaceRange(repStr, i, i + findStr.length()); }
				i += repStr.length(); replacements ++;
			}
			else {
				String messageText = dict.get("dialogs_findrep_alert_numrep_message") + " " + replacements + ".";
				JOptionPane.showMessageDialog(mainFrame, messageText,
				dict.get("dialogs_findrep_alert_numrep_title"), JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
		checkCanUndo();
	}

	private void open(File fileToOpen) {
		text.setText(""); file = fileToOpen;
		try {
			StringBuffer buf = new StringBuffer();
			FileReader fr = new FileReader(file);
			BufferedReader bufr = new BufferedReader(fr);
			boolean eof = false;
			while(!eof) {
				String line = bufr.readLine();
				if(line == null) { eof = true; } else { buf.append(line + "\n"); }
			}
			fr.close();
			//safe reading...
			if(buf.toString().length() <= 1) { text.setText(buf.toString()); }
			else { text.setText(buf.toString().substring(0, buf.toString().length() - 1)); }
			status.setText(dict.get("mainframe_statusbar_file_opened") + "-[" + file.getName() + "]");
			mainFrame.setTitle(Acidpad.version + "-[" + file.getName() + "]");
			text.setCaretPosition(0); history.discardAllEdits(); checkCanUndo();
		} catch(IOException e){ }
	}

	private void openAs() {
		JFileChooser fch = new JFileChooser(".");
		String path = "dialogs_file_extens";
		fch.addChoosableFileFilter(new AcidFileFilter(".htm", dict.get(path + "1")));
		fch.addChoosableFileFilter(new AcidFileFilter(".jsp", dict.get(path + "2")));
		fch.addChoosableFileFilter(new AcidFileFilter(".as", dict.get(path + "3")));
		fch.addChoosableFileFilter(new AcidFileFilter(".php", dict.get(path + "4")));
		fch.addChoosableFileFilter(new AcidFileFilter(".css", dict.get(path + "5")));
		fch.addChoosableFileFilter(new AcidFileFilter(".txt", dict.get(path + "6")));
		fch.addChoosableFileFilter(new AcidFileFilter(".apd", dict.get(path + "7")));
		fch.setFileHidingEnabled(true); fch.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fch.setMultiSelectionEnabled(false);
		fch.setDialogTitle(dict.get("dialogs_openfile_title"));
		int s = fch.showOpenDialog(mainFrame);
		if(s == JFileChooser.APPROVE_OPTION) {
			text.setText("");
			open(new File(fch.getCurrentDirectory().toString(), fch.getSelectedFile().getName()));
		}
	}

	private void save() {
		if(file == null) { saveAs(); }
		else {
			try {
				FileWriter fw = new FileWriter(file);
				fw.write(text.getText().toString()); fw.close();
				status.setText(dict.get("mainframe_statusbar_file_saved") + "-[" + file.getName() + "]");
				mainFrame.setTitle(Acidpad.version + "-[" + file.getName() + "]");
			} catch(IOException e) {}
		}
	}

	private void saveAs() {
		JFileChooser fch = new JFileChooser(".");
		String path = "dialogs_file_extens";
		fch.addChoosableFileFilter(new AcidFileFilter(".htm", dict.get(path + "1")));
		fch.addChoosableFileFilter(new AcidFileFilter(".jsp", dict.get(path + "2")));
		fch.addChoosableFileFilter(new AcidFileFilter(".asp", dict.get(path + "3")));
		fch.addChoosableFileFilter(new AcidFileFilter(".php", dict.get(path + "4")));
		fch.addChoosableFileFilter(new AcidFileFilter(".css", dict.get(path + "5")));
		fch.addChoosableFileFilter(new AcidFileFilter(".txt", dict.get(path + "6")));
		fch.addChoosableFileFilter(new AcidFileFilter(".apd", dict.get(path + "7")));
		fch.setFileHidingEnabled(true); fch.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fch.setMultiSelectionEnabled(false);
		fch.setDialogTitle(dict.get("dialogs_savefile_title"));
		int s = fch.showSaveDialog(mainFrame);
		if(s == JFileChooser.APPROVE_OPTION) {
			file = new File(fch.getCurrentDirectory().toString(), fch.getSelectedFile().getName()); save();
		}
	}

	private void checkCanUndo() {
		if(history.canUndo()) { undo.setEnabled(true); undoBut.setEnabled(true); }
		else if(!history.canUndo()) { undo.setEnabled(false); undoBut.setEnabled(false); }
		if(history.canRedo()) { redo.setEnabled(true); redoBut.setEnabled(true); }
		else if(!history.canRedo()) { redo.setEnabled(false); redoBut.setEnabled(false); }
	}

	private void regetSettings() {
		bgColorSelector.setColor(text.getBackground());
		fgColorSelector.setColor(text.getForeground());
		seColorSelector.setColor(text.getSelectionColor());
		stColorSelector.setColor(text.getSelectedTextColor());
		crColorSelector.setColor(text.getCaretColor());
		fontMemory = text.getFont();
		historySteps.setValue(new Integer(history.getLimit()));
		tabSizeField.setValue(new Integer(text.getTabSize()));
		if(properties.get("view_system").toString().equals("native")) {
			nativeView.setSelected(true); shotView.setImage("images/native_shot.gif");
		}
		else if(properties.get("view_system").toString().equals("metal")) {
			metalView.setSelected(true); shotView.setImage("images/metal_shot.gif");
		}
		else if(properties.get("view_system").toString().equals("motif")) {
			motifView.setSelected(true); shotView.setImage("images/motif_shot.gif");
		}
		else { nativeView.setSelected(true); shotView.setImage("images/native_shot.gif"); }
	}

	private void resetSettings() {
		//colors...
		text.setBackground(bgColorSelector.getColor()); text.setForeground(fgColorSelector.getColor());
		text.setSelectionColor(seColorSelector.getColor()); text.setSelectedTextColor(stColorSelector.getColor());
		text.setCaretColor(crColorSelector.getColor());
		textTwo.setBackground(bgColorSelector.getColor()); textTwo.setForeground(fgColorSelector.getColor());
		textTwo.setSelectionColor(seColorSelector.getColor()); textTwo.setSelectedTextColor(stColorSelector.getColor());
		textTwo.setCaretColor(crColorSelector.getColor());
		text.setFont(fontMemory); textTwo.setFont(fontMemory);
		try { history.setLimit(Integer.parseInt(historySteps.getValue().toString())); }
		catch (Exception e) { history.setLimit(100); }
		try {
			int ts = Integer.parseInt(tabSizeField.getValue().toString());
			if((ts >= 1) || (ts <=14)) { text.setTabSize(ts); textTwo.setTabSize(ts); }
		}
		catch (Exception e) { text.setTabSize(8); textTwo.setTabSize(8); }
	}

	private void applySettings() {
		dict = langs[settingsLangList.getSelectedIndex()]; langChanger.changeLanguage(dict);
		resetSettings(); mainFrame.repaint();
	}

	private String[] getHeadlinesForAbout() {
		//counting lines...
		int totalLines = 0; boolean hasMoreLines = true;
		do {
			if(!dict.containsKey("dialogs_about_line" + totalLines)) { hasMoreLines = false;
			} else { totalLines++; }
		} while(hasMoreLines);
		totalLines = totalLines;
		String[] output = new String[totalLines];
		for(int i = 0; i < totalLines; i++) { output[i] = dict.get("dialogs_about_line" + i).toString(); }
		return output;
	}

	private Color parseColor(String string) {
		int r = 0, g = 0, b = 0;
		StringTokenizer tok = new StringTokenizer(string, ",");
		r = Integer.parseInt(tok.nextToken()); g = Integer.parseInt(tok.nextToken()); b = Integer.parseInt(tok.nextToken());
		return new Color(r, g, b);
	}

	private void buildGrid(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy, Insets ins) {
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gbc.insets = ins;
	}

	//--------------------- main method... ---------------------
	public static void main(String[] arguments) {
		Properties properties = new Properties();
		String viewSystem;
		try {
			properties.load(new FileInputStream("./setup/main.conf")); viewSystem = properties.get("view_system").toString(); }
			catch(IOException e){ viewSystem = "native"; }
		try {
			if(viewSystem.equals("native")) { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
			else if(viewSystem.equals("metal")) { UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel()); }
			else if(viewSystem.equals("motif")) { UIManager.setLookAndFeel(new com.sun.java.swing.plaf.motif.MotifLookAndFeel()); }
			else { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		} catch (Exception e) { }
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		Acidpad acidpad = new Acidpad(arguments);
	}
}