package net.rim.device.api.crypto.certificate.status;

class QueryStatusDialog$RequestStateRunnable implements Runnable {
   private int _myState;
   private final QueryStatusDialog this$0;

   public QueryStatusDialog$RequestStateRunnable(QueryStatusDialog _1, int state) {
      this.this$0 = _1;
      this._myState = state;
   }

   @Override
   public void run() {
      if (!this.this$0._isClosed) {
         switch (this._myState) {
            case -2:
               break;
            case -1:
            case 0:
            case 1:
            case 2:
            default:
               this.this$0._textMessage.setText(QueryStatusDialog._rb.getString(26));
               break;
            case 3:
               this.this$0._textMessage.setText(QueryStatusDialog._rb.getString(27));
               break;
            case 4:
               this.this$0._textMessage.setText(QueryStatusDialog._rb.getString(28));
               break;
            case 5:
               this.this$0._textMessage.setText(QueryStatusDialog._rb.getString(29));
               break;
            case 6:
            case 11:
               this.this$0.close(1);
               break;
            case 7:
            case 8:
            case 9:
            case 10:
               this.this$0.close(4);
         }

         this.this$0._state = this._myState;
         this.this$0.setIcon();
      }
   }
}
