package net.rim.wica.runtime.metadata.internal.component.ui.control;

import java.util.Vector;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.TableColumnModel;
import net.rim.wica.runtime.metadata.internal.component.ui.ScreenModelImpl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIControlImpl;

public class TableColumnModelImpl extends UIControlImpl implements TableColumnModel {
   private int _evenRowStyle = -1;
   private int _formatType = -1;
   private int _headerStyle = -1;
   private int _oddRowStyle = -1;
   private int _style = -1;
   private String _name;
   private String _title;
   private String _format;
   private String _width;
   private boolean _frozen;
   private Object _inValue;
   private Object _inTitle;

   public String getName() {
      return this._name;
   }

   public void setStyle(int _style) {
      this._style = _style;
   }

   public Object getInValue() {
      return this._inValue;
   }

   @Override
   public int getEvenRowStyle() {
      return this._evenRowStyle;
   }

   @Override
   public int getOddRowStyle() {
      return this._oddRowStyle;
   }

   @Override
   public synchronized String getTitle() {
      return this._title;
   }

   @Override
   public synchronized void setTitle(String title) {
      if (!this.isEqual(this._title, title)) {
         this._title = title;
         this.onValueChanged(false);
      }
   }

   @Override
   public boolean isAutoSized() {
      return this._width == null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public double getWidthPercentage() {
      boolean var3 = false /* VF: Semaphore variable */;

      double var10000;
      try {
         var3 = true;
         if (this._width == null) {
            var10000 = (double)-4616189618054758400L;
            var3 = false;
         } else {
            var10000 = Double.parseDouble(this._width) * 4576918229304087675L;
            var3 = false;
         }
      } finally {
         if (var3) {
            return (double)-4616189618054758400L;
         }
      }

      return var10000;
   }

   @Override
   public boolean isFrozen() {
      return this._frozen;
   }

   @Override
   public void setFrozen(boolean frozen, boolean fromUI) {
      boolean currentlyFrozen = this._frozen;
      this._frozen = frozen;
      if (!fromUI && currentlyFrozen != frozen) {
         ((ScreenModelImpl)this.getScreen()).invalidateUI(false);
      }
   }

   @Override
   public String getCell(int row) {
      return (String)(super._value == null ? null : (super._valueType == 3 ? super._value : ((Vector)super._value).elementAt(row)));
   }

   @Override
   public int getHeaderStyle() {
      return this._headerStyle;
   }

   @Override
   public String getFormat() {
      return this._format;
   }

   @Override
   public int getType() {
      return this._formatType;
   }

   public TableColumnModelImpl(
      UIContainer parent,
      String name,
      Object inTitle,
      Object inValue,
      int formatType,
      String format,
      String width,
      int style,
      int headerStyle,
      int evenRowStyle,
      int oddRowStyle,
      boolean frozen,
      boolean visible
   ) {
      super(-1, 141, parent, style, 0, -1, -1, -1, inValue);
      this._name = name;
      this._evenRowStyle = evenRowStyle;
      this._format = format;
      this._frozen = frozen;
      this._headerStyle = headerStyle;
      this._oddRowStyle = oddRowStyle;
      this._inTitle = inTitle;
      this._formatType = formatType;
      this._width = width;
      this._inValue = inValue;
      this._style = style;
      this.setVisible(visible);
   }

   @Override
   public int getStyle() {
      return this._style;
   }

   protected TableColumnModelImpl(
      int id,
      String title,
      int formatType,
      String format,
      String width,
      int style,
      int headerStyle,
      int evenRowStyle,
      int oddRowStyle,
      boolean frozen,
      boolean visible
   ) {
      super(id, 141, style, 0, -1, -1, -1);
      this._evenRowStyle = evenRowStyle;
      this._format = format;
      this._frozen = frozen;
      this._headerStyle = headerStyle;
      this._oddRowStyle = oddRowStyle;
      this._title = title;
      this._formatType = formatType;
      this._width = width;
      this._style = style;
   }

   @Override
   protected void resolveInValue() {
      super.resolveInValue();
      if (this._inTitle != null) {
         String newTitle = (String)this.resolveInValue(this._inTitle, 3);
         if (!this.isEqual(this._title, newTitle)) {
            this._title = newTitle;
            super._valueChanged = true;
         }
      }
   }

   private boolean isEqual(String oldText, String newText) {
      return newText == null ? oldText == null : newText.equals(oldText);
   }
}
