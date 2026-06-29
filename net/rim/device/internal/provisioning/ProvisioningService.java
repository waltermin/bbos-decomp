package net.rim.device.internal.provisioning;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceBookSyncCollection;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.RadioInternal;

public final class ProvisioningService extends Thread implements GlobalEventListener {
   private ServiceBook _sb;
   private int _srId;
   private boolean _kick;
   private Vector _handlers;
   public static final long GUID = 2028567949942654338L;
   public static final int MAJOR_VERSION = 16;
   public static final int MINOR_VERSION = 0;
   public static final int VERSION = 16;
   private static final String PROV_SB_NAME = "Provisioning";
   private static final String PROV_SB_CID = "PROVISIONING";

   public static final void ProvisioningServiceMain(String[] args) {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      Proxy p = Proxy.getInstance();
      ProvisioningService ps = new ProvisioningService(p);
      p.startThread(ps);
      p.addGlobalEventListener(ps);
      ServiceBook.getSB().registerCIDAsSingleton("PROVISIONING");
      ServiceBookSyncCollection sync = ServiceBookSyncCollection.getInstance();
      sync.registerCIDForRestoreDisable("PROVISIONING");
      reg.put(2028567949942654338L, ps);
   }

   public static final ProvisioningService getInstance() {
      return (ProvisioningService)ApplicationRegistry.getApplicationRegistry().waitFor(2028567949942654338L);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private ProvisioningService(Application app) {
      EventLogger.register(2028567949942654338L, "prov", 2);
      this._handlers = (Vector)(new Object());
      this._sb = ServiceBook.getSB();
      this._srId = this.pickServiceRecord();
      boolean isBBXpSupported = true;

      try {
         RadioInternal.getBlackBerryExperienceMode();
      } finally {
         ;
      }

      if (isBBXpSupported) {
         boolean var26 = false /* VF: Semaphore variable */;

         label147:
         try {
            var26 = true;
            this.addHandler(new BBXpHandler(app));
            var26 = false;
         } finally {
            if (var26) {
               log("Failed to add BBXpHandler", 2);
               break label147;
            }
         }
      }

      if (WafOptionHandler.isSupported()) {
         boolean var20 = false /* VF: Semaphore variable */;

         label142:
         try {
            var20 = true;
            this.addHandler(new WafOptionHandler());
            var20 = false;
         } finally {
            if (var20) {
               log("Failed to add WafOptionHandler", 2);
               break label142;
            }
         }
      }

      boolean var14 = false /* VF: Semaphore variable */;

      label138:
      try {
         var14 = true;
         this.addHandler(new SuplHandler());
         var14 = false;
      } finally {
         if (var14) {
            log("Failed to add SuplHandler", 2);
            break label138;
         }
      }

      try {
         this.addHandler(new IntelligentPDPHandler());
      } finally {
         log("Failed to add IntelligentPDPHandler", 2);
         return;
      }
   }

   public final synchronized void addHandler(ProvisioningHandler handler) {
      this._handlers = ListenerUtilities.addListener(this._handlers, handler);
      this.updateHandler(handler);
   }

   public final synchronized void removeHandler(ProvisioningHandler handler) {
      this._handlers = ListenerUtilities.removeListener(this._handlers, handler);
   }

   public final void updateHandler(ProvisioningHandler handler) {
      DataBuffer db = (DataBuffer)(new Object(true));
      IntIntHashtable iih = (IntIntHashtable)(new Object());
      this.parseData(this.getAppData(), iih, db);
      handler.updateProvisioningData(iih, db);
   }

   public final ServiceRecord getServiceRecord() {
      ServiceRecord sr = null;
      if (this._srId != -1) {
         sr = this._sb.getRecordById(this._srId);
      }

      return sr;
   }

   private final void parseData(byte[] data, IntIntHashtable iih, DataBuffer db) {
      if (data != null && data.length > 0) {
         db.setData(data, 0, data.length);
         int version = db.readUnsignedByte();
         if ((version & 240) != 16) {
            throw new Object();
         }
      }

      while (db.available() > 0) {
         int type = db.readUnsignedByte();
         if (type == 0) {
            return;
         }

         iih.put(type, db.getPosition());
         db.skipBytes(db.readCompressedInt());
      }
   }

   private final synchronized byte[] getAppData() {
      if (this._srId != -1) {
         ServiceRecord sr = this._sb.getRecordById(this._srId);
         if (this.verifyServiceRecord(sr)) {
            return sr.getApplicationData();
         }
      }

      return null;
   }

   private final int pickServiceRecord() {
      ServiceRecord[] srs = this._sb.findRecordsByCid("PROVISIONING");
      if (srs != null) {
         for (int i = 0; i < srs.length; i++) {
            if (srs[i].getType() == 0 && StringUtilities.strEqual("Provisioning", srs[i].getName())) {
               return srs[i].getId();
            }
         }
      }

      return -1;
   }

   private final boolean verifyServiceRecord(ServiceRecord sr) {
      return sr != null
         && sr.getType() == 0
         && StringUtilities.strEqual("Provisioning", sr.getName())
         && StringUtilities.strEqualIgnoreCase("PROVISIONING", sr.getCid(), 1701707776);
   }

   public static final void log(String str, int level) {
      EventLogger.logEvent(2028567949942654338L, str.getBytes(), level);
      System.out.println(str);
   }

   @Override
   public final void run() {
      while (true) {
         byte[] data = null;
         synchronized (this) {
            while (!this._kick) {
               try {
                  this.wait();
               } finally {
                  continue;
               }
            }

            this._kick = false;
            data = this.getAppData();
         }

         try {
            this.processData(data);
         } finally {
            continue;
         }
      }
   }

   private final void processData(byte[] data) {
      DataBuffer db = (DataBuffer)(new Object(true));
      IntIntHashtable iih = (IntIntHashtable)(new Object());
      if (data != null) {
         log("Prov data updating", 5);
         this.parseData(data, iih, db);
      } else {
         log("Prov data removed", 5);
      }

      Enumeration handlers = this._handlers.elements();

      while (handlers.hasMoreElements()) {
         ProvisioningHandler handler = (ProvisioningHandler)handlers.nextElement();

         try {
            handler.updateProvisioningData(iih, db);
         } finally {
            continue;
         }
      }
   }

   @Override
   public final synchronized void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4220058463650496006L || guid == 8288627527798139133L || guid == 2522898683889177438L) {
         ServiceRecord sr = this._sb.getRecordById(data0);
         if (guid == 2522898683889177438L && data0 == this._srId
            || sr != null
               && StringUtilities.strEqual("Provisioning", sr.getName())
               && StringUtilities.strEqualIgnoreCase("PROVISIONING", sr.getCid(), 1701707776)) {
            this._srId = this.pickServiceRecord();
            this._kick = true;
            this.notify();
         }
      }
   }
}
