package net.rim.wica.runtime.event;

public interface EventService {
   void dispatchEvent(int var1, Object var2);

   void dispatchEvent(Object var1, int var2);

   void dispatchEvent(Object var1, int var2, int var3);

   void dispatchEvent(Object var1, int var2, Object var3);

   void dispatchEvent(Object var1, int var2, int var3, Object var4);

   void addListener(int var1, EventListener var2);

   void addListener(int[] var1, EventListener var2);

   void removeListener(int var1, EventListener var2);

   void removeListener(int[] var1, EventListener var2);

   void removeListener(EventListener var1);
}
