package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.apps.internal.blackberryemail.properties.MessagePropertiesModel;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;

public class ReliableTransmissionHelper implements TransmissionStatusListener {
   private ReliableTransmissionListener _listener;
   private String _typeString;
   private TransmissionService _service;
   private TransactionRecord[] _transactions;
   private ContextObject _contextObject;
   private int _lastCheckedIndexId;
   private static final long TRANSACTIONS_GUID = -2159541579865757717L;
   private static final long LAST_CHECKED_RETRY_INDEX_GUID = -9018585818145369823L;
   private static final int MAX_RETRY_PACKETS_PER_SCAN = 5;
   private static final long RETRY_TIMEOUT = 900000L;
   private static final long RETRY_BACKOFF_INTERVAL = 480000L;
   private static final long MAX_RETRIES_ALLOWED = 5L;
   private static final long SUCCESSFUL_TIMEOUT = 172800000L;

   @Override
   public void updateTransmissionStatus(TransmissionService transmissionService, int tag, int code, Object contextObject) {
      TransactionRecord transaction = this.findTransaction(tag);
      if (transaction != null) {
         int newStatus = 0;
         switch (code) {
            case 0:
               newStatus = 1;
               break;
            case 1:
            case 2:
            case 3:
            case 7:
               newStatus = 0;
               break;
            case 5:
            case 6:
               newStatus = 3;
               break;
            case 4229:
            case 4545:
               if (this._listener != null) {
                  this._listener.failedTransmission(transaction._serviceRecord, transaction._payload, code);
               }

               this.removeTransaction(transaction);
               return;
            default:
               OTAFMEvents.logEvent(1062490702, String.valueOf(code), 2);
         }

         if ((code & 128) != 0) {
            OTAFMEvents.logEvent(1413829202, String.valueOf(code), 2);
            newStatus = 2;
         }

         if (transaction._status != newStatus) {
            transaction._status = newStatus;
            transaction.updateLastChanged();
            PersistentObject.commit(transaction);
         }
      }
   }

   boolean hasWork() {
      return this._transactions.length > 0 && canTransmit();
   }

   void flushTransactions() {
      synchronized (this._transactions) {
         Array.resize(this._transactions, 0);
      }
   }

   public void transmitObject(ServiceRecord serviceRecord, Object object) {
      TransactionRecord transaction = this.createTransaction(serviceRecord, object);
      this.transmitTransaction(transaction);
   }

   boolean packetStillInStack(ServiceRecord serviceRecord, Recognizer recognizer) {
      TransactionRecord currentConfigCommand = this.locateTransaction(serviceRecord, recognizer);
      return currentConfigCommand != null && currentConfigCommand.packetNotSent();
   }

   TransactionRecord locateTransaction(ServiceRecord serviceRecord, Recognizer recognizer) {
      synchronized (this._transactions) {
         int index = this.locateTransactionIndex(0, serviceRecord, recognizer);
         return index != -1 ? this._transactions[index] : null;
      }
   }

   Object purgeTransactions(ServiceRecord serviceRecord, Recognizer recognizer) {
      Object payload = null;
      synchronized (this._transactions) {
         boolean foundSome = false;
         int index = 0;

         while (index != -1) {
            index = this.locateTransactionIndex(index, serviceRecord, recognizer);
            if (index != -1) {
               payload = this._transactions[index]._payload;
               foundSome = true;
               Arrays.removeAt(this._transactions, index);
            }
         }

         if (foundSome) {
            PersistentObject.commit(this._transactions);
         }

         return payload;
      }
   }

   void serviceRecordRemoved(ServiceRecord serviceRecord) {
      synchronized (this._transactions) {
         int count = this._transactions.length;
         int dest = 0;

         for (int src = 0; src < count; src++) {
            if (this._transactions[src]._serviceRecord != serviceRecord && src != dest) {
               this._transactions[dest++] = this._transactions[src];
            }
         }

         if (dest != count) {
            Array.resize(this._transactions, dest);
            PersistentObject.commit(this._transactions);
         }
      }
   }

