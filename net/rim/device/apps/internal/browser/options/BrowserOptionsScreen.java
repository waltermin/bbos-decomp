package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.BitSet;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.common.BrowserLockScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.debug.DebugViewerScreen;
import net.rim.device.apps.internal.browser.debug.PacketLoggerUi;
import net.rim.device.apps.internal.browser.push.BrowserPushModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.verbs.BrowserVerb;
import net.rim.device.cldc.io.utility.PacketLogger;
import net.rim.device.internal.browser.util.TimeLogger;

public final class BrowserOptionsScreen extends MainScreen implements ListFieldCallback, BrowserLockScreen, GlobalEventListener, BrowserOptionsChangeListener {
   private IBrowserProperty[] _userOptions;
   private IBrowserProperty[] _restrictedOptions;
   private IBrowserProperty[] _plugInOptions;
   private boolean _restrictedAccess = true;
   private ListField _options;
   boolean _releaseLockOnExit;
   private boolean _requiresRefresh;

   public BrowserOptionsScreen(boolean releaseLockOnExit) {
      this._releaseLockOnExit = releaseLockOnExit;
      this._userOptions = new IBrowserProperty[]{new BrowserConfigProperty(), new GeneralProperty(), new CacheOpsScreen()};
      this._restrictedOptions = new IBrowserProperty[]{new BrowserPushModel()};
      this._plugInOptions = BrowserDaemonRegistry.getPlugInOptions();
      this.setTitle((Field)(new Object(BrowserResources.getString(131))));
      this._options = (ListField)(new Object(this._userOptions.length + this._plugInOptions.length));
      this._options.setCallback(this);
      this.add(this._options);
   }

   @Override
   public final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.getApplication().addGlobalEventListener(this);
         GeneralProperty.addListener(this);
      } else {
         this.getApplication().removeGlobalEventListener(this);
         GeneralProperty.removeListener(this);
         if (this._requiresRefresh) {
            BrowserVerb verb = BrowserDaemonRegistry.getInstance().getBrowserVerbRepository().getVerbNoCheck(9);
            if (verb != null) {
               verb.invoke(null);
            }
         }
      }
   }

   @Override
   public final void optionsChanged(BitSet changedOptions) {
      if (changedOptions.isSet(26) || changedOptions.isSet(27) || changedOptions.isSet(36) || changedOptions.isSet(37)) {
         this._requiresRefresh = true;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         for (int i = 0; i < this._userOptions.length; i++) {
            IBrowserProperty var10000 = this._userOptions[i];
            if (this._userOptions[i] instanceof BrowserConfigProperty) {
               ((BrowserConfigProperty)var10000).itPolicyChanged();
            }
         }
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      graphics.drawText(this.get(index).getLabel(), 0, y, 64, width);
   }

   private final IBrowserProperty get(int index) {
      if (index < this._userOptions.length) {
         return this._userOptions[index];
      } else {
         return index < this._userOptions.length + this._plugInOptions.length
            ? this._plugInOptions[index - this._userOptions.length]
            : this._restrictedOptions[index - this._userOptions.length - this._plugInOptions.length];
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.get(index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return 0;
   }

   @Override
   public final boolean onMenu(int instance) {
      return this.invokeAction(1);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            Screen s = UiApplication.getUiApplication().getActiveScreen();
            Field fieldWithFocus = s.getLeafFieldWithFocus();
            if (fieldWithFocus instanceof Object) {
               int index = ((ListField)fieldWithFocus).getSelectedIndex();
               if (index >= 0) {
                  Verb openVerb = this.get(index).getOpenVerb(this._restrictedAccess);
                  openVerb.invoke(null);
               }
            }

            return true;
         default:
            return false;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         synchronized (Application.getEventLock()) {
            UiApplication.getUiApplication().popScreen(this);
         }

         this.cleanupScreen();
         return true;
      } else if (key == '\n') {
         Screen s = UiApplication.getUiApplication().getActiveScreen();
         Field fieldWithFocus = s.getLeafFieldWithFocus();
         if (fieldWithFocus instanceof Object) {
            int index = ((ListField)fieldWithFocus).getSelectedIndex();
            this.get(index).getOpenVerb(this._restrictedAccess).invoke(null);
         }

         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1380076611:
            PacketLogger.getInstance().clearSavedPackets();
            return true;
         case 1380076614:
            PacketLoggerUi.showPacketLog();
            return true;
         case 1380076616:
            PacketLogger.getInstance().startLogging(false);
            return true;
         case 1380076622:
            PacketLogger.getInstance().startLogging(true);
            return true;
         case 1380076624:
            PacketLoggerUi.showSavedMessage(PacketLogger.getInstance().savePackets());
            return true;
         case 1380076627:
            PacketLogger.getInstance().printPacketsToStdout();
            return true;
         case 1380078159:
            this._restrictedAccess = false;
            ((GeneralProperty)this._userOptions[1]).setAccessType(this._restrictedAccess);
            this._options.setSize(this._userOptions.length + this._plugInOptions.length + this._restrictedOptions.length);
            return true;
         case 1380078662:
            new DebugViewerScreen(TimeLogger.getInstance().getText(), "Time Log", null);
            return true;
         case 1380078671:
            TimeLogger.getInstance().startLogging();
            return true;
         default:
            return super.openDevelopmentBackdoor(backdoorCode);
      }
   }

   @Override
   public final void cleanupScreen() {
      if (this._releaseLockOnExit) {
         BrowserDaemonRegistry.getInstance().releaseBrowserLock();
      }
   }
}
