package net.rim.device.apps.internal.sms;

import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.io.SmsAddress;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.notification.NotificationsEngineListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;
import net.rim.device.cldc.io.daemon.TransportRegistry;
import net.rim.device.cldc.io.sms.SmsUtil;
import net.rim.device.cldc.io.sms.Transport;
import net.rim.device.internal.deviceoptions.SMSOptions;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.proxy.Proxy;

public final class SMSService extends Thread implements DatagramStatusListener, NotificationsEngineListener {
   private ContextObject _newMessageContext = new ContextObject();
   private Object _displayedMessage;
   private ContextObject _immediateContext = new ContextObject();
   private ContextObject _deferredContext;
   private Object[] _smsServiceListeners;
   private DatagramConnectionBase _conn;
   private SMSService$SMSSendThread _sendThread;
   private Vector _sendQueue;
   private SMSService$SingleSMSNotificationDialog _voicemailMessageDialog;
   private static final int MAX_EMAIL_ADDRESS_LENGTH = 120;
   private static final long GUID = 4928152549260665053L;

   public final synchronized void addSMSServiceListener(SMSServiceListener listener) {
      this._smsServiceListeners = ListenerUtilities.addListener(this._smsServiceListeners, listener);
   }

   public final synchronized void removeSMSServiceListener(SMSServiceListener listener) {
      this._smsServiceListeners = ListenerUtilities.removeListener(this._smsServiceListeners, listener);
   }

   final void cancelImmediateNotifications(SMSModel model) {
      if (NotificationsManager.isImmediateEventOccuring(7986617465467730856L)) {
         Proxy.getInstance().invokeLater(new SMSService$2(this, model));
      }
   }

   public final void sendMessage(SMSModel model, boolean onlySendIfErrorStatus) {
      SMSPayloadModel payload = model._payload;
      PersistableRIMModel addressModel = null;
      PersistableRIMModel[] addresses = payload.getAddresses();
      byte[] data = payload.getData();
      if (data != null) {
         for (int i = 0; i < addresses.length; i++) {
            addressModel = addresses[i];
            if (addressModel instanceof GroupAddressCardModel) {
               boolean firstValidAddressInThisGroup = true;
               GroupAddressCardModel group = (GroupAddressCardModel)addressModel;
               int total = group.size();

               for (int j = 0; j < total; j++) {
                  PersistableRIMModel childModel = (PersistableRIMModel)group.getAddressModelAt(j);
                  if (validateAddress(childModel)) {
                     if (firstValidAddressInThisGroup) {
                        model.changeAddress(group, childModel);
                        firstValidAddressInThisGroup = false;
                     } else {
                        model.addAddress(childModel);
                     }
                  }
               }
            }
         }

         for (int i = addresses.length - 1; i > -1; i--) {
            if (!onlySendIfErrorStatus || !SMSModel.isSuccessfullySent(model.getStatus(i))) {
               addressModel = addresses[i];
               String addressString = null;
               if (addressModel instanceof ConversionProvider) {
                  ConversionProvider conversionProvider = (ConversionProvider)addressModel;
                  ContextObject context = new ContextObject();
                  ContextObject.setFlag(context, 21);
                  ContextObject.setFlag(context, 55);
                  if (model.useSmartDialing(i)) {
                     ContextObject.setFlag(context, 117);
                  }

                  StringBuffer buf = new StringBuffer();
                  if (conversionProvider.convert(context, buf)) {
                     addressString = buf.toString();
                  }
               }

               if (addressString == null) {
                  addressString = addressModel.toString();
               }

               int ton = payload.getTON(i);
               int npi = payload.getNPI(i);
               if (npi == 0) {
                  npi = 1;
               }

               SMSPacketHeader header = new SMSPacketHeader();
               header.setPeerAddress(addressString, ton, npi);
               if (payload._scAddress != null) {
                  ton = payload.getByteField(10);
                  npi = payload.getByteField(11);
                  if (npi != 0) {
                     npi = 1;
                  }

                  header.setSCAddress(payload._scAddress, ton, npi);
               }

               header.setMessageCoding(payload.getByteField(0));
               header.setStatusReportRequest(SMSOptions.getDeliveryReports());
               int udhLength = payload._userDataHeader != null ? payload._userDataHeader.length : 0;
               header.setUserDataHeaderPresent(udhLength != 0);

               DatagramBase datagram;
               try {
                  datagram = (DatagramBase)this._conn.newDatagram(data.length + udhLength);
                  datagram.setAddressBase(new SmsAddress(header, null));
                  if (udhLength != 0) {
                     datagram.write(payload._userDataHeader);
                  }

                  datagram.write(data);
                  datagram.setDatagramStatusListener(this);
                  this._conn.allocateDatagramId(datagram);
               } finally {
                  ;
               }

               int datagramID = datagram.getDatagramId();
               MessageLookups.put(-6051701886797080507L, datagramID, model);
               model.setDatagramID(i, datagramID);
               synchronized (this._sendQueue) {
                  this._sendQueue.addElement(datagram);
               }

               model.setStatus(134217727, 0, 0, 0, datagramID);
            }
         }

         long folderId = model._folderId;
         if (folderId != 0) {
            Storage.removeMessage(model);
         }

         long time = System.currentTimeMillis();
         payload._creationDate = time;
         payload._transmissionDate = time;
         if (this._sendThread == null) {
            this._sendThread = new SMSService$SMSSendThread(this);
            ProtocolDaemon.getInstance().startThread(this._sendThread);
         }
      }
   }

