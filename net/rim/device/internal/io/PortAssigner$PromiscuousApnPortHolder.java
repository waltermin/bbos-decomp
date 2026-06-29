package net.rim.device.internal.io;

import java.util.Vector;
import net.rim.device.api.io.IOPortAlreadyBoundException;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.vm.WeakReference;

final class PortAssigner$PromiscuousApnPortHolder implements RadioStatusListener {
   private int[] _clients;
   private int[] _keys;
   private Object[] _values;
   private Object[] _activeApns;
   private final PortAssigner this$0;

   private PortAssigner$PromiscuousApnPortHolder(PortAssigner _1) {
      this.this$0 = _1;
      this._keys = new int[0];
      this._values = new Object[0];
      this._clients = new int[0];
      this._activeApns = new Object[0];
      ProtocolDaemon pd = ProtocolDaemon.getInstance();
      if (pd != null) {
         pd.addRadioListener(this);
      }
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      String apnName = null;

      try {
         apnName = RadioInfo.getAccessPointName(apn);
      } catch (RadioException re) {
         return;
      }

      switch (state) {
         case 0:
            try {
               synchronized (this._activeApns) {
                  if (Arrays.getIndex(this._activeApns, apnName) < 0) {
                     Arrays.add(this._activeApns, apnName);
                  }
               }

               this.triggerPromiscuousPortsRegistration(apnName);
               return;
            } catch (Exception var11) {
               return;
            }
         default:
            synchronized (this._activeApns) {
               Arrays.remove(this._activeApns, apnName);
            }
      }
   }

   private final void registerPromiscuousPort(int port, Object connection) {
      Vector v = (Vector)this.get(port);
      boolean firstClient = false;
      if (v == null) {
         firstClient = true;
      } else if (v.size() > 0) {
         WeakReference wk = (WeakReference)v.elementAt(0);
         if (wk == null || wk.get() == null) {
            this.deregisterPromiscuousPort(port, connection);
            firstClient = this.getClients(port) <= 0;
         }
      }

      if (firstClient) {
         v = new Vector(2);
         v.addElement(new WeakReference(connection));
         this.put(port, v);
      }

      if (this.getClients(port) > 0) {
         try {
            ControlledAccess.assertRRISignatures(true);
         } catch (ControlledAccessException cae) {
            throw new IOPortAlreadyBoundException(PortAssigner.PORT_ALREADY_BOUND_ERROR_STRING + port);
         }
      }

      this.addClient(port);
      v.setElementAt(new WeakReference(connection), 0);
      if (this.getClients(port) <= 1) {
         Object[] apns = null;
         synchronized (this._activeApns) {
            if (this._activeApns.length <= 0) {
               return;
            }

            apns = new Object[this._activeApns.length];
            System.arraycopy(this._activeApns, 0, apns, 0, this._activeApns.length);
         }

         for (int i = apns.length - 1; i >= 0; i--) {
            String apn = (String)apns[i];
            if (this.this$0.registerConnection(port, connection, apn, false) && v.indexOf(apn) < 0) {
               v.addElement(apn);
            }
         }
      }
   }

   private final void deregisterPromiscuousPort(int port, Object connection) {
      this.removeClient(port);
      if (this.getClients(port) <= 0) {
         Vector v = (Vector)this.get(port);
         if (v != null) {
            for (int i = v.size() - 1; i >= 1; i--) {
               this.this$0.deregisterConnection(port, connection, (String)v.elementAt(i), false);
            }
         }

         this.remove(port);
      }
   }

   private final void triggerPromiscuousPortsRegistration(String apn) {
      int[] keys = this.keys();

      for (int i = keys.length - 1; i >= 0; i--) {
         int port = keys[i];
         Vector v = (Vector)this.get(port);
         if (v != null && v.size() > 0) {
            WeakReference wk = (WeakReference)v.elementAt(0);
            Object connection = wk != null ? wk.get() : null;
            if (connection != null) {
               if (v != null && v.indexOf(apn) < 0 && this.this$0.registerConnection(port, connection, apn, false)) {
                  v.addElement(apn);
               }
            } else {
               this.deregisterPromiscuousPort(port, connection);
            }
         }
      }
   }

   private final int[] keys() {
      return this._keys;
   }

   private final Object remove(int key) {
      int i = Arrays.getIndex(this._keys, key);
      Object obj = null;
      if (i >= 0) {
         obj = this._values[i];
         Arrays.removeAt(this._keys, i);
         Arrays.removeAt(this._values, i);
         Arrays.removeAt(this._clients, i);
      }

      return obj;
   }

   private final Object get(int key) {
      int i = Arrays.getIndex(this._keys, key);
      return i >= 0 ? this._values[i] : null;
   }

   private final void put(int key, Object value) {
      int i = Arrays.getIndex(this._keys, key);
      if (i < 0) {
         i = this._keys.length;
         Arrays.add(this._keys, key);
         Arrays.add(this._values, value);
         Arrays.add(this._clients, 0);
      }

      this._keys[i] = key;
      this._values[i] = value;
      this._clients[i] = 0;
   }

   private final void addClient(int key) {
      int i = Arrays.getIndex(this._keys, key);
      if (i >= 0) {
         this._clients[i]++;
      }
   }

   private final void removeClient(int key) {
      int i = Arrays.getIndex(this._keys, key);
      if (i >= 0) {
         this._clients[i]--;
      }
   }

   private final int getClients(int key) {
      int i = Arrays.getIndex(this._keys, key);
      return i >= 0 ? this._clients[i] : 0;
   }

   PortAssigner$PromiscuousApnPortHolder(PortAssigner x0, PortAssigner$1 x1) {
      this(x0);
   }
}
