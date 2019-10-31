package com.looseboxes.msofficekiosk.functions.ui;

import com.looseboxes.msofficekiosk.functions.GetUserMessage;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */

public class DisplayException {

    private transient static final Logger LOG = LoggerFactory.getLogger(DisplayException.class);

    public void accept(Throwable t) {
        
        final String msg = "Encountered an unexpected problem while starting the application";

        LOG.warn(msg, t);

        final String userMsg = new GetUserMessage().apply(t, null);

        final Object obj;
        if(userMsg != null) {
            obj = userMsg;
        }else{

            final StringWriter w = new StringWriter();
            w.write(msg);
            w.write('\n');
            try(final PrintWriter pw = new PrintWriter(w)) {
                t.printStackTrace(pw);
            }

            final Dimension dim = new Dimension(500, 350);
            final JTextArea ta = new JTextArea(w.getBuffer().toString());
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setSize(dim);
            ta.setPreferredSize(dim);
            final JScrollPane pane = new JScrollPane(ta);
            pane.setSize(dim);
            pane.setPreferredSize(dim);
            obj = pane;
        }

        JOptionPane.showMessageDialog(null, obj, 
                "Startup Error", JOptionPane.WARNING_MESSAGE);
    }
}
