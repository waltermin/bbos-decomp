package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.customWordRepository.ja.JapaneseCustomWord;

class CustomDictUnitEditor$JapaneseCustomDictEditorScreen extends CustomDictUnitEditor$CustomDictEditorScreen {
   private SLControlObject _controlObject;
   private CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField _candidate;
   private CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField _reading;
   private final CustomDictUnitEditor this$0;

   public CustomDictUnitEditor$JapaneseCustomDictEditorScreen(CustomDictUnitEditor _1, String title, String initialString) {
      super(CustomDictUnitEditor$CustomDictEditorScreen._rb.getString(22), "");
      this.this$0 = _1;
      this._controlObject = (SLControlObject)InputContext.getInstance().getInputMethodControlObject();
   }

   private void init(JapaneseCustomWord word) {
      if (word != null) {
         String candidate = word.getCandidate();
         this.this$0._mainScreen.setText(candidate);
         this._candidate.setText(candidate);
         this._reading.setText(word.getReading());
         this.setPrompt(CustomDictUnitEditor$CustomDictEditorScreen._rb.getString(21));
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
   }

   @Override
   protected boolean accept() {
      return this.accept(true);
   }

   protected boolean accept(boolean confirmationDialog) {
      if (this._reading.getText().length() != 0 && this._candidate.getText().length() != 0) {
         if (confirmationDialog && !super.accept()) {
            return false;
         }

         JapaneseCustomWord newWord = (JapaneseCustomWord)(new Object(this._reading.getText(), this._candidate.getText(), 0));
         JapaneseCustomWord oldWord = (JapaneseCustomWord)(this.this$0._model != null ? this.this$0._model.getEntry() : null);
         if (CustomWordlistScreen.getCustomDictionary().isInRepository(newWord)) {
            Dialog.inform(OptionsResources.getString(1463));
            return false;
         }

         if (!this.this$0._context.getFlag(6) && this.this$0._context.getFlag(0) && oldWord != null) {
            CustomWordlistScreen.getCustomDictionary().remove(oldWord);
         }

         if (!(CustomWordlistScreen.getCustomDictionary().add(newWord) instanceof Object)) {
            Dialog.inform(CustomDictUnitEditor$CustomDictEditorScreen._rb.getString(20));
         }

         this.close();
         return true;
      } else if (this._reading.getText().length() == 0 && this._candidate.getText().length() == 0) {
         this.close();
         return true;
      } else {
         Dialog.inform(OptionsResources.getString(1462));
         return false;
      }
   }

   @Override
   public void setType(int type) {
      BasicEditField editField = this.getEditField();
      if (editField != null) {
         this.delete(editField);
      }

      this.add((Field)(new Object()));
      Font f = this.getFont().derive(this.getFont().getStyle() | 1);
      this._candidate = new CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField(this, false, 4505800798126336L, this);
      this._candidate.setFont(f);
      this._reading = new CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField(this, true, 4505800798126080L, this);
      this._reading.setFont(f);
      this._candidate.addTextChangeListener(new CustomDictUnitEditor$JapaneseCustomDictEditorScreen$WordFieldTextChangeListener(this, this._reading));
      this.setEditField(this._candidate);
      String candidateString = CustomDictUnitEditor$CustomDictEditorScreen._rb.getString(27);
      CustomDictUnitEditor$JapaneseCustomDictEditorScreen$Label candidateLabel = new CustomDictUnitEditor$JapaneseCustomDictEditorScreen$Label(
         this, candidateString, 0
      );
      candidateLabel.setFont(f);
      String readingString = CustomDictUnitEditor$CustomDictEditorScreen._rb.getString(26);
      CustomDictUnitEditor$JapaneseCustomDictEditorScreen$Label readingLabel = new CustomDictUnitEditor$JapaneseCustomDictEditorScreen$Label(
         this, readingString, 0
      );
      readingLabel.setFont(f);
      int stringWidth = Math.max(candidateLabel.getFont().getBounds(candidateString), readingLabel.getFont().getBounds(readingString)) + 2;
      candidateLabel.setWidth(stringWidth);
      HorizontalFieldManager hfm1 = (HorizontalFieldManager)(new Object());
      hfm1.add(candidateLabel);
      hfm1.add(this._candidate);
      this.add(hfm1);
      readingLabel.setWidth(stringWidth);
      HorizontalFieldManager hfm2 = (HorizontalFieldManager)(new Object());
      hfm2.add(readingLabel);
      hfm2.add(this._reading);
      this.add(hfm2);
   }
}
