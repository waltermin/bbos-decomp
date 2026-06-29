package net.rim.plazmic.mediaengine;

public interface MediaListener {
   int UNUSED = -1;
   int MEDIA_COMPLETE = 3;
   int MEDIA_REQUESTED = 7;
   int MEDIA_IO = 9;
   int MEDIA_REALIZED = 12;
   int MEDIA_LOADING_FAILED = 13;

   void mediaEvent(Object var1, int var2, int var3, Object var4);
}
