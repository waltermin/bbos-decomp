package net.rim.tid.itie;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.device.internal.ui.UiSettings;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.FocusEvent;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.awt.event.NavigationEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.CurrencyKeyDialog;
import net.rim.tid.im.layout.SLKeyLayout;

public final class EventHandler {
   private KeyEvent _keyEvent = new KeyEvent(null, 0, 0, 0, 0, '\u0000', Event.KEY_EVENT_MASK);
   private FocusEvent _focusEvent = new FocusEvent(null, 1004, Event.FOCUS_EVENT_MASK, 0);
   private NavigationEvent _navigationEvent = new NavigationEvent(null, 0);
   private int _focusHistoryStart = -1;
   private int _focusHistoryIndex = -1;
   private int _focusHistoryLogCount = 0;
   private int[] _focusHistoryAppId = new int[3];
   private String[] _focusHistoryComponent = new String[3];
   private boolean[] _focusHistoryEvent = new boolean[3];
   private int _focusHistoryLastAppId = -1;
   public static final int EH_CLOSE_CURRENCY_KEY_DIALOG;
   public static final int EVENT_COMMITTED_MASK;
   private static final int MAX_FOCUS_HISTORY_COUNT;

   EventHandler() {
   }

   private final void initCurrencySign() {
      ResourceBundle bundle = ResourceBundle.getBundle(-4248492586227566823L, "net.rim.device.internal.resource.Keypad");
      String currencyChoices = bundle.getString(104);
      String message = bundle.getString(103);
      int choiceCount = currencyChoices.length();
      String[] choices = new String[choiceCount];

      for (int i = 0; i < choiceCount; i++) {
         choices[i] = currencyChoices.substring(i, i + 1);
      }

      int ret = CurrencyKeyDialog.ask(message, choices, 0);
      if (ret != -1) {
         UiSettings.setCurrencyKey(currencyChoices.charAt(ret));
      }
   }

   public final int processKeyEvent(int ID, int keyCode, char keyChar, int modifiers, int time, boolean input) {
      if (Keypad.key(keyCode) == 36
         && Keypad.hasCurrencyKey()
         && UiSettings.getCurrencyKey() == 0
         && !ApplicationManager.getApplicationManager().isSystemLocked()
         && Keypad.map(keyCode) == '$') {
         this.initCurrencySign();
         if (UiSettings.getCurrencyKey() == 0) {
            return 131071;
         }
      }

      synchronized (InputContext.getInstance()) {
         return this.processKeyEvent0(ID, keyCode, keyChar, modifiers, time, input);
      }
   }

   private final synchronized int processKeyEvent0(int ID, int keyCode, char keyChar, int modifiers, int time, boolean input) {
      InputContext inputContext = InputContext.getInstance();
      IComponent comp = inputContext.getInputComponent();
      if (ID == 6913) {
         if (keyCode == 0) {
            return 0;
         }

         ID = 519;
      }

      switch (ID) {
         case 512:
         case 517:
         case 518:
            return 0;
         case 513:
         case 514:
         case 515:
         case 520:
         default:
            this._keyEvent.init(comp, ID, time, SLKeyLayout.convertStatusToModifiers(modifiers), keyCode >> 16, keyChar, input);
            break;
         case 516:
         case 519:
            this._keyEvent.init(comp, ID, time, SLKeyLayout.convertStatusToModifiers(modifiers), keyCode, keyChar, input);
      }

      if (comp != null) {
         comp.dispatchEvent(this._keyEvent);
      } else {
         inputContext.dispatchEvent(this._keyEvent);
      }

      this._keyEvent.setSource(null);
      return this._keyEvent.isConsumed() ? this._keyEvent.getKeyChar() | 65536 : this._keyEvent.getKeyChar();
   }

