package net.rim.wica.runtime.provisioning.internal;

import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.runtime.metadata.internal.def.MsgDefAccess;
import net.rim.wica.runtime.provisioning.internal.elements.MessageElement;

public class MessageCodeMapVisitor extends DefinitionVisitorAdapter {
   ToIntHashtable _nameToOldCode = (ToIntHashtable)(new Object());
   IntIntHashtable _messageCodeMap;

   public MessageCodeMapVisitor(MsgDefAccess msgDefs) {
      IntEnumeration e = msgDefs.getMsgDefIds();

      while (e.hasMoreElements()) {
         int defId = e.nextElement();
         this._nameToOldCode.put(msgDefs.getMsgName(defId), msgDefs.getMsgCode(defId));
      }

      this._messageCodeMap = (IntIntHashtable)(new Object(this._nameToOldCode.size()));
   }

   @Override
   public boolean visitMessageElement(MessageElement m) {
      if (this._nameToOldCode.containsKey(m.getName())) {
         this._messageCodeMap.put(this._nameToOldCode.get(m.getName()), m.getMessageCode());
      }

      return true;
   }

   IntIntHashtable getMessageCodeMap() {
      return this._messageCodeMap;
   }
}
