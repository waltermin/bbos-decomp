package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import javax.microedition.pim.PIM;
import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.blackberry.api.blackberrymessenger.Session;
import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.blackberry.api.pdap.ContactImpl;
import net.rim.blackberry.api.pdap.ContactListImpl;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.qm.peer.common.Contact;
import net.rim.device.apps.internal.qm.peer.common.GTPatriciaTreeHelper;
import net.rim.device.apps.internal.qm.peer.common.QmUtil;
import net.rim.device.apps.internal.smileys.Smileys;
import net.rim.vm.Array;

public final class PeerContact extends ContactStatus implements Contact, MessengerContact {
   private Object _idEncoding;
   private int _idHash = -1;
   private Object _displayNameEncoding;
   private Object _overrideDisplayNameEncoding;
   private Object _passcodeAnswerEncoding;
   private Object _keyEncoding;
   private Object _originalContactInfoEncoding;
   private byte[] _privateKey;
   private Vector _contactLists = new Vector();
   private int _refCount;
   private boolean _filtered;
   private Object _keywords;
   private int _uid;
   private int _cookie;
   private String _displayNameSmileys;
   private int[] _smileyOffsets = new int[0];
   private long[] _smileyIds = new long[0];
   static final int CONTACT_ID = 1;
   static final int DISPLAY_NAME_ID = 2;
   static final int OVERRIDE_DISPLAY_NAME_ID = 3;
   static final int PASSCODE_ANSWER_ID = 5;
   static final int KEY_ID = 6;
   static final int CRYPTO_KEY_ID = 8;
   static final int ORIGINAL_CONTACT_INFO_ID = 9;
   static final int CONTACT_ID_HASH = 17;
   static final int CONTACT_COOKIE = 18;
   static final int REFERENCE_COUNT = 19;
   private static final char PLACEHOLDER = '￼';
   static EmoticonStringPattern _smileyFacility = Smileys.getSmileyFacility();
   private static StringBuffer _buffer = new StringBuffer();

   final IntHashtable getPersistentData() {
      return super._persistentData;
   }

   final void removeItselfFromContactList() {
      for (int i = this._contactLists.size() - 1; i >= 0; i--) {
         ((PeerContactList)this._contactLists.elementAt(i)).removeContact(this);
      }
   }

   public final void madeContact() {
      if (this.isAlertable() || this.isTyping()) {
         this.setPresenceStatus(16384);
      }
   }

   public final void setPasscodeAnswer(String answer) {
      this._passcodeAnswerEncoding = PersistentContent.encode(answer, true, true);
      super._persistentData.put(5, this._passcodeAnswerEncoding);
      this.commit();
   }

   public final void setKey(String key) {
      this._keyEncoding = PersistentContent.encode(key, true, true);
      super._persistentData.put(6, this._keyEncoding);
      this.commit();
   }

   public final String getKey() {
      try {
         return PersistentContent.decodeString(this._keyEncoding).trim();
      } finally {
         ;
      }
   }

   public final void setPrivateKey(byte[] key) {
      this._privateKey = key;
      super._persistentData.put(8, this._privateKey);
      this.commit();
   }

   public final byte[] getPrivateKey() {
      return this._privateKey;
   }

   final Vector getContactLists() {
      return this._contactLists;
   }

   final int drawDisplayName(Graphics graphics, int x, int text_y, int smileys_y, int width) {
      this.smileysScan();
      int runner = x;
      int offset = 0;
      int smileysLen = this._smileyOffsets.length;
      int emoticonWidth = _smileyFacility.emoticonSize();

      for (int i = 0; i < smileysLen && runner < width; i++) {
         runner += graphics.drawText(this._displayNameSmileys, offset, this._smileyOffsets[i] - offset, runner, text_y, 70, width - runner);
         if (runner + emoticonWidth >= width) {
            runner += graphics.drawText('…', runner, text_y, 0, -1);
            return runner - x;
         }

         _smileyFacility.drawEmoticon(graphics, (int)this._smileyIds[i], runner + 1, smileys_y);
         runner += emoticonWidth;
         offset = this._smileyOffsets[i] + 1;
      }

      text_y += QmUtil.calculateCharacterDecoratorVerticalOffset(this._displayNameSmileys);
      if (offset != this._displayNameSmileys.length() && runner < width) {
         runner += graphics.drawText(this._displayNameSmileys, offset, this._displayNameSmileys.length() - offset, runner, text_y, 70, width - runner);
      }

      return runner - x;
   }

