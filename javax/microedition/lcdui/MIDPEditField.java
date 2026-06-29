package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.MathUtilities;

class MIDPEditField extends VerticalFieldManager {
   int _constraints;
   BasicEditField _field;
   FieldChangeListener _fieldChangeListener;
   Item _item;
   private static TextFilter _phoneFilter = new MIDPEditField$1();

   public MIDPEditField(String label, String text, int maxSize, int constraints, Item item) {
      super(1152921504606846976L);
      this._item = item;
      if (!this.validateText(text, constraints)) {
         throw new IllegalArgumentException();
      }

      if (!this.init(label, text, maxSize, constraints)) {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void setChangeListener(FieldChangeListener listener) {
      this._fieldChangeListener = listener;
      this._field.setChangeListener(listener);
   }

   private boolean validateText(String text, int constraints) {
      if (text != null && text.length() != 0) {
         AbstractString abstractString = AbstractStringWrapper.createInstance(text);
         TextFilter filter = null;
         switch (constraints & 65535) {
            case 2:
            default:
               if (text.equals("-")) {
                  return false;
               } else {
                  try {
                     Integer.parseInt(text);
                     return true;
                  } catch (NumberFormatException nfe) {
                     return false;
                  }
               }
            case 3:
               filter = _phoneFilter;
            case 1:
            case 4:
               if (filter != null && !filter.validate(abstractString)) {
                  return false;
               }

               return true;
            case 5:
               if (text.equals("-")) {
                  return false;
               } else {
                  try {
                     Float.valueOf(text);
                     return true;
                  } catch (NumberFormatException nfe) {
                     return false;
                  }
               }
         }
      } else {
         return true;
      }
   }

   private final String augmentLabel(String label) {
      if (label != null && label.length() > 0) {
         label = label + ' ';
      }

      return label;
   }

   private boolean init(String label, String text, int maxSize, int constraints) {
      if (maxSize > 0 && (text == null || text.length() <= maxSize)) {
         int constraintMaxValue = 5;
         if ((constraints & 65535) < 0 || (constraints & 65535) > constraintMaxValue) {
            throw new IllegalArgumentException();
         }

         if (!this.validateText(text, constraints)) {
            return false;
         }

         label = this.augmentLabel(label);
         this._constraints = constraints;
         if ((constraints & 65536) != 0) {
            this._field = new PasswordEditField(label, text, maxSize, 0);
            if ((constraints & 65535) == 2) {
               this._field.setFilter(TextFilter.get(1));
            }
         } else {
            switch (constraints & 65535) {
               case -1:
                  break;
               case 0:
               case 1:
               case 4:
               default:
                  this._field = new AutoTextEditField(label, text, maxSize, 0);
                  break;
               case 2:
                  this._field = new EditField(label, text, maxSize, 83886080);
                  break;
               case 3:
                  this._field = new EditField(label, text, maxSize, 0);
                  this._field.setFilter(_phoneFilter);
                  break;
               case 5:
                  this._field = new EditField(label, text, maxSize, 251658240);
            }
         }

         if ((constraints & 131072) != 0) {
            this._field.setEditable(false);
         }

         boolean hadFocus = this.getFieldWithFocus() != null;
         this.deleteAll();
         this.add(this._field);
         if (hadFocus) {
            this._field.setFocus();
         }

         this._field.setChangeListener(this._fieldChangeListener);
         this._field.setCookie(this._item);
         return true;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getString() {
      synchronized (Application.getEventLock()) {
         return this._field.getText();
      }
   }

   public void setString(String text) {
      synchronized (Application.getEventLock()) {
         if (text == null || text.length() <= this._field.getMaxSize() && this.validateText(text, this._constraints)) {
            this._field.setText(text);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public int getChars(char[] data) {
      synchronized (Application.getEventLock()) {
         String text = this.getString();
         int len = text.length();
         text.getChars(0, len, data, 0);
         return len;
      }
   }

   public void setChars(char[] data, int offset, int length) {
      synchronized (Application.getEventLock()) {
         String text = null;
         if (data != null) {
            try {
               text = new String(data, offset, length);
            } catch (StringIndexOutOfBoundsException e) {
               throw new ArrayIndexOutOfBoundsException();
            }
         }

         this.setString(text);
      }
   }

   public void insert(String src, int position) {
      synchronized (Application.getEventLock()) {
         if (this._field.getTextLength() + src.length() > this._field.getMaxSize()) {
            throw new IllegalArgumentException();
         }

         if (!this.validateText(src, this._constraints)) {
            throw new IllegalArgumentException();
         }

         position = MathUtilities.clamp(0, position, this._field.getTextLength());
         this._field.setCursorPosition(position);
         this._field.insert(src);
      }
   }

   public void insert(char[] data, int offset, int length, int position) {
      synchronized (Application.getEventLock()) {
         if (offset >= 0 && length >= 0 && offset <= data.length - length) {
            this.insert(new String(data, offset, length), position);
         } else {
            throw new ArrayIndexOutOfBoundsException();
         }
      }
   }

   public void delete(int offset, int length) {
      synchronized (Application.getEventLock()) {
         if (length > 0) {
            String currentText = this._field.getText();
            StringBuffer textAfterDelete = new StringBuffer(currentText);
            if (!this.validateText(textAfterDelete.delete(offset, offset + length).toString(), this._constraints)) {
               throw new IllegalArgumentException();
            }
         }

         if (offset >= 0 && length >= 0 && offset + length <= this._field.getTextLength()) {
            this._field.setCursorPosition(offset + length);
            this._field.backspace(length);
         } else {
            throw new StringIndexOutOfBoundsException();
         }
      }
   }

   public int getMaxSize() {
      synchronized (Application.getEventLock()) {
         return this._field.getMaxSize();
      }
   }

   public int setMaxSize(int maxSize) {
      synchronized (Application.getEventLock()) {
         if (maxSize <= 0) {
            throw new IllegalArgumentException();
         }

         if (maxSize < this.getMaxSize()) {
            String currentText = this._field.getText();
            if (currentText.length() > maxSize) {
               currentText = currentText.substring(0, maxSize);
            }

            if (!this.validateText(currentText, this._constraints)) {
               throw new IllegalArgumentException();
            }
         }

         this._field.setMaxSize(maxSize);
         return maxSize;
      }
   }

   public int size() {
      synchronized (Application.getEventLock()) {
         return this._field.getTextLength();
      }
   }

   public int getCaretPosition() {
      synchronized (Application.getEventLock()) {
         return this._field.getCursorPosition();
      }
   }

   public int getConstraints() {
      synchronized (Application.getEventLock()) {
         return this._constraints;
      }
   }

   public void setConstraints(int constraints) {
      synchronized (Application.getEventLock()) {
         String label = this.getLabel();
         if (!this.init(label, this._field.getText(), this._field.getMaxSize(), constraints)) {
            this.init(label, "", this._field.getMaxSize(), constraints);
         }
      }
   }

   public String getLabel() {
      synchronized (Application.getEventLock()) {
         String label = this._field.getLabel();
         return label != null && label.length() > 0 ? label.substring(0, label.length() - 1) : this._field.getLabel();
      }
   }

   public void setLabel(String label) {
      synchronized (Application.getEventLock()) {
         this._field.setLabel(this.augmentLabel(label));
      }
   }
}
