package net.rim.ecmascript.runtime;

class TryBlock {
   TryBlock next;
   ScopeChain scope;
   int catchIp;

   TryBlock(TryBlock pNext, ScopeChain pScope, int pCatchIp) {
      this.next = pNext;
      this.scope = pScope;
      this.catchIp = pCatchIp;
   }
}
