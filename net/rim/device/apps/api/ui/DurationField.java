package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;

public class DurationField extends Field {
   private TextRect _labelTextRect = (TextRect)(new Object(this, 70));
   private long _duration;
   private int _currentUnit;
   private int _smallestUnitsDisplayed;
   private int _largestUnitsDisplayed;
   private StringBuffer _fieldText = (StringBuffer)(new Object());
   private boolean _calculatedDataSet;
   private int _widthOfDurationString;
   private int _widthToCursor;
   private int _widthOfCursor;
   private boolean _digitEntered;
   private boolean _inLayout;
   private boolean _isLabelOnOwnLine;
   private boolean _isLabelOnOwnLineTheme;
   public static final int DAYS = 3;
   public static final int HOURS = 2;
   public static final int MINUTES = 1;
   public static final int SECONDS = 0;
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
      1830381057,
      712179968,
      712179968,
      276053826,
      16806977,
      1816472179,
      1950452283,
      527827200,
      16810638
   };
   private static final int ABSOLUTE_MAX = 99999;
   private static final int[] UNIT_MAX = new int[]{60, 60, 24, 100000, -804651006, 101, 102, -804651006, 103, 104, -804651006, 105, 106, -804651006, 107, 108};
   private static final int PADDING = 2;
   private static Tag TAG = Tag.create("date");
   private static Tag TAG_LABEL = Tag.create("label");
   private static MenuItem _changeOptionsItem = new DurationField$1(CommonResource.getBundle(), 1, 30270, Integer.MAX_VALUE);

   public DurationField(String label, int largestUnitsDisplayed, int smallestUnitsDisplayed, long duration) {
      super(22517998136852480L);
      this.setTag(TAG);
      this._labelTextRect.setTag(TAG_LABEL);
      this._labelTextRect.setText(label);
      this._largestUnitsDisplayed = largestUnitsDisplayed;
      this._smallestUnitsDisplayed = smallestUnitsDisplayed;
      this._duration = duration;
      this.checkUnitConsistency(true);
      this._isLabelOnOwnLineTheme = ThemeManager.getActiveTheme().isLabelOnOwnLine();
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

   public long getDuration() {
      return this._duration;
   }

   void getInPlaceRect(XYRect rect) {
      rect.x = this._isLabelOnOwnLineTheme ? 0 : this.getContentWidth() - this._widthOfDurationString;
      rect.width = this._widthOfDurationString;
      rect.y = this.getFieldText_Y();
      rect.height = this.getContentHeight();
   }

   public int getLargestUnitDisplayed() {
      return this._largestUnitsDisplayed;
   }

   @Override
   public int getPreferredWidth() {
      this.calculateUIValues(0);
      int width = this._widthOfDurationString;
      if (this._isLabelOnOwnLine && this._labelTextRect.getText() != null && !this._labelTextRect.getText().equals("")) {
         width = Math.max(this._widthOfDurationString, this._labelTextRect.getWidth());
      }

      return width;
   }

   @Override
   public int getPreferredHeight() {
      this.calculateUIValues(0);
      int var10000 = this.getFont().getHeight();
      return this._isLabelOnOwnLine && this._labelTextRect.getText() != null && !this._labelTextRect.getText().equals("") ? var10000 << 1 : var10000 << 0;
   }

   public int getSmallestUnitDisplayed() {
      return this._smallestUnitsDisplayed;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (!this.isEditable()) {
         return false;
      }

      if (InternalServices.isReducedFormFactor()) {
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

   public void setDuration(long duration) {
      this.setDuration(duration, Integer.MIN_VALUE);
   }

   protected void setDuration(long duration, int context) {
      if (duration < 0) {
         duration = 0;
      }

      if (duration != this._duration) {
         this._duration = duration;
         this._calculatedDataSet = false;
         this._digitEntered = false;
         this.calculateUIValues(0);
         this.fieldChangeNotify(context);
         this.invalidate();
      }
   }

   public void setSmallestUnitDisplayed(int smallestUnitToDisplay) {
      this._smallestUnitsDisplayed = smallestUnitToDisplay;
      this.checkUnitConsistency(true);
      this._calculatedDataSet = false;
      this.invalidate();
   }

   public void setLargestUnitDisplayed(int largestUnitToDisplay) {
      this._largestUnitsDisplayed = largestUnitToDisplay;
      this.checkUnitConsistency(false);
      this._calculatedDataSet = false;
      this.invalidate();
   }

   public void setUnitWithFocus(int currentUnit) {
      this._currentUnit = currentUnit;
      this.checkUnitConsistency(true);
      this._calculatedDataSet = false;
      this.invalidate();
   }

   public int getUnitWithFocus() {
      return this._currentUnit;
   }

   private void calcDurationFromFieldChange(int changeAmount) {
      this._duration = this._duration + (long)changeAmount * UNITS[this._currentUnit];
      if (this._duration < 0) {
         this._duration = 0;
      }

      this._calculatedDataSet = false;
   }

   private void calculateUIValues(int width) {
      throw new RuntimeException("cod2jar: array load: unknown element");
   }

   public void changeOptionDialog() {
      if (this.isEditable()) {
         DurationField field = new DurationField$2(this, null, this._largestUnitsDisplayed, this._smallestUnitsDisplayed, this._duration);
         field.setChangeListener(this.getChangeListener());
         field.setUnitWithFocus(this._currentUnit);
         DurationField$DurationInPlaceScreen changeDialog = new DurationField$DurationInPlaceScreen(this, field, 0);
         field.setFocus();
         boolean accepted = changeDialog.doModal();
         if (accepted) {
            this.setDuration(field.getDuration(), 0);
         }
      }
   }

   @Override
   public void getFocusRect(XYRect rect) {
      rect.height = this.getContentHeight();
      rect.y = 0;
      this.calculateUIValues(0);
      int focusWidthAdjustment;
      if (this.isEditable()) {
         rect.width = this._widthOfCursor;
         focusWidthAdjustment = this._widthOfDurationString - this._widthToCursor;
      } else {
         rect.width = this._widthOfDurationString - this._widthToCursor;
         focusWidthAdjustment = rect.width;
      }

      if (this._isLabelOnOwnLine) {
         rect.y = this.getFieldText_Y();
      }

      if (this._isLabelOnOwnLineTheme) {
         rect.x = this._widthToCursor;
      } else {
         rect.x = this.getContentWidth() - focusWidthAdjustment;
      }
   }

   @Override
   protected void layout(int width, int height) {
      if (!this._inLayout) {
         this._inLayout = true;
         this._calculatedDataSet = false;
         this._labelTextRect.setPosition(0, 0);
         this._labelTextRect.layout(width, this.getFont().getHeight());
         this._labelTextRect.reduceWidthToFit(this._labelTextRect.getWidth());
         this.calculateUIValues(width);
         this.setExtent(width, this.getPreferredHeight());
         this._inLayout = false;
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

      this._calculatedDataSet = false;
   }

   @Override
   protected void paint(Graphics g) {
      this.calculateUIValues(0);
      if (this._isLabelOnOwnLine) {
         int width = this.getContentWidth();
      } else {
         int width = this.getContentWidth() - this._widthOfDurationString - 2;
      }

      this._labelTextRect.paintSelf(g);
      int x = this.getContentWidth() - this._widthOfDurationString;
      int y = this.getFieldText_Y();
      int flags = 5;
      int var7;
      if (this._isLabelOnOwnLineTheme) {
         x = 0;
         flags = 6;
         var7 = this.getContentWidth();
      } else {
         var7 = this._widthOfDurationString;
      }

      g.drawText(this._fieldText, 0, this._fieldText.length(), x, y, flags, var7);
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
         this._calculatedDataSet = false;
      } else if (this._currentUnit > this._smallestUnitsDisplayed && amount > 0) {
         this._currentUnit--;
         this._calculatedDataSet = false;
      } else {
         extra = amount;
      }

      this.invalidate();
      return extra;
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
      this._labelTextRect.applyTheme();
      this._isLabelOnOwnLineTheme = ThemeManager.getActiveTheme().isLabelOnOwnLine();
      Manager manager = this.getManager();
      if (manager != null && manager.isValidLayout()) {
         this.updateLayout();
      }
   }

   int getFieldText_Y() {
      return this._isLabelOnOwnLine ? this.getFont().getHeight() : 0;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.changeOptionDialog();
            return true;
         default:
            return false;
      }
   }
}
