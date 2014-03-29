package powercraft.api.gres.doc;

public interface PC_GresDocInfoCollector {

	public void onLineChange(PC_GresDocumentLine line);

	public void onLineChanged(PC_GresDocumentLine line);

	public boolean onLineRecalc(PC_GresDocumentLine l);
	
}
