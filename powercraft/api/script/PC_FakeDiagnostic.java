package powercraft.api.script;

import java.util.Locale;

import javax.tools.Diagnostic;

import net.minecraft.nbt.NBTTagCompound;

public class PC_FakeDiagnostic implements Diagnostic<Void> {

	public static NBTTagCompound toCompound(Diagnostic<? extends Void> diagnostic){
		long line = diagnostic.getLineNumber();
		String message = diagnostic.getMessage(Locale.US);
		long columnNumber = diagnostic.getColumnNumber();
		long endPos = diagnostic.getEndPosition();
		long pos = diagnostic.getPosition();
		long startPos = diagnostic.getStartPosition();
		Kind kind = diagnostic.getKind();
		NBTTagCompound compound = new NBTTagCompound();
		if(line!=NOPOS)
			compound.setLong("line", diagnostic.getLineNumber());
		if(message!=null)
			compound.setString("message", message);
		if(columnNumber!=NOPOS)
			compound.setLong("columnNumber", columnNumber);
		if(endPos!=NOPOS)
			compound.setLong("endPos", endPos);
		if(pos!=NOPOS)
			compound.setLong("pos", pos);
		if(startPos!=NOPOS)
			compound.setLong("startPos", startPos);
		if(kind!=null)
			compound.setLong("kind", kind.ordinal());
		return compound;
	}
	
	public static PC_FakeDiagnostic fromCompound(NBTTagCompound compound) {
		return new PC_FakeDiagnostic(compound);
	}
	
	private long line;
	private String message;
	private long columnNumber;
	private long endPos;
	private long pos;
	private long startPos;
	private Kind kind;
	
	private PC_FakeDiagnostic(NBTTagCompound compound){
		if(compound.hasKey("line"))
			this.line = compound.getLong("line");
		else
			this.line = NOPOS;
		if(compound.hasKey("message"))
			this.message = compound.getString("message");
		if(compound.hasKey("columnNumber"))
			this.columnNumber = compound.getLong("columnNumber");
		else
			this.columnNumber = NOPOS;
		if(compound.hasKey("endPos"))
			this.endPos = compound.getLong("endPos");
		else
			this.endPos = NOPOS;
		if(compound.hasKey("pos"))
			this.pos = compound.getLong("pos");
		else
			this.pos = NOPOS;
		if(compound.hasKey("startPos"))
			this.startPos = compound.getLong("startPos");
		else
			this.startPos = NOPOS;
		if(compound.hasKey("kind"))
			this.kind = Kind.values()[compound.getInteger("kind")];
	}
	
	@Override
	public String getCode() {
		return null;
	}

	@Override
	public long getColumnNumber() {
		return this.columnNumber;
	}

	@Override
	public long getEndPosition() {
		return this.endPos;
	}

	@Override
	public Kind getKind() {
		return this.kind;
	}

	@Override
	public long getLineNumber() {
		return this.line;
	}

	@Override
	public String getMessage(Locale locale) {
		return this.message;
	}

	@Override
	public long getPosition() {
		return this.pos;
	}

	@Override
	public Void getSource() {
		return null;
	}

	@Override
	public long getStartPosition() {
		return this.startPos;
	}

}
