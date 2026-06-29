package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;

class BasicChoice extends Item implements Choice {
   VerticalFieldManager _container = new BasicChoice$1(this, 1152921504606846976L);
   boolean _onScreen;
   int _currentlySelectedIndex = -1;
   protected FieldChangeListener _changeListener;
   private int _numChoices;
   int _type;
   private int _fitPolicy;
   private Font[] _fonts = new Font[0];

   Field getField(int index) {
      synchronized (Application.getEventLock()) {
         return this._container.getField(index);
      }
   }

   protected void doSetSelectedFlags(boolean[] _1) {
      throw null;
   }

   protected void doSetSelectedIndex(int _1, boolean _2) {
      throw null;
   }

   protected boolean doIsSelected(int _1) {
      throw null;
   }

   protected void doSet(int _1, String _2, Image _3) {
      throw null;
   }

   protected void doDelete(int _1) {
      throw null;
   }

   protected void doInsert(int _1, String _2, Image _3) {
      throw null;
   }

   protected Image doGetImage(int _1) {
      throw null;
   }

   protected String doGetString(int _1) {
      throw null;
   }

   protected void setFieldFont(net.rim.device.api.ui.Font font, int elementNum) {
      this.getField(elementNum).setFont(font);
   }

   @Override
   public void setSelectedIndex(int elementNum, boolean selected) {
      synchronized (Application.getEventLock()) {
         this.checkIndex(elementNum);
         this.doSetSelectedIndex(elementNum, selected);
      }
   }

   @Override
   public void setSelectedFlags(boolean[] selectedArray) {
      synchronized (Application.getEventLock()) {
         if (selectedArray.length < this._numChoices) {
            throw new IllegalArgumentException();
         }

         if (this._numChoices != 0) {
            this.doSetSelectedFlags(selectedArray);
         }
      }
   }

   @Override
   public void setFitPolicy(int fitPolicy) {
      if (fitPolicy >= 0 && fitPolicy <= 2) {
         synchronized (Application.getEventLock()) {
            if (this._fitPolicy != fitPolicy) {
               this._fitPolicy = fitPolicy;
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int getFitPolicy() {
      return this._fitPolicy;
   }

   @Override
   public void setFont(int elementNum, Font font) {
      synchronized (Application.getEventLock()) {
         if (font == null) {
            font = Font.getDefaultFont();
         }

         if (this._fonts == null) {
            this._fonts = new Font[this._numChoices];
         }

         this._fonts[elementNum] = font;
         this.setFieldFont(font.getPeer(), elementNum);
      }
   }

   @Override
   public Font getFont(int elementNum) {
      synchronized (Application.getEventLock()) {
         return this._fonts != null && this._fonts[elementNum] != null ? this._fonts[elementNum] : Font.getDefaultFont();
      }
   }

   @Override
   public int getSelectedFlags(boolean[] selectedArray_return) {
      synchronized (Application.getEventLock()) {
         int count = this._numChoices;
         if (selectedArray_return.length < count) {
            throw new IllegalArgumentException();
         }

         int selectedCount = 0;

         int i;
         for (i = 0; i < count; i++) {
            boolean selected = this.isSelected(i);
            selectedArray_return[i] = selected;
            if (selected) {
               selectedCount++;
            }
         }

         while (i < selectedArray_return.length) {
            selectedArray_return[i] = false;
            i++;
         }

         return selectedCount;
      }
   }

   @Override
   public boolean isSelected(int elementNum) {
      synchronized (Application.getEventLock()) {
         this.checkIndex(elementNum);
         return this.doIsSelected(elementNum);
      }
   }

   @Override
   public void set(int elementNum, String stringPart, Image imagePart) {
      synchronized (Application.getEventLock()) {
         this.checkIndex(elementNum);
         this.doSet(elementNum, stringPart, imagePart);
      }
   }

   @Override
   public void deleteAll() {
      synchronized (Application.getEventLock()) {
         int count = this._numChoices;

         for (int i = 0; i < count; i++) {
            this.delete(0);
         }
      }
   }

   @Override
   public void delete(int elementNum) {
      synchronized (Application.getEventLock()) {
         this.checkIndex(elementNum);
         this._numChoices--;
         this.doDelete(elementNum);
         Arrays.removeAt(this._fonts, elementNum);
         if (this._currentlySelectedIndex == this._numChoices) {
            this._currentlySelectedIndex--;
         }
      }
   }

   @Override
   public void insert(int elementNum, String stringElement, Image imageElement) {
      synchronized (Application.getEventLock()) {
         if (elementNum >= 0 && elementNum <= this._numChoices) {
            this.doInsert(elementNum, stringElement, imageElement);
            this._numChoices++;
            Arrays.insertAt(this._fonts, Font.getDefaultFont(), elementNum);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   @Override
   public int append(String stringPart, Image imagePart) {
      synchronized (Application.getEventLock()) {
         int index = this._numChoices;
         this.insert(index, stringPart, imagePart);
         return index;
      }
   }

   @Override
   public Image getImage(int elementNum) {
      synchronized (Application.getEventLock()) {
         this.checkIndex(elementNum);
         return this.doGetImage(elementNum);
      }
   }

   @Override
   public String getString(int elementNum) {
      synchronized (Application.getEventLock()) {
         this.checkIndex(elementNum);
         return this.doGetString(elementNum);
      }
   }

   @Override
   public int size() {
      return this._numChoices;
   }

   @Override
   public int getSelectedIndex() {
      throw null;
   }

   private void checkIndex(int index) {
      if (index < 0 || index >= this._numChoices) {
         throw new IndexOutOfBoundsException();
      }
   }
}
