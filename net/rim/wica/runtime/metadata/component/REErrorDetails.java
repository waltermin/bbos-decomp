package net.rim.wica.runtime.metadata.component;

public class REErrorDetails {
   private int _category;
   private String _name;
   private String _requestName;
   private Object _data;

   public REErrorDetails(String requestName, int category, String name, Object data) {
      this._requestName = requestName;
      this._category = category;
      this._name = name;
      if (data == null) {
         this._data = "";
      } else {
         this._data = data;
      }
   }

   public int getCategory() {
      return this._category;
   }

   public Object getData() {
      return this._data;
   }

   public String getName() {
      return this._name;
   }

   public String getRequestMessageName() {
      return this._requestName;
   }

   public String getDescription() {
      return this._data.toString();
   }

   @Override
   public String toString() {
      StringBuffer message = (StringBuffer)(new Object("Category: "));
      message.append(this._category);
      message.append("\nName: ");
      message.append(this._name);
      message.append("\nRequest Name: ");
      message.append(this._requestName);
      message.append("\nData: ");
      message.append(this._data);
      return message.toString();
   }
}
