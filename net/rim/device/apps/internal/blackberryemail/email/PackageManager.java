package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Enumeration;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringPatternRepository;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.address.EmailAddressStringPattern;
import net.rim.device.apps.internal.blackberryemail.address.PINAddressStringPattern;
import net.rim.device.apps.internal.blackberryemail.address.UseOnceAddressVerb;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.profiles.Profiles;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.Security;

public final class PackageManager implements EmailEventLoggerEvents, GlobalEventListener, PersistentContentListener {
   private static final long PIN_COMPOSE_VERB = -2492130528195666495L;
   private static final long PIN_USE_ONCE_VERB = -8411466535318801376L;
   private static final long REGISTERED_PIN_ALREADY = -5708873098720308072L;
   private static final long PIN_FORWARD_VERB = 1862171883528027662L;
   private static boolean ALLOW_ONLY_ONE_PROFILE_CONFIGURATION_FOR_CMIME = false;

   private PackageManager() {
   }

   private static final void adjustVerbs() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      Verb pinComposeVerb = (Verb)applicationRegistry.get(-2492130528195666495L);
      Verb pinForwardVerb = (Verb)applicationRegistry.get(1862171883528027662L);
      if (pinComposeVerb == null) {
         pinComposeVerb = new PINComposeVerb();
         pinForwardVerb = new EmailResponseVerb(602880, 9149, 13, true, true);
         applicationRegistry.put(1862171883528027662L, pinForwardVerb);
         applicationRegistry.put(-2492130528195666495L, pinComposeVerb);
         applicationRegistry.put(-8411466535318801376L, UseOnceAddressVerb.newUseOncePINAddressVerb(pinComposeVerb));
      }

