package net.rim.plazmic.internal.mediaengine.util;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntHashtable;

public final class ProfileManager {
   public static final int PROFILE_ELEMENT;
   public static final int PROFILE_TIMER;
   public static final int PROFILE_VARIABLE;
   public static boolean _enabled = true;
   static Hashtable _elements;
   static IntHashtable _symbolMap;
   static String _appName;

   private ProfileManager() {
   }

   public static final void clearProfile() {
      Enumeration elements = getEnumeration();

      while (elements.hasMoreElements()) {
         ProfileElement element = (ProfileElement)elements.nextElement();
         remove(element.getName());
      }
   }

   public static final void reset(String name) {
      if (_enabled) {
         ProfileElement element = (ProfileElement)_elements.get(((StringBuffer)(new Object())).append(_appName).append(name).toString());
         if (element != null) {
            element.reset();
         }
      }
   }

   public static final void reset() {
      if (_enabled) {
         int size = _elements.size();
         Enumeration e = _elements.elements();

         for (int i = 0; i < size; i++) {
            ProfileElement element = (ProfileElement)e.nextElement();
            element.reset();
         }
      }
   }

   public static final void initialize(String name) {
      ProfileElement element = (ProfileElement)_elements.get(((StringBuffer)(new Object())).append(_appName).append(name).toString());
      if (element != null) {
         element.initialize();
      }
   }

   public static final void initialize() {
      int size = _elements.size();
      Enumeration e = _elements.elements();

      for (int i = 0; i < size; i++) {
         ProfileElement element = (ProfileElement)e.nextElement();
         element.initialize();
      }
   }

   public static final ProfileElement add(String name, int elementType) {
      return addGlobal(((StringBuffer)(new Object())).append(_appName).append(name).toString(), elementType);
   }

   public static final ProfileElement addGlobal(String name, int elementType) {
      ProfileElement element;
      switch (elementType) {
         case 0:
            element = new ProfileElement(name);
            break;
         case 1:
         default:
            element = new ProfileTimer(name);
            break;
         case 2:
            element = new ProfileVariable(name);
      }

      _elements.put(element.getName(), element);
      return element;
   }

   public static final String setSymbol(int value, String name) {
      return (String)_symbolMap.put(value, name);
   }

   public static final String getSymbol(int value) {
      return (String)_symbolMap.get(value);
   }

   public static final void remove(String name) {
      _elements.remove(((StringBuffer)(new Object())).append(_appName).append(name).toString());
   }

   public static final ProfileElement get(String name, int elementType) {
      return getGlobal(((StringBuffer)(new Object())).append(_appName).append(name).toString(), elementType);
   }

   public static final ProfileElement getGlobal(String name, int elementType) {
      ProfileElement element = (ProfileElement)_elements.get(name);
      if (element == null) {
         element = addGlobal(name, elementType);
         if (element == null) {
            return null;
         }
      }

      switch (elementType) {
         case 0:
            break;
         case 1:
         default:
            if (!(element instanceof ProfileTimer)) {
               return null;
            }
            break;
         case 2:
            if (!(element instanceof ProfileVariable)) {
               element = null;
            }
      }

      return element;
   }

   public static final ProfileElement get(String name) {
      return (ProfileElement)_elements.get(((StringBuffer)(new Object())).append(_appName).append(name).toString());
   }

   public static final ProfileElement getGlobal(String name) {
      return (ProfileElement)_elements.get(name);
   }

   public static final Enumeration getEnumeration() {
      return _elements.elements();
   }

   public static final int getSize() {
      return _elements.size();
   }

   public static final ProfileVariable getVariable(String name) {
      return (ProfileVariable)get(name, 2);
   }

   public static final ProfileTimer getTimer(String name) {
      return (ProfileTimer)get(name, 1);
   }

   public static final boolean start(String name) {
      if (!_enabled) {
         return false;
      }

      ProfileTimer timer = (ProfileTimer)get(name, 1);
      return timer != null ? timer.start() : false;
   }

   public static final boolean startSingleShot(String name) {
      if (!_enabled) {
         return false;
      }

      ProfileTimer timer = (ProfileTimer)get(name, 1);
      return timer != null ? timer.startSingleShot() : false;
   }

   public static final boolean stop(String name) {
      if (!_enabled) {
         return false;
      }

      ProfileTimer timer = (ProfileTimer)get(name, 1);
      return timer != null ? timer.stop() : false;
   }

   public static final int set(String name, int iValue) {
      ProfileVariable variable = (ProfileVariable)get(name, 2);
      return variable != null ? variable.set(iValue) : -1;
   }

   public static final int setGlobal(String name, int iValue) {
      ProfileVariable variable = (ProfileVariable)getGlobal(name, 2);
      return variable != null ? variable.set(iValue) : -1;
   }

   public static final boolean recordStart(String name, int capacity, boolean wrap) {
      ProfileVariable variable = (ProfileVariable)get(name, 2);
      return variable != null ? variable.setSave(capacity, wrap) : false;
   }

   public static final boolean recordStop(String name) {
      ProfileVariable variable = (ProfileVariable)get(name, 2);
      return variable != null ? variable.unsetSave() : false;
   }

   public static final boolean isRecording(String name) {
      ProfileVariable variable = (ProfileVariable)get(name, 2);
      return variable != null ? variable.isSaved() : false;
   }

   public static final int getValue(String name) {
      ProfileElement element = (ProfileElement)_elements.get(((StringBuffer)(new Object())).append(_appName).append(name).toString());
      return element != null && element instanceof ProfileVariable ? ((ProfileVariable)element).get() : -1;
   }

   public static final int getGlobalValue(String name) {
      ProfileElement element = (ProfileElement)_elements.get(name);
      if (element != null) {
         return !(element instanceof ProfileVariable) ? -1 : ((ProfileVariable)element).get();
      } else {
         return setGlobal(name, -1);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      ApplicationRegistry store = ApplicationRegistry.getApplicationRegistry();
      boolean var7 = false /* VF: Semaphore variable */;

      label73:
      try {
         var7 = true;
         _elements = (Hashtable)store.get(1416792644370612918L);
         if (_elements == null) {
            _elements = (Hashtable)(new Object());
            store.put(1416792644370612918L, _elements);
            var7 = false;
         } else {
            var7 = false;
         }
      } finally {
         if (var7) {
            if (_elements == null) {
               _elements = (Hashtable)(new Object());
            }
            break label73;
         }
      }

      store = ApplicationRegistry.getApplicationRegistry();
      boolean var4 = false /* VF: Semaphore variable */;

      label66:
      try {
         var4 = true;
         _symbolMap = (IntHashtable)store.get(8128867418941961688L);
         if (_symbolMap == null) {
            _symbolMap = (IntHashtable)(new Object());
            store.put(8128867418941961688L, _symbolMap);
            var4 = false;
         } else {
            var4 = false;
         }
      } finally {
         if (var4) {
            if (_symbolMap == null) {
               _symbolMap = (IntHashtable)(new Object());
            }
            break label66;
         }
      }

      _appName = "";
      ApplicationDescriptor ad = ApplicationDescriptor.currentApplicationDescriptor();
      _appName = ((StringBuffer)(new Object())).append(ad.getName()).append(":").toString();
   }
}
