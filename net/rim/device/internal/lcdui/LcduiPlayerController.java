package net.rim.device.internal.lcdui;

import javax.microedition.media.Player;

public interface LcduiPlayerController {
   void setPlayer(Player var1);

   void resizeComponent(int var1, int var2);

   Object getComponent();

   void setComponent(Object var1);

   void refresh();
}
