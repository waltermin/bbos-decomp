package net.rim.device.cldc.impl.tunnel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.tunnel.Tunnel;
import net.rim.device.cldc.io.tunnel.TunnelConfig;
import net.rim.device.cldc.io.tunnel.TunnelFactory;
import net.rim.vm.WeakReference;

public final class TunnelFactoryImpl extends TunnelFactory implements TunnelEvent {
   private Hashtable _managers = (Hashtable)(new Object(2));
   private final int _capableTunnels;

   TunnelFactoryImpl() {
      EventLogger.register(4292459735430940092L, "net.rim.tunnel", 2);
      EventLogger.logEvent(4292459735430940092L, 1231972724, 0);
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 7:
            this._capableTunnels = 7;
            break;
         default:
            this._capableTunnels = 1;
      }

      ApplicationRegistry.getApplicationRegistry().put(4292459735430940092L, this);
   }

   private final void pruneDeadManagers() {
      Enumeration e = this._managers.keys();

      while (e.hasMoreElements()) {
         Object key = e.nextElement();
         WeakReference w = (WeakReference)this._managers.get(key);
         if (w == null || w.get() == null) {
            this._managers.remove(key);
         }
      }
   }

   @Override
   public final Tunnel open(TunnelConfig config) {
      TunnelManagerImpl manager = null;
      TunnelManagerImpl managerForDormancy = null;
      TunnelImpl tunnel = new TunnelImpl(config);
      synchronized (this._managers) {
         this.pruneDeadManagers();
         WeakReference w = (WeakReference)this._managers.get(new TunnelFactoryImpl$APNKey(config.getName()));
         if (w != null && (manager = (TunnelManagerImpl)w.get()) != null) {
            Object tunnels = manager.setup(-380645052, null);
            if (tunnels == null) {
               throw new Object();
            }

            synchronized (tunnels) {
               if (manager.isClosed()) {
                  manager = null;
               } else {
                  if (!config.equivalent(manager.getConfig())) {
                     throw new Object("Configuration does not match");
                  }

                  manager.addTunnel(tunnel);
               }
            }
         }

         if (manager == null) {
            manager = new TunnelManagerImpl(config);
            manager.addTunnel(tunnel);
            this._managers.put(new TunnelFactoryImpl$APNKey(config.getName()), new Object(manager));
            ProtocolDaemon.getInstance().startThread(manager);
            if (this.outOfPhysicalTunnels()) {
               managerForDormancy = this.getManagerForDormancy();
            }
         }
      }

      tunnel.setManager(manager);
      if (managerForDormancy == null) {
         manager.kick(false);
      } else {
         managerForDormancy.standby();
         if (manager != managerForDormancy) {
            manager.kick(managerForDormancy.getStatus() != 0);
         }
      }

      return tunnel;
   }

   private final boolean outOfPhysicalTunnels() {
      int count = 0;
      Enumeration e = this._managers.elements();

      while (e.hasMoreElements()) {
         WeakReference w = (WeakReference)e.nextElement();
         TunnelManagerImpl manager;
         if (w != null && (manager = (TunnelManagerImpl)w.get()) != null && manager.getStatus() != 7) {
            count++;
         }
      }

      return count > this._capableTunnels;
   }

   private final TunnelManagerImpl getManagerForDormancy() {
      TunnelManagerImpl ret = null;
      Enumeration e = this._managers.elements();

      while (e.hasMoreElements()) {
         WeakReference w = (WeakReference)e.nextElement();
         TunnelManagerImpl manager;
         if (w != null
            && (manager = (TunnelManagerImpl)w.get()) != null
            && (ret == null || manager.getConfig().getPriority() >= ret.getConfig().getPriority() && manager.getAge() < ret.getAge())) {
            ret = manager;
         }
      }

      return ret;
   }

   @Override
   public final Object setup(int callType, Object context) {
      switch (callType) {
         case 202662285:
            return this.getTunnels((String)context);
         case 202662286:
            return this.getManagers((String)context);
         case 1483043725:
            this.clean();
         default:
            return null;
      }
   }

   protected final void kickManagers(boolean dormant) {
      synchronized (this._managers) {
         this.pruneDeadManagers();
         Enumeration e = this._managers.elements();

         while (e.hasMoreElements()) {
            WeakReference w = (WeakReference)e.nextElement();
            TunnelManagerImpl manager;
            if (w != null && (manager = (TunnelManagerImpl)w.get()) != null) {
               if (dormant) {
                  if (manager.getStatus() == 7) {
                     manager.kick(true);
                     break;
                  }
               } else if (manager.getStatus() != 7) {
                  manager.kick(false);
               }
            }
         }
      }
   }

   private final Vector getTunnels(String name) {
      Vector v = (Vector)(new Object());
      synchronized (this._managers) {
         Enumeration e = this._managers.elements();

         while (e.hasMoreElements()) {
            WeakReference w = (WeakReference)e.nextElement();
            TunnelManagerImpl manager;
            if (w != null && (manager = (TunnelManagerImpl)w.get()) != null && (name == null || name.equals(manager.getConfig().getName()))) {
               manager.setup(202662285, v);
            }
         }

         return v;
      }
   }

   private final Vector getManagers(String name) {
      Vector v = (Vector)(new Object());
      synchronized (this._managers) {
         Enumeration e = this._managers.elements();

         while (e.hasMoreElements()) {
            WeakReference w = (WeakReference)e.nextElement();
            TunnelManagerImpl manager;
            if (w != null && (name == null || (manager = (TunnelManagerImpl)w.get()) != null && name.equals(manager.getConfig().getName()))) {
               v.addElement(w);
            }
         }

         return v;
      }
   }

   private final void clean() {
      synchronized (this._managers) {
         this.pruneDeadManagers();
         Enumeration e = this._managers.elements();

         while (e.hasMoreElements()) {
            WeakReference w = (WeakReference)e.nextElement();
            TunnelManagerImpl manager;
            if (w != null && (manager = (TunnelManagerImpl)w.get()) != null) {
               manager.setup(1483043725, null);
            }
         }
      }
   }
}
