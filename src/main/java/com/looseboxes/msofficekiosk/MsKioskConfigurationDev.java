package com.looseboxes.msofficekiosk;

import com.bc.config.Config;
import com.bc.elmi.pu.entities.Unit;
import com.looseboxes.msofficekiosk.config.ConfigFactory;
import com.looseboxes.msofficekiosk.config.ConfigService;
import com.looseboxes.msofficekiosk.functions.StudentGroupListSupplier;
import com.looseboxes.msofficekiosk.popups.MultiInputDialog;
import com.looseboxes.msofficekiosk.security.LoginManager;
import com.looseboxes.msofficekiosk.security.jaas.LoginManagerJaas;
import com.looseboxes.msofficekiosk.test.TestDocKey;
import com.looseboxes.msofficekiosk.ui.AppUiContext;
import com.looseboxes.msofficekiosk.ui.MessageDialog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.security.auth.login.LoginException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author USER
 */
public class MsKioskConfigurationDev extends MsKioskConfiguration{

    private static String studentGroup;
    
    public static class LoginManagerJaasImpl extends LoginManagerJaas{
    
        private final ApplicationContext spring;
        
        public LoginManagerJaasImpl(ApplicationContext spring) throws LoginException {
            super(spring.getBean(MessageDialog.class), 
                    spring.getBean(AppUiContext.class).getMainWindowOptional().orElse(null));
            this.spring = Objects.requireNonNull(spring);
        }

        @Override
        public boolean promptUserLogin(int attempts) {
            final boolean loggedIn = super.promptUserLogin(attempts);
            if(loggedIn) {
                final List<Unit> unitList = spring.getBean(StudentGroupListSupplier.class).get();
                final Map inputMap = new HashMap();
                if(unitList == null || unitList.isEmpty()) {
                    inputMap.put(TestDocKey.STUDENT_GROUP, "");    
                }else{
    //                        final List<Selection> selectionValues = SelectionValues.from(unitList).getSelectionValues("");
                    inputMap.put(TestDocKey.STUDENT_GROUP, unitList.stream()
                        .map((u) -> u.getUnitname()).toArray());
                }
                final Config uiConfig = spring.getBean(ConfigFactory.class).getConfig(ConfigService.APP_UI);
                final Map outputMap = new MultiInputDialog(uiConfig).apply(inputMap, "Select/Enter Group (Could be your Syndicate/Department/Unit)");
                
                studentGroup = (String)outputMap.getOrDefault(TestDocKey.STUDENT_GROUP, null);
            }
            return loggedIn;
        }

        @Override
        public boolean logout() {
            final boolean loggedOut = super.logout(); 
            if(loggedOut) {
                studentGroup = null;
            }
            return loggedOut;
        }
        
        @Override
        public Optional<String> getUserGroup() {
            return Optional.ofNullable(studentGroup);
        }
    }
    
    @Override
    @Bean public StudentGroupListSupplier studentGroupListSupplier() {
            final Unit dlw = new Unit((short)1);dlw.setUnitname("DLW, SC - Syndicate 1");
            final Unit dmw = new Unit((short)1);dmw.setUnitname("DMW, SC - Syndicate 1");
            final Unit daw = new Unit((short)1);daw.setUnitname("DAW, SC - Syndicate 1");
            final List<Unit> unitList = Arrays.asList(dlw, dmw, daw);
        return () -> unitList;
    }

    @Override
    @Bean public LoginManager loginManager(ApplicationContext spring) {   
        try{
            return new LoginManagerJaasImpl(spring);
        }catch(LoginException e) {
            throw new RuntimeException(e);
        }
    }
}