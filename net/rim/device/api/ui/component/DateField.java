package net.rim.device.api.ui.component;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.DateFormatSymbols;
import net.rim.device.api.i18n.FieldPosition;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringProvider;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.tid.im.layout.SLKeyLayout;

public class DateField extends Field implements DrawStyle, FieldLabelProvider {
   private int _minimumWidth;
   private TextRect _label = new TextRect(this);
   private TextRect _dateTextRect;
   private DateFormat _format;
   private DateFormatSymbols _symbols = DateFormatSymbols.getInstance();
   private TimeZone _timezone = TimeZone.getDefault();
   private Calendar _utilCal;
   int _position;
   private int[] _c_fields;
   private XYRect[] _cursor;
   private int _oldWidth;
   private FieldPosition _c_position = new FieldPosition(10);
   private long _minuteIncrements = 60000;
   private boolean _isLabelOwnLine;
   private int[] _focus_order = new int[]{
      10,
      5,
      2,
      1,
      12,
      13,
      14,
      -804651003,
      10004,
      10000,
      10008,
      10012,
      10041,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -805044104,
      879126131,
      2560,
      100663296,
      0,
      16789504,
      1006632960,
      512,
      50353920,
      1795162112
   };
   boolean _first_focus = true;
   private static Tag TAG = Tag.create("date");
   private static Tag TAG_LABEL = Tag.create("label");
   public static final int DATE;
   public static final int TIME;
   public static final int CHANGE_ON_VERTICAL_SCROLL;
   public static final int FOCUS_MOVE_ON_HORIZONTAL_SCROLL;
   public static final int DATE_TIME;
   private static final int PADDING;
   private static MenuItem _changeOptionsItem = new DateField$1(CommonResource.getBundle(), 1, 30270, 10);
   private static XYRect _rect = new XYRect();

   void calcCachedData() {
      if (this.getManager() != null && this.getManager().isValidLayout()) {
         XYRect extent = this.getExtent();
         int oldWidth = extent.width;
         int oldHeight = extent.height;
         this.calcLayoutData(Math.max(this._oldWidth, this.getContentWidth()));
         extent = this.getExtent();
         if (extent.width != oldWidth || extent.height != oldHeight) {
            this.updateLayout();
         }

         this.invalidate();
      } else {
         this.formatDate(null);
      }
   }

   boolean changeOptionDialog() {
      if (this.isEditable() && this.getOriginal() == this) {
         DateField field = new DateField$2(this, null, this.getDate(), this._format, 6);
         field.setMinuteIncrements(this._minuteIncrements);
         field.setTimeZone(this._timezone);
         field.setChangeListener(this.getChangeListener());
         DateInPlaceScreen changeDialog = new DateInPlaceScreen(this, field, 0);
         boolean accepted = changeDialog.doModal();
         if (accepted) {
            this.setDate(field.getDate(), 0);
         } else {
            field.setDate(this.getDate());
         }

         this._position = field._position;
         this.calcCachedData();
         return true;
      } else {
         return false;
      }
   }

   protected int getCurrentSubfield() {
      return this._c_fields[this._position];
   }

   public long getDate() {
      return this._utilCal == null ? Long.MIN_VALUE : ((CalendarExtensions)this._utilCal).getTimeLong();
   }

   void getInPlaceRect(XYRect rect) {
      this._dateTextRect.getTextBounds(rect);
   }

   public void setTimeZone(TimeZone tz) {
      if (tz == null) {
         throw new NullPointerException();
      }

      this._timezone = tz;
      if (this._utilCal != null) {
         this._utilCal.setTimeZone(tz);
      }

      this.calcCachedData();
   }

   public int getMode() {
      int[] fields = !this.isEditable() && this._format != null ? this._format.getFields() : this._c_fields;
      int mode = 0;

      for (int lv = 0; lv < fields.length; lv++) {
         switch (fields[lv]) {
            case 0:
            case 3:
            case 4:
            case 6:
            case 8:
            case 11:
               break;
            case 1:
            case 2:
            case 5:
            case 7:
            default:
               mode |= 16;
               break;
            case 9:
            case 10:
            case 12:
               mode |= 32;
         }
      }

      return mode;
   }

   public TimeZone getTimeZone() {
      return this._timezone;
   }

