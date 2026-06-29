package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry;

import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.io.http.HttpHeaders;

public class ContentRegistry {
   private static final String TEXT_SERVICE_PROVIDER = "net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.PlainTextServiceProvider";
   private static final String IMAGE_SERVICE_PROVIDER = "net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.ImageServiceProvider";
   private static final String AUDIO_SERVICE_PROVIDER = "net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.AudioServiceProvider";
   private static final String VIDEO_SERVICE_PROVIDER = "net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.VideoServiceProvider";
   private static final String TEXT_TYPE_PREFIX = "text/";
   private static final String IMAGE_TYPE_PREFIX = "image/";
   private static final String AUDIO_TYPE_PREFIX = "audio/";
   private static final String VIDEO_TYPE_PREFIX = "video/";
   public static BrowserContent _browserContent;

   public static ServiceProvider getServiceProvider(String uri, String type) {
      ServiceProvider provider = null;

      try {
         HttpConnection conn = null;
         uri = _browserContent.resolveUrl(uri);
         if (uri.startsWith("cod://")) {
            conn = (HttpConnection)Connector.open(uri);
         } else {
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setProperty("x-rim-transcode-content", "none");
            RequestedResource resource = new RequestedResource(uri, requestHeaders, 0);
            conn = _browserContent.getRenderingApplication().getResource(resource, null);
         }

         if (conn != null) {
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
               if (conn.getType() != null) {
                  type = conn.getType();
               }

               InputStream is = conn.openInputStream();
               if (is != null) {
                  provider = getServiceProvider(is, type, conn);
                  is.close();
                  if (provider != null) {
                     provider.setService("Uri", uri);
                     return provider;
                  }
               }
            }
         }
      } finally {
         return provider;
      }

      return provider;
   }

   private static ServiceProvider getServiceProvider(InputStream param0, String param1, HttpConnection param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 06
      // 04: aconst_null
      // 05: areturn
      // 06: aload 1
      // 07: invokestatic net/rim/device/api/io/MIMETypeAssociations.getNormalizedType (Ljava/lang/String;)Ljava/lang/String;
      // 0a: astore 1
      // 0b: aconst_null
      // 0c: astore 3
      // 0d: aload 1
      // 0e: ldc_w "video/"
      // 11: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 14: ifeq 1e
      // 17: ldc_w "net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.VideoServiceProvider"
      // 1a: astore 3
      // 1b: goto 4e
      // 1e: aload 1
      // 1f: ldc_w "audio/"
      // 22: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 25: ifeq 2f
      // 28: ldc_w "net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.AudioServiceProvider"
      // 2b: astore 3
      // 2c: goto 4e
      // 2f: aload 1
      // 30: ldc_w "image/"
      // 33: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 36: ifeq 40
      // 39: ldc_w "net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.ImageServiceProvider"
      // 3c: astore 3
      // 3d: goto 4e
      // 40: aload 1
      // 41: ldc_w "text/"
      // 44: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 47: ifeq 4e
      // 4a: ldc_w "net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider.PlainTextServiceProvider"
      // 4d: astore 3
      // 4e: aconst_null
      // 4f: astore 4
      // 51: aload 3
      // 52: ifnull 85
      // 55: aload 3
      // 56: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 59: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 5c: checkcast net/rim/plazmic/internal/mediaengine/model/smil/v0_0/contentregistry/ServiceProvider
      // 5f: astore 4
      // 61: goto 70
      // 64: astore 5
      // 66: goto 70
      // 69: astore 5
      // 6b: goto 70
      // 6e: astore 5
      // 70: aload 4
      // 72: ifnull 85
      // 75: aload 4
      // 77: aload 0
      // 78: aload 1
      // 79: aload 2
      // 7a: invokevirtual net/rim/plazmic/internal/mediaengine/model/smil/v0_0/contentregistry/ServiceProvider.createServices (Ljava/io/InputStream;Ljava/lang/String;Ljavax/microedition/io/HttpConnection;)V
      // 7d: goto 85
      // 80: astore 5
      // 82: aconst_null
      // 83: astore 4
      // 85: aload 4
      // 87: areturn
      // try (40 -> 45): 46 null
      // try (40 -> 45): 48 null
      // try (40 -> 45): 50 null
      // try (53 -> 58): 59 null
   }
}
