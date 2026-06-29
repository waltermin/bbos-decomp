package net.rim.blackberry.api.mail.event;

public class MailEvent {
   protected Object _source;

   public MailEvent(Object source) {
      this._source = source;
   }

   public Object getSource() {
      return this._source;
   }

   @Override
   public String toString() {
      return null;
   }

   public void dispatch(Object _1) {
      throw null;
   }
}
