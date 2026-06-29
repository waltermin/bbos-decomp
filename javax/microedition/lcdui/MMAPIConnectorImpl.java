package javax.microedition.lcdui;

import javax.microedition.media.Player;
import net.rim.device.api.media.control.VideoPositionControl;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.lcdui.LcduiPlayerController;
import net.rim.device.internal.lcdui.MMAPIConnector;
import net.rim.device.internal.ui.component.MMAPIMediaField;

class MMAPIConnectorImpl implements MMAPIConnector {
   @Override
   public LcduiPlayerController setMediaCanvas(Canvas c, Player player) {
      LcduiPlayerController controller = (LcduiPlayerController)c.getPeer();
      controller.setPlayer(player);
      return controller;
   }

   @Override
   public LcduiPlayerController getMediaItem(Player player, String label, int width, int height) {
      MediaItem mediaItem = new MediaItem(label, width, height);
      LcduiPlayerController controller = mediaItem.getMediaField();
      controller.setPlayer(player);
      return controller;
   }

   @Override
   public LcduiPlayerController getMediaField(Player player, int width, int height) {
      LcduiPlayerController controller = new MMAPIMediaField(width, height);
      controller.setComponent(controller);
      controller.setPlayer(player);
      return controller;
   }

   @Override
   public void notifyPlayerPositionChange(Player player, XYRect rect) {
      if (player != null && player.getState() >= 200) {
         VideoPositionControl vpc = (VideoPositionControl)player.getControl("net.rim.device.api.media.control.VideoPositionControl");
         if (vpc != null) {
            vpc.setPosition(rect);
         }
      }
   }

   @Override
   public void notifyPlayerOffsetChange(Player player, XYRect rect) {
      if (player != null && player.getState() >= 200) {
         VideoPositionControl vpc = (VideoPositionControl)player.getControl("net.rim.device.api.media.control.VideoPositionControl");
         if (vpc != null) {
            vpc.offset(rect.x, rect.y);
         }
      }
   }
}