   boolean isSubFieldEditable(int testPosition) {
      boolean supported = false;
      int testField = this._c_fields[testPosition];
      switch (testField) {
         case 1:
         case 2:
         case 5:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         default:
            supported = true;
         case 0:
         case 3:
         case 4:
         case 6:
         case 7:
         case 8:
            return supported;
      }
   }

   public void setDate(long date) {
      this.setDate(date, Integer.MIN_VALUE);
   }

   protected void setDate(long date, int context) {
      boolean changed = false;
      if (date != Long.MIN_VALUE) {
         if (this._utilCal == null) {
            this._utilCal = Calendar.getInstance(this._timezone);
            changed = true;
         }

         if (date != this.getDate()) {
            ((CalendarExtensions)this._utilCal).setTimeLong(date);
            changed = true;
         }
      } else if (this._utilCal != null) {
         this._utilCal = null;
         changed = true;
      }

      if (changed) {
         this.calcCachedData();
         this.fieldChangeNotify(context);
      }
   }

   public void setDate(Date date) {
      this.setDate(date, Integer.MIN_VALUE);
   }

   protected void setDate(Date date, int context) {
      boolean changed = false;
      if (date != null) {
         if (this._utilCal == null) {
            this._utilCal = Calendar.getInstance(this._timezone);
            changed = true;
         }

         if (!date.equals(this._utilCal.getTime())) {
            this._utilCal.setTime(date);
            changed = true;
         }
      } else if (this._utilCal != null) {
         this._utilCal = null;
         changed = true;
      }

      if (changed) {
         this.calcCachedData();
         this.fieldChangeNotify(context);
      }
   }

   public void setFormat(DateFormat format) {
      this._format = format;
      if (this.isEditable()) {
         this._c_fields = this._format.getFields();
      } else {
         this._c_fields = new int[1];
      }

      this._position = 0;
      this.calcCachedData();
   }

   int getMinimumWidth() {
      return this._minimumWidth;
   }

   void setMinimumWidth(int minimumWidth) {
      this._minimumWidth = minimumWidth;
   }

