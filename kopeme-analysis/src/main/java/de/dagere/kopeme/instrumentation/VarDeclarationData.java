package de.dagere.kopeme.instrumentation;

/**
 * Data class to represent a command java variable declaration, 
 * containing {@link #type} and {@link #name}. 
 * 
 * @author dhaeb
 *
 */
public class VarDeclarationData {

	private static final String SEPARATOR_BETWEEN_DECLARATION = " ";
	
	private String type;
	private String name;

	public VarDeclarationData(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public VarDeclarationData(String declaration) {
		String[] sep = declaration.split(SEPARATOR_BETWEEN_DECLARATION);
		if(sep.length == 2){
			type = sep[0];
			name = sep[1];
		} else {
			throw new IllegalArgumentException("declaration not parsable: " + declaration);
		}
	}

	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return type + SEPARATOR_BETWEEN_DECLARATION + name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VarDeclarationData other = (VarDeclarationData) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
}
