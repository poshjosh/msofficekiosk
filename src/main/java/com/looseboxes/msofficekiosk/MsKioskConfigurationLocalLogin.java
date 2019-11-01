package com.looseboxes.msofficekiosk;

import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.security.jaas.LoginManagerJaas;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.security.auth.login.LoginException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author USER
 */
public class MsKioskConfigurationLocalLogin extends MsKioskConfiguration{

    @Override
    @Bean public LoginManager loginManager(ApplicationContext spring) {   
        final MessageDialog messageDialog = spring.getBean(MessageDialog.class);
        final AppUiContext uiCtx = spring.getBean(AppUiContext.class);
        try{
            return new LoginManagerJaas(messageDialog, uiCtx.getMainWindowOptional().orElse(null)){
                @Override
                public Optional<String> getUserGroup() {
                    return Optional.of("DAW");
                }
                @Override
                public List<String> getUserRoles() {
                    return Collections.singletonList("STUDENT-SC");
                }
            };
        }catch(LoginException e) {
            throw new RuntimeException(e);
        }
    }
}