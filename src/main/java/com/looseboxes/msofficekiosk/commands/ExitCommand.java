package com.looseboxes.msofficekiosk.commands;

import com.looseboxes.msofficekiosk.AppContext;
import com.looseboxes.msofficekiosk.MsKioskSetup;
import com.looseboxes.msofficekiosk.config.ConfigNamesInternal;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.ui.UI;
import com.looseboxes.msofficekiosk.ui.UiImpl;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * @author USER
 */
public class ExitCommand implements Callable{

    private static final Logger LOG = Logger.getLogger(ExitCommand.class.getName());
    
    private final boolean exitSystemOnExit;
    
    private final Predicate test;
    
    private final AppContext app;
    
    private final UI ui;
    
    public ExitCommand(AppContext app) {
        this((obj) -> true, app, new UiImpl());
    }
    
    public ExitCommand(Predicate test, AppContext app, UI ui) {
        this.test = Objects.requireNonNull(test);
        this.app = Objects.requireNonNull(app);
        this.ui = Objects.requireNonNull(ui);
        this.exitSystemOnExit = app.getConfig(ConfigService.APP_INTERNAL)
                .getBoolean(ConfigNamesInternal.EXIT_SYSTEM_ON_UI_EXIT);
        LOG.fine(() -> ConfigNamesInternal.EXIT_SYSTEM_ON_UI_EXIT + " = " + exitSystemOnExit);
    }

    @Override
    public Object call() throws Exception {

        if(test.test(this)) {
            try{
                if(!app.isShutdown()) {
                    app.shutdown();
                }
                if(!ui.isDisposed()) {
                    ui.dispose();
                }
            }finally{
                if(exitSystemOnExit) {
                    System.exit(0);
                }
            }
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }
}
