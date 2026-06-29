package net.rim.vm;

public final class Persistence {
   private Persistence() {
   }

   public static final native Object getRoot();

   public static final native void setRoot(Object var0);

   public static final native Object getSynchObject();

   public static final native void commit(Object var0, boolean var1);
}