   @Override
   public final void proceedWithDeferredEvent(long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object context) {
      if (sourceIdLong == 7986617465467730856L && !Phone.getInstance().isActive() && eventReferenceObject instanceof SMSModel) {
         this._displayedMessage = eventReferenceObject;
         ContextObject contextObject = new ContextObject(64);
         ShowMessageApp.displayMessage((SMSModel)eventReferenceObject, contextObject);
      }
   }

   @Override
   public final void deferredEventWasSuperseded(long soureIdLong, long eventIdLong, Object eventReferenceObject, Object object) {
   }

   @Override
   public final void notificationsEngineStateChanged(int stateInt, long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object contextObject) {
      if (stateInt == 1 && eventReferenceObject == this._displayedMessage && eventReferenceObject != null) {
         this._displayedMessage = null;
         ShowMessageApp.postEvent(-6275418955626563374L, 0, 0, eventReferenceObject, null);
         NotificationsManager.cancelDeferredEvent(sourceIdLong, eventIdLong, eventReferenceObject, 0, contextObject);
      }
   }

   @Override
   public final void updateDatagramStatus(int dgId, int code, Object context) {
      log(1431589699);
      SMSModel message = (SMSModel)MessageLookups.get(-6051701886797080507L, dgId);
      if (message == null) {
         log(1431588422);
      } else {
         ContextObject ctx = new ContextObject();
         Integer datagramIDObject = new Integer(dgId);
         ContextObject.put(ctx, -8210557334250400979L, datagramIDObject);
         int newStatus;
         switch (code) {
            case -1:
            case 3:
               log(1431590227);
               newStatus = 8191;
               if (context instanceof Integer) {
                  Integer intObject = (Integer)context;
                  ctx.putIntegerData(intObject);
               }
               break;
            case 0:
            default:
               newStatus = 33554431;
               break;
            case 1:
               newStatus = 134217727;
               break;
            case 2:
               newStatus = 67108863;
               break;
            case 4:
               newStatus = 1073741823;
               break;
            case 5:
               newStatus = 4194303;
               break;
            case 6:
               newStatus = 2097151;
               break;
            case 7:
               newStatus = 8388607;
         }

         message.changeStatus(0, 0, newStatus, code, true, true, false, ctx);
      }
   }

