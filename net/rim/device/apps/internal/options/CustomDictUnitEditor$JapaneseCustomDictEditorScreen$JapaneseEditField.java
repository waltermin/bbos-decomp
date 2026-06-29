package net.rim.device.apps.internal.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.KeyEvent;
import net.rim.tid.im.SLControlObject;

class CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField extends BasicEditField {
   private boolean _readingType;
   CustomDictUnitEditor$JapaneseCustomDictEditorScreen _parent;
   private final CustomDictUnitEditor$JapaneseCustomDictEditorScreen this$1;

   public CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField(
      CustomDictUnitEditor$JapaneseCustomDictEditorScreen _1, boolean readingType, long style, CustomDictUnitEditor$JapaneseCustomDictEditorScreen parent
   ) {
      super(null, null, 1000000, style);
      this.this$1 = _1;
      this._readingType = readingType;
      this._parent = parent;
   }

   private boolean isChar(char ch) {
      return ch > ' ';
   }

   private boolean isControl(char ch, int modifiers) {
      switch (ch) {
         case '\u0000':
         case '\b':
         case '\t':
         case '\n':
         case '\u001b':
         case '\u0081':
         case '\u0082':
         case '\u0083':
         case '\u0084':
         case '\u0085':
         case '\u0086':
         case '\u0089':
            return true;
         case ' ':
            if (!this._readingType && this.getTextLength() > 0) {
               return true;
            }

            return false;
         default:
            return false;
      }
   }

   @Override
   public void dispatchEvent(Event rEvent) {
      if (!(rEvent instanceof Object)) {
         super.dispatchEvent(rEvent);
      } else {
         KeyEvent event = (KeyEvent)rEvent;
         if (!event.isInputEvent()) {
            super.dispatchEvent(rEvent);
         } else {
            int keycode = event.getKeyCode();
            int modifiers = event.getModifiers();
            char key = Keypad.getLayout().getKeyChars(keycode, modifiers).charAt(0);
            if (this.isChar(key) || this.isControl(key, modifiers)) {
               super.dispatchEvent(rEvent);
               if (!rEvent.isConsumed()) {
                  if (rEvent.getID() == 516) {
                     Menu menu = (Menu)(new Object(65536));
                     ContextMenu cmenu = this.getContextMenu();
                     menu.add(cmenu, true);
                     menu.add(
                        new CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField$1(
                           this, CommonResource.getBundle().getString(18), 332288, 1000
                        )
                     );
                     Application.getApplication().invokeLater(new CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField$2(this, menu));
                     rEvent.consume();
                  }
               }
            }
         }
      }
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      int result = super.processKeyEvent(event, key, keycode, time);
      if (this._readingType && event == 513) {
         int pos = this.getComposedTextEnd();
         if (pos > 0 && (this.charAt(pos - 1) < 'a' || this.charAt(pos - 1) > 'z')) {
            SLControlObject controlObject = (SLControlObject)this.getInputContext().getInputMethodControlObject();
            if (controlObject != null) {
               controlObject.actionPerformed(-77, null);
            }
         }
      }

      return result;
   }

   static void access$800(CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField x0, Menu x1) {
      x0.onMenuDismissed(x1);
   }
}
