/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.looseboxes.msofficekiosk.misc;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 25, 2018 11:35:24 PM
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 */
public class TextEditorExample {
  Shell shell;

  ToolBar toolBar;

  StyledText styledText;

  Images images = null; // new Images();

  Vector cachedStyles = new Vector();

  Color RED = null;

  Color BLUE = null;

  Color GREEN = null;

  Font font = null;

  ToolItem boldButton, italicButton, underlineButton, strikeoutButton;

  public static void main(String[] args) {
    Display display = new Display();
    TextEditorExample example = new TextEditorExample();
    Shell shell = example.init(display);
    shell.setSize(500, 300);
    shell.open();
    while (!shell.isDisposed())
      if (!display.readAndDispatch())
        display.sleep();
    display.dispose();
  }
  
  Menu createEditMenu() {
    Menu bar = shell.getMenuBar();
    Menu menu = new Menu(bar);

    MenuItem item = new MenuItem(menu, SWT.PUSH);
    item.setText("Cut");
    item.setAccelerator(SWT.MOD1 + 'X');
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        handleCutCopy();
        styledText.cut();
      }
    });
    item = new MenuItem(menu, SWT.PUSH);
    item.setText("Copy");
    item.setAccelerator(SWT.MOD1 + 'C');
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        handleCutCopy();
        styledText.copy();
      }
    });
    item = new MenuItem(menu, SWT.PUSH);
    item.setText("Paste");
    item.setAccelerator(SWT.MOD1 + 'V');
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        styledText.paste();
      }
    });
    new MenuItem(menu, SWT.SEPARATOR);
    item = new MenuItem(menu, SWT.PUSH);
    item.setText("Font");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        setFont();
      }
    });
    return menu;
  }

  Menu createFileMenu() {
    Menu bar = shell.getMenuBar();
    Menu menu = new Menu(bar);

    MenuItem item = new MenuItem(menu, SWT.PUSH);
    item.setText("Exit");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        shell.close();
      }
    });

    return menu;
  }

  /*
   * Set a style
   */
  void setStyle(Widget widget) {
    Point sel = styledText.getSelectionRange();
    if ((sel == null) || (sel.y == 0))
      return;
    StyleRange style;
    for (int i = sel.x; i < sel.x + sel.y; i++) {
      StyleRange range = styledText.getStyleRangeAtOffset(i);
      if (range != null) {
        style = (StyleRange) range.clone();
        style.start = i;
        style.length = 1;
      } else {
        style = new StyleRange(i, 1, null, null, SWT.NORMAL);
      }
      if (widget == boldButton) {
        style.fontStyle ^= SWT.BOLD;
      } else if (widget == italicButton) {
        style.fontStyle ^= SWT.ITALIC;
      } else if (widget == underlineButton) {
        style.underline = !style.underline;
      } else if (widget == strikeoutButton) {
        style.strikeout = !style.strikeout;
      }
      styledText.setStyleRange(style);
    }
    styledText.setSelectionRange(sel.x + sel.y, 0);
  }

  /*
   * Clear all style data for the selected styledText.
   */
  void clear() {
    Point sel = styledText.getSelectionRange();
    if ((sel != null) && (sel.y != 0)) {
      StyleRange style;
      style = new StyleRange(sel.x, sel.y, null, null, SWT.NORMAL);
      styledText.setStyleRange(style);
    }
    styledText.setSelectionRange(sel.x + sel.y, 0);
  }

  /*
   * Set the foreground color for the selected styledText.
   */
  void fgColor(Color fg) {
    Point sel = styledText.getSelectionRange();
    if ((sel == null) || (sel.y == 0))
      return;
    StyleRange style, range;
    for (int i = sel.x; i < sel.x + sel.y; i++) {
      range = styledText.getStyleRangeAtOffset(i);
      if (range != null) {
        style = (StyleRange) range.clone();
        style.start = i;
        style.length = 1;
        style.foreground = fg;
      } else {
        style = new StyleRange(i, 1, fg, null, SWT.NORMAL);
      }
      styledText.setStyleRange(style);
    }
    styledText.setSelectionRange(sel.x + sel.y, 0);
  }

  void createMenuBar() {
    Menu bar = new Menu(shell, SWT.BAR);
    shell.setMenuBar(bar);

    MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
    fileItem.setText("File");
    fileItem.setMenu(createFileMenu());

    MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
    editItem.setText("edit");
    editItem.setMenu(createEditMenu());
  }

  void createShell(Display display) {
    shell = new Shell(display);
    shell.setText("Window");
    if(images != null) {
      images.loadAll(display);
    }
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    shell.setLayout(layout);
    shell.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        if (font != null)
          font.dispose();
        if(images != null) {
          images.freeAll();
        }
        RED.dispose();
        GREEN.dispose();
        BLUE.dispose();
      }
    });
  }

  void createStyledText() {
    initializeColors();
    styledText = new StyledText(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
        | SWT.H_SCROLL);
    GridData spec = new GridData();
    spec.horizontalAlignment = GridData.FILL;
    spec.grabExcessHorizontalSpace = true;
    spec.verticalAlignment = GridData.FILL;
    spec.grabExcessVerticalSpace = true;
    styledText.setLayoutData(spec);
    styledText.addExtendedModifyListener(new ExtendedModifyListener() {
      public void modifyText(ExtendedModifyEvent e) {
        handleExtendedModify(e);
      }
    });
  }

  void createToolBar() {
    toolBar = new ToolBar(shell, SWT.NULL);
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        setStyle(event.widget);
      }
    };
    boldButton = this.createCheckToolItem("Bold", (images==null?null:images.Bold), listener);
    italicButton = this.createCheckToolItem("Italic", (images==null?null:images.Italic), listener);
    underlineButton = this.createCheckToolItem("Underline", (images==null?null:images.Underline), listener);
    strikeoutButton = this.createCheckToolItem("Strikeout", (images==null?null:images.Strikeout), listener);

    ToolItem item = new ToolItem(toolBar, SWT.SEPARATOR);

    item = this.createPushToolItem((images==null?null:images.Red), "Red", RED);
    item = this.createPushToolItem((images==null?null:images.Green), "Green", GREEN);
    item = this.createPushToolItem((images==null?null:images.Blue), "Blue", BLUE);

    item = new ToolItem(toolBar, SWT.SEPARATOR);

    item = new ToolItem(toolBar, SWT.PUSH);
    if(images != null) {
      item.setImage(images.Erase);
    }else{
      item.setText("Erase");
    }
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        clear();
      }
    });
  }
  
  private ToolItem createCheckToolItem(String text, Image image, SelectionListener listener) {
    final ToolItem output = new ToolItem(toolBar, SWT.CHECK);
    if(image != null) {
      output.setImage(image);
    }else{
      output.setText(text);
    }
    output.setToolTipText(text);
    output.addSelectionListener(listener);
    return output;
  }
  
  private ToolItem createPushToolItem(Image image, String colorName, Color color) {
    final ToolItem output = new ToolItem(toolBar, SWT.PUSH);
    if(image != null) {
      output.setImage(image);
    }else{
      output.setText(colorName);
    }
    output.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        fgColor(color);
      }
    });
    return output;
  }

  /*
   * Cache the style information for styledText that has been cut or copied.
   */
  void handleCutCopy() {
    // Save the cut/copied style info so that during paste we will maintain
    // the style information. Cut/copied styledText is put in the clipboard in
    // RTF format, but is not pasted in RTF format. The other way to
    // handle the pasting of styles would be to access the Clipboard
    // directly and
    // parse the RTF styledText.
    cachedStyles = new Vector();
    Point sel = styledText.getSelectionRange();
    int startX = sel.x;
    for (int i = sel.x; i <= sel.x + sel.y - 1; i++) {
      StyleRange style = styledText.getStyleRangeAtOffset(i);
      if (style != null) {
        style.start = style.start - startX;
        if (!cachedStyles.isEmpty()) {
          StyleRange lastStyle = (StyleRange) cachedStyles
              .lastElement();
          if (lastStyle.similarTo(style)
              && lastStyle.start + lastStyle.length == style.start) {
            lastStyle.length++;
          } else {
            cachedStyles.addElement(style);
          }
        } else {
          cachedStyles.addElement(style);
        }
      }
    }
  }

  void handleExtendedModify(ExtendedModifyEvent event) {
    if (event.length == 0)
      return;
    StyleRange style;
    if (event.length == 1
        || styledText.getTextRange(event.start, event.length).equals(styledText.getLineDelimiter())) {
      // Have the new styledText take on the style of the styledText to its right
      // (during
      // typing) if no style information is active.
      int caretOffset = styledText.getCaretOffset();
      style = null;
      if (caretOffset < styledText.getCharCount())
        style = styledText.getStyleRangeAtOffset(caretOffset);
      if (style != null) {
        style = (StyleRange) style.clone();
        style.start = event.start;
        style.length = event.length;
      } else {
        style = new StyleRange(event.start, event.length, null, null,
            SWT.NORMAL);
      }
      if (boldButton.getSelection())
        style.fontStyle |= SWT.BOLD;
      if (italicButton.getSelection())
        style.fontStyle |= SWT.ITALIC;
      style.underline = underlineButton.getSelection();
      style.strikeout = strikeoutButton.getSelection();
      if (!style.isUnstyled())
        styledText.setStyleRange(style);
    } else {
      // paste occurring, have styledText take on the styles it had when it was
      // cut/copied
      for (int i = 0; i < cachedStyles.size(); i++) {
        style = (StyleRange) cachedStyles.elementAt(i);
        StyleRange newStyle = (StyleRange) style.clone();
        newStyle.start = style.start + event.start;
        styledText.setStyleRange(newStyle);
      }
    }
  }

  public Shell init(Display display) {
    createShell(display);
    createMenuBar();
    createToolBar();
    createStyledText();
    return shell;
  }

  void setFont() {
    FontDialog fontDialog = new FontDialog(shell);
    fontDialog.setFontList((styledText.getFont()).getFontData());
    FontData fontData = fontDialog.open();
    if (fontData != null) {
      if (font != null) {
        font.dispose();
      }
      font = new Font(shell.getDisplay(), fontData);
      styledText.setFont(font);
    }
  }

  void initializeColors() {
    Display display = Display.getDefault();
    RED = new Color(display, new RGB(255, 0, 0));
    BLUE = new Color(display, new RGB(0, 0, 255));
    GREEN = new Color(display, new RGB(0, 255, 0));
  }

  public Shell getShell() {
    return shell;
  }

  public ToolBar getToolBar() {
    return toolBar;
  }

  public StyledText getStyledText() {
    return styledText;
  }

  public Font getFont() {
    return font;
  }

  public ToolItem getBoldButton() {
    return boldButton;
  }

  public ToolItem getItalicButton() {
    return italicButton;
  }

  public ToolItem getUnderlineButton() {
    return underlineButton;
  }

  public ToolItem getStrikeoutButton() {
    return strikeoutButton;
  }
}

