package net.rim.device.apps.internal.browser.html;

final class HTMLForm$FormAction implements Runnable {
   private boolean _submit;
   private SubmitButton _submitButton;
   private final HTMLForm this$0;

   HTMLForm$FormAction(HTMLForm _1, boolean submit, SubmitButton submitButton) {
      this.this$0 = _1;
      this._submit = submit;
      this._submitButton = submitButton;
   }

   @Override
   public final void run() {
      if (this._submit) {
         label30:
         try {
            HTMLBrowserContent parent = ((HTMLDocumentImpl)this.this$0.getOwnerDocument()).getUiPeer();
            if (!parent.executeJavaScriptAction(this.this$0, this.this$0.getAttributeValue(164), null)) {
               return;
            }
         } finally {
            break label30;
         }

         if (this._submitButton != null) {
            this._submitButton.setSelected();
         }

         this.this$0.submit();
      } else {
         this.this$0.reset();
      }
   }
}