   final void paint(Graphics graphics, PeerConversation conversation, int x, int y, int width, int fontHeight, int iconHeight, boolean drawStatus) {
      fontHeight = QmUtil.calculateTrueFontHeight(this.getDisplayName());
      int absHeight = fontHeight > iconHeight ? fontHeight : iconHeight;
      int smileysHeight = _smileyFacility.emoticonSize();
      if (smileysHeight > absHeight) {
         absHeight = smileysHeight;
      }

      int indent = x + this.drawIcon(graphics, x, this.getY(y, absHeight, iconHeight), conversation != null ? conversation.isUnread() : false) + 1;
      if (this.isAlertable() && !this.isPending() && PeerApplication.alerts().isSet(this)) {
         PeerResources.drawIcon(graphics, x, y, 10);
      }

      if (this.isTyping()) {
         PeerResources.drawIcon(graphics, x, y, 12);
      }

      int text_y = this.getY(y, absHeight, fontHeight);
      Font originalFont = this.changeFont(graphics);
      indent += this.drawDisplayName(graphics, indent, text_y, this.getY(y, absHeight, smileysHeight), width - x);
      if (drawStatus) {
         this.drawStatus(graphics, x, text_y, width, indent);
      }

      if (originalFont != null) {
         graphics.setFont(originalFont);
      }
   }

   public final boolean isFiltered() {
      return this._filtered;
   }

   public final void setId(String id) {
      if (id != null) {
         id = id.toUpperCase();
         this.setIdInternal(id.hashCode(), QmUtil.encodeString(id));
      }
   }

   final void setIdInternal(int hashId, Object idEncoding) {
      this._idHash = hashId;
      super._persistentData.put(17, new Integer(hashId));
      this._idEncoding = idEncoding;
      super._persistentData.put(1, this._idEncoding);
      this.commit();
      this.resetKeywords();
   }

   public final void setFiltered(boolean filtered) {
      this._filtered = filtered;
   }

   final void removeContactList(PeerContactList list) {
      if (this._contactLists.contains(list)) {
         this._contactLists.removeElement(list);
      }
   }

   public final String getRealDisplayName() {
      return this._displayNameEncoding != null ? QmUtil.decodeString(this._displayNameEncoding) : this.getId();
   }

   public final void setDisplayName(String displayName) {
      if (Utils.isValidString(displayName)) {
         this.setDisplayNameInternal(PersistentContent.encode(displayName, true, true));
      }
   }

   final void setDisplayNameInternal(Object displayNameEncoding) {
      this._displayNameEncoding = displayNameEncoding;
      super._persistentData.put(2, displayNameEncoding);
      this.commit();
      if (this._contactLists != null) {
         PeerApplication.getInstance().getContactListCollection().contactUpdated(this);
      }

      this.resetKeywords();
      this._displayNameSmileys = null;
   }

   public final void setOverrideDisplayName(String displayName) {
      if (Utils.isValidString(displayName)) {
         this._overrideDisplayNameEncoding = PersistentContent.encode(displayName, true, true);
         super._persistentData.put(3, this._overrideDisplayNameEncoding);
         this.commit();
         if (this._contactLists != null) {
            PeerApplication.getInstance().getContactListCollection().contactUpdated(this);
         }
      }

      this.resetKeywords();
      this._displayNameSmileys = null;
   }

   final void addContactList(PeerContactList list) {
      if (!this._contactLists.contains(list)) {
         this._contactLists.addElement(list);
      }
   }

   final void setOriginalContactInfo(String originalContactInfo) {
      if (originalContactInfo != null) {
         this._originalContactInfoEncoding = PersistentContent.encode(originalContactInfo, true, true);
         super._persistentData.put(9, this._originalContactInfoEncoding);
         this.commit();
      }
   }

   final String getOriginalContactInfo() {
      if (this._originalContactInfoEncoding != null) {
         try {
            return PersistentContent.decodeString(this._originalContactInfoEncoding);
         } finally {
            ;
         }
      } else {
         return this.getId();
      }
   }

   final void setCookie(int cookie) {
      this._cookie = cookie;
      super._persistentData.put(18, new Integer(this._cookie));
      this.commit();
   }

   final int getCookie() {
      return this._cookie;
   }

   final void incrRefCount() {
      this._refCount++;
      super._persistentData.put(19, new Integer(this._refCount));
      this.commit();
   }

   final void decrRefCount() {
      this._refCount--;
      super._persistentData.put(19, new Integer(this._refCount));
      this.commit();
   }

   final int getRefCount() {
      return this._refCount;
   }

