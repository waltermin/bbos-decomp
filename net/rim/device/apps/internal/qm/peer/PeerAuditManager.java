package net.rim.device.apps.internal.qm.peer;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.proxy.Proxy;

public final class PeerAuditManager implements Runnable, GlobalEventListener {
   RIMModel _reportRecipient;
   String _reportUID;
   long _reportInterval;
   long _reportMaxInterval;
   IntHashtable _persistentData;
   private Vector _messages = (Vector)(new Object());
   private Hashtable _lookup = (Hashtable)(new Object());
   private Hashtable _reverseLookup = (Hashtable)(new Object());
   private int _invokeLaterId = -1;
   private StringBuffer _reportSubject = (StringBuffer)(new Object("BlackBerry Messenger Usage Report "));
   private Vector _reports;
   private StringBuffer _reportBody;
   private Vector _attachments;
   private long _lastTimeSent;
   private long _lastRecordLoggingDate;
   private PeerAuditManager$LogQueueObject[] _lockQueue;
   private Object _reportBodyEncoding;
   private boolean _attemptAtSend;
   public static final long PEER_AUDIT_MANAGER_GUID = 2666260834813870573L;
   public static final long PEER_AUDITOR_TEST = -1207069945313303284L;
   private static PeerAuditManager _instance;
   public static final int IT_POLICY_GROUP = 149;
   public static final int AUDIT_EMAIL_ADDRESS = 16;
   public static final int AUDIT_UID = 17;
   public static final int AUDIT_REPORT_INTERVAL = 18;
   public static final int AUDIT_MAX_REPORT_INTERVAL = 19;
   private static final int DEFAULT_REPORT_INTERVAL = 24;
   private static final int TYPE_REPORTS = 0;
   private static final int TYPE_RECENT_RECORDS = 1;
   private static final int TYPE_LAST_TIME_SENT = 2;
   private static final int TYPE_LAST_RECORD_LOGGING_DATE = 3;
   private static final int TYPE_ATTACHMENTS = 4;
   private static final int TYPE_SINGLE_REPORT = 0;
   private static final int TYPE_SUBJECT = 1;
   private static final int TYPE_BODY = 2;
   private static final int REPORT_MAX_SIZE = 30000;
   private static int _currentReportSize;
   private static StringBuffer _timeAsStringBuffer = (StringBuffer)(new Object());
   private static final String USAGE_REPORT_SUBJECT = "BlackBerry Messenger Usage Report ";
   private static SimpleDateFormat _dateFormat = (SimpleDateFormat)(new Object(48));
   private static SimpleDateFormat _timeFormat = (SimpleDateFormat)(new Object(6));
   private static Calendar _cal = Calendar.getInstance();
   private static CalendarExtensions _calEx = (CalendarExtensions)_cal;
   private static final int TYPE_LOG_MSG = 1;
   private static final int TYPE_LOG_OBJ = 2;
   private static final int TYPE_LOG_DSP = 3;
   private static final int TYPE_LOG_STS = 4;
   private static final String OUTGOING_DISPLAY_NAME_PREFIX = "\t<\t*\tDISPLAY_NAME:";
   private static final String INCOMING_DISPLAY_NAME_PREFIX = "\tDISPLAY_NAME:";
   private static final String OUTGOING_STATUS_PREFIX = "\t<\t*\tSTATUS:";
   private static final String INCOMING_STATUS_PREFIX = "\tSTATUS:";

   final void deserialize(byte[] param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 2
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: bipush 0
      // 01: istore 3
      // 02: new java/lang/Object
      // 05: dup
      // 06: aload 1
      // 07: bipush 0
      // 08: aload 1
      // 09: arraylength
      // 0a: iload 2
      // 0b: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 0e: astore 4
      // 10: aload 4
      // 12: invokestatic net/rim/device/api/synchronization/ConverterUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 15: istore 5
      // 17: goto 1f
      // 1a: astore 6
      // 1c: goto 5e
      // 1f: iload 5
      // 21: lookupswitch 50 1 0 19
      // 34: aload 0
      // 35: aload 4
      // 37: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 3a: iload 2
      // 3b: invokespecial net/rim/device/apps/internal/qm/peer/PeerAuditManager.deserializeReport ([BZ)Lnet/rim/device/api/util/IntHashtable;
      // 3e: astore 6
      // 40: aload 6
      // 42: ifnull 10
      // 45: aload 0
      // 46: getfield net/rim/device/apps/internal/qm/peer/PeerAuditManager._reports Ljava/util/Vector;
      // 49: aload 6
      // 4b: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 4e: bipush 1
      // 4f: istore 3
      // 50: goto 10
      // 53: aload 4
      // 55: invokestatic net/rim/device/api/synchronization/ConverterUtilities.skipField (Lnet/rim/device/api/util/DataBuffer;)V
      // 58: goto 10
      // 5b: astore 5
      // 5d: return
      // 5e: iload 3
      // 5f: ifeq 66
      // 62: aload 0
      // 63: invokespecial net/rim/device/apps/internal/qm/peer/PeerAuditManager.commit ()V
      // 66: return
      // try (11 -> 14): 15 null
      // try (11 -> 37): 37 null
   }

