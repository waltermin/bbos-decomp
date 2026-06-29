package net.rim.device.apps.internal.mms.model;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VisibilityControl;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.utility.lowMemory.PurgeProvider;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.api.MMSStatusReport;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;

final class MMSMessageModelImpl
   implements MMSMessageModel,
   KeyProvider,
   MatchProvider,
   PurgeProvider,
   ColumnPaintProvider,
   FolderProvider,
   EncryptableProvider,
   VisibilityControl,
   MessagePartsProvider {
   private long _folderId;
   private MMSPayloadModelImpl _payload;
   private Vector _readReports;
   private Vector _deliveryReports;
   private int _status;
   private int _httpErrorCode;
   private int _mmsResponseCode;
   private int _wapIOExceptionError;
   private int _wapIOExceptionAdditionalData;
   private int _flags;
   private long _deliveryDate;
   private long _readDate;
   static final int UNOPENED;
   static final int SAVED;
   static final int SAVED_THEN_ORPHANED;
   static final int DELETED;
   static final int INBOUND;
   static final int HIDE_SENDER;
   static final int USE_SMART_DIALING;
   static final int FORWARD_LOCKED;
   static final int PENDING_READ_NOTIFICATION;
   static final int IS_NEW;

   final void addDeliveryReport(MMSStatusReport report) {
      if (this._deliveryReports == null) {
         this._deliveryReports = (Vector)(new Object());
      }

      this._deliveryReports.addElement(report);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return MMSMessageVerbProvider.getVerbs(this, context, verbs);
   }

   @Override
   public final int match(Object searchCriteria) {
      if (!(searchCriteria instanceof Object)) {
         MMSPayloadModelImpl var6 = this._payload;
         SearchCriterion[] crit = (Object[])searchCriteria;

         for (int var8 = crit.length - 1; var8 >= 0; var8--) {
            SearchCriterion c = crit[var8];
            if (Match.performMatch(this, c) != 1 && Match.performMatch(var6, c) != 1) {
               return 0;
            }
         }

         return 1;
      } else {
         SearchCriterion criterion = (SearchCriterion)searchCriteria;
         switch (criterion.getType()) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 15:
            case 17:
            case 23:
            case 27:
               return -1;
            case 2:
            case 21:
            default:
               StringMatch matcher = (StringMatch)criterion.getValue();
               boolean match = matcher.indexOf(this._payload.getAttribute("subject")) >= 0;
               if (match) {
                  return 1;
               }

               return 0;
            case 9:
               if (this.isInbound()) {
                  return 1;
               }

               return 0;
            case 10:
               if (this.isInbound()) {
                  return 0;
               }

               return 1;
            case 11:
               if (this.isSaved()) {
                  return 1;
               }

               return 0;
            case 12:
            case 13:
            case 14:
            case 16:
            case 18:
            case 19:
            case 25:
            case 26:
               return 0;
            case 20:
               return 1;
            case 22:
               if (this.isDraft()) {
                  return 1;
               }

               return 0;
            case 24:
               if (criterion.getValue() == this.getUID()) {
                  return 1;
               }

               return 0;
            case 28:
               return this.isInbound() && !this.isOpened() ? 1 : 0;
         }
      }
   }

   @Override
   public final int getUID() {
      return (int)this._payload.getCreationDate();
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      return MMSMessageActionProvider.perform(this, actionId, context);
   }

   final void addReadReport(MMSProtocolDataUnit pdu) {
      if (pdu == null) {
         throw new Object();
      }

      long date = MMSUtilities.parseLong(pdu.getAttribute("date"), 0) * 1000;
      if (date <= 0) {
         date = System.currentTimeMillis();
      }

      String address = pdu.getAttribute("from");
      int status = MMSUtilities.parseInt(pdu.getAttribute("x-mms-read-status"), 128);
      System.out.println(((StringBuffer)(new Object("MMS ReadReport "))).append(address).append(" ,").append(status).append(", ").append(date).toString());
      this.updateStatusFromReadReport(address, date, status);
   }

   @Override
   public final Object clone(Object context) {
      MMSMessageModelImpl newMessage = new MMSMessageModelImpl();
      newMessage.setStatus(134217727);
      newMessage.setFlags(this.getFlags());
      MMSPayloadModelImpl payload = (MMSPayloadModelImpl)this.getPayload();
      newMessage.setPayload((MMSPayloadModel)payload.clone(context));
      return newMessage;
   }

   @Override
   public final Object invokeHotkey(Object context, int hotkeyID) {
      return MMSMessageHotKeyProvider.invokeHotkey(this, context, hotkeyID);
   }

   final void addDeliveryReport(MMSProtocolDataUnit pdu) {
      long date = MMSUtilities.parseLong(pdu.getAttribute("date"), 0) * 1000;
      if (date <= 0) {
         date = System.currentTimeMillis();
      }

      String address = null;
      Vector recipients = pdu.getRecipients();
      if (recipients != null && recipients.size() > 0) {
         address = (String)recipients.elementAt(0);
      }

      int status = MMSUtilities.parseInt(pdu.getAttribute("x-mms-status"), 132);
      System.out.println(((StringBuffer)(new Object("MMS DeliveryReport "))).append(address).append(" ,").append(status).append(", ").append(date).toString());
      this.updateStatusFromDeliveryReport(address, date, status);
   }

   final void setStatus(int status, int httpErrorCode, int mmsResponseCode, int wapIOExceptionError, int wapIOExceptionAdditionalData) {
      this._status = status;
      this._httpErrorCode = httpErrorCode;
      this._mmsResponseCode = mmsResponseCode;
      this._wapIOExceptionError = wapIOExceptionError;
      this._wapIOExceptionAdditionalData = wapIOExceptionAdditionalData;
   }

   final void addReadReport(MMSStatusReport report) {
      if (this._readReports == null) {
         this._readReports = (Vector)(new Object());
      }

      this._readReports.addElement(report);
   }

   final void setHttpErrorCode(int httpErrorCode) {
      this._httpErrorCode = httpErrorCode;
   }

   final void setStatus(int status) {
      this.setStatus(status, 0, 0, 0, 0);
   }

   public final void setMMSResponseCode(int mmsResponseCode) {
      this._mmsResponseCode = mmsResponseCode;
   }

   public final boolean requestedReadNotification() {
      String str = this.getPayload().getAttribute("x-mms-read-report");
      return MMSUtilities.parseInt(str, 129) == 128;
   }

   public final void setWAPIOExceptionError(int wapIOExceptionError) {
      this._wapIOExceptionError = wapIOExceptionError;
   }

   public final boolean isReadNotificationPending() {
      return this.flagsSet(256);
   }

   public final void setWAPIOExceptionAdditionalData(int wapIOExceptionAdditionalData) {
      this._wapIOExceptionAdditionalData = wapIOExceptionAdditionalData;
   }

   final boolean isNotYetSent() {
      switch (this.getStatus()) {
         case 4095:
         case 67108863:
         case 134217727:
            return true;
         default:
            return false;
      }
   }

   final void setDeliveryDate(long date) {
      this._deliveryDate = date;
   }

   final boolean isInSavedMessagesCache() {
      return this.flagsSet(2) && !this.flagsSet(4);
   }

   final void setReadDate(long date) {
      this._readDate = date;
   }

   final int getFlags() {
      return this._flags;
   }

   final void setFlags(int mask) {
      this._flags |= mask;
   }

   final void clearFlags() {
      this._flags = 0;
   }

   final void clearFlags(int mask) {
      this._flags &= ~mask;
   }

   public final boolean isDraft() {
      return this._status == Integer.MAX_VALUE;
   }

   final void setPayload(MMSPayloadModel payload) {
      ObjectGroup.createGroupIgnoreTooBig(payload);
      this._payload = (MMSPayloadModelImpl)payload;
   }

   @Override
   public final AttachmentDataProvider getAttachmentDataProvider() {
      return this._payload;
   }

   @Override
   public final MMSPayloadModel getPayload() {
      return this._payload;
   }

   @Override
   public final void setFolderId(long folderId) {
      this._folderId = folderId;
   }

   @Override
   public final boolean isSmartDialed() {
      return true;
   }

   @Override
   public final boolean isInbound() {
      return this.flagsSet(16);
   }

   @Override
   public final boolean isOpened() {
      return !this.flagsSet(1);
   }

   @Override
   public final boolean isUnopened() {
      return this.flagsSet(1);
   }

   @Override
   public final long getFolderId() {
      return this._folderId;
   }

   @Override
   public final boolean isSaved() {
      return this.flagsSet(2);
   }

   @Override
   public final boolean isSavedThenOrphaned() {
      return this.flagsSet(4);
   }

   @Override
   public final long getReadDate() {
      return this._readDate;
   }

   @Override
   public final boolean isSuccessfullySent() {
      if (this.isInbound()) {
         throw new Object("Valid for outbound calls only");
      }

      switch (this.getStatus()) {
         case 262143:
         case 524287:
         case 1048575:
         case 2097151:
         case 4194303:
         case 8388607:
         case 16777215:
         case 33554431:
            return true;
         default:
            return false;
      }
   }

   @Override
   public final long getDeliveryDate() {
      return this._deliveryDate;
   }

   @Override
   public final boolean isForwardLocked() {
      return this.flagsSet(128);
   }

   @Override
   public final int getWAPIOExceptionAdditionalData() {
      return this._wapIOExceptionAdditionalData;
   }

   @Override
   public final int getWAPIOExceptionError() {
      return this._wapIOExceptionError;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested != -7628247220259263034L && keyRequested != 92199951187614847L) {
         return 0;
      }

      keyArray[index] = this._payload.getCreationDate();
      return 1;
   }

   @Override
   public final boolean canPurge(int purgeType) {
      if (purgeType != 0 && purgeType != 1) {
         return purgeType == 3 ? this.flagsSet(2) : false;
      } else {
         return !this.flagsSet(2);
      }
   }

   @Override
   public final void purge(int purgeType) {
      LowMemoryManager.markAsRecoverable(this);
      this.perform(6780594967363292755L, null);
   }

   @Override
   public final int getMMSResponseCode() {
      return this._mmsResponseCode;
   }

   @Override
   public final void paint(ColumnPainter painter, Object context) {
      int priority = MMSUtilities.parseInt(this._payload.getAttribute("x-mms-priority"), 129);
      switch (priority) {
         case 128:
            painter.setPriority(3);
            break;
         case 130:
            painter.setPriority(2);
      }

      painter.drawIcon(1, MessageIcons.getIcons(), this.getStatusIcon());
      painter.drawTime(2, this.getPayload().getCreationDate());
      painter.drawModel(3, this.getAddressToPaint(), context);
      if (this.isUnopened()) {
         painter.setEmphasis(true);
      }

      painter.drawText(4, this.getPayload().getAttribute("subject"), false);
   }

   @Override
   public final int getHttpErrorCode() {
      return this._httpErrorCode;
   }

   @Override
   public final int getStatus() {
      return this._status;
   }

   @Override
   public final int getReadReportCount() {
      return this._readReports == null ? 0 : this._readReports.size();
   }

   @Override
   public final int getDeliveryReportCount() {
      return this._deliveryReports == null ? 0 : this._deliveryReports.size();
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return this._payload.checkCrypt(compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      MMSPayloadModelImpl payload = (MMSPayloadModelImpl)ObjectGroup.expandGroup(this._payload);
      payload.reCrypt(compress, encrypt);
      ObjectGroup.createGroupIgnoreTooBig(payload);
      this._payload = payload;
      return null;
   }

   @Override
   public final boolean inbound() {
      return this.isInbound();
   }

   @Override
   public final String getSubject() {
      return this.getPayload().getAttribute("subject");
   }

   @Override
   public final void setRead(Object context) {
      this.perform(5803508244060051872L, null);
   }

   @Override
   public final String getSender() {
      return this.getPayload().getAttribute("from");
   }

   @Override
   public final String[] getRecipients() {
      return new Object[]{this.getPayload().getAttribute("to"), this.getPayload().getAttribute("cc"), this.getPayload().getAttribute("bcc")};
   }

   @Override
   public final boolean allowDescriptiveForwardHeader() {
      return true;
   }

   @Override
   public final long getSentDate() {
      long sendDate = MMSUtilities.parseLong(this.getPayload().getAttribute("date"), 0) * 1000;
      if (sendDate <= 0) {
         sendDate = this.getPayload().getCreationDate();
      }

      return sendDate;
   }

   @Override
   public final String getName() {
      return MMSResources.getString(9);
   }

   @Override
   public final String getBody() {
      StringBuffer buf = (StringBuffer)(new Object());
      MMSUtilities.getMessageBodyText(buf, this.getAttachmentDataProvider());
      return buf.toString();
   }

   @Override
   public final MessageAttachment[] getAttachments() {
      Vector v = (Vector)(new Object());
      this.getNonTextAttachments(v, this.getAttachmentDataProvider());
      if (v.size() == 0) {
         return null;
      }

      int count = v.size();
      MessageAttachment[] attachments = new Object[count];

      for (int idx = 0; idx < count; idx++) {
         attachments[idx] = new MMSMessageAttachmentAdaptor((MMSAttachment)v.elementAt(idx));
      }

      return attachments;
   }

   @Override
   public final MMSStatusReport getReadReport(int index) {
      return index >= 0 && index <= this.getReadReportCount() - 1 && this._readReports != null ? (MMSStatusReport)this._readReports.elementAt(index) : null;
   }

   @Override
   public final MMSStatusReport getDeliveryReport(int index) {
      return index >= 0 && index <= this.getDeliveryReportCount() - 1 && this._deliveryReports != null
         ? (MMSStatusReport)this._deliveryReports.elementAt(index)
         : null;
   }

   @Override
   public final byte getVisibilityFlags() {
      return this.visibilityForStatus(this.getStatus());
   }

   @Override
   public final boolean isNew() {
      return this.flagsSet(512);
   }

   private final void updateStatusFromReadReport(String address, long date, int reportStatus) {
      this.addReadReport(new MMSStatusReportImpl(address, date, reportStatus));
      if (this._status != 2097151) {
         int status;
         switch (reportStatus) {
            case 129:
               status = 16777215;
               break;
            default:
               status = 2097151;
         }

         this.setStatus(status);
         this.setReadDate(date);
      }
   }

   private final int getStatusIcon() {
      int status = this.getStatus();
      if (this.isInbound()) {
         return this.isOpened() ? 196610 : 196609;
      }

      switch (status) {
         case 8191:
         case 16383:
         case 131071:
            return 196620;
         case 262143:
         case 524287:
         case 1048575:
         case 4194303:
         case 8388607:
         case 16777215:
            return 196618;
         case 2097151:
            return 196619;
         case 33554431:
            return 196617;
         case 67108863:
            return 196616;
         case 134217727:
            return 196615;
         case Integer.MAX_VALUE:
            return 196612;
         default:
            return 0;
      }
   }

   private final void getNonTextAttachments(Vector v, AttachmentDataProvider attachmentProvider) {
      Enumeration names = attachmentProvider.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            MMSAttachment attachment = attachmentProvider.getAttachment(name);
            if (attachment != null) {
               int type = attachment.getType();
               if (type == 62) {
                  byte[] data = attachment.getData();
                  MMSProtocolDataUnit pdu = new MMSProtocolDataUnit(data);
                  this.getNonTextAttachments(v, pdu);
               } else if (!MMSUtilities.isTextType(type)) {
                  v.addElement(attachment);
               }
            }
         }
      }
   }

   private final RIMModel getAddressToPaint() {
      if (this.isInbound()) {
         return this.getPayload().getSender();
      }

      Vector recipients = this.getPayload().getRecipients();
      if (recipients != null && recipients.size() > 0) {
         return (RIMModel)recipients.elementAt(0);
      }

      recipients = this.getPayload().getCcRecipients();
      return (RIMModel)(recipients != null && recipients.size() > 0 ? recipients.elementAt(0) : null);
   }

   private final void updateStatusFromDeliveryReport(String address, long date, int reportStatus) {
      this.addDeliveryReport(new MMSStatusReportImpl(address, date, reportStatus));
      if (this._status != 2097151 && this._status != 16777215) {
         int status;
         switch (reportStatus) {
            case 128:
               status = 8388607;
               break;
            case 130:
               status = 1048575;
               break;
            case 131:
               status = 524287;
               break;
            case 134:
               status = 262143;
               break;
            default:
               status = 4194303;
         }

         this.setStatus(status);
         this.setDeliveryDate(date);
      }
   }

   private final byte visibilityForStatus(int status) {
      byte flags = 0;
      switch (status) {
         default:
            flags = (byte)(flags | 1);
         case 2097151:
         case 33554431:
            return (byte)(flags | (this.isNew() ? 8 : 0));
      }
   }

   private final boolean flagsSet(int flags) {
      return (this._flags & flags) != 0;
   }
}
