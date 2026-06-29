package net.rim.device.internal.media.metadata;

import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EncodedImage;

public class MetadataHandlerFactoryImpl extends MetadataHandlerFactory {
   MetadataHandlerFactoryImpl() {
   }

   @Override
   protected MetaDataControl extractMetadata(EncodedImage image) {
      if (image instanceof Object) {
         return new JPEGMetadataExtractor(image);
      } else if (image instanceof Object) {
         return new PNGMetadataExtractor(image);
      } else {
         return (MetaDataControl)(image instanceof Object ? new GIFMetadataExtractor(image) : new Object());
      }
   }

   public static void init() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      registry.put(-3049172664916265609L, new MetadataHandlerFactoryImpl());
   }
}