   final void logOutgoingDisplayName(String was, String is) {
      if (this._reportRecipient != null) {
         synchronized (this._reportBody) {
            StringBuffer time = this.getTimeAsStringBuffer(System.currentTimeMillis());
            int size = (18 + was.length() + 3 + is.length() + time.length()) * 2;
            this.verifySize(size);
            this._reportBody.append(time);
            this._reportBody.append("\t<\t*\tDISPLAY_NAME:");
            this._reportBody.append(was);
            this._reportBody.append(" - ");
            this._reportBody.append(is);
            this.commitRecord();
            _currentReportSize += size;
         }
      }
   }

   final void logIncomingDisplayName(PeerContact contact, String was, String is) {
      if (this._reportRecipient != null) {
         if (PeerApplication.isDeviceLocked()) {
            PeerAuditManager$LogQueueObject log = new PeerAuditManager$LogQueueObject();
            log._type = 3;
            log._objects = new Object[]{contact, encode(was), encode(is)};
            this.addObjectToQueue(log);
         } else {
            synchronized (this._reportBody) {
               String oci = contact.getOriginalContactInfo();
               StringBuffer time = this.getTimeAsStringBuffer(System.currentTimeMillis());
               int size = (3 + oci.length() + 14 + was.length() + 3 + is.length() + time.length()) * 2;
               this.verifySize(size);
               this._reportBody.append(time);
               this._reportBody.append("\t>\t");
               this._reportBody.append(oci);
               this._reportBody.append("\tDISPLAY_NAME:");
               this._reportBody.append(was);
               this._reportBody.append(" - ");
               this._reportBody.append(is);
               this.commitRecord();
               _currentReportSize += size;
            }
         }
      }
   }

   final void logOutgoingStatus(String status, String message) {
      if (this._reportRecipient != null) {
         synchronized (this._reportBody) {
            StringBuffer time = this.getTimeAsStringBuffer(System.currentTimeMillis());
            int size = 12 + status.length() + time.length();
            if (message != null) {
               size += message.length() + 1;
            }

            size *= 2;
            this.verifySize(size);
            this._reportBody.append(time);
            this._reportBody.append("\t<\t*\tSTATUS:");
            this._reportBody.append(status);
            if (message != null) {
               this._reportBody.append(" ");
               this._reportBody.append(message);
            }

            this.commitRecord();
            _currentReportSize += size;
         }
      }
   }

   final void logIncomingStatus(PeerContact contact, String status, String message) {
      if (this._reportRecipient != null) {
         if (PeerApplication.isDeviceLocked()) {
            PeerAuditManager$LogQueueObject log = new PeerAuditManager$LogQueueObject();
            log._type = 4;
            log._objects = new Object[]{contact, encode(status), encode(message)};
            this.addObjectToQueue(log);
         } else {
            synchronized (this._reportBody) {
               StringBuffer time = this.getTimeAsStringBuffer(System.currentTimeMillis());
               String ci = contact == null ? "null" : contact.getOriginalContactInfo();
               int size = 3 + 8 + status.length() + ci.length() + time.length();
               if (message != null) {
                  size += message.length() + 1;
               }

               size *= 2;
               this.verifySize(size);
               this._reportBody.append(time);
               this._reportBody.append("\t>\t");
               this._reportBody.append(ci);
               this._reportBody.append("\tSTATUS:");
               this._reportBody.append(status);
               if (message != null) {
                  this._reportBody.append(" ");
                  this._reportBody.append(message);
               }

               this.commitRecord();
               _currentReportSize += size;
            }
         }
      }
   }

