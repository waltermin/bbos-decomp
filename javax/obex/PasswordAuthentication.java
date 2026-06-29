package javax.obex;

public class PasswordAuthentication {
   private byte[] _userName;
   private byte[] _password;

   public PasswordAuthentication(byte[] userName, byte[] password) {
      if (password == null) {
         throw new NullPointerException();
      }

      this._userName = userName;
      this._password = password;
   }

   public byte[] getUserName() {
      return this._userName;
   }

   public byte[] getPassword() {
      return this._password;
   }
}
