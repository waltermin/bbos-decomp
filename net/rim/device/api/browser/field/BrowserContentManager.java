package net.rim.device.api.browser.field;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class BrowserContentManager extends VerticalFieldManager {
   private BrowserContent _browserContent;
   private RenderingSession _renderingSession = RenderingSession.getNewInstance();

   public BrowserContentManager(long style) {
      super(style);
   }

   public final void setContent(HttpConnection httpConnection, RenderingApplication renderingApplication, Event event) {
      try {
         this._browserContent = this._renderingSession.getBrowserContent(httpConnection, renderingApplication, event);
         this.handleNewContent();
      } catch (RenderingException var5) {
      }
   }

   public final void setContent(HttpConnection httpConnection, RenderingApplication renderingApplication, int renderingFlags) {
      try {
         this._browserContent = this._renderingSession.getBrowserContent(httpConnection, renderingApplication, renderingFlags);
         this.handleNewContent();
      } catch (RenderingException var5) {
      }
   }

   private final void handleNewContent() {
      if (this._browserContent != null) {
         try {
            this._browserContent.finishLoading();
         } catch (RenderingException var5) {
         }

         Field field = this._browserContent.getDisplayableContent();
         if (field != null) {
            synchronized (Application.getEventLock()) {
               this.deleteAll();
               this.add(this._browserContent.getDisplayableContent());
            }

            this.invalidate();
         }
      }
   }

   public final RenderingSession getRenderingSession() {
      return this._renderingSession;
   }
}
