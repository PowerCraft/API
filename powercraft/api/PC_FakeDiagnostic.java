package powercraft.api;

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
			line = compound.getLong("line");
		else
			line = NOPOS;
		if(compound.hasKey("message"))
			message = compound.getString("message");
		if(compound.hasKey("columnNumber"))
			columnNumber = compound.getLong("columnNumber");
		else
			columnNumber = NOPOS;
		if(compound.hasKey("endPos"))
			endPos = compound.getLong("endPos");
		else
			endPos = NOPOS;
		if(compound.hasKey("pos"))
			pos = compound.getLong("pos");
		else
			pos = NOPOS;
		if(compound.hasKey("startPos"))
			startPos = compound.getLong("startPos");
		else
			startPos = NOPOS;
		if(compound.hasKey("kind"))
			kind = Kind.values()[compound.getInteger("kind")];
	}
	
	@Override
	public String getCode() {
		return null;
	}

	@Override
	public long getColumnNumber() {
		return columnNumber;
	}

	@Override
	public long getEndPosition() {
		return endPos;
	}

	@Override
	public Kind getKind() {
		return kind;
	}

	@Override
	public long getLineNumber() {
		return line;
	}

	@Override
	public String getMessage(Locale locale) {
		return message;
	}

	@Override
	public long getPosition() {
		return pos;
	}

	@Override
	public Void getSource() {
		return null;
	}

	@Override
	public long getStartPosition() {
		return startPos;
	}

}
