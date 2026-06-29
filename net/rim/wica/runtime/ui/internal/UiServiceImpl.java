package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.ui.ScreenManager;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.ui.UiService;
import net.rim.wica.runtime.ui.internal.component.ScreenContext;

public final class UiServiceImpl implements UiService, Serviceable, KeyListener {
   private ServiceProvider _provider;
   private ScreenManager _manager;
   private ApplicationScreen _screen;
   private ApplicationDialog _dialog;
   private WicletRuntime _runtime;
   private UiApplication _app = UiApplication.getUiApplication();
   private ResourceProvider _resourceProvider;
   private StyleFactory _styleFactory;
   private boolean _escapeKeyPressed;
   private boolean _modal;
   private UiServiceImpl$ModalDialogShower _shower;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$metadata$component$ui$ScreenManager;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$comm$CommunicationService;

   public final void forceModelUpdate() {
      Field focusedField = this._app.getActiveScreen().getLeafFieldWithFocus();

      while (focusedField != null && !(focusedField instanceof ModelUpdater)) {
         focusedField = focusedField.getManager();
      }

      if (focusedField instanceof ModelUpdater) {
         ((ModelUpdater)focusedField).updateModel();
      }
   }

   public final boolean isBackAvailable() {
      return this._manager.isBackAvailable();
   }

   public final void requestScreenBack() {
      if (this.isBackAvailable()) {
         this._runtime.requestScreenBack();
      } else {
         this.requestClose();
      }
   }

   public final void requestClose() {
      this.forceModelUpdate();
      this._runtime.stop();
   }

   public final WicletRuntime getRuntime() {
      return this._runtime;
   }

   public final ResourceProvider getResourceProvider() {
      return this._resourceProvider;
   }

   public final StyleFactory getStyleFactory() {
      return this._styleFactory;
   }

   @Override
   public final void showMenu(int instance) {
      if (this._screen != null) {
         this._screen.getContext().showMenu(instance);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 27) {
         this._escapeKeyPressed = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      if (this._escapeKeyPressed && Keypad.key(keycode) == 27) {
         this.requestClose();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      if (this._escapeKeyPressed && Keypad.key(keycode) == 27) {
         this.requestScreenBack();
         this._escapeKeyPressed = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final String toString() {
      return "UIService";
   }

   @Override
   public final int displayModalDialog(int type, String message) {
      synchronized (this) {
         this._modal = true;
      }

      this._shower = new UiServiceImpl$ModalDialogShower(message, type);
      if (this._app.isEventThread()) {
         this._app.invokeAndWait(this._shower);
      } else {
         this._runtime.notifyStartupLock();
         this._app.invokeLater(this._shower);
         synchronized (this._shower) {
            label62:
            try {
               this._shower.wait();
            } finally {
               break label62;
            }
         }
      }

      Screen currentScreen = this._app.getActiveScreen();
      if (currentScreen != null) {
         currentScreen.invalidate();
      }

      synchronized (this) {
         this._modal = false;
      }

      return this._shower.getResult();
   }

   @Override
   public final void closeModalDialog() {
      if (this._modal) {
         synchronized (this._shower) {
            this._shower.notify();
         }
      }
   }

   @Override
   public final void displayScreen(ScreenModel screenModel, boolean reset) {
      ScreenContext context = (ScreenContext)screenModel.getView();
      if (context == null) {
         context = new ScreenContext(this, screenModel, 0, 18014398509481984L);
      }

      synchronized (this._app.getAppEventLock()) {
         if (screenModel.isDialog()) {
            if (this._dialog == null) {
               this._dialog = new ApplicationDialog();
               this._dialog.addKeyListener(this);
            }

            this._dialog.setContext(context);
            if (!this._dialog.isDisplayed()) {
               this._app.pushScreen(this._dialog);
            }
         } else {
            if (this._dialog != null && this._dialog.isDisplayed()) {
               this._app.popScreen(this._dialog);
            }

            if (this._screen == null) {
               this._screen = new ApplicationScreen();
               this._screen.addKeyListener(this);
            }

            this._screen.setContext(context, !reset);
            if (!this._screen.isDisplayed()) {
               this._app.pushScreen(this._screen);
            }
         }
      }
   }

   @Override
   public final void performScreenBack() {
      this._manager.back();
   }

   @Override
   public final void setServices(ServiceProvider provider) {
      this._provider = provider;
      this._runtime = (WicletRuntime)this._provider
         .getService(
            class$net$rim$wica$runtime$metadata$WicletRuntime == null
               ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
               : class$net$rim$wica$runtime$metadata$WicletRuntime
         );
      this._manager = (ScreenManager)this._runtime
         .getService(
            class$net$rim$wica$runtime$metadata$component$ui$ScreenManager == null
               ? (class$net$rim$wica$runtime$metadata$component$ui$ScreenManager = class$("net.rim.wica.runtime.metadata.component.ui.ScreenManager"))
               : class$net$rim$wica$runtime$metadata$component$ui$ScreenManager
         );
      this._resourceProvider = new ResourceProvider(
         this._runtime.getWiclet(),
         (PersistenceService)this._provider
            .getService(
               class$net$rim$wica$runtime$persistence$PersistenceService == null
                  ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
                  : class$net$rim$wica$runtime$persistence$PersistenceService
            ),
         (CommunicationService)this._provider
            .getService(
               class$net$rim$wica$runtime$comm$CommunicationService == null
                  ? (class$net$rim$wica$runtime$comm$CommunicationService = class$("net.rim.wica.runtime.comm.CommunicationService"))
                  : class$net$rim$wica$runtime$comm$CommunicationService
            )
      );
      this._styleFactory = new StyleFactory(this._runtime.getWiclet().getStyles(), this._resourceProvider);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
