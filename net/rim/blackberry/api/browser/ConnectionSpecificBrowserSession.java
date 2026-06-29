package net.rim.blackberry.api.browser;

class ConnectionSpecificBrowserSession extends BrowserSession {
   ConnectionSpecificBrowserSession(String uid) {
      super._connectionUid = uid;
   }
}