   public final Field[] getUserInfoFields() {
      int index = 0;
      Field[] fields = new Field[8];
      index = this.setNickName(fields, index);
      index = this.setName(fields, index);
      index = this.setInfo(fields, index);
      index = this.setCustomStatusMessage(fields, index);
      Array.resize(fields, index);
      return fields;
   }

   @Override
   public final BlackBerryContact getBlackBerryContact() {
      if (!PersistentContent.isSecure()) {
         AddressCardModel rimContact = Utils.getAddressCard(this.getOriginalContactInfo());
         if (rimContact != null) {
            try {
               ContactListImpl contactList = (ContactListImpl)PIM.getInstance().openPIMList(1, 3);
               return new ContactImpl(rimContact, contactList);
            } finally {
               return null;
            }
         }
      }

      return null;
   }

   @Override
   public final Session getSession() {
      return new SessionImpl(this);
   }

   @Override
   public final String getDisplayName() {
      try {
         if (this._overrideDisplayNameEncoding != null) {
            String name = PersistentContent.decodeString(this._overrideDisplayNameEncoding);
            if (Utils.isValidString(name)) {
               return name;
            }
         }

         return this.getRealDisplayName();
      } finally {
         ;
      }
   }

   @Override
   public final int getContactId() {
      return this.getIdHash();
   }

