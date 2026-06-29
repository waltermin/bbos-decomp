package net.rim.device.internal.media.metadata;

import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.system.PNGEncodedImage;

public class MetadataHandlerFactoryImpl extends MetadataHandlerFactory {
   MetadataHandlerFactoryImpl() {
   }

   @Override
   protected MetaDataControl extractMetadata(EncodedImage image) {
      if (image instanceof JPEGEncodedImage) {
         return new JPEGMetadataExtractor(image);
      } else if (image instanceof PNGEncodedImage) {
         return new PNGMetadataExtractor(image);
      } else {
         return image instanceof GIFEncodedImage ? new GIFMetadataExtractor(image) : new MetaDataControlImpl();
      }
   }

   public static void init() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      registry.put(-3049172664916265609L, new MetadataHandlerFactoryImpl());
   }
}
