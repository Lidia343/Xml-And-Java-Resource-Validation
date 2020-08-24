public interface IResources extends dcmdon.resources.IResources
{
	public final static short RES_ID_POOL				= 140; // базовый класс для матриц
	
	public final static short RES_ID_UNIT				= 140; // базовый класс для юнитов
	public final static short RES_ID_SLOT				= 142; // базовый класс для слотов
	public final static short RES_ID_PORT				= 5012; // базовый класс для портов
	//public final static short ID_MODULE				= 146; // базовый класс для модулей

	
	public final static short RES_ID_OUTUNIT			= 145; // базовый класс для всех узлов, из портов которых выдаются воздействия (ТУ, ОТУ и тд)
	public final static short RES_ID_INUNIT				= 140; // базовый класс для всех узлов, порты которых реагируют на внешние воздействия (ТС, ТИ, ИТЧ и тд)
}