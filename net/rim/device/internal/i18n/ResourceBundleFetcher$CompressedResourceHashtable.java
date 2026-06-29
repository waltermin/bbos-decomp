package net.rim.device.internal.i18n;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.RIMProcessLauncher;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;

class ResourceBundleFetcher$CompressedResourceHashtable extends Hashtable {
   private Object _lockObject = new Object();
   private int _cachedFSGeneration = 0;
   ApplicationRegistry _appRegistry = ApplicationRegistry.getApplicationRegistry();
   private static final long CRC_GUID = -2054058001874678213L;
   private static final long MODULES_GUID = -5612214964325329634L;

   private ResourceBundleFetcher$CompressedResourceHashtable() {
      synchronized (this._lockObject) {
         RIMProcessLauncher.launch(new ResourceBundleFetcher$CompressedResourceHashtable$InitializeRunnable(this, null));

         try {
            this._lockObject.wait();
         } catch (InterruptedException var4) {
         }
      }
   }

   private void initialize() {
      int[] moduleHandles = new int[0];
      int generation = CodeStore.getModuleHandles(moduleHandles);
      int i = 0;

      while (i < moduleHandles.length) {
         if (this.addModule(moduleHandles[i])) {
            i++;
         } else {
            Arrays.removeAt(moduleHandles, i);
         }
      }

      this.setGenerationSingleton(generation, moduleHandles);
   }

   @Override
   public Object get(Object key) {
      this.verify();
      return super.get(key);
   }

   private void setGenerationSingleton(int generation, int[] moduleHandles) {
      synchronized (this) {
         this._cachedFSGeneration = generation;
         ApplicationRegistry.getApplicationRegistry().replace(-2054058001874678213L, new Integer(generation));
         ApplicationRegistry.getApplicationRegistry().replace(-5612214964325329634L, moduleHandles);
      }
   }

   private void updateBundleCache() {
      int[] oldModuleHandles = (int[])this._appRegistry.getOrWaitFor(-5612214964325329634L);
      if (oldModuleHandles == null) {
         throw new IllegalStateException("getOrWaitFor returned null");
      }

      int[] moduleHandles = new int[0];
      int generation = CodeStore.getModuleHandles(moduleHandles);
      this.setGenerationSingleton(generation, moduleHandles);
      int oldModuleHandlesCount = oldModuleHandles.length;
      int moduleHandlesCount = moduleHandles.length;
      int indexOld = 0;
      int indexNew = 0;

      while (true) {
         int newModuleHandle = indexNew < moduleHandlesCount ? moduleHandles[indexNew] : Integer.MAX_VALUE;
         int oldModuleHandle = indexOld < oldModuleHandlesCount ? oldModuleHandles[indexOld] : Integer.MAX_VALUE;
         if (oldModuleHandle > newModuleHandle) {
            if (this.addModule(newModuleHandle)) {
               indexNew++;
            } else {
               Arrays.removeAt(moduleHandles, indexNew);
               moduleHandlesCount--;
            }
         } else if (oldModuleHandle < newModuleHandle) {
            indexOld++;
         } else {
            if (oldModuleHandle == Integer.MAX_VALUE) {
               return;
            }

            indexOld++;
            indexNew++;
         }
      }
   }

   private void verify() {
      if (this._cachedFSGeneration == 0) {
         synchronized (this._lockObject) {
            ;
         }
      }

      int generation = CodeStore.getModuleHandles(null);
      if (generation != this._cachedFSGeneration) {
         synchronized (this._lockObject) {
            RIMProcessLauncher.launch(new ResourceBundleFetcher$CompressedResourceHashtable$UpdateRunnable(this, null));

            try {
               this._lockObject.wait();
            } catch (InterruptedException var5) {
            }
         }
      }
   }

   private boolean addModule(int moduleHandle) {
      String moduleName = CodeModuleManager.getModuleName(moduleHandle, 0);
      String name = "";
      if (moduleName != null) {
         Resource resource = Resource$Internal.findResourceClass(moduleName, moduleHandle, ".crb\n", true);
         if (resource != null) {
            Vector matches = resource.listResourcesEndingWith(".crb");
            if (matches.size() > 0) {
               for (int j = 0; j < matches.size(); j++) {
                  name = (String)matches.elementAt(j);
                  this.put(name, new Integer(moduleHandle));
               }
            }

            return true;
         }
      }

      return false;
   }

   boolean isLoaded(String name) {
      Integer data = (Integer)this.get(name);
      return data != null;
   }

   ResourceBundleFetcher$CompressedResourceHashtable(ResourceBundleFetcher$1 x0) {
      this();
   }
}