   private final void triggerNotifications(SMSPacketHeader header, SMSModel model, long id) {
      if (!header.isFromSIMCard() && (model == null || !model.flagsSet(1))) {
         Proxy.getInstance().invokeLater(new SMSService$1(this, model, id));
      }
   }

   SMSService() {
      this._immediateContext.putIntegerData(0);
      this._deferredContext = new ContextObject();
      this._deferredContext.setFlag(65);
      this._sendQueue = new Vector();
      ProtocolDaemon.getInstance().startThread(this);
      ApplicationRegistry.getApplicationRegistry().put(4928152549260665053L, this);
      EventLogger.register(-5553929614158418545L, "net.rim.smsui", 2);
   }

   public static final boolean canSend() {
      return RadioInfo.getState() == 1;
   }

   private final boolean scanInbox(
      SMSPacketHeader header, PersistableRIMModel address, int refNumber, int totalSegments, int segmentNumber, byte[] data, int simID, long folderId
   ) {
      int protocolMeaning = header.getProtocolMeaning();
      int protocolId = header.getProtocolId();
      long timestamp = header.getTimestamp();
      synchronized (FolderHierarchies.getLockObject()) {
         Folder folder = Storage.getSMSFolder(folderId);
         if (folder != null) {
            PersistedSortedCollection collection = (PersistedSortedCollection)folder.getContainedItems();
            ContextObject addressComparisonContext = new ContextObject();
            addressComparisonContext.setFlag(93);

            for (int i = collection.size() - 1; i >= 0; i--) {
               SMSModel model = (SMSModel)collection.getAt(i);
               boolean timestampMatches = model._payload._transmissionDate == timestamp;
               if (refNumber != -1 && refNumber == model._payload._refNumber) {
                  addressComparisonContext.put(254, model._payload.getFirstAddress());
                  if (address.equals(addressComparisonContext)) {
                     model.ungroupMessage();
                     boolean wasASegmentOfAnExistingMessage = model.addSegment(totalSegments, segmentNumber, simID, data);
                     if (wasASegmentOfAnExistingMessage) {
                        log(542331480);
                        if (ModelViewListenerRegistry.isViewerUp(0, model, model)) {
                           ModelViewListenerRegistry.notifyOfOpenedModelChange(model, model, null);
                        } else {
                           model.changeStatus(0, 1, 0, 0, true, true, true, null);
                        }
                     } else if (timestampMatches) {
                        log(541672525);
                     }

                     if (wasASegmentOfAnExistingMessage || timestampMatches) {
                        model.groupMessage();
                        PersistentObject.commit(model);
                        return false;
                     }
                  }
               }

               if (simID != 65535 && model._payload.checkSegmentID(simID)) {
                  if (timestampMatches) {
                     log(541672525);
                     return false;
                  }

                  model.ungroupMessage();
                  model._payload._segmentIDs = null;
                  model.delete(null, true);
                  log(1145918291);
                  return true;
               }

               if (model._payload.getByteField(2) == 64 && protocolMeaning == 64 && protocolId == model._payload.getByteField(3)) {
                  addressComparisonContext.put(254, model._payload.getFirstAddress());
                  if (address.equals(addressComparisonContext)) {
                     model.delete(null, true);
                     log(1145918036);
                  }
               }
            }
         }

         return true;
      }
   }

   private final PersistableRIMModel createAddressModel(String addressString, int addressType) {
      if (addressString == null) {
         return null;
      }

      this._newMessageContext.put(253, addressString);
      if (addressType == 5) {
         this._newMessageContext.setFlag(34);
         this._newMessageContext.putIntegerData(13);
      } else {
         this._newMessageContext.clearFlag(34);
      }

      PersistableRIMModel addressModel = (PersistableRIMModel)FactoryUtil.createInstance(
         addressType == 100 ? -2985347935260258684L : 3797587162219887872L, this._newMessageContext
      );
      this._newMessageContext.clearFlag(34);
      this._newMessageContext.remove(-4054673099568009991L);
      this._newMessageContext.remove(253);
      return addressModel;
   }

