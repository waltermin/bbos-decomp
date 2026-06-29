package javax.obex;

import javax.microedition.io.ContentConnection;

public interface Operation extends ContentConnection {
   void abort();

   HeaderSet getReceivedHeaders();

   void sendHeaders(HeaderSet var1);

   int getResponseCode();
}
