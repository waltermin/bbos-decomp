package net.rim.device.api.ui.component;

import java.util.Vector;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.i18n.CollatorImpl;

public class ObjectListField extends ListField implements DrawStyle, ListFieldCallback {
   private Vector _list = new Vector();
   private int _prefWidth;
   private CollatorImpl _collator = new CollatorImpl();
   private static String EMPTY_ROW = "";
   private static final int ATTRIBUTES_MASK = 71;

   public void set(Object[] list) {
      if (list == null) {
         list = new Object[0];
      }

      this._list.setSize(list.length);

      for (int lv = list.length - 1; lv >= 0; lv--) {
         this._list.setElementAt(list[lv], lv);
      }

      this.setSize(list.length, 0);
      this.fieldChangeNotify(Integer.MIN_VALUE);
   }

   public Object get(int index) {
      return this._list.elementAt(index);
   }

   public void set(int index, Object object) {
      if (object == null) {
         throw new NullPointerException();
      }

      this._list.setElementAt(object, index);
      this.fieldChangeNotify(Integer.MIN_VALUE);
      this.invalidate(index);
   }

   public void insert(int index, Object object) {
      if (object == null) {
         throw new NullPointerException();
      }

      this._list.insertElementAt(object, index);
      super.insert(index);
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      int end = this._list.size();

      for (int lv = start; lv < end; lv++) {
         if (this._collator.compare(this._list.elementAt(lv).toString(), prefix, prefix.length()) == 0) {
            return lv;
         }
      }

      return -1;
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return this._prefWidth;
   }

   @Override
   public Object get(ListField listField, int index) {
      return this._list.elementAt(index);
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      String text = this._list.elementAt(index).toString();
      int style = (int)(this.getStyle() & 71);
      int fudge = listField.adjustRowHeight(graphics.getFont(), index, text);
      graphics.drawText(text, 0, Integer.MAX_VALUE, 0, y + fudge, style, width);
   }

   @Override
   public void insert(int index) {
      this.insert(index, EMPTY_ROW);
   }

   public ObjectListField(long style) {
      super(0, style);
      this.setCallback(this);
   }

   @Override
   protected void layout(int width, int height) {
      if (this.isStyle(1152921504606846976L)) {
         Font font = this.getFont();
         this._prefWidth = 0;
         int end = this._list.size();

         for (int lv = end - 1; lv >= 0; lv--) {
            int temp = font.getBounds(this._list.elementAt(lv).toString());
            if (this._prefWidth < temp) {
               this._prefWidth = temp;
            }
         }
      } else {
         this._prefWidth = width;
      }

      super.layout(width, height);
   }

   @Override
   public void delete(int index) {
      this._list.removeElementAt(index);
      super.delete(index);
   }

   public ObjectListField() {
      this(0);
   }
}
