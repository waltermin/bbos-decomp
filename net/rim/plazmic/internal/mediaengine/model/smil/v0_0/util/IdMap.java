package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.util;

import java.util.Hashtable;

public class IdMap {
   private Hashtable _map = (Hashtable)(new Object());
   private int _nextId;
   private String _elementIdPrefix;
   public static final int DEFAULT_STARTING_VALUE;
   public static final String DEFAULT_ELEMENT_ID_PREFIX;

   public IdMap() {
      this(0, "_unknown_element");
   }

   public IdMap(int startingValue, String elementIdPrefix) {
      this._nextId = startingValue;
      this._elementIdPrefix = elementIdPrefix;
   }

   public String createNewId() {
      String id = ((StringBuffer)(new Object())).append(this._elementIdPrefix).append(this._nextId).toString();
      this._map.put(id, new Object(this._nextId++));
      return id;
   }

   public int getId(String id) {
      int result = -1;
      if (id != null && id.length() != 0) {
         Integer i = (Integer)this._map.get(id);
         if (i != null) {
            return i;
         }

         result = this._nextId;
         this._map.put(id, new Object(this._nextId++));
      }

      return result;
   }
}
