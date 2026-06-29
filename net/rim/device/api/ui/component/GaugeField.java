package net.rim.device.api.ui.component;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringProvider;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.Border;

public class GaugeField extends Field implements FieldLabelProvider {
   private int _min;
   private int _max;
   private int _current;
   private String _label;
   private boolean _alt = true;
   private int _barStart;
   private int _barEnd;
   private int _barCurrent;
   private Border _borderBar;
   private ThemeAttributeSet _tasBar;
   private ThemeAttributeSet _tasFill;
   private int _barBorderTop = 0;
   private int _barBorderBottom = 0;
   private int _barBorderLeft = 0;
   private StringBuffer _value = new StringBuffer();
   private static Tag TAG = Tag.create("gauge");
   private static Tag TAG_FILL = Tag.create("gauge-fill");
   private static Tag TAG_BAR = Tag.create("gauge-bar");
   public static final int NO_TEXT;
   public static final int PERCENT;
   public static final long LABEL_AS_PROGRESS;
   public static final int CURRENT_WITH_MAX;
   private static final int BAR_CLEARANCE;
   private static MenuItem _changeOptionsItem = new GaugeField$1(CommonResource.getBundle(), 1, 30201, 10);

   public void reset(String label, int min, int max, int start) {
      this.$init0(label, min, max, start);
      this.fieldChangeNotify(Integer.MIN_VALUE);
   }

   public int getValueMin() {
      return this._min;
   }

   public int getValueMax() {
      return this._max;
   }

   public int getValue() {
      return this._current;
   }

   public void setValue(int value) {
      this.reset(this._label, this._min, this._max, value);
   }

   boolean changeOptionDialog() {
      if (this.getOriginal() != this) {
         return false;
      } else {
         GaugeField fake = this.getChangeOptionGaugeField(null);
         GaugeField$GaugeFieldPopupScreen dialog = new GaugeField$GaugeFieldPopupScreen(fake);
         boolean accepted = dialog.doModal();
         if (accepted) {
            this.setNonProgrammaticValue(fake.getValue());
            return true;
         } else {
            fake.setValue(this.getValue());
            return true;
         }
      }
   }

   protected GaugeField getChangeOptionGaugeField(String label) {
      GaugeField field = new GaugeField(label, this._min, this._max, this._current, this.getStyle() | 18014398509481984L | 4503599627370496L);
      field.setCookie(this.getCookie());
      field.setChangeListener(this.getChangeListener());
      field._alt = false;
      return field;
   }

