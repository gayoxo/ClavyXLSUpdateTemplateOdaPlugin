/**
 * 
 */
package fdi.ucm.server.updateparser.xlstemplate.struture;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;


/**
 * @author Joaquin Gayoso-Cabada
 *Cobertura para XLS Antiguas
 *
 */
public class HojaAntigua extends Hoja{

	private java.util.List<List<HSSFCell>> ListaHijos;
	private java.util.List<List<HSSFCell>> ListaAmbitos;

	public HojaAntigua(String name) {
		super(name);
		ListaHijos=new ArrayList<List<HSSFCell>>();
	}

	/**
	 * @return the listaHijos
	 */
	public java.util.List<List<HSSFCell>> getListaHijos() {
		return ListaHijos;
	}

	/**
	 * @param listaHijos the listaHijos to set
	 */
	public void setListaHijos(java.util.List<List<HSSFCell>> listaHijos) {
		ListaHijos = listaHijos;
	}
	
	public void setListaAmbitos(java.util.List<List<HSSFCell>> listaAmbitos) {
		ListaAmbitos = listaAmbitos;
	}
	
	public java.util.List<List<HSSFCell>> getListaAmbitos() {
		return ListaAmbitos;
	}
	
}
