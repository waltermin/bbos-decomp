package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

public class TextField extends Item {
   MIDPEditField _edit;
   public static final int ANY = 0;
   public static final int EMAILADDR = 1;
   public static final int NUMERIC = 2;
   public static final int PHONENUMBER = 3;
   public static final int URL = 4;
   public static final int DECIMAL = 5;
   public static final int PASSWORD = 65536;
   public static final int UNEDITABLE = 131072;
   public static final int SENSITIVE = 262144;
   public static final int NON_PREDICTIVE = 524288;
   public static final int INITIAL_CAPS_WORD = 1048576;
   public static final int INITIAL_CAPS_SENTENCE = 2097152;
   public static final int CONSTRAINT_MASK = 65535;

   public TextField(String label, String text, int maxSize, int constraints) {
      synchronized (Application.getEventLock()) {
         this._edit = new MIDPEditField(label, text, maxSize, constraints, this);
         this._edit.setCookie(this);
         this.setPeer(this._edit);
      }
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      this._edit.setChangeListener(null);
      this._edit.setChangeListener(changeListener);
      return this._edit;
   }

   public String getString() {
      return this._edit.getString();
   }

   public void setString(String text) {
      this._edit.setString(text);
   }

   public int getChars(char[] data) {
      return this._edit.getChars(data);
   }

   public void setChars(char[] data, int offset, int length) {
      this._edit.setChars(data, offset, length);
   }

   public void insert(String src, int position) {
      this._edit.insert(src, position);
   }

   public void insert(char[] data, int offset, int length, int position) {
      this._edit.insert(data, offset, length, position);
   }

   public void delete(int offset, int length) {
      this._edit.delete(offset, length);
   }

   public int getMaxSize() {
      return this._edit.getMaxSize();
   }

   public int setMaxSize(int maxSize) {
      return this._edit.setMaxSize(maxSize);
   }

   public int size() {
      return this._edit.size();
   }

   public int getCaretPosition() {
      return this._edit.getCaretPosition();
   }

   public void setConstraints(int constraints) {
      this._edit.setConstraints(constraints);
   }

   public int getConstraints() {
      return this._edit.getConstraints();
   }

   @Override
   public String getLabel() {
      return this._edit.getLabel();
   }

   @Override
   public void setLabel(String label) {
      this._edit.setLabel(label);
   }

   public void setInitialInputMode(String characterSubset) {
   }
}
