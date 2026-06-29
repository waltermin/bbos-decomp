package net.rim.ecmascript.runtime;

class ScopeChain implements DebugScope {
   ScopeChain next;
   ESObject object;
   ESObject thiz;

   ScopeChain(ScopeChain pNext, ESObject pObject, ESObject pThiz) {
      this.next = pNext;
      this.object = pObject;
      this.thiz = pThiz;
   }

   @Override
   public DebugScope getOuter() {
      return this.next;
   }

   @Override
   public ESObject getObject() {
      return this.object;
   }

   @Override
   public long getThis() {
      return this.thiz == null ? Value.NULL : Value.makeObjectValue(this.thiz);
   }
}
