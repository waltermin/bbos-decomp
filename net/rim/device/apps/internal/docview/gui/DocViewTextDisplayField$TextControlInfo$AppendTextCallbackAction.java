package net.rim.device.apps.internal.docview.gui;

final class DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction implements DocViewTextDisplayField$CallbackAction {
   private Object[] _cookies;
   private String _text;
   private final DocViewTextDisplayField$TextControlInfo this$1;

   DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction(DocViewTextDisplayField$TextControlInfo _1, Object[] cookies, String text) {
      this.this$1 = _1;
      this._cookies = cookies;
      this._text = text;
   }

   @Override
   public final void executeCallback(Runnable[] updates) {
      if (this.this$1._richTextField == null) {
         this.this$1._richTextField = new DocViewTextDisplayField$TextControlInfo$TextScreenField(this.this$1);
      }

      if (this.this$1._richTextField.getTextLength() > 0) {
         this.this$1.this$0.addUpdateAction(updates, new DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction$1(this));
      } else {
         this.this$1.this$0.addUpdateAction(updates, new DocViewTextDisplayField$TextControlInfo$AppendTextCallbackAction$2(this));
      }

      if (this.this$1._richTextField.getIndex() == -1) {
         this.this$1
            .this$0
            .addUpdateAction(
               updates,
               new DocViewTextDisplayField$DocViewInsertFieldRunnable(
                  this.this$1.getDocViewActiveManager(),
                  this.this$1._richTextField,
                  this.this$1._initialInsertIndex,
                  this.this$1.this$0.isAllowedControl(this.this$1._richTextField)
               )
            );
      }
   }
}
