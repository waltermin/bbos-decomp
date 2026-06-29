package net.rim.device.internal.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.media.Player;
import net.rim.device.api.ui.XYRect;

public interface MMAPIConnector {
   LcduiPlayerController setMediaCanvas(Canvas var1, Player var2);

   LcduiPlayerController getMediaItem(Player var1, String var2, int var3, int var4);

   LcduiPlayerController getMediaField(Player var1, int var2, int var3);

   void notifyPlayerPositionChange(Player var1, XYRect var2);

   void notifyPlayerOffsetChange(Player var1, XYRect var2);
}
