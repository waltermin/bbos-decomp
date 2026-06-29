package net.rim.device.apps.internal.sms.voicemail;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.sms.SMSChangeStatusVerb;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.SMSOpenVerb;
import net.rim.device.apps.internal.sms.SMSUiRegistry;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.apps.internal.sms.ui.ViewerProvider;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.vm.Array;

public class VoicemailSMSModel extends SMSModel implements PersistableRIMModel {
   public VoicemailSMSModel(Object initialData) {
      super(initialData);
   }

   @Override
   protected void paintAddress(ColumnPainter painter, Object context) {
      painter.drawText(3, SMSResources.getString(35), false);
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      boolean inView = ContextObject.getFlag(context, 37);
      Verb defaultVerb = null;
      int numberOfVerbsAdded = 0;
      Array.resize(verbs, verbs.length + 10);
      if (!ContextObject.getFlag(context, 5) && !inView) {
         if (this.flagsSet(1)) {
            verbs[numberOfVerbsAdded++] = new SMSChangeStatusVerb(602450, 16, 0, 1, 0, this, null);
         } else {
            verbs[numberOfVerbsAdded++] = new SMSChangeStatusVerb(602448, 14, 1, 0, 0, this, null);
         }

         if (!this.flagsSet(16)) {
            verbs[numberOfVerbsAdded++] = new SMSChangeStatusVerb(602480, 18, 16, 0, 0, this, null);
         }

         defaultVerb = new SMSOpenVerb(this);
         verbs[numberOfVerbsAdded++] = defaultVerb;
         numberOfVerbsAdded += this.addVoicemailMessageOverSMSVerbs(verbs, numberOfVerbsAdded, context);
      }

      if (!VoicemailIconManager.getInstance().isIndicatorOn()) {
         VerbRepository vr = VerbRepository.getVerbRepository(-1180661228443544094L);
         Verb[] vmailVerbs = vr.getVerbs(3797587162219887872L);
         if (vmailVerbs != null) {
            Array.resize(verbs, verbs.length + vmailVerbs.length);

            for (int i = 0; i < vmailVerbs.length; i++) {
               if (ContextObject.getFlag(context, 37) && vmailVerbs[i] instanceof SetParameter) {
                  Verb var10000 = vmailVerbs[i];
                  if (vmailVerbs[i] instanceof Copyable) {
                     Verb copy = (Verb)((Copyable)var10000).copy();
                     ((SetParameter)copy).setParameter(new ContextObject(122));
                     verbs[numberOfVerbsAdded++] = copy;
                     if (defaultVerb == null) {
                        defaultVerb = copy;
                     }
                     continue;
                  }
               }

               verbs[numberOfVerbsAdded++] = vmailVerbs[i];
            }
         }
      }

      Array.resize(verbs, numberOfVerbsAdded);
      return defaultVerb;
   }

   @Override
   public int match(Object criteria) {
      if (!(criteria instanceof SearchCriterion)) {
         return super.match(criteria);
      }

      SearchCriterion crit = (SearchCriterion)criteria;
      switch (crit.getType()) {
         case 18:
            return 1;
         default:
            return super.match(criteria);
      }
   }

   @Override
   public Field getField(Object context) {
      Object delegateUi = SMSUiRegistry.getRegistry().getCurrentUi();
      if (delegateUi != null && delegateUi instanceof FieldProvider) {
         ContextObject contObj = ContextObject.castOrCreate(context);
         ContextObject.put(contObj, 250, this);
         return ((FieldProvider)delegateUi).getField(contObj);
      } else {
         VerticalFieldManager field = new VerticalFieldManager(1152921504606846976L);
         field.setCookie(this);
         Field titleField = this.getTitleField(context);
         field.add(titleField);
         field.add(new SeparatorField());
         ImageField iconField = new ImageField(36028797019029504L);
         iconField.setImage(MessageIcons.getIcons().getImage(this.getOverallStatusIcon()));
         HorizontalFieldManager statusHfm = new HorizontalFieldManager();
         statusHfm.add(iconField);
         StringBuffer stringBuffer = new StringBuffer();
         stringBuffer.append(' ');
         DateFormat.getInstance(53).formatLocal(stringBuffer, super._payload.getDisplayDate());
         statusHfm.add(new LabelField(stringBuffer, 18014398509481984L));
         field.add(statusHfm);
         RichTextField rtf = new RichTextField(this.getBody(), 18014398509481984L);
         rtf.setCookie(this);
         field.add(rtf);
         return field;
      }
   }

   @Override
   public Object invokeHotkey(Object context, int hotkeyID) {
      switch (hotkeyID) {
         case 152:
            Verb verb;
            if (this.flagsSet(1)) {
               verb = new SMSChangeStatusVerb(602450, 16, 0, 1, 0, this, null);
            } else {
               verb = new SMSChangeStatusVerb(602448, 14, 1, 0, 0, this, null);
            }

            return verb.invoke(context);
         default:
            return null;
      }
   }

   @Override
   public ModelScreen getViewer(Object context) {
      Object delegateUi = SMSUiRegistry.getRegistry().getCurrentUi();
      if (delegateUi != null && delegateUi instanceof ViewerProvider) {
         ContextObject contObj = ContextObject.castOrCreate(context);
         ContextObject.put(contObj, 250, this);
         return ((ViewerProvider)delegateUi).getViewer(contObj);
      } else {
         return new VoicemailViewerScreen(context);
      }
   }

   @Override
   protected int getType() {
      return 1;
   }

   @Override
   public boolean isAddressEmpty(Object context) {
      return true;
   }

   private Field getTitleField(Object context) {
      Field addressField = null;
      if (super._payload != null) {
         PersistableRIMModel[] addresses = super._payload.getAddresses();
         if (addresses.length > 0) {
            PersistableRIMModel var10000 = addresses[0];
            if (addresses[0] instanceof FieldProvider) {
               FieldProvider fieldProvider = (FieldProvider)var10000;
               ContextObject addressContextObject = ContextObject.clone(context);
               addressContextObject.setFlag(1);
               addressContextObject.clearFlag(0);
               addressContextObject.setFlag(9);
               addressField = fieldProvider.getField(addressContextObject);
            }
         }
      }

      String voiceMailString = SMSResources.getString(35);
      HorizontalFieldManager hfm = new HorizontalFieldManager();
      if (addressField != null) {
         hfm.add(new LabelField(voiceMailString + " ("));
         hfm.add(addressField);
         hfm.add(new LabelField(")"));
      } else {
         hfm.add(new LabelField(voiceMailString));
      }

      return hfm;
   }
}
