package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.component.PINAddressEditField;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringTokenizer;

final class ConvInviteBlob extends PeerDataBlob {
   private String _convId;
   private Vector _contacts;
   private Vector _contactNames;
   private String _contactsStr;
   private static final int CONV_ID = 1;
   private static final int CONTACT_STR = 2;

   public ConvInviteBlob() {
   }

   public ConvInviteBlob(String convId, Vector contacts, Vector contactNames) {
      this._convId = convId;
      this._contacts = contacts;
      this._contactNames = contactNames;
      if (convId != null && contacts != null) {
         this.formatContacts();
      }
   }

   private final void formatContacts() {
      StringBuffer sb = (StringBuffer)(new Object());

      for (int i = this._contacts.size() - 1; i >= 0; i--) {
         sb.append((String)this._contacts.elementAt(i));
         sb.append(':');
         sb.append((String)this._contactNames.elementAt(i));
         sb.append(',');
      }

      this._contactsStr = sb.toString();
   }

   private final void unpackContacts() {
      this._contacts = (Vector)(new Object(1));
      this._contactNames = (Vector)(new Object(1));
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

   public final Vector getContacts() {
      return this._contacts;
   }

   public final Vector getContactNames() {
      return this._contactNames;
   }

   public final String getConversationId() {
      return this._convId;
   }

   @Override
   public final int getType() {
      return 9;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      this.addStringToDataBuffer(db2, 1, this._convId);
      this.addStringToDataBuffer(db2, 2, this._contactsStr);
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
               ConverterUtilities.skipField(db);
               break;
            case 1:
            default:
               this._convId = ConverterUtilities.readString(db);
               break;
            case 2:
               this._contactsStr = ConverterUtilities.readString(db);
         }
      }

      ConverterUtilities.skipField(db);
      this.unpackContacts();
   }
}
