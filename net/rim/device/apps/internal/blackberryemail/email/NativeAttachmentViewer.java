package net.rim.device.apps.internal.blackberryemail.email;

import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.ResourceProvider;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class NativeAttachmentViewer implements RenderingApplication, ResourceProvider {
   private Screen _screen;
   private AbstractEmailFileAttachment _attachment;

   NativeAttachmentViewer(Screen screen, AbstractEmailFileAttachment attachment) {
      if (attachment != null && screen != null) {
         this._screen = screen;
         this._attachment = attachment;
      } else {
         throw new Object();
      }
   }

   public void init() {
      this._screen.add(this.createFieldForItem());
   }

   @Override
   public Object eventOccurred(Event event) {
      if (event.getUID() == 10002) {
         Application.getApplication().invokeAndWait(new NativeAttachmentViewer$1(this));
      }

      return null;
   }

   @Override
   public String getHTTPCookie(String url) {
      return null;
   }

   @Override
   public HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      return null;
   }

   @Override
   public InputConnection getInputConnection(RequestedResource resource, BrowserContent referrer) {
      return this._attachment.getInputConnection();
   }

   @Override
   public int getAvailableHeight(BrowserContent browserContent) {
      return this._screen.getHeight();
   }

   @Override
   public int getAvailableWidth(BrowserContent browserContent) {
      return this._screen.getWidth();
   }

   @Override
   public int getHistoryPosition(BrowserContent browserContent) {
      return -1;
   }

   @Override
   public void invokeRunnable(Runnable runnable) {
      new NativeAttachmentViewer$2(this, runnable).start();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Field createFieldForItem() {
      try {
         RenderingSession session = RenderingSession.getNewInstance();
         if (session == null) {
            return this.createRenderErrorField();
         }

         RenderingOptions options = session.getRenderingOptions();
         if (options == null) {
            return this.createRenderErrorField();
         }

         options.setProperty(4550690918222697397L, 26, false);
         options.setProperty(-2413443615265356506L, 1, true);
         InputConnection input = this.getInputConnection(null, null);
         if (input != null) {
            BrowserContent content = session.getBrowserContent(input, null, this, 0);
            if (content != null) {
               Field contentField = content.getDisplayableContent();
               if (contentField != null) {
                  this.invokeRunnable(new NativeAttachmentViewer$3(this, content, contentField));
               }

               return contentField;
            }
         }
      } catch (Throwable var7) {
         this.logError(error);
         return this.createRenderErrorField();
      }

      return this.createRenderErrorField();
   }

   private Field createRenderErrorField() {
      return (Field)(new Object(
         ((StringBuffer)(new Object()))
            .append(EmailResources.getString(107))
            .append('\n')
            .append(this._attachment != null ? this._attachment.getDisplayName() : "")
            .toString(),
         36028797018963968L
      ));
   }

   private void logError(RenderingException error) {
      EventLogger.logEvent(-1237457833540244999L, error.toString().getBytes(), 2);
   }
}
