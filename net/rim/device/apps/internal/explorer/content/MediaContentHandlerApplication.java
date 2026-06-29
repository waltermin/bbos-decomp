package net.rim.device.apps.internal.explorer.content;

import java.util.Vector;
import javax.microedition.content.ContentHandlerServer;
import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;
import javax.microedition.content.RequestListener;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Arrays;

public final class MediaContentHandlerApplication extends UiApplication {
   public static final String CLASSNAME = "net.rim.device.apps.internal.explorer.content.MediaContentHandlerApplication";
   private static final long APP_ID = 1802896972155356383L;
   private static MediaContentHandlerApplication _app;
   private static RenderScreen _renderScreen;
   private static Invocation _invocation;
   private static ContentHandlerServer _handler;
   private static RequestListener _listener = new MediaContentHandlerApplication$MediaContentHandlerApplicationRequestListener();

   public MediaContentHandlerApplication() {
      this.setApp(this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void main(String[] args) {
      if (args != null && args.length > 0 && args[0].equals("init")) {
         RenderingSession session = RenderingSession.getNewInstance();
         if (session != null) {
            Vector typesVector = session.getSupportedMimeType();
            String[] types = new String[typesVector.size()];
            typesVector.copyInto(types);
            String[] suffixes = new String[0];
            String[] normalizedTypes = new String[0];

            for (int i = 0; i < types.length; i++) {
               String suffix = MIMETypeAssociations.getExtensionFromMIMEType(types[i]);
               if (suffix != null && suffix.length() != 0) {
                  Arrays.add(suffixes, suffix);
               }

               String normalizedType = MIMETypeAssociations.getNormalizedType(types[i]);
               if (normalizedType != null && normalizedType.length() != 0) {
                  Arrays.add(normalizedTypes, normalizedType);
               }
            }

            Registry registry = Registry.getRegistry("net.rim.device.apps.internal.explorer.content.MediaContentHandlerApplication");
            ContentHandlerServer server = null;
            boolean var14 = false /* VF: Semaphore variable */;

            try {
               var14 = true;
               server = Registry.getServer("net.rim.device.apps.internal.explorer.content.MediaContentHandlerApplication");
               var14 = false;
            } finally {
               if (var14) {
                  label105:
                  try {
                     server = registry.register(
                        "net.rim.device.apps.internal.explorer.content.MediaContentHandlerApplication",
                        normalizedTypes,
                        suffixes,
                        new String[]{"open"},
                        null,
                        "net.rim.bb.mediacontenthandler",
                        null
                     );
                     break label105;
                  } finally {
                     break label105;
                  }
               }
            }

            if (server != null) {
               server.setListener(_listener);
               return;
            }
         }
      } else {
         new MediaContentHandlerApplication().enterEventDispatcher();
      }
   }

   private final void setApp(MediaContentHandlerApplication app) {
      _app = app;
   }

   public final void finish(int status) {
      this.finish(status, true);
   }

   public final void finish(int status, boolean shutdown) {
      if (_handler != null && _invocation != null) {
         _handler.finish(_invocation, status);
         _invocation = null;
      }

      if (shutdown) {
         System.exit(0);
      }
   }

   static final ContentHandlerServer access$002(ContentHandlerServer x0) {
      _handler = x0;
      return x0;
   }

   static final Invocation access$102(Invocation x0) {
      _invocation = x0;
      return x0;
   }

   static final RenderScreen access$302(RenderScreen x0) {
      _renderScreen = x0;
      return x0;
   }
}
