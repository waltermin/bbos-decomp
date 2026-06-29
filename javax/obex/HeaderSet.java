package javax.obex;

public interface HeaderSet {
   int COUNT;
   int NAME;
   int TYPE;
   int LENGTH;
   int TIME_ISO_8601;
   int TIME_4_BYTE;
   int DESCRIPTION;
   int TARGET;
   int HTTP;
   int WHO;
   int OBJECT_CLASS;
   int APPLICATION_PARAMETER;

   void setHeader(int var1, Object var2);

   Object getHeader(int var1);

   int[] getHeaderList();

   void createAuthenticationChallenge(String var1, boolean var2, boolean var3);

   int getResponseCode();
}
