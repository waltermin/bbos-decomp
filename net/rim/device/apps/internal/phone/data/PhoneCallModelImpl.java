package net.rim.device.apps.internal.phone.data;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.model.VisibilityControl;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.lowMemory.PurgeProvider;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneCallModel;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.IconCollection;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class PhoneCallModelImpl
   implements PhoneCallModel,
   ReadableList,
   WritableSet,
   SyncObject,
   Unopened,
   VerbProvider,
   FieldProvider,
   PaintProvider,
   ColumnPaintProvider,
   KeyProvider,
   ConversionProvider,
   MatchProvider,
   ActionProvider,
   DefaultProvider,
   PurgeProvider,
   HotKeyProvider,
   EncryptableProvider,
   FolderProvider,
   VisibilityControl {
   byte _type = 0;
   private PhoneCallPayloadModel _payload;
   int _refId = 0;
   int _errorCode = 0;
   protected int _uid = 0;
   private boolean _newFlag;
   private int _lineId = 1;
   public static final long SUBCLASS_FACTORIES;
   private static final byte[] phoneCallRecordId = new byte[]{112};
   private static ContextObjectWR _phoneCallSyncContextWR = (ContextObjectWR)(new Object(20, 19));
   private static WeakReference _dateTimeBufferWR = (WeakReference)(new Object(null));
   private static DateFormat _dateTimeFormatDateOnly;
   private static DateFormat _dateTimeFormatTimeOnly;
   private static DateFormat _dateTimeFormatDateAndTime;
   private static SimpleDateFormat _dateTimeFormatShortDate;
   private static Calendar _dateFormatCal = Calendar.getInstance();
   private static CalendarExtensions _shortDateFormatCal = (CalendarExtensions)_dateFormatCal;
   static final int DATE_OR_TIME;
   static final int DATE_AND_TIME;
   static final int DATE_AND_TIME_SHORT;
   static final int DATE_ONLY;
   static final int DATE_EXTRA_SHORT;
   static int[] _hints = new int[0];
   private static WeakReference _emailBodyTextBufferWR = (WeakReference)(new Object(null));

   @Override
   public int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      RIMModel submemberModel = null;
      if (ContextObject.getFlag(context, 58)) {
         RIMModel var11 = this.getCallerIDInfo();
         if (var11 instanceof Object) {
            PaintProvider paintProvider = (PaintProvider)var11;
            paintProvider.paint(graphics, x, y, width, height, context);
         }
      } else if (ContextObject.getFlag(context, 34)) {
         String typeString = MessageFormat.format(PhoneResources.getString(253), new Object[]{this.getTypeString()});
         graphics.drawText(typeString, x, y, 64, width);
      } else if (ContextObject.getFlag(context, 47)) {
         RIMModel var12 = this._payload.getAt(0);
         if (var12 instanceof Object) {
            PaintProvider paintProvider = (PaintProvider)var12;
            paintProvider.paint(graphics, x, y, width, height, context);
         }
      } else if (ContextObject.getFlag(context, 27)) {
         int flags = 0;
         if (this._type == 5 || this._type == 6) {
            StringBuffer buf = (StringBuffer)(new Object());
            buf.append(PhoneResources.getString(6020));
            buf.append(this._payload.getElapsedTime());
            graphics.drawText(buf, 0, buf.length(), x, y, flags, width);
         } else if (this._payload.getElapsedTime() != 0) {
            RIMModel var13 = this._payload.getAt(1);
            if (var13 instanceof Object) {
               PaintProvider paintProvider = (PaintProvider)var13;
               paintProvider.paint(graphics, x, y, width, height, context);
            }
         } else {
            StringBuffer buf;
            buf = (StringBuffer)(new Object());
            label44:
            switch (this._type) {
               case 0:
                  break;
               case 1:
                  switch (this._errorCode) {
                     case 0:
                        buf.append(PhoneResources.getString(169));
                        buf.append(PhoneResources.getString(123));
                     default:
                        break label44;
                  }
               case 2:
               case 3:
               default:
                  buf.append(PhoneResources.getString(169));
                  buf.append(PhoneResources.getString(this._type == 2 ? 125 : 126));
            }

            if (buf.length() > 0) {
               graphics.drawText(buf, 0, buf.length(), x, y, flags, width);
            } else {
               RIMModel var14 = this._payload.getAt(1);
               if (var14 instanceof Object) {
                  PaintProvider paintProvider = (PaintProvider)var14;
                  paintProvider.paint(graphics, x, y, width, height, context);
               }
            }
         }
      }

      return width;
   }

   @Override
   public Verb getVerbs(Object ctxt, Verb[] verbs) {
      if (ContextObject.getFlag(ctxt, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      CallerIDInfo cidi = null;
      if (ContextObject.getFlag(ctxt, 119)) {
         if (ContextObject.getFlag(ctxt, 3)) {
            return null;
         }

         if (this.isDirectConnectType()) {
            return null;
         }

         if (this.isConferenceCallLog()) {
            Field focusedField = (Field)ContextObject.get(ctxt, 6780852635736686874L);
            if (focusedField instanceof Object) {
               ObjectListField listField = (ObjectListField)focusedField;
               Object callerId = listField.get(listField, listField.getSelectedIndex());
               if (callerId instanceof CallerIDInfo) {
                  return ((CallerIDInfo)callerId).getVerbs(ctxt, verbs);
               }
            }
         }

         cidi = this.getCallerIDInfo();
         if (cidi != null) {
            return cidi.getVerbs(ctxt, verbs);
         }
      }

      if (PhoneUtilities.getPrivateFlag(ctxt, 72)) {
         cidi = this.getCallerIDInfo();
         if (cidi.isVoicemailCallerIDInfo()) {
            PhoneUtilities.setPrivateFlag(ctxt, 7);
            return null;
         } else {
            return this.getCallerIDVerbs(cidi, verbs, ctxt);
         }
      } else {
         int verbIndex = 0;
         Object addressCard = null;
         ContextObject context = ContextObject.clone(ctxt);
         if (!ContextObject.getFlag(context, 24)) {
            if (!ContextObject.getFlag(context, 3) && ContextObject.getFlag(context, 5)) {
               Array.resize(verbs, 0);
               return null;
            }

            Verb openCallVerb = new PhoneCallModelImpl$DoSomethingToThePhoneCallModelVerb(this, 0, 590080, CommonResource.getBundle(), 15);
            if (this._type == 4) {
               PhoneUtilities.appendVerbs(verbs, openCallVerb, this.getForwardCallLogVerb());
            } else {
               cidi = this.getCallerIDInfo();
               if (cidi != null) {
                  addressCard = cidi.getAddress();
                  if (addressCard != null) {
                     int phoneNumberCount = PhoneUtilities.countPhoneNumbersInAddressCard(addressCard);
                     if (phoneNumberCount > 1) {
                        PhoneUtilities.setPrivateFlag(context, 59);
                     } else {
                        PhoneUtilities.clearPrivateFlag(context, 59);
                     }
                  }

                  PhoneUtilities.setPrivateFlag(context, 70);
                  PhoneUtilities.setPrivateFlag(context, 15);
                  Verb[] cidiVerbs = new Object[0];
                  cidi.getVerbs(context, cidiVerbs);
                  Recognizer dialVerbRecognizer = DialVerb.getRecognizer();

                  for (int i = cidiVerbs.length - 1; i >= 0; i--) {
                     Verb v = cidiVerbs[i];
                     if (dialVerbRecognizer.recognize(v)) {
                        Array.resize(verbs, verbs.length + 1);
                        verbs[verbs.length - 1] = new PhoneCallModelImpl$DialMarkOpenWrapperVerb(this, v);
                     }

                     if (context.getFlag(102) && v instanceof Object) {
                        Array.resize(verbs, verbs.length + 1);
                        verbs[verbs.length - 1] = v;
                     }
                  }
               }

               PhoneUtilities.appendVerbs(verbs, openCallVerb, this.getForwardCallLogVerb());
               if (this.canMarkOpened() || this.canMarkUnopened()) {
                  int actionId = this.canMarkOpened() ? 1 : 2;
                  int menuOrdering = this.canMarkOpened() ? 602448 : 602450;
                  int menuId = this.canMarkOpened() ? 1325 : 1350;
                  Verb markOpenedVerb = new PhoneCallModelImpl$DoSomethingToThePhoneCallModelVerb(
                     this, actionId, menuOrdering, CommonResources.getResourceBundle(), menuId
                  );
                  PhoneUtilities.appendVerb(verbs, markOpenedVerb);
               }
            }

            return openCallVerb;
         } else {
            if (this._type != 4) {
               cidi = this.getCallerIDInfo();
               if (cidi != null) {
                  if (cidi.isVoicemailCallerIDInfo() && VoicemailIconManager.getInstance().isIndicatorOn()) {
                     return defaultVerb;
                  }

                  addressCard = cidi.getAddress();
                  if (addressCard != null) {
                     int phoneNumberCount = PhoneUtilities.countPhoneNumbersInAddressCard(addressCard);
                     if (phoneNumberCount > 1) {
                        context.setFlag(34);
                        PhoneUtilities.setPrivateFlag(context, 59);
                     } else {
                        PhoneUtilities.clearPrivateFlag(context, 59);
                     }
                  }

                  defaultVerb = cidi.getVerbs(context, verbs);
               }
            } else {
               Field focusedField = (Field)ContextObject.get(ctxt, 6780852635736686874L);
               if (focusedField instanceof Object) {
                  ObjectListField listField = (ObjectListField)focusedField;
                  Object callerId = listField.get(listField, listField.getSelectedIndex());
                  if (callerId instanceof CallerIDInfo) {
                     defaultVerb = this.getCallerIDVerbs((CallerIDInfo)callerId, verbs, context);
                  }
               }
            }

            PhoneUtilities.appendVerb(verbs, this.getForwardCallLogVerb());
            return defaultVerb;
         }
      }
   }

   @Override
   public int match(Object searchCriteria) {
      if (!(searchCriteria instanceof Object)) {
         Object[] var6 = null;
         if (searchCriteria instanceof Object[]) {
            var6 = (Object[])searchCriteria;
         }

         return var6 != null ? Match.match(this, this, var6, _hints) : -1;
      } else {
         SearchCriterion criterion = (SearchCriterion)searchCriteria;
         boolean match;
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
            case 18:
            case 20:
            case 23:
            case 27:
               return -1;
            case 2:
            case 21:
            default:
               String phone_subject = this.getTypeString();
               StringMatch matcher = (StringMatch)criterion.getValue();
               match = matcher.indexOf(phone_subject) >= 0;
               break;
            case 9:
               match = this.isIncoming() && !this.isDirectConnectType();
               break;
            case 10:
               match = !this.isIncoming() && !this.isDirectConnectType();
               break;
            case 11:
            case 12:
            case 13:
            case 19:
            case 22:
            case 25:
            case 26:
               match = false;
               break;
            case 14:
               match = !this.isDirectConnectType();
               break;
            case 16:
               match = this.isDirectConnectType();
               break;
            case 24:
               match = criterion.getValue() == this.getUID();
               break;
            case 28:
               match = this.isIncoming() && !this.isDirectConnectType() && this.isUnopened();
         }

         return match ? 1 : 0;
      }
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   @Override
   public boolean perform(long actionId, Object context) {
      boolean result = false;
      if (actionId == 6780594967363292755L) {
         this.delete();
      } else if (actionId == 5803508244060051872L) {
         if (this.canMarkOpened()) {
            markOpened(this);
         }
      } else if (actionId == -8629311385729242560L) {
         if (this.canMarkUnopened()) {
            markUnopened(this);
         }
      } else if (actionId == -5544992959212130441L) {
         result = this.canMarkOpened();
      } else if (actionId == 477896226347912237L) {
         result = this.canMarkUnopened();
      } else if (actionId == 635678369939227345L) {
         result = true;
      } else if (actionId == 278390328807340479L) {
         result = this.canMarkOpened() || this.getType() == 5;
      } else if (actionId == 4951292880494466830L) {
         VoiceUnopenedCount.getInstance().itemInitialized(this);
         CallLogCollection.getInstance().callLogInitialized(this);
      } else if (actionId == -6225946334564270161L) {
         if (this.canMarkOpened()) {
            markOpened(this);
         }
      } else if (actionId == -3967872215949752466L) {
         this.delete();
      } else if (actionId == 6099736323056465049L) {
         this.perform(5803508244060051872L, null);
         viewCallLog(this, context);
      } else if (actionId == 5213547777258110094L) {
         this.bulkMarkOld();
         result = true;
      } else if (actionId == -6072303684925088654L) {
         result = this.isNew();
      }

      return result;
   }

   @Override
   public boolean convert(Object context, Object target) {
      RIMModel submemberModel = null;
      StringBuffer buf = null;
      StringBuffer tempBuffer = (StringBuffer)(new Object());
      if (ContextObject.getFlag(context, 20) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addBytes(1, phoneCallRecordId);
         syncBuffer.addInt(2, this._type, 4);
         syncBuffer.addInt(3, this._payload.getElapsedTime(), 4);
         syncBuffer.addLong(4, this._payload.getTimeStamp());
         syncBuffer.addInt(5, this._refId, 4);
         syncBuffer.addInt(6, this._errorCode, 4);
         syncBuffer.addInt(7, this._uid, 4);
         syncBuffer.addInt(16, this._lineId, 4);
         return syncBuffer.addSubmembers(this, _phoneCallSyncContextWR.getContextObject());
      }

      if (!(target instanceof Object)) {
         return false;
      }

      buf = (StringBuffer)target;
      buf.setLength(0);
      String line_nl = "------------------\n";
      buf.append('\n');
      buf.append(line_nl);
      RIMModel var11 = this._payload.getAt(0);
      if (var11 instanceof Object) {
         ConversionProvider conversionProvider = (ConversionProvider)var11;
         if (conversionProvider.convert(context, tempBuffer)) {
            buf.append(tempBuffer);
            buf.append('\n');
         }
      }

      var11 = this._payload.getAt(1);
      if (var11 instanceof Object) {
         ConversionProvider conversionProvider = (ConversionProvider)var11;
         if (conversionProvider.convert(context, tempBuffer)) {
            buf.append(tempBuffer);
            buf.append('\n');
         }
      }

      int size = this.size();

      for (int i = 3; i < size; i++) {
         var11 = this._payload.getAt(i);
         ContextObject contextObject = ContextObject.clone(context);
         contextObject.setFlag(4);
         if (var11 == null) {
            break;
         }

         if (var11 instanceof Object) {
            ConversionProvider conversionProvider = (ConversionProvider)var11;
            if (conversionProvider.convert(contextObject, tempBuffer)) {
               buf.append(tempBuffer);
               buf.append('\n');
            }
         }
      }

      var11 = this._payload.getAt(2);
      if (var11 instanceof Object) {
         ConversionProvider conversionProvider = (ConversionProvider)var11;
         if (conversionProvider.convert(context, tempBuffer)) {
            buf.append(line_nl);
            buf.append(tempBuffer);
         }
      }

      return true;
   }

   public int getElapsedTime() {
      return this._payload.getElapsedTime();
   }

   @Override
   public Object invokeHotkey(Object context, int hotkeyID) {
      switch (hotkeyID) {
         case 152:
            int actionId = this.canMarkOpened() ? 1 : 2;
            Verb verb = new PhoneCallModelImpl$DoSomethingToThePhoneCallModelVerb(this, actionId, 0);
            return verb.invoke(context);
         default:
            return null;
      }
   }

   public long getTimeStamp() {
      return this._payload.getTimeStamp();
   }

   public void setTimeStamp(long timeStamp) {
      this._payload.setTimeStamp(timeStamp);
   }

   public void setNew(boolean newFlag) {
      this._newFlag = newFlag;
   }

   public int getErrorCode() {
      return this._errorCode;
   }

   public void setLineId(int lineId) {
      this._lineId = lineId;
   }

   void addressBookUpdated(int updateType, Object o) {
      if (this.getType() != 4) {
         CallerIDInfo cidi = this.getCallerIDInfo();
         if (cidi != null) {
            Object address = cidi.getAddress();
            if (address == null || address.equals(o)) {
               this.ungroupPayload();
               cidi = this.getCallerIDInfo();
               cidi.addressBookUpdated(updateType, o);
               cidi.reCrypt(true, true);
               this.groupPayload();
            }
         }
      }
   }

   String getNotes() {
      if (this.canHaveNotes()) {
         BodyModel notesModel = (BodyModel)this.getAt(2);
         if (notesModel != null) {
            return notesModel.getText();
         }
      }

      return null;
   }

   boolean hasNotes() {
      String notes = this.getNotes();
      return notes != null && notes.length() > 0;
   }

   boolean isDirectConnectType() {
      switch (this._type) {
         case 4:
            return false;
         case 5:
         case 6:
         case 7:
         default:
            return true;
      }
   }

   boolean isConferenceCallLog() {
      return this._type == 4;
   }

   boolean isUnknownNumber() {
      return this.getCallerIDInfo().isUnknownNumber();
   }

   public boolean isIncoming() {
      switch (this._type) {
         case -1:
         case 1:
         case 4:
            return false;
         case 0:
         case 2:
         case 3:
         case 5:
         case 6:
         default:
            return true;
      }
   }

   public void logAsMissed() {
      PhoneFolders.logMissedCall(this.getCallerIDInfo(), this._lineId);
   }

   protected Verb getForwardCallLogVerb() {
      return this.getForwardCallLogVerb(602880);
   }

   protected Verb getForwardCallLogVerb(int ordering) {
      return new PhoneCallModelImpl$DoSomethingToThePhoneCallModelVerb(this, 3, ordering, PhoneResources.getResourceBundle(), 426);
   }

   public void updateElapsedTime(int elapsedTime) {
      if (this._payload.setElapsedTime(elapsedTime)) {
         this.notifyListeners();
      }
   }

   public void groupPayload() {
      if (!this._payload.isReadOnly()) {
         this._payload = (PhoneCallPayloadModel)this._payload.makeReadOnly();
      }
   }

   public void ungroupPayload() {
      if (this._payload.isReadOnly()) {
         this._payload = (PhoneCallPayloadModel)this._payload.makeReadWrite();
      }
   }

   public void updateCallType(byte type) {
      if (this._type != type) {
         this._type = type;
         this.notifyListeners();
      }
   }

   int paintIcon(Graphics g, int x, int y, int iconIndex) {
      IconCollection icons = PhoneResources.getIcons();
      return icons.paint(g, x, y, iconIndex);
   }

   public int paintIcon(Graphics g, int x, int y) {
      return this.paintIcon(g, x, y, this.getTypeIconIndex());
   }

   StringBuffer getDurationString(boolean includeLabel) {
      StringBuffer buf = (StringBuffer)(new Object());
      if (includeLabel) {
         buf.append(CommonResources.getString(2003));
      }

      DateTimeUtilities.formatElapsedTime(this.getElapsedTime(), buf, false);
      return buf;
   }

   StringBuffer getDateTimeString(int flags) {
      StringBuffer buffer = WeakReferenceUtilities.getStringBuffer(_dateTimeBufferWR);
      synchronized (buffer) {
         buffer.setLength(0);
         DateFormat dateFormat = null;
         long timestamp = this.getTimeStamp();
         if ((flags & 2) != 0) {
            long currentTime = System.currentTimeMillis();
            if (DateTimeUtilities.isSameDate(currentTime, timestamp, TimeZone.getDefault(), null)) {
               dateFormat = _dateTimeFormatTimeOnly;
            } else {
               if ((flags & 32) != 0) {
                  _shortDateFormatCal.setTimeLong(timestamp);
                  return _dateTimeFormatShortDate.format(_shortDateFormatCal, buffer, null);
               }

               dateFormat = _dateTimeFormatDateOnly;
            }
         } else {
            if ((flags & 8) != 0) {
               buffer.setLength(0);
               _shortDateFormatCal.setTimeLong(timestamp);
               _dateTimeFormatShortDate.format(_shortDateFormatCal, buffer, null);
               buffer.append(' ');
               _dateTimeFormatTimeOnly.formatLocal(buffer, timestamp);
               return buffer;
            }

            if ((flags & 16) != 0) {
               dateFormat = _dateTimeFormatDateOnly;
            } else {
               dateFormat = _dateTimeFormatDateAndTime;
            }
         }

         dateFormat.formatLocal(buffer, timestamp);
         return buffer;
      }
   }

   Object getStatusString() {
      StringBuffer buf = (StringBuffer)(new Object());
      if (this._type == 5 || this._type == 6) {
         buf.append(PhoneResources.getString(6020));
         buf.append(this._payload.getElapsedTime());
         return buf;
      }

      if (this._payload.getElapsedTime() == 0) {
         switch (this._type) {
            case 0:
               break;
            case 1:
               switch (this._errorCode) {
                  case 0:
                     buf.append(PhoneResources.getString(169));
                     buf.append(PhoneResources.getString(123));
                     return buf.length() > 0 ? buf : "0:00";
                  default:
                     return buf.length() > 0 ? buf : "0:00";
               }
            case 2:
            case 3:
            default:
               buf.append(PhoneResources.getString(169));
               buf.append(PhoneResources.getString(this._type == 2 ? 125 : 126));
         }

         return buf.length() > 0 ? buf : "0:00";
      } else {
         return this.getDurationString(false);
      }
   }

   String getNumberTypeString() {
      switch (this.getType()) {
         case -1:
            return null;
         case 0:
         case 1:
         case 2:
         case 3:
         default:
            CallerIDInfo cidi = this.getCallerIDInfo();
            return cidi.getNumberTypeString();
      }
   }

   String getTypeString() {
      return PhoneResources.getCallTypeString(this._type);
   }

   public String getCallerIDString() {
      CallerIDInfo cidi = this.getCallerIDInfo();
      if (cidi != null) {
         if (cidi.addressLookupBlockedByContentProtection() && PersistentContent.getTicket() != null) {
            PhoneCallModelImpl newCallLog = PhoneFolders.updateCallerIDInfo(this, false);
            cidi = newCallLog.getCallerIDInfo();
         }

         return cidi.getDisplayString(false, true);
      } else {
         return null;
      }
   }

   public CallerIDInfo getCallerIDInfo(boolean contentProtectionUpdate) {
      CallerIDInfo cidi = this.getCallerIDInfo();
      if (cidi.addressLookupBlockedByContentProtection() && PersistentContent.getTicket() != null) {
         PhoneCallModelImpl newCallLog = PhoneFolders.updateCallerIDInfo(this, false);
         cidi = newCallLog.getCallerIDInfo();
      }

      return cidi;
   }

   public CallerIDInfo getCallerIDInfo() {
      return (CallerIDInfo)this._payload.getAt(3);
   }

   public void setRefId(int refId) {
      this._refId = refId;
   }

   public boolean isVoicemailCallLog() {
      CallerIDInfo cidi = this.getCallerIDInfo();
      return cidi != null && cidi.isVoicemailCallerIDInfo();
   }

   public void addSyncSubmembers(SyncBuffer syncBuffer) {
   }

   @Override
   public Object getDefault(Object current, Object context) {
      RIMModel callerIDInfo = this.getCallerIDInfo();
      if (!(callerIDInfo instanceof Object)) {
         return null;
      }

      DefaultProvider defaultProvider = (DefaultProvider)callerIDInfo;
      return defaultProvider.getDefault(current, context);
   }

   @Override
   public Object updateDefault(Object newdefault, Object context) {
      return null;
   }

   @Override
   public long getFolderId() {
      return PhoneFolders.getPhoneFolder(this).getLUID();
   }

   @Override
   public void setFolderId(long id) {
   }

   @Override
   public int size() {
      return this._payload.size();
   }

   @Override
   public void removeAll() {
      this.setTimeStamp(0);
      this.setElapsedTime(0);
      this._payload.removeAll();
   }

   @Override
   public void setType(byte type) {
      this._type = type;
      this._payload.setType(this._type);
   }

   @Override
   public Field getField(Object context) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      if (!contextObject.getFlag(58)) {
         if (contextObject.getFlag(35) && this.canHaveNotes()) {
            RIMModel memoModel = (RIMModel)this._payload.getAt(2);
            if (memoModel instanceof Object) {
               FieldProvider fieldProvider = (FieldProvider)memoModel;
               return fieldProvider.getField(context);
            }
         }

         return null;
      } else {
         VerticalFieldManager callerIDsManager = (VerticalFieldManager)(new Object());
         int size = this.size();
         if (this._type == 4) {
            ContextObject.setFlag(context, 80);
            ContextObject.setFlag(context, 59);
         }

         int participantCount = 0;

         for (int i = 3; i < size && this._payload.getAt(i) instanceof CallerIDInfo; i++) {
            participantCount++;
         }

         if (participantCount > 1) {
            ContextObject.setPrivateFlag(context, 4936088360624690805L, 12);
         } else {
            ContextObject.setFlag(context, 26);
         }

         boolean oldCompanyInfoFlag = ContextObject.getFlag(context, 118);
         boolean clearedCompanyInfoFlag = false;
         if (participantCount > 1 && ContextObject.getFlag(context, 24)) {
            String heading = PhoneResources.getString(174);
            callerIDsManager.add((Field)(new Object(heading)));
            callerIDsManager.add((Field)(new Object(2)));
            contextObject.clearFlag(118);
            clearedCompanyInfoFlag = true;
         }

         if (participantCount == 1) {
            CallerIDInfo callerIDInfo = (CallerIDInfo)this._payload.getAt(3);
            Field field = callerIDInfo.getField(context);
            if (field != null) {
               callerIDsManager.add(field);
            }
         } else {
            ContextObject.setFlag(context, 34);
            CallerIDInfo[] callerIDs = new CallerIDInfo[0];

            for (int i = 0; i < participantCount; i++) {
               CallerIDInfo callerIDInfo = (CallerIDInfo)this._payload.getAt(3 + i);
               if (callerIDInfo != null) {
                  Array.resize(callerIDs, callerIDs.length + 1);
                  callerIDs[callerIDs.length - 1] = callerIDInfo;
               }
            }

            ObjectListField callerIDListField = (ObjectListField)(new Object(2305843009213693960L));
            callerIDListField.set(callerIDs);
            callerIDsManager.add(callerIDListField);
         }

         if (oldCompanyInfoFlag && clearedCompanyInfoFlag) {
            contextObject.setFlag(118);
         }

         return callerIDsManager;
      }
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public void remove(Object submember) {
      if (!(submember instanceof TimeModel)) {
         this._payload.remove(submember);
      } else {
         TimeModel timeModel = (TimeModel)submember;
         switch (timeModel.getFormat()) {
            case 0:
               break;
            case 1:
            default:
               this.setTimeStamp(0);
               return;
            case 2:
               this.setElapsedTime(0);
               return;
         }
      }
   }

   @Override
   public boolean contains(Object element) {
      return this.getIndex(element) != -1;
   }

   @Override
   public void add(Object submember) {
      if (!(submember instanceof TimeModel)) {
         this._payload.add(submember);
      } else {
         TimeModel timeModel = (TimeModel)submember;
         switch (timeModel.getFormat()) {
            case 0:
               break;
            case 1:
            default:
               this.setTimeStamp(timeModel.getTime());
               return;
            case 2:
               this.setElapsedTime((int)timeModel.getTime());
               return;
         }
      }
   }

   @Override
   public int getIndex(Object element) {
      return ReadableListUtil.getIndex(element, this);
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public Object getAt(int index) {
      return this._payload.getAt(index);
   }

   @Override
   public void paint(ColumnPainter painter, Object context) {
      painter.setPriority(this.getPaintPriority());
      painter.drawIcon(1, PhoneResources.getIcons(), this.getTypeIconIndex());
      painter.drawTime(2, this._payload.getTimeStamp());
      CallerIDInfo cidi = this.getCallerIDInfo();
      if (cidi != null) {
         if (cidi.addressLookupBlockedByContentProtection() && PersistentContent.getTicket() != null) {
            PhoneCallModelImpl newCallLog = PhoneFolders.updateCallerIDInfo(this, false);
            cidi = newCallLog.getCallerIDInfo();
         }

         String cidiString = cidi.getDisplayString(false, true);
         if (cidiString != null && cidiString.length() > 0) {
            painter.drawText(3, cidiString, false);
         }
      }

      if (this.isUnopened()) {
         painter.setEmphasis(true);
      }

      painter.drawText(4, this.getTypeString(), false);
   }

   @Override
   public byte getVisibilityFlags() {
      byte flags = 21;
      if (this.isNew()) {
         flags = (byte)(flags | 8);
      }

      return flags;
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested != -7628247220259263034L && keyRequested != 92199951187614847L) {
         return 0;
      }

      keyArray[index] = this._payload.getTimeStamp();
      return 1;
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      if (keyRequested == -4145532165335996154L) {
         CallerIDInfo callerIDInfo = this.getCallerIDInfo();
         if (callerIDInfo != null) {
            return callerIDInfo.getKeys(context, keyArray, index, keyRequested);
         }
      }

      return 0;
   }

   @Override
   public int getOrder(Object context) {
      return 15000;
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public void updateCallerIDInfo(Object callerIDInfo) {
      this._payload.updateCallerIDInfo(callerIDInfo);
   }

   @Override
   public int getRefId() {
      return this._refId;
   }

   @Override
   public byte getType() {
      return this._type;
   }

   @Override
   public void setElapsedTime(int elapsedTime) {
      this._payload.setElapsedTime(elapsedTime);
   }

   @Override
   public void addCallerIDInfo(Object callerIDInfo) {
      this._payload.addCallerIDInfo(callerIDInfo);
   }

   @Override
   public boolean isUnopened() {
      switch (this._type) {
         case 2:
         case 5:
            return true;
         default:
            return false;
      }
   }

   @Override
   public boolean canPurge(int purgeType) {
      switch (purgeType) {
         case -1:
            return false;
         case 0:
         default:
            if (!this.hasNotes()) {
               return true;
            }

            return false;
         case 1:
            return true;
         case 2:
            return !this.hasNotes();
      }
   }

   @Override
   public void purge(int purgeType) {
      LowMemoryManager.markAsRecoverable(this);
      this.delete();
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      if (this._payload instanceof Object) {
         EncryptableProvider encryptable = this._payload;
         if (!encryptable.checkCrypt(compress, encrypt)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      if (this._payload instanceof Object) {
         EncryptableProvider encryptable = this._payload;
         Object newPayload = encryptable.reCrypt(compress, encrypt);
         if (newPayload != null) {
            this._payload = (PhoneCallPayloadModel)newPayload;
         }
      }

      return null;
   }

   @Override
   public int getLineId() {
      return this._lineId;
   }

   @Override
   public boolean isNew() {
      return this._newFlag;
   }

   @Override
   public void setErrorCode(int errorCode) {
      this._errorCode = errorCode;
   }

   private void bulkMarkOld() {
      if (this.isNew()) {
         this.setNew(false);
         PersistentObject.commit(this);
         VoiceUnopenedCount.getInstance().itemBulkMarkedOld();
         this.notifyListeners();
      }
   }

   private boolean canMarkOpened() {
      switch (this._type) {
         case 2:
            return true;
         default:
            return false;
      }
   }

   private boolean canHaveNotes() {
      switch (this._type) {
         case 4:
            return true;
         case 5:
         case 6:
         case 7:
         default:
            return false;
      }
   }

   private void notifyListeners() {
      Folder phoneFolder = PhoneFolders.getPhoneFolder(this);
      if (phoneFolder != null) {
         CollectionListener collection = (CollectionListener)phoneFolder.getContainedItems();
         MessagingUtil.robustElementUpdated(collection, this);
      }
   }

   private void delete() {
      PhoneFolders.removeItem(this);
   }

   protected PhoneCallModelImpl(Object initialData) {
      if (initialData instanceof PhoneCallInitialData) {
         this.constructInstance((PhoneCallInitialData)initialData);
      } else {
         this.constructInstance(new PhoneCallInitialData(0, (byte)0, 0, null, null));
      }
   }

   private static void viewCallLog(PhoneCallModelImpl callLog, Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      PhoneCallModelImpl callLogToView = callLog;
      CallerIDInfo callerIDInfo = callLog.getCallerIDInfo();
      if (callerIDInfo.addressLookupBlockedByContentProtection() && PersistentContent.getTicket() != null) {
         callLogToView = PhoneFolders.updateCallerIDInfo(callLogToView, callLogToView.canMarkOpened());
      } else if (callLogToView.canMarkOpened()) {
         callLogToView = markOpened(callLogToView);
      }

      PhoneUtilities.setPrivateFlag(contextObject, 44);
      CallLogViewerScreen screen = new CallLogViewerScreen(PhoneResources.getString(173), contextObject);
      screen.setModel(callLogToView);
      screen.go(false);
   }

   private void constructInstance(PhoneCallInitialData data) {
      this._payload = new PhoneCallPayloadModel(data);
      this.setType(data._type);
      this._refId = (int)this._payload.getTimeStamp();
      this._uid = UIDGenerator.getUID();
      this._lineId = data._lineId;
   }

   private void markOld() {
      this.markOld(true);
   }

   private Verb getCallerIDVerbs(CallerIDInfo cidi, Verb[] verbs, Object context) {
      if (cidi != null) {
         Verb[] cidiVerbs = new Object[0];
         Verb defaultVerb = cidi.getVerbs(context, cidiVerbs);
         if (cidiVerbs.length > 0) {
            int origCount = verbs.length;
            Array.resize(verbs, verbs.length + cidiVerbs.length);
            System.arraycopy(cidiVerbs, 0, verbs, origCount, cidiVerbs.length);
         }

         return defaultVerb;
      } else {
         return null;
      }
   }

   public static void updateDateTimeFormats() {
      _dateTimeFormatDateOnly = DateFormat.getInstance(56);
      _dateTimeFormatTimeOnly = DateFormat.getInstance(7);
      _dateTimeFormatDateAndTime = DateFormat.getInstance(63);
      _dateTimeFormatShortDate = (SimpleDateFormat)(new Object(PhoneResources.getString(6329)));
   }

   private void forwardCallLog() {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(6270925675423896343L);
      Verb[] composeVerbs = verbRepository.getVerbs(-2985347935260258684L);
      if (composeVerbs != null && composeVerbs.length > 0) {
         StringBuffer _emailBodyTextBuffer = WeakReferenceUtilities.getStringBuffer(_emailBodyTextBufferWR);
         ContextObject contextObject = (ContextObject)(new Object(43, 24));
         if (this.convert(contextObject, _emailBodyTextBuffer)) {
            contextObject.clearFlag(43);
            contextObject.put(-1188330299125235844L, PhoneResources.getString(135));
            contextObject.put(-8478555129720928586L, _emailBodyTextBuffer.toString());
            composeVerbs[0].invoke(contextObject);
         }
      }
   }

   private void markOld(boolean commit) {
      if (this.isNew()) {
         this.setNew(false);
         if (commit) {
            PersistentObject.commit(this);
         }

         VoiceUnopenedCount.getInstance().itemMarkedOld();
         this.notifyListeners();
      }
   }

   private static PhoneCallModelImpl markOpened(PhoneCallModelImpl model) {
      model.markOld(false);
      switch (model._type) {
         case 2:
            model = PhoneFolders.changeCallLogType(model, (byte)3);
         default:
            PersistentObject.commit(model);
            return model;
      }
   }

   private static PhoneCallModelImpl markUnopened(PhoneCallModelImpl model) {
      switch (model._type) {
         case 3:
            model = PhoneFolders.changeCallLogType(model, (byte)2);
         default:
            PersistentObject.commit(model);
            return model;
      }
   }

   protected PhoneCallModelImpl() {
      this._payload = new PhoneCallPayloadModel();
   }

   private int getTypeIconIndex() {
      switch (this._type) {
         case -1:
            return 0;
         case 0:
         default:
            return 0;
         case 1:
            return 1;
         case 2:
            return 2;
         case 3:
            return 3;
         case 4:
            return 4;
         case 5:
            return 5;
         case 6:
            return 6;
         case 7:
            return 7;
      }
   }

   private boolean canMarkUnopened() {
      switch (this._type) {
         case 3:
            return true;
         default:
            return false;
      }
   }

   private byte getPaintPriority() {
      if (this._type == 5) {
         int count = this._payload.getElapsedTime();
         if (count > 1) {
            return 2;
         }
      }

      return 1;
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof PhoneCallModelImpl) {
         CallerIDInfo thisAddress = this.getCallerIDInfo();
         CallerIDInfo thatAddress = ((PhoneCallModelImpl)o).getCallerIDInfo();
         if (thisAddress != null) {
            return CallerIDInfo.callerIDEqual(thisAddress, thatAddress);
         }
      }

      return false;
   }

   static {
      updateDateTimeFormats();
   }
}
