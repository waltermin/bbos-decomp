package net.rim.device.apps.internal.blackberryemail.sendmethods;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncodingProvider;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethodFactory;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.classification.MessageClassification;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCache;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCacheData;
import net.rim.device.apps.internal.blackberryemail.properties.MessagePropertiesDefaults;

public class SendMethodSelector {
   private SendMethodFactory[] _registeredFactories = new SendMethodFactory[0];
   private SendMethodSelector$EmailBodyProviderRecognizer _emailBodyProviderRecognizer = new SendMethodSelector$EmailBodyProviderRecognizer(null);
   private static final long ID = -7659632296316850234L;
   private static SendMethodSelector _instance;

   public static SendMethodSelector getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (SendMethodSelector)applicationRegistry.getOrWaitFor(-7659632296316850234L);
         if (_instance == null) {
            _instance = new SendMethodSelector();
            applicationRegistry.put(-7659632296316850234L, _instance);
         }
      }

      return _instance;
   }

   private SendMethodSelector() {
   }

   public synchronized boolean registerSendMethodFactory(SendMethodFactory sendMethodFactory) {
      long newEncodingUID = sendMethodFactory.getEncodingUID();
      int newPriority = sendMethodFactory.getPriority();
      int numRegisteredFactories = this._registeredFactories.length;

      for (int i = 0; i < numRegisteredFactories; i++) {
         if (this._registeredFactories[i].getEncodingUID() == newEncodingUID) {
            if (this._registeredFactories[i].getPriority() < newPriority) {
               this._registeredFactories[i] = sendMethodFactory;
               return true;
            }

            return false;
         }
      }

      Arrays.add(this._registeredFactories, sendMethodFactory);
      return true;
   }

   public synchronized boolean unregisterSendMethodFactory(SendMethodFactory sendMethodFactory) {
      int registeredIndex = Arrays.getIndex(this._registeredFactories, sendMethodFactory);
      if (registeredIndex < 0) {
         return false;
      }

      Arrays.removeAt(this._registeredFactories, registeredIndex);
      return true;
   }

   public synchronized SendMethod[] getSendMethods(
      EmailMessageModel emailMessageModel, ServiceRecord serviceRecord, MessageClassification classification, Object context
   ) {
      SendMethod[] sendMethods = new SendMethod[0];
      int numSecureEncodingsUsed = 0;
      int minimumEncodingActions = classification != null ? classification.getMinimumEncodingActions() : 0;
      int numSendMethodFactories = this._registeredFactories.length;

      for (int i = 0; i < numSendMethodFactories; i++) {
         SendMethodFactory sendMethodFactory = this._registeredFactories[i];
         long currentEncodingUID = sendMethodFactory.getEncodingUID();
         if (currentEncodingUID != 182808770805039415L
            || (!ITPolicy.getBoolean(24, 5, false) || serviceRecord == null) && (!ITPolicy.getBoolean(24, 6, false) || serviceRecord != null)) {
            SendMethod[] currentSendMethods = sendMethodFactory.create(emailMessageModel, serviceRecord, context);
            int numCurrentSendMethods = currentSendMethods == null ? 0 : currentSendMethods.length;

            for (int j = 0; j < numCurrentSendMethods; j++) {
               int currentEncodingActions = currentSendMethods[j].getEncodingAction();
               int missingRequiredActions = ~currentEncodingActions & minimumEncodingActions;
               if (missingRequiredActions == 0) {
                  Arrays.add(sendMethods, currentSendMethods[j]);
               }
            }

            if (currentEncodingUID != 182808770805039415L) {
               numSecureEncodingsUsed++;
            }
         }
      }

      int sendMethodFlags = 0;
      if (numSecureEncodingsUsed > 1) {
         sendMethodFlags |= 4;
      }

      int numSendMethods = sendMethods.length;

      for (int i = 0; i < numSendMethods; i++) {
         sendMethods[i].setFlags(sendMethodFlags);
      }

      return sendMethods;
   }

   public int selectDefaultSendMethod(
      SendMethod[] sendMethods,
      EmailMessageModel emailMessageModel,
      long preferredEncodingUID,
      int preferredEncodingAction,
      String firstRecipientAddress,
      Object context
   ) {
      int numSendMethods = sendMethods != null ? sendMethods.length : 0;
      switch (numSendMethods) {
         case -1:
            int preferredSendMethod = this.getPreferredSendMethod(sendMethods, emailMessageModel, preferredEncodingUID, preferredEncodingAction, context);
            if (preferredSendMethod >= 0) {
               return preferredSendMethod;
            } else {
               int overridingDefaultSendMethod = this.getOverridingDefaultSendMethod(sendMethods, emailMessageModel, context);
               if (overridingDefaultSendMethod >= 0) {
                  return overridingDefaultSendMethod;
               } else {
                  int originalItemSendMethod = this.getOriginalItemSendMethod(sendMethods, emailMessageModel, context);
                  if (originalItemSendMethod >= 0) {
                     return originalItemSendMethod;
                  } else {
                     int firstRecipientSendMethod = this.getFirstRecipientSendMethod(sendMethods, emailMessageModel, firstRecipientAddress, context);
                     if (firstRecipientSendMethod >= 0) {
                        return firstRecipientSendMethod;
                     }

                     return -1;
                  }
               }
            }
         case 0:
         default:
            return -1;
         case 1:
            return 0;
      }
   }

   private int getPreferredSendMethod(
      SendMethod[] sendMethods, EmailMessageModel emailMessageModel, long preferredEncodingUID, int preferredEncodingAction, Object context
   ) {
      if (preferredEncodingUID == -1 && preferredEncodingAction == -1) {
         return -1;
      }

      int preferredIndex = this.locateFullOrPartialMatch(sendMethods, emailMessageModel, preferredEncodingUID, preferredEncodingAction, false, context);
      return preferredIndex >= 0 ? preferredIndex : 0;
   }

   private int getOverridingDefaultSendMethod(SendMethod[] sendMethods, EmailMessageModel emailMessageModel, Object context) {
      MessagePropertiesDefaults messagePropertiesDefaults = MessagePropertiesDefaults.getInstance();
      long messagingOptionsEncodingUID = messagePropertiesDefaults.getEncodingUID();
      int messagingOptionsEncodingAction = messagePropertiesDefaults.getEncodingAction();
      int numSendMethods = sendMethods.length;

      for (int i = 0; i < numSendMethods; i++) {
         SendMethod currentSendMethod = sendMethods[i];
         if (currentSendMethod.getEncodingUID() == messagingOptionsEncodingUID
            && currentSendMethod.getEncodingAction() == messagingOptionsEncodingAction
            && (currentSendMethod.getFlags() & 8) != 0
            && currentSendMethod.isConfigurable(emailMessageModel, context)) {
            return i;
         }
      }

      return -1;
   }

   private int getOriginalItemSendMethod(SendMethod[] sendMethods, EmailMessageModel emailMessageModel, Object context) {
      Object originalItem = ContextObject.get(context, 245);
      if (originalItem instanceof EmailMessageModel) {
         EmailMessageModel originalMessage = (EmailMessageModel)originalItem;
         Object obj = SubmemberUtilities.getFirstSubmember(originalMessage, this._emailBodyProviderRecognizer);
         if (obj instanceof EncodingProvider) {
            EncodingProvider encodingProvider = (EncodingProvider)obj;
            long originalItemEncodingUID = encodingProvider.getEncodingUID();
            int originalItemEncodingAction = encodingProvider.getEncodingAction();
            return this.locateFullOrPartialMatch(sendMethods, emailMessageModel, originalItemEncodingUID, originalItemEncodingAction, true, context);
         }
      }

      return -1;
   }

   private int getFirstRecipientSendMethod(SendMethod[] sendMethods, EmailMessageModel emailMessageModel, String firstRecipientAddress, Object context) {
      if (firstRecipientAddress == null) {
         return -1;
      }

      RecipientCacheData cacheData = RecipientCache.getInstance().get(firstRecipientAddress);
      if (cacheData != null) {
         long recipientCacheEncodingUID = cacheData.getEncodingUID();
         int recipientCacheEncodingAction = 0;
         int recipientCacheFlags = cacheData.getFlags();
         if ((recipientCacheFlags & 512) != 0) {
            recipientCacheEncodingAction |= 2;
         }

         if ((recipientCacheFlags & 256) != 0) {
            recipientCacheEncodingAction |= 1;
         }

         int recipientCacheIndex = this.locateFullOrPartialMatch(
            sendMethods, emailMessageModel, recipientCacheEncodingUID, recipientCacheEncodingAction, true, context
         );
         if (recipientCacheIndex >= 0) {
            return recipientCacheIndex;
         }
      }

      MessagePropertiesDefaults messagePropertiesDefaults = MessagePropertiesDefaults.getInstance();
      long messagingOptionsEncodingUID = messagePropertiesDefaults.getEncodingUID();
      int messagingOptionsEncodingAction = messagePropertiesDefaults.getEncodingAction();
      int messagingOptionsIndex = this.locateFullOrPartialMatch(
         sendMethods, emailMessageModel, messagingOptionsEncodingUID, messagingOptionsEncodingAction, true, context
      );
      if (messagingOptionsIndex >= 0) {
         return messagingOptionsIndex;
      }

      int plainTextIndex = this.locateFullOrPartialMatch(sendMethods, emailMessageModel, 182808770805039415L, 0, true, context);
      return plainTextIndex >= 0 ? plainTextIndex : 0;
   }

   private int locateFullOrPartialMatch(
      SendMethod[] sendMethods,
      EmailMessageModel emailMessageModel,
      long desiredEncodingUID,
      int desiredEncodingAction,
      boolean matchOnlyIfConfigurable,
      Object context
   ) {
      int bestPartialMatchIndex = -1;
      int bestPartialMatchAction = 0;
      int currentEncodingAction = -1;
      int numSendMethods = sendMethods.length;

      for (int i = 0; i < numSendMethods; i++) {
         SendMethod currentSendMethod = sendMethods[i];
         if ((!matchOnlyIfConfigurable || currentSendMethod.isConfigurable(emailMessageModel, context))
            && (desiredEncodingUID == 182808770805039415L || desiredEncodingUID == currentSendMethod.getEncodingUID())) {
            currentEncodingAction = currentSendMethod.getEncodingAction();
            if ((desiredEncodingAction & currentEncodingAction) == desiredEncodingAction) {
               if (currentEncodingAction == desiredEncodingAction) {
                  return i;
               }

               if (bestPartialMatchIndex < 0 || currentEncodingAction < bestPartialMatchAction) {
                  bestPartialMatchAction = currentEncodingAction;
                  bestPartialMatchIndex = i;
               }
            }
         }
      }

      return bestPartialMatchIndex;
   }
}
