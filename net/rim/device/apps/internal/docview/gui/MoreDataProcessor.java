package net.rim.device.apps.internal.docview.gui;

interface MoreDataProcessor {
   byte CHUNK_ADDED;
   byte CHUNK_FAILED;
   byte CHUNK_TRYAGAIN;

   void processEmbeddedInitialChunk(Object var1, int var2, String var3, int var4);

   byte moreDataProcessed(byte[] var1, int var2, int var3, boolean var4);

   void saveLatestRequest(int var1, String var2);

   MoreDataProcessor getEmbeddedDisplayScreen(String var1);

   void processMoreResponse(ServerResponse var1, byte[] var2, int var3);
}
