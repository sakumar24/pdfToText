package converter;

import javax.swing.JFileChooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ConverterGUI {

	protected Shell PDFToTextConverter;
	private Text folderPath;
	public static Text logsText;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConverterGUI window = new ConverterGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		PDFToTextConverter.open();
		PDFToTextConverter.layout();
		while (!PDFToTextConverter.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		PDFToTextConverter = new Shell();
		PDFToTextConverter.setSize(830, 495);
		PDFToTextConverter.setText("PDFToTextConveter");
		
		Label folderPathlabel = new Label(PDFToTextConverter, SWT.NONE);
		folderPathlabel.setBounds(10, 29, 81, 15);
		folderPathlabel.setText("Input Folder:");
		
		folderPath = new Text(PDFToTextConverter, SWT.BORDER);
		folderPath.setBounds(10, 50, 492, 25);
		
		Button selectFolderBtn = new Button(PDFToTextConverter, SWT.NONE);
		selectFolderBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) 
			{
				folderPath.setText(getFolderPath());
			}
		});
		selectFolderBtn.setBounds(508, 47, 81, 27);
		selectFolderBtn.setText("Select Folder");
		
		Button btnNewButton = new Button(PDFToTextConverter, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				Converter conv = new Converter();
				conv.convert(folderPath.getText());
			}
		});
		btnNewButton.setBounds(603, 44, 81, 31);
		btnNewButton.setText("Convert");
		
		logsText = new Text(PDFToTextConverter, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		logsText.setBounds(10, 102, 783, 334);
		
		Label lblLogs = new Label(PDFToTextConverter, SWT.NONE);
		lblLogs.setBounds(10, 81, 55, 15);
		lblLogs.setText("Logs:");

	}
	private static String getFolderPath()
	{
		String path=null;
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Choose Directory");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);

	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
	    	path = chooser.getSelectedFile().getAbsolutePath();
	    else
	    	System.out.println("No Selection.");
		return path;
	}
	
	public static void appendLogs(String str)
	{
		logsText.append(str);
	}
}