   void periodicScan() {
      if (canTransmit()) {
         this.scanForRetries();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void transmitTransaction(TransactionRecord transaction) {
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         this._contextObject.put(-6095803566992128485L, transaction._serviceRecord);
         this._service.transmitObject(this._typeString, transaction._payload, this, transaction._id, this._contextObject);
         var4 = false;
      } finally {
         if (var4) {
            this._contextObject.remove(-6095803566992128485L);
         }
      }

      this._contextObject.remove(-6095803566992128485L);
   }

   private TransactionRecord findTransaction(int id) {
      synchronized (this._transactions) {
         for (int i = this._transactions.length - 1; i >= 0; i--) {
            if (this._transactions[i]._id == id) {
               return this._transactions[i];
            }
         }

         return null;
      }
   }

   private int locateTransactionIndex(int initialIndex, ServiceRecord serviceRecord, Recognizer recognizer) {
      synchronized (this._transactions) {
         int count = this._transactions.length;

         for (int i = initialIndex; i < count; i++) {
            TransactionRecord transaction = this._transactions[i];
            if (serviceRecord == transaction._serviceRecord && recognizer.recognize(transaction._payload)) {
               return i;
            }
         }

         return -1;
      }
   }

   private TransactionRecord createTransaction(ServiceRecord serviceRecord, Object payload) {
      TransactionRecord transaction = new MessagePropertiesModel(serviceRecord, payload);
      synchronized (this._transactions) {
         int count = this._transactions.length;
         Array.resize(this._transactions, count + 1);
         this._transactions[count] = transaction;
         PersistentObject.commit(this._transactions);
         return transaction;
      }
   }

   static boolean canTransmit() {
      ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid("CMIME");
      synchronized (serviceRecords) {
         for (int i = serviceRecords.length - 1; i >= 0; i--) {
            if (canTransmit(serviceRecords[i])) {
               return true;
            }
         }

         return false;
      }
   }

   static boolean canTransmit(ServiceRecord serviceRecord) {
      return ServiceRouting.getInstance().isServiceRoutable(serviceRecord.getUid(), -1);
   }

   private void removeTransaction(TransactionRecord transaction) {
      synchronized (this._transactions) {
         int count = this._transactions.length;

         for (int i = count - 1; i >= 0; i--) {
            if (this._transactions[i] == transaction) {
               Arrays.removeAt(this._transactions, i);
               PersistentObject.commit(this._transactions);
               return;
            }
         }
      }
   }

   private void removeTransaction(TransactionRecord transaction, int code) {
      this.removeTransaction(transaction);
      if (this._listener != null) {
         if (code == 0) {
            this._listener.successfulTransmission(transaction._serviceRecord, transaction._payload);
            return;
         }

         this._listener.failedTransmission(transaction._serviceRecord, transaction._payload, code);
      }
   }

   public ReliableTransmissionHelper(ReliableTransmissionListener listener, String typeString, TransmissionService service) {
      this._listener = listener;
      this._typeString = typeString;
      this._service = service;
      this._contextObject = (ContextObject)(new Object());
      this._lastCheckedIndexId = PersistentInteger.getId(-9018585818145369823L, 0);
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-2159541579865757717L);
      synchronized (persistentObject) {
         this._transactions = (MessagePropertiesModel[])persistentObject.getContents();
         if (this._transactions == null) {
            this._transactions = new MessagePropertiesModel[0];
            persistentObject.setContents(this._transactions, 51);
            persistentObject.commit();
         }
      }

      this.resetPendingTransactions();
   }

   private final void resetPendingTransactions() {
      synchronized (this._transactions) {
         for (int i = this._transactions.length - 1; i >= 0; i--) {
            if (this._transactions[i]._status == 0) {
               this._transactions[i]._status = 4;
            }
         }
      }
   }

