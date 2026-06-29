package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.email.EmailTransport;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;
import net.rim.vm.Array;

public final class EmailHeaderModelFactory extends RIMModelFactory {
   private static EmailHeaderModelFactory _factory;

   EmailHeaderModelFactory() {
   }

   private static final EmailHeaderModelFactory getInstance() {
      if (_factory == null) {
         _factory = (EmailHeaderModelFactory)ApplicationRegistry.getApplicationRegistry().waitFor(-8034039608019345282L);
      }

      return _factory;
   }

   @Override
   public final Object createInstance(Object context) {
      int headerType = ContextObject.getIntegerData(context, 0);
      return this.internalCreateInstance(headerType, context);
   }

   private final EmailHeaderModel internalCreateInstance(int headerType, Object context) {
      switch (headerType) {
         case 0:
         default:
            return new ToEmailHeaderModel(context);
         case 1:
            return new CcEmailHeaderModel(context);
         case 2:
            return new BccEmailHeaderModel(context);
         case 3:
            return new FromEmailHeaderModel(context);
         case 4:
            return new SenderEmailHeaderModel(context);
         case 5:
            return new ReplyToEmailHeaderModel(context);
         case 6:
            return new OriginalRecipientHeaderModel(context);
      }
   }

   public static final EmailHeaderModel createEmailHeaderModel(Object context) {
      return (EmailHeaderModel)getInstance().createInstance(context);
   }

   public static final EmailHeaderModel createInstance(int headerType, Object context) {
      return getInstance().internalCreateInstance(headerType, context);
   }

   public static final EmailHeaderModel createInstance(String[] stringPair, int headerType, ContextObject context) {
      switch (headerType) {
         case 0:
         default:
            return new ToEmailHeaderModel(stringPair, context);
         case 1:
            return new CcEmailHeaderModel(stringPair, context);
         case 2:
            return new BccEmailHeaderModel(stringPair, context);
         case 3:
            return new FromEmailHeaderModel(stringPair, context);
         case 4:
            return new SenderEmailHeaderModel(stringPair, context);
         case 5:
            return new ReplyToEmailHeaderModel(stringPair, context);
         case 6:
            return new OriginalRecipientHeaderModel(stringPair, context);
      }
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailHeaderModel;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      boolean bcc = true;
      boolean cc = true;
      Object o = ContextObject.get(context, 32241034113959076L);

      label54:
      try {
         if (o instanceof TransitoryMessagePropertiesModel) {
            TransitoryMessagePropertiesModel tmpm = (TransitoryMessagePropertiesModel)o;
            SendMethod s = tmpm.getSelectedSendMethod();
            ServiceRecord sr = s.getServiceRecord();
            EmailTransport t = (EmailTransport)sr.getTransport();
            cc = t.isCcSupported();
            bcc = t.isBccSupported();
         }
      } finally {
         break label54;
      }

      bcc &= ITPolicy.getBoolean(16, true);
      int ignoreType = -1;
      Integer ignoreTypeInteger = (Integer)ContextObject.get(context, -3076179409848094191L);
      if (ignoreTypeInteger != null) {
         ignoreType = ignoreTypeInteger;
      }

      int index = -1;
      Verb[] verbs = new Object[3];
      if (ignoreType != 0) {
         verbs[++index] = new EmailHeaderAddVerb(0);
      }

      if (cc && ignoreType != 1) {
         verbs[++index] = new EmailHeaderAddVerb(1);
      }

      if (bcc && ignoreType != 2) {
         verbs[++index] = new EmailHeaderAddVerb(2);
      }

      Array.resize(verbs, index + 1);
      return verbs;
   }
}
