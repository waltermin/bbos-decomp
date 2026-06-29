package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringProvider;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.layout.SLKeyLayout;

public class DOWField extends Field implements FieldLabelProvider {
   private int _yPosOfWeekDays = 0;
   private String[] _dowFirstLetter = CommonResources.getStringArray(116);
   private String[] _dowShortForm = CommonResources.getStringArray(9094);
   private TextRect _label = new TextRect(this);
   private String _labelText;
   private BitSet _daysOfTheWeek = new BitSet(7);
   private BitSet _requiredDaysOfTheWeek = new BitSet(7);
   private int _dowIndex;
   private char[] _hotKeys = new char[7];
   private int _firstDOWIndex;
   private int _greatestWidth;
   private int _totalWidth;
   private static final int NUM_DAYS_IN_WEEK = 7;
   private static final int MAX_DOW_INDEX = 6;
   protected static final int LABEL_PADDING = 2;
   protected static final int WEEK_DAYS_PADDING = 2;
   private static MenuItem _changeOptionsItem = new DOWField$1(CommonResource.getBundle(), 1, 30270, Integer.MAX_VALUE);

   public DOWField(String label, BitSet daysOfTheWeek) {
      super(22517998136852480L);
      this._labelText = label;
      this._label.setText(label);
      if (daysOfTheWeek != null) {
         this.setDays(daysOfTheWeek);
      }

      this.buildHotKeys();
      this.calculateWidths();
   }

   public void setDays(BitSet daysOfTheWeek) {
      BitSet dow = this._daysOfTheWeek;
      dow.reset();

      for (int i = 0; i < 7; i++) {
         if (daysOfTheWeek.isSet(i) || this._requiredDaysOfTheWeek.isSet(i)) {
            dow.set(i);
         }
      }
   }

   public void getDays(BitSet daysOfTheWeek) {
      BitSet dow = this._daysOfTheWeek;
      daysOfTheWeek.reset();

      for (int i = 0; i < 7; i++) {
         if (dow.isSet(i)) {
            daysOfTheWeek.set(i);
         }
      }
   }

   public void setRequiredDays(BitSet requiredDaysOfTheWeek) {
      BitSet requiredDOW = this._requiredDaysOfTheWeek;
      BitSet currentDOW = this._daysOfTheWeek;
      requiredDOW.reset();

      for (int i = 0; i < 7; i++) {
         if (requiredDaysOfTheWeek.isSet(i)) {
            requiredDOW.set(i);
            currentDOW.set(i);
         }
      }
   }

   public void setFirstDayOfWeek(int firstDOWIndex) {
      this._firstDOWIndex = firstDOWIndex - 1;
      if (this._firstDOWIndex < 0 || this._firstDOWIndex > 6) {
         this._firstDOWIndex = 0;
      }

      this.buildHotKeys();
   }

   private void buildHotKeys() {
      ResourceBundle rb = ResourceBundle.getBundle(2545338480386147321L, "net.rim.device.apps.internal.resource.Ui");

      for (int i = 0; i < 7; i++) {
         int dowIndex = i + this._firstDOWIndex;
         if (dowIndex >= 7) {
            dowIndex -= 7;
         }

         String hotKeyString = rb.getString(10 + dowIndex);
         this._hotKeys[dowIndex] = Character.toLowerCase(hotKeyString.charAt(0));
      }
   }

   private void toggleDay(int dowIndex) {
      if (this._daysOfTheWeek.isSet(dowIndex) && !this._requiredDaysOfTheWeek.isSet(dowIndex)) {
         this._daysOfTheWeek.clear(dowIndex);
      } else {
         this._daysOfTheWeek.set(dowIndex);
      }

      this.fieldChangeNotify(0);
      this.invalidate();
   }

   @Override
   public void getFocusRect(XYRect rect) {
      rect.height = this.getHeight();
      rect.y = this._yPosOfWeekDays;
      int focusWidthAdjustment;
      if (this.isEditable()) {
         rect.width = this._greatestWidth;
         int focusRectsToSubtract = this._firstDOWIndex - this._dowIndex;
         if (this._dowIndex >= this._firstDOWIndex) {
            focusRectsToSubtract += 7;
         }

         focusWidthAdjustment = this._greatestWidth * focusRectsToSubtract;
      } else {
         rect.width = this._totalWidth;
         focusWidthAdjustment = this._totalWidth;
      }

      rect.x = this.getWidth() - focusWidthAdjustment;
   }

   @Override
   protected void layout(int width, int height) {
      int labelBounds = this.getFont().getBounds(this._labelText);
      int preferredWidth = labelBounds + 2 + this._totalWidth;
      this._label.layout(Math.min(labelBounds + 2, width), height);
      if (preferredWidth > width) {
         height = this._label.getHeight() + this.getFont().getHeight();
         this._yPosOfWeekDays = this._label.getHeight();
      } else {
         height = Math.max(this._label.getHeight(), this.getFont().getHeight());
         this._yPosOfWeekDays = 0;
      }

      this.setExtent(width, height);
   }

