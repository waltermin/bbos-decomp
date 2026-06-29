package net.rim.device.apps.api.framework.verb;

public class WrapperVerb extends Verb {
   protected Verb _innerVerb;
   protected Object _parameter;

   public WrapperVerb(Verb innerVerb, Object parameter, int ordering) {
      super(ordering);
      this._innerVerb = innerVerb;
      this._parameter = parameter;
   }

   public Verb getInnerVerb() {
      return this._innerVerb;
   }

   @Override
   public Object invoke(Object parameter) {
      return this._innerVerb.invoke(this._parameter);
   }

   @Override
   public String toString() {
      return this._innerVerb.toString();
   }

   @Override
   public String toString(Object context) {
      return this._innerVerb.toString(context);
   }

   @Override
   public int getVerbGroupId() {
      return this._innerVerb.getVerbGroupId();
   }
}