   public final boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      switch (event) {
         case 516:
         case 6914:
            if ((this.processKeyEvent(516, 0, '\u0000', status, time, true) & 65536) == 65536) {
               return true;
            }

            return false;
         case 519:
         case 6913:
            synchronized (InputContext.getInstance()) {
               return this.processNavigationEvent0(event, dx, dy, status, time);
            }
         default:
            return false;
      }
   }

   private final boolean processNavigationEvent0(int event, int dx, int dy, int status, int time) {
      int eventID = 0;
      byte var9;
      if (event == 519) {
         var9 = 2;
      } else {
         if (event != 6913) {
            return false;
         }

         var9 = 1;
      }

      InputContext inputContext = InputContext.getInstance();
      IComponent comp = inputContext.getInputComponent();
      this._navigationEvent.init(comp, var9, dx, dy, status);
      if (comp != null) {
         comp.dispatchEvent(this._navigationEvent);
      } else {
         inputContext.dispatchEvent(this._navigationEvent);
      }

      this._navigationEvent.setSource(null);
      if (!this._navigationEvent.isConsumed()) {
         switch (event) {
            case 519:
               if ((this.processKeyEvent(519, dy, (char)status, status, time, true) & 65536) == 65536) {
                  return true;
               }

               return false;
            case 6913:
               if ((this.processKeyEvent(519, dx, (char)status, status, time, true) & 65536) == 65536) {
                  return true;
               }

               return false;
         }
      }

      return this._navigationEvent.isConsumed();
   }

   public final void focusGained(IComponent src, int time, int appID) {
      synchronized (InputContext.getInstance()) {
         this.focusGained0(src, time, appID);
      }
   }

   private final void focusGained0(IComponent src, int time, int appID) {
      if (src != null) {
         InputContext ic = InputContext.getInstance();
         int currentId = ic.getAppId();
         if (ic.getInputComponent() != src || currentId == -1 || currentId != appID || ic.getInputComponent() == src && currentId == appID) {
            this.fillFocusHistory(appID, src, true);
            this._focusEvent.init(src, 1004, appID);
            src.dispatchEvent(this._focusEvent);
            this._focusEvent.setSource(null);
         }
      }
   }

   public final void focusLost(IComponent src, int time, int appID) {
      synchronized (InputContext.getInstance()) {
         this.focusLost0(src, time, appID);
      }
   }

   private final void focusLost0(IComponent src, int time, int appID) {
      this.fillFocusHistory(appID, src, false);
      if (src instanceof TextInputDialog) {
         InputContext.getInstance().endComposition();
      } else {
         IComponent registeredInputComponent = InputContext.getInstance().getInputComponent();
         if (registeredInputComponent != null) {
            IComponent emulationEventSrc = InputContext.getInstance().getAppId() == appID ? registeredInputComponent : src;
            if (emulationEventSrc == null) {
               return;
            }

            this._focusEvent.init(emulationEventSrc, 1005, appID);
            emulationEventSrc.dispatchEvent(this._focusEvent);
            this._focusEvent.setSource(null);
         }
      }
   }

   public static final EventHandler getInstance() {
      return ((IMContext)InputContext.getInstance()).getEventHandler();
   }

   public final void actionPerformed(int action, Object parameter) {
      switch (action) {
         case 1:
            CurrencyKeyDialog.closeDialog();
      }
   }

   private final void fillFocusHistory(int appId, IComponent src, boolean eventType) {
      if (this._focusHistoryLogCount != 0) {
         this.printFocusEvent(appId, src == null ? "NULL" : src.getClass().getName(), eventType);
         if (appId != this._focusHistoryLastAppId) {
            this._focusHistoryLogCount = 3;
         } else {
            this._focusHistoryLogCount--;
         }
      } else {
         int count = this._focusHistoryIndex - this._focusHistoryStart;
         if (count < 0) {
            count += 3;
         }

         this._focusHistoryIndex = (this._focusHistoryIndex + 1) % 3;
         if (this._focusHistoryIndex == this._focusHistoryStart || this._focusHistoryStart == -1) {
            this._focusHistoryStart = (this._focusHistoryStart + 1) % 3;
         }

         this._focusHistoryAppId[this._focusHistoryIndex] = appId;
         this._focusHistoryComponent[this._focusHistoryIndex] = src == null ? "NULL" : src.getClass().getName();
         this._focusHistoryEvent[this._focusHistoryIndex] = eventType;
         if (appId != this._focusHistoryLastAppId) {
            this.printFocusHistory();
         }
      }

      this._focusHistoryLastAppId = appId;
   }

   private final void printFocusEvent(int appId, String component, boolean eventType) {
      String eventTypeText = eventType ? "Focus gained" : "Focus lost";
      String appName = this.findAppName(appId);
      System.out.println("FocusHistory: " + eventTypeText + "; App " + appName + "; Component " + component);
   }

   private final void printFocusHistory() {
      if (this._focusHistoryStart == -1) {
         System.out.println("FocusHistory: No focus history.");
      } else {
         int i = this._focusHistoryStart;

         while (true) {
            this.printFocusEvent(this._focusHistoryAppId[i], this._focusHistoryComponent[i], this._focusHistoryEvent[i]);
            this._focusHistoryComponent[i] = null;
            if (i == this._focusHistoryIndex) {
               this._focusHistoryLogCount = 3;
               this._focusHistoryStart = this._focusHistoryIndex = -1;
               return;
            }

            i = (i + 1) % 3;
         }
      }
   }

   private final String findAppName(int appId) {
      ApplicationManager am = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] apps = am.getVisibleApplications();

      for (int i = 0; i < apps.length; i++) {
         if (am.getProcessId(apps[i]) == appId) {
            return apps[i].getName();
         }
      }

      return "NoName";
   }
}
