package in.co.hopin.Server;

public class ServerConstants {
	
	//public static  String  SERVER_ADDRESS = "http://192.168.1.4/hopon/";
	//public static  String  SERVER_ADDRESS = "http://www.greenyatra.org/sb/";
	public static  String  SERVER_ADDRESS = "http://hopin.co.in/api";
	public static String USERSERVICE="/UserService";
	public static String USERDETAILSSERVICE="/UserDetailsService";
	public static String REQUESTSERVICE="/RequestService";
	public static String CHATSERVICE= "/ChatService";
	public static String CHATSERVERIP= "54.243.171.212";
	public static String CHATADMINACKFROM= "admin@hopin.co.in";
	
	public static String AppendServerIPToFBID(String fbid)
	{
		return fbid+"@"+CHATSERVERIP;
	}


}
