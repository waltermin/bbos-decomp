package net.rim.device.internal.system;

import java.util.Vector;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.vm.Array;

class CodeStore$DependencyList {
   private int _generation;
   private IntHashtable _dependencyLists;
   private IntHashtable _moduleHandleToDependencyHandles;
   private int[] _queue;
   private static final int IS_DEPENDENCY = 1;

   CodeStore$DependencyList() {
      this.reset();
   }

   private void reset() {
      long start = System.currentTimeMillis();

      int[] moduleHandles;
      int generation1;
      int generation2;
      do {
         generation1 = CodeStore.getModuleHandles(null);
         moduleHandles = CodeModuleManager.getModuleHandles(true);
         generation2 = CodeStore.getModuleHandles(null);
      } while (generation1 != generation2);

      this._generation = generation2;
      int numModuleHandles = moduleHandles.length;
      ToIntHashtable moduleNameToHandleHastable = new ToIntHashtable(numModuleHandles * 4 + 1);
      this._moduleHandleToDependencyHandles = new IntHashtable(numModuleHandles * 4 + 1);

      for (int i = 0; i < numModuleHandles; i++) {
         try {
            int moduleHandle = moduleHandles[i];
            String moduleName = CodeModuleManager.getModuleName(moduleHandle);
            moduleNameToHandleHastable.put(moduleName, moduleHandle);
            int index = 0;

            while (true) {
               String aliasName = CodeModuleManager.getModuleAliasName(moduleHandle, index);
               if (aliasName == null) {
                  break;
               }

               moduleNameToHandleHastable.put(aliasName, moduleHandle);
               index++;
            }
         } catch (IllegalArgumentException e) {
            this._generation = 0;
            System.out.println("CodeStore.DependencyList.reset() 1 - IAE when fetching module alias name (invalid handle)");
         }
      }

      Vector dependencyNames = new Vector(numModuleHandles);

      for (int i = 0; i < numModuleHandles; i++) {
         int moduleHandle = moduleHandles[i];
         dependencyNames.removeAllElements();

         try {
            int index = 1;

            while (true) {
               String dependencyModuleName = CodeModuleManager.getModuleName(moduleHandle, index);
               if (dependencyModuleName == null) {
                  break;
               }

               dependencyNames.addElement(dependencyModuleName);
               index++;
            }
         } catch (IllegalArgumentException e) {
            this._generation = 0;
            System.out.println("CodeStore.DependencyList.reset() 2 - IAE when fetching module name of dependency (invalid handle)");
         }

         int numNames = dependencyNames.size();
         int[] dependencyHandles = new int[numNames];

         for (int j = numNames - 1; j >= 0; j--) {
            int dependencyHandle = moduleNameToHandleHastable.get(dependencyNames.elementAt(j));
            if (dependencyHandle != -1) {
               dependencyHandles[j] = dependencyHandle;
            } else {
               Array.resize(dependencyHandles, dependencyHandles.length - 1);
               System.out.println("CodeStore.DependencyList.reset() - Cannot find handle from name " + dependencyNames.elementAt(j));
            }
         }

         this._moduleHandleToDependencyHandles.put(moduleHandle, dependencyHandles);
      }

      this._dependencyLists = new IntHashtable(numModuleHandles * 2 + 1);
      this._queue = new int[numModuleHandles];
      System.out.println("CodeStore.DependencyList.reset() took " + (System.currentTimeMillis() - start));
   }

   private IntIntHashtable buildModuleDependencyList(int moduleHandle) {
      long start = System.currentTimeMillis();
      IntIntHashtable moduleDependencyList = new IntIntHashtable();
      moduleDependencyList.put(moduleHandle, 1);
      this._queue[0] = moduleHandle;
      int read = 0;
      int write = 1;

      while (read < write) {
         int handle = this._queue[read++];
         int[] dependencies = (int[])this._moduleHandleToDependencyHandles.get(handle);
         int numDependencies = dependencies == null ? 0 : dependencies.length;
         if (numDependencies > 0) {
            for (int j = numDependencies - 1; j >= 0; j--) {
               int h = dependencies[j];
               if (moduleDependencyList.put(h, 1) != 1) {
                  this._queue[write++] = h;
               }
            }
         } else {
            System.out.println("CodeStore.DependencyList.buildModuleDependencyList() - found no dependencies for " + handle);
         }
      }

      System.out.println("CodeStore.DependencyList.buildModuleDependencyList() took " + (System.currentTimeMillis() - start) + " - [" + write + "]");
      return moduleDependencyList;
   }

   synchronized boolean isDependency(int processModuleHandle, int callingModuleHandle) {
      if (this._generation != CodeStore.getModuleHandles(null)) {
         this.reset();
      }

      IntIntHashtable moduleDependencyList = (IntIntHashtable)this._dependencyLists.get(processModuleHandle);
      if (moduleDependencyList == null) {
         moduleDependencyList = this.buildModuleDependencyList(processModuleHandle);
         this._dependencyLists.put(processModuleHandle, moduleDependencyList);
      }

      return moduleDependencyList.get(callingModuleHandle) == 1;
   }
}
