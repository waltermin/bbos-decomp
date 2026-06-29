package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.internal.i18n.CommonResource;

public class BaseDurationField extends Field {
   private Vector _rowsOfText = (Vector)(new Object());
   private StringBuffer _durationText = (StringBuffer)(new Object());
   private long _duration;
   private int _currentUnit;
   private int _smallestUnitsDisplayed;
   private int _largestUnitsDisplayed;
   private int _cursorHeight;
   private int _cursorXOffset;
   private int _cursorYOffset;
   private int _cursorWidth;
   private int _oldWidth;
   private int _durationWidth;
   private int _durationHeight;
   private boolean _digitEntered;
   public static final int DAYS = 3;
   public static final int HOURS = 2;
   public static final int MINUTES = 1;
   public static final int SECONDS = 0;
   private static MenuItem _changeOptionsItem = new BaseDurationField$2(CommonResource.getBundle(), 1, 30270, Integer.MAX_VALUE);
   private static Tag TAG = Tag.create("date");
   private static final int[][][] DISPLAY_NAMES = new int[][][]{
      (int[][])({107, 108, -804651004, 1000, 60000, 3600000, 86400000, 207814912}),
      (int[][])({105, 106, -804651006, 107, 108, -804651004, 1000, 60000}),
      (int[][])({103, 104, -804651006, 105, 106, -804651006, 107, 108}),
      (int[][])({101, 102, -804651006, 103, 104, -804651006, 105, 106})
   };
   private static final int MILLIS_PER_SECOND = 1000;
   private static final int MILLIS_PER_MINUTE = 60000;
   private static final int MILLIS_PER_HOUR = 3600000;
   private static final int MILLIS_PER_DAY = 86400000;
   private static final int[] UNITS = new int[]{
      1000,
      60000,
      3600000,
      86400000,
      207814912,
      1277135469,
      16218465,
      2781953,
      -1910540799,
      1979777154,
      1283624479,
      821524833,
      1870004480,
      1849779563,
      56711012,
      1870004480
   };
   private static final int ABSOLUTE_MAX = 99999;
   private static final int[] UNIT_MAX = new int[]{60, 60, 24, 100000, -804651006, 101, 102, -804651006, 103, 104, -804651006, 105, 106, -804651006, 107, 108};

   public BaseDurationField(int largestUnitsDisplayed, int smallestUnitsDisplayed, long duration, long style) {
      super(validateStyle(style));
      this.setTag(TAG);
      this._largestUnitsDisplayed = largestUnitsDisplayed;
      this._smallestUnitsDisplayed = smallestUnitsDisplayed;
      this._duration = duration;
      this.checkUnitConsistency(true);
   }

   public long getDuration() {
      return this._duration;
   }

   public void setDuration(long duration) {
      this.setDuration(duration, Integer.MIN_VALUE);
   }

   public int getLargestUnitDisplayed() {
      return this._largestUnitsDisplayed;
   }

   public void setLargestUnitDisplayed(int largestUnitToDisplay) {
      this._largestUnitsDisplayed = largestUnitToDisplay;
      this.checkUnitConsistency(false);
      this.calcCachedData();
   }

   public int getSmallestUnitDisplayed() {
      return this._smallestUnitsDisplayed;
   }

   public void setSmallestUnitDisplayed(int smallestUnitToDisplay) {
      this._smallestUnitsDisplayed = smallestUnitToDisplay;
      this.checkUnitConsistency(true);
      this.calcCachedData();
   }

   public int getUnitWithFocus() {
      return this._currentUnit;
   }

   public void setUnitWithFocus(int currentUnit) {
      this._currentUnit = currentUnit;
      this.checkUnitConsistency(true);
      this.calcCachedData();
   }

   @Override
   public int getPreferredWidth() {
      this.calcLayoutData(Integer.MAX_VALUE);
      return this._durationWidth;
   }

   @Override
   public int getPreferredHeight() {
      return this.getFont().getHeight();
   }

