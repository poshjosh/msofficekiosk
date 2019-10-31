/*
 * Copyright 2019 NUROX Ltd.
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

package com.looseboxes.msofficekiosk.test;

import com.bc.elmi.pu.entities.Test;
import com.bc.elmi.pu.entities.Unit;
import com.looseboxes.msofficekiosk.Cache;
import com.looseboxes.msofficekiosk.net.Rest;
import com.looseboxes.msofficekiosk.security.LoginManager;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.validation.ValidationException;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 29, 2019 7:40:04 PM
 */
public class TestsImpl implements Tests {

    private static final Logger LOG = Logger.getLogger(TestsImpl.class.getName());
    
    private final Cache cache;
    
    private Test userCreatedTest;
    
    private Test activatedTest;
    
    private static class StarttimeComparator implements Comparator<Test>{
        @Override
        public int compare(Test lhs, Test rhs) {
            return lhs.getStarttime().compareTo(rhs.getStarttime());
        }
    }
    
    public TestsImpl(Cache cache) {
        this.cache = Objects.requireNonNull(cache);
    }
    
    private List<Test> load() {
        final List<Test> loaded = load(cache);
        LOG.log(Level.FINE, "Loaded: {0} Tests", (loaded==null?null:loaded.size()));
        return loaded == null || loaded.isEmpty() ?
                new ArrayList<>() : new ArrayList<>(loaded);
    }
    
    private List<Test> load(Cache cache) {
        try{
            return cache.getListFromJson(Rest.RESULTNAME_TESTS, Test.class, Collections.EMPTY_LIST);
        }catch(IOException | ParseException e) {
            LOG.log(Level.WARNING, null, e);
            return Collections.EMPTY_LIST;
        }
    }
    
    @Override
    public void delete(Test toDelete) {
        
        if(this.userCreatedTest != null && toDelete.equals(this.userCreatedTest)) {
            LOG.log(Level.FINE, "Clearing: {0}", this.userCreatedTest);
            this.userCreatedTest = null;
        }
        
        if(this.activatedTest != null && toDelete.equals(this.activatedTest)) {
            LOG.log(Level.FINE, "Clearing: {0}", this.activatedTest);
            this.activatedTest = null;
        }
        
        final List<Test> loaded = load();
        final List<Test> matching = loaded.stream().filter((test) -> toDelete.equals(test)).collect(Collectors.toList());
        LOG.fine(() -> "Found matching tests: " + matching);

        if(matching.size() > 1) {
            LOG.warning(() -> "Found " + matching.size() + " Tests matching: " + toDelete + 
                    "\nMatching: " + matching);
        }

        if( ! matching.isEmpty()){
            
            if(loaded.removeAll(matching)) {
            
                try{
                    cache.putAsJson(Rest.RESULTNAME_TESTS, loaded);
                }catch(IOException e){
                    LOG.log(Level.WARNING, null, e);
                }
            }
        }
    }
    
    public boolean isTestUnit(Test test, Object idOrUnitname) {
        final Predicate<Unit> unitOrChildHasName = new UnitOrChildHasIdOrName(idOrUnitname);
        final boolean flag = test.getUnitList().stream()
                        .filter(unitOrChildHasName)
                        .findFirst().isPresent();
        return flag;
    }
    
    @Override
    public Test activateTest(LoginManager loginManager, int period, TimeUnit timeUnit) {
        
        final Test test = getCurrentOrPendingTest(period, timeUnit)
                .orElseThrow(() -> new ValidationException("No current or pending Test"));
        
        activateTest(test, loginManager);
        
        return test;
    }

    @Override
    public void activateTest(Test test, LoginManager loginManager) {
        
        final String username = loginManager.validateUsername();
        
        final Object idOrUnitname = loginManager.validateUsergroup();
        
        Objects.requireNonNull(test.getTestname(), "Test was configured, but without a name!");
        
        if(test.getUnitList() == null || test.getUnitList().isEmpty()) {
            throw new ValidationException("Test was configured, but was not assigned to units/groups who will take the Test");
        }
        
        if( ! this.isTestUnit(test, idOrUnitname)) {
            throw new ValidationException("Current logged in user: " + username + 
                        ", belongs to unit: " + idOrUnitname + ",\nwhich is not among units scheduled to take scheduled test: " + 
                        test.getTestname() + "\n" + test.getUnitList().stream()
                                .map((unit) -> unit.getUnitname())
                                .collect(Collectors.joining(", ")));
        }
        
        activatedTest = test;
        
        LOG.log(Level.FINE, "Activated: {0}", test);
    }