   private void scanForRetries() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 1
      // 002: invokestatic java/lang/System.currentTimeMillis ()J
      // 005: lstore 2
      // 006: aload 0
      // 007: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._transactions [Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;
      // 00a: dup
      // 00b: astore 4
      // 00d: monitorenter
      // 00e: aload 0
      // 00f: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._lastCheckedIndexId I
      // 012: invokestatic net/rim/vm/PersistentInteger.get (I)I
      // 015: istore 5
      // 017: iload 5
      // 019: aload 0
      // 01a: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._transactions [Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;
      // 01d: arraylength
      // 01e: if_icmplt 02d
      // 021: bipush 0
      // 022: istore 5
      // 024: aload 0
      // 025: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._lastCheckedIndexId I
      // 028: iload 5
      // 02a: invokestatic net/rim/vm/PersistentInteger.set (II)V
      // 02d: iload 5
      // 02f: istore 6
      // 031: iload 5
      // 033: aload 0
      // 034: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._transactions [Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;
      // 037: arraylength
      // 038: if_icmplt 03e
      // 03b: goto 15e
      // 03e: aload 0
      // 03f: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._transactions [Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;
      // 042: iload 5
      // 044: aaload
      // 045: astore 7
      // 047: lload 2
      // 048: aload 7
      // 04a: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._lastChanged J
      // 04d: lsub
      // 04e: lstore 8
      // 050: aload 7
      // 052: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._status I
      // 055: bipush 2
      // 057: if_icmpeq 067
      // 05a: aload 7
      // 05c: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._status I
      // 05f: bipush 4
      // 061: if_icmpeq 067
      // 064: goto 0fe
      // 067: aload 7
      // 069: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._payload Ljava/lang/Object;
      // 06c: dup
      // 06d: instanceof net/rim/device/apps/internal/blackberryemail/otasync/MessageListCommand
      // 070: ifne 077
      // 073: pop
      // 074: goto 096
      // 077: checkcast net/rim/device/apps/internal/blackberryemail/otasync/MessageListCommand
      // 07a: astore 10
      // 07c: aload 10
      // 07e: invokevirtual net/rim/device/apps/internal/blackberryemail/otasync/MessageListCommand.isRestoreMessageList ()Z
      // 081: ifeq 087
      // 084: goto 13a
      // 087: aload 0
      // 088: aload 7
      // 08a: sipush 12931
      // 08d: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper.removeTransaction (Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;I)V
      // 090: iinc 5 -1
      // 093: goto 13a
      // 096: lload 8
      // 098: bipush 0
      // 099: i2l
      // 09a: lcmp
      // 09b: iflt 0b4
      // 09e: lload 8
      // 0a0: ldc_w 900000
      // 0a3: i2l
      // 0a4: aload 7
      // 0a6: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._retryCount I
      // 0a9: i2l
      // 0aa: ldc_w 480000
      // 0ad: i2l
      // 0ae: lmul
      // 0af: ladd
      // 0b0: lcmp
      // 0b1: ifle 13a
      // 0b4: aload 7
      // 0b6: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._retryCount I
      // 0b9: i2l
      // 0ba: bipush 5
      // 0bc: i2l
      // 0bd: lcmp
      // 0be: ifle 0d0
      // 0c1: aload 0
      // 0c2: aload 7
      // 0c4: sipush 8321
      // 0c7: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper.removeTransaction (Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;I)V
      // 0ca: iinc 5 -1
      // 0cd: goto 13a
      // 0d0: aload 7
      // 0d2: dup
      // 0d3: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._retryCount I
      // 0d6: bipush 1
      // 0d7: iadd
      // 0d8: putfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._retryCount I
      // 0db: iinc 1 1
      // 0de: ldc_w 1381257817
      // 0e1: bipush 0
      // 0e2: invokestatic net/rim/device/apps/internal/blackberryemail/otasync/OTAFMEvents.logEvent (II)V
      // 0e5: aload 0
      // 0e6: aload 7
      // 0e8: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper.retransmitObject (Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;)V
      // 0eb: goto 0f5
      // 0ee: astore 10
      // 0f0: goto 0f5
      // 0f3: astore 10
      // 0f5: iload 1
      // 0f6: bipush 5
      // 0f8: if_icmple 13a
      // 0fb: goto 15e
      // 0fe: aload 7
      // 100: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._status I
      // 103: bipush 1
      // 104: if_icmpeq 111
      // 107: aload 7
      // 109: getfield net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord._status I
      // 10c: bipush 3
      // 10e: if_icmpne 13a
      // 111: lload 8
      // 113: bipush 0
      // 114: i2l
      // 115: lcmp
      // 116: ifge 126
      // 119: aload 7
      // 11b: invokevirtual net/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord.updateLastChanged ()V
      // 11e: aload 7
      // 120: invokestatic net/rim/device/api/system/PersistentObject.commit (Ljava/lang/Object;)V
      // 123: goto 13a
      // 126: lload 8
      // 128: ldc_w 172800000
      // 12b: i2l
      // 12c: lcmp
      // 12d: ifle 13a
      // 130: aload 0
      // 131: aload 7
      // 133: bipush 0
      // 134: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper.removeTransaction (Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;I)V
      // 137: iinc 5 -1
      // 13a: iinc 5 1
      // 13d: iload 5
      // 13f: aload 0
      // 140: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._transactions [Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;
      // 143: arraylength
      // 144: if_icmplt 14a
      // 147: bipush 0
      // 148: istore 5
      // 14a: iload 5
      // 14c: iload 6
      // 14e: if_icmpeq 15e
      // 151: iload 6
      // 153: aload 0
      // 154: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._transactions [Lnet/rim/device/apps/internal/blackberryemail/otasync/TransactionRecord;
      // 157: arraylength
      // 158: if_icmpge 15e
      // 15b: goto 031
      // 15e: aload 0
      // 15f: getfield net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper._lastCheckedIndexId I
      // 162: iload 5
      // 164: invokestatic net/rim/vm/PersistentInteger.set (II)V
      // 167: aload 4
      // 169: monitorexit
      // 16a: return
      // 16b: astore 11
      // 16d: aload 4
      // 16f: monitorexit
      // 170: aload 11
      // 172: athrow
      // try (107 -> 113): 114 null
      // try (107 -> 113): 116 null
      // try (9 -> 172): 173 null
      // try (173 -> 176): 173 null
   }

   private void retransmitObject(TransactionRecord transaction) {
      if (canTransmit(transaction._serviceRecord)) {
         transaction._status = 0;
         transaction.updateLastChanged();
         PersistentObject.commit(transaction);
         this.transmitTransaction(transaction);
      }
   }
}