   final void logIncomingMessage(PeerContact contact, Vector contacts, String msgBody) {
      if (this._reportRecipient != null) {
         if (PeerApplication.isDeviceLocked()) {
            PeerAuditManager$LogQueueObject log = new PeerAuditManager$LogQueueObject();
            log._type = 1;
            log._objects = new Object[]{contact, contacts, encode(msgBody)};
            this.addObjectToQueue(log);
         } else {
            synchronized (this._reportBody) {
               StringBuffer time = this.getTimeAsStringBuffer(System.currentTimeMillis());
               String ci = contact == null ? "null" : contact.getOriginalContactInfo();
               int size = 3 + ci.length() + 1 + msgBody.length() + time.length();
               size += this.getContactsSize(contacts, contact);
               size *= 2;
               this.verifySize(size);
               this._reportBody.append(time);
               this._reportBody.append("\t>\t");
               this._reportBody.append(ci);
               int count = contacts.size();
               if (count > 1) {
                  this._reportBody.append("(");
                  this.appendContacts(contacts, contact);
                  this._reportBody.append(")");
               }

               this._reportBody.append("\t");
               this._reportBody.append(msgBody);
               this.commitRecord();
               _currentReportSize += size;
            }
         }
      }
   }

   final void logOutgoingMessage(Vector contacts, String msgBody) {
      if (this._reportRecipient != null) {
         synchronized (this._reportBody) {
            StringBuffer time = this.getTimeAsStringBuffer(System.currentTimeMillis());
            int size = 4 + msgBody.length() + time.length();
            size += this.getContactsSize(contacts, null);
            size *= 2;
            this.verifySize(size);
            this._reportBody.append(time);
            this._reportBody.append("\t<\t");
            this.appendContacts(contacts, null);
            this._reportBody.append("\t");
            this._reportBody.append(msgBody);
            this.commitRecord();
            _currentReportSize += size;
         }
      }
   }

   final void logIncomingObject(PeerContact contact, FileMessage file) {
      if (this._reportRecipient != null) {
         if (PeerApplication.isDeviceLocked()) {
            PeerAuditManager$LogQueueObject log = new PeerAuditManager$LogQueueObject();
            log._type = 2;
            log._objects = new Object[]{contact, file};
            this.addObjectToQueue(log);
         } else {
            synchronized (this._reportBody) {
               StringBuffer time = this.getTimeAsStringBuffer(System.currentTimeMillis());
               String ci = contact == null ? "null" : contact.getOriginalContactInfo();
               String filename = file.getFilename();
               int size = 3 + ci.length() + 3 + filename.length() + 2 + time.length();
               size *= 2;
               this.verifySize(size);
               this._reportBody.append(time);
               this._reportBody.append("\t>\t");
               this._reportBody.append(ci);
               this._reportBody.append("\t<<");
               this._reportBody.append(file.getFilename());
               this._reportBody.append(">>");
               int filesize = file.getSize();
               if (filesize <= 30000) {
                  size += filesize;
                  this.verifySize(size);
                  this._attachments.addElement(file);
                  this._persistentData.put(4, this._attachments);
               }

               this.commitRecord();
               _currentReportSize += size;
            }
         }
      }
   }

   final void logOutgoingObject(PeerContact contact, String filename, Object file) {
      if (this._reportRecipient != null) {
         synchronized (this._reportBody) {
            StringBuffer time = this.getTimeAsStringBuffer(System.currentTimeMillis());
            String ci = contact == null ? "null" : contact.getOriginalContactInfo();
            int size = 3 + ci.length() + 3 + filename.length() + 2 + time.length();
            size *= 2;
            this.verifySize(size);
            this._reportBody.append(time);
            this._reportBody.append("\t<\t");
            this._reportBody.append(ci);
            this._reportBody.append("\t<<");
            this._reportBody.append(filename);
            this._reportBody.append(">>");
            int filesize = 0;
            if (!(file instanceof FileMessage)) {
               if (file instanceof byte[]) {
                  filesize = ((byte[])file).length;
               }
            } else {
               filesize = ((FileMessage)file).getSize();
            }

            if (filesize != 0 && filesize <= 30000) {
               size += filesize;
               this.verifySize(size);
               this._attachments.addElement(file);
               this._persistentData.put(4, this._attachments);
            }

            this.commitRecord();
            _currentReportSize += size;
         }
      }
   }