   public void setMinuteIncrements(long minuteIncrements) {
      this._minuteIncrements = minuteIncrements;
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public void setLabel(String label) {
      this._label.setText(label);
      this.calcCachedData();
   }

   @Override
   public String getLabel() {
      return (String)this._label.getText();
   }

   private boolean isEraSupported() {
      for (int lv = 0; lv < this._c_fields.length; lv++) {
         if (this._c_fields[lv] == 0) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isSelectionCopyable() {
      return true;
   }

   public DateField() {
      this(null, 0, 48);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (!this.isEditable()) {
         return false;
      }

      key = this.transformKeyChar(key, status);
      if (key == ' ') {
         int changeAmount;
         if ((status & 2) != 0) {
            changeAmount = -1;
         } else {
            changeAmount = 1;
         }

         this.calcDateFromFieldChange(changeAmount);
      } else {
         if (key == '\n') {
            this.focusRemove();
            boolean result = this.moveFocus(1, 0, 0) == 0;
            this.focusAdd(true);
            return result;
         }

         if (key == 27) {
            return false;
         }

         if (this._utilCal == null) {
            this._utilCal = Calendar.getInstance(this._timezone);
         }

         boolean numeric = false;
         int currentField = this._c_fields[this._position];
         int currentValue = this._utilCal.get(currentField);
         switch (currentField) {
            case 0:
            case 3:
            case 4:
            case 6:
            case 7:
            case 8:
            case 9:
               break;
            case 1:
            case 5:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            default:
               numeric = true;
               break;
            case 2:
               if (!this.usingAlphaMonth()) {
                  numeric = true;
               }
         }

         if (numeric && !Character.isDigit(key)) {
            key = Keypad.getAltedChar(key);
         }

         if (!Character.isDigit(key)) {
            key = Character.toLowerCase(key);
            String[] options = null;
            switch (currentField) {
               case 2:
                  options = this._symbols.getMonths();
                  break;
               case 7:
                  options = this._symbols.getWeekdays();
                  break;
               case 9:
                  options = this._symbols.getAmPmStrings();
            }

            if (options != null) {
               SLKeyLayout layout = Keypad.getLayout();
               int modif = SLKeyLayout.convertStatusToModifiers(status);
               StringBuffer keysBuffer = layout.getComplementaryChars(key, modif);
               if (keysBuffer != null) {
                  String keys = keysBuffer.toString();
                  int keyLen = keys.length();
                  int minimum = ((CalendarExtensions)this._utilCal).getActualMinimum(currentField);

                  label101:
                  for (int i = currentValue + 1 - minimum; i != currentValue - minimum; i++) {
                     if (i >= options.length) {
                        i = -1;
                     } else {
                        for (int j = 0; j < keyLen; j++) {
                           if (Character.toLowerCase(options[i].charAt(0)) == keys.charAt(j)) {
                              ((CalendarExtensions)this._utilCal).add(currentField, i + minimum - currentValue);
                              break label101;
                           }
                        }
                     }
                  }
               }
            }
         } else {
            int num = Character.digit(key, 10);
            int max = ((CalendarExtensions)this._utilCal).getActualMaximum(currentField) + (currentField == 10 ? 1 : 0);
            int min = ((CalendarExtensions)this._utilCal).getActualMinimum(currentField);
            int fudge = currentField == 2 ? 1 : 0;
            int value = this.isMuddy() ? (currentValue + fudge) * 10 + num : num;
            if (value > max + fudge) {
               value %= 10;
            }

            value = Math.max(value, fudge);
            if (currentField == 10 && value == 12) {
               value = 0;
            }

            value = MathUtilities.clamp(min + fudge, value, max + fudge);
            this._utilCal.set(currentField, value - fudge);
         }
      }

      this.calcCachedData();
      this.fieldChangeNotify(0);
      return true;
   }

   private char transformKeyChar(char key, int status) {
      if (InternalServices.isReducedFormFactor()) {
         switch (key) {
            case ' ':
               switch (this._c_position.getField()) {
                  case 2:
                  case 9:
                     return key;
                  default:
                     return Keypad.map(key, 1);
               }
            case '0':
               key = Keypad.getUnaltedChar('0');
         }
      }

      return key;
   }

   @Override
   protected boolean keyControl(char key, int status, int time) {
      char keyPress = Keypad.getAltedChar(key);
      if (Character.isDigit(keyPress)) {
         return this.keyChar(keyPress, 1, time);
      }

      if ((status & 1) == 0) {
         Screen screen = UiApplication.getUiApplication().getActiveScreen();
         if (screen != null) {
            switch (key) {
               case '\u0080':
                  break;
               case '\u0081':
                  int numEditableBefore = 0;

                  for (int counterEditable = 0; counterEditable < this._position; counterEditable++) {
                     if (this.isSubFieldEditable(counterEditable)) {
                        numEditableBefore++;
                     }
                  }

                  return screen.dispatchTrackwheelEvent(519, -(numEditableBefore + 1), status, time);
               case '\u0082':
                  int numEditableAfter = 0;

                  for (int counterEditable = this._position + 1; counterEditable < this._c_fields.length; counterEditable++) {
                     if (this.isSubFieldEditable(counterEditable)) {
                        numEditableAfter++;
                     }
                  }

                  return screen.dispatchTrackwheelEvent(519, numEditableAfter + 1, status, time);
               case '\u0083':
               case '\u0084':
               default:
                  return screen.dispatchTrackwheelEvent(519, key == 131 ? -1 : 1, status, time);
            }
         }
      }

      return super.keyControl(key, status, time);
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      return Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) != 0 ? this.changeOptionDialog() : false;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            return this.changeOptionDialog();
         default:
            return false;
      }
   }

   @Override
   protected void layout(int width, int height) {
      if (this.isEditable()) {
         this._c_fields = this._format.getFields();
      }

      this._oldWidth = width;
      this.calcLayoutData(width);
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (Ui.getMode() < 2 && this.isEditable()) {
         contextMenu.addItem(_changeOptionsItem);
      }

      contextMenu.setDefault(-1);
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      super.moveFocus(amount, status, time);
      if (((status & 536870912) == 0 || (status & 65536) == 0 && (status & 131072) != 0) && (status & 1) != 0 && this.isEditable()) {
         this.calcDateFromFieldChange(Ui.getIncreaseDirection() * amount);
         this.calcCachedData();
         this.fieldChangeNotify(0);
         this.clampDateOnInterval(status, amount);
         return 0;
      }

      if ((status & 536870912) != 0 && (status & 65536) == 0) {
         if ((status & 131072) != 0 && this.isChangeOnVerticalScrollSet()) {
            this.calcDateFromFieldChange(Ui.getIncreaseDirection() * amount);
            this.calcCachedData();
            this.fieldChangeNotify(0);
            return 0;
         } else {
            return amount;
         }
      } else {
         int dir;
         if (amount > 0) {
            dir = 1;
         } else {
            dir = -1;
         }

         int last = this._c_fields.length - 1;
         int position = this._position;
         int newposition = position;

         label90:
         while (amount != 0) {
            do {
               if ((status & 536870912) != 0) {
                  if (!this.isEditable()) {
                     amount = 0;
                     break label90;
                  }

                  if (position + dir > last) {
                     if (this.isFocusMoveOnHorizontalScrollSet()) {
                        return amount;
                     }

                     position = last;

                     while (!this.isSubFieldEditable(position)) {
                        position--;
                     }
                  } else if (position + dir < 0) {
                     if (this.isFocusMoveOnHorizontalScrollSet()) {
                        return amount;
                     }

                     position = 0;

                     while (!this.isSubFieldEditable(position)) {
                        position++;
                     }
                  } else {
                     position += dir;
                  }
               } else {
                  position += dir;
               }

               if (position < 0 || position > last) {
                  break label90;
               }
            } while (!this.isSubFieldEditable(position));

            newposition = position;
            amount -= dir;
         }

         if (this._position != newposition) {
            this._position = newposition;
         }

         this.calcCachedData();
         this.clampDateOnInterval(status, amount);
         return amount;
      }
   }

   @Override
   public void getFocusRect(XYRect rect) {
      if (this._cursor == null) {
         super.getFocusRect(rect);
      } else {
         rect.set(this._cursor[0]);
         int i = this._cursor.length;

         while (--i > 0) {
            rect.union(this._cursor[i]);
         }
      }
   }

   private void clampDateOnInterval(int status, int amount) {
      if ((status & 1) != 0 && this.isEditable() && this.getCurrentSubfield() == 12) {
         long date = this.getDate();
         long clampedDate = date / 60000 * 60000;
         long newDate = date / this._minuteIncrements;
         newDate *= this._minuteIncrements;
         if (newDate != clampedDate) {
            if (clampedDate > 0 && amount > 0) {
               newDate += this._minuteIncrements;
            } else if (clampedDate < 0 && amount < 0) {
               newDate -= this._minuteIncrements;
            }
         }

         this.setDate(newDate + (date - clampedDate), 0);
      }
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      if (!Trackball.isSupported()) {
         if (direction == 1) {
            this._position = 0;

            while (this._position < this._c_fields.length - 1 && !this.isSubFieldEditable(this._position)) {
               this._position++;
            }
         } else if (direction != -1) {
            while (this._position < this._c_fields.length - 1 && !this.isSubFieldEditable(this._position)) {
               this._position++;
            }

            if (!this.isSubFieldEditable(this._position)) {
               while (this._position > 0 && !this.isSubFieldEditable(this._position)) {
                  this._position--;
               }
            }
         } else {
            this._position = this._c_fields.length - 1;

            while (this._position > 0 && !this.isSubFieldEditable(this._position)) {
               this._position--;
            }
         }
      } else {
         while (this._position < this._c_fields.length && !this.isSubFieldEditable(this._position)) {
            this._position++;
         }

         if (this._first_focus) {
            int i = 0;

            while (i < this._focus_order.length && this._focus_order[i] != this._position) {
               i++;
            }

            label93:
            for (int j = 0; j < i; j++) {
               for (int k = 0; k < this._c_fields.length; k++) {
                  if (this._c_fields[k] == this._focus_order[j] && this.isSubFieldEditable(k)) {
                     this._position = k;
                     break label93;
                  }
               }
            }
         }

         this._first_focus = false;
      }

      this.calcCachedData();
   }

   @Override
   protected void paint(Graphics graphics) {
      this._label.paintSelf(graphics);
      this._dateTextRect.paintSelf(graphics);
   }

   @Override
   public void selectionCopy(Clipboard cb) {
      DateField$FormattedDate formattedDate = new DateField$FormattedDate(this.getDate(), this._format, this._timezone);
      if (ControlledAccess.verifyRRISignatures(true)) {
         cb.put(formattedDate);
      } else {
         cb.put(formattedDate.toString());
      }
   }

   private void calcDateFromFieldChange(int changeAmount) {
      int currentField = this._c_fields[this._position];
      if (this._utilCal == null) {
         this._utilCal = Calendar.getInstance(this._timezone);
      } else {
         switch (currentField) {
            case 0:
            case 3:
            case 4:
            case 6:
            case 8:
               break;
            case 1:
            case 2:
            case 5:
            case 10:
            case 11:
            case 13:
            case 14:
            default:
               ((CalendarExtensions)this._utilCal).add(currentField, changeAmount);
               break;
            case 7:
               ((CalendarExtensions)this._utilCal).add(5, changeAmount);
               break;
            case 9:
               ((CalendarExtensions)this._utilCal).roll(9, changeAmount);
               break;
            case 12:
               int increment = (int)(this._minuteIncrements / 60000);
               int currentValue = this._utilCal.get(currentField) % increment;
               ((CalendarExtensions)this._utilCal)
                  .add(
                     currentField,
                     0 > changeAmount && 0 < currentValue ? (changeAmount + 1) * increment - currentValue : changeAmount * increment - currentValue
                  );
         }

         if (!this.isEraSupported()) {
            int era = this._utilCal.get(0);
            if (era == 0) {
               this._utilCal.set(0, 1);
            }
         }
      }
   }

   public DateField(String label, long date, long style) {
      this(label, date, getDateFormatFromStyle(style), style);
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this._cursor == null) {
         synchronized (_rect) {
            this.getInPlaceRect(_rect);
            this.drawHighlightRegion(graphics, 1, on, _rect.x, _rect.y, _rect.width, _rect.height);
         }
      } else {
         int i = this._cursor.length;

         while (--i >= 0) {
            XYRect rect = this._cursor[i];
            this.drawHighlightRegion(graphics, 1, on, rect.x, rect.y, rect.width, rect.height);
         }
      }
   }

