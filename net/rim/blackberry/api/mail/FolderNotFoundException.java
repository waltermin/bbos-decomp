package net.rim.blackberry.api.mail;

public class FolderNotFoundException extends MessagingException {
   protected String _folderName;

   public FolderNotFoundException(String folderName) {
      this._folderName = folderName;
   }

   public FolderNotFoundException(String folderName, String s) {
      super(s);
      this._folderName = folderName;
   }

   public String getFolderName() {
      return this._folderName;
   }
}
