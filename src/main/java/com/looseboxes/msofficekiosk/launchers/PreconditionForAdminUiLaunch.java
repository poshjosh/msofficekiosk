package com.looseboxes.msofficekiosk.launchers;

import com.looseboxes.msofficekiosk.MsKioskSetup;
import com.looseboxes.msofficekiosk.validators.Precondition;

/**
 * @author USER
 */
public class PreconditionForAdminUiLaunch implements Precondition<MsKioskSetup>{
    @Override
    public boolean test(MsKioskSetup t) {
        return true;
    }
}