   public DateField(String label, long date, DateFormat format) {
      this(label, date, format, 0);
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (this.isEditable()) {
         this._c_fields = this._format.getFields();
      } else {
         this._c_fields = new int[1];
         this._position = 0;
      }
   }

   public DateField(String label, long date, long style, DateFormat format) {
      this(label, date, format, style);
   }

   @Override
   public int getPreferredHeight() {
      int height = this.getFont().getHeight();
      int rows = this._isLabelOwnLine && this._label.getTextString() != null ? 2 : 1;
      return height * rows;
   }

   @Override
   public int getPreferredWidth() {
      StringBuffer text = (StringBuffer)this._dateTextRect.getText();
      text.setLength(0);
      this._format.format(this._utilCal, text, null);
      Font font = this.getFont();
      int dateWidth = font.getBounds(text, 0, text.length());
      int labelWidth = font.getBounds(this._label.getTextString());
      if (labelWidth != 0) {
         labelWidth += 5;
      }

      return Math.max(this._minimumWidth, dateWidth + labelWidth);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this._label.applyTheme();
      this._isLabelOwnLine = ThemeManager.getActiveTheme().isLabelOnOwnLine();
      if (this._isLabelOwnLine) {
         int textStyle = this.getFieldStyle();
         textStyle &= -8;
         textStyle |= 6;
         this._dateTextRect.setStyle(textStyle);
      }
   }