   @Override
   public void setLabel(String label) {
      this.$init0(label, this._min, this._max, this._current);
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public String getLabel() {
      return this._label;
   }

   @Override
   protected void applyTheme() {
      this._tasBar = ThemeManager.getActiveTheme().getAttributeSet(TAG_BAR);
      this._tasFill = ThemeManager.getActiveTheme().getAttributeSet(TAG_FILL);
      if (this._tasBar != null) {
         this.setThemeAttributesSpecial(this._tasBar, null);
         this._borderBar = ThemeAttributeSet.getBorder(this);
         this.setThemeAttributesSpecial(null, null);
      }

      super.applyTheme();
   }

   @Override
   protected void layout(int width, int height) {
      this.setThemeAttributesSpecial(this._tasBar, null);
      Border barBorder = ThemeAttributeSet.getBorder(this);
      if (barBorder != null) {
         this._barBorderTop = barBorder.getTop();
         this._barBorderBottom = barBorder.getBottom();
         this._barBorderLeft = barBorder.getLeft();
         height = this.getFont().getHeight() + this._barBorderTop + this._barBorderBottom;
      } else {
         height = this.getFont().getHeight();
      }

      this.setThemeAttributesSpecial(null, null);
      this.setExtent(width, height);
      this.barLayout();
   }

   @Override
   public int getPreferredHeight() {
      return this.getFont().getHeight();
   }

   @Override
   public int getPreferredWidth() {
      return Display.getWidth() - 2;
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (this.isStyle(4503599627370496L) && Ui.getMode() < 2) {
         contextMenu.addItem(_changeOptionsItem);
      }
   }

   @Override
   protected void paint(Graphics graphics) {
      long style = this.getStyle();
      if ((style & 65536) == 0 && this._label != null) {
         graphics.drawText(this._label, 0, 1 + this._barBorderTop);
      }

      this.setThemeAttributesSpecial(this._tasBar, graphics);
      graphics.setColor(ThemeAttributeSet.getColor(this, 0));
      graphics.fillRect(this._barStart, 1, this._barEnd - this._barStart, this.getHeight() - 2);
      this._borderBar = ThemeAttributeSet.getBorder(this);
      if (this._borderBar != null) {
         graphics.setColor(ThemeAttributeSet.getColor(this, 3));
         XYRect rect = Ui.getTmpXYRect();
         rect.set(this._barStart, 1, this._barEnd - this._barStart, this.getHeight() - 2);
         this._borderBar.paint(graphics, rect);
         Ui.returnTmpXYRect(rect);
      }

      if ((style & 2) == 0) {
         graphics.setColor(ThemeAttributeSet.getColor(this, 1));
         graphics.drawText(this._value, 0, this._value.length(), this._barStart, 1 + this._barBorderTop, 52, this._barEnd - this._barStart);
      }

      this.setThemeAttributesSpecial(this._tasFill, graphics);
      this._borderBar = ThemeAttributeSet.getBorder(this);
      graphics.pushRegion(
         this._barStart + this._barBorderLeft,
         1 + this._barBorderTop,
         this._barCurrent - this._barStart,
         this.getHeight() - 2 - this._barBorderTop - this._barBorderBottom,
         0,
         0
      );
      graphics.setColor(ThemeAttributeSet.getColor(this, 4));
      graphics.fillRect(0, 0, this._barCurrent - this._barStart, this.getHeight() - 2 - this._barBorderTop - this._barBorderBottom);
      if (this._borderBar != null) {
         graphics.setColor(ThemeAttributeSet.getColor(this, 3));
         XYRect rect = Ui.getTmpXYRect();
         rect.set(0, 0, this._barCurrent - this._barStart, this.getHeight() - 2 - this._barBorderTop - this._barBorderBottom);
         this._borderBar.paint(graphics, rect);
         Ui.returnTmpXYRect(rect);
      }

      if ((style & 2) == 0) {
         graphics.setColor(ThemeAttributeSet.getColor(this, 5));
         graphics.drawText(this._value, 0, this._value.length(), -this._barBorderLeft, 0, 52, this._barEnd - this._barStart);
      }

      graphics.popContext();
      this.setThemeAttributesSpecial(null, graphics);
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      if (!this.isEditable()) {
         return amount;
      }

      if (this._alt && (status & 1) != 1) {
         return amount;
      }

      this.$init0(this._label, this._min, this._max, MathUtilities.clamp(this._min, this._current + amount, this._max));
      this.fieldChangeNotify(0);
      return 0;
   }

   private void barLayout() {
      this._barStart = 0;
      if (this._label != null && !this.isStyle(65536)) {
         this._barStart = this.getFont().getBounds(this._label);
      }

      this._barStart++;
      this._barEnd = this.getWidth() - 1;
      if (this._current == this._max) {
         this._barCurrent = this._barEnd;
      } else {
         long barWidth = this._barEnd - this._barStart;
         long rangeWidth = this._max - this._min;
         this._barCurrent = (int)((barWidth << 32) / rangeWidth * (this._current - this._min) >> 32) + this._barStart;
      }
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this._borderBar != null) {
         if (on) {
            graphics.setColor(ThemeAttributeSet.getColor(this, 1));
         } else {
            graphics.setColor(ThemeAttributeSet.getColor(this, 0));
         }

         XYRect rect = Ui.getTmpXYRect();
         rect.set(this._barStart - 1, 0, this._barEnd - this._barStart + 1, this.getHeight());
         this._borderBar.paint(graphics, rect);
         Ui.returnTmpXYRect(rect);
      } else {
         if (on) {
            graphics.setColor(ThemeAttributeSet.getColor(this, 1));
            graphics.drawRect(this._barStart - 1, 0, this._barEnd - this._barStart + 1, this.getHeight());
         }
      }
   }

   public GaugeField(String label, int min, int max, int start, long style) {
      super(style);
      this.setTag(TAG);
      this.$init0(label, min, max, start);
   }

   private void $init0(String label, int min, int max, int start) {
      if (min >= max) {
         throw new IllegalArgumentException();
      }

      if (start >= min && start <= max) {
         this._label = label;
         this._min = min;
         this._max = max;
         this._current = start;
         this._value.setLength(0);
         if (this.isStyle(4)) {
            if (this._current == this._max) {
               this._value.append("100%");
            } else {
               this._value.append((this._current - this._min) * 100 / (this._max - this._min));
               this._value.append('%');
            }
         } else if (this.isStyle(8)) {
            this._value.append(this._current);
            this._value.append(" / ");
            this._value.append(this._max);
         } else if (this.isStyle(65536)) {
            if (this._label != null) {
               this._value.append(this._label);
            }
         } else {
            this._value.append(this._current);
         }

         Manager manager = this.getManager();
         if (manager != null && manager.isValidLayout()) {
            this.barLayout();
            this.invalidate();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public GaugeField() {
      this(null, 0, 100, 0, 4);
   }

   private void setNonProgrammaticValue(int value) {
      this.$init0(this._label, this._min, this._max, value);
      this.fieldChangeNotify(0);
   }
}
