package net.rim.device.apps.internal.lbs.verbs;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;

public final class LBSVerbFactory implements VerbFactory {
   private static final long LBS_VERB_FACTORY_GUID = 2622913951318770504L;

   public static final void registerOnceOnSystemStart() {
      Object object = ApplicationRegistry.getApplicationRegistry().get(2622913951318770504L);
      if (object == null) {
         LBSVerbFactory lbsVerbFactory = new LBSVerbFactory();
         ApplicationRegistry.getApplicationRegistry().put(2622913951318770504L, lbsVerbFactory);
         VerbFactoryRepository.addFactory(-6650104226833963074L, lbsVerbFactory);
         VerbFactoryRepository.addFactory(-5325383713183591786L, lbsVerbFactory);
         VerbFactoryRepository.addFactory(-1011653757168863700L, lbsVerbFactory);
         VerbFactoryRepository.addFactory(-5544721730296222436L, lbsVerbFactory);
         VerbFactoryRepository.addFactory(5911208747185054768L, lbsVerbFactory);
      }
   }

   public static final void unregister() {
      Object object = ApplicationRegistry.getApplicationRegistry().remove(2622913951318770504L);
      if (object instanceof LBSVerbFactory) {
         LBSVerbFactory lbsVerbFactory = (LBSVerbFactory)object;
         VerbFactoryRepository.removeFactory(-6650104226833963074L, lbsVerbFactory);
         VerbFactoryRepository.removeFactory(-5325383713183591786L, lbsVerbFactory);
         VerbFactoryRepository.removeFactory(-1011653757168863700L, lbsVerbFactory);
         VerbFactoryRepository.removeFactory(-5544721730296222436L, lbsVerbFactory);
         VerbFactoryRepository.removeFactory(5911208747185054768L, lbsVerbFactory);
      }
   }

   private LBSVerbFactory() {
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      if (!ApplicationManager.getApplicationManager().isSystemLocked()) {
         ViewAddressVerb verb = null;
         if (ContextObject.getFlag(context, 11)) {
            Object focusedModel = ContextObject.get(context, 250);
            if (focusedModel instanceof Object) {
               MailingAddressModel address = (MailingAddressModel)focusedModel;
               Object addressCard = ContextObject.get(context, 252);
               verb = new ViewAddressVerb(1131008, 31);
               verb.setParameter(context);
               return new Object[]{verb};
            }
         }

         Object entry = ContextObject.get(context, 252);
         if (entry != null && entry instanceof Object) {
            AddressCardModel model = (AddressCardModel)entry;
            Verb[] verbs = new Object[0];

            for (int i = model.size() - 1; i >= 0; i--) {
               Object o = model.getAt(i);
               if (o instanceof Object) {
                  ContextObject verbContext = ContextObject.clone(context);
                  ContextObject.put(verbContext, 250, o);
                  verb = new ViewAddressVerb(1131008, 31);
                  verb.setParameter(verbContext);
                  Arrays.add(verbs, verb);
               }
            }

            return verbs;
         }
      }

      return null;
   }
}
