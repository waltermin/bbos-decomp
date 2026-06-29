package net.rim.device.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.MessageListener;
import net.rim.device.internal.system.UnhandledGlobalKeyListener;
import net.rim.device.internal.ui.BackingStore;
import net.rim.device.internal.ui.MIDletApplication;
import net.rim.device.internal.ui.UiInternalListener;
import net.rim.device.internal.util.TestException;
import net.rim.vm.Message;
import net.rim.vm.MessageQueue;
import net.rim.vm.Monitor;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

final class UiEngineImpl implements GlobalEventListener, HolsterListener, MessageListener, SystemListener2, UiEngine {
   private UiEngineImpl$ScreenList _screenList = new UiEngineImpl$ScreenList(this);
   private Screen _inputScreen;
   private XYRect _appInvalid = new XYRect();
   private boolean _somethingInvalid;
   private XYRect _fullScreenRect = new XYRect(0, 0, Display.getWidth(), Display.getHeight());
   private UiEngineImpl$BottomScreen _bottomScreen;
   private int _suspendPainting;
   private Application _app;
   private boolean _isMidlet;
   private boolean _isInPopScreen;
   private Object[] _uiEngineListener;
   private Object[] _userInputEventListener;
   private GlobalRepaintNotifier _globalRepaintNotifier = new GlobalRepaintNotifier();
   Graphics _fbGraphics = new Graphics();
   private int _stylusX = -1;
   private int _stylusY = -1;
   static final boolean DEBUG_PAINT = false;
   private static final int NO_PAINT_WATERMARK = 5;
   static int _layoutGeneration;
   private static UiEngineImpl _uiEngine;

   public final void addUiEngineListener(UiEngineListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._uiEngineListener = ListenerUtilities.addListener(this._uiEngineListener, listener);
   }

   final Screen[] getLocalWrappedScreens() {
      return this._screenList.wrapLocalScreens();
   }

   final boolean allowImmediate(Screen screen) {
      return !this._appInvalid.isEmpty() ? false : this._screenList.isTopmost(screen);
   }

   public final void doPainting() {
      if (this._somethingInvalid) {
         if (this._suspendPainting == 0) {
            if (!this._app.isForeground() && !this.equals(GlobalScreenManager.getPaintControlEngine())) {
               this.paintInProcessGlobalScreens();
               this._somethingInvalid = false;
            } else if (GlobalScreenManager.getPaintControlEngine() != null && !this.equals(GlobalScreenManager.getPaintControlEngine())) {
               this.checkForExtentChanges();
               this.paintWrappedLocalScreens();
               this._somethingInvalid = false;
            } else {
               this.checkForExtentChanges();
               boolean hasAppInvalid = !this._appInvalid.isEmpty();
               XYRect screenInvalid = Ui.getTmpXYRect();
               this.gatherInvalidRegions(screenInvalid, false, 0);
               XYRect totalInvalid = Ui.getTmpXYRect();
               totalInvalid.set(this._appInvalid);
               totalInvalid.unionNoEmpty(screenInvalid);
               if (totalInvalid.isEmpty()) {
                  this._somethingInvalid = false;
                  Ui.returnTmpXYRect(screenInvalid);
                  Ui.returnTmpXYRect(totalInvalid);
               } else {
                  int highest = this._screenList.highestOpaqueRegionContaining(totalInvalid);
                  Ui.returnTmpXYRect(screenInvalid);
                  Ui.returnTmpXYRect(totalInvalid);
                  this.gatherInvalidRegions(this._appInvalid, true, highest);
                  XYRect paintedRect = Ui.getTmpXYRect();
                  XYRect invalid = Ui.getTmpXYRect();
                  XYRect tmp = Ui.getTmpXYRect();
                  Screen screen = null;
                  XYRect appInvalid = Ui.getTmpXYRect();
                  appInvalid.set(this._appInvalid);
                  this._appInvalid.set(0, 0, 0, 0);
                  this._somethingInvalid = false;
                  int screenCount = this._screenList.getScreenCount();

                  for (int i = highest; i < screenCount; i++) {
                     if (i == -1) {
                        screen = this._bottomScreen;
                     } else {
                        screen = this._screenList.getScreen(i);
                     }

                     XYRect extent = this.getScreenExtent(screen);
                     invalid.set(this.getScreenInvalid(screen));
                     tmp.set(appInvalid);
                     tmp.intersect(extent);
                     invalid.unionNoEmpty(tmp);
                     if (this.isScreenTransparent(screen) || this.isScreenTransparentBorder(screen)) {
                        tmp.set(paintedRect);
                        tmp.intersect(extent);
                        invalid.unionNoEmpty(tmp);
                     }

                     if (hasAppInvalid && i == screenCount - 1) {
                        Graphics.resetOverlays();
                     }

                     invalid.intersect(this._fullScreenRect);
                     if (!invalid.isEmpty() && this.isScreenDisplayed(screen)) {
                        screen.doPaintInternal(invalid);
                        paintedRect.unionNoEmpty(invalid);
                        paintedRect.intersect(this._fullScreenRect);
                     }
                  }

                  if (this._somethingInvalid && !invalid.isEmpty() && this._app.getMessageQueueSize() == 0) {
                     this._app.invokeLater(Ui.nullRunnable);
                  }

                  Ui.returnTmpXYRect(appInvalid);
                  Ui.returnTmpXYRect(paintedRect);
                  Ui.returnTmpXYRect(invalid);
                  Ui.returnTmpXYRect(tmp);
                  this.updateDisplay();
               }
            }
         }
      }
   }

   final void invalidateTransparentScreens(Screen modified) {
      int index = this._screenList.getIndex(modified);
      if (index >= 0) {
         int numScreens = this._screenList.getScreenCount();
         XYRect rect = this.getScreenExtent(modified);

         for (int lv = index + 1; lv < numScreens; lv++) {
            Screen screen = this._screenList.getScreenInNonEventThread(lv);
            if (screen == null) {
               return;
            }

            if (this.isInProcess(screen) && this.isScreenTransparent(screen)) {
               screen.invalidateAll(rect.x - screen.getLeft(), rect.y - screen.getTop(), rect.width, rect.height);
            }
         }
      }
   }

   public final Application getApplication() {
      assertIpcOrDependency();
      return this._app;
   }

   final void removeLocalWrappedScreens() {
      for (int i = this._screenList.getLocalScreenCount() - 1; i >= 0; i--) {
         Screen next = this._screenList.getScreen(i);
         if (next instanceof UiEngineImpl$ProxyScreen) {
            ((UiEngineImpl$ProxyScreen)next).getWrappedScreen().setBackingStoreUpdated(false);
            ((UiEngineImpl$ProxyScreen)next).getWrappedScreen().invalidateInternal();
            this._screenList.pop(next);
         }
      }

      Screen topmostLocal = this._screenList.getTopmostLocalScreen();
      if (topmostLocal != null && this._screenList.getGlobalScreenCount() > 0) {
         topmostLocal.invalidate();
      }
   }

   final void injectLocalWrappedScreens(Screen[] screens) {
      if (this.isMessageValid(screens)) {
         this.removeLocalWrappedScreens();
      }

      for (int i = 0; i < screens.length; i++) {
         UiEngineImpl$ProxyScreen next = (UiEngineImpl$ProxyScreen)screens[i];
         if (next.getWrappedScreen().getUiEngineImpl() != null) {
            if (!next.getWrappedScreen().getUiEngineImpl().getApplication().isForeground()) {
               return;
            }

            next.setUiEngine(this);
            next.doLayoutNoSynch();
            this._screenList.push(next);
            this._screenList.updateExtent(next);
            Screen wrapped = next.getWrappedScreen();
            wrapped.invalidateInternal();
            wrapped.cleanBackingStore();
         }
      }

      this.notifyPaintableScreens(true);
      this.notifyVisibleScreens(true);
   }

