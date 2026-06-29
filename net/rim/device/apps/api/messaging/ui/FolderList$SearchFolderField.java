package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.ui.component.BasicEditField;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.im.SLControlObject;

final class FolderList$SearchFolderField extends BasicEditField {
   private boolean _preventCall;
   private String _searchPattern;
   private final FolderList this$0;

   public FolderList$SearchFolderField(FolderList _1, String title) {
      super(title, null, Integer.MAX_VALUE, 1188950302162698240L);
      this.this$0 = _1;
      this._preventCall = false;
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      return key == 32 && this.getTextLength() == 0 ? key : super.processKeyEvent(event, key, keycode, time);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   protected final boolean keyControl(char key, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   protected final void updateInputStyle() {
      super.updateInputStyle();
      SLControlObject controlObject = (SLControlObject)this.getInputContext().getInputMethodControlObject();
      controlObject.actionPerformed(38, this.this$0._inputMethodSearchDelegate);
   }

   @Override
   public final int inputMethodTextChanged(InputMethodEvent event) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   protected final void updateText() {
      this._preventCall = true;
      if (this._searchPattern == null) {
         super.setText("");
      } else {
         super.setText(this._searchPattern);
      }

      this._preventCall = false;
   }
}
