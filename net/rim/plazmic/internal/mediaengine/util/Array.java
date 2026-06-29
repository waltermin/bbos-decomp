package net.rim.plazmic.internal.mediaengine.util;

public interface Array {
   int compare(int var1, int var2);

   boolean equals(int var1, Object var2);

   void swap(int var1, int var2);

   void copy(int var1, int var2, int var3);

   void clear(int var1);

   void get(Object var1, int var2);

   void set(Object var1, int var2);

   void init(int var1);

   void growTo(int var1, int var2, int var3, boolean var4);

   int length();
}
