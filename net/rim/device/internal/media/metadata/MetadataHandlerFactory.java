package net.rim.device.internal.media.metadata;

import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EncodedImage;

public class MetadataHandlerFactory {
   protected static final long GUID = -3049172664916265609L;
   private static MetadataHandlerFactory _instance;

   MetadataHandlerFactory() {
   }

   public static MetaDataControl extract(EncodedImage image) {
      if (_instance == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         _instance = (MetadataHandlerFactory)registry.waitFor(-3049172664916265609L);
      }

      return _instance.extractMetadata(image);
   }

   protected MetaDataControl extractMetadata(EncodedImage _1) {
      throw null;
   }
}
