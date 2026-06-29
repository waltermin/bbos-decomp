package net.rim.device.apps.internal.browser.plugin.media;

import java.io.InputStream;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingException;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.plugin.BrowserContentProvider;
import net.rim.device.api.browser.plugin.BrowserContentProviderContext;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.browser.common.ProtocolContentProvider;
import net.rim.device.apps.internal.browser.plugin.media.field.MediaBrowserField;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.internal.media.MediaNatives;
import net.rim.device.internal.system.InternalServices;

public final class MediaRenderingConverter extends BrowserContentProvider implements ProtocolContentProvider {
   private static final String[] ACCEPT;
   private static final String[] ACCEPT_PROTOCOLS = new String[]{"rtsp"};

   @Override
   public final String[] getSupportedMimeTypes() {
      return ACCEPT;
   }

   @Override
   public final String[] getAccept(RenderingOptions renderingOptions) {
      return ACCEPT;
   }

   @Override
   public final String[] getAcceptProtocols() {
      return ACCEPT_PROTOCOLS;
   }

   @Override
   public final BrowserContent getBrowserContent(BrowserContentProviderContext providerContext) throws RenderingException {
      RenderingApplication renderingApplication = providerContext.getRenderingApplication();
      RenderingSession renderingSession = providerContext.getRenderingSession();
      InputConnection inputConnection = providerContext.getInputConnection();
      InputStream in = providerContext.getInputStream();
      int flags = providerContext.getFlags();
      Event event = providerContext.getEvent();
      String baseUrl = RendererControl.getUrl(inputConnection);
      if (inputConnection != null && renderingSession != null) {
         RenderingOptions renderingOptions = renderingSession.getRenderingOptions();
         boolean embedded = false;
         if ((flags & 16) != 0) {
            embedded = true;
         }

         BrowserContentBaseImpl browserContent = new BrowserContentBaseImpl(baseUrl, null, renderingApplication, renderingOptions, flags);
         MediaBrowserField field = new MediaBrowserField(inputConnection, in, baseUrl, browserContent, embedded, event);
         browserContent.setContent(field);
         browserContent.setBrowserPageContext(field);
         return browserContent;
      } else {
         throw new RenderingException();
      }
   }

   static {
      String[] accept = new String[]{"audio/x-mpegurl", "audio/midi", "audio/sp-midi", "audio/x-midi", "audio/x-mid", "audio/mid"};
      if (InternalServices.isSoftwareCapable(7)) {
         Arrays.add(accept, "video/3gpp");
         if (RadioInfo.getNetworkType() == 4) {
            Arrays.add(accept, "video/3gpp2");
         }

         Arrays.add(accept, "video/mp4");
         Arrays.add(accept, "video/x-msvideo");
         Arrays.add(accept, "video/quicktime");
         if (MediaNatives.isVideoDecoderCodecSupported(4)) {
            Arrays.add(accept, "video/x-ms-asf");
            Arrays.add(accept, "video/x-ms-wm");
            Arrays.add(accept, "video/x-ms-wmv");
            Arrays.add(accept, "video/x-ms-wmx");
         }
      }

      if (InternalServices.isSoftwareCapable(2)) {
         if (Audio.isCodecSupported(3)) {
            Arrays.add(accept, "audio/mpeg");
            Arrays.add(accept, "audio/x-mpeg");
         }

         if (Audio.isCodecSupported(0)) {
            Arrays.add(accept, "audio/x-wav");
            Arrays.add(accept, "audio/wav");
         }

         if (Audio.isCodecSupported(7)) {
            Arrays.add(accept, "audio/amr");
         }

         if (Audio.isCodecSupported(10)) {
            Arrays.add(accept, "audio/3gpp");
            if (RadioInfo.getNetworkType() == 4) {
               Arrays.add(accept, "audio/3gpp2");
            }

            Arrays.add(accept, "audio/mp4");
            Arrays.add(accept, "audio/aac");
         }

         if (Audio.isCodecSupported(11)) {
            Arrays.add(accept, "audio/x-gsm");
         }

         if (Audio.isCodecSupported(9)) {
            Arrays.add(accept, "audio/basic");
         }

         if (Audio.isCodecSupported(12)) {
            Arrays.add(accept, "audio/x-ms-wma");
         }

         if (Audio.isCodecSupported(13)) {
            Arrays.add(accept, "audio/qcelp");
         }
      }

      ACCEPT = accept;
   }
}
