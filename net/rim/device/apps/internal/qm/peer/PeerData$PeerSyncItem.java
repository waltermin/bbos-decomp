package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongEnumeration;

public final class PeerData$PeerSyncItem extends OTASyncCapableSyncItem {
   private CollectionListener _callback;
   private static final long PEER_SYNC_ITEM = -2083195621769849627L;
   private static final int TYPE_ENTRY_KEY = 1;
   private static final int TYPE_PEER_CONTACT_LIST_COLLECTION = 2;
   private static final int TYPE_USER_NAME_KEY = 3;
   private static final int TYPE_PASSWORD_KEY = 4;
   private static final int TYPE_PEER_CONTACT_LIST = 6;
   private static final int TYPE_PEER_CONTACT_LIST_COLLECTION_KEY = 7;
   private static final int TYPE_REQUESTS = 8;
   private static final int TYPE_AUDIT_DATA = 9;
   private static final int TYPE_CONTACT_ID_KEY = 10;
   private static final int TYPE_FLAGS_KEY = 11;

   private final void setCallback(CollectionListener callback) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final String getSyncName() {
      return "BlackBerry Messenger";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      int size = PeerData.access$200().size();
      if (size > 0) {
         LongEnumeration keys = PeerData.access$200().keys();

         while (keys.hasMoreElements()) {
            this.processKey(keys.nextElement(), buffer);
         }
      }

      return true;
   }

   private final void processKey(long key, DataBuffer buffer) {
      if (key == 5587026183716775221L) {
         this.onDefaultContactListCollection(key, buffer);
      } else if (key == -7708773143185203648L) {
         ConverterUtilities.writeString(buffer, 3, PeerData.getUserName());
      } else if (key == 1033784522318989540L) {
         ConverterUtilities.writeString(buffer, 4, PeerData.getPasswordKey());
      } else if (key == -1021583542955865241L) {
         ConverterUtilities.writeString(buffer, 10, PeerData.getPin());
      } else if (key == 293351436076650281L) {
         this.onRequestsKey(key, buffer);
      } else if (key == 1166674198523868859L) {
         this.onAuditData(key, buffer);
      } else {
         if (key == -222541206799519196L) {
            ConverterUtilities.writeInt(buffer, 11, PeerData.access$200().get(-222541206799519196L));
         }
      }
   }

   private final void onDefaultContactListCollection(long key, DataBuffer buffer) {
      byte[] b = this.serializePeerContacListCollection(key, buffer.isBigEndian());
      if (b != null) {
         ConverterUtilities.writeByteArray(buffer, 2, b);
      }
   }

