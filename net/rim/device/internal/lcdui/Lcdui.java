package net.rim.device.internal.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;

public final class Lcdui {
   private boolean _paintCallback;
   private Callbacks _paintCallbacks;
   private Screen _screen;
   private XYRect _invalid = new XYRect();
   private int _callbackType = 0;
   private int _callbackState = 0;
   private Callbacks _keyCallbacks;
   private int _keycode;
   private CommandListener _commandListener;
   private Command _command;
   private Displayable _displayable;
   private Runnable _runnable;
   private Object _notifier;
   private Graphics _midpGraphics;
   private boolean _suppressKeyEvents;
   private int _currentKeyStates;
   private int _keyDownHistory;
   public static final long MIDLET_NOTIFICATION_GUID;
   private static Lcdui _impl = new Lcdui();
   private static final int NONE;
   public static final int KEYDOWN;
   public static final int KEYREPEAT;
   public static final int KEYUP;
   public static final int KEYDOWNUP;
   private static final int COMMANDACTION;
   private static final int INVOKELATER;
   public static final int USER_INVOKELATER;
   private static final int FRESH;
   private static final int STALE;
   private static final Object _eventDeliveryLock = new Object();

   public static final void init() {
      _impl._paintCallback = false;
      _impl._paintCallbacks = null;
      _impl._screen = null;
      _impl._callbackType = 0;
      _impl._keyCallbacks = null;
      _impl._commandListener = null;
      _impl._command = null;
      _impl._displayable = null;
      _impl._suppressKeyEvents = false;
      _impl._currentKeyStates = 0;
      _impl._keyDownHistory = 0;
      _impl._midpGraphics = null;
   }

   public static final void setMIDPGraphics(Graphics graphics) {
      _impl._midpGraphics = graphics;
   }

   public static final Graphics getMIDPGraphics() {
      return _impl._midpGraphics;
   }

   public static final void setSuppressKeyEvents(boolean b) {
      _impl._suppressKeyEvents = b;
   }

   public static final boolean getSuppressKeyEvents() {
      return _impl._suppressKeyEvents;
   }

   public static final int getKeyDownHistory() {
      return _impl._keyDownHistory;
   }

   public static final int getCurrentKeyStates() {
      return _impl._currentKeyStates;
   }

   public static final void setKeyDownHistory(int keyDownHistory) {
      _impl._keyDownHistory = keyDownHistory;
   }

   public static final void setCurrentKeyStates(int currentKeyStates) {
      _impl._currentKeyStates = currentKeyStates;
   }

   public static final void setInvokeLaterCallback(Runnable runnable, Object notifier) {
      _impl._callbackType = 7;
      _impl._runnable = runnable;
      _impl._notifier = notifier;
   }

   public static final void setPaintCallback(net.rim.device.api.ui.Graphics g, Screen s, Callbacks callback) {
      XYRect clip = g.getClippingRect();
      _impl._paintCallback = true;
      _impl._screen = s;
      _impl._paintCallbacks = callback;
      _impl._invalid.union(clip);
   }

   public static final void setKeyCallback(int type, Callbacks callback, int keycode) {
      _impl._callbackType = type;
      _impl._keyCallbacks = callback;
      _impl._keycode = keycode;
   }

   public static final void setCommandActionCallback(CommandListener commandListener, Command command, Displayable displayable) {
      _impl._callbackState = 0;
      _impl._callbackType = 6;
      _impl._commandListener = commandListener;
      _impl._command = command;
      _impl._displayable = displayable;
   }

   public static final void runCallback() {
      _impl.runCallback0();
   }

   private final void runCallback0() {
      int callbackType = this._callbackType;
      this._callbackType = 0;
      switch (callbackType) {
         case 2:
         case 3:
         case 4:
         case 5:
         default:
            this._keyCallbacks.keyCallback(callbackType, this._keycode);
            this._keyCallbacks = null;
            return;
         case 6:
            if (this._commandListener != null && this._callbackState == 0) {
               this._callbackState = 1;
               if (this._displayable instanceof Canvas) {
                  synchronized (getEventDeliveryLock()) {
                     this._commandListener.commandAction(this._command, this._displayable);
                  }
               } else {
                  this._commandListener.commandAction(this._command, this._displayable);
               }
            }

            this._commandListener = null;
            this._command = null;
            this._displayable = null;
            return;
         case 7:
            if (this._runnable != null) {
               synchronized (getEventDeliveryLock()) {
                  this._runnable.run();
               }

               if (this._notifier != null) {
                  synchronized (this._notifier) {
                     this._notifier.notifyAll();
                     return;
                  }
               }
            }
         case 1:
      }
   }

   public static final Object getEventDeliveryLock() {
      return _eventDeliveryLock;
   }

   public static final void runPaintCallback() {
      _impl.runPaintCallback0();
   }

   private final void runPaintCallback0() {
      if (this._paintCallback) {
         net.rim.device.api.ui.Graphics g;
         synchronized (Application.getEventLock()) {
            g = this._screen.getGraphics();
         }

         g.pushContext(this._invalid, 0, 0);
         this._paintCallbacks.paintCallback(g);
         this._screen.updateDisplay();
         this._paintCallback = false;
         this._screen = null;
         this._paintCallbacks = null;
         this._invalid.set(0, 0, 0, 0);
      }
   }
}
