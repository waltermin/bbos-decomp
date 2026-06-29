package java.lang;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Vector;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.vm.TraceBack;

public final class Class {
   private int _VMClassRef;
   private int _VMThread;
   private int _arrayType;
   private int _arrayNumDims;
   private int _arrayNext;

   private Class() {
   }

   @Override
   public final String toString() {
      StringBuffer prefix;
      if (this.isInterface()) {
         prefix = new StringBuffer("interface");
      } else {
         prefix = new StringBuffer("class");
      }

      return prefix.append(' ').append(this.getName()).toString();
   }

   public static final Class forName(String className) {
      if (className.charAt(0) != '[') {
         return forName0(className);
      }

      try {
         int dims = 0;

         while (className.charAt(dims) == '[') {
            dims++;
         }

         Class clazz = null;
         char base = className.charAt(dims);
         if (base == 'L') {
            int end = className.length() - 1;
            if (className.charAt(end) != ';' || dims + 1 >= end) {
               throw new ClassNotFoundException(className);
            }

            clazz = forName0(className.substring(dims + 1, end));
            if (clazz == null) {
               throw new ClassNotFoundException(className);
            }
         }

         return forNameDims(clazz, base, dims);
      } catch (ClassNotFoundException e) {
         throw e;
      } catch (Throwable var6) {
         throw new ClassNotFoundException(className);
      }
   }

   private static final native Class forName0(String var0);

   private static final native Class forNameDims(Class var0, char var1, int var2);

   private final native Object newInstance0();

   public final Object newInstance() {
      try {
         return this.newInstance0();
      } catch (IllegalAccessException iae) {
         throw new IllegalAccessException("newInstance (" + this.getName() + ')');
      } catch (InstantiationException ie) {
         throw new InstantiationException("newInstance (" + this.getName() + ')');
      } catch (OutOfMemoryError oome) {
         throw new OutOfMemoryError("newInstance (" + this.getName() + ')');
      }
   }

   public final native boolean isInstance(Object var1);

   public final native boolean isAssignableFrom(Class var1);

   public final native boolean isInterface();

   public final boolean isArray() {
      return this.getArrayType() != 'V';
   }

   private final native char getArrayType();

   private final native int getArrayDims();

   public final String getName() {
      char type = this.getArrayType();
      if (type == 'V') {
         String pkg = this.getPackageName();
         return pkg.length() == 0 ? this.getClassName() : pkg + '.' + this.getClassName();
      }

      StringBuffer str = new StringBuffer();
      int dims = this.getArrayDims();

      while (--dims >= 0) {
         str.append('[');
      }

      str.append(type);
      if (type == 'L') {
         String pkg = this.getPackageName();
         if (pkg.length() != 0) {
            str.append(pkg).append('.');
         }

         str.append(this.getClassName()).append(';');
      }

      return str.toString();
   }

   private final native String getClassName();

   private final native String getPackageName();

   public final InputStream getResourceAsStream(String name) {
      if (name.length() > 0 && name.charAt(0) == '/') {
         name = name.substring(1);
      } else {
         String pkg = this.getPackageName();
         if (pkg.length() > 0) {
            name = pkg.replace('.', '/') + '/' + name;
         }
      }

      name = this.fixResourceName(name);
      if (name == null) {
         return null;
      }

      Resource resource = Resource$Internal.getResourceClass(TraceBack.getCallingModuleName(2));
      if (resource != null) {
         byte[] data = resource.getResource(name);
         if (data != null) {
            return new ByteArrayInputStream(data);
         }
      }

      return null;
   }

   private final String fixResourceName(String name) {
      Vector dirVector = new Vector();
      int startIdx = 0;
      int endIdx = 0;

      while ((endIdx = name.indexOf(47, startIdx)) != -1) {
         if (endIdx == startIdx) {
            startIdx++;
         } else {
            String curDir = name.substring(startIdx, endIdx);
            startIdx = endIdx + 1;
            if (!curDir.equals(".")) {
               if (curDir.equals("..")) {
                  int size = dirVector.size();
                  if (size <= 0) {
                     return null;
                  }

                  dirVector.removeElementAt(size - 1);
               } else {
                  dirVector.addElement(curDir);
               }
            }
         }
      }

      int nameLength = name.length();
      StringBuffer dirName = new StringBuffer(nameLength);
      int numElements = dirVector.size();

      for (int i = 0; i < numElements; i++) {
         dirName.append((String)dirVector.elementAt(i));
         dirName.append("/");
      }

      if (startIdx < nameLength) {
         String filename = name.substring(startIdx);
         if (filename.endsWith(".class") && !".class".equals(filename)) {
            return null;
         }

         dirName.append(name.substring(startIdx));
      }

      return dirName.toString();
   }
}
