package com.looseboxes.msofficekiosk.misc;

import com.looseboxes.msofficekiosk.misc.FileSupplierFromUserSelection;
import com.looseboxes.msofficekiosk.misc.MSWordFileFilter;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

//import net.atlanticbb.tantlinger.shef.HTMLEditorPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class SwtOleFrameInSwingApp {

        private transient static final Logger LOG = Logger.getLogger(SwtOleFrameInSwingApp.class.getName());
	
	private static OleFrame oleFrame1;
	private static OleClientSite clientSite;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = new Display();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame mainFrame = new JFrame("Main Window");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.setSize(800, 600);

				JMenuBar menuBar = new JMenuBar();
				
				JMenu menuTool = new JMenu("Tool");
				
				menuBar.add(menuTool);
				
				JMenuItem openBrowserMenuItem = new JMenuItem("Open Word Viewer");
				
				menuTool.add(openBrowserMenuItem);
				mainFrame.setJMenuBar(menuBar);
				
//				HTMLEditorPane editorPane = new HTMLEditorPane();
				JEditorPane editorPane = new JEditorPane();
				editorPane.setPreferredSize(new Dimension(800, 600));
				editorPane.setBounds(10, 10, 800, 600);
				editorPane.setText("SHEF HTML Editor");
				

				mainFrame.getContentPane().add(editorPane);
				mainFrame.pack();
				mainFrame.setVisible(true);
	
				openBrowserMenuItem.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						JFrame childFrame = new JFrame("Word Viewer Window");
						childFrame.setSize(850, 690);

						JPanel childPanel = new JPanel();
						childPanel.setSize(850, 40);
						final JButton selectAllButton = new JButton("Select All");
						final JButton copyButton = new JButton("Copy");
						final JButton copyPasteButton = new JButton("Copy & Paste");
						childPanel.add(selectAllButton);
						childPanel.add(copyButton);
						childPanel.add(copyPasteButton);
						
						final Canvas canvas = new Canvas();
						canvas.setSize(850, 650);

						childFrame.getContentPane().add(childPanel, BorderLayout.NORTH);
						childFrame.getContentPane().add(canvas,BorderLayout.SOUTH);
						childFrame.pack();
						childFrame.setVisible(true);

						display.asyncExec(new Runnable() {
							
							public void run() {
								FillLayout thisLayout = new FillLayout(
										org.eclipse.swt.SWT.HORIZONTAL);
								Shell shell = SWT_AWT.new_Shell(display, canvas);
								shell.setLayout(thisLayout);
								shell.setSize(800, 600);
								
								try {
									oleFrame1 = new OleFrame(shell, SWT.NONE);
                                                                        
                                                                        final Supplier<File> fileSupplier = new FileSupplierFromUserSelection(new MSWordFileFilter());
                                                                        
									clientSite = new OleClientSite(oleFrame1, SWT.NULL, fileSupplier.get());
									clientSite.setBounds(0, 0, 104, 54);
									clientSite.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
									LOG.fine("Complete process OLE Client Site");
									
								} catch (Exception e2) {
									final String str = "Create OleClientSite Error";
                                                                        LOG.log(Level.WARNING, str, e2);
									return;
								}
								shell.layout();
								shell.open();

								selectAllButton.addActionListener(new ActionListener() {
									
									public void actionPerformed(ActionEvent e) {
										display.asyncExec(new Runnable() {
											public void run() {
												try {
													selectAll();
												} catch (Exception e2) {
													final String str = "OleAutomation Error. Action: SelectAll";
													LOG.log(Level.WARNING, str, e2);
													return;
												}
											}
										});
									}
								});

								copyButton.addActionListener(new ActionListener() {
									
									public void actionPerformed(ActionEvent e) {
										display.asyncExec(new Runnable() {
											
											public void run() {
												try {
													copySelection();
												} catch (Exception e2) {
													final String str = "OleAutomation Error. Action: CopySelection";
													LOG.log(Level.WARNING, str, e2);
													return;
												}
											}
										});
									}
								});
							}
							
							OleAutomation document;
							int[] ids;
							Variant result;

							private void selectAll() {
								document = new OleAutomation(clientSite);
								System.out.println("Instantiated document");
								ids = document.getIDsOfNames(new String[] {"Select"});
								result = document.invoke(ids[0]);

								document.dispose();
								
								LOG.fine(() -> "Invoked select");
							}

							private void copySelection() {
								document = new OleAutomation(clientSite);
								ids = document.getIDsOfNames(new String[] {"Application"});
								result = document.getProperty(ids[0]);
								document.dispose();
								
								LOG.fine(() -> "Got application");

								OleAutomation application = result.getAutomation();
								result.dispose();
								ids = application.getIDsOfNames(new String[] {"Selection"});
								result = application.getProperty(ids[0]);
								application.dispose();
																
								LOG.fine(() -> "Got selection");
								
								OleAutomation selection = result.getAutomation();
								result.dispose();
								ids = selection.getIDsOfNames(new String[] {"Copy"});
								result = selection.invoke(ids[0]);
								result.dispose();
								
								LOG.fine(() -> "Invoked Copy");
								
								result.dispose();
								ids = selection.getIDsOfNames(new String[] {"Move"});
								result = selection.invoke(ids[0]);
								result.dispose();

								LOG.fine(() -> "Invoked Move to deselect");
							}
						});
						
					}
				});
			}
		});
		display.addListener(SWT.CLOSE, new Listener() {
			
			public void handleEvent(Event event) {
				EventQueue.invokeLater(new Runnable() {
					
					public void run() {
						Frame[] frames = JFrame.getFrames();
						for (int i = 0; i < frames.length; i++) {
							frames[i].dispose();
						}
					}
				});
			}
		});
		while (!display.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
			
		}
	}
	
}