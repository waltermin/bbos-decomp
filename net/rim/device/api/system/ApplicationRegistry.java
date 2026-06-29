package net.rim.device.api.system;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.internal.system.ApplicationRegistryHashtable;
import net.rim.vm.DebugSupport;
import net.rim.vm.Process;

public final class ApplicationRegistry {
   private ApplicationRegistryHashtable _registry = new ApplicationRegistryHashtable(1223);
   private LongHashtable _monitors = new LongHashtable();
   private boolean _startupComplete;
   public static final int MAX_WAIT_MILLIS;
   private static final int TYPE_HASHTABLE;
   private static final int TYPE_LONG_HASHTABLE;
   private static final int TYPE_INT_HASHTABLE;
   private static final int TYPE_VECTOR;
   private static final int TYPE_INT_VECTOR;
   private static final int TYPE_OBJECT;

   ApplicationRegistry() {
      Process.registerAppRegistry(this);
   }

   public final Object get(long id) {
      return this.get(0, id, true, null, null);
   }

   public final Object getOrWaitFor(long id) {
      Monitor monitor;
      synchronized (this._registry) {
         Object obj = this.get(0, id, true, null, null);
         if (obj != null) {
            return obj;
         }

         monitor = (Monitor)this._monitors.get(id);
         if (monitor == null) {
            monitor = new Monitor(id, Thread.currentThread());
            this._monitors.put(id, monitor);
            return null;
         }

         Thread owner = monitor.getOwner();
         if (owner == null) {
            throw new RuntimeException("ApplicationRegistry.getOrWaitFor(0x" + NumberUtilities.toString(id, 16) + ") missing owner");
         }

         if (!owner.isAlive()) {
            synchronized (monitor) {
               monitor.wakeyWakey();
            }

            throw new RuntimeException("ApplicationRegistry.getOrWaitFor(0x" + NumberUtilities.toString(id, 16) + ") owner died " + owner);
         }
      }

      return this.waitForObjectToBeRegistered(monitor, 0, id, true, null, null, false);
   }

   final Object get(int moduleHandle, long id, boolean protect, CodeSigningKey readKey, CodeSigningKey replaceKey) {
      Object obj = this._registry.get(id, protect);
      if (obj instanceof ControlledAccess) {
         ControlledAccess ca = (ControlledAccess)obj;
         obj = ca.getObject();
         if (!protect) {
            ca.assertReadPermission(moduleHandle);
            ca.assertKeys(readKey, replaceKey);
         }
      }

      return obj;
   }

   public final Object remove(long id) {
      return this.remove(0, id, true, null, null);
   }

   final Object remove(int moduleHandle, long id, boolean protect, CodeSigningKey readKey, CodeSigningKey replaceKey) {
      Object o = this.get(moduleHandle, id, protect, readKey, replaceKey);
      if (o != null) {
         this._registry.remove(id, protect);
      }

      return o;
   }

   final void kickAllWaitingThreads() {
      this._startupComplete = true;
      synchronized (this._monitors) {
         Enumeration e = this._monitors.elements();

         while (e.hasMoreElements()) {
            Monitor monitor = (Monitor)e.nextElement();
            System.out.println("AR: kick 0x" + NumberUtilities.toString(monitor.getGUID(), 16));
            synchronized (monitor) {
               monitor.wakeyWakey();
            }
         }

         this._monitors.clear();
      }
   }

   final ControlledAccess getControlledAccess(long id) {
      return this.getControlledAccess(id, true);
   }

   final ControlledAccess getControlledAccess(long id, boolean protect) {
      Object obj = this._registry.get(id, protect);
      return !(obj instanceof ControlledAccess) ? null : (ControlledAccess)obj;
   }

   public final void put(long id, Object value) {
      this.put(0, id, true, value, false);
   }

   public final Object replace(long id, Object value) {
      return this.put(0, id, true, value, true);
   }

