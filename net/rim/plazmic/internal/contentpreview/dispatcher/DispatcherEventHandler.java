package net.rim.plazmic.internal.contentpreview.dispatcher;

public interface DispatcherEventHandler {
   String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/DispatcherEventHandler.java#1 $";

   void openSession(String var1, boolean var2);

   void enumerateDevices();

   void getValidDevice(String var1);

   void waitForSessionReady(String var1, int var2);

   void pushFile(String var1, String var2);

   void raiseWindow(String var1);

   void closeSession(String var1);

   void shutdownDispatcherService();

   void getServerVersion();

   void getRecentSession();

   void getSpecificSession(int var1);

   void getControlPanelPort(String var1);

   void getPlaybackCommandPort(String var1);

   void sessionReady(int var1);

   void logMessage(String var1, int var2, String var3, String[] var4);

   void getSessionProgress(String var1);

   void dequeueThemeRegistrationRequest(int var1);

   void dequeueThemeActivationRequest(int var1);

   void voidMessage();

   void sessionOk(String var1);

   void sessionPort(int var1);

   void sessionProgress(int var1);

   void deviceType(String var1);

   void dispatcherServiceFailure(String var1);

   void noSuchSession(String var1);

   void invalidDevice(String var1);

   void timeoutExpiry(String var1);

   void filePushFailure(String var1);

   void themeRequest(String var1);

   void serverProperties(int var1);
}
