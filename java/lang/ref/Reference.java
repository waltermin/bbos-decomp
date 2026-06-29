package java.lang.ref;

public class Reference {
   private Object _referent;
   private Object _next;

   public Object get() {
      return this._referent;
   }

   public void clear() {
      this._referent = null;
   }

   Reference(Object referent) {
      this._referent = referent;
      this.register();
   }

   private native void register();
}
