package net.rim.tid.im.conv;

import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.itie.IComponent;
import net.rim.vm.Array;

public class ConversionEvent extends Event {
   private KeyEvent _initialEvent;
   private StringBuffer _keyChars = (StringBuffer)(new Object());
   private StringBuffer _optionalKeyChars = (StringBuffer)(new Object());
   private int _keyCode;
   private int _modifiers;
   private byte _lookupOpenStyle;
   private boolean _kbType;
   private char[] _tempBuf = new char[2];
   public static final int COMPOSED_TEXT_NOT_CHANGED;
   public static final int COMPOSED_TEXT_CHANGED;
   public static final int CARET_POSITION_CHANGED;
   public static final int LOOKUP_POSITION_CHANGED;
   public static final boolean KEYBOARD_TYPE_FULL;
   public static final boolean KEYBOARD_TYPE_REDUCED;
   public static final byte LOOKUP_OPEN_DEFAULT;
   public static final byte LOOKUP_OPEN_DELAYED;
   public static final byte LOOKUP_OPEN_IMMEDIATE;

   public ConversionEvent(KeyEvent initialEvent, StringBuffer keyChars, int keyCode, IComponent src, boolean kbType) {
      super(src, 0, 0);
      this.init(initialEvent, keyChars, keyCode, src, kbType);
   }

   public void init(KeyEvent initialEvent, StringBuffer keyChars, int keyCode, IComponent src, boolean kbType) {
      this.setID(0);
      this._initialEvent = initialEvent;
      this.setKeyChars(keyChars);
      this._keyCode = keyCode;
      this._kbType = kbType;
      if (initialEvent != null) {
         this._modifiers = initialEvent.getModifiers();
      } else {
         this._modifiers = 0;
      }

      this._lookupOpenStyle = 0;
      this._optionalKeyChars.setLength(0);
   }

   public void consumeInitialEvent() {
      if (this._initialEvent != null) {
         this._initialEvent.consume();
      }
   }

   @Override
   public void consume() {
      super.consume();
      if (this._initialEvent != null) {
         this._initialEvent.consume();
      }
   }

   public boolean getKBType() {
      return this._kbType;
   }

   public Event getInitialEvent() {
      return this._initialEvent;
   }

   public StringBuffer getKeyChars() {
      return this._keyChars;
   }

   public void addOptionalKeyChars(String keyChars) {
      this._optionalKeyChars.append(keyChars);
   }

   public void addOptionalKeyChars(StringBuffer keyChars) {
      this._optionalKeyChars.append(keyChars);
   }

   public StringBuffer getOptionalKeyChars() {
      return this._optionalKeyChars;
   }

   public void setKeyChars(StringBuffer keyChars) {
      this._keyChars.setLength(0);
      if (keyChars != null) {
         int len = keyChars.length();
         if (len > this._tempBuf.length) {
            Array.resize(this._tempBuf, len);
         }

         keyChars.getChars(0, len, this._tempBuf, 0);
         this._keyChars.append(this._tempBuf, 0, len);
      }
   }

   public int getKeyCode() {
      return this._keyCode;
   }

   public void setKeyCode(int newKeyCode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setID(int newID) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setModifiers(int newModifiers) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public int getModifiers() {
      return this._modifiers;
   }

   public void setLookupOpenStyle(byte style) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public byte getLookupOpenStyle() {
      return this._lookupOpenStyle;
   }
}
