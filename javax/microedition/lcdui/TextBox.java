package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class TextBox extends Screen {
   MIDPEditField _edit;

   public TextBox(String title, String text, int maxSize, int constraints) {
      super(new MIDPScreen());
      synchronized (Application.getEventLock()) {
         this.getPeer().setDisplayable(this);
         Manager container = new VerticalFieldManager(3459045988797251584L);
         this.getPeer().add(container);
         this._edit = new MIDPEditField(null, text, maxSize, constraints, null);
         container.add(this._edit);
         this.setTitle(title);
      }
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

   public void setInitialInputMode(String characterSubset) {
   }
}