   private final String getPopupMessage(int count) {
      String message = null;
      return count > 1 ? MessageFormat.format(SMSResources.getString(396), new String[]{"" + count}) : SMSResources.getString(372);
   }

   private final void processIncomingDatagram(DatagramBase datagram) {
      SmsAddress address = (SmsAddress)datagram.getAddressBase();
      SMSPacketHeader header = address.getHeader();
      byte[] data = datagram.getData();
      byte[] userDataHeader = (byte[])datagram.getProperty(SmsUtil.PROPERTY_USER_DATA_HEADER);
      int refNumber = -1;
      int totalSegments = 1;
      int segmentNumber = 1;
      Integer ref = (Integer)datagram.getProperty(SmsUtil.PROPERTY_REF_NUMBER);
      if (ref != null) {
         refNumber = ref;
         ref = (Integer)datagram.getProperty(SmsUtil.PROPERTY_TOTAL_SEGMENTS);
         if (ref != null) {
            totalSegments = ref;
         }

         ref = (Integer)datagram.getProperty(SmsUtil.PROPERTY_SEGMENT_NUMBER);
         if (ref != null) {
            segmentNumber = ref;
         }
      }

      int simID = header.getID();
      boolean storeMessage = true;
      boolean voicemailMessage = false;
      boolean ringtoneMessage = false;
      String popupMessage = decodeSMSData(header.getMessageCoding(), data);
      if (popupMessage != null && popupMessage.trim().length() == 0) {
         popupMessage = null;
      }

      if (header.getMessageClass() == 0) {
         storeMessage = false;
         simID = 65535;
      }

      if (header.getProtocolMeaning() == 64 && header.getProtocolId() == 0) {
         storeMessage = false;
         popupMessage = null;
      }

      int[] ports = address.getPorts();
      if (ports != null) {
         label611:
         try {
            Transport smsTransport = (Transport)TransportRegistry.get("net.rim.device.cldc.io.sms.Transport");
            if (smsTransport.isPortReserved(ports[0])) {
               storeMessage = false;
               popupMessage = null;
            }
         } finally {
            break label611;
         }

         switch (ports[0]) {
            case 2948:
            case 65536:
            case 65552:
               storeMessage = false;
               popupMessage = null;
         }
      }

      if (header.getProtocolMeaning() == 255) {
         storeMessage = false;
         popupMessage = null;
      }

      int peerAddressType = header.getPeerType();
      String unfilteredPeerAddressString = header.getPeerAddress();
      String peerAddressString = unfilteredPeerAddressString;
      if (peerAddressType != 100) {
         peerAddressString = addressFilter(peerAddressString);
      }

      PersistableRIMModel peerAddress = this.createAddressModel(peerAddressString, peerAddressType);
      PersistableRIMModel callbackAddress = this.createAddressModel(header.getCallbackAddress(), header.getCallbackType());
      boolean fromSIM = header.isFromSIMCard();
      if (simID != 65535 && !SMSOptions.getStoreOnSIM() && !fromSIM) {
         SIMManager.getInstance().deleteSMSMessage(simID);
         simID = 65535;
      }

      long folderId = simID == 65535 ? 1393133342214151287L : -441701525336570016L;
      if (this.scanInbox(header, peerAddress, refNumber, totalSegments, segmentNumber, data, simID, folderId)) {
         if (!SMSOptions.getStoreOnSIM()
            || folderId != 1393133342214151287L
            || this.scanInbox(header, peerAddress, refNumber, totalSegments, segmentNumber, data, simID, -441701525336570016L)) {
            log(541938264);
            if (this._smsServiceListeners != null) {
               Object[] listeners = this._smsServiceListeners;

               for (int i = listeners.length - 1; i >= 0; i--) {
                  SMSServiceListener listener = (SMSServiceListener)listeners[i];

                  try {
                     if (listener.smsMessageReceived(header, data, userDataHeader, ports)) {
                        storeMessage = false;
                        popupMessage = null;
                     }
                  } finally {
                     continue;
                  }
               }
            }

            if (!fromSIM) {
               if (unfilteredPeerAddressString != null && unfilteredPeerAddressString.length() == 2 && peerAddressType == 5 && header.getPeerPlan() == 0) {
                  byte character1 = (byte)unfilteredPeerAddressString.charAt(0);
                  byte character2 = (byte)unfilteredPeerAddressString.charAt(1);
                  if (character2 == 0 || character2 == 1) {
                     voicemailMessage = true;
                     if (data.length == 1 && data[0] == 32) {
                        storeMessage = false;
                        popupMessage = null;
                     }

                     int ind;
                     if ((character2 & 1) == 0) {
                        ind = 1;
                     } else {
                        ind = 2;
                     }

                     int state = VoicemailIconManager.getCPHSState();
                     if ((character1 & 1) == 1) {
                        state |= ind;
                        if (!storeMessage) {
                           int msgCount = 1;
                           if (userDataHeader != null && userDataHeader.length > 3 && userDataHeader[0] == 1) {
                              msgCount = userDataHeader[3] & 255;
                           }

                           popupMessage = this.getPopupMessage(msgCount);
                        }

                        log(1129729870);
                     } else {
                        state &= ~ind;
                        log(1129729862);
                     }

                     if (VoicemailIconManager.isMwisEnabled()) {
                        int indicator = ind == 2 ? 32 : 0;
                        int count = (character1 & 1) == 1 ? -1 : 0;
                        VoicemailIconManager.setState(indicator, count);
                     }

                     VoicemailIconManager.setCPHSState(state);
                  }
               }

               if (header.isMessageWaitingGroup()) {
                  voicemailMessage = true;
                  if (!header.isMessageWaitingStore()) {
                     storeMessage = false;
                  }

                  int numMessages = 0;
                  if (header.isMessageWaitingActive()) {
                     numMessages = header.getNumMessages();
                     if (numMessages == 0) {
                        numMessages = -1;
                     }

                     if (!storeMessage && popupMessage == null) {
                        popupMessage = this.getPopupMessage(numMessages);
                     }

                     log(1297567566);
                  } else {
                     if (!storeMessage) {
                        popupMessage = null;
                     }

                     log(1297567558);
                  }

                  VoicemailIconManager.setState(header.getMessageWaitingType(), numMessages);
               }

               if (userDataHeader != null && userDataHeader.length > 0) {
                  int i = 0;

                  while (i < userDataHeader.length) {
                     byte type = userDataHeader[i++];
                     int len = userDataHeader[i++] & 255;
                     switch (type) {
                        case 1:
                           int msgWaitingType = -1;
                           log(1430539607);
                           if (len >= 1) {
                              int msgWaitingTypeAndStorageInd = userDataHeader[i++] & 255;
                              if ((msgWaitingTypeAndStorageInd & 128) == 0) {
                                 storeMessage = false;
                              }

                              msgWaitingType = msgWaitingTypeAndStorageInd & 127;
                           }

                           int msgCount = -1;
                           if (len >= 2) {
                              msgCount = userDataHeader[i++] & 255;
                              if (!storeMessage) {
                                 if (msgCount != 0) {
                                    if (popupMessage == null) {
                                       popupMessage = this.getPopupMessage(msgCount);
                                    }
                                 } else {
                                    popupMessage = null;
                                 }
                              }
                           }

                           if (msgWaitingType != -1) {
                              voicemailMessage = msgWaitingType == 0;
                              VoicemailIconManager.setState(msgWaitingType, msgCount);
                           }
                           break;
                        default:
                           i += len;
                     }
                  }
               }
            }

            int recordStatus = header.getRecordStatus();
            if ((recordStatus & 2) == 0) {
               this._newMessageContext.setFlag(104);
            } else {
               this._newMessageContext.clearFlag(104);
            }

            if ((recordStatus & 4) == 0) {
               this._newMessageContext.setFlag(38);
               byte var52 = 0;
               if (voicemailMessage) {
                  var52 = 1;
               } else if (ringtoneMessage) {
                  var52 = 2;
               }

               Firewall fw = Firewall.getInstance();
               if (fw.isBlockingEnabled((byte)1) && (popupMessage != null || storeMessage)) {
                  popupMessage = null;
                  storeMessage = false;
                  fw.incrementBlockedCount((byte)1);
               }

               if (!storeMessage) {
                  this._newMessageContext.setFlag(104);
               }

               SMSModel model = SMSModelFactory.createSMSModel(var52, this._newMessageContext);
               SMSPayloadModel payload = model._payload;
               payload._imsiCRC = SIMCard.getIMSICRC();
               model.addAddress(peerAddress, header.getPeerType(), header.getPeerPlan());
               payload.setCallbackAddress(callbackAddress);
               payload.setByteFields(header);
               payload._transmissionDate = header.getTimestamp();
               if (header.isReplyPath()) {
                  payload._scAddress = header.getSCAddress();
               }

               payload._refNumber = refNumber;
               payload._userDataHeader = userDataHeader;
               model.addSegment(totalSegments, segmentNumber, simID, data);
               model.groupMessage();
               if (storeMessage) {
                  Storage.fileMessage(model, folderId);
                  LowMemoryManager.poll();
                  this.triggerNotifications(header, model, model.getUID());
               } else {
                  if (popupMessage != null) {
                     long notificationId = header.getTimestamp();
                     if (voicemailMessage) {
                        synchronized (Application.getApplication().getAppEventLock()) {
                           if (this._voicemailMessageDialog != null) {
                              this._voicemailMessageDialog.saveMessage(false);
                              this._voicemailMessageDialog.resetDialog(model, folderId, popupMessage, this._immediateContext, notificationId);
                           } else {
                              this._voicemailMessageDialog = new SMSService$SingleSMSNotificationDialog(
                                 this, model, folderId, popupMessage, this._immediateContext, notificationId
                              );
                              this._voicemailMessageDialog.show();
                           }
                        }
                     } else {
                        new SMSNotificationDialog(model, folderId, popupMessage, this._immediateContext, notificationId).show();
                     }

                     this.triggerNotifications(header, null, notificationId);
                  }

                  SIMManager.getInstance().deleteSMSMessage(simID);
                  log(541937235);
               }
            } else {
               log(1297042761);
            }
         }
      }
   }