    @Override
    public List<Test> get(){
        return Collections.unmodifiableList(load());
    }
    
    @Override
    public Optional<Test> get(Object id){
        final List<Test> loaded = load();
        return loaded.stream().filter((t) -> t.getTestid().toString().equals(id.toString())).findFirst();
    }

    @Override
    public Optional<Test> getCurrentOrPendingTest(int period, TimeUnit timeUnit) {
        final List<Test> loaded = load();
        final Test output = getCurrentTest(loaded).orElse(getPendingTest(loaded, period, timeUnit).orElse(null));
        return Optional.ofNullable(output);
    }
    
    @Override
    public Optional<Test> getPendingTest(int period, TimeUnit timeUnit) {
        final List<Test> loaded = load();
        return getPendingTest(loaded, period, timeUnit);
    }
    
    public Optional<Test> getPendingTest(List<Test> loaded, int period, TimeUnit timeUnit) {
        if(userCreatedTest != null) {
            return Optional.ofNullable(userCreatedTest);
        }
        final List<Test> futureTests = new ArrayList<>(this.getFutureTestsBefore(loaded, period, timeUnit));
        Collections.sort(futureTests, this.getComparator());
        final Test pendingTest = futureTests.isEmpty() ? null : futureTests.get(0);
        LOG.log(Level.FINE, "Pending: {0}", pendingTest);
        return Optional.ofNullable(pendingTest);
    }

    @Override
    public Optional<Test> getCurrentTest() {
        final List<Test> loaded = load();
        return getCurrentTest(loaded);
    }
    
    public Optional<Test> getCurrentTest(List<Test> loaded) {
        final Date now = new Date();
        final Predicate<Test> predicate = (test) -> {
            final Date endtime = new Date(test.getStarttime().getTime() + TimeUnit.MINUTES.toMillis(test.getDurationinminutes()));
            return test.getStarttime().after(now) && now.before(endtime);
        };
        final List<Test> currentTests = new ArrayList<>(loaded.stream().filter(predicate).collect(Collectors.toList()));
        Collections.sort(currentTests, getComparator());
        final Test currentTest = currentTests.isEmpty() ? null : currentTests.get(0);
        LOG.log(Level.FINE, "Current: {0}", currentTest);
        return Optional.ofNullable(currentTest);
    }

    @Override
    public Comparator<Test> getComparator() {
        return new StarttimeComparator();
    }

    public List<Test> getFutureTestsBefore(int period, TimeUnit timeUnit) {
        final List<Test> loaded = load();
        return getFutureTestsBefore(loaded, period, timeUnit);
    }

    public List<Test> getFutureTestsBefore(List<Test> tests, int period, TimeUnit timeUnit) {
        LOG.fine(() -> "Period: " + period + " " + timeUnit + "\nTests: " + tests);
        final Date now = new Date(System.currentTimeMillis() - ADVANCE_TIME_MILLIS);
        final Date target = new Date(System.currentTimeMillis() + timeUnit.toMillis(period));
        LOG.fine(() -> "Start: " + now + ", end: " + target);
        final Predicate<Date> futureDateIsBefore = (date) -> {
            final boolean ret = date.after(now) && date.before(target);
            LOG.fine(() -> "Passed: " + ret + ", starttime: " + date);
            return ret;
        };
        return tests.stream().filter((test) -> futureDateIsBefore.test(test.getStarttime())).collect(Collectors.toList());
    }

    @Override
    public Optional<Test> getActivatedTest() {
        return Optional.ofNullable(activatedTest);
    }

    @Override
    public Test getUserCreatedTest() {
        return userCreatedTest;
    }

    @Override
    public void setUserCreatedTest(Test userCreatedTest) {
        this.userCreatedTest = userCreatedTest;
    }
}
