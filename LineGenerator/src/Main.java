import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class Main extends JFrame implements ActionListener {
	JTextField inputItems;
	JTextArea inputCode, outputCode;
	JScrollPane scrollInputCode, scrollOutputCode;
	boolean showOutput = false;
	String output = "";

	public Main() {
		super("Line Generator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(560, 960);
		setLocation(2560 / 2 - 560 / 2, 1440 / 2 - 960 / 2);
		setLayout(null);

		Font font = new Font("Consolas", Font.BOLD, 14);

		inputItems = new JTextField("int-float-double-long-short-byte");
		// inputItems = new JTextField("Bubble-Insertion-Merge-Quick-Selection-Shell-Heap-Counting-Radix-IterRadix-Bucket");
		inputItems.setFont(font);
		inputItems.setBounds(20, 30, 500, 25);

		inputCode = new JTextArea("println(# - $);");
		inputCode.setFont(font);
		inputCode.setBounds(20, 80, 500, 400);

		scrollInputCode = new JScrollPane(inputCode);
		scrollInputCode.setBounds(20, 80, 500, 400);

		outputCode = new JTextArea();
		outputCode.setFont(font);
		outputCode.setBounds(20, 500, 500, 400);

		scrollOutputCode = new JScrollPane(outputCode);
		scrollOutputCode.setBounds(20, 500, 500, 400);
		scrollOutputCode.setVisible(showOutput);
		setSize(560, showOutput ? 960 : 540);

		inputItems.addActionListener(this);
		getContentPane().add(inputItems);
		getContentPane().add(scrollInputCode);
		getContentPane().add(scrollOutputCode);

		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "all");
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "update");
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "copy");
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "copyWithInput");
		getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "toggle");

		getRootPane().getActionMap().put("all", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
				StringSelection selection = new StringSelection(output);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
			}
		});
		getRootPane().getActionMap().put("update", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		getRootPane().getActionMap().put("copy", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(output);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
			}
		});
		getRootPane().getActionMap().put("copyWithInput", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection selection = new StringSelection(inputCode.getText() + "\n\n" + output);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
			}
		});
		getRootPane().getActionMap().put("toggle", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showOutput = !showOutput;
				scrollOutputCode.setVisible(showOutput);
				setSize(560, showOutput ? 960 : 540);
			}
		});


		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		update();
	}

	public void update() {
		String s1 = inputItems.getText();
		String s2 = inputCode.getText();
		output = "";
		if (s2.contains("$")) {
			String[] items = s1.split("-");

			for (int i = 0; i < items.length; i++) {
				output += new String(s2).replace("$", items[i]).replace("#", String.valueOf(i));
				output += "\n";
			}
		}

		outputCode.setText(output);
	}

	public static void main(String[] args) {
		new Main();
	}
}