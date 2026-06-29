package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Timer;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.StateProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.model.VisibilityControl;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.messaging.NonpersistedUtilityFolders;
import net.rim.device.apps.api.messaging.messagelist.ForwardAsVerb;
import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.api.utility.lowMemory.PurgeProvider;
import net.rim.device.apps.internal.blackberryemail.EmailSyncState;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolderSync;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.folder.FolderUtil;
import net.rim.device.apps.internal.blackberryemail.folder.RibbonUpdater;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;
import net.rim.device.apps.internal.blackberryemail.header.TimeStampModel;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;
import net.rim.device.apps.internal.blackberryemail.replywithouttextstub.ReplyWithoutTextStubRecognizer;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

public class EmailMessageModelImpl
   implements EmailMessageModel,
   SyncObject,
   VerbProvider,
   ColumnPaintProvider,
   KeyProvider,
   MatchProvider,
   HotKeyProvider,
   ActionProvider,
   FolderProvider,
   StateProvider,
   ConversionProvider,
   PurgeProvider,
   CloneProvider,
   DefaultProvider,
   EncryptableProvider,
   CMIMEReferenceIdProvider,
   VisibilityControl,
   MessagePartsProvider,
   AccessibleContextProxy {
   private int _messageStatus;
   private int _messageTransmissionError;
   String _errorMessageOverride;
   int _cursorPosition;
   private int _messageFlags;
   private int _packedInfo;
   private long _folderId;
   private int _attachmentCount;
   private byte _resendAttempts;
   public EmailPayloadModelImpl _payload;
   private int _GMEReferenceIdentifier;
   private static final int DELETE_ON_HANDHELD;
   private static final int DELETE_ON_HANDHELD_AND_DESKTOP;
   private static final int INITIAL_MESSAGE_CREATE;
   private static final long PRIVATE_FLAG_KEY;
   private static final long LAST_DELETE_LOCATION_ID_KEY;
   private static int _lastDeleteLocationID = PersistentInteger.getId(-6814995600535137073L, 1);
   static final String EXCEPTION_ONLYRIMMODELS;
   private static final int PACKEDINFO_PRIORITY_SHIFT;
   private static final int PACKEDINFO_PRIORITY_MASK;
   private static final int PACKEDINFO_NOTIFICATION_SHIFT;
   private static final int PACKEDINFO_NOTIFICATION_MASK;
   private static final int PACKEDINFO_MSGTYPE_SHIFT;
   private static final int PACKEDINFO_MSGTYPE_MASK;
   private static final int PACKEDINFO_SENSITIVITY_SHIFT;
   private static final int PACKEDINFO_SENSITIVITY_MASK;
   private static final int[] RESEND_DELAY_TIMES = new int[]{300, 300, 1200, 1800, -804651007, 51, -804651004, 63, 64, 65, 66, -804651004, 67, 68, 69, 74};
   private static Timer _resendTimer = (Timer)(new Object());
   private static final int MESSAGE_HEADER_SIZE;
   private static WeakReference _messageHeaderWR = (WeakReference)(new Object(null));
   private static WeakReference _folderDataBufferWR = (WeakReference)(new Object(null));
   static int[] _hints = new int[0];
   private static ContextObject _purgeStaleMessagesContext;

   @Override
   public int getCMIMEReferenceIdentifier() {
      return this._payload.getCMIMEReferenceIdentifier();
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         if (ContextObject.getFlag(context, 12) && this.inbound() && this.canBeRepliedTo() && EmailMessageUtilities.allowOutboundMessages(this)) {
            Verb replyVerb = this.getEmailReplyVerb(false);
            if (replyVerb != null) {
               Array.resize(verbs, verbs.length + 1);
               verbs[verbs.length - 1] = replyVerb;
            }
         }

         if (ContextObject.getFlag(context, 13) && this.canBeForwarded() && EmailMessageUtilities.allowOutboundMessages(this)) {
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = this.getEmailForwardVerb();
            ForwardAsVerb forwardAsVerb = (ForwardAsVerb)(new Object(this));
            if (forwardAsVerb.canInvoke(null)) {
               Array.resize(verbs, verbs.length + 1);
               verbs[verbs.length - 1] = forwardAsVerb;
            }
         }

         return null;
      } else {
         int numberOfVerbsUsed = 0;
         Array.resize(verbs, 12);
         int defaultVerbNumber = -1;
         if (!ContextObject.getFlag(context, 5)) {
            if (!this.inbound()) {
               if (!ContextObject.getFlag(context, 78)) {
                  verbs[numberOfVerbsUsed++] = new SearchAddressVerb(
                     this, 16861472, MessageResources.getBundle(), 111, RecognizerRepository.getRecognizers(3306251366082544277L)
                  );
               }

               if (!this.isSuccessfullySent()
                  && !this.isDraft()
                  && EmailMessageUtilities.allowOutboundMessages(this)
                  && (this.flagsSet(8192) || EmailMessageUtilities.getServiceRecordForMessage(this) != null)) {
                  verbs[numberOfVerbsUsed++] = new EmailResendVerb(this);
               }
            } else {
               if (this.canBeRepliedTo() && EmailMessageUtilities.allowOutboundMessages(this)) {
                  Verb replyVerb = this.getEmailReplyVerb(false);
                  if (replyVerb != null) {
                     verbs[numberOfVerbsUsed++] = replyVerb;
                  }
               }

               if (!ContextObject.getFlag(context, 78)) {
                  verbs[numberOfVerbsUsed++] = new SearchAddressVerb(
                     this, 16861456, MessageResources.getBundle(), 112, RecognizerRepository.getRecognizers(-1249752217278100236L)
                  );
               }

               if (this.flagsSet(1)) {
                  verbs[numberOfVerbsUsed++] = new EmailChangeStatusVerb(602450, 18, 0, 1, 0, this);
               } else {
                  verbs[numberOfVerbsUsed++] = new EmailChangeStatusVerb(602448, 17, 1, 0, 0, this);
               }
            }

            defaultVerbNumber = numberOfVerbsUsed;
            verbs[numberOfVerbsUsed++] = this.getEmailOpenVerb(context);
            if (this.canBeForwarded()) {
               verbs[numberOfVerbsUsed++] = this.getEmailForwardVerb();
               ForwardAsVerb forwardAsVerb = (ForwardAsVerb)(new Object(this));
               if (forwardAsVerb.canInvoke(null)) {
                  verbs[numberOfVerbsUsed++] = forwardAsVerb;
               }
            }

            String subject = this.getSubject();
            if (!ContextObject.getFlag(context, 78) && subject != null && subject.length() > 0) {
               verbs[numberOfVerbsUsed++] = new SearchSubjectVerb(this);
            }

            EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(this.getFolderId());
            EmailFolder folder = null;
            if (hierarchy != null) {
               folder = EmailHierarchy.getEmailFolder(this.getFolderId());
               if (folder == null) {
                  folder = (EmailFolder)hierarchy.getFolder(hierarchy.getInboxFolder());
               }
            }

            if (folder != null && folder.isInFolderDatabase()) {
               verbs[numberOfVerbsUsed++] = (Verb)(new Object(this));
            }

            if (!this.flagsSet(16)) {
               verbs[numberOfVerbsUsed++] = new EmailChangeStatusVerb(602480, 19, 16, 0, 0, this);
            }

            Verb[] submemberVerbs = this.getSubmemberVerbs(context);
            if (submemberVerbs != null && submemberVerbs.length > 0) {
               Array.resize(verbs, verbs.length + submemberVerbs.length);
               System.arraycopy(submemberVerbs, 0, verbs, numberOfVerbsUsed, submemberVerbs.length);
               numberOfVerbsUsed += submemberVerbs.length;
            }
         }

         Array.resize(verbs, numberOfVerbsUsed);
         return defaultVerbNumber != -1 ? verbs[defaultVerbNumber] : null;
      }
   }

   @Override
   public int match(Object criteria) {
      if (!(criteria instanceof Object)) {
         try {
            SearchCriterion[] crit = (Object[])criteria;
            return Match.match(this, this, crit, _hints);
         } finally {
            return -1;
         }
      } else {
         SearchCriterion crit = (SearchCriterion)criteria;
         boolean match;
         switch (crit.getType()) {
            case 8:
            case 18:
            case 20:
            case 21:
            case 23:
            case 27:
               return -1;
            case 9:
               match = this.inbound();
               break;
            case 10:
               match = !this.inbound();
               break;
            case 11:
               match = this.flagsSet(16);
               break;
            case 12:
            default:
               match = !this.flagsSet(532480);
               break;
            case 13:
            case 14:
            case 16:
               match = false;
               break;
            case 15:
               match = crit.getValue() == this._folderId;
               break;
            case 17:
               int[] values = (int[])crit.getValue();
               int userId = values[0];
               EmailHierarchy emailHierarchy = EmailHierarchy.getEmailHierarchyForFolder(this._folderId);
               if (userId != -1) {
                  match = userId == emailHierarchy.getServiceUserId();
               } else {
                  match = values[1] == emailHierarchy.getServiceUidHash();
               }
               break;
            case 19:
               match = this.flagsSet(524288);
               break;
            case 22:
               match = this.isDraft();
               break;
            case 24:
               match = crit.getValue() == this.getCMIMEReferenceIdentifier();
               break;
            case 25:
               match = this.flagsSet(8192);
               break;
            case 26:
               match = !this.flagsSet(532480) && this.hasViewableAttachment();
               break;
            case 28:
               match = this.inbound() && !this.flagsSet(1);
         }

         return match ? 1 : 0;
      }
   }

   @Override
   public int getUID() {
      int uid = this.getCMIMEReferenceIdentifier();
      if (this.flagsSet(8192) && this.inbound()) {
         uid = -uid;
      }

      return uid;
   }

   @Override
   public boolean perform(long actionId, Object context) {
      boolean returnValue = false;
      long childActionId = -1;
      if (actionId == 6780594967363292755L) {
         childActionId = -8870086693994175796L;
      }

      if (actionId == -3967872215949752466L) {
         childActionId = -909775579847177570L;
      }

      if (childActionId != -1) {
         int size = this._payload.size();

         for (int i = 0; i < size; i++) {
            Object payloadElement = this._payload.getAt(i);
            if (!(payloadElement instanceof ProxyModel)) {
               if (payloadElement instanceof Object) {
                  RIMModel m = (RIMModel)payloadElement;
                  if (m instanceof Object) {
                     ((ActionProvider)m).perform(childActionId, context);
                  }
               }
            } else {
               Object m = ((ProxyModel)payloadElement).getObject();
               if (m instanceof Object) {
                  ((ActionProvider)m).perform(childActionId, context);
               }
            }
         }
      }

      if (actionId == 6099736323056465049L) {
         EventLogger.logEvent(-1237457833540244999L, 1330660685, 5);
         ContextObject contextObject = ContextObject.castOrCreate(context);
         contextObject.put(424670468422402792L, this);
         this.getEmailOpenVerb(context).invoke(contextObject);
         returnValue = true;
      } else if (actionId == -198247372487919817L) {
         this.performActionsWhenMessageIsReallyAboutToBeBlownAway(context);
      } else if (actionId == -3967872215949752466L) {
         returnValue = this.delete(context, false, true, true);
      } else if (actionId == -6225946334564270161L) {
         returnValue = this.changeStatus(1, 0, 0, 0, true, true, true, false, context);
      } else if (actionId == -8570780006855731756L) {
         returnValue = this.changeStatus(16, 0, 0, 0, true, true, false, false, context);
      } else if (actionId == 5803508244060051872L) {
         returnValue = this.changeStatus(1, 0, 0, 0, true, true, true, false, context);
      } else if (actionId == -8629311385729242560L) {
         returnValue = this.changeStatus(0, 1, 0, 0, true, true, true, false, context);
      } else if (actionId != 4951292880494466830L) {
         if (actionId == 6780594967363292755L) {
            returnValue = this.delete(context, true, false, false);
         } else if (actionId == 3675472832548253043L) {
            returnValue = this.delete(context, true, false, false, false);
         } else if (actionId == -8494690080715024104L) {
            returnValue = this.delete(context, true, true, false);
         } else if (actionId == 2817016600554138331L) {
            returnValue = this.delete(context, true, true, false, false);
         } else if (actionId != 1092577344890817449L) {
            if (actionId == -5544992959212130441L) {
               returnValue = this.inbound() && !this.flagsSet(1);
            } else if (actionId == 477896226347912237L) {
               returnValue = this.inbound() && this.flagsSet(1);
            } else {
               if (actionId == 635678369939227345L) {
                  if (!this.isDraft()) {
                     return true;
                  }

                  return false;
               }

               if (actionId != -2415955221176628574L) {
                  if (actionId == 278390328807340479L) {
                     if (this.inbound() && !this.flagsSet(1)) {
                        returnValue = true;
                     }
                  } else if (actionId == 3103370408204507200L) {
                     if (this.flagsSet(16)) {
                        returnValue = true;
                     }
                  } else if (actionId == 3456946836994320775L) {
                     if (this.flagsSet(8)) {
                        returnValue = true;
                     }
                  } else if (actionId == -4201671119995560115L) {
                     int status = this.getStatus();
                     if (status == 8191 || status == 16383) {
                        returnValue = true;
                     }
                  } else if (actionId == -1042102706756508802L) {
                     if (!this.flagsSet(16)) {
                        returnValue = true;
                     }
                  } else if (actionId == -2350631868838777916L) {
                     returnValue = this.canBeRepliedTo();
                  } else if (actionId == 8669091670697752579L) {
                     if (this.canBeRepliedTo()) {
                        Verb replyVerb = this.getEmailReplyVerb(!this.inbound());
                        if (replyVerb != null) {
                           replyVerb.invoke(context);
                           returnValue = true;
                        }
                     }
                  } else if (actionId == 5213547777258110094L) {
                     if (this.isNew()) {
                        this.changeStatus(0, 67108864, 0, 0, this.isNew(), true, true, true, context);
                        EmailHierarchy emailHierarchy = EmailHierarchy.getEmailHierarchyForFolder(this._folderId);
                        RibbonUpdater.updateUnreadCount(emailHierarchy, true, false, true, !this.flagsSet(2), true, false);
                        EmailMessageUtilities.cancelImmediateNotifications(this, null);
                        returnValue = true;
                     }
                  } else if (actionId == -6072303684925088654L) {
                     returnValue = this.isNew();
                  }
               } else {
                  RIMModel model = (RIMModel)ContextObject.get(context, -321822713458159100L);
                  if (model instanceof EmailMessageModel) {
                     EmailMessageModel emailModel = (EmailMessageModel)model;
                     String subject1 = emailModel.getSubject();
                     String subject2 = this.getSubject();
                     if (subject1 != null && subject2 != null) {
                        subject1 = subject1.trim();
                        subject2 = subject2.trim();
                        int originalSubjectIndex1 = EmailMessageUtilities.getOriginalSubjectIndex(subject1);
                        int originalSubjectIndex2 = EmailMessageUtilities.getOriginalSubjectIndex(subject2);
                        if (subject1.regionMatches(
                           false,
                           originalSubjectIndex1,
                           subject2,
                           originalSubjectIndex2,
                           Math.max(subject1.length() - originalSubjectIndex1, subject2.length() - originalSubjectIndex2)
                        )) {
                           returnValue = true;
                        }
                     }
                  }
               }
            }
         } else {
            EmailFolder destinationFolder = (EmailFolder)ContextObject.get(context, -1219344331000926502L);
            if (destinationFolder != null) {
               Folder baseFolderFordestination = destinationFolder.getBaseFolder();
               EmailHierarchy currentHierarchy = EmailHierarchy.getEmailHierarchyForFolder(this.getFolderId());
               if (baseFolderFordestination == currentHierarchy.getBaseFolder()) {
                  long currentFolderLUID = this.getFolderId();
                  if (currentFolderLUID != currentHierarchy.getOrphanedSavedFolder().getLUID()) {
                     EmailFolder currentFolder = (EmailFolder)currentHierarchy.getFolder(this.getFolderId());
                     EmailHierarchy.fileMessage(this, currentFolderLUID, destinationFolder.getLUID());
                     this.changeStatus(6, 0, 0, 0, true, true, true, true, context);
                     OTAMessageSync.getInstance().messageMovedOnDevice(this, currentFolder, destinationFolder, context);
                  }
               }
            } else {
               this.changeStatus(6, 0, 0, 0, true, true, false, true, context);
            }

            returnValue = true;
         }
      } else {
         boolean filed = this.flagsSet(2);
         boolean saved = this.flagsSet(16);
         boolean savedThenOrphaned = this.flagsSet(8);
         if (context instanceof EmailFolder) {
            EmailFolder folder = (EmailFolder)context;
            switch (folder.getFolderType()) {
               case 79:
                  EventLogger.logEvent(-1237457833540244999L, 1229344077, 2);
                  break;
               case 80:
               default:
                  if (!saved || !savedThenOrphaned) {
                     this.setFlags(24);
                     EventLogger.logEvent(-1237457833540244999L, 1229344077, 3);
                  }
                  break;
               case 81:
                  if (filed) {
                     this.clearFlags(2);
                     EventLogger.logEvent(-1237457833540244999L, 1229344077, 3);
                  }
                  break;
               case 82:
                  if (!filed) {
                     this.setFlags(2);
                     EventLogger.logEvent(-1237457833540244999L, 1229344077, 3);
                  }
            }
         }

         if (this.inbound() && !this.flagsSet(1)) {
            RibbonUpdater.updateUnreadCount(EmailHierarchy.getEmailHierarchyForFolder(this._folderId), true, true, true, !filed, this.isNew());
         }

         if (this.notYetSent()) {
            this.setStatus(16383, 0);
         }

         if (saved && !savedThenOrphaned) {
            NonpersistedUtilityFolders.addMessageToUtilityFolder(7175316403005034194L, this);
         }

         if (this.flagsSet(524288)) {
            PagingSupport.enablePagingSupport();
         }

         returnValue = true;
      }

      return returnValue;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         this.writeNetworkMessageData(context, syncBuffer);
         syncBuffer.addInt(75, this.getCMIMEReferenceIdentifier(), 4);
         String errorMessage = this.getTransmissionErrorMessage();
         if (errorMessage != null) {
            syncBuffer.addField(19, errorMessage);
         }

         if ((this.getFlags() & 524288) != 0) {
            syncBuffer.addInt(30, 21, 1);
         } else if (this.isNNE()) {
            syncBuffer.addInt(30, 127, 1);
         }

         boolean oldInbound = ContextObject.getFlag(context, 38);
         boolean oldPIN = ContextObject.getFlag(context, 94);
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            if (this.inbound()) {
               ContextObject.setFlag(context, 38);
            } else {
               ContextObject.clearFlag(context, 38);
            }

            if (this.flagsSet(8192)) {
               ContextObject.setFlag(context, 94);
            } else {
               ContextObject.clearFlag(context, 94);
            }

            syncBuffer.addSubmembers(this, context);
            var9 = false;
         } finally {
            if (var9) {
               if (oldInbound) {
                  ContextObject.setFlag(context, 38);
               } else {
                  ContextObject.clearFlag(context, 38);
               }

               if (oldPIN) {
                  ContextObject.setFlag(context, 94);
               } else {
                  ContextObject.clearFlag(context, 94);
               }
            }
         }

         if (oldInbound) {
            ContextObject.setFlag(context, 38);
         } else {
            ContextObject.clearFlag(context, 38);
         }

         if (oldPIN) {
            ContextObject.setFlag(context, 94);
            return true;
         } else {
            ContextObject.clearFlag(context, 94);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public Object clone(Object context) {
      ContextObject creationContext = ContextObject.clone(context);
      if (this.inbound()) {
         ContextObject.setFlag(creationContext, 38);
      } else {
         ContextObject.clearFlag(creationContext, 38);
      }

      EmailMessageModelImpl newMessage = new EmailMessageModelImpl(creationContext);
      CloneProvider cloneProvider = this._payload;
      newMessage._payload = (EmailPayloadModelImpl)cloneProvider.clone(creationContext);
      EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(this._folderId);
      if (newMessage.inbound()) {
         newMessage.setStatus(this.getStatus(), this.getTransmissionError());
         newMessage._folderId = hierarchy.getInboxFolder();
      } else {
         newMessage.setStatus(Integer.MAX_VALUE, 0);
         newMessage._folderId = hierarchy.getSentFolder();
      }

      newMessage.setFlags(this.getFlags() & 6291520);
      newMessage.setFlags(this.getFlags() & 8388608);
      newMessage.setFlags(this.getFlags() & 16809984);
      newMessage.setSensitivity(this.getSensitivity());
      newMessage.setPriority(this.getPriority());
      newMessage.setGMEReferenceIdentifier(this.getGMEReferenceIdentifier());
      newMessage.setType(this.getType());
      newMessage.setTimestamp(System.currentTimeMillis());
      return newMessage;
   }

   @Override
   public Object invokeHotkey(Object context, int hotkeyID) {
      switch (hotkeyID) {
         case 147:
         case 151:
            break;
         case 148:
         default:
            if (this.canBeRepliedTo()) {
               Verb replyVerb = this.getEmailReplyVerb(!this.inbound());
               if (replyVerb != null) {
                  return replyVerb.invoke(context);
               }
            }
            break;
         case 149:
            if (this.canBeForwarded() && this.canBeRepliedTo()) {
               Verb replyVerb = this.getEmailReplyVerb(true);
               if (replyVerb != null) {
                  return replyVerb.invoke(context);
               }
            }
            break;
         case 150:
            if (this.canBeForwarded()) {
               return this.getEmailForwardVerb().invoke(context);
            }
            break;
         case 152:
            Verb verb;
            if (this.flagsSet(1)) {
               verb = new EmailChangeStatusVerb(602450, 18, 0, 1, 0, this);
            } else {
               verb = new EmailChangeStatusVerb(602448, 17, 1, 0, 0, this);
            }

            return verb.invoke(context);
         case 153:
            EmailFolder folder = EmailHierarchy.getEmailFolder(this.getFolderId());
            if (folder != null && folder.isInFolderDatabase()) {
               Verb verb = new Object(this);
               return ((Verb)verb).invoke(context);
            }
      }

      return null;
   }

   boolean canBeRepliedTo() {
      boolean replyAllowed = this.flagsSet(32);
      if (replyAllowed && this.isNNE()) {
         boolean itpolicyValue = ITPolicy.getBoolean(23, 9, false);
         replyAllowed = !itpolicyValue;
      }

      return replyAllowed;
   }

   boolean canBeForwarded() {
      boolean forwardAllowed = (this.inbound() || this.isSuccessfullySent()) && !this.flagsSet(33554432);
      if (forwardAllowed && this.isNNE()) {
         boolean itpolicyValue = ITPolicy.getBoolean(23, 9, false);
         forwardAllowed = !itpolicyValue;
      }

      return forwardAllowed;
   }

   boolean notYetSent() {
      switch (this.getStatus()) {
         case 4095:
         case 32767:
         case 67108863:
         case 134217727:
         case 268435455:
         case 536870911:
         case 1073741823:
            return true;
         default:
            return false;
      }
   }

   public boolean isSuccessfullySent() {
      switch (this.getStatus()) {
         case 2097151:
         case 4194303:
         case 8388607:
         case 33554431:
            return true;
         default:
            return false;
      }
   }

   public boolean isMissingMessageOnServer() {
      return this.getStatus() == 8191 && this.getTransmissionError() == 49;
   }

   protected Verb getEmailFileVerb() {
      EmailFolder folder = EmailHierarchy.getEmailFolder(this.getFolderId());
      return (Verb)(folder != null && folder.isInFolderDatabase() ? new Object(this) : null);
   }

   protected Verb getEmailSaveVerb() {
      return !this.flagsSet(16) ? new EmailChangeStatusVerb(602480, 19, 16, 0, 0, this) : null;
   }

   protected Verb getEmailForwardVerb() {
      return new EmailResponseVerb(602880, 9149, 13, this);
   }

   void resetStatus() {
      this.setStatus(this.inbound() ? 2047 : 33554431, 0, true);
   }

   protected Verb getEmailOpenVerb(Object context) {
      Verb[] defaultVerbs = new Object[2];
      defaultVerbs[0] = new EmailOpenVerb(this);
      ContextObject contextObject = ContextObject.castOrCreate(context);
      contextObject.put(248, defaultVerbs);
      contextObject.put(424670468422402792L, this);
      contextObject.setFlag(87);
      RIMModel payloadModel = this.getPayload();
      if (payloadModel instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)payloadModel;
         verbProvider.getVerbs(contextObject, new Object[0]);
      }

      contextObject.remove(248);
      contextObject.remove(424670468422402792L);
      contextObject.clearFlag(87);
      return defaultVerbs[0];
   }

   public void setStatus(int messageStatus, int messageTransmissionError, boolean force) {
      if (this._messageStatus != 8191 && this._messageStatus != 1 || force) {
         if (messageStatus != 8191 && messageStatus != 1) {
            messageTransmissionError = 0;
            this._errorMessageOverride = null;
         }

         this._messageStatus = messageStatus;
         this._messageTransmissionError = messageTransmissionError;
      }
   }

   public SubjectModel getSubjectModel() {
      if (this._payload instanceof Object) {
         ReadableList items = this._payload;
         int size = items.size();

         for (int i = 0; i < size; i++) {
            Object item = items.getAt(i);
            if (item instanceof SubjectModel) {
               return (SubjectModel)item;
            }
         }
      }

      return null;
   }

   @Override
   public int getTransmissionError() {
      return this._messageTransmissionError;
   }

   @Override
   public void setTransmissionErrorMessage(String errorMessage) {
      this._errorMessageOverride = errorMessage;
   }

   @Override
   public String getTransmissionErrorMessage() {
      return this._errorMessageOverride;
   }

   @Override
   public void setFlags(int mask) {
      this._messageFlags |= mask;
   }

   @Override
   public void clearFlags(int mask) {
      this._messageFlags &= ~mask;
   }

   @Override
   public int getFlags() {
      return this._messageFlags;
   }

   @Override
   public boolean flagsSet(int flags) {
      return (this._messageFlags & flags) != 0;
   }

   @Override
   public void setInbound(boolean inbound) {
      this._payload.setInbound(inbound);
   }

   @Override
   public boolean inbound() {
      return this._payload.inbound();
   }

   @Override
   public void setIsNNE(boolean nne) {
      this._payload.setIsNNE(nne);
   }

   @Override
   public boolean isNNE() {
      return this._payload.isNNE();
   }

   @Override
   public Object getAt(int index) {
      return this._payload.getAt(index);
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public int getIndex(Object element) {
      return ReadableListUtil.getIndex(element, this);
   }

   @Override
   public int size() {
      return this._payload.size();
   }

   @Override
   public void add(Object submember) {
      if (!(submember instanceof TimeStampModel)) {
         this._payload.add(submember);
      } else {
         TimeStampModel timeStampModel = (TimeStampModel)submember;
         this.setTimestamp(timeStampModel.getRawDate());
      }
   }

   @Override
   public boolean contains(Object element) {
      return this.getIndex(element) != -1;
   }

   @Override
   public void remove(Object submember) {
      if (submember instanceof TimeStampModel) {
         this.setTimestamp(0);
      } else {
         this._payload.remove(submember);
      }
   }

   @Override
   public void removeAll() {
      this._payload.removeAll();
   }

   @Override
   public void setRecipientType(byte type) {
      this._payload.setRecipientType(type);
   }

   @Override
   public void setType(byte type) {
      this._packedInfo = this._packedInfo & -16711681 | type << 16 & 0xFF0000;
   }

   @Override
   public byte getType() {
      return (byte)((this._packedInfo & 0xFF0000) >> 16);
   }

   @Override
   public void setCursorPosition(int pos) {
      this._cursorPosition = pos;
   }

   @Override
   public int getCursorPosition() {
      return this._cursorPosition;
   }

   @Override
   public void setPayload(EmailPayloadModel payload) {
      this._payload = (EmailPayloadModelImpl)payload;
   }

   @Override
   public EmailPayloadModel getPayload() {
      return this._payload;
   }

   @Override
   public void setAttachmentCount(int count) {
      this._attachmentCount = count;
   }

   @Override
   public int getAttachmentCount() {
      return this._attachmentCount;
   }

   @Override
   public String getSubject() {
      SubjectModel subjectModel = this.getSubjectModel();
      return subjectModel != null ? subjectModel.getSubject() : null;
   }

   @Override
   public int getStatus() {
      return this._messageStatus;
   }

   @Override
   public String getSender() {
      return null;
   }

   @Override
   public String[] getRecipients() {
      return null;
   }

   @Override
   public boolean allowDescriptiveForwardHeader() {
      return true;
   }

   @Override
   public long getSentDate() {
      return 0;
   }

   @Override
   public MessageAttachment[] getAttachments() {
      return null;
   }

   @Override
   public String getName() {
      return EmailResources.getString(this.flagsSet(8192) ? 202 : 201);
   }

   @Override
   public String getBody() {
      BodyModel bodyModel = this.getBodyModel();
      return bodyModel != null ? bodyModel.getText() : null;
   }

   @Override
   public BodyModel getBodyModel() {
      if (this._payload instanceof Object) {
         ReadableList items = this._payload;
         int size = items.size();

         for (int i = 0; i < size; i++) {
            Object item = items.getAt(i);
            if (item instanceof Object && item instanceof Object) {
               return (BodyModel)item;
            }
         }
      }

      return null;
   }

   @Override
   public void setGMEReferenceIdentifier(int GMEReferenceIdentifier) {
      this._GMEReferenceIdentifier = GMEReferenceIdentifier;
   }

   @Override
   public byte getEncoding() {
      return this._payload.getEncoding();
   }

   @Override
   public void setRead(Object context) {
      this.changeStatus(1, 0, 0, 0, true, true, false, false, context);
   }

   @Override
   public boolean changeStatus(
      int flagsToSet,
      int flagsToClear,
      int newStatus,
      int transmissionError,
      boolean commit,
      boolean notify,
      boolean uiInitiated,
      boolean preventShortCut,
      Object context
   ) {
      if ((flagsToSet & flagsToClear) != 0) {
         throw new Object("Trying to set and clear the same bit");
      }

      if ((flagsToSet & 2) != 0 && EmailHierarchy.isInInboxOrSentItemsFolder(this)) {
         flagsToSet &= -3;
         flagsToClear |= 2;
      }

      boolean statusChanged = false;
      if (newStatus != 0) {
         if (newStatus != this.getStatus()) {
            statusChanged = true;
         } else if (transmissionError != 0) {
            int transmissionErrorToCompare = transmissionError;
            if (newStatus != 8191 && newStatus != 1) {
               transmissionErrorToCompare = 0;
            }

            if (transmissionErrorToCompare != this.getTransmissionError()) {
               statusChanged = true;
            }
         }
      }

      int currentlySetFlags = this.getFlags();
      if (!preventShortCut
         && (flagsToSet == 0 || ((flagsToSet ^ currentlySetFlags) & flagsToSet) == 0)
         && (flagsToClear == 0 || ((flagsToClear ^ ~currentlySetFlags) & flagsToClear) == 0)
         && !statusChanged) {
         return false;
      }

      if (!this.inbound()) {
         if (!preventShortCut
            && (flagsToSet & 2) == 0
            && (flagsToClear & 2) == 0
            && (flagsToSet & 16) == 0
            && (flagsToClear & 16) == 0
            && (flagsToSet & 8) == 0
            && newStatus == 0
            && (flagsToSet & 4) == 0
            && (flagsToClear & 4) == 0) {
            return false;
         }

         if (newStatus == 33554431 && NativeAttachmentRequestProcessor$Helper.messageContainsLargeAttachments(this)) {
            newStatus = 67108863;
            if (context instanceof Object) {
               ContextObject contextObject = (ContextObject)context;
               Boolean isInvokedFromProcessor = (Boolean)contextObject.get(4619344424211138694L);
               if (isInvokedFromProcessor != null && isInvokedFromProcessor) {
                  newStatus = 33554431;
               }
            }
         }
      } else {
         if (!preventShortCut
            && (flagsToSet & 2) == 0
            && (flagsToClear & 2) == 0
            && (flagsToSet & 1) == 0
            && (flagsToClear & 1) == 0
            && (flagsToSet & 16) == 0
            && (flagsToClear & 16) == 0
            && (flagsToSet & 32) == 0
            && (flagsToClear & 32) == 0
            && (flagsToSet & 8) == 0
            && newStatus == 0
            && (flagsToSet & 4) == 0
            && (flagsToClear & 4) == 0
            && (flagsToSet & 67108864) == 0
            && (flagsToClear & 67108864) == 0) {
            return false;
         }

         EmailHierarchy emailHierarchy = null;
         if (this._folderId != 0) {
            emailHierarchy = EmailHierarchy.getEmailHierarchyForFolder(this._folderId);
         }

         synchronized (FolderHierarchies.getLockObject()) {
            synchronized (this) {
               if ((flagsToSet & 1) != 0 && !this.flagsSet(1)) {
                  RibbonUpdater.updateUnreadCount(emailHierarchy, true, false, true, !this.flagsSet(2), this.isNew());
                  EmailMessageUtilities.cancelImmediateNotifications(this, null);
                  this.setFlags(1);
                  this.clearFlags(67108864);
                  if (!ContextObject.getPrivateFlag(context, 1970546000003176090L, 2) && !ContextObject.getFlag(context, 111)) {
                     OTAMessageSync.getInstance().messageReadStatusChangeOnDevice(this, true, context);
                     if ((currentlySetFlags & 2) == 0 && EmailHierarchy.isInPersonalFolder(this)) {
                        long currentFolderId = this.getFolderId();
                        EmailHierarchy.fileMessage(this, currentFolderId, currentFolderId);
                        flagsToSet |= 2;
                        if (MessageListOptions.getOptions().getFlag(16)) {
                           ContextObject.clearFlag(context, 62);
                        }
                     }
                  }
               } else if ((flagsToClear & 1) != 0 && this.flagsSet(1)) {
                  boolean isNewMessage = (flagsToSet & 67108864) != 0 && !this.flagsSet(67108864);
                  RibbonUpdater.updateUnreadCount(emailHierarchy, true, true, true, !this.flagsSet(2), isNewMessage);
                  this.clearFlags(1);
                  if (!ContextObject.getPrivateFlag(context, 1970546000003176090L, 2)) {
                     OTAMessageSync.getInstance().messageReadStatusChangeOnDevice(this, false, context);
                  }
               } else if (!this.flagsSet(1)) {
                  if ((flagsToSet & 2) != 0 && !this.flagsSet(2)) {
                     boolean isNew = this.isNew();
                     if (isNew) {
                        RibbonUpdater.updateUnreadCount(emailHierarchy, true, false, true, false, isNew, false);
                        EmailMessageUtilities.cancelImmediateNotifications(this, null);
                        this.clearFlags(67108864);
                     }

                     RibbonUpdater.updateUnreadCount(emailHierarchy, true, false, false, true, isNew);
                     this.setFlags(2);
                  } else if ((flagsToClear & 2) != 0) {
                     if (!this.flagsSet(2) && this.isNew()) {
                        RibbonUpdater.updateUnreadCount(emailHierarchy, true, false, true, true, true, false);
                        EmailMessageUtilities.cancelImmediateNotifications(this, null);
                        this.clearFlags(67108864);
                     } else if (this.flagsSet(2)) {
                        boolean isNew = this.isNew();
                        if (isNew) {
                           RibbonUpdater.updateUnreadCount(emailHierarchy, true, false, true, false, isNew, false);
                           EmailMessageUtilities.cancelImmediateNotifications(this, null);
                           this.clearFlags(67108864);
                        }

                        RibbonUpdater.updateUnreadCount(emailHierarchy, true, true, false, true, false);
                        this.clearFlags(2);
                     }
                  }
               }
            }
         }
      }

      if (!this.flagsSet(16) && (flagsToSet & 16) != 0) {
         NonpersistedUtilityFolders.addMessageToUtilityFolder(7175316403005034194L, this);
         if (uiInitiated) {
            MessageListOptions options = MessageListOptions.getOptions();
            if (options.isKeepSavedMessagesDurationDefinedByItPolicy()) {
               int durationFromItPolicy = options.getKeepSavedMessagesDuration();
               StringBuffer sb = (StringBuffer)(new Object());
               sb.append(CommonResources.getString(9167));
               sb.append(" ");
               if (durationFromItPolicy == -1) {
                  sb.append(CommonResources.getString(9145));
               } else {
                  sb.append(durationFromItPolicy);
                  sb.append(" ");
                  sb.append(CommonResources.getString(9144));
               }

               Status.show(sb.toString());
            } else {
               Status.show(CommonResources.getString(5001));
            }
         }
      }

      if (this.flagsSet(16) && (flagsToClear & 16) != 0) {
         NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, this);
      }

      if (!this.flagsSet(8) && (flagsToSet & 8) != 0) {
         NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, this);
      }

      this.setFlags(flagsToSet);
      this.clearFlags(flagsToClear);
      if (newStatus != 0 || transmissionError != 0) {
         this.setStatus(newStatus, transmissionError);
      }

      if (commit) {
         PersistentObject.commit(this);
      }

      if (notify) {
         Collection collection = EmailHierarchy.getStorageCollection(this.getFolderId(), this.flagsSet(2));
         if (collection instanceof Object) {
            CollectionListener listener = (CollectionListener)collection;
            MessagingUtil.robustElementUpdated(listener, this);
            if (this.flagsSet(16) && !this.flagsSet(8)) {
               NonpersistedUtilityFolders.updateMessageInUtilityFolder(7175316403005034194L, this);
            }
         }

         ModelViewListenerRegistry.notifyOfOpenedModelChange(this, this, context);
      }

      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void paint(ColumnPainter painter, Object context) {
      boolean inbound = this.inbound();
      if (EmailMessageUtilities.treatAsLevel1(this)) {
         painter.setLevelOne(true);
      }

      painter.setPriority(getPaintPriority(this.getPriority()));
      painter.drawIcon(1, MessageIcons.getIcons(), getStatusIcon(this));
      painter.drawTime(2, this._payload._creationDate);
      if (!this.flagsSet(1)) {
         painter.setEmphasis(true);
      }

      ContextObject contextObject = ContextObject.castOrCreate(context);
      Object oldParentModel = contextObject.put(246, this);
      boolean oldInbound = contextObject.getFlag(38);
      if (inbound && !oldInbound) {
         contextObject.setFlag(38);
      } else if (!inbound && oldInbound) {
         contextObject.clearFlag(38);
      }

      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         int size = this._payload.size();

         for (int i = 0; i < size; i++) {
            Object payloadElement = this._payload.getAt(i);
            if (payloadElement instanceof Object) {
               RIMModel m = (RIMModel)payloadElement;
               if (m instanceof Object) {
                  ColumnPaintProvider columnPaintProvider = (ColumnPaintProvider)m;
                  columnPaintProvider.paint(painter, contextObject);
               }
            }
         }

         var14 = false;
      } finally {
         if (var14) {
            if (oldParentModel != null) {
               contextObject.put(246, oldParentModel);
            } else {
               contextObject.remove(246);
            }

            if (!oldInbound && inbound) {
               contextObject.clearFlag(38);
            } else if (oldInbound && !inbound) {
               contextObject.setFlag(38);
            }
         }
      }

      if (oldParentModel != null) {
         contextObject.put(246, oldParentModel);
      } else {
         contextObject.remove(246);
      }

      if (!oldInbound && inbound) {
         contextObject.clearFlag(38);
      } else if (oldInbound && !inbound) {
         contextObject.setFlag(38);
      }
   }

   @Override
   public byte getRecipientType() {
      return this._payload.getRecipientType();
   }

   @Override
   public void setStatus(int messageStatus, int messageTransmissionError) {
      this.setStatus(messageStatus, messageTransmissionError, false);
   }

   @Override
   public void setSensitivity(byte sensitivity) {
      if (sensitivity < 1 || sensitivity > 4) {
         sensitivity = 0;
      }

      this._packedInfo = this._packedInfo & 16777215 | sensitivity << 24 & 0xFF000000;
   }

   @Override
   public byte getSensitivity() {
      return (byte)((this._packedInfo & 0xFF000000) >> 24);
   }

   @Override
   public void setPriority(byte priority) {
      this._packedInfo = this._packedInfo & -256 | priority << 0 & 0xFF;
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      int keyCount = 0;
      int n = this._payload.size();

      for (int i = 0; i < n; i++) {
         RIMModel m = (RIMModel)this._payload.getAt(i);
         if (m instanceof Object) {
            KeyProvider keyProvider = (KeyProvider)m;
            keyCount += keyProvider.getKeys(context, keyArray, index + keyCount, keyRequested);
         }
      }

      return keyCount;
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      if (keyRequested == -4145532165335996154L) {
         AddressReference sender = this.getSenderInfo();
         if (sender != null) {
            Object senderModel = sender.getInsideModel();
            if (senderModel instanceof Object) {
               return ((KeyProvider)senderModel).getKeys(context, keyArray, index, keyRequested);
            }
         }
      }

      return 0;
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested != -7628247220259263034L && keyRequested != 92199951187614847L) {
         return 0;
      }

      long result;
      if (this._payload != null) {
         result = this._payload._creationDate;
      } else {
         result = Long.MAX_VALUE;
      }

      keyArray[index] = result;
      return 1;
   }

   @Override
   public void setCMIMEReferenceIdentifier(int CMIMEReferenceIdentifier) {
      this._payload.setCMIMEReferenceIdentifier(CMIMEReferenceIdentifier);
   }

   @Override
   public void setEncoding(byte encoding) {
      this._payload.setEncoding(encoding);
   }

   @Override
   public int getGMEReferenceIdentifier() {
      return this._GMEReferenceIdentifier;
   }

   @Override
   public long getFolderId() {
      return this._folderId;
   }

   @Override
   public void setFolderId(long id) {
      if (this._folderId == 0 && id != 0 && !this.flagsSet(1)) {
         RibbonUpdater.updateUnreadCount(EmailHierarchy.getEmailHierarchyForFolder(id), false, true, true, !this.flagsSet(2), this.isNew());
      }

      this._folderId = id;
   }

   @Override
   public void setStateInfo(int state, Object context) {
      int flagsToSet = 0;
      int flagsToClear = 0;
      if (EmailSyncState.isRead(state)) {
         flagsToSet |= 1;
      } else {
         flagsToClear |= 1;
      }

      if (EmailSyncState.isMoved(state)) {
         flagsToSet |= 4;
      } else {
         flagsToClear |= 4;
      }

      this.changeStatus(flagsToSet, flagsToClear, 0, 0, false, true, false, false, null);
      int newFolderId = EmailSyncState.getFolderId(state);
      long oldFolderLUID = this.getFolderId();
      EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(oldFolderLUID);
      long newFolderLUID = hierarchy.getEmailFolder(newFolderId);
      if (oldFolderLUID != newFolderLUID) {
         EmailHierarchy.fileMessage(this, oldFolderLUID, newFolderLUID);
         OTAMessageSync.getInstance()
            .messageMovedOnDevice(this, (EmailFolder)hierarchy.getFolder(oldFolderLUID), (EmailFolder)hierarchy.getFolder(newFolderLUID), context);
         this.changeStatus(2, 0, 0, 0, true, true, false, true, null);
         EmailFolder newFolder = (EmailFolder)hierarchy.getFolder(newFolderLUID);
         if (newFolder != null) {
            FolderPreselector.updateDefaultFolder(this, newFolder, false);
         }
      }
   }

   @Override
   public int getStateInfo(Object context) {
      long folderLUID = this.getFolderId();
      int folderId = 0;
      EmailFolder f = EmailMessageSync.getEmailFolder(folderLUID);
      if (f != null) {
         folderId = f.getFolderId();
      } else {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(folderLUID);
         folderId = hierarchy.getFolderIdForFolder(folderLUID);
      }

      return EmailSyncState.makeStateInfo(folderId, this.flagsSet(1), this.flagsSet(4), false, false);
   }

   @Override
   public boolean canPurge(int purgeType) {
      boolean ret = false;
      if (!this.flagsSet(16)) {
         if (purgeType == 1) {
            ret = true;
         } else if (purgeType == 0) {
            EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(this.getFolderId());
            ret = hierarchy.canPurge(this);
         }

         if (ret && this.flagsSet(262144)) {
            this.clearFlags(262144);
            return ret;
         }
      } else if (purgeType == 3) {
         ret = this.flagsSet(16);
      }

      return ret;
   }

   @Override
   public void purge(int purgeType) {
      if (_purgeStaleMessagesContext == null) {
         _purgeStaleMessagesContext = (ContextObject)(new Object(23));
      }

      LowMemoryManager.markAsRecoverable(this);
      this.delete(_purgeStaleMessagesContext, true, false, false);
   }

   @Override
   public Object getDefault(Object current, Object context) {
      if (ContextObject.getFlag(context, 46) && current == null) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(this.getFolderId());
         if (hierarchy != null) {
            current = hierarchy.getFolder(hierarchy.getInboxFolder());
         }
      }

      if (ContextObject.getFlag(context, 46) && current instanceof EmailFolder) {
         EmailFolder folder = (EmailFolder)current;
         EmailHierarchy hierarchy = folder.getEmailHierarchy();
         if (folder.getFolderType() != 2 && folder.getFolderType() != 4) {
            return folder;
         }

         FolderPreselector selector = FolderPreselector.getInstance(hierarchy);
         EmailFolder attempt = selector.getRecommendedFolder(this);
         return attempt != null ? attempt : folder;
      } else {
         return current;
      }
   }

   @Override
   public Object updateDefault(Object newdefault, Object context) {
      if (ContextObject.getFlag(context, 46) && newdefault instanceof EmailFolder) {
         FolderPreselector.updateDefaultFolder(this, (EmailFolder)newdefault, false);
      }

      return this;
   }

   @Override
   public byte getPriority() {
      return (this.getFlags() & 524288) != 0 ? 2 : (byte)((this._packedInfo & 0xFF) >> 0);
   }

   @Override
   public long getTimestamp() {
      return this._payload._timestamp;
   }

   @Override
   public void setTimestamp(long timestamp) {
      this._payload._timestamp = timestamp;
   }

   @Override
   public void setNotificationLevel(byte notificationLevel) {
      this._packedInfo = this._packedInfo & -65281 | notificationLevel << 8 & 0xFF00;
   }

   @Override
   public byte getNotificationLevel() {
      return (byte)((this._packedInfo & 0xFF00) >> 8);
   }

   @Override
   public boolean proceedWithDelete(Object context, boolean multipleItems) {
      if (!ContextObject.getFlag(context, 73)) {
         return true;
      }

      if (this.flagsSet(8)) {
         return true;
      }

      if (!this.notYetSent() && !this.isDraft()) {
         ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(this);
         if (CMIMEUtilities.getDeleteOnLocation(serviceRecord) == 2) {
            String[] choices = MessageResources.getStringArray(172);
            int[] values = new int[]{0, 1, -1, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656, 1918985587, 1226860643, 1867325550};
            ContextObject.clearFlag(context, 73);
            int choice = multipleItems ? -1 : PersistentInteger.get(_lastDeleteLocationID);
            if (!multipleItems && choice < 0 || choice > 1) {
               choice = 1;
            }

            choice = Dialog.ask(MessageResources.getString(multipleItems ? 174 : 173), choices, values, choice);
            if (choice == -1) {
               return false;
            }

            ContextObject.setPrivateFlag(context, 1970546000003176090L, choice == 0 ? 1 : 0);
            if (!multipleItems) {
               PersistentInteger.set(_lastDeleteLocationID, choice);
            }
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return this._payload.checkCrypt(compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._payload = (EmailPayloadModelImpl)this._payload.reCrypt(compress, encrypt);
      return null;
   }

   @Override
   public ServiceRecord getServiceRecordForMessage() {
      return EmailMessageUtilities.getServiceRecordForMessage(this);
   }

   @Override
   public AddressReference getSenderInfo() {
      if (this._payload instanceof Object) {
         ReadableList items = this._payload;
         int size = items.size();

         for (int i = 0; i < size; i++) {
            Object item = items.getAt(i);
            if (item instanceof EmailHeaderModel) {
               EmailHeaderModel m = (EmailHeaderModel)item;
               if (m.getHeaderType() == 3) {
                  return m;
               }
            }
         }
      }

      return null;
   }

   @Override
   public byte getVisibilityFlags() {
      return this.visibilityForStatus(this.getStatus());
   }

   @Override
   public boolean scheduleResend(int code) {
      if (this._resendAttempts >= RESEND_DELAY_TIMES.length) {
         return false;
      }

      if (this.inbound()) {
         return false;
      }

      int delayTime = RESEND_DELAY_TIMES[this._resendAttempts] * 1000;
      if (code == 4243) {
         delayTime = 20000;
      }

      _resendTimer.schedule(new EmailMessageModelImpl$ScheduledResend(this, this), delayTime);
      return true;
   }

   @Override
   public int autoResendAttempts() {
      return this._resendAttempts;
   }

   @Override
   public boolean isNew() {
      return this.flagsSet(67108864);
   }

   @Override
   public AccessibleContext getAccessibleContext() {
      String _accessibleName = "";

      for (int i = 0; i < this._payload._submembers.length; i++) {
         Object _item = this._payload._submembers[i];
         if (_item instanceof Object && _item instanceof EmailHeaderModel) {
            PersistableRIMModel _model = ((AddressReference)_item).getInsideModel();
            if (_model instanceof Object) {
               String _address = ((AccessibleContextProxy)_model).getAccessibleContext().getAccessibleName();
               if (_address != null) {
                  if (this._payload.inbound() && ((EmailHeaderModel)_item).getHeaderType() == 3) {
                     _accessibleName = ((StringBuffer)(new Object())).append(_accessibleName).append("From: ").append(_address).append(", ").toString();
                     break;
                  }

                  if (!this._payload.inbound() && ((EmailHeaderModel)_item).getHeaderType() == 0) {
                     _accessibleName = ((StringBuffer)(new Object())).append(_accessibleName).append("To: ").append(_address).append(", ").toString();
                     break;
                  }
               }
            }
         }
      }

      _accessibleName = ((StringBuffer)(new Object())).append(_accessibleName).append(this.getSubject()).toString();
      return (AccessibleContext)(new Object(_accessibleName, 0, 4));
   }

   private void writeFolderInfo(byte[] messageHeader) {
      DataBuffer _folderDataBuffer = WeakReferenceUtilities.getDataBuffer(_folderDataBufferWR, true);
      _folderDataBuffer.setData(messageHeader, 80, 12, false);
      EmailFolderSync.writeFolder(this._folderId, _folderDataBuffer);
   }

   private Verb[] getSubmemberVerbs(Object context) {
      RIMModel payloadModel = this.getPayload();
      if (payloadModel instanceof Object) {
         Verb[] submemberVerbs = new Object[0];
         VerbProvider verbProvider = (VerbProvider)payloadModel;
         verbProvider.getVerbs(context, submemberVerbs);
         return submemberVerbs;
      } else {
         return null;
      }
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof EmailMessageModelImpl)) {
         return false;
      }

      EmailMessageModelImpl message = (EmailMessageModelImpl)object;
      return message._messageStatus == this._messageStatus
            && message._cursorPosition == this._cursorPosition
            && message._messageFlags == this._messageFlags
            && message._packedInfo == this._packedInfo
            && message._folderId == this._folderId
            && message._attachmentCount == this._attachmentCount
         ? this._payload.equals(message._payload)
         : false;
   }

   private boolean delete(Object context, boolean commitChanges, boolean suppressGhost, boolean isBulkDelete) {
      return this.delete(context, commitChanges, suppressGhost, isBulkDelete, true);
   }

   private Verb getEmailReplyVerb(boolean replyToAll) {
      if (!this.canBeRepliedTo()) {
         return null;
      } else {
         return replyToAll ? new EmailResponseVerb(603136, 9148, 30, this) : new EmailResponseVerb(602624, 9147, 53, this);
      }
   }

   private boolean delete(Object context, boolean commitChanges, boolean suppressGhost, boolean isBulkDelete, boolean saveOrphan) {
      if ((this.getFlags() & 262144) != 0) {
         return false;
      }

      synchronized (FolderHierarchies.getLockObject()) {
         EmailHierarchy hierarchy = null;
         boolean createGhostImage = false;
         boolean candidateForOTASync = !isBulkDelete;
         boolean purgingSavedMessage = false;
         EmailFolder folder = null;
         if (!ContextObject.getFlag(context, 52)) {
            boolean moveToDeleted = true;
            if (suppressGhost) {
               createGhostImage = false;
            } else {
               createGhostImage = this.deleteMessageOnHHAndDesktop(context);
               candidateForOTASync = createGhostImage && !isBulkDelete;
               if (ContextObject.getFlag(context, 23)) {
                  createGhostImage = true;
                  moveToDeleted = false;
                  candidateForOTASync = false;
                  purgingSavedMessage = this.flagsSet(16);
               }
            }

            long folderLUID = this._folderId;
            hierarchy = EmailHierarchy.getEmailHierarchyForFolder(folderLUID);
            folder = (EmailFolder)hierarchy.getFolder(folderLUID);
            if (createGhostImage && !FolderUtil.isServiceless(hierarchy)) {
               int flags = 0;
               if (moveToDeleted) {
                  EmailFolder deletedFolder = hierarchy.getDeletedFolder();
                  if (deletedFolder != null && deletedFolder.isInFolderDatabase()) {
                     flags = EmailSyncState.makeStateInfo(deletedFolder.getFolderId(), this.flagsSet(1), true, false, true);
                  } else {
                     createGhostImage = false;
                  }
               } else if (folder != null && folder.isInFolderDatabase()) {
                  flags = EmailSyncState.makeStateInfo(folder.getFolderId(), this.flagsSet(1), this.flagsSet(4), false, false);
               } else {
                  createGhostImage = false;
               }

               if (createGhostImage) {
                  hierarchy.addGhostMessageReference(this.getCMIMEReferenceIdentifier(), flags);
               }
            }

            WritableSet collection = (WritableSet)EmailHierarchy.getStorageCollection(folderLUID, this.flagsSet(2));
            collection.remove(this);
            if (!purgingSavedMessage && this.flagsSet(16) && saveOrphan) {
               long orphanedSavedMessagesFolder = hierarchy.getOrphanedSavedFolder().getLUID();
               EmailHierarchy.addMessage(this, orphanedSavedMessagesFolder);
               this.changeStatus(8, 0, 0, 0, commitChanges, true, false, false, context);
            } else {
               this.performActionsWhenMessageIsReallyAboutToBeBlownAway(context);
            }
         } else {
            if (ContextObject.getFlag(context, 22) || ContextObject.getFlag(context, 78)) {
               ContextObject c = ContextObject.clone(context);
               c.clearFlag(52);
               c.clearFlags(22, 78);
               if (!this.flagsSet(8)) {
                  this.delete(c, commitChanges, suppressGhost, isBulkDelete);
               }

               if (this.flagsSet(8)) {
                  c.setFlag(52);
                  this.delete(c, commitChanges, suppressGhost, isBulkDelete);
               }

               return true;
            }

            if (this.flagsSet(8)) {
               Folder messageFolder = EmailHierarchy.getEmailFolder(this._folderId);
               if (messageFolder != null) {
                  WritableSet messageFolderCollection = (WritableSet)messageFolder.getContainedItems();
                  this.performActionsWhenMessageIsReallyAboutToBeBlownAway(context);
                  messageFolderCollection.remove(this);
               }
            } else {
               this.changeStatus(0, 16, 0, 0, commitChanges, true, false, false, context);
            }

            candidateForOTASync = false;
         }

         if (candidateForOTASync && (this.getFlags() & 8192) == 0 && !ContextObject.getFlag(context, 19)) {
            OTAMessageSync.getInstance().messageDeletedOnDevice(this, folder, context);
         }

         return true;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void performActionsWhenMessageIsReallyAboutToBeBlownAway(Object context) {
      synchronized (FolderHierarchies.getLockObject()) {
         ContextObject contextObject = ContextObject.castOrCreate(context);
         boolean emailMessageWasPresent = contextObject.get(-7450314121582082994L) != null;
         boolean var19 = false /* VF: Semaphore variable */;

         try {
            var19 = true;
            if (!this.inbound() && NativeAttachmentRequestProcessor$Helper.messageContainsLargeAttachments(this)) {
               byte errorCode = 2;
               byte[] reason = "User deleted the message".getBytes();
               NativeAttachmentRequestProcessor.getInstance()
                  .cancelRequest(this.getCMIMEReferenceIdentifier(), Integer.MIN_VALUE, errorCode, reason, false, false, true);
            }

            if (!emailMessageWasPresent) {
               contextObject.put(-7450314121582082994L, this);
            }

            int size = this._payload.size();

            for (int i = 0; i < size; i++) {
               Object payloadElement = this._payload.getAt(i);
               if (payloadElement instanceof Object) {
                  RIMModel m = (RIMModel)payloadElement;
                  if (m instanceof Object) {
                     ((ActionProvider)m).perform(-7793619941406158181L, context);
                  }
               }
            }

            var19 = false;
         } finally {
            if (var19) {
               if (!emailMessageWasPresent) {
                  contextObject.remove(-7450314121582082994L);
               }
            }
         }

         if (!emailMessageWasPresent) {
            contextObject.remove(-7450314121582082994L);
         }

         boolean oldFlag = contextObject.getFlag(111);
         boolean var15 = false /* VF: Semaphore variable */;

         try {
            var15 = true;
            contextObject.setFlag(111);
            this.changeStatus(1, 0, 0, 0, false, true, true, false, contextObject);
            var15 = false;
         } finally {
            if (var15) {
               if (!oldFlag) {
                  contextObject.clearFlag(111);
               }
            }
         }

         if (!oldFlag) {
            contextObject.clearFlag(111);
         }

         MessageLookups.remove(-4420850319371185992L, this.getCMIMEReferenceIdentifier());
         MessageLookups.remove(431630751329425149L, this.getGMEReferenceIdentifier());
         MessageLookups.remove(2623838111545834320L, this.getCMIMEReferenceIdentifier());
         MessageLookups.remove(4530015158237739359L, this.getCMIMEReferenceIdentifier());
         NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, this);
      }

      if (!this.inbound() && this.notYetSent()) {
         EmailSendUtility.cancelTransportMessage(this, context);
      }

      this.setFlags(262144);
   }

   protected static byte getPaintPriority(byte priority) {
      switch (priority) {
         case 1:
            return 1;
         case 2:
         default:
            return 2;
         case 3:
            return 3;
      }
   }

   private static int getStatusIcon(EmailMessageModelImpl message) {
      int flags = message.getFlags();
      int status = message.getStatus();
      boolean opened = (flags & 1) != 0;
      boolean filed = (flags & 2) != 0;
      boolean rx_page = (flags & 524288) != 0;
      boolean inbound = message.inbound();
      if (inbound && !opened && EmailHierarchy.isInPersonalFolder(message)) {
         filed = true;
      }

      if (filed) {
         EmailFolder folder = EmailHierarchy.getEmailFolder(message.getFolderId());
         filed = folder != null && folder.isInFolderDatabase();
      }

      if (inbound) {
         if (status == 1) {
            return 3;
         } else if (rx_page) {
            return opened ? 18 : 17;
         } else if (filed) {
            return opened ? 14 : 13;
         } else if (message.isNNE()) {
            return opened ? 131074 : 131073;
         } else if (message.hasViewableAttachment()) {
            return opened ? 20 : 19;
         } else {
            return opened ? 2 : 1;
         }
      } else {
         switch (status) {
            case 8191:
            case 16383:
               return 12;
            case 32767:
            case 134217727:
               return 7;
            case 2097151:
               return 11;
            case 4194303:
               return 10;
            case 33554431:
               return 9;
            case 67108863:
               return 8;
            case 268435455:
               return 6;
            case Integer.MAX_VALUE:
               return 4;
            default:
               return 0;
         }
      }
   }

   private boolean deleteMessageOnHHAndDesktop(Object context) {
      ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(this);
      int deleteOnLocation = CMIMEUtilities.getDeleteOnLocation(serviceRecord);
      if (deleteOnLocation == 2) {
         if (ContextObject.getPrivateFlag(context, 1970546000003176090L, 1)) {
            return true;
         }

         if (ContextObject.getPrivateFlag(context, 1970546000003176090L, 0)) {
            return false;
         }
      }

      boolean deleteOnHandheldAndDesktop = false;
      switch (deleteOnLocation) {
         case 1:
            deleteOnHandheldAndDesktop = true;
            break;
         default:
            deleteOnHandheldAndDesktop = false;
      }

      return deleteOnHandheldAndDesktop;
   }

   public EmailMessageModelImpl(Object initialData) {
      this(initialData, false);
   }

   public EmailMessageModelImpl(Object initialData, boolean initiallyOpened) {
      this(initialData, initiallyOpened, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public EmailMessageModelImpl(Object initialData, boolean initiallyOpened, boolean isNew) {
      this.setSensitivity((byte)1);
      this.setPriority((byte)1);
      ContextObject contextObject = ContextObject.castOrCreate(initialData);
      if (contextObject.getFlag(94)) {
         this.setFlags(8192);
      }

      boolean var7 = false /* VF: Semaphore variable */;

      try {
         var7 = true;
         contextObject.setPrivateFlag(1970546000003176090L, 2);
         this._payload = new EmailPayloadModelImpl(initialData);
         if (contextObject.getFlag(38)) {
            this.setFlags(1);
            this.changeStatus(isNew && !initiallyOpened ? 67108864 : 0, initiallyOpened ? 0 : 1, 2047, 0, false, false, false, false, contextObject);
            var7 = false;
         } else {
            this.setFlags(1);
            this.changeStatus(0, 0, Integer.MAX_VALUE, 0, false, false, false, false, contextObject);
            var7 = false;
         }
      } finally {
         if (var7) {
            contextObject.clearPrivateFlag(1970546000003176090L, 2);
         }
      }

      contextObject.clearPrivateFlag(1970546000003176090L, 2);
   }

   private boolean isDraft() {
      return this.getStatus() == Integer.MAX_VALUE;
   }

   private void writeNetworkMessageData(Object context, SyncBuffer syncBuffer) {
      byte[] _messageHeader = WeakReferenceUtilities.getByteArray(_messageHeaderWR, 106);
      Arrays.fill(_messageHeader, (byte)0);
      this.writeStatus(context, _messageHeader);
      this.writeTimestamps(context, _messageHeader);
      HackyMoreData hackyMoreData = new HackyMoreData();
      MorePartModel morePartModel = EmailMoreVerb.findBodyMorePartModel(this);
      if (morePartModel != null) {
         hackyMoreData.populateFromMorePartModel(morePartModel);
      }

      this.writeMoreData(hackyMoreData, _messageHeader);
      this.writeRecipientType(_messageHeader);
      this.writeFolderInfo(_messageHeader);
      this.writeEncodingId(_messageHeader);
      syncBuffer.addBytes(0, _messageHeader);
      syncBuffer.addInt(100, hackyMoreData._bodyContentLengthOnDevice, 4);
      int transportError = this.getTransmissionError();
      if (transportError != 0) {
         syncBuffer.addInt(59, transportError, 4);
      }
   }

   private static int mapToCppDeviceTransmissionError(int transmissionError) {
      if (transmissionError >= 16768 && transmissionError <= 16895) {
         return transmissionError - 16768;
      }

      switch (transmissionError) {
         case 0:
            return 0;
         case 4528:
            return 29;
         case 4544:
            return 30;
         case 4545:
            return 31;
         case 4560:
            return 32;
         case 4576:
            return 33;
         case 4592:
            return 34;
         case 4607:
            return 35;
         default:
            return 36;
      }
   }

   private void writeStatus(Object context, byte[] messageHeader) {
      if (!this.inbound()) {
         messageHeader[2] = (byte)(messageHeader[2] | 1);
      }

      if (!this.flagsSet(16)) {
         messageHeader[2] = (byte)(messageHeader[2] | 2);
      }

      switch (this.getType()) {
         case 1:
            messageHeader[2] = (byte)(messageHeader[2] | 4);
            break;
         case 16:
            messageHeader[2] = (byte)(messageHeader[2] | 8);
      }

      if (this.canBeRepliedTo()) {
         messageHeader[2] = (byte)(messageHeader[2] | 16);
      }

      if (!this.flagsSet(8)) {
         messageHeader[2] = (byte)(messageHeader[2] | 128);
      }

      if (!this.flagsSet(1)) {
         messageHeader[3] = (byte)(messageHeader[3] | 8);
      }

      if (this.flagsSet(4)) {
         messageHeader[3] = (byte)(messageHeader[3] | 16);
      }

      if (this.flagsSet(16777216)) {
         messageHeader[3] = (byte)(messageHeader[3] | 32);
      }

      if (this.flagsSet(2)) {
         messageHeader[3] = (byte)(messageHeader[3] | 64);
      }

      this.writeMessageHeaderBitFieldThatSpansByteBoundaries(this.getNotificationLevel(), 3, 3, 7, messageHeader);
      if (this._payload.isCopyInsteadOfReference()) {
         messageHeader[4] = (byte)(messageHeader[4] | 8);
      }

      if (this.flagsSet(33554432)) {
         messageHeader[4] = (byte)(messageHeader[4] | 64);
      }

      this.writeMessageHeaderInt(this.getStatus(), 6, messageHeader);
      int transmissionError = this.getTransmissionError();
      int cppDeviceTransmissionError = mapToCppDeviceTransmissionError(transmissionError);
      this.writeMessageHeaderShort(cppDeviceTransmissionError, 10, messageHeader);
      if (this.flagsSet(2097152)) {
         messageHeader[40] = (byte)(messageHeader[40] | 2);
      }

      if (this.flagsSet(64)) {
         messageHeader[40] = (byte)(messageHeader[40] | 4);
      }

      if (this.flagsSet(4194304)) {
         messageHeader[40] = (byte)(messageHeader[40] | 16);
      }

      byte priority = this.getPriority();
      if (priority == 2) {
         messageHeader[40] = (byte)(messageHeader[40] | 8);
      }

      if (priority == 3) {
         messageHeader[40] = (byte)(messageHeader[40] | 32);
      }

      this.writeMessageHeaderBitFieldThatSpansByteBoundaries(this.getSensitivity(), 3, 40, 6, messageHeader);
      if (this.flagsSet(16384)) {
         messageHeader[100] = (byte)(messageHeader[100] | 4);
      }

      if (this.flagsSet(32768)) {
         messageHeader[100] = (byte)(messageHeader[100] | 8);
      }

      if (this.flagsSet(131072)) {
         messageHeader[100] = (byte)(messageHeader[100] | 16);
      }

      if (this.flagsSet(1048576)) {
         messageHeader[100] = (byte)(messageHeader[100] | 32);
      }

      this.writeMessageHeaderInt(this.getUID(), 92, messageHeader);
      Object object = SubmemberUtilities.getFirstSubmember(this, EmailPayloadRecognizer.getInstance());
      if (object == null) {
         object = SubmemberUtilities.getFirstSubmember(this, ReplyWithoutTextStubRecognizer.getInstance());
      }

      if (object instanceof CMIMEReferenceIdProvider) {
         this.writeMessageHeaderInt(((CMIMEReferenceIdProvider)object).getCMIMEReferenceIdentifier(), 70, messageHeader);
      }
   }

   private void writeTimestamps(Object context, byte[] messageHeader) {
      ConverterUtilities.writeNetworkMessageDate(this._payload._creationDate, messageHeader, 28);
      ConverterUtilities.writeNetworkMessageDate(this._payload._creationDate, messageHeader, 32);
      long timestamp = this.getTimestamp();
      if (timestamp == 0) {
         timestamp = this._payload._creationDate;
      }

      ConverterUtilities.writeNetworkMessageDate(timestamp, messageHeader, 36);
   }

   private void writeRecipientType(byte[] messageHeader) {
      byte recipient = this.getRecipientType();
      this.writeMessageHeaderByte(recipient, 68, messageHeader);
   }

   private void writeEncodingId(byte[] messageHeader) {
      byte encoding = this.getEncoding();
      this.writeMessageHeaderByte(encoding, 103, messageHeader);
   }

   private boolean hasViewableAttachment() {
      if (this.getAttachmentCount() == 0) {
         return false;
      }

      for (int i = this._payload.size() - 1; i >= 0; i--) {
         Object item = this._payload.getAt(i);
         if (item instanceof ProxyModel || item instanceof Object || item instanceof AbstractEmailFileAttachment) {
            return true;
         }

         if (item instanceof UnknownMimePartModel) {
            UnknownMimePartModel unknownMimeAttachment = (UnknownMimePartModel)item;
            if (unknownMimeAttachment.isViewable()) {
               return true;
            }
         }
      }

      return false;
   }

   private void writeMoreData(HackyMoreData hackyMoreData, byte[] messageHeader) {
      if (!this.flagsSet(256)) {
         messageHeader[2] = (byte)(messageHeader[2] | 32);
      }

      messageHeader[2] = (byte)(messageHeader[2] | 64);
      if (this.flagsSet(1024)) {
         messageHeader[4] = (byte)(messageHeader[4] | 16);
      }

      messageHeader[100] = (byte)(messageHeader[100] | 2);
      if (this.flagsSet(4096)) {
         messageHeader[3] = (byte)(messageHeader[3] | 4);
      }

      this.writeMessageHeaderInt(hackyMoreData._bodyContentLength, 56, messageHeader);
      this.writeMessageHeaderInt(hackyMoreData._bodyTrueContentLength, 60, messageHeader);
      this.writeMessageHeaderInt(this._cursorPosition, 78, messageHeader);
      this.writeMessageHeaderInt(hackyMoreData._morePartID, 52, messageHeader);
   }

   private byte visibilityForStatus(int status) {
      byte flags = 0;
      switch (status) {
         default:
            flags = (byte)(flags | 1);
         case 2097151:
         case 4194303:
         case 8388607:
         case 33554431:
            flags = (byte)(flags | (this.flagsSet(8192) ? 2 : 4));
            return (byte)(flags | (this.isNew() ? 8 : 0));
      }
   }

   private void writeMessageHeaderBitFieldThatSpansByteBoundaries(
      byte value, int numberOfBitsToReadFromSourceByte, int firstDestinationByteOffset, int bitIntoFirstDestinationByteToStart, byte[] messageHeader
   ) {
      int valueBits = (value & (1 << numberOfBitsToReadFromSourceByte) - 1) << bitIntoFirstDestinationByteToStart;
      messageHeader[firstDestinationByteOffset] |= (byte)valueBits;
      messageHeader[firstDestinationByteOffset + 1] = (byte)(messageHeader[firstDestinationByteOffset + 1] | (byte)(valueBits >> 8));
   }

   private void writeMessageHeaderByte(byte value, int offset, byte[] messageHeader) {
      messageHeader[offset] = (byte)(value & 0xFF);
   }

   private void writeMessageHeaderInt(int value, int offset, byte[] messageHeader) {
      messageHeader[offset] = (byte)(value & 0xFF);
      messageHeader[offset + 1] = (byte)(value >> 8 & 0xFF);
      messageHeader[offset + 2] = (byte)(value >> 16 & 0xFF);
      messageHeader[offset + 3] = (byte)(value >> 24 & 0xFF);
   }

   private void writeMessageHeaderShort(int value, int offset, byte[] messageHeader) {
      messageHeader[offset] = (byte)(value & 0xFF);
      messageHeader[offset + 1] = (byte)(value >> 8 & 0xFF);
   }

   static byte access$008(EmailMessageModelImpl x0) {
      return x0._resendAttempts++;
   }
}
