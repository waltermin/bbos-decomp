package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.contentregistry.serviceprovider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.protocol.DataSource;
import net.rim.device.api.ui.Field;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player.VideoPlayerWrapper;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.VideoField;

public class VideoServiceProvider extends UIComponentServiceProvider {
   private Player _player;
   private VideoControl _videoControl;
   private static final int NUM_SERVICES;

   public VideoServiceProvider() {
      super(3);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void createServices(InputStream is, String type, HttpConnection conn) {
      ByteArrayInputStream bis = (ByteArrayInputStream)(new Object(this.bufferData(is).toByteArray()));

      try {
         this._player = Manager.createPlayer((DataSource)(new Object(bis, type, conn.getLength())));
         net.rim.plazmic.internal.mediaengine.model.smil.v0_0.player.Player player = new VideoPlayerWrapper(this._player);
         this.setService("Player", player);
         this._videoControl = (VideoControl)this._player.getControl("VideoControl");
         Field field = new VideoField();
         this._videoControl.initDisplayMode(1, field);
         this.setService("UI_Component", field);
      } catch (Throwable var8) {
         throw new Object(me.getMessage());
      }
   }
}
