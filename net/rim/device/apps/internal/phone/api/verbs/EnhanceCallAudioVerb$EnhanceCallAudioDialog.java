package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.EnhanceCallAudioServices;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.PopupDialog;

class EnhanceCallAudioVerb$EnhanceCallAudioDialog extends PopupDialog implements PhoneEventListener {
   static RadioButtonGroup _radioButtons;
   static RadioButtonField _rbfFocus;
   static int _optionNum;

   public EnhanceCallAudioVerb$EnhanceCallAudioDialog() {
      super((Manager)(new Object(1153202979583557632L)));
      this.setModal(true);
      _radioButtons = (RadioButtonGroup)(new Object());
      _optionNum = 0;
      _rbfFocus = null;
      this.add((Field)(new Object(PhoneResources.getString(6324), 36028797018963968L)));
      this.add((Field)(new Object(4)));
      this.add(this.buildOption(3, 6327, _radioButtons));
      this.add(this.buildOption(1, 6325, _radioButtons));
      this.add(this.buildOption(2, 6326, _radioButtons));
      this.add((Field)(new Object(4)));
      VoiceServices.addPhoneEventListener(this);
      if (_rbfFocus != null) {
         _rbfFocus.setFocus();
      }
   }

   @Override
   public void close(int reason) {
      VoiceServices.removePhoneEventListener(this);
      super.close(reason);
   }

   @Override
   public void phoneEventNotify(int eventId, int callId, Object context) {
      if (eventId == 1002 || eventId == 1006) {
         this.close(-1);
      }
   }

   private void selectNewState() {
      int oldState = EnhanceCallAudioServices.getInstance().getState();
      int newState = _radioButtons.getSelectedIndex();
      if (newState == 3) {
         newState = 0;
      }

      if (oldState != newState) {
         EnhanceCallAudioServices.getInstance().setState(newState);
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      super.keyChar(key, status, time);
      if (key == '\n') {
         this.selectNewState();
         this.close(0);
         return true;
      }

      if (key == 27) {
         this.close(-1);
      }

      return true;
   }

   @Override
   protected boolean invokeAction(int action) {
      super.invokeAction(action);
      if (action == 1) {
         this.selectNewState();
         this.close(0);
         return true;
      } else {
         return false;
      }
   }

   private HorizontalFieldManager buildOption(int state, int text, RadioButtonGroup rbg) {
      HorizontalFieldManager optionLine = (HorizontalFieldManager)(new Object(1152921504606846976L));
      boolean selected = false;
      if (_optionNum++ == EnhanceCallAudioServices.getInstance().getState()) {
         selected = true;
      }

      RadioButtonField rbf = (RadioButtonField)(new Object(" ", rbg, selected, 2147483648L));
      if (selected) {
         _rbfFocus = rbf;
         rbf.select(true);
      }

      ImageField imageField = (ImageField)(new Object());
      imageField.setImage(EnhanceCallAudioServices.getInstance().getIconForState(state));
      VerticalFieldManager centeredImage = (VerticalFieldManager)(new Object(51539607552L));
      centeredImage.add(imageField);
      LabelField labelField = (LabelField)(new Object(PhoneResources.getString(text)));
      optionLine.add(rbf);
      optionLine.add(centeredImage);
      optionLine.add((Field)(new Object(4)));
      optionLine.add(labelField);
      return optionLine;
   }
}