      Verb pinUseOnceVerb = (Verb)applicationRegistry.get(-8411466535318801376L);
      if (!ITPolicy.getBoolean(7, true)) {
         VerbRepository.getVerbRepository(-110058785485458643L).deregister(pinForwardVerb, -3138111110618290303L);
         VerbRepository.getVerbRepository(-7881764549058890736L).deregister(pinComposeVerb, 4246852237058296601L);
         VerbRepository.getVerbRepository(8016149483483360697L).deregister(pinUseOnceVerb, 4246852237058296601L);
         applicationRegistry.replace(-5708873098720308072L, Boolean.FALSE);
      } else {
         Boolean flag = (Boolean)applicationRegistry.get(-5708873098720308072L);
         boolean registeredPINAlready = false;
         if (flag != null) {
            registeredPINAlready = flag;
         }

         if (!registeredPINAlready) {
            VerbRepository.getVerbRepository(-110058785485458643L).register(pinForwardVerb, -3138111110618290303L);
            VerbRepository.getVerbRepository(-7881764549058890736L).register(pinComposeVerb, 4246852237058296601L);
            VerbRepository.getVerbRepository(8016149483483360697L).register(pinUseOnceVerb, 4246852237058296601L);
            applicationRegistry.replace(-5708873098720308072L, Boolean.TRUE);
            return;
         }
      }
   }

   public static final void registerOnceOnSystemStart() {
      adjustVerbs();
      PackageManager pm = new PackageManager();
      Proxy proxy = Proxy.getInstance();
      proxy.addGlobalEventListener(pm);
      PersistentContent.addListener(pm);
      Verb composeVerb = new EmailComposeVerb();
      VerbRepository.getVerbRepository(-7881764549058890736L).register(composeVerb, -2985347935260258684L);
      VerbRepository.getVerbRepository(6270925675423896343L).register(composeVerb, -2985347935260258684L);
      Verb forwardEmailVerb = new EmailResponseVerb(602880, 9149, 13, false, true);
      VerbRepository.getVerbRepository(-110058785485458643L).register(forwardEmailVerb, -6822293833372928884L);
      Verb composeOptionsVerb = (Verb)(new Object(16978176, CommonResource.getBundle(), 20, EmailResources.getResourceBundle(), 128, 3735013535338552331L, 1000));
      VerbRepository.getVerbRepository(5244072729690617291L).register(composeOptionsVerb, -6822293833372928884L);
      VerbRepository.getVerbRepository(-2204303273264560528L).register(new MessageListOptionsVerb(), -6822293833372928884L);
      NotificationsManager.registerSource(-1845850106795451018L, new PackageManager$1(), 2);
      NotificationsManager.registerSource(-327746170160875990L, new PackageManager$2(), 2);
      Profiles profiles = Profiles.getInstance();
      if (profiles != null && profiles.pagingProfilePresent()) {
         PagingSupport.enablePagingSupport();
      }

      EmailMessageModelFactory emmf = new EmailMessageModelFactory();
      ApplicationRegistry.getApplicationRegistry().put(-6822293833372928884L, emmf);
      RecognizerRepository.registerRecognizer(-6822293833372928884L, emmf);
      StringPatternRepository.addPattern(new EmailAddressStringPattern());
      StringPatternRepository.addPattern(new PINAddressStringPattern());
      RIMModelFactoryRepository.addFactory(2497613418300956405L, new EmailAttachmentModelFactory());
      TransmissionService transmissionService = TransmissionServiceManager.get(8399767144006445082L);
      if (transmissionService != null) {
         EmailTransmissionServiceListener emailListener = EmailTransmissionServiceListener.createInstance();
         transmissionService.addTransmissionServiceListener(
            "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE", 100, emailListener
         );
         transmissionService.addTransmissionServiceListener(
            "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MORE", 100, emailListener
         );
         transmissionService.addTransmissionServiceListener(
            "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_ERROR", 100, emailListener
         );
         transmissionService.addTransmissionServiceListener(
            "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_DELIVERY", 100, emailListener
         );
         transmissionService.addTransmissionServiceListener(
            "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_FOLDER_MANAGEMENT", 100, emailListener
         );
         transmissionService.setDefaultTransmissionStatusListener(emailListener);
         NotificationsManager.registerNotificationsEngineListener(-1845850106795451018L, emailListener);
         NotificationsManager.registerNotificationsEngineListener(-327746170160875990L, emailListener);
         NotificationsManager.registerNotificationsEngineListener(6432934947797527350L, emailListener);
      }

      SyncManager syncManager = SyncManager.getInstance();
      EmailMessageSync emailMessageSync = new EmailMessageSync();
      syncManager.enableSynchronization(emailMessageSync);
      syncManager.addSyncEventListener(emailMessageSync);
      syncManager.enableSynchronization(new PINMessageSync(), true, 7);
      SavedEmailOTASync savedEmailSync = new SavedEmailOTASync();
      syncManager.enableSynchronization(savedEmailSync);
      syncManager.addSyncEventListener(savedEmailSync);
      EventLogger.register(-1237457833540244999L, "net.rim.blackberryemail", 2);
      registerHotKeys();
      initializeSecurityServiceColours();
      registerAllCMIMEProfiles();
      registerNativeAttachmentMIMEContentVerbRepositoryVerbs(CMIMEUtilities.isFileAttachmentAllowedByItPolicy());
   }

   private static final void registerAllCMIMEProfiles() {
      if (!ALLOW_ONLY_ONE_PROFILE_CONFIGURATION_FOR_CMIME) {
         ServiceBook sb = ServiceBook.getSB();
         ServiceRecord[] srs = sb.findRecordsByCid("CMIME");

         for (int i = srs.length - 1; i >= 0; i--) {
            ServiceRecord sr = srs[i];
            if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "CMIME", 1701707776)) {
               registerCMIMEProfile(sr);
            }
         }
      }
   }

   private static final void registerHotKeys() {
      Verb composeVerb = new EmailComposeVerb();
      if (!InternalServices.isReducedFormFactor()) {
         char hotKey = EmailResources.getString(144).charAt(0);
         Verb v = new EmailMoreVerb((byte)1);
         HotKeys.registerHotKey(2, hotKey, v, true);
         hotKey = EmailResources.getString(142).charAt(0);
         v = new EmailMoreVerb((byte)2);
         HotKeys.registerHotKey(2, hotKey, v, true);
         HotKeys.registerHotKey(1, EmailResources.getResourceBundle(), 1124, composeVerb, true);
         HotKeys.registerHotKey(3, EmailResources.getResourceBundle(), 1124, composeVerb, true);
      } else {
         HotKeys.registerHotKey(1, 'L', composeVerb, true);
         HotKeys.registerHotKey(3, 'L', composeVerb, true);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         adjustVerbs();
         initializeSecurityServiceColours();
      } else if (guid == -273986034351666339L) {
         registerHotKeys();
      } else if (guid == 4681343386835470834L) {
         EmailSendUtility.sendDuressMessage();
      } else if (guid == 2573494863350550132L) {
         initializeSecurityServiceColours();
      } else if (guid != -4220058463650496006L && guid != 2522898683889177438L && guid != 8288627527798139133L) {
         if (guid == 9206737719270818227L && data0 == 24 && data1 == 42) {
            initializeSecurityServiceColours();
         }
      } else {
         handleServiceBookEvents(guid, data0, data1, object0, object1);
      }
   }

   private static final void registerCMIMEProfile(ServiceRecord sr) {
      registerCMIMEProfile(sr, null);
   }

   private static final void registerCMIMEProfile(ServiceRecord sr, ServiceRecord sr2) {
      if (sr != null) {
         PackageManager$RelatedProfileName profileSettingName = new PackageManager$RelatedProfileName(sr.getName());
         long sourceID = CMIMEUtilities.getProfileSourceIDForService(sr);
         long source2ID = CMIMEUtilities.getProfileSourceIDForService(sr2);
         if (source2ID != -1 && sourceID != source2ID) {
            Profiles.getInstance().moveSource(source2ID, sourceID);
            deRegisterCMIMEProfile(sr2);
         }

         long[] relatedSources = NotificationsManager.getRelatedSourceIds(-1845850106795451018L, false);
         if (relatedSources != null && relatedSources.length == 0) {
            hideCMIMEProfile(-1845850106795451018L);
         }

         NotificationsManager.registerSource(sourceID, profileSettingName, 2, -1845850106795451018L);
         unHideCMIMEProfile(sourceID);
      }
   }

   private static final void deRegisterCMIMEProfile(ServiceRecord sr) {
      if (sr != null) {
         long sourceID = CMIMEUtilities.getProfileSourceIDForService(sr);
         hideCMIMEProfile(sourceID);
         long[] relatedSources = NotificationsManager.getRelatedSourceIds(-1845850106795451018L, false);
         if (relatedSources != null && relatedSources.length == 0) {
            unHideCMIMEProfile(-1845850106795451018L);
         }
      }
   }

   private static final void hideCMIMEProfile(long sourceID) {
      Profiles.getInstance().hideSource(sourceID);
   }

   private static final void unHideCMIMEProfile(long sourceID) {
      Profiles.getInstance().unHideSource(sourceID);
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      EmailHierarchy.crypt(generation);
   }

   private static final void initializeSecurityServiceColours() {
      Security security = Security.getInstance();
      generateAttribute(security.getSecurityITPolicyServiceColour(), "security-service-message-itpolicy", false);
      generateAttribute(security.getSecurityOtherServiceColour(), "security-service-message", false);
      generateAttribute(security.getSecurityITPolicyServiceColour(), "security-service-address-itpolicy", true);
      generateAttribute(security.getSecurityOtherServiceColour(), "security-service-address", true);
   }

   private static final void generateAttribute(int colour, String setName, boolean solid) {
      Theme theme = ThemeManager.getActiveTheme();
      Theme$Writer writer = theme.getWriterInternalDeprecated();
      ThemeAttributeSet$Writer attr = writer.createThemeAttributeSetWriter(setName);
      if (colour >= 0) {
         int length = 120;
         if (solid) {
            attr.setColor(0, colour);
         } else {
            Bitmap bitmap = (Bitmap)(new Object(length, length));
            Graphics g = (Graphics)(new Object(bitmap));
            int backgroundRGB = g.getBackgroundColor();

            for (int x = 0; x < length; x++) {
               for (int y = 0; y < length; y++) {
                  g.setColor((x + y) % length < length >> 1 ? colour : backgroundRGB);
                  g.drawPoint(x, y);
               }
            }

            attr.setBackgroundImage(bitmap);
         }
      }

      writer.put(attr, true);
   }

   private static final void handleServiceBookEvents(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4220058463650496006L || guid == 2522898683889177438L || guid == 8288627527798139133L) {
         if (guid == -4220058463650496006L) {
            if (object0 instanceof Object && !ALLOW_ONLY_ONE_PROFILE_CONFIGURATION_FOR_CMIME) {
               ServiceRecord sr = (ServiceRecord)object0;
               if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "CMIME", 1701707776)) {
                  registerCMIMEProfile(sr);
                  return;
               }
            }
         } else if (guid == 2522898683889177438L) {
            if (object0 instanceof Object && !ALLOW_ONLY_ONE_PROFILE_CONFIGURATION_FOR_CMIME) {
               ServiceRecord sr = (ServiceRecord)object0;
               if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "CMIME", 1701707776)) {
                  deRegisterCMIMEProfile(sr);
                  return;
               }
            }
         } else if (guid == 8288627527798139133L && object0 instanceof Object && !ALLOW_ONLY_ONE_PROFILE_CONFIGURATION_FOR_CMIME) {
            ServiceRecord sr = (ServiceRecord)object0;
            ServiceRecord sr2 = null;
            if (object0 instanceof Object) {
               sr2 = (ServiceRecord)object0;
            }

            if (StringUtilities.strEqualIgnoreCase(sr.getCid(), "CMIME", 1701707776)) {
               if (sr.getType() == 2) {
                  deRegisterCMIMEProfile(sr);
                  return;
               }

               registerCMIMEProfile(sr, sr2);
            }
         }
      }
   }

   private static final void registerNativeAttachmentMIMEContentVerbRepositoryVerbs(boolean register) {
      if (register) {
         deregisterVerbWithMIMEContentVerbRepository();
         registerVerbWithMIMEContentVerbRepository();
      } else {
         deregisterVerbWithMIMEContentVerbRepository();
      }
   }

   private static final void registerVerbWithMIMEContentVerbRepository() {
      Verb sendAsEmailComposeVerb = EmailComposeVerb.getSendAsVerb();
      Enumeration e = EncodedImage.getSupportedMIMETypes();

      while (e.hasMoreElements()) {
         String imageMime = (String)e.nextElement();
         MIMEContentVerbRepository.register(sendAsEmailComposeVerb, imageMime);
      }

      MIMEContentVerbRepository.register(sendAsEmailComposeVerb, "application/vnd.wap.connectivity-wbxml");
      MIMEContentVerbRepository.register(sendAsEmailComposeVerb, "video/3gpp");
      MIMEContentVerbRepository.register(sendAsEmailComposeVerb, "video/mp4");
      MIMEContentVerbRepository.register(sendAsEmailComposeVerb, "video/x-ms-wmv");
      MIMEContentVerbRepository.register(sendAsEmailComposeVerb, "video/quicktime");
      MIMEContentVerbRepository.register(sendAsEmailComposeVerb, "video/x-msvideo");
      MIMEContentVerbRepository.register(sendAsEmailComposeVerb, "audio/amr");
   }

   private static final void deregisterVerbWithMIMEContentVerbRepository() {
      Verb sendAsEmailComposeVerb = EmailComposeVerb.getSendAsVerb();
      Enumeration e = EncodedImage.getSupportedMIMETypes();

      while (e.hasMoreElements()) {
         String imageMime = (String)e.nextElement();
         MIMEContentVerbRepository.deregister(sendAsEmailComposeVerb, imageMime);
      }

      MIMEContentVerbRepository.deregister(sendAsEmailComposeVerb, "application/vnd.wap.connectivity-wbxml");
   }
}
