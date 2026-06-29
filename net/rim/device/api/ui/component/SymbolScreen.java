package net.rim.device.api.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.tid.im.SLControlObject;

class SymbolScreen extends PopupScreen implements TextInputDialog {
   protected Manager _fm;
   protected SymbolScreen$Header _header;
   protected SymbolScreen$SymbolField _symbols;
   protected Field _targetField;
   protected BasicEditField _targetEditField;
   SLControlObject _controlObject;
   private long _inputMethodID;
   private Object _additionalSymbolData;
   private int _keysTotal = 1;
   private int _pendingPageNumber = 0;
   private int _pageTimer = -1;
   private byte[] _properties;
   private static final boolean DEBUG = false;
   private static SymbolScreen _screen;
   public static final int ASCII_FULLWIDTH_SHIFT = 65248;
   private static final int JA_ENCODING = 2;
   private static final int JA_CODE_MSB = 3;
   private static final int JA_CODE_LSB = 4;
   private static final int DISPLAY_WIDTH_ALLOWED = Display.getWidth() * 84 / 100;

   protected SymbolScreen() {
      super(new VerticalFieldManager(), 131072);
      this._fm = this.getDelegate();
      this._fm.add(this._symbols = this.getSymbolField());
      int horizontal = Display.getWidth() * 2 / 100;
      int vertical = Display.getHeight() * 2 / 100;
      this.setMargin(vertical, horizontal, vertical, horizontal);
   }

   protected void doModal(Field field) {
      this._inputMethodID = this.getInputContext().getActiveInputMethodID();
      int fieldCount = this._fm.getFieldCount();
      if (this._inputMethodID == 512) {
         if (fieldCount == 1) {
            this._fm.deleteAll();
            if (this._header == null) {
               this._header = new SymbolScreen$Header(this);
            }

            this._fm.add(this._header);
            this._fm.add(this._symbols);
         }
      } else if (fieldCount > 1) {
         this._fm.deleteAll();
         this._fm.add(this._symbols);
      }

      this._controlObject = (SLControlObject)this.getInputContext().getInputMethodControlObject();
      if (this._controlObject != null) {
         this._additionalSymbolData = this._controlObject.getAdditionalSymbolData(0);
         this._properties = this._controlObject.getIMProperties((byte)3);
      } else {
         this._additionalSymbolData = null;
      }

      this._targetField = field;
      if (field instanceof BasicEditField) {
         this._targetEditField = (BasicEditField)field;
      }

      UiEngine engine = Ui.getUiEngine();
      if (this._properties != null && this._inputMethodID == 512 && this._properties.length > 4) {
         this._header.setHeader(this._properties[2] & 255, ((this._properties[3] & 255) << 8) + (this._properties[4] & 255), false);
      }

      this._symbols.reset();
      Screen screen = field.getScreen();
      if (!screen.isDisplayed()) {
         this.cleanup();
      } else {
         if (!screen.isGlobal()) {
            engine.pushModalScreen(this);
         } else {
            engine.pushGlobalScreen(this, engine.getGlobalPriority(screen), 5);
         }

         this.cleanup();
      }
   }

   private void cleanup() {
      this._targetField = null;
      this._targetEditField = null;
   }

   @Override
   public void close() {
      super.close();
      if (this._inputMethodID == 512 && this._properties.length > 4) {
         boolean update = false;
         if (this._properties[2] != (byte)this._header.getEncoding()) {
            this._properties[2] = (byte)this._header.getEncoding();
            update = true;
         }

         int code = this._header.getCode();
         if (this._properties[3] != (byte)(code >>> 8)) {
            this._properties[3] = (byte)(code >>> 8);
            update = true;
         }

         if (this._properties[4] != (byte)code) {
            this._properties[4] = (byte)code;
            update = true;
         }

         if (update) {
            this._controlObject.actionPerformed(-78, null);
         }
      }
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      if (this._inputMethodID == 512 && event == 513) {
         int keyChar = keycode >>> 16;
         Screen screen = this._targetField.getScreen();
         switch (keyChar) {
            case 16:
               break;
            case 17:
            default:
               this.dispatchTrackwheelEvent(519, -1, 0, time);
               return 65536;
            case 18:
               this.dispatchTrackwheelEvent(519, 1, 0, time);
               return 65536;
            case 19:
               return screen.processKeyEvent(event, key, keycode, time);
         }
      }

      return super.processKeyEvent(event, key, keycode, time);
   }

   protected SymbolScreen$SymbolField getSymbolField() {
      return new SymbolScreen$SymbolField(this);
   }

   protected static SymbolScreen getSymbolScreen() {
      if (_screen == null) {
         _screen = new SymbolScreen();
      }

      return _screen;
   }

   Field getTarget() {
      return this._targetField;
   }

   public boolean isEmpty(TextField field) {
      return this._symbols.isEmpty(field);
   }

   public static void show(Field field) {
      Application.getApplication().invokeLater(new SymbolScreen$1(field));
   }

   protected static boolean contains(int c) {
      return getSymbolScreen()._symbols.mapContains(c);
   }

   static int access$310(SymbolScreen x0) {
      return x0._keysTotal--;
   }
}