   private void calcLayoutData(int width) {
      this._position = MathUtilities.clamp(0, this._position, this._c_fields.length - 1);
      this._c_position.setField(this._c_fields[this._position]);
      this.formatDate(this._c_position);
      int labelXOffset = 0;
      int labelYOffset = 0;
      this._label.layout(width, Integer.MAX_VALUE);
      int textStyle = this.getFieldStyle();
      this._dateTextRect.layout(width, Integer.MAX_VALUE);
      int extentX = this._dateTextRect.getWidth();
      int extentY = this._dateTextRect.getHeight();
      int labelWidth = this._label.getWidth();
      int dateXOffset;
      int dateYOffset;
      if (labelWidth <= 0) {
         dateXOffset = 0;
         dateYOffset = 0;
      } else if (this._isLabelOwnLine) {
         dateXOffset = 0;
         dateYOffset = this._label.getHeight();
         extentY += dateYOffset;
      } else {
         int desiredWidth = labelWidth + 5 + extentX;
         if (desiredWidth <= width) {
            dateXOffset = labelWidth + 5;
            dateYOffset = this._label.getLineHeight(0) - this._dateTextRect.getLineHeight(0);
            if (dateYOffset < 0) {
               labelYOffset = -dateYOffset;
               dateYOffset = 0;
            }

            extentX = desiredWidth;
            this._dateTextRect.reduceWidthToFit(width - dateXOffset);
         } else {
            dateXOffset = 0;
            dateYOffset = this._label.getHeight();
            extentX = Math.max(labelWidth, extentX);
            extentY += dateYOffset;
         }
      }

      if ((textStyle & 1152921504606846976L) != 0 || (textStyle & 7) != 6) {
         extentX = width;
      }

      if (this._minimumWidth > 0) {
         extentX = Math.max(extentX, this._minimumWidth);
      }

      this._label.setPosition(labelXOffset, labelYOffset);
      this._dateTextRect.setPosition(dateXOffset, dateYOffset);
      this.setExtent(extentX, extentY);
      if (this.isEditable() && this.isSubFieldEditable(this._position)) {
         this._cursor = this._dateTextRect.getTextBounds(this._c_position.getBeginIndex(), this._c_position.getEndIndex());
      } else {
         this._cursor = null;
      }
   }