/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/

class Images {

  // Bitmap Images
  public Image Bold;

  public Image Italic;

  public Image Underline;

  public Image Strikeout;

  public Image Red;

  public Image Green;

  public Image Blue;

  public Image Erase;

  Image[] AllBitmaps;

  Images() {
  }

  public void freeAll() {
    if(AllBitmaps != null) {
      for (int i = 0; i < AllBitmaps.length; i++) {
        AllBitmaps[i].dispose();
      }   
      AllBitmaps = null;
    }
  }

  Image createBitmapImage(Display display, String fileName) {
    InputStream sourceStream = Images.class.getResourceAsStream(fileName
        + ".bmp");
    InputStream maskStream = Images.class.getResourceAsStream(fileName
        + "_mask.bmp");
    ImageData source = new ImageData(sourceStream);
    ImageData mask = new ImageData(maskStream);
    Image result = new Image(display, source, mask);
    try {
      sourceStream.close();
      maskStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void loadAll(Display display) {
    // Bitmap Images
    Bold = createBitmapImage(display, "bold");
    Italic = createBitmapImage(display, "italic");
    Underline = createBitmapImage(display, "underline");
    Strikeout = createBitmapImage(display, "strikeout");
    Red = createBitmapImage(display, "red");
    Green = createBitmapImage(display, "green");
    Blue = createBitmapImage(display, "blue");
    Erase = createBitmapImage(display, "erase");

    AllBitmaps = new Image[] { Bold, Italic, Underline, Strikeout, Red,
        Green, Blue, Erase, };
  }
}