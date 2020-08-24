public interface IResources
{
	public final static short RES_ID_ALL						= 0;	// маска для всех классов ресурсов

	public static final short RES_ID_ALIVECONTROLLER	= 10;	// класс со свойствами для контроля живучести

	/*public static final short RES_ID_TARGET			 	= 11;	//
	public final static short RES_ID_TIME		= 100;	// время*/
	//
	public final static short RES_ID_WORKEXPRESSION		= 500;	// активное конструируемое выражение

	public static final short RES_ID_OCCURRENCES_CONFIGURATION = 144; //Конфигурация ситуации
	
	/** уникальный идентификатор ресурса <b>ControlArea</b>*/ 
	public static final short RES_ID_CONTROLAREA		= 1424; 

	/** уникальный идентификатор ресурса <b>Picture</b>*/ 
	public static final short RES_ID_PICTURE			= 1429; 
	/** уникальный идентификатор ресурса <b>MonitoringArea</b>*/ 
	short RES_ID_MONITORINGAREA	= 1430;
	
	/** уникальный идентификатор ресурса <b>OccurenceArea</b>*/ 
	public static final short RES_ID_OCCURENCEAREA		= 1460;
	
	/*public static final short RESID_EVENTS				= 1450; 

//	public static final short RESIDWORKBENCH_STATE	= 5000;	// изменение состояния АРМ*/
	
	public final static short RES_ID_COMMAND			= 2301; // команда
	
	//Ресурсы NET
	public final static short RES_ID_NET				= 5001;	// 
	
	  /*    public final static short RES_ID_ZONE				= 5002;	// 
	
	public final static short RES_ID_CLUSTER			= 5002;	//  */
	
	public final static short RES_ID_NODE				= 5004;	// 
	public final static short RES_ID_BEARER				= 5005;	// 
	public final static short RES_ID_MEDIA				= 5006;	// 
	
	//Ресурсы PCOMPLEX
	//public final static short RES_ID_PCOMPLEX			= 5011;	// 
	public final static short RES_ID_APP				= 5012;	// 
	public final static short RES_ID_COMPONENT			= 5013;	// 
//	public static final short RES_ID_COMMUNICATOR   	= 5014;//
	public final static short RES_ID_OUTCONN			= 5015;	//
	public final static short RES_ID_INCONN				= 5016;	// 
	public final static short RES_ID_PLUGIN				= 5017;	// 
	public final static short RES_ID_TASK				= 5018;	// 
	public final static short RES_ID_TASK_PORT			= 5019;	// 
	public static final short RES_ID_WORKBENCH			= 5020;	//
	public final static short RES_ID_USER				= 5021;	//
	     //public static final short RES_ID_PICTUREMANAGER 	= 5011;	//
	//  public static final short RES_ID_COMMLINK			= 5012;	//
	public static final short RES_ID_APPINTERACTION	 	= 5024;	//
}