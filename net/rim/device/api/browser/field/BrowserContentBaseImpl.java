package net.rim.device.api.browser.field;

import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.utility.general.URI;

public class BrowserContentBaseImpl implements BrowserContent {
   protected EncodedImage _icon;
   protected String _iconUrl;
   protected RenderingApplication _renderingApplication;
   protected RenderingOptions _renderingOptions;
   protected String _url;
   protected String _baseUrl;
   protected String _title;
   protected String _error;
   private Field _content;
   protected int _flags;
   protected BrowserPageContext _context;

   public void setContent(Field field) {
      this._content = field;
   }

   public void setTitle(String title) {
      if (title != null && title.length() != 0) {
         this._title = title;
      }
   }

   public void setBaseUrl(String url) {
      this._baseUrl = url;
   }

   public final int getSharedFlags() {
      return this._flags & 0xFF & -8;
   }

   public void setBrowserPageContext(BrowserPageContext context) {
      this._context = context;
   }

   public void setIconUrl(String iconUrl) {
      this._iconUrl = iconUrl;
   }

   public String getIconUrl() {
      return this._iconUrl;
   }

   public void setIcon(EncodedImage icon) {
      this._icon = icon;
   }

   @Override
   public EncodedImage getIcon() {
      return this._icon;
   }

   @Override
   public String getError() {
      return this._error;
   }

   @Override
   public String getURL() {
      return this._url;
   }

   @Override
   public String getTitle() {
      return this._title;
   }

   @Override
   public BrowserPageContext getBrowserPageContext() {
      return this._context;
   }

   @Override
   public RenderingOptions getRenderingOptions() {
      return this._renderingOptions;
   }

   @Override
   public String resolveUrl(String relative) {
      return URI.getAbsoluteURL(relative, this._baseUrl != null ? this._baseUrl : this._url);
   }

   @Override
   public void resourceReady(RequestedResource resource) {
   }

   @Override
   public void finishLoading() {
   }

   @Override
   public final Field getDisplayableContent() {
      return this._content;
   }

   @Override
   public RenderingApplication getRenderingApplication() {
      return this._renderingApplication;
   }

   @Override
   public int getRenderingFlags() {
      return this._flags;
   }

   @Override
   public void setError(String string) {
      this._error = string;
   }

   public BrowserContentBaseImpl(String url, Field content, RenderingApplication renderingApplication, RenderingOptions renderingOptions, int flags) {
      if (renderingOptions == null) {
         throw new IllegalArgumentException("Rendering options cannot be null");
      }

      this._flags = flags;
      this._renderingOptions = renderingOptions;
      this._renderingApplication = renderingApplication;
      this._url = url;
      this._content = content;
   }
}
