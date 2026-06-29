package net.rim.plazmic.internal.contentpreview.dispatcher;

public interface Dispatcher {
   String rcsid;

   String getSession(int var1);

   int getControlPanelPort(String var1);

   int getPlaybackCommandPort(String var1);

   void log(String var1, int var2, String var3, String[] var4);
}
