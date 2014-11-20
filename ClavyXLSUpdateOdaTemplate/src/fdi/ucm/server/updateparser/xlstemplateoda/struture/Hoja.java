/**
 * 
 */
package fdi.ucm.server.updateparser.xlstemplateoda.struture;

/**
 * @author Joaquin Gayoso-Cabada
 *Cobertura para hojas Excel
 *
 */
public abstract class Hoja {

	
	
	private String Name;

	public Hoja(String name) {
		Name=name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}
	
	
}
