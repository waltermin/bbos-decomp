package javax.microedition.media.protocol;

import javax.microedition.media.Controllable;

public interface SourceStream extends Controllable {
   int NOT_SEEKABLE = 0;
   int SEEKABLE_TO_START = 1;
   int RANDOM_ACCESSIBLE = 2;

   ContentDescriptor getContentDescriptor();

   long getContentLength();

   int read(byte[] var1, int var2, int var3);

   int getTransferSize();

   long seek(long var1);

   long tell();

   int getSeekType();
}
