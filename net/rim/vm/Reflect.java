package net.rim.vm;

public final class Reflect implements VMConstants {
   private Reflect() {
   }

   public static final native int getClassClass(Class var0);

   public static final native int getObjectClass(Object var0);

   public static final native int getComponentClass(Object var0);

   public static final native String getClassName(int var0);

   public static final native int[] getFields(int var0, boolean var1);

   public static final native int getFieldType(int var0, int var1);

   public static final native int classify(int var0);

   public static final native int getWordField(int var0, int var1, Object var2);

   public static final native void setWordField(int var0, int var1, Object var2, int var3);

   public static final native long getDWordField(int var0, int var1, Object var2);

   public static final native void setDWordField(int var0, int var1, Object var2, long var3);

   public static final native int getFieldAttributes(int var0, int var1);

   public static final native int getMethodAttributes(int var0, int var1);

   public static final native int getClassAttributes(int var0);

   public static final native int[] getMethods(int var0, boolean var1);

   public static final native int[] getMethods(String var0, int var1, boolean var2);

   public static final native int[] getConstructors(int var0, boolean var1);

   public static final native String getFieldName(int var0, int var1);

   public static final native String getMethodName(int var0, int var1);

   public static final native int getReturnType(int var0, int var1);

   public static final native int[] getParameterTypes(int var0, int var1);

   public static final native int invokeWord(int var0, int var1, Object var2, int[] var3);

   public static final native long invokeDWord(int var0, int var1, Object var2, int[] var3);

   public static final native Object newInstance(int var0, int var1, int[] var2);

   public static final native Object newArray(int var0, int var1);

   public static final native boolean isStaticField(int var0, int var1);

   public static final native boolean isStaticMethod(int var0, int var1);

   public static final native boolean isPublicField(int var0, int var1);

   public static final native boolean isPublicMethod(int var0, int var1);

   public static final native boolean isAssignableFrom(int var0, int var1);
}
