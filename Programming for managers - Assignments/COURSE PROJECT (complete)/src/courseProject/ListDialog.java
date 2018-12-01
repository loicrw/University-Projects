package courseProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListDialog {
	//initialize all instance variables and class variables
	public static int chosenFlight = 99999;
	private JScrollPane menuScrollPane;

	private JList<?> list;
	private JLabel label;
	private JOptionPane optionPane;
	private JButton okButton, cancelButton;
	private ActionListener okEvent, cancelEvent;
	private JDialog dialog;

	//two different type of constructors
	public ListDialog(String message, JList<?> listToDisplay) {
		list = listToDisplay;
		label = new JLabel(message);
		createAndDisplayOptionPane();
	}

	public ListDialog(String title, String message, JList<?> listToDisplay) {
		this(message, listToDisplay);
		dialog.setTitle(title);
	}

	//method that sets up the optionPane
	private void createAndDisplayOptionPane() {
		setupButtons();
		JPanel pane = layoutComponents();
		optionPane = new JOptionPane(pane);
		optionPane.setOptions(new Object[] { okButton, cancelButton});
		dialog = optionPane.createDialog("Flight Results");
	}

	//put the buttons and add an actionListener for each button for when its clicked
	private void setupButtons() {
		okButton = new JButton("Book");
		okButton.addActionListener(e -> handleOkButtonClick(e));

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> handleCancelButtonClick(e));
	}

	//set the layout for the panel
	private JPanel layoutComponents() {
		centerListElements();
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setPreferredSize(new Dimension(600,600));		
		panel.add(label, BorderLayout.NORTH);
		menuScrollPane = new JScrollPane(list);
		panel.add(menuScrollPane, BorderLayout.CENTER);
		return panel;
	}

	//center the contents in the list
	private void centerListElements() {
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
	}

	//actionlistener for when ok is clicked
	public void setOnOk(ActionListener event) {
		okEvent = event;
	}

	//actionListener for when cancel is clicked
	public void setOnClose(ActionListener event) {
		cancelEvent = event;
	}

	//when ok is clicked hide the frame
	private void handleOkButtonClick(ActionEvent e) {
		if (okEvent != null) {
			okEvent.actionPerformed(e);
		}
		hide();
	}

	//when cancel is clicked end the program
	private void handleCancelButtonClick(ActionEvent e) {
		if (cancelEvent != null) {
			cancelEvent.actionPerformed(e);
		}
		System.exit(0);
	}

	//method which makes dialog visible
	public void show() {
		dialog.setVisible(true);
	}



	private void hide() {
		dialog.setVisible(false);
	}
	

	//method which returns the index of the item that has been selected
	public Object getSelectedItem() {
		return list.getSelectedIndex();
	}

}
