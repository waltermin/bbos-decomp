package net.rim.device.api.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.vm.Message;
import net.rim.vm.TraceBack;

public class UiApplication extends Application implements UiEngine {
   private UiEngineImpl _uiEngine;
   private int _style;
   private int _stylusX = -1;
   private int _stylusY = -1;
   public static final int STYLE_NO_SCREEN_CAPABLE;

   public final void publicProcessMessage(Message message) {
      this._uiEngine.processMessage(this.getAppEventLock(), message, false);
   }

   public final void popScreen() {
      String warning = "Warning: Calling obsolete function UiApplication.popScreen()";
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      TraceBack.printStackTrace(new PrintStream(out));
      warning = warning + out;
      System.out.println(warning);
      EventLogger.logEvent(-7509200465648525729L, warning.getBytes(), 0);
      this.popScreen(this.getActiveScreen());
   }

   public void setStyle(int style) {
      this._style &= ~style;
      this._style |= style;
   }

   public int getStyle() {
      return this._style;
   }

   public final void doPainting() {
      this._uiEngine.doPainting();
   }

   @Override
   public int getStylusY() {
      return this._stylusY;
   }

   @Override
   public final void suspendPainting(boolean suspend) {
      this._uiEngine.suspendPainting(suspend);
   }

   @Override
   public final boolean isPaintingSuspended() {
      return this._uiEngine.isPaintingSuspended();
   }

   @Override
   public int getStylusX() {
      return this._stylusX;
   }

   @Override
   public final void updateDisplay() {
      this._uiEngine.updateDisplay();
   }

   @Override
   public int getGlobalPriority(Screen screen) {
      return this._uiEngine.getGlobalPriority(screen);
   }

   @Override
   public final void popScreen(Screen screen) {
      this._uiEngine.popScreen(screen);
   }

   @Override
   public final void pushGlobalScreen(Screen screen, int priority, int flags) {
      this._uiEngine.pushGlobalScreen(screen, priority, flags);
   }

   @Override
   public final void pushScreen(Screen screen) {
      this._uiEngine.pushScreen(screen);
   }

   @Override
   public final void pushModalScreen(Screen screen) {
      this._uiEngine.pushModalScreen(screen);
   }

   @Override
   public final int getScreenCount() {
      return this._uiEngine.getScreenCount();
   }

   @Override
   public final void repaint() {
      this._uiEngine.repaint();
   }

   @Override
   public final void relayout() {
      this._uiEngine.relayout();
   }

   @Override
   public final Screen getActiveScreen() {
      return this._uiEngine.getActiveScreen();
   }

   @Override
   public final void queueStatus(Screen screen, int priority, boolean inputRequired) {
      this._uiEngine.queueStatus(screen, priority, inputRequired);
   }

   @Override
   public final void pushGlobalScreen(Screen screen, int priority, boolean inputRequired) {
      this._uiEngine.pushGlobalScreen(screen, priority, inputRequired);
   }

   @Override
   public final void dismissStatus(Screen screen) {
      this._uiEngine.dismissStatus(screen);
   }

   @Override
   public void addUserInputEventListener(UserInputEventListener listener) {
      this._uiEngine.addUserInputEventListener(listener);
   }

   @Override
   public int getLayoutGeneration() {
      return this._uiEngine.getLayoutGeneration();
   }

   @Override
   public void removeUserInputEventListener(UserInputEventListener listener) {
      this._uiEngine.removeUserInputEventListener(listener);
   }

   @Override
   public void setStylusPos(int x, int y) {
      this._stylusX = x;
      this._stylusY = y;
   }

   @Override
   public boolean isTopmostGlobal() {
      return this._uiEngine.isTopmostGlobal();
   }

   @Override
   public int getTopmostGlobalPriority() {
      return this._uiEngine.getTopmostGlobalPriority();
   }

   protected UiApplication() {
      this._uiEngine = UiEngineImpl.getUiEngine();
   }

   @Override
   public void activate() {
      super.activate();
      Trackball.updateDeviceWithAppSettings();
   }

   @Override
   protected boolean acceptsForeground() {
      return true;
   }

   public static final UiApplication getUiApplication() {
      return (UiApplication)Application.getApplication();
   }
}