   public DateField(String label, long date, int style, DateFormat format) {
      this(label, date, (long)style, format);
   }

   public DateField(String label, long date, DateFormat format, long style) {
      super(validateStyle(style));
      this.setTag(TAG);
      this._label.setTag(TAG_LABEL);
      this._isLabelOwnLine = ThemeManager.getActiveTheme().isLabelOnOwnLine();
      this._label.setText(label);
      this._format = format;
      if (this.isEditable()) {
         this._c_fields = this._format.getFields();
      } else {
         this._c_fields = new int[1];
      }

      if (date != Long.MIN_VALUE) {
         this._utilCal = Calendar.getInstance(this._timezone);
         ((CalendarExtensions)this._utilCal).setTimeLong(date);
      }

      StringBuffer dateStringBuffer = new StringBuffer();
      format.format(this._utilCal, dateStringBuffer, this._c_position);
      int textStyle = this.getFieldStyle();
      if (this._isLabelOwnLine) {
         textStyle &= -8;
         textStyle |= 6;
      }

      this._dateTextRect = new TextRect(this, dateStringBuffer, textStyle);
   }

   @Override
   public String toString() {
      return this._dateTextRect.getTextString();
   }

   private static long validateStyle(long style) {
      if ((style & 7) == 0) {
         style |= 5;
      }

      if ((style & 13510798882111488L) == 0) {
         style |= 4503599627370496L;
      }

      if ((style & 54043195528445952L) == 0) {
         style |= 18014398509481984L;
      }

      return style & -49;
   }

   private static DateFormat getDateFormatFromStyle(long style) {
      switch ((int)(style & 48)) {
         case 16:
            return DateFormat.getInstance(48);
         case 32:
            return DateFormat.getInstance(6);
         case 48:
            return DateFormat.getInstance(54);
         default:
            throw new IllegalArgumentException("style must include one of DATE, TIME, or DATE_TIME.");
      }
   }

   private boolean usingAlphaMonth() {
      String dateFormat = ((SimpleDateFormat)this._format).toPattern();
      int index = dateFormat.indexOf(77);
      if (index >= 0) {
         int numMs = 0;

         do {
            numMs++;
            index++;
         } while (index < dateFormat.length() && dateFormat.charAt(index) == 'M');

         if (numMs > 2) {
            return true;
         }
      }

      return false;
   }

   private boolean isChangeOnVerticalScrollSet() {
      return (this.getStyle() & 64) > 0;
   }

   private boolean isFocusMoveOnHorizontalScrollSet() {
      return (this.getStyle() & 128) > 0;
   }

   private void formatDate(FieldPosition fieldPosition) {
      StringBuffer dateStringBuffer = (StringBuffer)this._dateTextRect.getText();
      dateStringBuffer.setLength(0);
      this._format.format(this._utilCal, dateStringBuffer, fieldPosition);
      this._dateTextRect.setText(dateStringBuffer);
   }

   @Override
   public int getAccessibleRole() {
      return 9;
   }

   @Override
   public String getAccessibleName() {
      return this.getLabel() + " " + this.toString();
   }
}
