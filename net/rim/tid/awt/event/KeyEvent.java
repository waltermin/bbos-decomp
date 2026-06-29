package net.rim.tid.awt.event;

import net.rim.tid.awt.Event;
import net.rim.tid.itie.IComponent;

public final class KeyEvent extends InputEvent {
   private int _keyCode;
   private char _keyChar;
   private boolean _inputEvent;
   private boolean _componentDispatch;
   public static final int VK_ENTER = 10;
   public static final int VK_BACK_SPACE = 8;
   public static final int VK_TAB = 9;
   public static final int VK_ESCAPE = 27;
   public static final int VK_SPACE = 32;
   public static final int VK_END = 134;
   public static final int VK_HOME = 133;
   public static final int VK_LEFT = 131;
   public static final int VK_UP = 129;
   public static final int VK_RIGHT = 132;
   public static final int VK_DOWN = 130;
   public static final int VK_DELETE = 127;
   public static final int VK_SHIFT_X = 137;
   public static final int VK_SYM = 128;
   public static final int VK_HELP = 156;
   public static final int VK_CHAR_CONTEXT = 22;
   public static final int VK_UNDEFINED = 0;
   public static final char CHAR_UNDEFINED = '\u0000';
   public static final int KEY_PRESSED = 513;
   public static final int KEY_RELEASED = 515;
   public static final int KEY_REPEATED = 514;
   public static final int KEY_ROLLED = 519;
   public static final int THUMB_CLICK = 516;

   public KeyEvent(IComponent source, int eventID, long when, int modifiers, int kCode, char kChar, int eMask) {
      super(source, eventID, when, modifiers, eMask | Event.KEY_EVENT_MASK);
      this._keyCode = kCode;
      this._keyChar = kChar;
   }

   public final void init(IComponent aSource, int aId, long when, int modifiers, int kCode, char kChar) {
      this.init(aSource, aId, when, modifiers, kCode, kChar, true);
   }

   public final void init(IComponent aSource, int aId, long when, int modifiers, int kCode, char kChar, boolean aInputEvent) {
      super._source = aSource;
      super._ID = aId;
      super._when = when;
      super._modifiers = modifiers;
      this._keyCode = kCode;
      this._keyChar = kChar;
      super._consumed = false;
      this._inputEvent = aInputEvent;
      this._componentDispatch = false;
   }

   public final boolean isInputEvent() {
      return this._inputEvent;
   }

   public final char getKeyChar() {
      return this._keyChar;
   }

   public final int getKeyCode() {
      return this._keyCode;
   }

   public final void setKeyChar(char keyChar) {
      this._keyChar = keyChar;
   }

   public final void setKeyCode(int keyCode) {
      this._keyCode = keyCode;
   }

   public final void setComponentDispatchEnabled(boolean enable) {
      this._componentDispatch = enable;
   }

   @Override
   public final boolean isComponentDispatchEnabled() {
      return this._componentDispatch;
   }
}
