package net.rim.device.internal.io.file;

import java.util.Hashtable;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EncodedImage;

public class MetaDataProvider {
   private static final long THUMBNAIL_PROVIDERS = 5925472548652966264L;
   private static Hashtable _providers;
   private static ImageThumbnailProvider _imageProvider;
   private static MediaMetaDataProvider _mediaProvider;

   public Object[] getMetaData(FileConnection _1, EncodedImage _2, byte[] _3, int _4, int _5, int _6, Object[] _7) {
      throw null;
   }

   public static final MetaDataProvider getProvider(String contentType) {
      if (contentType == null) {
         return null;
      }

      MetaDataProvider provider = (MetaDataProvider)_providers.get(contentType);
      if (provider == null) {
         switch (MIMETypeAssociations.getMediaTypeFromMIMEType(contentType)) {
            case 0:
               break;
            case 1:
            default:
               return _imageProvider;
            case 2:
               return _mediaProvider;
         }
      }

      return provider;
   }

   public static final void registerProvider(String contentType, MetaDataProvider provider) {
      _providers.put(contentType, provider);
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      Object obj = registry.getOrWaitFor(5925472548652966264L);
      if (!(obj instanceof Object)) {
         _providers = (Hashtable)(new Object());
         _imageProvider = new ImageThumbnailProvider();
         _mediaProvider = new MediaMetaDataProvider();
         _providers.put("image/*", _imageProvider);
         _providers.put("audio/*", _mediaProvider);
         registry.put(5925472548652966264L, _providers);
      } else {
         _providers = (Hashtable)obj;
         _imageProvider = (ImageThumbnailProvider)_providers.get("image/*");
         _mediaProvider = (MediaMetaDataProvider)_providers.get("audio/*");
      }
   }
}
