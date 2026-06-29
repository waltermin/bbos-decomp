package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.apps.internal.sms.SMSUiRegistry;
import net.rim.device.apps.internal.sms.resources.SMSResources;

public class SMSVerbFactory implements GlobalEventListener {
   private Verb _smsComposeVerb;
   private Verb _useOnceVerb;
   private Verb _useOnceButtonVerb;
   private Verb _smsForwardVerb;
   private boolean _registerSMS;
   public static final long SMS_COMPOSE_VERB;
   public static final long SMS_FORWARD_VERB;
   public static final long USE_ONCE_VERB;
   public static final long USE_ONCE_BUTTON_VERB;
   private static final long REGISTER_SMS;

   public SMSVerbFactory() {
      this.registerVerb();
   }

   private void registerVerb() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      this._smsComposeVerb = (Verb)applicationRegistry.get(6790437200588381895L);
      if (this._smsComposeVerb == null) {
         this._smsComposeVerb = new SMSComposeVerb();
         this._useOnceVerb = this.createUseOnceVerb();
         this._useOnceButtonVerb = new UseOnceSMSButtonVerb(this._useOnceVerb);
         this._smsForwardVerb = new SMSForwardVerb(true);
         applicationRegistry.replace(6790437200588381895L, this._smsComposeVerb);
         applicationRegistry.replace(3717785362621998069L, this._smsForwardVerb);
         applicationRegistry.replace(-3014132759451700551L, this._useOnceVerb);
         applicationRegistry.replace(6105870237412407148L, this._useOnceButtonVerb);
      }

      if (!SMSPacketHeader.isSendSupported()) {
         this._registerSMS = false;
         applicationRegistry.replace(8570221746716860371L, new Object(this._registerSMS));
         VerbRepository.getVerbRepository(-7881764549058890736L).deregister(this._smsComposeVerb, 3797587162219887872L);
         VerbRepository.getVerbRepository(-7881764549058890736L).deregister(this._smsComposeVerb, -2985347935260258684L);
         VerbRepository.getVerbRepository(-6761056765378641298L).deregister(this._smsComposeVerb, 3797587162219887872L);
         VerbRepository.getVerbRepository(8016149483483360697L).deregister(this._useOnceVerb, 3797587162219887872L);
         VerbRepository.getVerbRepository(-5389783330697330291L).deregister(this._useOnceButtonVerb, 3797587162219887872L);
         VerbRepository.getVerbRepository(-110058785485458643L).deregister(this._smsForwardVerb, -7381165762800557185L);
      } else {
         Boolean flag = (Boolean)applicationRegistry.get(8570221746716860371L);
         if (flag == null) {
            this._registerSMS = false;
         } else {
            this._registerSMS = flag;
         }

         SMSUiRegistry uiRegistry = SMSUiRegistry.getRegistry();
         Object delegate = uiRegistry.getCurrentUi();
         boolean composeRegistered = false;
         if (delegate != null && delegate instanceof Object) {
            ContextObject context = (ContextObject)(new Object(44));
            Verb delegateCompose = ((VerbProvider)delegate).getVerbs(context, new Object[0]);
            if (this._smsComposeVerb != delegateCompose) {
               this.updateRegisteredVerbs(delegateCompose, applicationRegistry);
               composeRegistered = true;
            }
         } else if (uiRegistry.getUiDelegates().size() > 0) {
            this.updateRegisteredVerbs(new SMSComposeVerb(), applicationRegistry);
            composeRegistered = true;
         }

         if (!this._registerSMS) {
            this._registerSMS = true;
            applicationRegistry.replace(8570221746716860371L, new Object(this._registerSMS));
            if (!composeRegistered) {
               VerbRepository.getVerbRepository(-7881764549058890736L).register(this._smsComposeVerb, 3797587162219887872L);
               if (SMSService.isEmailAddressAsSMSAddressSupported()) {
                  VerbRepository.getVerbRepository(-7881764549058890736L).register(this._smsComposeVerb, -2985347935260258684L);
               }

               VerbRepository.getVerbRepository(-6761056765378641298L).register(this._smsComposeVerb, 3797587162219887872L);
            }

            VerbRepository.getVerbRepository(8016149483483360697L).register(this._useOnceVerb, 3797587162219887872L);
            VerbRepository.getVerbRepository(-5389783330697330291L).register(this._useOnceButtonVerb, 3797587162219887872L);
            VerbRepository.getVerbRepository(-110058785485458643L).register(this._smsForwardVerb, -7381165762800557185L);
         }
      }

      this._useOnceVerb = (Verb)applicationRegistry.get(-3014132759451700551L);
      this._useOnceButtonVerb = (Verb)applicationRegistry.get(6105870237412407148L);
   }

   private Verb createUseOnceVerb() {
      if (SMSService.isEmailAddressAsSMSAddressSupported()) {
         Verb[] wrappedVerbs = new Object[]{new UseOnceSMSAddressVerb(this._smsComposeVerb, 0), new UseOnceSMSAddressVerb(this._smsComposeVerb, 1)};
         return (Verb)(new Object(
            SMSResources.getString(190),
            SMSResources.getString(415),
            wrappedVerbs[0].getOrdering(),
            wrappedVerbs,
            new Object[]{SMSResources.getString(418), SMSResources.getString(414)},
            wrappedVerbs[0]
         ));
      } else {
         return new UseOnceSMSAddressVerb(this._smsComposeVerb);
      }
   }

   private void updateRegisteredVerbs(Verb compose, ApplicationRegistry applicationRegistry) {
      VerbRepository.getVerbRepository(-7881764549058890736L).deregister(this._smsComposeVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-7881764549058890736L).deregister(this._smsComposeVerb, -2985347935260258684L);
      VerbRepository.getVerbRepository(-6761056765378641298L).deregister(this._smsComposeVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(8016149483483360697L).deregister(this._useOnceVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-5389783330697330291L).deregister(this._useOnceButtonVerb, 3797587162219887872L);
      this._smsComposeVerb = compose;
      this._useOnceVerb = this.createUseOnceVerb();
      this._useOnceButtonVerb = new UseOnceSMSButtonVerb(this._useOnceVerb);
      applicationRegistry.replace(6790437200588381895L, this._smsComposeVerb);
      applicationRegistry.replace(-3014132759451700551L, this._useOnceVerb);
      applicationRegistry.replace(6105870237412407148L, this._useOnceButtonVerb);
      VerbRepository.getVerbRepository(-7881764549058890736L).register(this._smsComposeVerb, 3797587162219887872L);
      if (SMSService.isEmailAddressAsSMSAddressSupported()) {
         VerbRepository.getVerbRepository(-7881764549058890736L).register(this._smsComposeVerb, -2985347935260258684L);
      }

      VerbRepository.getVerbRepository(-6761056765378641298L).register(this._smsComposeVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(8016149483483360697L).register(this._useOnceVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-5389783330697330291L).register(this._useOnceButtonVerb, 3797587162219887872L);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L || guid == 7884295420352689779L) {
         this.registerVerb();
      }
   }
}
