package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.StylusListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FontMetrics;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.OptionsChangeListener;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenuFactory;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.SLKeyLayout;

class CalendarViewController
   implements TrackwheelListener,
   StylusListener,
   CalendarView,
   DateTimeMonitor$DateTimeMonitorCallback,
   CollectionMonitor$CollectionMonitorCallback,
   KeyListener,
   OptionsChangeListener,
   GlobalEventListener {
   private CalendarActions _calActions;
   private CalendarViewVerbManager _verbManager;
   private CalendarApp _calendarUIApplication;
   private PreListenerScreen _screen = new PreListenerScreen(this);
   private CalendarViewController$CalendarViewHeader _header;
   private SeparatorField _headerSep = new SeparatorField();
   private Calendar _cal = Calendar.getInstance();
   private CalendarExtensions _calEx = (CalendarExtensions)this._cal;
   private DateTimeMonitor _dateMon;
   private CollectionMonitor[] _colMons;
   private int _previousView = -1;
   private boolean _returnToPreviousView = false;
   protected boolean _disableSimpleNav;
   private ContextObject _contextObject = new ContextObject();
   private StringBuffer _tempBuffer = new StringBuffer();
   private CalendarViewController$LoadCalendarViewThread _threadedViewLoader = new CalendarViewController$LoadCalendarViewThread(this);
   private FontMetrics _fontMetrics = new FontMetrics();
   private int _dismissRequests = 0;
   private int _dialogRequestCount = 0;
   protected static final byte PASSED = 0;
   protected static final byte SELECTED = 1;
   protected static final byte CACHED = 2;
   protected static final byte NEXT_DAY = 3;
   protected static final byte PREV_DAY = 4;
   private static final long ORGANIZING_CALENDAR_DELAY = 200L;

   protected void setVerbManager(CalendarViewVerbManager verbManager) {
      this._verbManager = verbManager;
   }

   @Override
   public void collectionChanged() {
      this.loadViewContents(0, (byte)1, null, (byte)2, false, false, (byte)1);
   }

   public void selectedEventChanged(Object _1) {
      throw null;
   }

   public int moveFocus(Field _1, int _2) {
      throw null;
   }

   protected void initializeAdditionalFields() {
      throw null;
   }

   protected void uninitializeAdditionalFields() {
      throw null;
   }

   protected void addAdditionalFields(Screen _1) {
      throw null;
   }

   protected long getSelectedStartTime() {
      throw null;
   }

   protected long getSelectedEndTime() {
      throw null;
   }

   protected Object getSelectedObject() {
      throw null;
   }

   protected boolean loadViewContents(
      long time, byte timeToUseFlag, Object object, byte objectToUseFlag, boolean updateSelectedDate, boolean reposition, byte loadType
   ) {
      return this.loadViewContents(time, timeToUseFlag, object, objectToUseFlag, updateSelectedDate, reposition, false, loadType);
   }

   protected boolean loadViewContents(
      long time,
      byte timeToUseFlag,
      Object object,
      byte objectToUseFlag,
      boolean updateSelectedDate,
      boolean reposition,
      boolean preserveSelectedTime,
      byte loadType
   ) {
      if (this.getApplication() != null) {
         this._threadedViewLoader.addLoadRequest(time, timeToUseFlag, object, objectToUseFlag, updateSelectedDate, reposition, preserveSelectedTime, loadType);
      }

      return true;
   }

   Runnable loadViewContentsNow(
      long time,
      byte timeToUseFlag,
      Object object,
      byte objectToUseFlag,
      boolean updateSelectedDate,
      boolean reposition,
      boolean preserveSelectedTime,
      byte loadType
   ) {
      if (timeToUseFlag == 1) {
         time = this.getSelectedStartTime();
      } else if (timeToUseFlag == 2) {
         time = CalOptionCache.getTimeWithFocus();
      }

      if (objectToUseFlag == 1) {
         object = this.getSelectedObject();
      } else if (objectToUseFlag == 2) {
         object = CalOptionCache.getObjectWithFocus();
      }

      return this.loadViewContentsNow(time, object, updateSelectedDate, reposition, preserveSelectedTime, loadType);
   }

   protected Runnable loadViewContentsNow(long _1, Object _3, boolean _4, boolean _5, boolean _6, byte _7) {
      throw null;
   }

   protected void reloadView() {
      CalendarExtensions calEx = (CalendarExtensions)this.getScratchCalendar();
      calEx.setTimeLong(this.getSelectedStartTime());
      this.loadViewContents(calEx.getTimeLong(), (byte)0, null, (byte)0, true, true, (byte)0);
   }

   public synchronized void dismissOrganizingCalendarDialog() {
      this._dismissRequests++;
      int processId = CalendarProxy.getCalendarUIAppProcessId();
      if (processId != 0) {
         RIMGlobalMessagePoster.postGlobalEvent(processId, -5520237139346668942L, 0, this._dismissRequests, this, null);
         this._dismissRequests = 0;
         this._dialogRequestCount = 0;
      }
   }

   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.keyChar('\n', 0, 0);
            return true;
         default:
            return false;
      }
   }

   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      return false;
   }

   protected Calendar getScratchCalendar() {
      return this._cal;
   }

   protected Application getApplication() {
      return this._calendarUIApplication;
   }

   protected void setUpFields() {
      this._screen.add(this._header);
      if (!Graphics.isColor()) {
         this._screen.add(this._headerSep);
      }

      this.addAdditionalFields(this._screen);
   }

   protected void populateMenu(SystemEnabledMenu menu) {
      VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
      this._verbManager.getVerbs(verbToMenu, this.getSelectedStartTime(), this.getSelectedObject());
      verbToMenu.getMenu(menu, null, null);
      verbToMenu.clear();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   Object invokeVerb(Verb verb, Object parameter) {
      Object result = null;
      long lastTimeViewed = this.getSelectedStartTime();
      CalOptionCache.setTimeWithFocus(lastTimeViewed);
      Object selected = this.getSelectedObject();
      CalOptionCache.setSuggestedUserDuration(this.getSelectedEndTime() - lastTimeViewed);
      CalOptionCache.setObjectWithFocus(selected);
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         if (selected != null) {
            this._contextObject.put(3696141428889703675L, selected);
            this._contextObject.put(-8458526561932482160L, selected);
         }

         CalOptionCache.setObjectWithFocus(result = VerbToMenuFactory.createInstance().invoke(verb, this._contextObject));
         var10 = false;
      } finally {
         if (var10) {
            this._contextObject.remove(3696141428889703675L);
            this._contextObject.remove(-8458526561932482160L);
         }
      }

      this._contextObject.remove(3696141428889703675L);
      this._contextObject.remove(-8458526561932482160L);
      long newTimeViewed = CalOptionCache.getTimeWithFocus();
      if (newTimeViewed != lastTimeViewed || this._contextObject.getPrivateFlag(-3866311304884942232L, 1)) {
         this.loadViewContents(newTimeViewed, (byte)0, null, (byte)0, true, verb instanceof GotoDateVerb, (byte)0);
      }

      this._contextObject.clearPrivateFlag(-3866311304884942232L, 1);
      return result;
   }

   protected void updateCurrentTime() {
      this._calEx.setTimeLong(System.currentTimeMillis());
      this._header.setCurrentTime(this._cal);
   }

   protected void updateSelectedDate() {
      this.updateSelectedDate(this.getSelectedStartTime());
   }

   protected void updateSelectedDate(long selectedDate) {
      this._header.setSelectedDate(selectedDate);
   }

   protected void setDelayedHeaderRendering(boolean val) {
      this._header.setDelayedRendering(val);
   }

   protected void setNavigationFieldForHeader(Field navField) {
      this._header.setNavField(navField);
   }

   protected boolean passKeyToSelectedObject(char key) {
      RIMModel calElement = (RIMModel)this.getSelectedObject();
      if (!(calElement instanceof HotKeyProvider)) {
         return false;
      }

      HotKeyProvider calHKProvider = (HotKeyProvider)calElement;
      Object o2 = calHKProvider.invokeHotkey(null, key);
      return o2 != null;
   }

   protected char mapKey(char key, int status) {
      return key;
   }

   public synchronized void showOrganizingCalendarDialog() {
      int processId = CalendarProxy.getCalendarUIAppProcessId();
      if (processId != 0) {
         this._dialogRequestCount++;
         if (this._dialogRequestCount == 1) {
            RIMGlobalMessagePoster.postGlobalEvent(processId, -5520237139346668942L, 1, 0, null, null);
         }
      }
   }

   protected boolean onHotKeyAction(Verb action, long lastTimeViewed) {
      if (action != null) {
         ContextObject contextObject = new ContextObject();
         Object selected = this.getSelectedObject();
         if (selected != null) {
            contextObject.put(3696141428889703675L, selected);
            contextObject.put(-8458526561932482160L, selected);
         }

         if (this._verbManager instanceof MonthVerbManager) {
            contextObject.setFlag(87);
         }

         Object o = action.invoke(contextObject);
         CalOptionCache.setObjectWithFocus(o);
         long newTimeToView = CalOptionCache.getTimeWithFocus();
         if (newTimeToView != lastTimeViewed && lastTimeViewed != 0) {
            this.loadViewContents(newTimeToView, (byte)0, null, (byte)0, true, action instanceof GotoDateVerb, (byte)0);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean onBottom(int eventTime, long lastTimeViewed, Screen screen) {
      return false;
   }

   protected boolean onTop(int eventTime, long lastTimeViewed, Screen screen) {
      return false;
   }

   protected boolean onLeft(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.dispatchTrackwheelEvent(519, -1, 1, eventTime);
   }

   protected boolean onRight(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.dispatchTrackwheelEvent(519, 1, 1, eventTime);
   }

   protected boolean onUp(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.dispatchTrackwheelEvent(519, -1, 0, eventTime);
   }

   public void selectedDateChanged(long _1) {
      throw null;
   }

   protected boolean onDown(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.dispatchTrackwheelEvent(519, 1, 0, eventTime);
   }

   protected boolean onPageUp(int eventTime, long lastTimeViewed, Screen screen) {
      return this.onHotKeyAction(this._verbManager.findFromChar(CalendarApp._rb.getString(341).charAt(0), lastTimeViewed), lastTimeViewed);
   }

   protected boolean onPageDown(int eventTime, long lastTimeViewed, Screen screen) {
      return this.onHotKeyAction(this._verbManager.findFromChar(CalendarApp._rb.getString(340).charAt(0), lastTimeViewed), lastTimeViewed);
   }

   protected boolean headerExtentContains(int x, int y) {
      return this._header.getExtent().contains(x, y);
   }

   protected void switchToNextView() {
      long lastTimeViewed = this.getSelectedStartTime();
      CalOptionCache.setTimeWithFocus(lastTimeViewed);
      CalOptionCache.setSuggestedUserDuration(this.getSelectedEndTime() - lastTimeViewed);
      this._calActions.switchViews(0, true);
   }

   protected int getHeaderHeight() {
      return this._header.getHeight();
   }

   @Override
   public boolean trackwheelRoll(int amount, int status, int time) {
      long lastTimeViewed = this.getSelectedStartTime();
      CalOptionCache.setTimeWithFocus(lastTimeViewed);
      return false;
   }

   @Override
   public boolean trackwheelUnclick(int status, int time) {
      return false;
   }

   @Override
   public boolean stylusDown(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean stylusDrag(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean stylusUp(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean stylusTap(int x, int y, int status, int time) {
      if (this.headerExtentContains(x, y)) {
         this.switchToNextView();
         return true;
      } else {
         return this.keyChar('\n', status, time);
      }
   }

   @Override
   public boolean stylusDoubleTap(int x, int y, int status, int time) {
      return this.keyChar('\n', status, time);
   }

   @Override
   public boolean stylusTapHold(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      key = UiInternal.map(Keypad.getLayout().getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
      if (key == 27) {
         if (this._returnToPreviousView && this._previousView > 0) {
            this._calActions.switchViews(this._previousView, false);
            return true;
         } else {
            this._calActions.close();
            return true;
         }
      } else {
         long lastTimeViewed = this.getSelectedStartTime();
         CalOptionCache.setTimeWithFocus(lastTimeViewed);
         CalOptionCache.setSuggestedUserDuration(this.getSelectedEndTime() - lastTimeViewed);
         CalOptionCache.setObjectWithFocus(this.getSelectedObject());
         Verb action = null;
         if (key == '\n') {
            VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
            this._verbManager.getVerbs(verbToMenu, lastTimeViewed, this.getSelectedObject());
            action = verbToMenu.getDefaultVerb();
            verbToMenu.clear();
         } else {
            key = this.mapKey(key, status);
            action = this._verbManager.findFromChar(key, lastTimeViewed);
         }

         if (action == null) {
            CalOptionCache.setObjectWithFocus(null);
            return this.passKeyToSelectedObject(key);
         } else {
            return this.onHotKeyAction(action, lastTimeViewed);
         }
      }
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      return false;
   }

   @Override
   public void hourChanged() {
      this.dateChanged();
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      if (InternalServices.isReducedFormFactor() && (Keypad.status(keycode) & 1) == 0) {
         Verb action = null;
         long lastTimeViewed = this.getSelectedStartTime();
         CalOptionCache.setTimeWithFocus(lastTimeViewed);
         CalOptionCache.setSuggestedUserDuration(this.getSelectedEndTime() - lastTimeViewed);
         CalOptionCache.setObjectWithFocus(this.getSelectedObject());
         switch (Keypad.key(keycode)) {
            case 66:
               return this.onDown(time, lastTimeViewed, this._screen);
            case 67:
               return this.onBottom(time, lastTimeViewed, this._screen);
            case 68:
               return this.onLeft(time, lastTimeViewed, this._screen);
            case 69:
               return this.onTop(time, lastTimeViewed, this._screen);
            case 71:
               VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
               this._verbManager.getVerbs(verbToMenu, lastTimeViewed, this.getSelectedObject());
               action = verbToMenu.getDefaultVerb();
               verbToMenu.clear();
               return this.onHotKeyAction(action, lastTimeViewed);
            case 74:
               return this.onRight(time, lastTimeViewed, this._screen);
            case 77:
               return this.onPageDown(time, lastTimeViewed, this._screen);
            case 81:
               return this.onHotKeyAction(this._verbManager.findFromChar(CalendarApp._rb.getString(349).charAt(0), lastTimeViewed), lastTimeViewed);
            case 84:
               return this.onUp(time, lastTimeViewed, this._screen);
            case 85:
               return this.onPageUp(time, lastTimeViewed, this._screen);
         }
      }

      return false;
   }

   @Override
   public void minuteChanged() {
      this.updateCurrentTime();
   }

   @Override
   public void dateChanged() {
      this.updateCurrentTime();
      long selectedDate = this._calEx.getTimeLong();
      this.updateSelectedDate(selectedDate);
      this.loadViewContents(selectedDate, (byte)0, null, (byte)0, true, true, (byte)0);
   }

   @Override
   public void dateFormatChanged() {
      TimeStringCache.clearCache();
      this.loadViewContents(0, (byte)1, null, (byte)1, true, false, (byte)1);
      this.updateCurrentTime();
   }

   @Override
   public void setTimeWithFocus(long timeWithFocus) {
      this.loadViewContents(timeWithFocus, (byte)0, null, (byte)0, true, false, (byte)0);
   }

   @Override
   public long getTimeWithFocus() {
      return this.getSelectedStartTime();
   }

   @Override
   public void remove() {
      CalendarOptions.getOptions().removeOptionsChangeListener(this);
      this._dateMon.stopMonitor();

      for (int i = 0; i < this._colMons.length; i++) {
         this._colMons[i].stopMonitor();
      }

      this._calendarUIApplication.popScreen(this._screen);
   }

   @Override
   public void refresh() {
      this.loadView(false);
   }

   @Override
   public void display(int previousView, boolean returnToPreviousView) {
      this._previousView = previousView;
      this._returnToPreviousView = returnToPreviousView;
      this._verbManager.setPreviousView(previousView);
      this._calendarUIApplication.pushScreen(this._screen);
      this.loadView(false);
      CalendarOptions.getOptions().addOptionsChangeListener(this);
      this._dateMon.startMonitor();

      for (int i = 0; i < this._colMons.length; i++) {
         this._colMons[i].startMonitor();
      }
   }

   @Override
   public void uninitialize() {
      this.uninitializeAdditionalFields();
   }

   @Override
   public boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public void initialize() {
      this.initializeAdditionalFields();
      this.setUpFields();
      this._dateMon = new DateTimeMonitor(this, this._calendarUIApplication, this._cal);
      CalDB[] calDBs = CalendarServiceManager.getInstance().getCalendarDatabases(true);
      this._colMons = new CollectionMonitor[calDBs.length];

      for (int i = 0; i < calDBs.length; i++) {
         this._colMons[i] = new CollectionMonitor(this, this._calendarUIApplication, calDBs[i]);
      }

      this._screen.addKeyListener(this);
      this._screen.addStylusListener(this);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L) {
         this.reloadView();
      } else if (guid == -4802534882197295853L) {
         this._calActions.close();
      } else {
         if (guid == -7131874474196788121L) {
            if (CalOptionCache.getCurrentOpenViewer() == null && PersistentContent.isEncryptionEnabled()) {
               this._calActions.close();
               return;
            }
         } else if (guid == 5483692278053761660L) {
            this.loadView(true);
         }
      }
   }

   @Override
   public void optionsChanged(int _1) {
      throw null;
   }

   CalendarViewController(
      CalendarApp calendarUIApplication, CalendarActions calActions, CalendarViewVerbManager verbManager, boolean disableSimpleNav, boolean showWeekNumber
   ) {
      this._threadedViewLoader.start();
      this._calendarUIApplication = calendarUIApplication;
      this._calActions = calActions;
      this._verbManager = verbManager;
      this._calendarUIApplication.addGlobalEventListener(this);
      this._disableSimpleNav = disableSimpleNav;
      this._header = new CalendarViewController$CalendarViewHeader(this, showWeekNumber);
      this._headerSep.setId("title");
   }

   private void loadView(boolean keepCurrentDate) {
      long time = 0;
      byte timeLoadParam = 2;
      byte objectLoadParam = 0;
      Object currentObject = CalOptionCache.getObjectWithFocus();
      if (keepCurrentDate && currentObject == null) {
         time = CalOptionCache.getTimeWithFocus();
         timeLoadParam = 0;
      }

      this.loadViewContents(time, timeLoadParam, currentObject, objectLoadParam, true, currentObject == null, (byte)0);
      if (!keepCurrentDate) {
         this.updateCurrentTime();
      }
   }
}
