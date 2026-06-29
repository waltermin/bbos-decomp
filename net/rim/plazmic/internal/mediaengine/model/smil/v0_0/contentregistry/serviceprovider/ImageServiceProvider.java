package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.system.EncodedImage;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player.AnimatedBitmapPlayer;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.ScalableBitmapField;

public class ImageServiceProvider extends UIComponentServiceProvider {
   private static final int NUM_SERVICES;
   public static final int POLLING_INTERVAL;

   public ImageServiceProvider() {
      super(3);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void createServices(InputStream is, String type, HttpConnection conn) {
      byte[] data = this.bufferData(is).toByteArray();
      EncodedImage image = null;

      try {
         image = EncodedImage.createEncodedImage(data, 0, data.length);
      } catch (Throwable var8) {
         throw new Object(iae.getMessage());
      }

      this.setDrmStatus(conn);
      ScalableBitmapField field = new ScalableBitmapField(null, super._fieldStyle);
      field.setServiceProvider(this);
      field.setProtected(this.isForwardLocked());
      field.setImage(image);
      if (image.getFrameCount() > 1) {
         field.setMaximumLoopIterations(Integer.MAX_VALUE);
         field.setUnderlyingBackgroundColor(16777215);
         this.setService("Player", new AnimatedBitmapPlayer(field, 200));
      }

      this.setService("UI_Component", field);
      this.setService("Model", image);
   }
}
