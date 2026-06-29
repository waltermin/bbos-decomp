package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;

final class CalendarOptionsEntryScreen$CalendarFolderField extends Field {
   private TextRect _label = (TextRect)(new Object(this, 64));
   private TextRect _text = (TextRect)(new Object(this));
   private int _color;
   private CalendarKey _key;
   private int _iconSpace;
   private static final int ICON_PADDING;

   public CalendarOptionsEntryScreen$CalendarFolderField(String name, String value, CalendarKey calendarKey) {
      super(18014398509481984L);
      this._key = calendarKey;
      this._label.setText(name);
      this._text.setText(value);
      this._iconSpace = this.getFont().getHeight();
      this.refresh();
   }

   public final void refresh() {
      this._color = CalendarOptions.getOptions().getCalendarColour(this._key);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this._label.applyTheme();
      this._text.applyTheme();
      this._iconSpace = this.getFont().getHeight();
   }

   @Override
   protected final void layout(int width, int height) {
      this._label.layout(width - this._iconSpace, height);
      this._label.setPosition(this._iconSpace, 0);
      this._text.layout(width - this._iconSpace, height);
      if (!ThemeManager.getActiveTheme().isLabelOnOwnLine() && this._label.getWidth() + this._text.getWidth() + this._iconSpace <= width) {
         this._text.setPosition(width - this._text.getWidth(), 0);
      } else {
         this._text.setPosition(this._iconSpace, this._label.getHeight());
      }

      this.setExtent(width, this._text.getExtent().Y2());
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._color != -1) {
         int oldColor = graphics.getColor();
         XYRect rect = Ui.getTmpXYRect();
         rect.set(2, 2, this._iconSpace - 4, this._iconSpace - 4);
         graphics.setColor(this._color);
         graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
         graphics.setColor(oldColor);
         graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
         Ui.returnTmpXYRect(rect);
      }

      this._label.paintSelf(graphics);
      this._text.paintSelf(graphics);
   }
}