   final void commitRecord() {
      this._reportBody.append('\n');
      this._lastRecordLoggingDate = System.currentTimeMillis();
      Object encoding = PersistentContent.encode(this._reportBody.toString(), true, true);
      this._persistentData.put(1, encoding);
      this.commit();
   }

   final byte[] serialize(boolean bigEndian) {
      this.compileNewReport();
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));
      int count = this._reports.size();

      for (int index = 0; index < count; index++) {
         byte[] b = this.serializeReport((IntHashtable)this._reports.elementAt(index), bigEndian);
         if (b != null) {
            ConverterUtilities.writeByteArray(buffer, 0, b);
         }
      }

      return buffer.toArray();
   }

   public final boolean isSuccessfullySent(EmailMessageModel msg) {
      switch (msg.getStatus()) {
         case 2097151:
         case 4194303:
         case 8388607:
         case 33554431:
            return true;
         default:
            return false;
      }
   }

   public final void deviceUnlocked() {
      if (this._reportBodyEncoding != null) {
         String decrypt = PersistentContent.decodeString(this._reportBodyEncoding);
         this._reportBody = (StringBuffer)(new Object(decrypt));
         String var5 = null;
         int size = this._lockQueue != null ? this._lockQueue.length : 0;

         for (int i = 0; i < size; i++) {
            PeerAuditManager$LogQueueObject o = this._lockQueue[i];
            switch (o._type) {
               case 0:
                  break;
               case 1:
               default:
                  this.logIncomingMessage((PeerContact)o._objects[0], (Vector)o._objects[1], decode(o._objects[2]));
                  break;
               case 2:
                  this.logIncomingObject((PeerContact)o._objects[0], (FileMessage)o._objects[1]);
                  break;
               case 3:
                  this.logIncomingDisplayName((PeerContact)o._objects[0], decode(o._objects[1]), decode(o._objects[2]));
                  break;
               case 4:
                  this.logIncomingStatus((PeerContact)o._objects[0], decode(o._objects[1]), decode(o._objects[2]));
            }
         }

         this._lockQueue = null;
         if (this._attemptAtSend) {
            this._attemptAtSend = false;
            Proxy.getInstance().invokeLater(this);
         }
      }
   }

   public final void deviceLocked() {
      this._lockQueue = new PeerAuditManager$LogQueueObject[0];
      if (this._reportBody != null) {
         this._reportBodyEncoding = PersistentContent.encode(this._reportBody.toString(), true, true);
         this._reportBody = (StringBuffer)(new Object());
      }
   }

   @Override
   public final void run() {
      if (PeerApplication.isDeviceLocked()) {
         this._attemptAtSend = true;
      } else {
         synchronized (this) {
            this.compileNewReport();
            this.processReports();
            this.processMessages();
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this.loadAuditITPolicy();
      }
   }

   private final int getContactsSize(Vector contacts, PeerContact except) {
      int size = 0;

      for (int index = 0; index < contacts.size(); index++) {
         PeerContact current = (PeerContact)contacts.elementAt(index);
         if (current != except) {
            String curInfo = current.getOriginalContactInfo();
            size += curInfo.length();
            if (index > 0) {
               size++;
            }
         }
      }

      return size;
   }

   private final String formatDate(long time) {
      _calEx.setTimeLong(time);
      return _dateFormat.format(_cal);
   }

   private final String formatTime(long time) {
      _calEx.setTimeLong(time);
      return _timeFormat.format(_cal);
   }

   private final StringBuffer getTimeAsStringBuffer(long time) {
      _timeAsStringBuffer.setLength(0);
      String recordDate = this.formatDate(time);
      String lastRecordLoggingDate = this.formatDate(this._lastRecordLoggingDate);
      if (this._lastRecordLoggingDate == -1 || !recordDate.equals(lastRecordLoggingDate)) {
         _timeAsStringBuffer.append(recordDate);
         _timeAsStringBuffer.append('\n');
      }

      _timeAsStringBuffer.append(this.formatTime(time));
      return _timeAsStringBuffer;
   }

   private final void clearTransientData() {
      this._messages.removeAllElements();
      this._lookup.clear();
      this._reverseLookup.clear();
   }

   private final void verifySize(int size) {
      if (size + _currentReportSize > 30000) {
         this.run();
      }
   }

   private final void compileNewReport() {
      long currentTime = System.currentTimeMillis();
      if (this._reportBody.length() != 0 || this._reportMaxInterval < currentTime - this._lastTimeSent) {
         this._reportSubject.append(this.formatDate(this._lastTimeSent));
         this._reportSubject.append(' ');
         this._reportSubject.append(this.formatTime(this._lastTimeSent));
         this._reportSubject.append(" - ");
         this._reportSubject.append(this.formatDate(currentTime));
         this._reportSubject.append(' ');
         this._reportSubject.append(this.formatTime(currentTime));
         if (this._reportBody.length() == 0) {
            this._reportSubject.append("  - no records logged");
         }

         this._lastTimeSent = currentTime;
         IntHashtable recentReport = (IntHashtable)(new Object());
         recentReport.put(1, this._reportSubject.toString());
         recentReport.put(2, this._reportBody.toString());
         recentReport.put(4, this._attachments);
         this._reports.addElement(recentReport);
         this._reportSubject.setLength(34);
         this._reportBody.setLength(0);
         this._attachments = (Vector)(new Object());
         this._persistentData.put(1, "");
         this.commit();
         _currentReportSize = 0;
      }
   }

   private final void processReports() {
      if (this._reportRecipient != null) {
         int count = this._reports.size();

         for (int index = 0; index < count; index++) {
            IntHashtable report = (IntHashtable)this._reports.elementAt(index);
            if (this._lookup.get(report) == null) {
               EmailMessageModel msg = this.buildMessage(report);
               RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
               if (service != null) {
                  ServiceRecord sr = service.getOutgoingServiceRecord();
                  if (sr != null) {
                     EmailSendUtility.sendMessage(msg, sr, new Object());
                     EmailHierarchy.removeMessage(msg, msg.getFolderId());
                     this._messages.addElement(msg);
                     this._lookup.put(report, msg);
                     this._reverseLookup.put(msg, report);
                  }
               }
            }
         }
      }
   }

   private final void processMessages() {
      boolean doCommit = false;
      int count = this._messages.size();

      for (int index = count - 1; index >= 0; index--) {
         EmailMessageModel msg = (EmailMessageModel)this._messages.elementAt(index);
         IntHashtable report = (IntHashtable)this._reverseLookup.get(msg);
         if (this.isSuccessfullySent(msg)) {
            this._reports.removeElement(report);
            this._lookup.remove(report);
            this._messages.removeElement(msg);
            this._reverseLookup.remove(msg);
            doCommit = true;
         }
      }

      if (doCommit) {
         this.commit();
      }
   }

   private final EmailMessageModel buildMessage(IntHashtable report) {
      EmailMessageModel msg = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
      EmailBuilderApi.addRecipient(msg, 0, this._reportRecipient);
      EmailBuilderApi.addSubjectLine(msg, (String)report.get(1));
      EmailBuilderApi.addMessageBody(msg, (String)report.get(2));
      msg.setType((byte)32);
      Vector attachments = (Vector)report.get(4);

      for (int i = 0; i < attachments.size(); i++) {
         msg.add(attachments.elementAt(i));
      }

      return msg;
   }

   public static final PeerAuditManager getInstance() {
      if (_instance == null) {
         _instance = (PeerAuditManager)ApplicationRegistry.getApplicationRegistry().get(2666260834813870573L);
         if (_instance == null) {
            _instance = new PeerAuditManager();
            ApplicationRegistry.getApplicationRegistry().put(2666260834813870573L, _instance);
         }
      }

      return _instance;
   }

   private final void commit() {
      PersistentObject.commit(this._persistentData);
   }

   private final void addObjectToQueue(Object o) {
      Arrays.add(this._lockQueue, o);
   }

   private static final Object encode(String s) {
      return PersistentContent.encode(s, true, true);
   }

   private static final String decode(Object o) {
      try {
         return PersistentContent.decodeString(o);
      } finally {
         ;
      }
   }

   private final void loadAuditITPolicy() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      LongHashtable auditorTestData = (LongHashtable)ar.get(-1207069945313303284L);
      if (auditorTestData != null) {
         Object address = auditorTestData.get(253);
         if (address instanceof Object) {
            this._reportRecipient = this.getReportRecipient((String)address);
            this._reportInterval = 86400000;
            this._reportMaxInterval = 604800000;
         }
      } else {
         this._reportRecipient = this.getReportRecipient(ITPolicy.getString(149, 16));
         this._reportUID = ITPolicy.getString(149, 17);
         this._reportInterval = ITPolicy.getInteger(149, 18, 24) * 60 * 60 * 1000;
         this._reportMaxInterval = ITPolicy.getInteger(149, 19, 168) * 60 * 60 * 1000;
      }

      this.clearTransientData();
      if (this._invokeLaterId != -1) {
         Proxy.getInstance().cancelInvokeLater(this._invokeLaterId);
      }

      this._invokeLaterId = Proxy.getInstance().invokeLater(this, this._reportInterval, true);
   }

   private final RIMModel getReportRecipient(String address) {
      RIMModel recipient = null;
      if (address != null && address.length() > 0) {
         String[] names = new Object[2];
         names[0] = address;
         names[1] = address;
         ContextObject context = (ContextObject)(new Object());
         ContextObject.put(context, 251, names);
         recipient = (RIMModel)FactoryUtil.createInstance(-2985347935260258684L, context);
      }

      return recipient;
   }

   private final void appendContacts(Vector contacts, PeerContact except) {
      for (int index = contacts.size() - 1; index >= 0; index--) {
         PeerContact contact = (PeerContact)contacts.elementAt(index);
         if (contact != except) {
            this._reportBody.append(contact.getOriginalContactInfo());
            if (index > 0) {
               this._reportBody.append(',');
            }
         }
      }
   }

   private final byte[] serializeReport(IntHashtable report, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));
      if (report != null) {
         ConverterUtilities.writeString(buffer, 1, (String)report.get(1));
         ConverterUtilities.writeString(buffer, 2, PersistentContent.decodeString(report.get(2)));
      }

      return buffer.toArray();
   }

   private PeerAuditManager() {
      _cal.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      this.loadAuditITPolicy();
      Proxy.getInstance().addGlobalEventListener(this);
      this._persistentData = PeerData.getAuditData();
      if (this._persistentData != null && this._reportRecipient != null) {
         this._reports = (Vector)this._persistentData.get(0);
         Object records = this._persistentData.get(1);
         if (records instanceof Object) {
            this._reportBody = (StringBuffer)(new Object((String)records));
         } else {
            this._reportBody = (StringBuffer)(new Object());
            this._reportBody.append(records);
         }

         this._attachments = (Vector)this._persistentData.get(4);
         this._lastTimeSent = this._persistentData.get(2);
         this._lastRecordLoggingDate = this._persistentData.get(3);
         _currentReportSize = this._reportBody.length() * 2;

         for (int index = 0; index < this._attachments.size(); index++) {
            Object att = this._attachments.elementAt(index);
            if (att instanceof FileMessage) {
               _currentReportSize = _currentReportSize + ((FileMessage)att).getSize();
            } else if (att instanceof FileTransferBlob) {
               _currentReportSize = _currentReportSize + ((FileTransferBlob)att).getSize();
            }
         }
      } else {
         this._persistentData = (IntHashtable)(new Object());
         this._reports = (Vector)(new Object());
         this._reportBody = (StringBuffer)(new Object());
         this._attachments = (Vector)(new Object());
         this._lastTimeSent = System.currentTimeMillis();
         this._lastRecordLoggingDate = -1;
         this._persistentData.put(0, this._reports);
         this._persistentData.put(4, this._attachments);
         this._persistentData.put(1, "");
         this._persistentData.put(2, new Object(this._lastTimeSent));
         this._persistentData.put(3, new Object(this._lastRecordLoggingDate));
         PeerData.setAuditData(this._persistentData);
         this.commit();
         _currentReportSize = 0;
      }
   }

   private final IntHashtable deserializeReport(byte[] buf, boolean bigEndian) {
      IntHashtable report = (IntHashtable)(new Object());
      DataBuffer buffer = (DataBuffer)(new Object(buf, 0, buf.length, bigEndian));

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               ;
            }

            switch (type) {
               case 0:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  report.put(1, ConverterUtilities.readString(buffer));
                  break;
               case 2:
                  report.put(2, PersistentContent.decodeString(ConverterUtilities.readString(buffer)));
            }
         }
      } finally {
         ;
      }
   }
}
