package fdi.ucm.server.updateparser.xlstemplate;

/**
 * Interface parseModel, funciones necesarias para parseal un objeto, parsear su modelo y sus instancias
 * @author Joaquin Gayoso-Cabada
 *
 */
public interface InterfaceXLSTemplateparser {

	/**
	 * Funcion inicial del proceso del modelo.
	 */
	public void ProcessAttributes();
	
	/**
	 * Funcion inicial del proceso las instancias.
	 */
	public void ProcessInstances();
	
}
