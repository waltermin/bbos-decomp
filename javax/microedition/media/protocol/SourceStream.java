package javax.microedition.media.protocol;

import javax.microedition.media.Controllable;

public interface SourceStream extends Controllable {
   int NOT_SEEKABLE;
   int SEEKABLE_TO_START;
   int RANDOM_ACCESSIBLE;

   ContentDescriptor getContentDescriptor();

   long getContentLength();

   int read(byte[] var1, int var2, int var3);

   int getTransferSize();

   long seek(long var1);

   long tell();

   int getSeekType();
}
