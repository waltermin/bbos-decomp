package net.rim.tid.awt.event;

import net.rim.tid.awt.Event;
import net.rim.tid.itie.IComponent;

public final class KeyEvent extends InputEvent {
   private int _keyCode;
   private char _keyChar;
   private boolean _inputEvent;
   private boolean _componentDispatch;
   public static final int VK_ENTER;
   public static final int VK_BACK_SPACE;
   public static final int VK_TAB;
   public static final int VK_ESCAPE;
   public static final int VK_SPACE;
   public static final int VK_END;
   public static final int VK_HOME;
   public static final int VK_LEFT;
   public static final int VK_UP;
   public static final int VK_RIGHT;
   public static final int VK_DOWN;
   public static final int VK_DELETE;
   public static final int VK_SHIFT_X;
   public static final int VK_SYM;
   public static final int VK_HELP;
   public static final int VK_CHAR_CONTEXT;
   public static final int VK_UNDEFINED;
   public static final char CHAR_UNDEFINED;
   public static final int KEY_PRESSED;
   public static final int KEY_RELEASED;
   public static final int KEY_REPEATED;
   public static final int KEY_ROLLED;
   public static final int THUMB_CLICK;

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
