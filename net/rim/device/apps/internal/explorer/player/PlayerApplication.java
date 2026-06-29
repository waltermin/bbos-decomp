package net.rim.device.apps.internal.explorer.player;

import javax.microedition.io.InputConnection;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.vm.Process;

final class PlayerApplication extends UiApplication implements GlobalEventListener, KeyListener {
   private PlayerApplication _app;
   private RenderScreen _renderScreen;
   private boolean _exitOnEscape;
   private PlayerVerbManager _verbManager;
   private ActionProvider _actionProvider;
   private static final long GUID_LAUNCH_PLAYER = -2364922203810937362L;
   private static final long GUID_STOP_PLAYER = 7880603250674304426L;
   private static final long GUID_SHOW_PLAYER = 110711286572786542L;

   public final void stop() {
      this._verbManager.deRegister();
      System.exit(0);
   }

   public final void invoke(InputConnection connection, Object context) {
      synchronized (this._app.getAppEventLock()) {
         if (this._renderScreen != null) {
            this.popScreen(this._renderScreen);
            this._renderScreen = null;
         }

         this._renderScreen = new RenderScreen(this._app, context, connection);
         if (this._renderScreen != null) {
            this._app.pushScreen(this._renderScreen);
            this._renderScreen.finishLoadingFile();
         }
      }
   }

   public final void pushForeground() {
      this.requestForeground();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -2364922203810937362L) {
         if (object0 instanceof Object) {
            InputConnection connection = (InputConnection)object0;
            this.invoke(connection, object1);
         }

         this._exitOnEscape = ContextObject.getFlag(object1, 39) && !ContextObject.getFlag(object1, 40);
         Object waitingScreen = ContextObject.get(object1, -1477447097671931650L);
         if (waitingScreen instanceof Object) {
            this._actionProvider = (ActionProvider)waitingScreen;
         }

         this.pushForeground();
      } else if (guid == 7880603250674304426L) {
         this.stop();
      } else {
         if (guid == 110711286572786542L) {
            this.pushForeground();
         }
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      boolean isEscape = Keypad.map(keycode) == 27;
      boolean isEnd = Keypad.key(keycode) == 18;
      if ((isEscape || isEnd) && this._exitOnEscape) {
         Screen screen = this.getActiveScreen();
         if (!(screen instanceof Object)) {
            new PlayerApplication$2(this, screen).start();
            return !isEnd;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   PlayerApplication() {
      this.addGlobalEventListener(this);
      this.addKeyListener(this);
      this.setApp(this);
      this._verbManager = new PlayerVerbManager(this);
      this._verbManager.register();
      ApplicationProcess applicationProcess = (ApplicationProcess)Process.currentProcess();
      if (applicationProcess != null) {
         Runnable cleanupRunnable = new PlayerApplication$1(this);
         applicationProcess.addCleanupRunnable(cleanupRunnable);
      }
   }

   @Override
   public final void activate() {
      if (this._actionProvider != null) {
         this._actionProvider.perform(5803508244060051872L, new Object());
         this._actionProvider = null;
      }

      super.activate();
   }

   @Override
   public final void deactivate() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   private final void setApp(PlayerApplication app) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public static final void main(String[] args) {
      PlayerApplication app = new PlayerApplication();
      app.enterEventDispatcher();
   }

   @Override
   protected final boolean acceptsForeground() {
      return true;
   }
}