   private final byte[] serializePeerContacListCollection(long key, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));
      ConverterUtilities.writeLong(buffer, 7, key);
      Object value = PeerData.access$200().get(key);
      if (!(value instanceof Object)) {
         return null;
      }

      BigVector pclc = (BigVector)value;
      int size = pclc.size();

      for (int i = 0; i < size; i++) {
         ConverterUtilities.writeByteArray(buffer, 6, this.serializePeerContactList((IntHashtable)pclc.elementAt(i), bigEndian));
      }

      return buffer.toArray();
   }

   private final byte[] serializePeerContactList(IntHashtable pcl, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));
      IntEnumeration keys = pcl.keys();

      while (keys.hasMoreElements()) {
         this.processKeySerPeerContactList(keys.nextElement(), buffer, pcl, bigEndian);
      }

      return buffer.toArray();
   }

   private final void processKeySerPeerContactList(int key, DataBuffer buffer, IntHashtable pcl, boolean bigEndian) {
      switch (key) {
         case 1:
            ConverterUtilities.writeString(buffer, 1, PersistentContent.decodeString(pcl.get(1)));
            return;
         case 3:
            this.onPeerContactListHashId(pcl, buffer);
            return;
         case 5:
            this.onPeerContactListContactsId(pcl, buffer, bigEndian);
      }
   }

   private final void onPeerContactListHashId(IntHashtable pcl, DataBuffer buffer) {
      Object obj = pcl.get(3);
      if (obj instanceof Object) {
         ConverterUtilities.writeInt(buffer, 3, obj);
      }
   }

   private final void onPeerContactListContactsId(IntHashtable pcl, DataBuffer buffer, boolean bigEndian) {
      Object obj = pcl.get(5);
      if (obj instanceof Object) {
         Vector pcs = (Vector)obj;
         int size = pcs.size();

         for (int i = 0; i < size; i++) {
            ConverterUtilities.writeByteArray(buffer, 5, this.serializePeerContact((IntHashtable)pcs.elementAt(i), bigEndian));
         }
      }
   }

   private final byte[] serializePeerContact(IntHashtable pc, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));
      IntEnumeration keys = pc.keys();

      while (keys.hasMoreElements()) {
         this.processKeySerPeerContact(keys.nextElement(), buffer, pc, bigEndian);
      }

      return buffer.toArray();
   }

   private final void processKeySerPeerContact(int key, DataBuffer buffer, IntHashtable pc, boolean bigEndian) {
      switch (key) {
         case 1:
         case 2:
         case 3:
         case 5:
         case 6:
         case 7:
         case 9:
         default:
            ConverterUtilities.writeString(buffer, key, PersistentContent.decodeString(pc.get(key)));
            return;
         case 4:
            ConverterUtilities.writeInt(buffer, 4, pc.get(4));
            return;
         case 8:
            ConverterUtilities.writeByteArray(buffer, 8, (byte[])pc.get(8));
         case 0:
      }
   }

   private final void onRequestsKey(long key, DataBuffer buffer) {
      byte[] b = this.serializeRequests(buffer.isBigEndian());
      if (b != null) {
         ConverterUtilities.writeByteArray(buffer, 8, b);
      }
   }

   private final void onAuditData(long key, DataBuffer buffer) {
      byte[] b = PeerAuditManager.getInstance().serialize(buffer.isBigEndian());
      if (b != null) {
         ConverterUtilities.writeByteArray(buffer, 9, b);
      }
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               break;
            }

            this.processType(type, buffer);
         }
      } finally {
         ;
      }

      PeerData.access$300(false);
      this._callback.elementUpdated(null, null, this);
      return true;
   }

   private final void processType(int type, DataBuffer buffer) {
      switch (type) {
         case 1:
         case 5:
         case 6:
         case 7:
            ConverterUtilities.skipField(buffer);
            return;
         case 2:
         default:
            this.onTypePeerContactListCollection(buffer);
            return;
         case 3:
            PeerData.access$200().put(-7708773143185203648L, ConverterUtilities.readString(buffer));
            return;
         case 4:
            PeerData.access$200().put(1033784522318989540L, ConverterUtilities.readString(buffer));
            return;
         case 8:
            this.onTypeRequests(buffer);
            return;
         case 9:
            this.onTypeAuditData(buffer);
            return;
         case 10:
            PeerData.access$200().put(-1021583542955865241L, ConverterUtilities.readString(buffer));
            return;
         case 11:
            this.onTypeFlagsKey(buffer);
      }
   }

   private final void onTypePeerContactListCollection(DataBuffer buffer) {
      byte[] b = ConverterUtilities.readByteArray(buffer);
      BigVector pclc = (BigVector)(new Object());
      this.deserializePeerContactListCollection(b, buffer.isBigEndian(), pclc);
      PeerData.access$200().put(5587026183716775221L, pclc);
   }

   private final void onTypeRequests(DataBuffer buffer) {
      byte[] bytes = ConverterUtilities.readByteArray(buffer);
      Vector requests = this.deseralizeRequests(bytes, buffer.isBigEndian());
      if (requests != null) {
         PeerData.access$200().put(293351436076650281L, requests);
      }
   }

   private final void onTypeAuditData(DataBuffer buffer) {
      byte[] auditData = ConverterUtilities.readByteArray(buffer);
      PeerAuditManager.getInstance().deserialize(auditData, buffer.isBigEndian());
   }

   private final void onTypeFlagsKey(DataBuffer buffer) {
      int flags = ConverterUtilities.readInt(buffer);
      PeerData.access$200().put(-222541206799519196L, new Object(flags));
   }

   private final void deserializePeerContactListCollection(byte[] buf, boolean bigEndian, BigVector pclc) {
      DataBuffer buffer = (DataBuffer)(new Object(buf, 0, buf.length, bigEndian));

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               return;
            }

            this.processTypeDesPeerContactListCollection(type, buffer, pclc, bigEndian);
         }
      } finally {
         return;
      }
   }

   private final void processTypeDesPeerContactListCollection(int type, DataBuffer buffer, BigVector pclc, boolean bigEndian) {
      switch (type) {
         case 5:
            ConverterUtilities.skipField(buffer);
            break;
         case 6:
            byte[] b = ConverterUtilities.readByteArray(buffer);
            IntHashtable pcl = this.deserializePeerContactList(b, bigEndian);
            if (pcl != null) {
               pclc.addElement(pcl);
               return;
            }
            break;
         case 7:
         default:
            long key = ConverterUtilities.readLong(buffer);
            if (key != 5587026183716775221L) {
               System.out.println(((StringBuffer)(new Object("BBM: Wrong DEFAULT_CONTACT_LIST_COLLECTION key - "))).append(Long.toString(key, 16)).toString());
               return;
            }
      }
   }

   private final IntHashtable deserializePeerContactList(byte[] buf, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(buf, 0, buf.length, bigEndian));
      IntHashtable pcl = (IntHashtable)(new Object());
      Vector pcs = (Vector)(new Object());
      pcl.put(5, pcs);

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               ;
            }

            this.processTypeDesPeerContactList(type, buffer, pcl, pcs, bigEndian);
         }
      } finally {
         ;
      }
   }

   private final void processTypeDesPeerContactList(int type, DataBuffer buffer, IntHashtable pcl, Vector pcs, boolean bigEndian) {
      switch (type) {
         case 1:
            pcl.put(1, ConverterUtilities.readString(buffer));
            return;
         case 3:
            pcl.put(3, new Object(ConverterUtilities.readInt(buffer)));
            return;
         case 5:
            byte[] b = ConverterUtilities.readByteArray(buffer);
            IntHashtable pc = this.deserializePeerContact(b, bigEndian);
            if (pc != null) {
               pcs.addElement(pc);
               return;
            }
            break;
         default:
            ConverterUtilities.skipField(buffer);
      }
   }

   private final IntHashtable deserializePeerContact(byte[] buf, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(buf, 0, buf.length, bigEndian));
      IntHashtable pc = (IntHashtable)(new Object());

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               ;
            }

            this.processTypeDesPeerContact(type, buffer, pc, bigEndian);
         }
      } finally {
         ;
      }
   }

   private final void processTypeDesPeerContact(int type, DataBuffer buffer, IntHashtable pc, boolean bigEndian) {
      switch (type) {
         case 0:
            ConverterUtilities.skipField(buffer);
            return;
         case 1:
         case 2:
         case 3:
         case 5:
         case 6:
         case 7:
         case 9:
         default:
            pc.put(type, PersistentContent.encode(ConverterUtilities.readString(buffer), true, true));
            return;
         case 4:
            pc.put(4, new Object(ConverterUtilities.readInt(buffer)));
            return;
         case 8:
            pc.put(8, ConverterUtilities.readByteArray(buffer));
      }
   }

   private final byte[] serializeRequests(boolean bigEndian) {
      Vector requests = (Vector)PeerData.access$200().get(293351436076650281L);
      if (requests != null && requests.size() > 0) {
         DataBuffer buffer = (DataBuffer)(new Object(bigEndian));

         for (int i = 0; i < requests.size(); i++) {
            IntHashtable request = (IntHashtable)requests.elementAt(i);
            int keys = request.get(1);
            switch (keys) {
               case 1:
                  break;
               case 2:
               default:
                  ConverterUtilities.writeByteArray(buffer, 2, this.serializeNewContactRequest(request, bigEndian));
                  break;
               case 3:
                  ConverterUtilities.writeByteArray(buffer, 3, this.serializeWrongPasscodeRequest(request, bigEndian));
            }
         }

         return buffer.toArray();
      } else {
         return null;
      }
   }

   private final Vector deseralizeRequests(byte[] buf, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(buf, 0, buf.length, bigEndian));
      Vector requests = (Vector)(new Object());

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               ;
            }

            this.processTypeDesRequests(type, buffer, requests, bigEndian);
         }
      } finally {
         ;
      }
   }

   private final void processTypeDesRequests(int type, DataBuffer buffer, Vector requests, boolean bigEndian) {
      switch (type) {
         case 1:
            ConverterUtilities.skipField(buffer);
            break;
         case 2:
         default:
            IntHashtable datax = this.deserializeNewContactRequest(ConverterUtilities.readByteArray(buffer), bigEndian);
            if (datax != null) {
               requests.addElement(datax);
               return;
            }
            break;
         case 3:
            IntHashtable data = this.deserializeWrongPasscodeRequest(ConverterUtilities.readByteArray(buffer), bigEndian);
            if (data != null) {
               requests.addElement(data);
               return;
            }
      }
   }

   private final byte[] serializeWrongPasscodeRequest(IntHashtable request, boolean bigEndian) {
      IntEnumeration keys = request.keys();
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));

      while (keys.hasMoreElements()) {
         switch (keys.nextElement()) {
            case 1:
               ConverterUtilities.writeInt(buffer, 1, request.get(1));
               break;
            case 5:
               ConverterUtilities.writeString(buffer, 5, PersistentContent.decodeString(request.get(5)));
               break;
            case 6:
               ConverterUtilities.writeString(buffer, 6, PersistentContent.decodeString(request.get(6)));
         }
      }

      return buffer.getLength() > 0 ? buffer.toArray() : null;
   }

   private final IntHashtable deserializeWrongPasscodeRequest(byte[] buf, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(buf, 0, buf.length, bigEndian));
      IntHashtable data = (IntHashtable)(new Object());

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               ;
            }

            this.processTypeDesWrongPasscodeRequest(type, buffer, data, bigEndian);
         }
      } finally {
         ;
      }
   }

   private final void processTypeDesWrongPasscodeRequest(int type, DataBuffer buffer, IntHashtable data, boolean bigEndian) {
      switch (type) {
         case 1:
            data.put(1, new Object(ConverterUtilities.readInt(buffer)));
            return;
         case 5:
            data.put(5, ConverterUtilities.readByteArray(buffer));
            return;
         case 6:
            data.put(6, ConverterUtilities.readByteArray(buffer));
            return;
         default:
            ConverterUtilities.skipField(buffer);
      }
   }

   private final byte[] serializeNewContactRequest(IntHashtable request, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));
      IntEnumeration keys = request.keys();

      while (keys.hasMoreElements()) {
         switch (keys.nextElement()) {
            case 1:
               ConverterUtilities.writeInt(buffer, 1, request.get(1));
               break;
            case 5:
               ConverterUtilities.writeString(buffer, 5, PersistentContent.decodeString(request.get(5)));
               break;
            case 6:
               ConverterUtilities.writeString(buffer, 6, PersistentContent.decodeString(request.get(6)));
               break;
            case 7:
               byte[] b = this.serializeEmailInvitation((IntHashtable)request.get(7), bigEndian);
               if (b != null) {
                  ConverterUtilities.writeByteArray(buffer, 7, b);
               }
         }
      }

      return buffer.getLength() > 0 ? buffer.toArray() : null;
   }

   private final IntHashtable deserializeNewContactRequest(byte[] buf, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(buf, 0, buf.length, bigEndian));
      IntHashtable data = (IntHashtable)(new Object());

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               ;
            }

            this.processTypeDesNewContactRequest(type, buffer, data, bigEndian);
         }
      } finally {
         ;
      }
   }

   private final void processTypeDesNewContactRequest(int type, DataBuffer buffer, IntHashtable data, boolean bigEndian) {
      switch (type) {
         case 1:
            data.put(1, new Object(ConverterUtilities.readInt(buffer)));
            return;
         case 5:
            data.put(5, ConverterUtilities.readByteArray(buffer));
            return;
         case 6:
            data.put(6, ConverterUtilities.readByteArray(buffer));
            return;
         case 7:
            IntHashtable ei = this.deserializeEmailInvitation(ConverterUtilities.readByteArray(buffer), bigEndian);
            if (ei != null) {
               data.put(7, ei);
               return;
            }
            break;
         default:
            ConverterUtilities.skipField(buffer);
      }
   }

   private final byte[] serializeEmailInvitation(IntHashtable invite, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(bigEndian));
      IntEnumeration keys = invite.keys();

      while (keys.hasMoreElements()) {
         this.processKeySerEmailInvitation(keys.nextElement(), buffer, invite, bigEndian);
      }

      return buffer.getLength() > 0 ? buffer.toArray() : null;
   }

   private final void processKeySerEmailInvitation(int key, DataBuffer buffer, IntHashtable invite, boolean bigEndian) {
      switch (key) {
         case 1:
            int stage = invite.get(1);
            ConverterUtilities.writeInt(buffer, 1, stage);
            return;
         case 2:
            int cookie = invite.get(2);
            ConverterUtilities.writeInt(buffer, 2, cookie);
            return;
         case 3:
            int flag = invite.get(3);
            ConverterUtilities.writeInt(buffer, 3, flag);
            return;
         case 4:
            ConverterUtilities.writeByteArray(buffer, 4, (byte[])invite.get(4));
            return;
         case 5:
            ConverterUtilities.writeByteArray(buffer, 5, (byte[])invite.get(5));
            return;
         case 6:
            ConverterUtilities.writeByteArray(buffer, 6, (byte[])invite.get(6));
            return;
         case 7:
            ConverterUtilities.writeByteArray(buffer, 7, (byte[])invite.get(7));
            return;
         case 8:
            ConverterUtilities.writeString(buffer, 8, PersistentContent.decodeString(invite.get(8)));
            return;
         case 39:
            int inboundflag = invite.get(39);
            ConverterUtilities.writeInt(buffer, 39, inboundflag);
            return;
         case 40:
            ConverterUtilities.writeString(buffer, 40, PersistentContent.decodeString(invite.get(40)));
            return;
         case 41:
            int isPin = invite.get(41);
            ConverterUtilities.writeInt(buffer, 41, isPin);
      }
   }

   private final IntHashtable deserializeEmailInvitation(byte[] buf, boolean bigEndian) {
      DataBuffer buffer = (DataBuffer)(new Object(buf, 0, buf.length, bigEndian));
      IntHashtable data = (IntHashtable)(new Object());

      try {
         while (true) {
            int type;
            try {
               type = ConverterUtilities.getType(buffer);
            } finally {
               ;
            }

            this.processTypeDesEmailInvitation(type, buffer, data, bigEndian);
         }
      } finally {
         ;
      }
   }

   private final void processTypeDesEmailInvitation(int type, DataBuffer buffer, IntHashtable data, boolean bigEndian) {
      switch (type) {
         case 1:
            data.put(1, new Object(ConverterUtilities.readInt(buffer)));
            return;
         case 2:
            data.put(2, new Object(ConverterUtilities.readInt(buffer)));
            return;
         case 3:
            data.put(3, new Object(ConverterUtilities.readInt(buffer)));
            return;
         case 4:
            data.put(4, ConverterUtilities.readByteArray(buffer));
            return;
         case 5:
            data.put(5, ConverterUtilities.readByteArray(buffer));
            return;
         case 6:
            data.put(6, ConverterUtilities.readByteArray(buffer));
            return;
         case 7:
            data.put(7, ConverterUtilities.readByteArray(buffer));
            return;
         case 8:
            data.put(8, ConverterUtilities.readByteArray(buffer));
            return;
         case 39:
            data.put(39, new Object(ConverterUtilities.readInt(buffer)));
            return;
         case 40:
            data.put(40, ConverterUtilities.readByteArray(buffer));
            return;
         case 41:
            data.put(41, new Object(ConverterUtilities.readInt(buffer)));
            return;
         default:
            ConverterUtilities.skipField(buffer);
      }
   }

   @Override
   public final boolean removeAllSyncObjects() {
      synchronized (PeerData.access$200()) {
         IntHashtable auditData = PeerData.getAuditData();
         PeerData.access$200().clear();
         if (auditData != null) {
            PeerData.setAuditData(auditData);
         }

         PeerData.access$300(false);
      }

      this._callback.elementUpdated(null, null, this);
      return true;
   }

   static final void access$000(PeerData$PeerSyncItem x0, CollectionListener x1) {
      x0.setCallback(x1);
   }
}
