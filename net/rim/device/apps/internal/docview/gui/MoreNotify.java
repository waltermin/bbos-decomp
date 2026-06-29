package net.rim.device.apps.internal.docview.gui;

interface MoreNotify {
   void notifyMoreRequestCompleted(int var1, int var2, ServerResponse var3, byte[] var4, int var5);

   void notifyMoreRequestFailed(LatestRequestInfo var1);
}
