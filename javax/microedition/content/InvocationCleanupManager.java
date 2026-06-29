package javax.microedition.content;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.internal.content.ContentHandlerRegistrationHelper;
import net.rim.device.internal.system.CodeStore;

class InvocationCleanupManager implements GlobalEventListener {
   private IntHashtable _activeInvocations = new IntHashtable();
   private IntHashtable _activeRequestHandlers = new IntHashtable();
   private IntHashtable _activeResponseHandlers = new IntHashtable();
   private IntHashtable _contentHandlerModules = new IntHashtable();
   private Hashtable _moduleNameToHandleMap = new Hashtable();
   private Hashtable _classnameToRegistrationTypeMap = new Hashtable();
   private int _crc;
   private int[] _currentHandles = new int[0];
   private static final long INVOCATION_CLEANUP_MANAGER_ID;
   private static InvocationCleanupManager _instance;

   void checkModules() {
      int[] newHandles = new int[0];
      int crc = CodeStore.getModuleHandles(newHandles);
      if (crc != this._crc) {
         IntVector modulesToInstall = new IntVector();
         Vector handlersToDelete = new Vector();
         int len = Math.max(this._currentHandles.length, newHandles.length);
         int currentIndex = 0;
         int newIndex = 0;

         while (currentIndex < len && newIndex < len) {
            if (currentIndex == this._currentHandles.length) {
               currentIndex = len;
               break;
            }

            if (newIndex == newHandles.length) {
               newIndex = len;
               break;
            }

            if (this._currentHandles[currentIndex] >= newHandles[newIndex]) {
               if (this._currentHandles[currentIndex] > newHandles[newIndex]) {
                  modulesToInstall.addElement(newHandles[newIndex]);
                  newIndex++;
               } else if (this._currentHandles[currentIndex] == newHandles[newIndex]) {
                  currentIndex++;
                  newIndex++;
               }
            } else {
               String[] classnames = (String[])this._contentHandlerModules.get(this._currentHandles[currentIndex]);
               if (classnames != null && classnames.length > 0) {
                  for (int i = 0; i < classnames.length; i++) {
                     handlersToDelete.addElement(classnames[i]);
                  }
               }

               currentIndex++;
            }
         }

         if (currentIndex != newIndex) {
            if (currentIndex == len) {
               for (int i = newIndex; i < newHandles.length; i++) {
                  modulesToInstall.addElement(newHandles[i]);
               }
            } else {
               for (int i = currentIndex; i < this._currentHandles.length; i++) {
                  String[] classnames = (String[])this._contentHandlerModules.get(this._currentHandles[i]);
                  if (classnames != null && classnames.length > 0) {
                     for (int j = 0; j < classnames.length; j++) {
                        handlersToDelete.addElement(classnames[j]);
                     }
                  }
               }
            }
         }

         int size = handlersToDelete.size();

         for (int i = 0; i < size; i++) {
            String cname = (String)handlersToDelete.elementAt(i);
            RegistryImpl.getRegistryImpl().unregisterInternal((String)handlersToDelete.elementAt(i));
         }

         size = modulesToInstall.size();

         for (int i = 0; i < size; i++) {
            int newHandle = modulesToInstall.elementAt(i);
            if (CodeModuleManager.isMidlet(newHandle)) {
               ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(newHandle);
               if (descriptors != null && descriptors.length > 0) {
                  ApplicationDescriptor original = descriptors[0];
                  String[] args = original.getArgs();
                  String[] newArgs = new String[]{"dostaticcontenthandlerregistration", Integer.toString(newHandle)};
                  ApplicationDescriptor registerDescriptor = new ApplicationDescriptor(original, original.getName(), newArgs);

                  try {
                     ApplicationManager.getApplicationManager().runApplication(registerDescriptor, false);
                  } catch (ApplicationManagerException var17) {
                  }
               }
            } else {
               ContentHandlerRegistrationHelper.getInstance().registerContentHandlers(modulesToInstall.elementAt(i));
            }
         }

         this._crc = crc;
         this._currentHandles = newHandles;
      }
   }

   void addActiveInvocation(int pid, Invocation invocation) {
      Invocation[] invocations = (Invocation[])this._activeInvocations.get(pid);
      if (invocations == null) {
         invocations = new Invocation[]{invocation};
         this._activeInvocations.put(pid, invocations);
      } else {
         Arrays.add(invocations, invocation);
      }
   }

   void removeActiveInvocation(int pid, Invocation invocation) {
      Invocation[] invocations = (Invocation[])this._activeInvocations.get(pid);
      if (invocations != null) {
         Arrays.remove(invocations, invocation);
         if (invocations.length == 0) {
            this._activeInvocations.remove(pid);
         }
      }
   }

   boolean requestHandlerStarted(int pid, ContentHandlerServerImpl handler) {
      InvocationCleanupManager$HandlerStatus status = (InvocationCleanupManager$HandlerStatus)this._activeRequestHandlers.get(pid);
      if (status == null) {
         this._activeRequestHandlers.put(pid, new InvocationCleanupManager$HandlerStatus(handler));
         return true;
      } else {
         status.invocationsAdded = true;
         return false;
      }
   }

   boolean responseHandlerStarted(int pid, RegistryImpl registry) {
      InvocationCleanupManager$HandlerStatus status = (InvocationCleanupManager$HandlerStatus)this._activeResponseHandlers.get(pid);
      if (status == null) {
         this._activeResponseHandlers.put(pid, new InvocationCleanupManager$HandlerStatus(registry));
         return true;
      } else {
         status.invocationsAdded = true;
         return false;
      }
   }

