package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.ServiceProvider;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player.AudioPlayerWrapper;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player.Player;

public class AudioServiceProvider extends ServiceProvider {
   private static final int NUM_SERVICES = 2;

   public AudioServiceProvider() {
      super(2);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void createServices(InputStream is, String type, HttpConnection conn) throws IOException {
      ByteArrayInputStream bis = new ByteArrayInputStream(this.bufferData(is).toByteArray());

      try {
         Player player = new AudioPlayerWrapper(bis, type, conn.getLength());
         this.setService("Player", player);
      } catch (Throwable var7) {
         throw new IOException(me.getMessage());
      }
   }
}
