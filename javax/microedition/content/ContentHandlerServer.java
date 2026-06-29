package javax.microedition.content;

public interface ContentHandlerServer extends ContentHandler {
   Invocation getRequest(boolean var1);

   void cancelGetRequest();

   boolean finish(Invocation var1, int var2);

   void setListener(RequestListener var1);

   String getAccessAllowed(int var1);

   int accessAllowedCount();

   boolean isAccessAllowed(String var1);
}
