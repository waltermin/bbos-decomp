package javax.microedition.media;

public interface Player extends Controllable {
   int UNREALIZED;
   int REALIZED;
   int PREFETCHED;
   int STARTED;
   int CLOSED;
   long TIME_UNKNOWN;

   void realize();

   void prefetch();

   void start();

   void stop();

   void deallocate();

   void close();

   void setTimeBase(TimeBase var1);

   TimeBase getTimeBase();

   long setMediaTime(long var1);

   long getMediaTime();

   int getState();

   long getDuration();

   String getContentType();

   void setLoopCount(int var1);

   void addPlayerListener(PlayerListener var1);

   void removePlayerListener(PlayerListener var1);
}
