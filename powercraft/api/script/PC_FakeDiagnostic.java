package powercraft.api.script;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.tools.Diagnostic;

import powercraft.api.PC_NBTTagHandler;
import powercraft.api.PC_Field.Flag;
import net.minecraft.nbt.NBTTagCompound;

public final class PC_FakeDiagnostic implements Diagnostic<String> {
	
	public static final PC_DiagnosticTranslater DEFAULT_TRANSLATER = new PC_DiagnosticTranslater(){

		@Override
		public String translate(String message, String[] args, Locale locale) {
			return message;
		}
		
	};
	
	public static NBTTagCompound toCompound(Diagnostic<?> diagnostic){
		long line = diagnostic.getLineNumber();
		String message;
		String[] args;
		if(diagnostic instanceof PC_FakeDiagnostic){
			message = ((PC_FakeDiagnostic)diagnostic).message;
			args = ((PC_FakeDiagnostic)diagnostic).args;
		}else if(diagnostic instanceof Callable){
			try{
				Object obj = ((Callable<?>) diagnostic).call();
				if(obj instanceof String[]){
					String[] s = (String[])obj;
					message = s[0];
					args = Arrays.copyOfRange(s, 1, s.length);
				}else{
					message = diagnostic.getMessage(Locale.US);
					args = null;
				}
			}catch(Exception e){
				message = diagnostic.getMessage(Locale.US);
				args = null;
			}
		}else{
			message = diagnostic.getMessage(Locale.US);
			args = null;
		}
		long columnNumber = diagnostic.getColumnNumber();
		long endPos = diagnostic.getEndPosition();
		long pos = diagnostic.getPosition();
		Object source = diagnostic.getSource();
		long startPos = diagnostic.getStartPosition();
		Kind kind = diagnostic.getKind();
		NBTTagCompound compound = new NBTTagCompound();
		if(line!=NOPOS)
			compound.setLong("line", diagnostic.getLineNumber());
		if(message!=null)
			compound.setString("message", message);
		PC_NBTTagHandler.saveToNBT(compound, "args", args, Flag.SYNC);
		if(columnNumber!=NOPOS)
			compound.setLong("columnNumber", columnNumber);
		if(endPos!=NOPOS)
			compound.setLong("endPos", endPos);
		if(pos!=NOPOS)
			compound.setLong("pos", pos);
		if(source instanceof String)
			compound.setString("source", (String)source);
		if(startPos!=NOPOS)
			compound.setLong("startPos", startPos);
		if(kind!=null)
			compound.setLong("kind", kind.ordinal());
		return compound;
	}
	
	public static PC_FakeDiagnostic fromCompound(NBTTagCompound compound, PC_DiagnosticTranslater translater) {
		return new PC_FakeDiagnostic(compound, translater);
	}
	
	public static PC_FakeDiagnostic fromCompound(NBTTagCompound compound) {
		return new PC_FakeDiagnostic(compound, DEFAULT_TRANSLATER);
	}
	
	private PC_DiagnosticTranslater translater;
	private long line;
	private String message;
	private String[] args;
	private long columnNumber;
	private long endPos;
	private long pos;
	private String source;
	private long startPos;
	private Kind kind;
	
	public PC_FakeDiagnostic(long line, String message, String[] args, long columnNumber, long endPos, long pos, String source, long startPos, Kind kind, PC_DiagnosticTranslater translater) {
		this.line = line;
		this.message = message;
		this.args = args;
		this.columnNumber = columnNumber;
		this.endPos = endPos;
		this.pos = pos;
		this.source = source;
		this.startPos = startPos;
		this.kind = kind;
		this.translater = translater;
	}

	private PC_FakeDiagnostic(NBTTagCompound compound, PC_DiagnosticTranslater translater){
		if(compound.hasKey("line"))
			this.line = compound.getLong("line");
		else
			this.line = NOPOS;
		if(compound.hasKey("message"))
			this.message = compound.getString("message");
		this.args = PC_NBTTagHandler.loadFromNBT(compound, "args", String[].class, Flag.SYNC);
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
		if(compound.hasKey("source"))
			this.source = compound.getString("source");
		if(compound.hasKey("startPos"))
			this.startPos = compound.getLong("startPos");
		else
			this.startPos = NOPOS;
		if(compound.hasKey("kind"))
			this.kind = Kind.values()[compound.getInteger("kind")];
		this.translater = translater;
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
		return this.translater.translate(this.message, this.args, locale);
	}

	@Override
	public long getPosition() {
		return this.pos;
	}

	@Override
	public String getSource() {
		return this.source;
	}

	@Override
	public long getStartPosition() {
		return this.startPos;
	}

}
