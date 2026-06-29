package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;

final class ESCharacterDataPrototype extends ESObject {
   ESCharacterDataPrototype(ESNodePrototype proto) {
      this.setPrototype(proto);
      this.addHostFunction(new ESCharacterDataPrototype$1(this, Names.CharacterData, "substringData", 2));
      this.addHostFunction(new ESCharacterDataPrototype$2(this, Names.CharacterData, "appendData", 1));
      this.addHostFunction(new ESCharacterDataPrototype$3(this, Names.CharacterData, "insertData", 2));
      this.addHostFunction(new ESCharacterDataPrototype$4(this, Names.CharacterData, "deleteData", 2));
      this.addHostFunction(new ESCharacterDataPrototype$5(this, Names.CharacterData, "replaceData", 3));
   }
}
