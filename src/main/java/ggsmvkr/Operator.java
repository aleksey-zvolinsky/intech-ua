package ggsmvkr;


public class Operator {

   private String login;
   private String name;
   private String passwd;


   public Operator(String _login) {
      this.login = _login;
   }

   public String getLogin() {
      return this.login;
   }

   public void setLogin(String _login) {
      this.login = _login;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String _name) {
      this.name = _name;
   }

   public String getPasswd() {
      return this.passwd;
   }

   public void setPasswd(String _passwd) {
      this.passwd = _passwd;
   }
}
