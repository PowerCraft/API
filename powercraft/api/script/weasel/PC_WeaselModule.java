package powercraft.api.script.weasel;

public interface PC_WeaselModule {

	public PC_WeaselClassSave createClassSave(boolean createDefault);

	public PC_WeaselEngine createEngine(PC_WeaselClassSave classSave, int memSize, Object handler);

	public PC_WeaselEngine loadEngine(PC_WeaselClassSave classSave, byte[] data, Object handler);

}
