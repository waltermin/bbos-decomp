package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.component.PINAddressEditField;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringTokenizer;

final class SimpleMsgDataBlob extends PeerDataBlob {
   private int _msgId;
   private String _msg;
   private String _conversationId;
   private String _from;
   private Vector _contacts = (Vector)(new Object(1));
   private Vector _contactNames = (Vector)(new Object(1));
   private String _contactsStr;
   private boolean _system;
   private static final int MSG_ID = 1;
   private static final int MSG = 2;
   private static final int CONV_ID = 3;
   private static final int FROM = 4;
   private static final int CONTACT_STR = 5;
   private static final int SYSTEM = 8;
   private static final StringBuffer _sb = (StringBuffer)(new Object());

   public SimpleMsgDataBlob() {
      this(0, null);
   }

   public SimpleMsgDataBlob(int id, String msg) {
      this._msgId = id;
      this._msg = msg;
   }

   public SimpleMsgDataBlob(int id, String msg, String convId, Vector contacts, Vector contactNames, boolean system) {
      this._msgId = id;
      this._msg = msg;
      this._conversationId = convId;
      this._from = PeerApplication.getSession().getDisplayName();
      this._contacts = contacts;
      this._contactNames = contactNames;
      this._system = system;
      this.formatContacts();
   }

   public final String getMessage() {
      return this._msg;
   }

   @Override
   final int getId() {
      return this._msgId;
   }

   public final String getConversationId() {
      return this._conversationId;
   }

   public final boolean isSystemMessage() {
      return this._system;
   }

   private final void unpackContacts() {
      this._contacts.removeAllElements();
      this._contactNames.removeAllElements();
      StringTokenizer st = (StringTokenizer)(new Object(this._contactsStr, ','));

      while (st.hasMoreTokens()) {
         String s = st.nextToken();
         int index = s.indexOf(58);
         if (index != -1) {
            String id = s.substring(0, index);
            if (PINAddressEditField.validateText(id)) {
               String name = s.substring(index + 1);
               this._contacts.addElement(id);
               this._contactNames.addElement(name);
            }
         }
      }
   }

   private final void formatContacts() {
      _sb.setLength(0);

      for (int i = this._contacts.size() - 1; i >= 0; i--) {
         _sb.append((String)this._contacts.elementAt(i));
         _sb.append(':');
         _sb.append((String)this._contactNames.elementAt(i));
         _sb.append(',');
      }

      this._contactsStr = _sb.toString();
   }

   public final Vector getContacts() {
      return this._contacts;
   }

   public final Vector getContactNames() {
      return this._contactNames;
   }

   @Override
   public final int getType() {
      return 1;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      ConverterUtilities.writeInt(db2, 1, this._msgId);
      this.addStringToDataBuffer(db2, 3, this._conversationId);
      this.addStringToDataBuffer(db2, 4, this._from);
      this.addStringToDataBuffer(db2, 5, this._contactsStr);
      this.addStringToDataBuffer(db2, 2, this._msg);
      ConverterUtilities.convertInt(db2, 8, this._system ? 1 : 0, 1);
      ConverterUtilities.writeEmptyField(db2, 0);
      db2.trim();
      this.appendDataBuffer(db, db2);
   }

   @Override
   public final void unPickle(DataBuffer db, int length) {
      int type;
      while (db.available() > 2 && (type = ConverterUtilities.getType(db, true)) != 0) {
         switch (type) {
            case 0:
            case 6:
            case 7:
               ConverterUtilities.skipField(db);
               break;
            case 1:
            default:
               this._msgId = ConverterUtilities.readInt(db);
               break;
            case 2:
               this._msg = ConverterUtilities.readString(db);
               break;
            case 3:
               this._conversationId = ConverterUtilities.readString(db);
               break;
            case 4:
               this._from = ConverterUtilities.readString(db);
               break;
            case 5:
               this._contactsStr = ConverterUtilities.readString(db);
               break;
            case 8:
               this._system = ConverterUtilities.readInt(db) != 0;
         }
      }

      ConverterUtilities.skipField(db);
      this.unpackContacts();
   }
}
