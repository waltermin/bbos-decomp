package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.CharacterData;

class ESCharacterData extends ESNode {
   ESCharacterData(CharacterData characterData) {
      this(characterData, Names.CharacterData);
   }

   ESCharacterData(CharacterData characterData, String name) {
      this(characterData, name, JavaScriptEngine.getInstance()._characterDataPrototype);
   }

   ESCharacterData(CharacterData characterData, String name, ESObject proto) {
      super(characterData, name, proto);
   }

   CharacterData getCharacterData() {
      return (CharacterData)this.getNode();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long requestFieldValue(String name) throws ThrownValue {
      if (name == Names.data) {
         try {
            return JavaScriptEngine.makeStringValue(this.getCharacterData().getData());
         } catch (Throwable var4) {
            throw ESDOMException.createThrownValue(e);
         }
      } else {
         return name == Names.length ? Value.makeIntegerValue(this.getCharacterData().getLength()) : super.requestFieldValue(name);
      }
   }
}
