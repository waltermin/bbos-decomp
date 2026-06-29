package javax.xml.rpc;

public interface Stub {
   String USERNAME_PROPERTY;
   String PASSWORD_PROPERTY;
   String ENDPOINT_ADDRESS_PROPERTY;
   String SESSION_MAINTAIN_PROPERTY;

   Object _getProperty(String var1);

   void _setProperty(String var1, Object var2);
}
