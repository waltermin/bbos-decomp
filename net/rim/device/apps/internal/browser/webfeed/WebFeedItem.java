package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.internal.ui.IconCollection;

final class WebFeedItem extends Field {
   private String _description;
   private String _link;
   private String _title;
   private long _pubDate;
   private String _formattedTime;
   private String _guid;
   private int _status = 1;
   private boolean _isTextTruncated;
   private String _baseUrl;
   private String _enclosureLink;
   private int _enclosureSize;
   private String _enclosureMimeType;
   public static final int STATUS_READ = 0;
   public static final int STATUS_UNREAD = 1;
   private static final int ICON_COUNT = 2;
   private static IconCollection _icons = IconCollection.get("net_rim_browser_webfeeds", 2);

   public WebFeedItem() {
      super(18014398509481984L);
      this._pubDate = System.currentTimeMillis();
   }

   public final void setBaseUrl(String baseUrl) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final String getBaseUrl() {
      return this._baseUrl;
   }

   public final void setDescription(String description) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setPubDate(String pubDate) {
      this._pubDate = HttpDateParser.parse(pubDate);
      if (this._pubDate == 0) {
         this._pubDate = System.currentTimeMillis();
      }
   }

   public final void setStatus(int status) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getStatus() {
      return this._status;
   }

   public final long getPubDate() {
      return this._pubDate;
   }

   public final String getDescription() {
      return this._description;
   }

   public final void setLink(String link) {
      this._link = URI.getAbsoluteURL(link, this._baseUrl);
   }

   public final String getLink() {
      return this._link;
   }

   public final void setTitle(String title) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final String getTitle() {
      return this._title;
   }

   public final void setFormattedTime(String time) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setGuid(String guid) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final String getGuid() {
      if (this._guid == null && this._link != null) {
         this._guid = this._link + this._pubDate;
      }

      return this._guid;
   }

   public final void setEnclosure(String url, int byteSize, String mimeType) {
      this._enclosureLink = url;
      this._enclosureSize = byteSize;
      this._enclosureMimeType = mimeType;
   }

   public final String getEnclosureLink() {
      return this._enclosureLink;
   }

   public final int getEnclosureSize() {
      return this._enclosureSize;
   }

   public final String getEnclosureType() {
      return this._enclosureMimeType;
   }

   final void repaintItem() {
      this.invalidate();
   }

   final boolean isTextTruncated() {
      return this._isTextTruncated;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, this.getFont().getHeight());
   }

   @Override
   protected final void paint(Graphics g) {
      WebFeedField parentField = ((WebFeedItemManager)this.getManager()).getParent();
      int width = this.getContentWidth();
      int fg = g.getColor();
      int x = 0;
      if (this._status == 1) {
         x = _icons.paint(g, 0, 0, 0);
         parentField._tg.setStyle(1);
      } else {
         x = _icons.paint(g, 0, 0, 1);
         parentField._tg.setStyle(0);
      }

      DrawTextParam param = Ui.getTmpDrawTextParam();
      param.iMaxAdvance = parentField._timeWidth - 8;
      param.iAlignment = 5;
      parentField._tg.drawText(g, this._formattedTime, 0, this._formattedTime.length(), x, 0, param, null);
      if (this._title != null && this._title.length() > 0 && parentField._timeWidth < width) {
         TextMetrics metrics = Ui.getTmpTextMetrics();
         param.iAlignment = 0;
         param.iMaxAdvance = width - parentField._timeWidth - x;
         param.iTruncateWithEllipsis = 2;
         int numChars = this._title.length();
         parentField._tg.drawText(g, this._title, 0, numChars, parentField._timeWidth + x, 0, param, metrics);
         this._isTextTruncated = metrics.iCharacters < numChars;
         Ui.returnTmpTextMetrics(metrics);
      }

      Ui.returnTmpDrawTextParam(param);
      g.setColor(fg);
   }
}
