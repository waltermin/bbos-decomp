package javax.microedition.jcrmi;

public class RemoteStub {
   protected RemoteRef ref;

   public void setRef(RemoteRef remoteref) {
      this.ref = remoteref;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof RemoteStub)) {
         return false;
      }

      RemoteStub st = (RemoteStub)obj;
      return st.ref != null && this.ref != null ? this.ref.equals(st.ref) : false;
   }

   @Override
   public int hashCode() {
      return this.ref.remoteHashCode();
   }
}
