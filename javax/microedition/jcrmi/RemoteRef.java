package javax.microedition.jcrmi;

public interface RemoteRef {
   Object invoke(String var1, Object[] var2);

   boolean remoteEquals(RemoteRef var1);

   int remoteHashCode();
}