   public static final String decodeSMSData(int messageCoding, byte[] data, boolean stk) {
      if (stk && messageCoding == 1) {
         messageCoding = 0;
      }

      try {
         if (data != null) {
            return new String(data, getSmsEncoder(messageCoding));
         }
      } finally {
         return null;
      }

      return null;
   }

   public static final String decodeSMSData(int messageCoding, byte[] data) {
      return decodeSMSData(messageCoding, data, false);
   }

   public static final String getSmsEncoder(int messageCoding) {
      switch (messageCoding) {
         case 0:
            return "SMS";
         case 2:
            return "UnicodeBigUnmarked";
         case 5:
            return "ISO8859_1";
         case 6:
            return "KSC5601";
         default:
            return "ASCII";
      }
   }

   private static final String addressFilter(String phoneNumber) {
      return phoneNumber != null && !phoneNumber.equals("+") && !phoneNumber.equals("0000000000") ? phoneNumber : "";
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 1
      // 01: aconst_null
      // 02: aconst_null
      // 03: invokestatic net/rim/device/api/io/SmsAddress.makeAddress (ZLnet/rim/device/api/system/SMSPacketHeader;[I)Ljava/lang/String;
      // 06: astore 2
      // 07: aload 0
      // 08: aload 2
      // 09: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 0c: checkcast net/rim/device/api/io/DatagramConnectionBase
      // 0f: putfield net/rim/device/apps/internal/sms/SMSService._conn Lnet/rim/device/api/io/DatagramConnectionBase;
      // 12: aload 0
      // 13: getfield net/rim/device/apps/internal/sms/SMSService._conn Lnet/rim/device/api/io/DatagramConnectionBase;
      // 16: bipush 0
      // 17: invokevirtual net/rim/device/api/io/DatagramConnectionBase.newDatagram (I)Ljavax/microedition/io/Datagram;
      // 1a: checkcast net/rim/device/api/io/DatagramBase
      // 1d: astore 1
      // 1e: goto 29
      // 21: astore 2
      // 22: ldc_w 1129205574
      // 25: invokestatic net/rim/device/apps/internal/sms/SMSService.log (I)V
      // 28: return
      // 29: aload 0
      // 2a: getfield net/rim/device/apps/internal/sms/SMSService._conn Lnet/rim/device/api/io/DatagramConnectionBase;
      // 2d: aload 1
      // 2e: invokevirtual net/rim/device/api/io/DatagramConnectionBase.receive (Ljavax/microedition/io/Datagram;)V
      // 31: aload 0
      // 32: aload 1
      // 33: invokespecial net/rim/device/apps/internal/sms/SMSService.processIncomingDatagram (Lnet/rim/device/api/io/DatagramBase;)V
      // 36: goto 29
      // 39: astore 2
      // 3a: ldc_w 1381517647
      // 3d: invokestatic net/rim/device/apps/internal/sms/SMSService.log (I)V
      // 40: goto 29
      // 43: astore 2
      // 44: goto 29
      // try (0 -> 16): 17 null
      // try (21 -> 28): 29 null
      // try (21 -> 28): 33 null
   }