   final Object put(int moduleHandle, long id, boolean protect, Object value, boolean allowReplace) {
      Monitor monitor;
      Object oldValue;
      synchronized (this._registry) {
         oldValue = this._registry.get(id, protect);
         if (id == (int)id && DeviceInfo.isSimulator()) {
            throw new IllegalArgumentException("32-bit id used");
         }

         if (!allowReplace && oldValue != null) {
            throw new IllegalArgumentException("object already exists");
         }

         if (oldValue instanceof ControlledAccess) {
            ControlledAccess ca = (ControlledAccess)oldValue;
            oldValue = ca.getObject();
            if (!protect) {
               ca.assertReplacePermission(moduleHandle);

               try {
                  ca.assertReadPermission(moduleHandle);
               } catch (ControlledAccessException e) {
                  oldValue = null;
               }
            }
         }

         this._registry.put(id, value, protect);
         monitor = (Monitor)this._monitors.remove(id);
      }

      if (monitor != null) {
         synchronized (monitor) {
            monitor.wakeyWakey();
         }
      }

      return oldValue;
   }

   public final Object waitFor(long id) {
      return this.waitFor(0, id, true, null, null, false);
   }

   public final Object waitForStartup(long id) {
      return this.waitFor(0, id, true, null, null, true);
   }

   final Object waitFor(int moduleHandle, long id, boolean protect, CodeSigningKey readKey, CodeSigningKey replaceKey, boolean startupOnly) {
      Monitor monitor;
      synchronized (this._registry) {
         Object obj = this.get(moduleHandle, id, protect, readKey, replaceKey);
         if (obj != null) {
            return obj;
         }

         if (startupOnly && this._startupComplete) {
            return null;
         }

         monitor = (Monitor)this._monitors.get(id);
         if (monitor == null) {
            monitor = new Monitor(id);
            this._monitors.put(id, monitor);
         }
      }

      return this.waitForObjectToBeRegistered(monitor, moduleHandle, id, protect, readKey, replaceKey, startupOnly);
   }

   private final Object waitForObjectToBeRegistered(
      Monitor monitor, int moduleHandle, long id, boolean protect, CodeSigningKey readKey, CodeSigningKey replaceKey, boolean allowNull
   ) {
      synchronized (monitor) {
         if (!monitor.hasBeenNotified()) {
            try {
               monitor.wait(120000);
            } catch (InterruptedException var12) {
            }
         }

         Object obj = this.get(moduleHandle, id, protect, readKey, replaceKey);
         if (obj == null && !allowNull) {
            DebugSupport.logStackTraces();
            throw new RuntimeException("ApplicationRegistry.waitFor(0x" + NumberUtilities.toString(id, 16) + ") timeout");
         } else {
            return obj;
         }
      }
   }

   public final Vector getVector(long id) {
      return (Vector)this.get(id, 4);
   }

   public final IntVector getIntVector(long id) {
      return (IntVector)this.get(id, 5);
   }

   public final Hashtable getHashtable(long id) {
      return (Hashtable)this.get(id, 1);
   }

   public final IntHashtable getIntHashtable(long id) {
      return (IntHashtable)this.get(id, 3);
   }

   public final LongHashtable getLongHashtable(long id) {
      return (LongHashtable)this.get(id, 2);
   }

   public final Object getObject(long id) {
      return this.get(id, 6);
   }

   private final Object get(long id, int type) {
      synchronized (this._registry) {
         Object obj = this.get(id);
         if (obj == null) {
            switch (type) {
               case 0:
                  throw new IllegalArgumentException();
               case 1:
               default:
                  obj = new Hashtable();
                  break;
               case 2:
                  obj = new LongHashtable();
                  break;
               case 3:
                  obj = new IntHashtable();
                  break;
               case 4:
                  obj = new Vector();
                  break;
               case 5:
                  obj = new IntVector();
                  break;
               case 6:
                  obj = new Object();
            }

            this.put(id, obj);
         }

         return obj;
      }
   }

   public static final ApplicationRegistry getApplicationRegistry() {
      return (ApplicationRegistry)Process.getAppRegistry();
   }
}
