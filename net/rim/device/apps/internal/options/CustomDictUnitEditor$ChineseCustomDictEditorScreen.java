package net.rim.device.apps.internal.options;

import java.util.Vector;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.itie.IMContext;
import net.rim.tid.util.PinInfo;

class CustomDictUnitEditor$ChineseCustomDictEditorScreen extends CustomDictUnitEditor$CustomDictEditorScreen {
   private CustomDictUnitEditor$ChineseCustomDictEditorScreen _editorScreen;
   private PinInfo _pinInfo;
   private SLControlObject _controlObject;
   private byte _learningStateSaved;
   private byte _mmaStateSaved;
   private byte _predictiveStateSaved;
   private final CustomDictUnitEditor this$0;

   public CustomDictUnitEditor$ChineseCustomDictEditorScreen(CustomDictUnitEditor _1, String title, String initialString) {
      super(title, initialString);
      this.this$0 = _1;
      this._pinInfo = new PinInfo();
      this._controlObject = (SLControlObject)InputContext.getInstance().getInputMethodControlObject();
      this._editorScreen = this;
      if (initialString != null && initialString.length() > 1) {
         PinInfo info = (PinInfo)CustomWordlistScreen.getCustomDictionary().find(initialString);
         if (info != null) {
            this._pinInfo.set(info);
         }
      }
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         byte[] param = new byte[1];
         this._controlObject.actionPerformed(26, param);
         this._learningStateSaved = param[0];
         this._controlObject.actionPerformed(-116, param);
         this._mmaStateSaved = param[0];
         this._controlObject.actionPerformed(39, param);
         this._predictiveStateSaved = param[0];
      } else {
         this._controlObject.actionPerformed(this._learningStateSaved, null);
         this._controlObject.actionPerformed(this._mmaStateSaved, null);
         this._controlObject.actionPerformed(this._predictiveStateSaved, null);
      }
   }

   @Override
   protected boolean accept() {
      if (!super.accept()) {
         return false;
      }

      String newEntry = this.this$0._mainScreen.getText();
      String originalReplacedString = "";
      this._pinInfo.setWord(newEntry);
      if (InputContext.getInstance().getActiveInputMethodID() == 16384) {
         this.createSinglePinInfoFromVector();
         this._pinInfo.setWord(newEntry);
      }

      if (newEntry.equals(originalReplacedString)) {
         Dialog.inform(OptionsResources.getString(1462));
         return false;
      }

      if (this.this$0._model != null) {
         originalReplacedString = (String)this.this$0._model.getEntry();
      }

      if ((originalReplacedString.equals(newEntry) || !CustomWordlistScreen.getCustomDictionary().contains(this._pinInfo))
         && !CustomWordlistScreen.getCustomDictionary().isInRepository(this._pinInfo)) {
         if (newEntry.length() >= 2 && newEntry.length() <= 6) {
            if (!this.this$0._context.getFlag(6) && this.this$0._context.getFlag(0)) {
               CustomWordlistScreen.getCustomDictionary().remove(originalReplacedString);
            }

            CustomWordlistScreen.getCustomDictionary().add(this._pinInfo);
            this._pinInfo = new PinInfo();
            this.close();
            return true;
         } else {
            Dialog.inform(OptionsResources.getString(1973));
            this.makeComposed();
            return false;
         }
      } else {
         Dialog.inform(OptionsResources.getString(1463));
         this.makeComposed();
         return false;
      }
   }

   public void createSinglePinInfoFromVector() {
      CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField temp = (CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField)this.getEditField();
      Vector pinInfoVector = temp.getPinInfoFR();
      this._pinInfo = new PinInfo();
      int i = 0;

      while (pinInfoVector.size() > 0) {
         PinInfo curInfo = (PinInfo)pinInfoVector.elementAt(i);
         pinInfoVector.removeElementAt(i);
         this._pinInfo.setFR(curInfo.getPinIds(), curInfo.getTones(), curInfo.getCharsIndexes(), curInfo.length());
      }
   }

   @Override
   public void makeComposed() {
      BasicEditField field = this.getEditField();
      if (InputContext.getInstance().getActiveInputMethodID() != 16384) {
         int length = field.getCommittedTextLength();
         if (length > 0) {
            IMContext context = (IMContext)InputContext.getInstance();
            context.setComposedText(0, length);
         }

         this._controlObject.actionPerformed(154, this._pinInfo);
      } else {
         field.setText("");
      }
   }

   @Override
   public void setType(int type) {
      BasicEditField editField = this.getEditField();
      if (editField != null) {
         this.delete(editField);
      }

      editField = new CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField(this, 4503601774870656L);
      this.setEditField(editField);
      this.add(editField);
   }
}
