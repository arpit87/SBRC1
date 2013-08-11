package in.co.hopin.HelperClasses;

public class ThisAppConfig extends ConfigBase{
	
	private static ThisAppConfig instance = null;

	public static final String APPUUID = "uuid";	
	
	//app settings
	public static final String NEWUSERPOPUP = "new_user_popup";
	public static final String APPOPENCOUNT = "app_open_count";
	public static final String CHATPOPUP = "chat_popup";
	public static final String WOMANFILTER = "woman_filter";
	public static final String FBFRIENDONLYFILTER = "fb_friendonly_filter";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_ON_SERVER_EXPIRATION_TIME ="onServerExpirationTimeMs";
    public static final String SWIPETUTORIALSHOWN = "swipe_tutorial_shown";
    public static final String DEVICEID = "device_id";
	
	
	
	private ThisAppConfig(){super(Constants.APP_CONF_FILE);}
	
	public static ThisAppConfig getInstance()
	{
		if(instance == null)
			instance = new ThisAppConfig();
		return instance;
		
	}
}
