package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.ESObject;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.script.internal.EcmaUtilities;
import net.rim.wica.runtime.script.internal.ScriptNames;

final class ESCollection extends ESObject {
   private String _id;
   private DataCollection _collection;

   ESCollection(String id, DataCollection collection, ESObject prototype) {
      super("MDSCollection", prototype);
      this._id = id;
      this._collection = collection;
      if (collection.isSupported("sendEmail")) {
         int length = ScriptNames.SendProperties.length;
         this.setGrowthIncrement(length);

         for (int i = 0; i < length; i++) {
            this.addField(ScriptNames.SendProperties[i], 5, EcmaUtilities.makeStringValue(ScriptNames.SendProperties[i]));
         }
      }
   }

   final String getId() {
      return this._id;
   }

   final DataCollection getCollection() {
      return this._collection;
   }
}
