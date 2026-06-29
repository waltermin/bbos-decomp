package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.VoiceMailVerb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

public final class SpecialAddressCard implements PersistableRIMModel, PaintProvider, VerbProvider, FieldProvider {
   private long _typeUid;
   public static final long VOICE_MAIL = -7117173429217454741L;
   public static final long EMERGENCY_CALL = 2280195576896513113L;
   public static final long SIM_CALL_SETUP = -2948267102114848593L;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      return graphics.drawText(this.toString(), x, y, 0, width);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      if (this.getTypeUid() != -7117173429217454741L || !PhoneUtilities.getPrivateFlag(context, 66) && VoicemailIconManager.getInstance().isIndicatorOn()) {
         return null;
      }

      Array.resize(verbs, verbs.length + 1);
      verbs[verbs.length - 1] = new VoiceMailVerb();
      return verbs[verbs.length - 1];
   }

   final long getTypeUid() {
      return this._typeUid;
   }

   @Override
   public final Field getField(Object context) {
      return (Field)(new Object(this.toString()));
   }

   @Override
   public final int getOrder(Object context) {
      return 0;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   static final boolean is911Call(Object address) {
      return !(address instanceof SpecialAddressCard) ? false : ((SpecialAddressCard)address).getTypeUid() == 2280195576896513113L;
   }

   SpecialAddressCard(long typeUid) {
      this._typeUid = typeUid;
   }

   @Override
   public final String toString() {
      if (this._typeUid == -7117173429217454741L) {
         return PhoneResources.getString(190);
      } else {
         return this._typeUid == 2280195576896513113L ? PhoneResources.getString(185) : "";
      }
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof SpecialAddressCard)) {
         if (o instanceof CallerIDInfo) {
            Object address = ((CallerIDInfo)o).getAddress();
            if (address instanceof SpecialAddressCard) {
               if (((SpecialAddressCard)address).getTypeUid() == this._typeUid) {
                  return true;
               }

               return false;
            }
         }

         return false;
      } else {
         return ((SpecialAddressCard)o).getTypeUid() == this._typeUid;
      }
   }
}
