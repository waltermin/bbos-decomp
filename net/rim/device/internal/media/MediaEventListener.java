package net.rim.device.internal.media;

public interface MediaEventListener {
   void mediaError(int var1, int var2);

   void mediaStopped(int var1);

   void mediaPauseComplete(int var1);

   void mediaSeek(int var1, int var2);

   void mediaLoaded(int var1);

   void mediaStatusUpdate(int var1, int var2);

   void mediaParametersChangedComplete(int var1);

   void mediaAuthenticationRequired(int var1, Object var2, Object var3);
}
