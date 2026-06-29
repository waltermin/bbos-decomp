package net.rim.device.api.ui.component;

import net.rim.device.api.i18n.ResourceBundle;

class SymbolScreen$Encoding extends LabelField {
   SymbolScreen$Header _parent;
   private int _encodingType;
   private String[] _encodingStrings;
   private final SymbolScreen this$0;

   public SymbolScreen$Encoding(SymbolScreen _1, long style, int encoding, SymbolScreen$Header parent) {
      super("", style);
      this.this$0 = _1;
      this._parent = parent;
      ResourceBundle bundle = ResourceBundle.getBundle(8562590855522002223L, "net.rim.device.internal.resource.Input");
      this._encodingStrings = new String[2];
      this._encodingStrings[0] = bundle.getString(19);
      this._encodingStrings[1] = bundle.getString(18);
      this.setEncoding(encoding);
   }

   public void setEncoding(int encodingType) {
      if (encodingType < 0 || encodingType >= this._encodingStrings.length) {
         encodingType = 0;
      }

      this._encodingType = encodingType;
      this.setText(this._encodingStrings[this._encodingType]);
      if (this._parent._code != null) {
         this._parent._code.setType(this._encodingType);
      }
   }

   public int getEncoding() {
      return this._encodingType;
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      this.setEncoding(++this._encodingType);
      this._parent.update();
      return true;
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      if (event != 513) {
         return 0;
      }

      char ch = (char)(keycode >>> 16);
      switch (ch) {
         case '\n':
            this.setEncoding(++this._encodingType);
            this._parent.update();
            return 0;
         case '\u001b':
         case ' ':
            this.this$0.close();
            return 0;
         default:
            this.this$0._symbols.keyDown(keycode, time);
            return 0;
      }
   }
}
