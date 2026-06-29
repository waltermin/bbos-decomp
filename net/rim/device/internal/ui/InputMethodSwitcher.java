package net.rim.device.internal.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

public final class InputMethodSwitcher extends PopupScreen implements FocusChangeListener, ListFieldCallback, Runnable, GlobalEventListener {
   private int _selectedIM;
   UiApplication _app;
   private ListField _listField;
   private String[] _imNames;
   private Locale[] _imLocales;
   private boolean _startedFromMenu;
   private Runnable _onExit;
   private Locale _selectedLocale;
   private boolean _closing;

   public final void selectNext(int status) {
      if ((status & 1) == 0) {
         this.closeSwitcher(false);
      }

      this.setIndex(this._selectedIM + 1);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.getPreferredWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._imNames[index];
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index > -1 && index < this._imNames.length) {
         graphics.drawText(this._imNames[index], 0, y, 64, width);
      }
   }

   @Override
   public final void focusChanged(Field field, int action) {
      if (action == 2) {
         this._selectedIM = this._listField.getSelectedIndex();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 5961289116197897667L && data0 == 1 && data1 != 1) {
         this.doCloseSwitcher(false);
      }
   }

   @Override
   public final void run() {
      ResourceBundle bundle = ResourceBundle.getBundle(-6812884907508133143L, "net.rim.device.internal.resource.Common");
      String[] choices = bundle.getStringArray(10113);
      StringBuffer message = new StringBuffer(bundle.getString(10114));
      message.append(StringUtilities.toUpperCase(this._selectedLocale.getDisplayName(), this._selectedLocale.getCode()));
      message.append(bundle.getString(10115));
      InputMethodSwitcher$IMSwitcherDialog d = new InputMethodSwitcher$IMSwitcherDialog(message.toString(), choices, 0);
      switch (d.doModal()) {
         case 0:
         default:
            IMSwitcherOption.getInstance().setState((byte)2);
            Locale.setDefaultInputForSystem(this._selectedLocale);
            return;
         case 1:
            IMSwitcherOption.getInstance().setState((byte)3);
            Locale.setDefaultInputForSystem(this._selectedLocale);
         case -1:
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached && !this._startedFromMenu) {
         this.selectNext(1);
      }
   }

   public InputMethodSwitcher(Locale[] inputLocales, String[] inputLocalesNames, boolean startedFromMenu, Runnable onExit) {
      super(new VerticalFieldManager(299067162755072L), 0);
      this._startedFromMenu = startedFromMenu;
      if (this._startedFromMenu) {
         InputContext.getInstance().setIMSwitchEnabled(false);
      }

      this._onExit = onExit;
      this._app = UiApplication.getUiApplication();
      this._app.addGlobalEventListener(this);
      this._imNames = inputLocalesNames;
      this._imLocales = inputLocales;
      this._listField = new ListField(this._imLocales.length, 8);
      this._listField.setCallback(this);
      this._listField.setFocusListener(this);
      this.add(this._listField);
      this.setIndex(0);
      boolean isRIM = ControlledAccess.verifyCodeModuleSignature(TraceBack.getCallingModule(2), 51);
      int priority = isRIM ? -1073741823 : -1;
      if (ApplicationManager.getApplicationManager().isSystemLocked()) {
         priority = -1073741825;
      }

      this._app.pushGlobalScreen(this, priority, 6);
   }

   private final void setIndex(int index) {
      this._selectedIM = index;
      if (this._selectedIM >= this._imNames.length) {
         this._selectedIM = 0;
      }

      this._listField.setSelectedIndex(this._selectedIM);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.closeSwitcher(true);
      return true;
   }

   private final void closeSwitcher(boolean selected) {
      boolean oldClosing;
      synchronized (this) {
         oldClosing = this._closing;
         this._closing = true;
      }

      if (!oldClosing) {
         this.doCloseSwitcher(selected);
      }
   }

   private final void doCloseSwitcher(boolean selected) {
      System.out.println("switch_input_method");
      if (selected && this._selectedIM != 0) {
         this._selectedLocale = this._imLocales[this._selectedIM];
      }

      boolean changeLocale = true;
      this._app.removeGlobalEventListener(this);
      this._app.popScreen(this);
      if (this._onExit != null) {
         if (this._selectedLocale != null && IMSwitcherOption.getInstance().getState() == 1) {
            int pid = ApplicationManager.getApplicationManager().getForegroundProcessId();
            Process pr = Process.getProcess(pid);
            if (pr instanceof ApplicationProcess) {
               changeLocale = false;
               ((ApplicationProcess)pr).getApplication().invokeLater(this);
            }
         }

         this._app.invokeLater(this._onExit);
      }

      if (changeLocale) {
         Locale.setDefaultInputForSystem(this._selectedLocale);
      }

      if (this._startedFromMenu) {
         InputContext.getInstance().setIMSwitchEnabled(true);
      }
   }

   @Override
   protected final boolean keyStatus(int keycode, int time) {
      if (!this._startedFromMenu && Keypad.status(keycode) != 32768) {
         this.closeSwitcher(Keypad.key(keycode) == 257);
      }

      return true;
   }

   private final void onEnter(int status) {
      if (!this._startedFromMenu && (status & 1) != 0) {
         this.setIndex(this._selectedIM + 1);
      } else {
         this.closeSwitcher(true);
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int status = Keypad.status(keycode);
      keycode = Keypad.keycode((char)Keypad.key(keycode), status & -2);
      switch (Keypad.map(keycode)) {
         case '\n':
            this.onEnter(status);
            return true;
         case '\u001b':
            if (this._startedFromMenu || (status & 1) == 0) {
               this.closeSwitcher(false);
            }

            return true;
         default:
            return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this.onEnter(status);
      return true;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return true;
   }
}