   @Override
   public final String getKeywords() {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         if (this._keywords == null) {
            this._keywords = GTPatriciaTreeHelper.generateKeywords(this.getDisplayName());
         }

         try {
            return PersistentContent.decodeString(this._keywords);
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   public final void setUID(int uid) {
      this._uid = uid;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   @Override
   public final int getIdHash() {
      return this._idHash;
   }

   @Override
   public final String getId() {
      try {
         return PersistentContent.decodeString(this._idEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final void contactStatusChanged() {
      super.contactStatusChanged();
      if (this._contactLists != null) {
         PeerApplication.getInstance().getContactListCollection().contactUpdated(this);
      }
   }

   @Override
   final void setAuthorized(boolean authorized) {
      if (this._contactLists.size() > 0) {
         this._contactLists.removeAllElements();
      }

      super.setAuthorized(authorized);
   }

   @Override
   protected final void commit() {
      PersistentObject.commit(super._persistentData);
   }

   private final int setNickName(Field[] fields, int index) {
      if (this._displayNameEncoding != null) {
         _buffer.setLength(0);
         _buffer.append(PeerResources.getString(0));
         _buffer.append(' ');
         _buffer.append(PersistentContent.decodeString(this._displayNameEncoding));
         fields[index++] = new RichTextField(_buffer.toString());
      }

      return index;
   }

   private final int setName(Field[] fields, int index) {
      if (this._overrideDisplayNameEncoding != null) {
         String overrideDispName = PersistentContent.decodeString(this._overrideDisplayNameEncoding);
         if (Utils.isValidString(overrideDispName)) {
            _buffer.setLength(0);
            _buffer.append(PeerResources.getString(1));
            _buffer.append(' ');
            _buffer.append(overrideDispName);
            fields[index++] = new RichTextField(_buffer.toString());
         }
      }

      return index;
   }

   private final int setInfo(Field[] fields, int index) {
      _buffer.setLength(0);
      _buffer.append(PeerResources.getString(2));
      _buffer.append(' ');
      _buffer.append(this.getOriginalContactInfo());
      fields[index++] = new ActiveRichTextField(_buffer.toString());
      String status = super.toString();
      if (status != null) {
         _buffer.setLength(0);
         _buffer.append(PeerResources.getString(3));
         _buffer.append(' ');
         _buffer.append(status);
         fields[index++] = new RichTextField(_buffer.toString());
      }

      return index;
   }

   private final int setCustomStatusMessage(Field[] fields, int index) {
      String customStatusMessage = this.getCustomStatusMessage();
      if (Utils.isValidString(customStatusMessage)) {
         _buffer.setLength(0);
         _buffer.append(PeerResources.getString(4));
         _buffer.append(' ');
         _buffer.append(customStatusMessage);
         fields[index++] = new RichTextField(_buffer.toString());
      }

      return index;
   }

   PeerContact() {
      super._persistentData = new IntHashtable();
      this.resetKeywords();
   }

   public PeerContact(IntHashtable contactData) {
      super(contactData);
      this._idEncoding = contactData.get(1);
      this._displayNameEncoding = contactData.get(2);
      this._overrideDisplayNameEncoding = contactData.get(3);
      this._passcodeAnswerEncoding = contactData.get(5);
      this._originalContactInfoEncoding = contactData.get(9);
      this._keyEncoding = contactData.get(6);
      this.setData(contactData);
      this.resetKeywords();
   }

   @Override
   public final String toString() {
      return this.getDisplayName();
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof PeerContact ? this._idHash == ((PeerContact)obj).getIdHash() : false;
   }

   private final void resetKeywords() {
      this._keywords = null;
   }

   @Override
   final void lock() {
      super.lock();
      this._idEncoding = PersistentContent.reEncode(this._idEncoding, true, true);
      this._displayNameEncoding = PersistentContent.reEncode(this._displayNameEncoding, true, true);
      this._displayNameSmileys = null;
      this.commit();
   }

   @Override
   public final void setPresenceStatus(int value) {
      boolean alertable = this.isAlertable();
      super.setPresenceStatus(value);
      if (alertable) {
         if (!this.isAlertable()) {
            PeerApplication.getInstance();
            if (PeerApplication.alerts().isSet(this)) {
               PeerApplication.getInstance();
               PeerApplication.notifications().triggerContactAvailable(this);
               return;
            }
         }
      } else if (this.isAlertable()) {
         PeerApplication.getInstance();
         PeerApplication.dismissContactAvailableDialog(this);
      }
   }

   @Override
   final int drawIcon(Graphics g, int x, int y, boolean unread) {
      if (this.isPending()) {
         PeerResources.drawIcon(g, x, y, 1);
         return PeerResources.drawIcon(g, x, y, 9);
      } else {
         return super.drawIcon(g, x, y, unread);
      }
   }

   public PeerContact(String name, String id, boolean pending) {
      super(null);
      this.setId(id);
      this.setDisplayName(name);
      this.setPending(pending);
      if (!pending) {
         this.setPresenceStatus(16384);
      }

      this.commit();
      this.resetKeywords();
   }

   private final void drawStatus(Graphics graphics, int x, int y, int width, int indent) {
      String message = this.getCustomStatusMessage();
      if (Utils.isValidString(message)) {
         _buffer.setLength(0);
         _buffer.append(' ');
         _buffer.append('-');
         _buffer.append(' ');
         _buffer.append(message);
         graphics.drawText(_buffer, 0, _buffer.length(), indent, y, 70, width - indent - x);
      }
   }

   private final Font changeFont(Graphics graphics) {
      Font originalFont = null;
      if (this.isAlertable() || this.isPending()) {
         originalFont = graphics.getFont();
         Font newFont = originalFont.derive(2);
         graphics.setFont(newFont);
      }

      return originalFont;
   }

   private final int getY(int y, int absHeight, int height) {
      return absHeight > height ? absHeight - height >> 1 + y : y;
   }

   private final void setData(IntHashtable contactData) {
      Object obj = contactData.get(8);
      if (obj instanceof byte[]) {
         this._privateKey = (byte[])obj;
      }

      obj = contactData.get(18);
      if (obj instanceof Integer) {
         this._cookie = (Integer)obj;
      }

      obj = contactData.get(19);
      if (obj instanceof Integer) {
         this._refCount = (Integer)obj;
      }

      obj = contactData.get(17);
      if (obj instanceof Integer) {
         this._idHash = (Integer)obj;
      }
   }

   private final void cleanSmilies() {
      Array.resize(this._smileyOffsets, 0);
      Array.resize(this._smileyIds, 0);
   }

   private final void smileysScan() {
      if (this._displayNameSmileys == null) {
         this.cleanSmilies();
         String text = this.getDisplayName();
         if (text != null) {
            char[] text2 = new char[text.length()];
            StringPattern$Match match = new StringPattern$Match();
            match.endIndex = 0;
            int pos = 0;
            int length = text.length();
            int dstIndex = 0;
            if (_smileyFacility != null) {
               for (AbstractString abstractString = AbstractStringWrapper.createInstance(text);
                  _smileyFacility.findMatch(abstractString, pos, length, match);
                  pos = match.endIndex
               ) {
                  if (match.beginIndex != pos) {
                     text.getChars(pos, match.beginIndex, text2, dstIndex);
                     dstIndex += match.beginIndex - pos;
                  }

                  Arrays.add(this._smileyOffsets, dstIndex);
                  Arrays.add(this._smileyIds, match.id);
                  text2[dstIndex++] = '￼';
               }
            }

            if (pos < length) {
               text.getChars(pos, length, text2, dstIndex);
               dstIndex += length - pos;
            }

            Array.resize(text2, dstIndex);
            this._displayNameSmileys = new String(text2);
         }
      }
   }
}