   @Override
   public void getFocusRect(XYRect rect) {
      if (this.isEditable()) {
         rect.x = this._cursorXOffset;
         rect.y = this._cursorYOffset;
         rect.height = this._cursorHeight;
         rect.width = this._cursorWidth;
      } else {
         this.getInPlaceRect(rect);
      }
   }

   public void changeOptionDialog() {
      if (this.isEditable() && this.getOriginal() == this) {
         BaseDurationField field = new BaseDurationField$1(this, this._largestUnitsDisplayed, this._smallestUnitsDisplayed, this._duration, 0);
         field.setChangeListener(this.getChangeListener());
         field.setUnitWithFocus(this._currentUnit);
         BaseDurationField$BaseDurationInPlaceScreen changeDialog = new BaseDurationField$BaseDurationInPlaceScreen(this, field, 0);
         field.setFocus();
         boolean accepted = changeDialog.doModal();
         if (accepted) {
            this.setDuration(field.getDuration(), 0);
         } else {
            field.setDuration(this.getDuration());
         }
      }
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      if (!this.isEditable()) {
         return super.moveFocus(amount, status, time);
      }

      int extra = 0;
      if ((status & 1) != 0) {
         this.calcDurationFromFieldChange(amount * Ui.getIncreaseDirection());
         this.fieldChangeNotify(0);
      } else if (this._currentUnit < this._largestUnitsDisplayed && amount < 0) {
         this._currentUnit++;
         this.calcCachedData();
      } else if (this._currentUnit > this._smallestUnitsDisplayed && amount > 0) {
         this._currentUnit--;
         this.calcCachedData();
      } else {
         extra = amount;
      }

      this.invalidate();
      return extra;
   }

   @Override
   public boolean isSelectionCopyable() {
      return true;
   }

   @Override
   public void selectionCopy(Clipboard cb) {
      cb.put(this.toString());
   }

   @Override
   public String toString() {
      return this._durationText.toString();
   }

   @Override
   protected void layout(int width, int height) {
      this._oldWidth = width;
      this.calcLayoutData(width);
      this.setExtent(this._durationWidth, this._durationHeight);
   }

   @Override
   protected void paint(Graphics graphics) {
      int numberOfRows = this._rowsOfText.size();
      int i = 0;

      for (int y = 0; i < numberOfRows; y += this._cursorHeight) {
         graphics.drawText((String)this._rowsOfText.elementAt(i), 0, y);
         i++;
      }
   }

   @Override
   protected void onFocus(int direction) {
      if (this.isEditable()) {
         if (direction > 0) {
            this._currentUnit = this._largestUnitsDisplayed;
         } else if (direction < 0) {
            this._currentUnit = this._smallestUnitsDisplayed;
         }
      } else {
         this._currentUnit = this._largestUnitsDisplayed;
      }

      this.calcCachedData();
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (!this.isEditable()) {
         return false;
      }

      if (this.isCharm()) {
         switch (key) {
            case ' ':
               key = Keypad.map(key, 1);
               break;
            case '0':
               key = Keypad.getUnaltedChar('0');
         }
      }

      if (key == ' ') {
         int changeAmount = 0;
         byte var10;
         if ((status & 2) != 0) {
            var10 = -1;
         } else {
            var10 = 1;
         }

         this.calcDurationFromFieldChange(var10);
         this.fieldChangeNotify(0);
         this.invalidate();
         return true;
      } else {
         if (key == '\n') {
            this.focusRemove();
            boolean result = this.moveFocus(1, 0, 0) == 0;
            this.focusAdd(true);
            return result;
         }

         char altKey = Keypad.getAltedChar(key);
         if (!Character.isDigit(key)) {
            if (!Character.isDigit(altKey)) {
               return false;
            }

            key = altKey;
         }

         int prevVal;
         if (this._currentUnit < this._largestUnitsDisplayed) {
            prevVal = (int)(this._duration % UNITS[this._currentUnit + 1]) / UNITS[this._currentUnit];
         } else {
            prevVal = (int)(this._duration / UNITS[this._currentUnit]);
         }

         int val = Character.digit(key, 10);
         int newVal = this.isMuddy() && this._digitEntered ? prevVal * 10 + val : val;
         this._digitEntered = true;
         if (newVal > 99999 || this._currentUnit < this._largestUnitsDisplayed && newVal >= UNIT_MAX[this._currentUnit]) {
            newVal = val;
         }

         this.calcDurationFromFieldChange(newVal - prevVal);
         this.fieldChangeNotify(0);
         this.invalidate();
         return true;
      }
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      if (Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) != 0) {
         this.changeOptionDialog();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (Ui.getMode() < 2 && this.isEditable()) {
         contextMenu.addItem(_changeOptionsItem);
      }
   }

