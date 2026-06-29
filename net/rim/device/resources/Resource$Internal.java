package net.rim.device.resources;

import java.io.UnsupportedEncodingException;
import java.util.Vector;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.vm.Memory;
import net.rim.vm.Process;
import net.rim.vm.WeakReference;

public final class Resource$Internal {
   private static final int MAX_MODULES = Memory.getMaxNumCodeSections();
   private static String _moduleName;
   private static WeakReference _moduleNameResourceWF = new WeakReference(null);

   private Resource$Internal() {
   }

   private static final String getExtension(String name) {
      int offset = name.lastIndexOf(46);
      if (offset != -1) {
         String ext = name.substring(offset);
         return new StringBuffer(ext.length() + 1).append(ext).append('\n').toString();
      } else {
         return null;
      }
   }

   public static final synchronized Resource findResourceClass(String moduleName, int moduleHandle, String ext, boolean cacheResult) {
      Resource cachedResource = (Resource)_moduleNameResourceWF.get();
      if (moduleName.equals(_moduleName) && cachedResource != null) {
         return cachedResource;
      }

      int codeSectionIndex = Process.getModuleHandle(moduleName);
      if (codeSectionIndex != 0 && Process.isModuleEldestSibling(codeSectionIndex)) {
         if (ext != null) {
            try {
               int codeModuleManagerHandle = CodeModuleManager.getModuleHandle(moduleName);
               byte[] bytes = CodeModuleManager.getModuleResourceData(codeModuleManagerHandle);
               if (bytes != null) {
                  try {
                     String resourceExtensions = new String(bytes, "UTF-8");
                     if (resourceExtensions.indexOf(ext) == -1) {
                        return null;
                     }
                  } catch (UnsupportedEncodingException var10) {
                  }
               }
            } catch (IllegalArgumentException var11) {
            }
         }

         Resource resource = null;

         try {
            StringBuffer sb = new StringBuffer(30 + moduleName.length());
            sb.append("com.rim.resources.").append(moduleName).append("RIMResources");
            Class c = Process.classForName(sb.toString(), codeSectionIndex);
            resource = (Resource)c.newInstance();
            if (resource != null) {
               if (cacheResult) {
                  _moduleNameResourceWF.set(resource);
                  _moduleName = moduleName;
               }

               resource._name = moduleName;
            }
         } catch (Exception var9) {
         }

         return resource;
      } else {
         return null;
      }
   }

   public static final Resource getResourceClass(String moduleName) {
      return getResourceClass(moduleName, true);
   }

   public static final Resource getResourceClass(String moduleName, boolean cacheResult) {
      Resource resource = null;
      resource = findResourceClass(moduleName, Process.getModuleHandle(moduleName), null, cacheResult);
      if (resource == null) {
         int idx = moduleName.indexOf(45);
         if (idx != -1) {
            int len = moduleName.length();
            int digit = idx + 1;

            while (digit < len && Character.isDigit(moduleName.charAt(digit))) {
               digit++;
            }

            if (digit == len) {
               moduleName = moduleName.substring(0, idx);
               resource = findResourceClass(moduleName, Process.getModuleHandle(moduleName), null, cacheResult);
            }
         }
      }

      return resource;
   }

   private static final byte[] FindResourceInDependencyGraph(int firstRoot, int secondRoot, String name) {
      byte[] result = null;
      boolean[] inQueue = new boolean[MAX_MODULES];
      int[] queue = new int[MAX_MODULES];
      int head = 0;
      int tail = 0;
      String ext = getExtension(name);

      for (int i = 0; i < 2; i++) {
         int root = i == 0 ? firstRoot : secondRoot;
         if (!inQueue[root]) {
            queue[tail++] = root;
            inQueue[root] = true;

            while (head < tail) {
               int moduleHandle = queue[head++];
               if (head > 1) {
                  String moduleName = Process.getModuleName(moduleHandle, 0);
                  if (moduleName != null) {
                     Resource resource = findResourceClass(moduleName, moduleHandle, ext, true);
                     if (resource != null) {
                        result = resource.findResource(name);
                        if (result != null) {
                           return result;
                        }
                     }
                  }
               }

               int index = 1;

               int childHandle;
               while ((childHandle = Process.getModuleHandle(moduleHandle, index++)) != 0) {
                  if (!inQueue[childHandle]) {
                     queue[tail++] = childHandle;
                     inQueue[childHandle] = true;
                  }
               }
            }
         }
      }

      System.out.println("FRIDG: could not find " + name);
      return result;
   }

   public static final byte[] getResource(String name, int moduleHandle) {
      String moduleName = CodeModuleManager.getModuleName(moduleHandle, 0);
      if (moduleName != null) {
         String ext = getExtension(name);
         Resource resource = findResourceClass(moduleName, moduleHandle, ext, true);
         if (resource != null) {
            return resource.findResource(name);
         }
      }

      return null;
   }

   public static final ToIntHashtable getResourceCache(String endsWithText) {
      ToIntHashtable cache = new ToIntHashtable();
      String ext = getExtension(endsWithText);

      for (int moduleHandle = 0; moduleHandle < MAX_MODULES; moduleHandle++) {
         String moduleName = Process.getModuleName(moduleHandle, 0);
         if (moduleName != null) {
            Resource resource = findResourceClass(moduleName, moduleHandle, ext, true);
            if (resource != null) {
               Vector matches = resource.listResourcesEndingWith(endsWithText);
               if (matches.size() > 0) {
                  for (int j = 0; j < matches.size(); j++) {
                     cache.put(matches.elementAt(j), moduleHandle);
                  }
               }
            }
         }
      }

      return cache;
   }
}
