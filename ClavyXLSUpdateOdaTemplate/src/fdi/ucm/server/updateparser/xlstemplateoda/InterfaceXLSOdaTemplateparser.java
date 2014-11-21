package fdi.ucm.server.updateparser.xlstemplateoda;

/**
 * Interface parseModel, funciones necesarias para parseal un objeto, parsear su modelo y sus instancias
 * @author Joaquin Gayoso-Cabada
 *
 */
public interface InterfaceXLSOdaTemplateparser {

	/**
	 * Funcion inicial del proceso del modelo.
	 */
	public void ProcessAttributes();
	
	/**
	 * Funcion inicial del proceso las instancias.
	 */
	public void ProcessInstances();
	
}
