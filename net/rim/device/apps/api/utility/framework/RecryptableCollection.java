package net.rim.device.apps.api.utility.framework;

public interface RecryptableCollection {
   int getSize(Object var1);

   Object getElementAt(int var1, Object var2);

   void replaceElementAt(Object var1, Object var2, int var3, Object var4);

   void updateListeners(Object var1, Object var2, Object var3);

   void commit(Object var1);

   void reCryptStarted(Object var1);

   void reCryptEnded(Object var1);
}
