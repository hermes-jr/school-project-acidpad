import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class JFontChooser implements ActionListener, ListSelectionListener, KeyListener, WindowListener {
	//setting up components...
	private Font currfont;
	private FontPreview preview;
	private JDialog fontDialog;
	private JTextField familyField, styleField, sizeField;
	private JList familyList, styleList, sizeList;
	private DefaultListModel familyModel, styleModel, sizeModel;
	private JButton approve, cancel;
	private String[] allfamils, allstyles, allsizes;
	private String outFamily;
	private LanguagePack dict;
	int outStyle, outSize, ret;
	public static final int APPROVE_OPTION = 0;
	public static final int CANCEL_OPTION = 1;

	public int showFontDialog(JFrame parent, Font f, LanguagePack dict) {
		//setting up components...
		currfont = f; this.dict = dict;
		preview = new FontPreview(currfont);
		outFamily = currfont.getFamily(); outStyle = currfont.getStyle(); outSize = currfont.getSize();
		fontDialog = new JDialog(parent); fontDialog.setSize(450, 350);
		fontDialog.setResizable(false); fontDialog.setModal(true);
		fontDialog.setTitle(dict.get("dialogs_font_title").toString());
		fontDialog.setLocationRelativeTo(parent);
		familyField = new JTextField(); familyField.addKeyListener(this);
		styleField = new JTextField(); styleField.addKeyListener(this);
		sizeField = new JTextField(); sizeField.addKeyListener(this);
		familyModel = new DefaultListModel();
		styleModel = new DefaultListModel();
		sizeModel = new DefaultListModel();
		familyList = new JList(); familyList.setSelectionMode(0);
		//creating fonts list...
		allfamils = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for(int i = 0; i < allfamils.length; i++) { familyModel.add(i, allfamils[i].toString()); }
		//end of creating fonts list...
		familyList.setModel(familyModel);
		styleList = new JList(); styleList.setSelectionMode(0);
		//creating styles list...
		allstyles = new String[]{dict.get("dialogs_font_styles_re").toString(), dict.get("dialogs_font_styles_it").toString(), 
			dict.get("dialogs_font_styles_bl").toString(), dict.get("dialogs_font_styles_bi").toString()};
		for(int i = 0; i < allstyles.length; i++) { styleModel.add(i, allstyles[i].toString()); }
		//end of creating styles list...
		styleList.setModel(styleModel);
		sizeList = new JList(); sizeList.setSelectionMode(0);
		//creating size list...
		allsizes = new String[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22",
			"24", "26", "28", "36", "48", "72"};
		for(int i = 0; i < allsizes.length; i++) { sizeModel.add(i, allsizes[i].toString()); }
		//end of creating size list...
		sizeList.setModel(sizeModel);
		familyList.addListSelectionListener(this);
		styleList.addListSelectionListener(this);
		sizeList.addListSelectionListener(this);
		//setting defaults...
		familyList.setSelectedValue(currfont.getFamily(), true); familyField.setText(currfont.getFamily());
		if(currfont.getStyle() == Font.PLAIN) {
			styleList.setSelectedIndex(0); styleField.setText(styleList.getSelectedValue().toString());
		}
		else if(currfont.getStyle() == Font.ITALIC) {
			styleList.setSelectedIndex(1); styleField.setText(styleList.getSelectedValue().toString());
		}
		else if(currfont.getStyle() == Font.BOLD) {
			styleList.setSelectedIndex(2); styleField.setText(styleList.getSelectedValue().toString());
		}
		else if(currfont.getStyle() == Font.BOLD + Font.ITALIC) {
			styleList.setSelectedIndex(3); styleField.setText(styleList.getSelectedValue().toString());
		}
		sizeList.setSelectedValue(currfont.getSize() + "", true);
		sizeField.setText(sizeList.getSelectedValue().toString());

		approve = new JButton(dict.get("dialogs_font_buttons_ok").toString()); approve.addActionListener(this);
		cancel = new JButton(dict.get("dialogs_font_buttons_cancel").toString()); cancel.addActionListener(this);

		//constructing panel...
		JPanel fontDialogPane = new JPanel(null);

		JPanel fpa = new JPanel(new BorderLayout());
		JPanel fpb = new JPanel(new BorderLayout());
		fpa.add("North", new JLabel(dict.get("dialogs_font_labels_font").toString()));
		fpb.add("North", familyField);
		JScrollPane spa = new JScrollPane(familyList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		fpb.add("Center", spa);
		fpa.add("Center", fpb);
		fontDialogPane.add(fpa); fpa.setBounds(5, 5, 155, 315);

		JPanel slpa = new JPanel(new BorderLayout());
		JPanel slpb = new JPanel(new BorderLayout());
		slpa.add("North", new JLabel(dict.get("dialogs_font_labels_style").toString()));
		slpb.add("North", styleField);
		JScrollPane spb = new JScrollPane(styleList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		slpb.add("Center", spb);
		slpa.add("Center", slpb);
		fontDialogPane.add(slpa); slpa.setBounds(165, 5, 100, 190);

		JPanel szpa = new JPanel(new BorderLayout());
		JPanel szpb = new JPanel(new BorderLayout());
		szpa.add("North", new JLabel(dict.get("dialogs_font_labels_size").toString()));
		szpb.add("North", sizeField);
		JScrollPane spc = new JScrollPane(sizeList, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		szpb.add("Center", spc);
		szpa.add("Center", szpb);
		fontDialogPane.add(szpa); szpa.setBounds(270, 5, 85, 190);

		fontDialogPane.add(approve); approve.setBounds(360, 20, 80, 25);
		fontDialogPane.add(cancel); cancel.setBounds(360, 50, 80, 25);
		JPanel prevpane = new JPanel(new BorderLayout()); prevpane.add("Center", preview);
		prevpane.setBorder(new EtchedBorder());
		fontDialogPane.add(prevpane); prevpane.setBounds(165, 200, 190, 120);
		//end of constructing panel...

		fontDialog.addWindowListener(this); fontDialog.setContentPane(fontDialogPane); fontDialog.show(); return ret;
	}

	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if(source == approve) {
			currfont = new Font(outFamily, outStyle, outSize); fontDialog.dispose(); ret = APPROVE_OPTION;
		}
		else if(source == cancel) { fontDialog.dispose(); ret = CANCEL_OPTION; }
		fontDialog.repaint();
	}

	public void keyTyped(KeyEvent evt) { }
	public void keyPressed(KeyEvent evt) { }
	public void keyReleased(KeyEvent evt) {
	Object source = evt.getSource();
		if(source == familyField) { quickFind(familyField, familyList, allfamils); }
		else if(source == styleField) { quickFind(styleField, styleList, allstyles); }
		else if(source == sizeField) { quickFind(sizeField, sizeList, allsizes); }
		fontDialog.repaint();
	}

	public void valueChanged(ListSelectionEvent evt) {
		Object source = evt.getSource();
		if(source == familyList) {
			if(familyList.hasFocus()) { familyField.setText(familyList.getSelectedValue().toString()); }
			outFamily = familyList.getSelectedValue().toString(); calcFont(); preview.setFont(currfont);
		}
		else if(source == styleList) {
			if(styleList.hasFocus()) { styleField.setText(styleList.getSelectedValue().toString()); }
			if(styleList.getSelectedIndex() == 0) { outStyle = Font.PLAIN; }
			else if(styleList.getSelectedIndex() == 1) { outStyle = Font.ITALIC; }
			else if(styleList.getSelectedIndex() == 2) { outStyle = Font.BOLD; }
			else if(styleList.getSelectedIndex() == 3) { outStyle = Font.BOLD + Font.ITALIC; }
			calcFont(); preview.setFont(currfont);
		}
		else if(source == sizeList) {
			if(sizeList.hasFocus()) { sizeField.setText(sizeList.getSelectedValue().toString()); }
			outSize = Integer.parseInt(sizeList.getSelectedValue().toString());
			calcFont(); preview.setFont(currfont);
		}
		fontDialog.repaint();
	}

	public void windowOpened(WindowEvent evt) { }
	public void windowClosed(WindowEvent evt) { }
	public void windowClosing(WindowEvent evt) {
	Object source = evt.getSource();
		if(source == fontDialog) { fontDialog.dispose(); ret = CANCEL_OPTION; }
		fontDialog.repaint();
	}
	public void windowIconified(WindowEvent evt) { }
	public void windowDeiconified(WindowEvent evt) { }
	public void windowActivated(WindowEvent evt) { }
	public void windowDeactivated(WindowEvent evt) { }

	private void quickFind(JTextField textsrc, JList listsrc, String[] arraysrc) {
		String searchline = textsrc.getText().toString(); boolean found = false;
		for(int i = 0; (i < arraysrc.length) && (found == false); i++) {
			String searchpath = new String(arraysrc[i]);
			if((searchpath.toLowerCase().startsWith(searchline.toLowerCase())) && (searchpath != null)) {
				listsrc.ensureIndexIsVisible(i); listsrc.setSelectedIndex(i); found = true;
			}
		}
	}

	public Font getCurrentFont() { currfont = new Font(outFamily, outStyle, outSize); return currfont; }

	//--------------------- private methods... ---------------------
	private void calcFont() { currfont = new Font(outFamily, outStyle, outSize); }
}

class FontPreview extends JPanel {
	private Font fontToDraw;

	public FontPreview(Font font) { fontToDraw = font; repaint(); }
	public void setFont(Font font) { fontToDraw = font; repaint(); }

	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(getBackground());
		g.fillRect(0, 0, getSize().width, getSize().height);
		g.setColor(Color.black);
		g.setFont(fontToDraw);
		FontMetrics fm = getFontMetrics(fontToDraw);
		g.drawString("AaBbCc 123", 10, fm.getHeight() + 10);
	}
}