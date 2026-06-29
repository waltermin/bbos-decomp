package net.rim.plazmic.mediaengine;

public interface MediaListener {
   int UNUSED;
   int MEDIA_COMPLETE;
   int MEDIA_REQUESTED;
   int MEDIA_IO;
   int MEDIA_REALIZED;
   int MEDIA_LOADING_FAILED;

   void mediaEvent(Object var1, int var2, int var3, Object var4);
}
