package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PTTKeyHandler;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.gprs.GSM230Filter;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.awt.event.InputMethodEvent;

public final class DTMFEchoField extends BasicEditField {
   private int _maxDrawCount;
   private int _drawWidth;
   private Image _cursor;
   private int _dtmfSentCount;
   private static final int CURSOR_PADDING = PhoneUtilities.smallMonoScreen() ? 1 : 3;

   public DTMFEchoField() {
      super(null, null, Integer.MAX_VALUE, 1170935903116337152L);
      this.setFilter(new DTMFEchoFilter());
      this.setPasteable(false);
   }

   public final void setCursor(Image cursor) {
      if (this._cursor != cursor) {
         this._cursor = cursor;
         this.invalidate();
      }
   }

   @Override
   public final void setDirty(boolean dirty) {
   }

   @Override
   public final void setMuddy(boolean muddy) {
   }

   public final void onDTMFBufferCleared() {
      if (this.getTextLength() != 0) {
         this.setText("");
      }

      this._dtmfSentCount = 0;
   }

   public final String getDTMFString() {
      return this.getText();
   }

   @Override
   public final boolean isEditable() {
      if (this._drawWidth == 0) {
         return false;
      }

      LiveCall liveCall = this.getCurrentCall();
      return liveCall != null && liveCall.acceptingDTMF();
   }

   @Override
   protected final void layout(int width, int height) {
      this._drawWidth = width;
      super.layout(Integer.MAX_VALUE, height);
   }

   @Override
   protected final void paint(Graphics graphics) {
      Font font = graphics.getFont();
      String text = this.getDisplayText().toString();
      int length = text.length();
      if (this._maxDrawCount == 0) {
         int minCharWidth = Math.min(font.getAdvance('1'), font.getAdvance('I'));
         this._maxDrawCount = (this._drawWidth + minCharWidth - 1) / minCharWidth;
      }

      if (length > this._maxDrawCount) {
         text = text.substring(1, text.length());
         this.setText(text);
         length--;
      }

      int width = font.getAdvance(text, 0, length);
      if (this._cursor == null) {
         int x = Math.min(0, this._drawWidth - width);
         graphics.drawText(text, 0, length, x, 0, 0, -1);
      } else {
         int fontHeight = font.getHeight();
         int cursorWidth = this._cursor.getWidth(fontHeight, fontHeight);
         int cursorHeight = this._cursor.getHeight(fontHeight, fontHeight);
         int x = Math.min(0, this._drawWidth - cursorWidth - CURSOR_PADDING - width);
         x += graphics.drawText(text, 0, length, x, 0, 0, -1);
         this._cursor.paint(graphics, x + CURSOR_PADDING, 0, cursorWidth, cursorHeight);
      }
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
   }

   @Override
   public final void getFocusRectPhantom(XYRect rect) {
   }

   @Override
   public final void getFocusRect(XYRect rect) {
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      switch (event) {
         case 512:
            break;
         case 513:
         case 514:
         case 515:
         default:
            PTTKeyHandler handler = (PTTKeyHandler)ApplicationRegistry.getApplicationRegistry().get(-7975050928526187730L);
            if (handler != null && handler.isPTTKey(keycode)) {
               return 0;
            }
      }

      switch (event) {
         case 513:
            if (PhoneUtilities.isSpeakerPhoneKey(keycode) || PhoneUtilities.isMuteKey(keycode)) {
               return 0;
            }
            break;
         case 514:
            if (Keypad.key(keycode) != 27) {
               return 65536;
            }

            return super.processKeyEvent(event, key, keycode, time);
         case 515:
         case 520:
            break;
         default:
            return super.processKeyEvent(event, key, keycode, time);
      }

      if (event == 515) {
         if (Keypad.key(keycode) == 36) {
            return 0;
         }

         LiveCall liveCall = this.getCurrentCall();
         if (liveCall != null && liveCall.acceptingDTMF()) {
            liveCall.stopDTMFTone();
         }

         char keyChar = Keypad.map(keycode);
         if (keyChar == '#' || Keypad.getAltedChar(keyChar) == '#') {
            String dtmfTones = this.getDTMFString() + '#';
            int len = dtmfTones.length();
            boolean matched = GSM230Filter.getCode(dtmfTones) != -1;
            if (!matched && len > 5) {
               dtmfTones = dtmfTones.substring(len - 5, len);
               matched = GSM230Filter.getCode(dtmfTones) != -1;
            }

            String tonesToDial = dtmfTones;
            if (matched) {
               Application.getApplication().invokeLater(new DTMFEchoField$1(this, tonesToDial), 50, false);
            }
         }
      }

      int keyVal = Keypad.key(keycode);
      return keyVal == 18 ? 0 : super.processKeyEvent(event, key, keycode, time);
   }

   @Override
   protected final boolean keyStatus(int keycode, int time) {
      LiveCall liveCall = this.getCurrentCall();
      if (liveCall != null && liveCall.acceptingDTMF()) {
         int status = Keypad.status(keycode);
         if ((status & 65) != 0) {
            char key = Keypad.map(keycode);
            char altedChar = Keypad.getAltedChar(key);
            if (PhoneNumberServices.isDTMFKey(altedChar)) {
               liveCall.startDTMFTone(altedChar);
            }
         }
      }

      return super.keyStatus(keycode, time);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      LiveCall liveCall = this.getCurrentCall();
      if (liveCall != null && liveCall.acceptingDTMF()) {
         char key = Keypad.map(keycode);
         char altedChar = Keypad.getAltedChar(key);
         if (!PhoneUtilities.isQwertyReducedKeyboard() && key >= 'A' && key <= 'Z') {
            liveCall.startDTMFTone(key);
         } else {
            int status = Keypad.status(keycode);
            if ((status & 65) != 0) {
               char letterChar = CharacterUtilities.toUpperCase(Keypad.getUnaltedChar(key), 1701707776);
               if (!PhoneUtilities.isQwertyReducedKeyboard() && letterChar >= 'A' && letterChar <= 'Z') {
                  liveCall.startDTMFTone(letterChar);
               }
            } else if (PhoneNumberServices.isDTMFKey(altedChar)) {
               liveCall.startDTMFTone(altedChar);
            } else if (PhoneNumberServices.isDTMFKey(key)) {
               liveCall.startDTMFTone(key);
            }
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key != 127 && Keypad.getAltedChar(key) != 127) {
         return super.keyChar(key, status, time);
      }

      if (PhoneUtilities.cdmaTypeNetwork()) {
         super.backspace();
      }

      return true;
   }

   @Override
   public final int inputMethodTextChanged(InputMethodEvent event) {
      int ret = super.inputMethodTextChanged(event);
      LiveCall liveCall = this.getCurrentCall();
      if (liveCall != null && liveCall.acceptingDTMF()) {
         int committedLength = this.getCommittedTextLength();
         if (committedLength > this._dtmfSentCount) {
            StringBufferGap buffer = this.getDisplayText();

            while (this._dtmfSentCount < committedLength) {
               char ch = buffer.charAt(this._dtmfSentCount++);
               if (ch >= 'A' && ch <= 'Z' && PhoneUtilities.isQwertyReducedKeyboard()) {
                  liveCall.sendDTMFTone(ch);
               }
            }
         }
      }

      return ret;
   }

   private final LiveCall getCurrentCall() {
      return (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
   }
}