   @Override
   protected void applyFont() {
      super.applyFont();
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         this.updateLayout();
      }
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         this.updateLayout();
      }
   }

   void getInPlaceRect(XYRect rect) {
      rect.x = 0;
      rect.y = 0;
      rect.width = this._durationWidth;
      rect.height = this._durationHeight;
   }

   private void calcLayoutData(int width) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   private void appendSpaceOrStartNewRow(StringBuffer textRow, String textToBeAdded, int maxWidth, Font font) {
      int textWidth = font.getBounds(textRow);
      if (textWidth + font.getBounds(((StringBuffer)(new Object(" "))).append(textToBeAdded).toString()) <= maxWidth) {
         textRow.append(" ");
      } else {
         this._durationWidth = Math.max(textWidth, this._durationWidth);
         this._rowsOfText.addElement(textRow.toString());
         textRow.setLength(0);
      }
   }

   private void calcCachedData() {
      if (this.getManager() != null && this.getManager().isValidLayout()) {
         int oldContentWidth = this.getContentWidth();
         int oldContentHeight = this.getContentHeight();
         this.calcLayoutData(Math.max(this._oldWidth, oldContentWidth));
         if (this._durationWidth != oldContentWidth || this._durationHeight != oldContentHeight) {
            this.updateLayout();
         }

         this.invalidate();
      }
   }

   private void setDuration(long duration, int context) {
      if (duration < 0) {
         duration = 0;
      }

      if (duration != this._duration) {
         this._duration = duration;
         this._digitEntered = false;
         this.calcCachedData();
         this.fieldChangeNotify(context);
      }
   }

   private void calcDurationFromFieldChange(int changeAmount) {
      this._duration = this._duration + (long)changeAmount * UNITS[this._currentUnit];
      if (this._duration < 0) {
         this._duration = 0;
      }

      this.calcCachedData();
   }

   private void checkUnitConsistency(boolean smallestHasPriority) {
      if (this._smallestUnitsDisplayed < 0) {
         this._smallestUnitsDisplayed = 0;
      } else if (this._smallestUnitsDisplayed > 3) {
         this._smallestUnitsDisplayed = 3;
      }

      if (this._largestUnitsDisplayed < 0) {
         this._largestUnitsDisplayed = 0;
      } else if (this._largestUnitsDisplayed > 3) {
         this._largestUnitsDisplayed = 3;
      }

      if (this._largestUnitsDisplayed < this._smallestUnitsDisplayed) {
         if (smallestHasPriority) {
            this._largestUnitsDisplayed = this._smallestUnitsDisplayed;
         } else {
            this._smallestUnitsDisplayed = this._largestUnitsDisplayed;
         }
      }

      if (this._currentUnit < this._smallestUnitsDisplayed) {
         this._currentUnit = this._smallestUnitsDisplayed;
      } else {
         if (this._currentUnit > this._largestUnitsDisplayed) {
            this._currentUnit = this._largestUnitsDisplayed;
         }
      }
   }

   private boolean isCharm() {
      return Keypad.getHardwareLayout() == 1364346180;
   }

   private static long validateStyle(long style) {
      if ((style & 13510798882111488L) == 0) {
         style |= 4503599627370496L;
      }

      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      return style;
   }
}
