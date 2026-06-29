package net.rim.device.apps.internal.browser.webfeed;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.TextGraphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.cldc.util.CalendarExtensions;

final class WebFeedField extends VerticalFieldManager implements Runnable {
   private BrowserContent _browserContent;
   private RenderingSession _renderingSession;
   private WebFeedItem[] _items;
   private long[] _times;
   private Calendar _renderedCalendar;
   private TimeZone _timeZone;
   private DateFormat _dateFormat = new SimpleDateFormat(40);
   private DateFormat _timeFormat = new SimpleDateFormat(7);
   TextGraphics _tg;
   int _timeWidth;
   private int _tooltipTimerId = -1;
   private UiApplication _app;
   private WebFeedPopup _tooltip;
   private static final Tag TAG = Tag.create("webfeeditem");

   public WebFeedField(RenderingSession renderingSession) {
      super(3459081173169340416L);
      this.setTag(TAG);
      this._renderingSession = renderingSession;
      this._items = new WebFeedItem[0];
      this._times = new long[0];
      this._tg = new TextGraphics(Font.getDefault());
      DrawTextParam param = Ui.getTmpDrawTextParam();
      this._tg.setStyle(1);
      this._timeWidth = this._tg.measureText("88:88p ", 0, 7, param, null);
      Ui.returnTmpDrawTextParam(param);
      this._renderedCalendar = Calendar.getInstance();
      this._timeZone = this._renderedCalendar.getTimeZone();
      this._app = UiApplication.getUiApplication();
      this._tooltip = new WebFeedPopup();
   }

   public final void addItem(WebFeedItem item) {
      WebFeedStatusStore.getInstance().updateItemStatus(item);
      long date = item.getPubDate();
      int i = this._items.length - 1;

      while (i >= 0 && this._times[i] <= date) {
         i--;
      }

      synchronized (Application.getEventLock()) {
         if (i < 0 || !DateTimeUtilities.isSameDate(date, this._times[i], this._timeZone, this._timeZone, this._renderedCalendar)) {
            Arrays.insertAt(this._items, null, ++i);
            ((CalendarExtensions)this._renderedCalendar).setTimeLong(date);
            this._renderedCalendar.set(11, 23);
            this._renderedCalendar.set(12, 59);
            this._renderedCalendar.set(13, 59);
            this._renderedCalendar.set(14, 999);
            Arrays.insertAt(this._times, ((CalendarExtensions)this._renderedCalendar).getTimeLong(), i);
            Field dateField = new WebFeedDateField(this._dateFormat.format(this._renderedCalendar));
            this.insert(dateField, i);
         }

         Arrays.insertAt(this._items, item, ++i);
         Arrays.insertAt(this._times, date, i);
         WebFeedItemManager itemManager = new WebFeedItemManager(this, item);
         ((CalendarExtensions)this._renderedCalendar).setTimeLong(date);
         item.setFormattedTime(this._timeFormat.format(this._renderedCalendar));
         item.setFont(this.getFont());
         itemManager.add(item);
         this.insert(itemManager, i);
      }
   }

   final void markPriorOpened(int index) {
      WebFeedStatusStore store = WebFeedStatusStore.getInstance();

      for (int i = index; i < this._items.length; i++) {
         store.setItemStatus(this._items[i], true);
      }
   }

   @Override
   public final void focusChangeNotify(int action) {
      super.focusChangeNotify(action);
      this.cancelTooltip();
      this._tooltipTimerId = this._app.invokeLater(this, 750, false);
   }

   public final WebFeedItem[] getItems() {
      return this._items;
   }

   final void setBrowserContent(BrowserContent browserContent) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final BrowserContent getBrowserContent() {
      return this._browserContent;
   }

   final RenderingSession getRenderingSession() {
      return this._renderingSession;
   }

   @Override
   protected final void onObscured() {
      super.onObscured();
      this.cancelTooltip();
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      this.cancelTooltip();
   }

   private final void cancelTooltip() {
      if (this._tooltipTimerId != -1) {
         this._app.cancelInvokeLater(this._tooltipTimerId);
         this._tooltipTimerId = -1;
      }
   }

   @Override
   public final void run() {
      this._tooltipTimerId = -1;
      Field field = this.getFieldWithFocus();
      if (field != null) {
         WebFeedItem item = this._items[field.getIndex()];
         if (item != null && item.isTextTruncated()) {
            XYRect rect = Ui.getTmpXYRect();
            rect.y = 0;
            item.transformToScreen(rect);
            int fieldYPos = rect.y;
            Ui.returnTmpXYRect(rect);
            this._tooltip.show(this._app, item.getTitle(), 0, fieldYPos, item.getHeight());
         }
      }
   }
}