   final boolean isValid() {
      return this._appInvalid.width == 0 || this._appInvalid.height == 0;
   }

   final XYRect[] getOpaqueRegionsArray() {
      return this._screenList.getOpaqueRegionsArray();
   }

   final boolean isEventLockRequired() {
      return this._app.isHandlingEvents() && !Monitor.monitorOwned(this._app.getAppEventLock());
   }

   final void assertHaveEventLock() {
      if (this._app.isHandlingEvents() && !Monitor.monitorOwned(this._app.getAppEventLock())) {
         throw new IllegalStateException("UI engine accessed without holding the event lock.");
      }
   }

   final void notifyUserInputEventListener(int device) {
      Object[] listeners = this._userInputEventListener;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            ((UserInputEventListener)listeners[i]).onUserInput(device, 0);
         }
      }
   }

   final void forceRepaintIfNotOnEventThread() {
      if (!this._app.isEventThread() && this._app.getMessageQueueSize() == 0) {
         this._app.invokeLater(Ui.nullRunnable);
      }
   }

   final void statusDismissedEvent(Screen screen) {
      GlobalScreenManager.assertHaveLock();
      if (screen.getPushMethod() == 0) {
         throw new IllegalStateException("Cannot mix pushGlobalScreen-popScreen with queueStatus-dismissStatus.");
      }

      this._app.invokeLater(new UiEngineImpl$StatusDismissedHandler(this, screen));
      RIMGlobalMessagePoster.postGlobalEvent(5961289116197897667L, 2, 0, screen.getExtent(), null);
   }

   final void statusDisplayedEvent(Screen screen, boolean inputRequired, boolean redisplay, boolean isTopmost, XYRect revokedInvalid) {
      GlobalScreenManager.assertHaveLock();
      screen.setPushMethod(1);
      this._app.invokeLater(new UiEngineImpl$StatusDisplayedHandler(this, screen, inputRequired, redisplay, revokedInvalid));
   }

   final void setSomethingInvalid() {
      this._somethingInvalid = true;
   }

   public final void removeUiEngineListener(UiEngineListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._uiEngineListener = ListenerUtilities.removeListener(this._uiEngineListener, listener);
   }

   final Screen getActiveLocalGlobalScreen() {
      return this._screenList.getTopmostScreen();
   }

   final void appInvalidate(Screen screen) {
      XYRect extent = this.getScreenExtent(screen);
      this.appInvalidate(extent.x, extent.y, extent.width, extent.height);
   }

   final Screen getGlobalScreen() {
      return this._screenList.getTopmostGlobalScreen();
   }

   final Screen getInputScreen() {
      return this._inputScreen;
   }

   final Screen getScreen(int index) {
      return this._screenList.getLocalScreen(index);
   }

   final Screen getScreenAbove(Screen screen) {
      assertIpcOrDependency();
      return this._screenList.getLocalScreenAbove(screen);
   }

   final Screen getScreenBelow(Screen screen) {
      assertIpcOrDependency();
      return this._screenList.getLocalScreenBelow(screen);
   }

   final boolean isOutOfProcessGlobalScreen(Screen screen) {
      return screen != null && screen.isGlobal() && !this.isInProcess(screen);
   }

   final boolean hasNonNullBackingStore(Screen screen) {
      return this.isOutOfProcessGlobalScreen(screen) && ((UiEngineImpl$ProxyScreen)screen).getWrappedScreen().getBackingStore() != null;
   }

   final void appInvalidate(int x, int y, int width, int height) {
      synchronized (this._appInvalid) {
         this._appInvalid.unionNoEmpty(x, y, width, height);
         this._somethingInvalid = true;
         this.forceRepaintIfNotOnEventThread();
      }
   }

   final boolean shouldRemoveLocalWrappedScreens() {
      Screen[] hidden = this._screenList.getHiddenGlobalScreens();
      int count = 0;

      for (int i = 0; i < hidden.length; i++) {
         Screen next = hidden[i];
         if (this.isInProcess(next) && next.acceptsInput() || next.getUiEngine() == null) {
            count++;
         }
      }

      return this.getLocalInProcessGlobalScreenCount() + count - 1 == this.getScreenCount();
   }

   final boolean shouldAddLocalWrappedScreens() {
      Screen[] hidden = this._screenList.getHiddenGlobalScreens();

      for (int i = 0; i < hidden.length; i++) {
         Screen next = hidden[i];
         if (this.isInProcess(next) && next.acceptsInput()) {
            return true;
         }
      }

      return false;
   }

   final int getLocalGlobalScreenCount() {
      return this._screenList.getScreenCount();
   }

   public final int getUiApplicationStyle() {
      return !(this._app instanceof UiApplication) ? 0 : ((UiApplication)this._app).getStyle();
   }

   final int getScreenIndex(Screen screen) {
      return this._screenList.getLocalIndex(screen);
   }

   final int getLocalGlobalScreenIndex(Screen screen) {
      return this._screenList.getIndex(screen);
   }

   @Override
   public final void queueStatus(Screen screen, int priority, boolean inputRequired) {
      if (screen.isPaintController()) {
         GlobalScreenManager.injectLocalWrappedScreens(this);
      }

      GlobalScreenManager.queue(screen, priority, inputRequired, Process.currentProcess().getProcessId(), this);
      UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
      if (listener != null) {
         listener.onPushGlobalScreen(this, screen, priority, 1073741826);
      }
   }

   @Override
   public final void pushGlobalScreen(Screen screen, int priority, boolean inputRequired) {
      GlobalScreenManager.push(screen, priority, inputRequired, Process.currentProcess().getProcessId(), this);
      UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
      if (listener != null) {
         listener.onPushGlobalScreen(this, screen, priority, 1073741824);
      }
   }

   @Override
   public final void pushGlobalScreen(Screen screen, int priority, int flags) {
      if (screen.isGlobal()) {
         throw new IllegalStateException("Global screen already pushed.");
      }

      if (screen.getUiEngine() != null) {
         throw new IllegalStateException("Global screen already pushed as a local.");
      }

      this.assertHaveEventLock();
      boolean modal = (flags & 1) != 0;
      if (modal && !this._app.isEventThread()) {
         throw new RuntimeException("pushGlobalScreen(modal) called by a non-event thread");
      }

      UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
      if (listener != null) {
         listener.onPushGlobalScreen(this, screen, priority, flags);
      }

      Object[] listeners = this._uiEngineListener;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            ((UiEngineListener)listeners[i]).onPushGlobalScreen(screen, priority, flags);
         }
      }

      screen.setPushMethod(0);
      Screen screenObscured = null;
      UiEngineImpl$FocusNotifier focusNotifier = null;
      Throwable exception = null;
      synchronized (GlobalScreenManager.getLock()) {
         screenObscured = this._screenList.getTopmostScreen();
         GlobalScreenManager.push(screen, priority, flags, Process.currentProcess().getProcessId(), this);
         this._screenList.copyGlobalScreens();
         Screen newTopmost = this._screenList.getTopmostScreen();
         if (newTopmost == screenObscured) {
            screenObscured = null;
         }

         if (screen.acceptsInput()) {
            focusNotifier = this.setInputScreen();
         }
      }

      if (_layoutGeneration != screen._layoutGeneration) {
         screen.invalidateLayout0();
         screen._layoutGeneration = _layoutGeneration;
      }

      try {
         screen.doLayout();
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      this._somethingInvalid = true;

      try {
         screen.callOnUiEngineAttached(true);
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      if (this._app.isForeground()) {
         try {
            this.notifyPaintableScreens(true);
         } catch (TestException e) {
            exception = e;
         } catch (Throwable e) {
            exception = e;
         }

         try {
            this.notifyVisibleScreens(true);
         } catch (TestException e) {
            exception = e;
         } catch (Throwable e) {
            exception = e;
         }
      }

      try {
         this.notifyPaintableGlobalScreens(true);
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      try {
         this.notifyVisibleGlobalScreens(true);
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      if (screenObscured != null && screenObscured.isUiEngineAttached()) {
         try {
            screenObscured.callOnObscured();
         } catch (TestException e) {
            exception = e;
         } catch (Throwable e) {
            exception = e;
         }
      }

      if (focusNotifier != null) {
         try {
            focusNotifier.run();
         } catch (TestException e) {
            exception = e;
         } catch (Throwable e) {
            exception = e;
         }
      }

      this.notifyNewlyHiddenGlobalScreens();
      screen.invalidate();
      if (!this._app.isEventThread() && this._app.hasEventThread()) {
         this.doPainting();
      }

      if (exception != null) {
         if (!(exception instanceof RuntimeException)) {
            throw (Error)exception;
         } else {
            throw (RuntimeException)exception;
         }
      } else {
         if (modal) {
            this.doPainting();
            UiModalEventThread thread = new UiModalEventThread(screen);
            this._app.startModalEventThread(thread);
         }
      }
   }

   @Override
   public final void dismissStatus(Screen screen) {
      screen.setDismissing(true);
      GlobalScreenManager.dismiss(screen, this, true, Process.currentProcess().getProcessId());
      screen.setDismissing(false);
   }

   @Override
   public final void processMessage(Object eventLock, Message message, boolean consumed) {
      synchronized (eventLock) {
         boolean suppressPaint = false;
         boolean updateDisplayOutsideNormalPaint = false;
         if (!consumed) {
            int event = message.getEvent();
            Screen inputScreen = this._inputScreen;
            label279:
            switch (message.getDevice()) {
               case 0:
                  switch (event) {
                     case 3:
                        this.repaint();
                        suppressPaint = true;
                        if (InternalServices.isFermion()) {
                           RIMGlobalMessagePoster.postGlobalEvent(-8341035019292897176L);
                        }
                        break label279;
                     case 10:
                        Class cls;
                        try {
                           cls = Class.forName("net.rim.device.internal.ui.Backdoor");
                        } catch (ClassNotFoundException cnfe) {
                           break label279;
                        }

                        Object obj;
                        try {
                           obj = cls.newInstance();
                        } catch (InstantiationException ie) {
                           break label279;
                        } catch (IllegalAccessException aie) {
                           break label279;
                        }

                        ((Runnable)obj).run();
                        break label279;
                     case 12:
                        UiEngineImpl$FocusNotifier focusNotifier = null;
                        if (this._app.isForeground()) {
                           GlobalScreenManager.setForegroundEngine(this);
                        }

                        synchronized (GlobalScreenManager.getLock()) {
                           this._screenList.copyGlobalScreens();
                           focusNotifier = this.setInputScreen();
                        }

                        if (focusNotifier != null) {
                           focusNotifier.run();
                        }

                        Screen topmostScreen = this._screenList.getTopmostScreen();
                        if (this.isInProcess(topmostScreen) && !topmostScreen.isGlobal()) {
                           topmostScreen.callOnExposed();
                        }

                        this.notifyPaintableScreens(true);
                        this.notifyVisibleScreens(true);
                        suppressPaint = true;
                        Trackball.updateDeviceWithAppSettings();
                        Keypad.updateKeyTone();
                        Object[] listeners = this._uiEngineListener;
                        if (listeners == null) {
                           break label279;
                        }

                        int i = listeners.length - 1;

                        while (true) {
                           if (i < 0) {
                              break label279;
                           }

                           ((UiEngineListener)listeners[i]).onForeground();
                           i--;
                        }
                     case 13:
                        if (!this._app.isForeground()) {
                           UiEngineImpl$FocusNotifier focusNotifier = null;
                           synchronized (GlobalScreenManager.getLock()) {
                              focusNotifier = this.setInputScreen();
                           }

                           if (focusNotifier != null) {
                              focusNotifier.run();
                           }

                           Screen topmostScreen = this._screenList.getTopmostScreen();
                           if (this.isInProcess(topmostScreen) && !topmostScreen.isGlobal()) {
                              topmostScreen.callOnObscured();
                           }

                           Screen screen = topmostScreen;

                           for (;
                              screen != null && screen.isGlobal();
                              screen = this._screenList.getIndex(screen) == 0 ? null : this._screenList.getScreenBelow(screen)
                           ) {
                              if (this.isInProcess(screen)) {
                                 screen.invalidate();
                              }
                           }

                           for (int i = 0; i < this._screenList.getLocalScreenCount(); i++) {
                              this.releaseBackingStore(this._screenList.getLocalScreen(i));
                           }

                           this.notifyPaintableScreens(false);
                           this.notifyVisibleScreens(false);
                           this.notifyPaintableGlobalScreens(true);
                           this.notifyPaintableWrappedLocalScreens(true);
                           Object[] listeners = this._uiEngineListener;
                           if (listeners != null) {
                              for (int i = listeners.length - 1; i >= 0; i--) {
                                 ((UiEngineListener)listeners[i]).onBackground();
                              }
                           }
                        } else {
                           String errMessage = "UIE: JVM_SWITCH_BACKGROUND and isForeground()";
                           System.out.println(errMessage);
                           EventLogger.logEvent(-4685663286194863677L, errMessage.getBytes(), 0);
                        }
                     default:
                        break label279;
                  }
               case 2:
                  this.notifyUserInputEventListener(1);
                  if (inputScreen == null || inputScreen != GlobalScreenManager.getScreenWithFocus()) {
                     Screen previous = GlobalScreenManager.getScreenWithFocus();
                     String errMessage = "UIE: Focus - target lost, prev="
                        + previous
                        + ", input="
                        + inputScreen
                        + ", app="
                        + this._app
                        + ", "
                        + this._screenList.getScreenListDebugging();
                     System.out.println(errMessage);
                     EventLogger.logEvent(-4685663286194863677L, errMessage.getBytes(), 0);
                     UiEngineImpl$FocusNotifier focusNotifier = null;
                     synchronized (GlobalScreenManager.getLock()) {
                        this._screenList.copyGlobalScreens();
                        this._inputScreen = null;
                        focusNotifier = this.setInputScreen();
                        inputScreen = this._inputScreen;
                     }

                     if (focusNotifier != null) {
                        focusNotifier.run();
                     }
                  }

                  if (inputScreen == null) {
                     this.keyNotHandled(message);
                     break;
                  }

                  switch (event) {
                     case 515:
                     case 518:
                        char key = (char)message.getSubMessage();
                        int keycode = message.getData0();
                        int time = message.getData1();
                        UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
                        if (listener != null) {
                           listener.onUserKey(this, inputScreen, event, key, keycode, time);
                        }

                        if ((keycode & 128) != 0 && inputScreen.ignoreBacklightOffKeyEvent(event, key, keycode, time)) {
                           this.keyNotHandled(message);
                        } else {
                           int code = Keypad.key(keycode);
                           boolean isVolumeKey = false;
                           char volumeKey = 0;
                           if (4096 == code || 4097 == code) {
                              if (event == 515) {
                                 break;
                              }

                              event = 32768;
                              volumeKey = (char)(code == 4096 ? 150 : 151);
                              isVolumeKey = true;
                           }

                           boolean handled;
                           char charkey;
                           if (event != 32768) {
                              int result = inputScreen.processKeyEvent(event, key, keycode, time);
                              handled = (result & 65536) != 0;
                              charkey = (char)(result & 65535);
                           } else {
                              charkey = isVolumeKey ? volumeKey : key;
                              handled = inputScreen.dispatchKeyEvent(32768, charkey, keycode, time);
                           }

                           int status = Keypad.status(keycode);
                           if (!handled) {
                              int amount = 0;
                              int trackwheelEvent = 519;
                              switch (charkey) {
                                 case '\u0081':
                                    amount = -1;
                                    break;
                                 case '\u0082':
                                    amount = 1;
                                    break;
                                 case '\u0083':
                                    amount = -1;
                                    status |= 1;
                                    break;
                                 case '\u0084':
                                    amount = 1;
                                    status |= 1;
                                    break;
                                 case '\u0090':
                                    trackwheelEvent = 516;
                                    amount = 1;
                              }

                              if (amount != 0) {
                                 handled = inputScreen.dispatchNavigationEvent(trackwheelEvent, 0, amount, status | 1073741824, message.getData1());
                              }
                           }

                           if (!handled) {
                              this.keyNotHandled(message);
                           }
                        }
                        break;
                     case 516:
                     case 517:
                     case 519:
                     default:
                        int status = message.getData0();
                        int time = message.getData1();
                        UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
                        if (listener != null) {
                           listener.onUserTrackwheel(this, inputScreen, event, message.getSubMessage(), status, time);
                        }

                        if ((status & 128) == 0 || DeviceInfo.isInHolster()) {
                           inputScreen.dispatchNavigationEvent(event, 0, message.getSubMessage(), status | 1073741824, time);
                        }
                  }

                  updateDisplayOutsideNormalPaint = true;
                  break;
               case 26:
                  this.notifyUserInputEventListener(3);
                  if (inputScreen != null) {
                     int subMessage = message.getSubMessage();
                     int x = subMessage & 65535;
                     int y = subMessage >> 16 & 65535;
                     int status = message.getData0();
                     int time = message.getData1();
                     this.setStylusPos(x, y);
                     UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
                     if (listener != null) {
                        listener.onUserStylus(this, inputScreen, event, x, y, status, time);
                     }

                     inputScreen.dispatchStylusEvent(event, x, y, status, time);
                     this.setStylusPos(-1, -1);
                     updateDisplayOutsideNormalPaint = true;
                  }
                  break;
               case 27:
                  this.notifyUserInputEventListener(4);
                  if (inputScreen != null) {
                     int status = message.getData0();
                     int time = message.getData1();
                     if ((status & 128) == 0) {
                        int subMessage = message.getSubMessage();
                        int dx = (short)subMessage;
                        int dy = -(subMessage >> 16);
                        UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
                        if (listener != null) {
                           listener.onUserTrackball(this, inputScreen, event, dx, dy, status, time);
                        }

                        inputScreen.dispatchNavigationEvent(event, dx, dy, status | 536870912, time);
                        updateDisplayOutsideNormalPaint = true;
                     }
                  }
            }
         }

         if (!this._isMidlet) {
            if (this._appInvalid.width > 0 && this._appInvalid.height > 0) {
               Thread thread = Thread.currentThread();
               if (thread instanceof UiModalEventThread) {
                  UiModalEventThread modalThread = (UiModalEventThread)thread;
                  if (modalThread.getScreen().getUiEngine() == null) {
                     return;
                  }
               }

               suppressPaint = false;
            }

            if (!suppressPaint) {
               MessageQueue messageQueue = Process.currentProcess().getMessageQueue();
               if (messageQueue.getSize() >= 5) {
                  suppressPaint = true;
               }
            }

            if (!suppressPaint) {
               this.doPainting();
               if (updateDisplayOutsideNormalPaint) {
                  this.updateDisplay();
               }
            }
         }
      }

      if (this._isMidlet) {
         ((MIDletApplication)this._app).updateScreen();
      }
   }

   @Override
   public final int getScreenCount() {
      return this._screenList.getLocalScreenCount();
   }

   @Override
   public final int getTopmostGlobalPriority() {
      for (int i = 0; i < this._screenList.getScreenCount(); i++) {
         Screen next = this._screenList.getScreen(i);
         if (!next.isGlobal()) {
            break;
         }

         if (this.isInProcess(next)) {
            return GlobalScreenManager.getGlobalPriority(next);
         }
      }

      for (int i = 0; i < this._screenList.getHiddenGlobalScreens().length; i++) {
         Screen next = this._screenList.getHiddenGlobalScreens()[i];
         if (this.isInProcess(next)) {
            return GlobalScreenManager.getGlobalPriority(next);
         }
      }

      return 50;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 5961289116197897667L) {
         if (data0 == 3) {
            if (this._app.isForeground() && object0 instanceof GlobalRepaintNotifier) {
               GlobalRepaintNotifier notifier = (GlobalRepaintNotifier)object0;
               synchronized (notifier) {
                  boolean screenExtentChanged = notifier.isExtentChanged();
                  if (screenExtentChanged) {
                     this.layoutOutOfProcessGlobalScreens();
                  }

                  this._somethingInvalid = true;
                  notifier.reset();
               }

               this.doPainting();
            }
         } else if ((object0 == null || object0 instanceof XYRect) && (object1 == null || object1 instanceof Integer)) {
            this.globalScreenEventCommon(data0, (XYRect)object0, (Integer)object1);
         }
      }

      if (guid == 1286649819098130486L && this.equals(GlobalScreenManager.getPaintControlEngine())) {
         this._somethingInvalid = true;
         this.doPainting();
      }

      if (guid == 3160755958169834551L && this.equals(GlobalScreenManager.getPaintControlEngine()) && object0 instanceof Screen[]) {
         this.injectLocalWrappedScreens((Screen[])object0);
      }

      if (guid == -4394903006263251010L) {
         this.applyFont();
      }

      if (guid == 2573494863350550132L) {
         this.applyTheme();
      }

      if (guid == -4394903006263251010L || guid == 2573494863350550132L || guid == -7464003439710973532L || guid == 7207871974803693937L) {
         this.relayout();
      }

      if (guid == 3596208183088439728L || guid == 8877632280522743328L) {
         this.appInvalidate(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
      }

      if (guid == -2650018024822507413L) {
         Display.$init();
      }
   }

   @Override
   public final void inHolster() {
      if (this._app.isForeground()) {
         this.notifyVisibleScreens(false);
      }

      this.notifyVisibleGlobalScreens(false);
   }

   @Override
   public final void outOfHolster() {
      if (this._app.isForeground()) {
         this.notifyVisibleScreens(true);
         if (this._inputScreen != null && !this._inputScreen.equals(GlobalScreenManager.getScreenWithFocus())) {
            UiEngineImpl$FocusNotifier focusNotifier = null;
            synchronized (GlobalScreenManager.getLock()) {
               this._inputScreen = null;
               focusNotifier = this.setInputScreen();
            }

            if (focusNotifier != null) {
               focusNotifier.run();
            }

            this.repaint();
         }
      }

      this.notifyVisibleGlobalScreens(true);
   }

   @Override
   public final boolean isTopmostGlobal() {
      for (int i = 0; i < this._screenList.getScreenCount(); i++) {
         Screen next = this._screenList.getScreen(i);
         if (!next.isGlobal()) {
            break;
         }

         if (this.isInProcess(next)) {
            return true;
         }
      }

      for (int i = 0; i < this._screenList.getHiddenGlobalScreens().length; i++) {
         Screen next = this._screenList.getHiddenGlobalScreens()[i];
         if (this.isInProcess(next)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final Screen getActiveScreen() {
      assertIpcOrDependency();
      return this._screenList.getTopmostLocalScreen();
   }

   @Override
   public final void removeUserInputEventListener(UserInputEventListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._userInputEventListener = ListenerUtilities.removeListener(this._userInputEventListener, listener);
   }

   @Override
   public final void relayout() {
      int numScreens = this._screenList.getScreenCount();
      _layoutGeneration++;

      for (int i = 0; i < numScreens; i++) {
         Screen screen = this._screenList.getScreen(i);
         if (this.isInProcess(screen)) {
            screen.invalidateLayout0();
            screen.doLayout();
         }
      }

      this.repaint();
   }

   @Override
   public final void repaint() {
      this._appInvalid.set(0, 0, Display.getWidth(), Display.getHeight());
      this._somethingInvalid = true;
      this.doPainting();
   }

   @Override
   public final void popScreen(Screen screen) {
      this.assertHaveEventLock();
      if (screen.getUiEngine() != this) {
         throw new IllegalArgumentException("popScreen: UiEngine is " + screen.getUiEngine() + " != " + this);
      }

      if (this._isInPopScreen) {
         throw new IllegalStateException("popScreen: Already in popScreen");
      }

      if (screen.getPushMethod() == 1) {
         throw new IllegalStateException("Cannot mix queueStatus-dismissStatus with pushGlobalScreen-popScreen.");
      }

      UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
      if (listener != null) {
         listener.onPopScreen(this, screen);
      }

      Object[] listeners = this._uiEngineListener;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            ((UiEngineListener)listeners[i]).onPopScreen(screen);
         }
      }

      Throwable exception = null;
      UiEngineImpl$FocusNotifier focusNotifier = null;
      Screen screenExposed = null;
      this._isInPopScreen = true;
      boolean topmost = this._screenList.isTopmost(screen);
      if (!screen.isGlobal()) {
         if (topmost) {
            Graphics.resetOverlays();
            Graphics.releaseGraphics(screen);
         }

         this._appInvalid.unionNoEmpty(screen.getExtent());
         this._somethingInvalid = true;
         synchronized (GlobalScreenManager.getLock()) {
            this._screenList.pop(screen);
         }
      } else {
         synchronized (GlobalScreenManager.getLock()) {
            screen.setDismissing(true);
            GlobalScreenManager.dismiss(screen, this, false, Process.currentProcess().getProcessId());
            screen.setDismissing(false);
            this._screenList.copyGlobalScreens();
         }
      }

      synchronized (GlobalScreenManager.getLock()) {
         if (screen == this._inputScreen) {
            focusNotifier = this.setInputScreen();
         }
      }

      if (topmost) {
         screenExposed = this._screenList.getTopmostScreen();
         if (screenExposed != null && (!this.isInProcess(screenExposed) || !screenExposed.isGlobal() && !this._app.isForeground())) {
            screenExposed = null;
         }
      }

      this._isInPopScreen = false;
      if (screenExposed != null) {
         try {
            screenExposed.callOnExposed();
         } catch (TestException e) {
            exception = e;
         } catch (Throwable e) {
            exception = e;
         }
      }

      if (this._app.isForeground()) {
         try {
            this.notifyPaintableScreens(true);
         } catch (TestException e) {
            exception = e;
         } catch (Throwable e) {
            exception = e;
         }

         try {
            this.notifyVisibleScreens(true);
         } catch (TestException e) {
            exception = e;
         } catch (Throwable e) {
            exception = e;
         }
      }

      try {
         this.notifyPaintableGlobalScreens(true);
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      try {
         this.notifyVisibleGlobalScreens(true);
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      try {
         screen.doPaintabilityWalk(false);
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      try {
         screen.doVisibilityWalk(false);
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      if (focusNotifier != null) {
         try {
            focusNotifier.run();
         } catch (TestException e) {
            exception = e;
         } catch (Throwable e) {
            exception = e;
         }
      }

      try {
         screen.callOnUiEngineAttached(false);
      } catch (TestException e) {
         exception = e;
      } catch (Throwable e) {
         exception = e;
      }

      if (screen.isGlobal()) {
         this.notifyNewlyExposedGlobalScreens();
      } else {
         GlobalScreenManager.updateInjectedScreens(this);
      }

      screen.setUiEngine(null);
      this.releaseBackingStore(screen);
      if (!this._app.isEventThread() && this._app.hasEventThread() && this._app.getMessageQueueSize() == 0) {
         this._app.invokeLater(Ui.nullRunnable);
      }

      if (exception != null) {
         if (!(exception instanceof RuntimeException)) {
            throw (Error)exception;
         } else {
            throw (RuntimeException)exception;
         }
      }
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void fastReset() {
      Trackball.updateDeviceWithAppSettings();
   }

   @Override
   public final void backlightStateChange(boolean on) {
      boolean val = on;
      if ((Display.getProperties() & 16384) != 0) {
         if (val && !this._app.isForeground()) {
            val = false;
         }

         this.notifyVisibleScreens(val);
         this.notifyVisibleGlobalScreens(on);
      }
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final void pushModalScreen(Screen screen) {
      if (!this._app.isEventThread()) {
         throw new RuntimeException("pushModalScreen called by a non-event thread");
      }

      if (!this.equals(GlobalScreenManager.getPaintControlEngine()) && this.getApplication().isForeground()) {
         GlobalScreenManager.setForegroundEngine(this);
      }

      this.pushScreen(screen);
      this.doPainting();
      UiModalEventThread thread = new UiModalEventThread(screen);
      this._app.startModalEventThread(thread);
   }

   @Override
   public final void pushScreen(Screen screen) {
      this.assertHaveEventLock();
      Throwable exception = null;
      UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
      if (listener != null) {
         listener.onPushScreen(this, screen);
      }

      Object[] listeners = this._uiEngineListener;
      if (listeners != null) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            ((UiEngineListener)listeners[i]).onPushScreen(screen);
         }
      }

      screen.setUiEngine(this);
      screen.setPushMethod(0);
      Graphics.resetOverlays();
      Screen screenObscured = null;
      UiEngineImpl$FocusNotifier focusNotifier = null;
      synchronized (GlobalScreenManager.getLock()) {
         screenObscured = this._screenList.getTopmostLocalScreen();
         this._screenList.push(screen);
         if (screen.acceptsInput()) {
            focusNotifier = this.setInputScreen();
         }
      }

      if (_layoutGeneration != screen._layoutGeneration) {
         screen.invalidateLayout0();
         screen._layoutGeneration = _layoutGeneration;
      }

      try {
         screen.doLayout();
      } catch (TestException e) {
         if (exception == null) {
            exception = e;
         }
      } catch (Throwable e) {
         if (exception == null) {
            exception = e;
         }
      }

      this._screenList.updateExtent(screen);
      GlobalScreenManager.updateInjectedScreens(this);
      this._somethingInvalid = true;

      try {
         screen.callOnUiEngineAttached(true);
      } catch (TestException e) {
         if (exception == null) {
            exception = e;
         }
      } catch (Throwable e) {
         if (exception == null) {
            exception = e;
         }
      }

      try {
         if (this._app.isForeground() && this._screenList.isTopmost(screen)) {
            screen.callOnExposed();
         } else {
            screen.callOnObscured();
         }
      } catch (TestException e) {
         if (exception == null) {
            exception = e;
         }
      } catch (Throwable e) {
         if (exception == null) {
            exception = e;
         }
      }

      if (this._app.isForeground()) {
         try {
            this.notifyPaintableScreens(true);
         } catch (TestException e) {
            if (exception == null) {
               exception = e;
            }
         } catch (Throwable e) {
            if (exception == null) {
               exception = e;
            }
         }

         try {
            this.notifyVisibleScreens(true);
         } catch (TestException e) {
            if (exception == null) {
               exception = e;
            }
         } catch (Throwable e) {
            if (exception == null) {
               exception = e;
            }
         }
      }

      if (screenObscured != null && screenObscured.isUiEngineAttached()) {
         try {
            screenObscured.callOnObscured();
         } catch (TestException e) {
            if (exception == null) {
               exception = e;
            }
         } catch (Throwable e) {
            if (exception == null) {
               exception = e;
            }
         }
      }

      if (focusNotifier != null) {
         try {
            focusNotifier.run();
         } catch (TestException e) {
            if (exception == null) {
               exception = e;
            }
         } catch (Throwable e) {
            if (exception == null) {
               exception = e;
            }
         }
      }

      screen.invalidate();
      if (!this._app.isEventThread() && this._app.hasEventThread()) {
         this.doPainting();
      }

      if (exception != null) {
         if (!(exception instanceof RuntimeException)) {
            throw (Error)exception;
         } else {
            throw (RuntimeException)exception;
         }
      }
   }

   @Override
   public final void updateDisplay() {
      UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
      if (listener != null) {
         listener.onUpdateDisplay(this);
      }

      if (this._screenList.getScreenCount() > 0) {
         Graphics.updateDisplay();
      }
   }

   @Override
   public final int getLayoutGeneration() {
      return _layoutGeneration;
   }

   @Override
   public final void setStylusPos(int x, int y) {
      this._stylusX = x;
      this._stylusY = y;
   }

   @Override
   public final void suspendPainting(boolean suspend) {
      synchronized (this._screenList) {
         if (suspend) {
            this._suspendPainting++;
         } else {
            if (this._suspendPainting == 0) {
               throw new IllegalStateException("suspendPainting: extra suspend");
            }

            this._suspendPainting--;
         }
      }
   }

   @Override
   public final int getStylusX() {
      return this._stylusX;
   }

   @Override
   public final int getStylusY() {
      return this._stylusY;
   }

   @Override
   public final boolean isPaintingSuspended() {
      return this._suspendPainting > 0;
   }

   @Override
   public final int getGlobalPriority(Screen screen) {
      return GlobalScreenManager.getGlobalPriority(screen);
   }

   @Override
   public final void addUserInputEventListener(UserInputEventListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._userInputEventListener = ListenerUtilities.addListener(this._userInputEventListener, listener);
   }

   static final Screen getTopmostScreen() {
      return _uiEngine.getActiveScreen();
   }

   private final UiEngineImpl$FocusNotifier setInputScreen() {
      UiEngineImpl$FocusNotifier focusNotifier = null;
      GlobalScreenManager.assertHaveLock();
      Screen newInputScreen = null;

      for (int i = this._screenList.getScreenCount() - 1; i >= 0; i--) {
         Screen screen = this._screenList.getScreen(i);
         if (screen.acceptsInput()) {
            newInputScreen = screen;
            break;
         }
      }

      if (newInputScreen != null && !this.isInProcess(newInputScreen)) {
         newInputScreen = null;
      }

      if (!this._app.isForeground()
         && (!DeviceInfo.isInHolster() || !ApplicationManager.getApplicationManager().isInHolsterInputProcess())
         && newInputScreen != null
         && !newInputScreen.isGlobal()) {
         newInputScreen = null;
      }

      if (this._inputScreen != newInputScreen) {
         focusNotifier = new UiEngineImpl$FocusNotifier(this._inputScreen, newInputScreen, this.getApplication());
         UiInternalListener listener = GlobalScreenManager.getUiInternalListener();
         if (listener != null) {
            listener.onFocus(this, this._inputScreen, newInputScreen);
         }

         Object[] listeners = this._uiEngineListener;
         if (listeners != null) {
            for (int i = listeners.length - 1; i >= 0; i--) {
               ((UiEngineListener)listeners[i]).onFocus(this._inputScreen, newInputScreen);
            }
         }

         this._inputScreen = newInputScreen;
         Screen oldScreenWithFocus = GlobalScreenManager.getScreenWithFocus();
         if (newInputScreen != null || oldScreenWithFocus != null && oldScreenWithFocus.getUiEngine() == this) {
            GlobalScreenManager.setScreenWithFocus(newInputScreen);
         }
      }

      return focusNotifier;
   }

   private final int getLocalInProcessGlobalScreenCount() {
      return this._screenList.getLocalScreenCount() + this._screenList.getInProcessGlobalScreenCount();
   }

   private final void notifyNewlyHiddenGlobalScreens() {
      Screen[] hiddenGlobals = this._screenList.getHiddenGlobalScreens();

      for (int i = 0; i < hiddenGlobals.length; i++) {
         hiddenGlobals[i].doPaintabilityWalk(false);
         hiddenGlobals[i].doVisibilityWalk(false);
      }
   }

   private final void notifyVisibleGlobalScreens(boolean visible) {
      if (visible && (DeviceInfo.isInHolster() || !Backlight.isEnabled() && (Display.getProperties() & 16384) != 0)) {
         visible = false;
      }

      int lv = this._screenList.getScreenCount() - 1;
      if (visible) {
         XYRect rect = new XYRect();
         int screenWidth = Display.getWidth();
         int screenHeight = Display.getHeight();

         while (lv >= 0) {
            Screen screen = this._screenList.getScreen(lv);
            if (screen.isGlobal()) {
               screen.doVisibilityWalk(visible);
               this.getScreenExtent(screen, rect);
               if (!this.isScreenTransparent(screen) && rect.contains(0, 0, screenWidth, screenHeight)) {
                  visible = false;
                  lv--;
                  break;
               }
            }

            lv--;
         }
      }

      for (; lv >= 0; lv--) {
         Screen screen = this._screenList.getScreen(lv);
         if (screen.isGlobal()) {
            screen.doVisibilityWalk(visible);
         }
      }
   }

   private final void notifyPaintableScreens(boolean paintable) {
      int lv = this._screenList.getScreenCount() - 1;
      if (paintable) {
         XYRect rect = new XYRect();
         int screenWidth = Display.getWidth();
         int screenHeight = Display.getHeight();

         while (lv >= 0) {
            Screen screen = this._screenList.getScreen(lv);
            screen.doPaintabilityWalk(paintable);
            this.getScreenExtent(screen, rect);
            if (!this.isScreenTransparent(screen) && rect.contains(0, 0, screenWidth, screenHeight)) {
               paintable = false;
               lv--;
               break;
            }

            lv--;
         }
      }

      while (lv >= 0) {
         this._screenList.getScreen(lv).doPaintabilityWalk(paintable);
         lv--;
      }

      this._bottomScreen.doPaintabilityWalk(paintable);
   }

   private final void notifyPaintableGlobalScreens(boolean paintable) {
      int lv = this._screenList.getScreenCount() - 1;
      if (paintable) {
         XYRect rect = new XYRect();
         int screenWidth = Display.getWidth();
         int screenHeight = Display.getHeight();

         while (lv >= 0) {
            Screen screen = this._screenList.getScreen(lv);
            if (screen.isGlobal()) {
               screen.doPaintabilityWalk(paintable);
               this.getScreenExtent(screen, rect);
               if (!this.isScreenTransparent(screen) && rect.contains(0, 0, screenWidth, screenHeight)) {
                  paintable = false;
                  lv--;
                  break;
               }
            }

            lv--;
         }
      }

      for (; lv >= 0; lv--) {
         Screen screen = this._screenList.getScreen(lv);
         if (screen.isGlobal()) {
            screen.doPaintabilityWalk(paintable);
         }
      }
   }

   private final void notifyPaintableWrappedLocalScreens(boolean paintable) {
      int lv = this._screenList.getScreenCount() - 1;
      if (paintable) {
         XYRect rect = new XYRect();
         int screenWidth = Display.getWidth();
         int screenHeight = Display.getHeight();

         while (lv >= 0) {
            Screen screen = this._screenList.getScreen(lv);
            if (!screen.isGlobal() && screen instanceof UiEngineImpl$ProxyScreen) {
               screen.doPaintabilityWalk(paintable);
               this.getScreenExtent(screen, rect);
               if (!this.isScreenTransparent(screen) && rect.contains(0, 0, screenWidth, screenHeight)) {
                  paintable = false;
                  lv--;
                  break;
               }
            }

            lv--;
         }
      }

      for (; lv >= 0; lv--) {
         Screen screen = this._screenList.getScreen(lv);
         if (screen.isGlobal() && screen instanceof UiEngineImpl$ProxyScreen) {
            screen.doPaintabilityWalk(paintable);
         }
      }
   }

   private final void releaseBackingStore(Screen screen) {
      BackingStore backingStore = screen.getBackingStore();
      XYRect extent = screen.getExtent();
      if (backingStore != null && this._fullScreenRect.equals(extent)) {
         if (!screen.isTransparent() && !screen.isTransparentBorder()) {
            GlobalScreenManager.returnBackingStore(backingStore);
         } else {
            GlobalScreenManager.returnTransparentBackingStore(backingStore);
         }

         screen.clearBackingStore();
      }
   }

   private final void keyNotHandled(Message message) {
      int key = message.getData0() >> 16;
      if (Arrays.getIndex(UnhandledGlobalKeyListener.GLOBAL_KEYS, key) != -1) {
         message.setDevice(56);
         message.post();
      } else {
         if (key == 261) {
            Keypad.changeShiftState(message.getEvent());
         }
      }
   }

   private final void notifyNewlyExposedGlobalScreens() {
      int screenCount = this._screenList.getScreenCount();

      for (int i = this._screenList.getLocalScreenCount(); i < screenCount; i++) {
         Screen globalScreen = this._screenList.getScreen(i);
         if (globalScreen instanceof UiEngineImpl$ProxyScreen) {
            globalScreen = ((UiEngineImpl$ProxyScreen)globalScreen).getWrappedScreen();
         }

         globalScreen.invalidate();
      }
   }

   private final void globalScreenEventCommon(int type, XYRect appInvalid, Integer processId) {
      if (processId != null && processId == Process.currentProcess().getProcessId()) {
         UiEngineImpl$FocusNotifier focusNotifier = null;
         synchronized (GlobalScreenManager.getLock()) {
            focusNotifier = this.setInputScreen();
         }

         if (focusNotifier != null) {
            focusNotifier.run();
         }

         this.globalScreenEventPainting(type, appInvalid);
      } else {
         Screen oldTopmost = this._screenList.getTopmostScreen();
         synchronized (GlobalScreenManager.getLock()) {
            this._screenList.copyGlobalScreens();
         }

         Screen newTopmost = this._screenList.getTopmostScreen();
         UiEngineImpl$FocusNotifier focusNotifier = null;
         synchronized (GlobalScreenManager.getLock()) {
            focusNotifier = this.setInputScreen();
         }

         if (type == 1) {
            if ((this._app.isForeground() || oldTopmost != null && oldTopmost.isGlobal())
               && oldTopmost != null
               && oldTopmost != newTopmost
               && oldTopmost.getUiEngine() != null
               && oldTopmost.isUiEngineAttached()) {
               oldTopmost.callOnObscured();
            }

            if (focusNotifier != null) {
               focusNotifier.setEnableFocusNotificationForIM(true);
            }
         } else if (type == 2) {
            if ((this._app.isForeground() || newTopmost != null && newTopmost.isGlobal()) && newTopmost != null && oldTopmost != newTopmost) {
               newTopmost.callOnExposed();
            }

            if (focusNotifier != null) {
               focusNotifier.setEnableFocusNotificationForIM(true);
            }

            this.notifyNewlyExposedGlobalScreens();
         }

         if (focusNotifier != null) {
            focusNotifier.run();
         }

         this.globalScreenEventPainting(type, appInvalid);
      }
   }

   private final void applyTheme() {
      int numScreens = this._screenList.getScreenCount();

      for (int lv = 0; lv < numScreens; lv++) {
         Screen screen = this._screenList.getScreen(lv);
         if (this.isInProcess(screen)) {
            screen.applyTheme();
         }
      }
   }

   private final void layoutOutOfProcessGlobalScreens() {
      int screenCount = this._screenList.getScreenCount();

      for (int i = this._screenList.getLocalScreenCount(); i < screenCount; i++) {
         Screen globalScreen = this._screenList.getScreen(i);
         if (globalScreen instanceof UiEngineImpl$ProxyScreen) {
            globalScreen.invalidateLayout0();
            globalScreen.doLayout();
         }
      }
   }

   private final void paintWrappedLocalScreens() {
      boolean screenChanged = false;
      boolean sendNotification = false;

      for (int i = this._screenList.getLocalScreenCount() - 1; i >= 0; i--) {
         Screen local = this._screenList.getScreen(i);
         XYRect extent = local.getExtent();
         XYRect cachedExtent = this._screenList.getExtent(i);
         if (!extent.equals(cachedExtent)) {
            screenChanged = true;
            local.invalidateLayout0();
            local.doLayout();
            local.invalidateInternal();
            local.clearBackingStore();
            cachedExtent.set(extent);
         }

         if (!this._appInvalid.isEmpty() && local.getExtent().intersects(this._appInvalid)) {
            screenChanged = true;
            local.invalidateInternal(this._appInvalid.x, this._appInvalid.y, this._appInvalid.width, this._appInvalid.height);
            if (local.getContentRect().contains(this._appInvalid)) {
               this._appInvalid.set(0, 0, 0, 0);
            }
         }

         if (!local.getInvalid().isEmpty()) {
            XYRect tmp = Ui.getTmpXYRect();
            tmp.set(local.getInvalid());
            tmp.intersect(local.getContentRect());
            if (this._screenList.highestOpaqueRegionContaining(tmp) == i || this._screenList.highestOpaqueRegionContaining(tmp) == -1) {
               screenChanged = true;
            }

            Ui.returnTmpXYRect(tmp);
         }

         if (screenChanged) {
            sendNotification = true;
            local.doPaint0(true, true);
            screenChanged = false;
         }
      }

      if (sendNotification && GlobalScreenManager.getPaintControlEngine().getApplication().getMessageQueueSize() < 5) {
         RIMGlobalMessagePoster.postGlobalEvent(
            GlobalScreenManager.getPaintControlEngine().getApplication().getProcessId(), 1286649819098130486L, 3, 0, null, null
         );
      }
   }

   private final boolean isInProcess(Screen screen) {
      return screen instanceof UiEngineImpl$ProxyScreen ? false : screen != null && this != null && this.equals(screen.getUiEngineImpl());
   }

   private final boolean isScreenDisplayed(Screen screen) {
      if (screen instanceof UiEngineImpl$ProxyScreen) {
         screen = ((UiEngineImpl$ProxyScreen)screen).getWrappedScreen();
      }

      return screen.isDisplayed();
   }

   static final UiEngineImpl getUiEngine() {
      assertIpcOrDependency();
      if (_uiEngine == null) {
         Application app = Application.getApplication();
         if (app == null) {
            throw new RuntimeException("No application instance");
         }

         synchronized (app.getAppEventLock()) {
            if (_uiEngine == null) {
               _uiEngine = new UiEngineImpl(app);
            }
         }
      }

      return _uiEngine;
   }

   public static final void assertIpcOrDependency() {
      if (!CodeStore.isPartOfCurrentApp(TraceBack.getCallingModule(3)) && !ApplicationControl.isIPCAllowed(true)) {
         throw new ControlledAccessException("Unauthorized attempt to attach to this application");
      }
   }

   private final void applyFont() {
      int numScreens = this._screenList.getScreenCount();

      for (int lv = 0; lv < numScreens; lv++) {
         Screen screen = this._screenList.getScreen(lv);
         if (this.isInProcess(screen)) {
            screen.applyFont();
         }
      }
   }

   private final XYRect getScreenExtent(Screen screen) {
      if (screen instanceof UiEngineImpl$ProxyScreen) {
         screen = ((UiEngineImpl$ProxyScreen)screen).getWrappedScreen();
      }

      return screen.getExtent();
   }

   private final void getScreenExtent(Screen screen, XYRect extent) {
      if (screen instanceof UiEngineImpl$ProxyScreen) {
         screen = ((UiEngineImpl$ProxyScreen)screen).getWrappedScreen();
      }

      screen.getExtent(extent);
   }

   private final XYRect getScreenInvalid(Screen screen) {
      if (screen instanceof UiEngineImpl$ProxyScreen) {
         ((UiEngineImpl$ProxyScreen)screen).updateInvalid();
      }

      return screen.getInvalid();
   }

   private final boolean isScreenTransparent(Screen screen) {
      if (screen instanceof UiEngineImpl$ProxyScreen) {
         screen = ((UiEngineImpl$ProxyScreen)screen).getWrappedScreen();
      }

      return screen.isTransparent();
   }

   private final boolean isScreenTransparentBorder(Screen screen) {
      if (screen instanceof UiEngineImpl$ProxyScreen) {
         screen = ((UiEngineImpl$ProxyScreen)screen).getWrappedScreen();
      }

      return screen.isTransparentBorder();
   }

   private final void notifyVisibleScreens(boolean visible) {
      if (visible && (DeviceInfo.isInHolster() || !Backlight.isEnabled() && (Display.getProperties() & 16384) != 0)) {
         visible = false;
      }

      int lv = this._screenList.getScreenCount() - 1;
      if (visible) {
         XYRect rect = new XYRect();
         int screenWidth = Display.getWidth();
         int screenHeight = Display.getHeight();

         while (lv >= 0) {
            Screen screen = this._screenList.getScreen(lv);
            screen.doVisibilityWalk(visible);
            this.getScreenExtent(screen, rect);
            if (!this.isScreenTransparent(screen) && rect.contains(0, 0, screenWidth, screenHeight)) {
               visible = false;
               lv--;
               break;
            }

            lv--;
         }
      }

      while (lv >= 0) {
         this._screenList.getScreen(lv).doVisibilityWalk(visible);
         lv--;
      }
   }

   private final void paintInProcessGlobalScreens() {
      boolean screenChanged = false;
      boolean screenExtentChanged = false;
      int screenCount = this._screenList.getScreenCount();

      for (int i = this._screenList.getLocalScreenCount(); i < screenCount; i++) {
         Screen globalScreen = this._screenList.getScreen(i);
         if (this.isInProcess(globalScreen)) {
            XYRect extent = globalScreen.getExtent();
            XYRect cachedExtent = this._screenList.getExtent(i);
            if (!extent.equals(cachedExtent) || globalScreen.isClearBackingStore()) {
               screenExtentChanged = true;
               globalScreen.invalidateLayout0();
               globalScreen.doLayout();
               globalScreen.invalidate();
               globalScreen.clearBackingStore();
               cachedExtent.set(extent);
               globalScreen.setClearBackingStore(false);
            }

            if (screenExtentChanged || !globalScreen.getInvalid().isEmpty()) {
               screenChanged = true;
               globalScreen.doPaint0(true, true);
            }
         }
      }

      if (screenChanged) {
         this._globalRepaintNotifier.post(screenExtentChanged);
      }
   }

   static final Screen getTopmostLocalGlobalScreen() {
      return _uiEngine.getActiveLocalGlobalScreen();
   }

   private final void globalScreenEventPainting(int type, XYRect appInvalid) {
      if (this._app.isForeground()) {
         this.notifyPaintableScreens(true);
         this.notifyVisibleScreens(true);
         if (type == 1) {
            this.notifyNewlyHiddenGlobalScreens();
         } else if (type == 2) {
            this.notifyNewlyExposedGlobalScreens();
         }

         if (appInvalid != null) {
            this._appInvalid.unionNoEmpty(appInvalid);
            this._somethingInvalid = true;
         }

         this.doPainting();
      }

      if (this.equals(GlobalScreenManager.getPaintControlEngine()) && !this._app.isForeground()) {
         this.notifyPaintableScreens(true);
         if (appInvalid != null) {
            this._appInvalid.unionNoEmpty(appInvalid);
         }

         this._somethingInvalid = true;
         this.doPainting();
      }
   }

   private final boolean isMessageValid(Screen[] screens) {
      if (screens.length == 0) {
         return false;
      }

      UiEngineImpl$ProxyScreen next = (UiEngineImpl$ProxyScreen)screens[0];
      UiEngineImpl engine = next.getWrappedScreen().getUiEngineImpl();
      return engine != null && engine.getApplication().isForeground();
   }

   private final void checkForExtentChanges() {
      int screenCount = this._screenList.getScreenCount();

      for (int i = 0; i < screenCount; i++) {
         Screen screen = this._screenList.getScreen(i);
         XYRect extent = this.getScreenExtent(screen);
         XYRect cached = this._screenList.getExtent(i);
         if (!extent.equals(cached)) {
            this._appInvalid.unionNoEmpty(extent);
            this._appInvalid.unionNoEmpty(cached);
            this._screenList.updateExtent(screen);
         }
      }

      this._appInvalid.intersect(this._fullScreenRect);
   }

   private final void gatherInvalidRegions(XYRect accumulator, boolean transparentOnly, int startIndex) {
      int screenCount = this._screenList.getScreenCount();
      boolean includeTransparentBorder = false;
      XYRect appInvalidIn = Ui.getTmpXYRect();
      appInvalidIn.set(accumulator);

      for (int i = startIndex; i < screenCount; i++) {
         if (i != -1) {
            Screen screen = this._screenList.getScreen(i);
            XYRect invalid = Ui.getTmpXYRect();
            invalid.set(this.getScreenInvalid(screen));
            if (this.isScreenTransparentBorder(screen) && this._screenList.getScreenCount() > 0) {
               for (Screen below = this._screenList.getScreenBelow(screen); below != null; below = this._screenList.getScreenBelow(below)) {
                  XYRect invalidBelow = this.getScreenInvalid(below);
                  if (!invalidBelow.isEmpty()) {
                     invalid.unionNoEmpty(invalidBelow);
                     includeTransparentBorder = true;
                  }
               }

               if (!appInvalidIn.isEmpty()) {
                  invalid.unionNoEmpty(appInvalidIn);
                  includeTransparentBorder = true;
               }
            }

            if (invalid.isEmpty()) {
               Ui.returnTmpXYRect(invalid);
            } else {
               if (!transparentOnly || this.isScreenTransparent(screen) || includeTransparentBorder) {
                  accumulator.unionNoEmpty(invalid);
                  includeTransparentBorder = false;
               }

               Ui.returnTmpXYRect(invalid);
            }
         }
      }

      Ui.returnTmpXYRect(appInvalidIn);
      accumulator.intersect(this._fullScreenRect);
   }

   private UiEngineImpl(Application app) {
      this._app = app;
      if (app instanceof MIDletApplication) {
         this._isMidlet = true;
      }

      app.setMessageListener(this);
      app.addGlobalEventListenerInternal(this);
      app.addHolsterListener(this);
      if ((Display.getProperties() & 16384) != 0) {
         app.addSystemListener(this);
      }

      this._bottomScreen = new UiEngineImpl$BottomScreen();
      this._bottomScreen.setUiEngine(this);
      this._bottomScreen.doLayout();
      if (app.isForeground() && ((ApplicationProcess)Process.getProcess(app.getProcessId())).acceptsForeground()) {
         GlobalScreenManager.setForegroundEngine(this);
      }

      synchronized (GlobalScreenManager.getLock()) {
         this._screenList.copyGlobalScreens();
      }
   }
}