   public static final SMSService getInstance() {
      return (SMSService)ApplicationRegistry.getApplicationRegistry().get(4928152549260665053L);
   }

   public static final void log(int id) {
      EventLogger.logEvent(-5553929614158418545L, id, 0);
   }

   public static final boolean isEmailAddressAsSMSAddressSupported() {
      return (RadioInfo.getActiveWAFs() & 2) != 0;
   }

   public static final boolean isSMSToMultipleRecipientsSupported() {
      return SMSOptions.getMultipleRecipients();
   }

   public static final boolean validateAddress(RIMModel address) {
      boolean emailIsValid = isEmailAddressAsSMSAddressSupported();
      if (address instanceof EmailAddressModel && emailIsValid) {
         String addressString = ((EmailAddressModel)address).getAddress();
         if (addressString != null) {
            if (addressString.length() <= 120) {
               return true;
            }

            Status.show(SMSResources.getString(421));
         }

         return false;
      } else {
         if (!(address instanceof GroupAddressCardModel)) {
            if (address instanceof PhoneNumberModel) {
               return true;
            }
         } else {
            GroupAddressCardModel group = (GroupAddressCardModel)address;
            int total = group.size();
            boolean badAddressFound = false;
            boolean goodAddressFound = false;

            for (int i = 0; i < total; i++) {
               switch (group.getAddressModelTypeAt(i)) {
                  case 0:
                     if (emailIsValid) {
                        goodAddressFound = true;
                     } else {
                        badAddressFound = true;
                     }
                     break;
                  case 2:
                     goodAddressFound = true;
                     break;
                  default:
                     badAddressFound = true;
               }
            }

            if (goodAddressFound) {
               if (badAddressFound) {
                  String smsString = SMSResources.getString(610);
                  group.warnUserSomeAddressesCannotReceive(smsString);
               }

               return true;
            }
         }

         return false;
      }
   }
}
