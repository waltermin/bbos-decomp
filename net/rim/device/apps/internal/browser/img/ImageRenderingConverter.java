package net.rim.device.apps.internal.browser.img;

import java.io.InputStream;
import java.util.Enumeration;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.vm.Array;

public final class ImageRenderingConverter extends BrowserContentProvider {
   private static String[] _accept = new Object[0];
   private static String _acceptString;
   private static boolean _storedAnimationValue = false;

   private static final void refreshMimeTypes(RenderingOptions renderingOptions) {
      if (renderingOptions != null) {
         boolean newValue = renderingOptions.getPropertyWithIntValue(4550690918222697397L, 12, 10) == 0;
         if (newValue != _storedAnimationValue) {
            _acceptString = null;
            String oppositeValue = ((StringBuffer)(new Object("image/gif;anim="))).append((char)(_storedAnimationValue ? '0' : '1')).toString();

            for (int i = 0; i < _accept.length; i++) {
               if (StringUtilities.strEqualIgnoreCase(_accept[i], oppositeValue, 1701707776)) {
                  _accept[i] = ((StringBuffer)(new Object("image/gif;anim="))).append((char)(newValue ? '0' : '1')).toString();
               }
            }

            _storedAnimationValue = newValue;
         }
      }
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      refreshMimeTypes(renderingOptions);
      return _accept;
   }

   @Override
   public final String[] getSupportedMimeTypes() {
      return _accept;
   }

   public static final String getAcceptString() {
      if (_acceptString == null) {
         if (_accept.length > 0) {
            StringBuffer buff = (StringBuffer)(new Object(_accept[0]));

            for (int i = 1; i < _accept.length; i++) {
               buff.append(',');
               buff.append(_accept[i]);
            }

            _acceptString = buff.toString();
         } else {
            _acceptString = "";
         }
      }

      return _acceptString;
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext browserContentProviderContext) {
      RenderingApplication renderingApplication = browserContentProviderContext.getRenderingApplication();
      RenderingSession renderingSession = browserContentProviderContext.getRenderingSession();
      InputConnection inputConnection = browserContentProviderContext.getInputConnection();
      InputStream inputStream = browserContentProviderContext.getInputStream();
      int flags = browserContentProviderContext.getFlags();
      String url = RendererControl.getUrl(inputConnection);
      Event event = browserContentProviderContext.getEvent();
      String referrer = event != null ? event.getSourceURL() : null;
      if (inputConnection == null) {
         throw new Object();
      }

      try {
         if (inputStream == null) {
            inputStream = inputConnection.openInputStream();
         }

         ImageRenderer renderer = new ImageRenderer(inputConnection, inputStream, url, renderingSession, renderingApplication, referrer, flags);
         return renderer.processData();
      } finally {
         throw new Object();
      }
   }

   static {
      Enumeration mimeTypes = EncodedImage.getSupportedMIMETypes();

      for (int i = _accept.length; mimeTypes.hasMoreElements(); i++) {
         Array.resize(_accept, i + 1);
         _accept[i] = (String)mimeTypes.nextElement();
         if (StringUtilities.strEqualIgnoreCase(_accept[i], "image/gif", 1701707776)) {
            _accept[i] = ((StringBuffer)(new Object())).append(_accept[i]).append(";anim=1").toString();
         }
      }
   }
}
