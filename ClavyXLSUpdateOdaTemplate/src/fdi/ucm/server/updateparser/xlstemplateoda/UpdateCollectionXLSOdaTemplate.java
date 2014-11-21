/**
 * 
 */
package fdi.ucm.server.updateparser.xlstemplateoda;

import java.util.ArrayList;

import fdi.ucm.server.modelComplete.ImportExportDataEnum;
import fdi.ucm.server.modelComplete.ImportExportPair;
import fdi.ucm.server.modelComplete.UpdateCollection;
import fdi.ucm.server.modelComplete.collection.CompleteCollectionAndLog;

/**
 * @author Joaquin Gayoso-Cabada
 *
 */
public class UpdateCollectionXLSOdaTemplate extends UpdateCollection{

	private static ArrayList<ImportExportPair> Parametros;
	
	
	public UpdateCollectionXLSOdaTemplate() {
		super();
	}
	
	@Override
	public CompleteCollectionAndLog processCollecccion(
			ArrayList<String> dateEntrada) {
		
		CollectionXLSOdaTemplate C=null;
		 ArrayList<String> Log = new ArrayList<String>();
		if (dateEntrada.size()>0 && !dateEntrada.get(0).isEmpty())
		{ 
		String fileName = dateEntrada.get(0);
		 System.out.println(fileName);
		 C = new CollectionXLSOdaTemplate();
		 C.Leer_Archivo_Excel(fileName,Log);
		}
		else
		{
			if (dateEntrada.size()<=0)
					Log.add("Error: Numero de Elementos de entrada invalidos");
			if (dateEntrada.get(0).isEmpty()) 
				Log.add("Error: Path del file vacio");
		}
		 return new CompleteCollectionAndLog(C.getColeccion(),Log);
	}

	@Override
	public ArrayList<ImportExportPair> getConfiguracion() {
		if (Parametros==null)
		{
			ArrayList<ImportExportPair> ListaCampos=new ArrayList<ImportExportPair>();
			ListaCampos.add(new ImportExportPair(ImportExportDataEnum.File, "Upload XLS File :"));
			Parametros=ListaCampos;
			return ListaCampos;
		}
		else return Parametros;
	}

	@Override
	public String getName() {
		return "Update by XLS Template Oda";
	}

	@Override
	public boolean getCloneLocalFiles() {
		return false;
	}

}
