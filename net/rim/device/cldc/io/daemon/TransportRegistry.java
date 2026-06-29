package net.rim.device.cldc.io.daemon;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.io.TransportBase;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.io.TrafficLogger;

public final class TransportRegistry extends Thread {
   private Hashtable _instances = new Hashtable();
   private TransportRegistry$Request[] _requests = new TransportRegistry$Request[0];
   private TrafficLogger _tLogger;
   private static final long ID = -3443331856084987690L;
   public static final long REGISTER_NOW_RUNNABLE = -258173064361783611L;
   public static final long REGISTER_NOW_RESULT = -8887235436078834958L;

   private TransportRegistry() {
   }

   private final TransportBase getSingleton(String transportName) {
      TransportBase transport = (TransportBase)this._instances.get(transportName);
      if (transport != null) {
         return transport;
      }

      if (Thread.currentThread() == this) {
         try {
            Class transportClass = Class.forName(transportName);
            transport = (TransportBase)transportClass.newInstance();
         } catch (InstantiationException var9) {
         } catch (ClassNotFoundException var10) {
         } catch (IllegalAccessException var11) {
         } catch (ClassCastException var12) {
         }

         if (transport != null) {
            try {
               transport.init();
            } catch (IOException var8) {
            }

            synchronized (this) {
               transport.setTrafficLogger(this._tLogger);
               this._instances.put(transportName, transport);
               return transport;
            }
         } else {
            return transport;
         }
      } else {
         TransportRegistry$Request request = new TransportRegistry$Request();
         request._transportName = transportName;
         synchronized (this._requests) {
            Arrays.add(this._requests, request);
            this._requests.notify();
         }

         synchronized (request) {
            while (!request._done) {
               try {
                  request.wait();
               } catch (InterruptedException var13) {
               }
            }

            return request._transportBase;
         }
      }
   }

   @Override
   public final void run() {
      while (true) {
         TransportRegistry$Request request;
         synchronized (this._requests) {
            while (this._requests.length == 0) {
               try {
                  request = null;
                  this._requests.wait();
               } catch (InterruptedException var8) {
               }
            }

            request = this._requests[0];
            Arrays.removeAt(this._requests, 0);
         }

         TransportBase transport = null;

         try {
            transport = this.getSingleton(request._transportName);
         } catch (IOException var7) {
         }

         synchronized (request) {
            request._transportBase = transport;
            request._done = true;
            request.notify();
         }
      }
   }

   private static final TransportRegistry getRegistry() {
      return (TransportRegistry)ApplicationRegistry.getApplicationRegistry().waitFor(-3443331856084987690L);
   }

   public static final TransportBase get(String transportName) {
      return getRegistry().getSingleton(transportName);
   }

   static final void init() {
      TransportRegistry transportRegistry = new TransportRegistry();
      ApplicationRegistry.getApplicationRegistry().put(-3443331856084987690L, transportRegistry);
      transportRegistry.start();
   }

   public static final void setTrafficLogger(TrafficLogger logger) {
      getRegistry().setTrafficLoggerInternal(logger);
   }

   private final synchronized void setTrafficLoggerInternal(TrafficLogger logger) {
      this._tLogger = logger;
      Enumeration enu = this._instances.elements();

      while (enu.hasMoreElements()) {
         ((TransportBase)enu.nextElement()).setTrafficLogger(this._tLogger);
      }
   }
}