   void requestRetreived(int pid) {
      InvocationCleanupManager$HandlerStatus status = (InvocationCleanupManager$HandlerStatus)this._activeRequestHandlers.get(pid);
      if (status != null) {
         status.canExit = true;
      }
   }

   void responseRetreived(int pid) {
      InvocationCleanupManager$HandlerStatus status = (InvocationCleanupManager$HandlerStatus)this._activeResponseHandlers.get(pid);
      if (status != null) {
         status.canExit = true;
      }
   }

   void addContentHandlerModule(int moduleHandle, String classname, boolean dynamic) {
      IntEnumeration e = this._contentHandlerModules.keys();

      label28:
      while (e.hasMoreElements()) {
         int oldHandle = e.nextElement();
         String[] oldHandlesClassnames = (String[])this._contentHandlerModules.get(oldHandle);

         for (int i = 0; i < oldHandlesClassnames.length; i++) {
            if (classname.equals(oldHandlesClassnames[i])) {
               Arrays.removeAt(oldHandlesClassnames, i);
               if (oldHandlesClassnames.length == 0) {
                  this._contentHandlerModules.remove(oldHandle);
                  this._moduleNameToHandleMap.remove(CodeModuleManager.getModuleName(moduleHandle));
               }
               break label28;
            }
         }
      }

      String[] newClassnames = (String[])this._contentHandlerModules.get(moduleHandle);
      if (newClassnames == null) {
         String[] var9 = new String[]{classname};
         this._contentHandlerModules.put(moduleHandle, var9);
         this._moduleNameToHandleMap.put(CodeModuleManager.getModuleName(moduleHandle), new Integer(moduleHandle));
      } else {
         Arrays.add(newClassnames, classname);
      }

      this._classnameToRegistrationTypeMap.put(classname, new Boolean(dynamic));
   }

   void removeContentHandler(int moduleHandle, String classname) {
      String[] classnames = (String[])this._contentHandlerModules.get(moduleHandle);
      Arrays.remove(classnames, classname);
      if (classnames.length == 0) {
         this._contentHandlerModules.remove(moduleHandle);

         try {
            this._moduleNameToHandleMap.remove(CodeModuleManager.getModuleName(moduleHandle));
         } catch (Exception var5) {
         }
      }

      this._classnameToRegistrationTypeMap.remove(classname);
   }

   void moduleUpgraded(String moduleName, int moduleHandle) {
      Integer oldHandle = (Integer)this._moduleNameToHandleMap.get(moduleName);
      if (oldHandle != null) {
         int oldHandleValue = oldHandle;
         String[] classnames = (String[])this._contentHandlerModules.get(oldHandleValue);
         if (classnames != null) {
            String[] dynamicClassnames = new String[0];
            String[] staticClassnames = new String[0];

            for (int i = 0; i < classnames.length; i++) {
               Boolean dynamic = (Boolean)this._classnameToRegistrationTypeMap.get(classnames[i]);
               if (dynamic) {
                  Arrays.add(dynamicClassnames, classnames[i]);
               } else {
                  Arrays.add(staticClassnames, classnames[i]);
               }
            }

            if (dynamicClassnames.length > 0) {
               this._contentHandlerModules.put(moduleHandle, classnames);
            }

            if (staticClassnames.length > 0) {
               this._contentHandlerModules.put(oldHandleValue, classnames);
            } else {
               this._contentHandlerModules.remove(oldHandleValue);
            }
         }

         this._moduleNameToHandleMap.put(moduleName, new Integer(moduleHandle));
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != -1270659756336956134L) {
         if (guid == -4232371946002803201L) {
            this.checkModules();
            return;
         }

         if (guid == 256826950193107649L) {
            this.checkModules();
         }
      } else if (object0 instanceof int[]) {
         int[] deadProcesses = (int[])object0;

         for (int i = 0; i < deadProcesses.length; i++) {
            Invocation[] invocations = (Invocation[])this._activeInvocations.get(deadProcesses[i]);
            if (invocations != null) {
               for (int j = 0; j < invocations.length; j++) {
                  RegistryImpl.invocationFinished(invocations[j], 7);
               }

               this._activeInvocations.remove(deadProcesses[i]);
            }

            InvocationCleanupManager$HandlerStatus status = (InvocationCleanupManager$HandlerStatus)this._activeRequestHandlers.get(deadProcesses[i]);
            if (status != null) {
               if (!status.canExit) {
                  status.handler.cancelOldInvocations();
               }

               this._activeRequestHandlers.remove(deadProcesses[i]);
               if (status.invocationsAdded && status.handler.getQueueSize() > 0) {
                  status.handler.start();
               }
            }

            status = (InvocationCleanupManager$HandlerStatus)this._activeResponseHandlers.get(deadProcesses[i]);
            if (status != null) {
               if (!status.canExit) {
                  status.registry.removeOldInvocations();
               }

               this._activeResponseHandlers.remove(deadProcesses[i]);
               if (status.invocationsAdded && status.registry.getQueueSize() > 0) {
                  status.registry.respond();
               }
            }
         }
      }
   }

   static InvocationCleanupManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (InvocationCleanupManager)ar.getOrWaitFor(-863891308161769843L);
         if (_instance == null) {
            _instance = new InvocationCleanupManager();
            ar.put(-863891308161769843L, _instance);
         }
      }

      return _instance;
   }

   private InvocationCleanupManager() {
      this._crc = CodeStore.getModuleHandles(this._currentHandles);
   }
}