   @Override
   protected void onFocus(int direction) {
      if (direction > 0) {
         this._dowIndex = this._firstDOWIndex;
      } else {
         if (direction < 0) {
            this._dowIndex = this._firstDOWIndex + 6;
            if (this._dowIndex >= 7) {
               this._dowIndex -= 7;
            }
         }
      }
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      if (!this.isEditable()) {
         return super.moveFocus(amount, status, time);
      }

      if ((status & 1) != 0) {
         this.toggleDay(this._dowIndex);
         return 0;
      }

      int extra = 0;
      this.setMuddy(false);
      int dowColumn = this._dowIndex - this._firstDOWIndex;
      if (this._dowIndex < this._firstDOWIndex) {
         dowColumn += 7;
      }

      dowColumn += amount;
      if (dowColumn < 0) {
         extra = dowColumn;
         this._dowIndex = this._firstDOWIndex;
      } else if (dowColumn > 6) {
         extra = dowColumn - 6;
         this._dowIndex = 0;
      } else {
         this._dowIndex = dowColumn + this._firstDOWIndex;
         if (this._dowIndex >= 7) {
            this._dowIndex -= 7;
         }
      }

      return extra;
   }

   @Override
   protected boolean invokeAction(int action) {
      if (!this.isEditable()) {
         return false;
      }

      switch (action) {
         case 1:
            this.toggleDay(this._dowIndex);
            return true;
         default:
            return false;
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (!this.isEditable()) {
         return false;
      }

      if (key == ' ') {
         this.toggleDay(this._dowIndex);
         return true;
      }

      String chars = null;
      if (InputContext.getInstance(false).isSureType()) {
         StringBuffer temp = Keypad.getLayout().getComplementaryChars(key, SLKeyLayout.convertStatusToModifiers(status));
         if (temp != null) {
            chars = temp.toString();
         }
      }

      char keyToCheck = UiInternal.map(Keypad.getLayout().getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
      keyToCheck = Character.toLowerCase(keyToCheck);

      for (int i = 0; i < 7; i++) {
         if (this._hotKeys[i] == keyToCheck || chars != null && chars.indexOf(this._hotKeys[i]) != -1) {
            this.toggleDay(i);
            return true;
         }
      }

      return false;
   }

   @Override
   public String getLabel() {
      return this._labelText;
   }

   @Override
   public void setLabel(String label) {
      this._labelText = label;
      this._label.setText(label);
      this.updateLayout();
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public String toString() {
      boolean addComma = false;
      StringBuffer s = new StringBuffer();

      for (int i = 0; i < 7; i++) {
         int dowIndex = i + this._firstDOWIndex;
         if (dowIndex >= 7) {
            dowIndex -= 7;
         }

         if (this._daysOfTheWeek.isSet(dowIndex)) {
            if (addComma) {
               s.append(',');
               s.append(' ');
            } else {
               addComma = true;
            }

            s.append(this._dowShortForm[dowIndex]);
         }
      }

      return s.toString();
   }

   @Override
   protected void paint(Graphics g) {
      BitSet dow = this._daysOfTheWeek;
      Font normal = this.getFont();
      Font bold = normal.derive(1);
      Font plain = normal.derive(0);
      if (this._labelText != null) {
         g.setFont(this.getFont());
         this._label.paintSelf(g);
      }

      int xPos = this.getWidth() - this._totalWidth;

      for (int i = 0; i < 7; i++) {
         int dowIndex = i + this._firstDOWIndex;
         if (dowIndex >= 7) {
            dowIndex -= 7;
         }

         if (dow.isSet(dowIndex)) {
            g.setFont(bold);
         } else {
            g.setFont(plain);
         }

         g.drawText(this._dowFirstLetter[dowIndex].charAt(0), xPos, this._yPosOfWeekDays, 4, this._greatestWidth);
         xPos += this._greatestWidth;
      }
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this.isEditable() && Ui.getMode() < 2) {
         contextMenu.addItem(_changeOptionsItem);
      }
   }

   private void calculateWidths() {
      this._greatestWidth = 0;
      TextMetrics _textMetrics = new TextMetrics();
      Font font = this.getFont();
      Font plainFont = font.derive(0);
      Font boldFont = font.derive(1);

      for (int i = 0; i < 7; i++) {
         int dowIndex = i + this._firstDOWIndex;
         if (dowIndex >= 7) {
            dowIndex -= 7;
         }

         int plainWidth = plainFont.measureText(this._dowFirstLetter[dowIndex], 0, 1, null, _textMetrics);
         int boldWidth = boldFont.measureText(this._dowFirstLetter[dowIndex], 0, 1, null, _textMetrics);
         int greatestWidthTemp;
         if (plainWidth > boldWidth) {
            greatestWidthTemp = plainWidth;
         } else {
            greatestWidthTemp = boldWidth;
         }

         if (greatestWidthTemp > this._greatestWidth) {
            this._greatestWidth = greatestWidthTemp;
         }
      }

      this._greatestWidth += 2;
      this._totalWidth = this._greatestWidth * 7;
   }

   @Override
   protected void applyFont() {
      super.applyFont();
      this.calculateWidths();
   }
}
