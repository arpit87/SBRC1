package in.co.hopin.HelperClasses;


public class ThisUserConfig extends ConfigBase{
	

	private static ThisUserConfig instance = null;
	
	
	public static final String FBLOGGEDIN = "fbloggedin";
	public static final String FBRELOGINREQUIRED = "fbreloggedin";
	public static final String FBINFOSENTTOSERVER = "fbinfosent";
	public static final String FBACCESSTOKEN = "fb_access_token";
	public static final String FBACCESSEXPIRES = "fb_excess_expires";
	public static final String USERID = "user_id";
    public static final String USERNAME = "username";
	public static final String FBPICURL = "fb_pic_url";
	public static final String FBUSERNAME = "fb_username";
	public static final String FB_FIRSTNAME = "fb_firstname";
	public static final String FB_LASTNAME = "fb_lastname";
	public static final String FB_FULLNAME = "fb_fullname";
	public static final String FBUID = "fb_user_uid";	
	public static final String PASSWORD = "password";
	public static final String CHATPASSWORD = "password";
	public static final String CHATUSERID = "chat_userid";
	public static final String IsOfferMode = "offer_mode";
	public static final String MOBILE = "mobile";
    public static final String ACTIVE_REQ_INSTA = "active_req_insta";
    public static final String ACTIVE_REQ_CARPOOL = "active_req_carpool";
    public static final String GENDER = "fb_gender";
    public static final String EMAIL = "email";    
    public static final String WELCOMENOTESENT = "welcomemsgsent"; 
	
	private ThisUserConfig(){super(Constants.USER_CONF_FILE);}
	
	public static ThisUserConfig getInstance()
	{
		if(instance == null)
			instance = new ThisUserConfig();
		return instance;
		
	}
}
