/**
 * 
 */
package fdi.ucm.server.updateparser.xlstemplateoda;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteStructure;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;
import fdi.ucm.server.updateparser.xlstemplateoda.struture.Hoja;
import fdi.ucm.server.updateparser.xlstemplateoda.struture.HojaAntigua;
import fdi.ucm.server.updateparser.xlstemplateoda.struture.HojaNueva;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase que implementa la creacion de la base de datos per se
 * @author Joaquin Gayoso-Cabada
 *
 */
public class CollectionXLSOdaTemplate implements InterfaceXLSOdaTemplateparser {


	private static final String XLS_COLLECTION = "XLS COllection";
	private static final String COLECCION_A_APARTIR_DE_UN_XLS = "Coleccion a partir de un XLS";
	private CompleteCollection coleccionstatica;
	private ArrayList<String> Log;

	
	public CollectionXLSOdaTemplate() {
		coleccionstatica=new CompleteCollection(XLS_COLLECTION, COLECCION_A_APARTIR_DE_UN_XLS+ new Timestamp(new Date().getTime()));
	}
	
	/* (non-Javadoc)
	 * @see fdi.ucm.server.importparser.sql.SQLparserModel#ProcessAttributes()
	 */
	@Override
	public void ProcessAttributes() {
		
	}

	
	 /**
	 
	  * Este metodo es usado para leer archivos Excel
	 
	  *
	 
	  * @param Nombre_Archivo
	 
	  *            - Nombre de archivo Excel.
	 * @param log 
	 
	  */
	 public void Leer_Archivo_Excel(String Nombre_Archivo, ArrayList<String> log) {
	 
	  /**
	 
	   * Crea una nueva instancia de Lista_Datos_Celda
	 
	   */
	 
		 Log=log;
		 
	  ArrayList<Hoja> Hojas=new ArrayList<Hoja>();

	 
	  if (Nombre_Archivo.contains(".xlsx")) {
	 
		  Hojas=GENERAR_XLSX(Nombre_Archivo);
	 
	  } else if (Nombre_Archivo.contains(".xls")) {
	 
		  Hojas=GENERAR_XLS(Nombre_Archivo);

	 
	  }
	 
	  /**
	 
	   * Llama el metodo Imprimir_Consola para imprimir los datos de la celda
	 
	   * en la consola.
	 
	   */
	 
	  Imprimir_Consola(Hojas);
	 
	 }
	 
	 private ArrayList<Hoja> GENERAR_XLSX(String Nombre_Archivo) {
	 
		 ArrayList<Hoja> Salida=new ArrayList<Hoja>(); 
		 
		 
	  try {
	 
	   /**
	 
	    * Crea una nueva instancia de la clase FileInputStream
	 
	    */
	 
	   FileInputStream fileInputStream = new FileInputStream(
	 
	     Nombre_Archivo);
	 
	   /**
	 
	    * Crea una nueva instancia de la clase XSSFWorkBook
	 
	    */
	 
	   XSSFWorkbook Libro_trabajo = new XSSFWorkbook(fileInputStream);
	 
	   
	   int NStilos=Libro_trabajo.getNumberOfSheets();
		 
	   valida(Libro_trabajo,NStilos);
	   

	   for (int i = 0; i < NStilos; i++) {

		   XSSFSheet Hoja_hssf = Libro_trabajo.getSheetAt(i);
		   

			   HojaNueva Hojax=new HojaNueva(Hoja_hssf.getSheetName());
			   
			   
			   
			   Iterator<Row> Iterador_de_Fila = Hoja_hssf.rowIterator();
				 
			   List<List<XSSFCell>> Lista_Datos_Celda2 = new ArrayList<List<XSSFCell>>();
			   
			   while (Iterador_de_Fila.hasNext()) {
			 
				   XSSFRow Fila_hssf = (XSSFRow) Iterador_de_Fila.next();
			 
			    Iterator<Cell> iterador = Fila_hssf.cellIterator();
			 
			    List<XSSFCell> Lista_celda_temporal = new ArrayList<XSSFCell>();
			 
			    while (iterador.hasNext()) {
			 
			    	XSSFCell Celda_hssf = (XSSFCell) iterador.next();
			 
			     Lista_celda_temporal.add(Celda_hssf);
			 
			    }
			 
			    Lista_Datos_Celda2.add(Lista_celda_temporal);
			 
			   }
			   
			   Hojax.setListaHijos(Lista_Datos_Celda2);
			   Salida.add(Hojax);

		 
		   
		  
	}
	   
	   

	  } catch (Exception e) {
	 
	   e.printStackTrace();
	 
	  }
	 
	  return Salida;
	 }
	 
	
	 

	private void valida(XSSFWorkbook libro_trabajo, int nStilos) {
		  
		 boolean Datos=false;
		 boolean Metadatos=false;
		 boolean Recursos=false;
		 boolean Archivos=false;
		 boolean URL=false;
		 
		   for (int i = 0; i < nStilos; i++) {

			   XSSFSheet Hoja_hssf = libro_trabajo.getSheetAt(i);
			   
			   String Name=Hoja_hssf.getSheetName(); 
			   
			   if (Name.equals("Datos")) 
				   Datos=true;
			   
			   if (Name.equals("Metadatos")) 
				   Metadatos=true;
			   
			   if (Name.equals("Recursos")) 
				   Recursos=true;
			   
			   if (Name.equals("Archivos")) 
				   Archivos=true;
			   
			   if (Name.equals("URL")) 
				   URL=true;
			   
		   }
		   
		   if (!Datos) 
			   Log.add("Pestaña datos no encontrada");
		   
		   if (!Metadatos) 
			   Log.add("Pestaña Metadatos no encontrada");
		   
		   if (!Recursos) 
			   Log.add("Pestaña Recursos no encontrada");
		   
		   if (!Archivos) 
			   Log.add("Pestaña Archivos no encontrada");
		   
		   if (!URL) 
			   Log.add("Pestaña URL no encontrada");
		   
		
	}

	private ArrayList<Hoja> GENERAR_XLS(String Nombre_Archivo) {
	 
		 ArrayList<Hoja> Salida=new ArrayList<Hoja>();
		 
	  try {
	 
	   /**
	 
	    * Crea una nueva instancia de la clase FileInputStream
	 
	    */
	 
	   FileInputStream fileInputStream = new FileInputStream(
	 
	     Nombre_Archivo);
	 
	   /**
	 
	    * Crea una nueva instancia de la clase POIFSFileSystem
	 
	    */
	 
	   POIFSFileSystem fsFileSystem = new POIFSFileSystem(fileInputStream);
	 
	   /**
	 
	    * Crea una nueva instancia de la clase HSSFWorkBook
	 
	    */
	 
	   HSSFWorkbook Libro_trabajo = new HSSFWorkbook(fsFileSystem);
	   
	   
	   int NStilos=Libro_trabajo.getNumberOfSheets();
	 
	   valida(Libro_trabajo,NStilos);
	   
	   for (int i = 0; i < NStilos; i++) {
		   HSSFSheet Hoja_hssf = Libro_trabajo.getSheetAt(i);
		   
		   
		   HojaAntigua Hojax=new HojaAntigua(Hoja_hssf.getSheetName());
		   
		   
		   Iterator<Row> Iterador_de_Fila = Hoja_hssf.rowIterator();
			 
		   List<List<HSSFCell>> Lista_Datos_Celda2 = new ArrayList<List<HSSFCell>>();
		   
		   while (Iterador_de_Fila.hasNext()) {
		 
		    HSSFRow Fila_hssf = (HSSFRow) Iterador_de_Fila.next();
		 
		    Iterator<Cell> iterador = Fila_hssf.cellIterator();
		 
		    List<HSSFCell> Lista_celda_temporal = new ArrayList<HSSFCell>();
		 
		    while (iterador.hasNext()) {
		 
		     HSSFCell Celda_hssf = (HSSFCell) iterador.next();
		 
		     int yo=Celda_hssf.getColumnIndex();
		     while (Lista_celda_temporal.size()<yo)
		     	{
		    	 Lista_celda_temporal.add(null);
		     	}
		     Lista_celda_temporal.add(Celda_hssf);
		 
		    }
		 
		    Lista_Datos_Celda2.add(Lista_celda_temporal);
		 
		   }
		   
		   Hojax.setListaHijos(Lista_Datos_Celda2);
		   Salida.add(Hojax);

		   
		   
	}
	   
	   
	  } catch (Exception e) {
	 
	   e.printStackTrace();
	 
	  }
	  
	  return Salida;
	 
	 }
	 
	 private void valida(HSSFWorkbook libro_trabajo, int nStilos) {
		
		 boolean Datos=false;
		 boolean Metadatos=false;
		 boolean Recursos=false;
		 boolean Archivos=false;
		 boolean URL=false;
		 
		   for (int i = 0; i < nStilos; i++) {

			   HSSFSheet Hoja_hssf = libro_trabajo.getSheetAt(i);
			   
			   String Name=Hoja_hssf.getSheetName(); 
			   
			   if (Name.equals("Datos")) 
				   Datos=true;
			   
			   if (Name.equals("Metadatos")) 
				   Metadatos=true;
			   
			   if (Name.equals("Recursos")) 
				   Recursos=true;
			   
			   if (Name.equals("Archivos")) 
				   Archivos=true;
			   
			   if (Name.equals("URL")) 
				   URL=true;
			   
		   }
		   
		   if (!Datos) 
			   Log.add("Pestaña datos no encontrada");
		   
		   if (!Metadatos) 
			   Log.add("Pestaña Metadatos no encontrada");
		   
		   if (!Recursos) 
			   Log.add("Pestaña Recursos no encontrada");
		   
		   if (!Archivos) 
			   Log.add("Pestaña Archivos no encontrada");
		   
		   if (!URL) 
			   Log.add("Pestaña URL no encontrada");
		
	}

	/**
	 
	  * Este método se utiliza para imprimir los datos de la celda a la consola.
	 
	  *
	 
	  * @param Datos_celdas
	 
	  *            - Listado de los datos que hay en la hoja de cálculo.
	 
	  */
	 
	 private void Imprimir_Consola(List<Hoja> HojasEntrada) {
	 
		 Long counterbase=Long.MIN_VALUE;
	
		 Hoja Hoja_hssfDatos= findDatos(HojasEntrada);
		 Hoja Hoja_hssfMetaDatos= findMetadatos(HojasEntrada);
		 Hoja Hoja_hssfRecursos= findRecursos(HojasEntrada);
		 Hoja Hoja_hssfFiles= findArchivos(HojasEntrada);
		 Hoja Hoja_hssfURL= findURLs(HojasEntrada);
		   
		if (Hoja_hssfDatos!=null)
		   procesVO(Hoja_hssfDatos,Hoja_hssfMetaDatos,Hoja_hssfRecursos,counterbase);
		 
		if (Hoja_hssfFiles!=null)
			   procesFiles(Hoja_hssfFiles,counterbase);
		
		if (Hoja_hssfURL!=null)
			   procesUrls(Hoja_hssfURL,counterbase);
		 
 
		 
		 
	 
	 
	 }


	private void procesUrls(Hoja hoja_hssfURL, Long counterbase) {
		CompleteGrammar Grammar=new CompleteGrammar("Urls", "Urls", coleccionstatica);
		coleccionstatica.getMetamodelGrammar().add(Grammar);
		HashMap<Integer, CompleteTextElementType> Hash=new HashMap<Integer, CompleteTextElementType>();
		HashMap<String, CompleteTextElementType> HashPath=new HashMap<String, CompleteTextElementType>();
		HashMap<Long,CompleteDocuments> Documents=new HashMap<Long,CompleteDocuments>();
		proceshoja(Grammar,hoja_hssfURL,Hash,HashPath,Documents,counterbase, true);
	}

	private void procesFiles(Hoja hoja_hssfFiles, Long counterbase) {
		CompleteGrammar Grammar=new CompleteGrammar("Files", "Files", coleccionstatica);
		coleccionstatica.getMetamodelGrammar().add(Grammar);
		HashMap<Integer, CompleteTextElementType> Hash=new HashMap<Integer, CompleteTextElementType>();
		HashMap<String, CompleteTextElementType> HashPath=new HashMap<String, CompleteTextElementType>();
		HashMap<Long,CompleteDocuments> Documents=new HashMap<Long,CompleteDocuments>();
		proceshoja(Grammar,hoja_hssfFiles,Hash,HashPath,Documents,counterbase, true);
	}

	private void procesVO(Hoja hoja_hssfDatos, Hoja hoja_hssfMetaDatos,
			Hoja hoja_hssfRecursos, Long counterbase) {
		CompleteGrammar Grammar=new CompleteGrammar("Virtual object", "Virtual object", coleccionstatica);
		coleccionstatica.getMetamodelGrammar().add(Grammar);
		HashMap<Integer, CompleteTextElementType> Hash=new HashMap<Integer, CompleteTextElementType>();
		HashMap<String, CompleteTextElementType> HashPath=new HashMap<String, CompleteTextElementType>();
		HashMap<Long,CompleteDocuments> Documents=new HashMap<Long,CompleteDocuments>();
		proceshoja(Grammar,hoja_hssfDatos,Hash,HashPath,Documents,counterbase, true);
		proceshoja(Grammar,hoja_hssfMetaDatos,Hash,HashPath,Documents,counterbase, false);
		proceshoja(Grammar,hoja_hssfRecursos,Hash,HashPath,Documents,counterbase, false);
		
	}

	private void proceshoja(CompleteGrammar grammar, Hoja hoja_hssfDatos,
			HashMap<Integer, CompleteTextElementType> hash,
			HashMap<String, CompleteTextElementType> hashPath, HashMap<Long, CompleteDocuments> documents, Long counterbase,Boolean Datos) {
		if (hoja_hssfDatos instanceof HojaAntigua)
		{
			List<List<HSSFCell>> Datos_celdas = ((HojaAntigua) hoja_hssfDatos).getListaHijos();
			
			 String Valor_de_celda;
			 
			  for (int FilaX = 0; FilaX < Datos_celdas.size(); FilaX++) {
			 
				  CompleteDocuments Doc=new CompleteDocuments(coleccionstatica, grammar, "", "");  
						
				  
			   List<HSSFCell> Lista_celda_temporal = Datos_celdas.get(FilaX);
			   
			 
			   ArrayList<CompleteTextElement> Ambitar=new ArrayList<CompleteTextElement>();
			   
			   for (int ColumnaX = 0; ColumnaX < Lista_celda_temporal.size(); ColumnaX++) {
			 
			 
			     HSSFCell hssfCell = Lista_celda_temporal.get(ColumnaX);
			     
			     
			     
			     
			     
			     
				 if (hssfCell!=null)  
					 Valor_de_celda = hssfCell.toString();
				 else 
					 Valor_de_celda="";
			     
			     if (ColumnaX==0)
			     {
			    	 try {
			    		 double Valorposible = Double.parseDouble(Valor_de_celda);
			    		 long valuecelda=(long)Valorposible;
			    		 Long valueCeldaL = Long.valueOf(valuecelda);
			    		 CompleteDocuments Doc2 = documents.get(valueCeldaL );
			    		 if (Doc2!=null)
			    			 Doc=Doc2;
			    		 else
			    		 {
			    		 Doc.setClavilenoid(valuecelda);
			    		 coleccionstatica.getEstructuras().add(Doc);
			    		 documents.put(valueCeldaL, Doc);
			    		 }
						} catch (Exception e) {
							Doc.setClavilenoid(counterbase);
							coleccionstatica.getEstructuras().add(Doc);
							 documents.put(counterbase, Doc);
							counterbase++;
						}
			    	
			     }
			     
			     
			     else if (ColumnaX==1&&Datos)
			     {
			    	 if (FilaX==1)
				    	 try {
				    		 grammar.setClavilenoid(Long.parseLong(Valor_de_celda));
							} catch (Exception e) {
								grammar.setClavilenoid(-1l);
							}
			    	 Doc.setDescriptionText(Valor_de_celda);

			    	
			     }
			     
			     else
			     {
			    if (FilaX==0)
			    	 {
			    	CompleteTextElementType C=generaStructura(Valor_de_celda,grammar,hashPath);
			    	hash.put(new Integer(ColumnaX), C);
//			    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
			    	
			    	 }
			    else if (FilaX==1)
		    	 {
			    	CompleteTextElementType C=hash.get(new Integer(ColumnaX));
			    	try {
			    		C.setClavilenoid(Long.parseLong(Valor_de_celda));
					} catch (Exception e) {
						C.setClavilenoid(-1l);
					}
			    	
		    	hash.put(new Integer(ColumnaX), C);
//		    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
		    	
		    	 }
			    else 
			    	{
			    	
			    	CompleteTextElementType C=hash.get(new Integer(ColumnaX));
			    	CompleteTextElement CT=new CompleteTextElement(C, Valor_de_celda);
			    	CT.setDocumentsFather(Doc);
			    	ArrayList<Integer> ALIst = new ArrayList<Integer>();
			    	
			    		
			    	
			    	CT.setAmbitos(ALIst);
			    	if (!CT.getValue().isEmpty())
			    		Doc.getDescription().add(CT);
			    	

			    	
			    	
			    	}
			      
			     }
			     
			     
			   }
			 
			   int maximoIntegerV=buscaelmaximoInteger(Doc);
			     for (CompleteTextElement completeTextElement : Ambitar) {
			    	 ArrayList<Integer> maximoInteger=calculamaximo(completeTextElement.getHastype().getClavilenoid(),Doc);
			    	
			    	if (maximoInteger.size()>0)
				    	if (maximoInteger.get(0)<0)
				    		maximoInteger.set(0, 0);
				    	else
				    		{
				    		maximoIntegerV++;
				    		maximoInteger.set(0, maximoIntegerV);
				    		}
					completeTextElement.setAmbitos(maximoInteger);
				}
//			   System.out.println();
			 
			  }
			
		}else if (hoja_hssfDatos instanceof HojaNueva)
		{
			List<List<XSSFCell>> Datos_celdas = ((HojaNueva) hoja_hssfDatos).getListaHijos();
			
			 String Valor_de_celda;
			 
			  for (int FilaX = 0; FilaX < Datos_celdas.size(); FilaX++) {
			 
				  List<XSSFCell> Lista_celda_temporal = Datos_celdas.get(FilaX);

				  
				  CompleteDocuments Doc=new CompleteDocuments(coleccionstatica, grammar, Integer.toString(FilaX), "");  

			 
					ArrayList<CompleteTextElement> Ambitar=new ArrayList<CompleteTextElement>();
			   for (int ColumnaX = 0; ColumnaX < Lista_celda_temporal.size(); ColumnaX++) {
			 
			  
			 
			     XSSFCell hssfCell = (XSSFCell) Lista_celda_temporal.get(ColumnaX);
			 

			     
				   if (hssfCell!=null)  
						 Valor_de_celda = hssfCell.toString();
					 else 
						 Valor_de_celda="";
				   
			     
			 
			     if (ColumnaX==0)
			     {
			    	 try {
			    		 double Valorposible = Double.parseDouble(Valor_de_celda);
			    		 long valuecelda=(long)Valorposible;
			    		 Long valueCeldaL = Long.valueOf(valuecelda);
			    		 CompleteDocuments Doc2 = documents.get(valueCeldaL );
			    		 if (Doc2!=null)
			    			 Doc=Doc2;
			    		 else
			    		 {
			    		 Doc.setClavilenoid(valuecelda);
			    		 coleccionstatica.getEstructuras().add(Doc);
			    		 documents.put(valueCeldaL, Doc);
			    		 }
						} catch (Exception e) {
							Doc.setClavilenoid(counterbase);
							coleccionstatica.getEstructuras().add(Doc);
							 documents.put(counterbase, Doc);
							counterbase++;
						}
			    	
			     }
			     else if (ColumnaX==1&&Datos)
			     {
			    	 if (FilaX==1)
				    	 try {
				    		 grammar.setClavilenoid(Long.parseLong(Valor_de_celda));
							} catch (Exception e) {
								grammar.setClavilenoid(-1l);
							}
			    	 Doc.setDescriptionText(Valor_de_celda);

			    	
			     }
			     else
			     
			     {
			    if (FilaX==0)
			    	 {
			    	CompleteTextElementType C=generaStructura(Valor_de_celda,grammar,hashPath);
			    	hash.put(new Integer(ColumnaX), C);
//			    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
			    	
			    	 }
			    else if (FilaX==1)
		    	 {
			    	CompleteTextElementType C=hash.get(new Integer(ColumnaX));
			    	try {
			    		C.setClavilenoid(Long.parseLong(Valor_de_celda));
					} catch (Exception e) {
						C.setClavilenoid(-1l);
					}
			    	
		    	hash.put(new Integer(ColumnaX), C);
//		    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
		    	
		    	 }
			    else 
			    	{
			    	
			    	CompleteTextElementType C=hash.get(new Integer(ColumnaX));
			    	CompleteTextElement CT=new CompleteTextElement(C, Valor_de_celda);
			    	CT.setDocumentsFather(Doc);
			    	ArrayList<Integer> ALIst=new ArrayList<Integer>();
			    	
			    	
			    	CT.setAmbitos(ALIst);
			    	
			    	if (!CT.getValue().isEmpty())
			    		Doc.getDescription().add(CT);
			    	
			    	
			    	}
			 
			   }
			 
//			   System.out.println();
			 
			  }
			   
			   int maximoIntegerV=buscaelmaximoInteger(Doc);
			     for (CompleteTextElement completeTextElement : Ambitar) {
			    	 ArrayList<Integer> maximoInteger=calculamaximo(completeTextElement.getHastype().getClavilenoid(),Doc);
			    	
			    	if (maximoInteger.size()>0)
				    	if (maximoInteger.get(0)<0)
				    		maximoInteger.set(0, 0);
				    	else
				    		{
				    		maximoIntegerV++;
				    		maximoInteger.set(0, maximoIntegerV);
				    		}
					completeTextElement.setAmbitos(maximoInteger);
				}
			   
			  }
		}
		
	}

	private Hoja findDatos(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals("Datos"))
				return hoja;
		}
		return null;
	}
	
	private Hoja findMetadatos(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals("Metadatos"))
				return hoja;
		}
		return null;
	}
	
	private Hoja findRecursos(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals("Recursos"))
				return hoja;
		}
		return null;
	}
	
	private Hoja findArchivos(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals("Archivos"))
				return hoja;
		}
		return null;
	}
	
	private Hoja findURLs(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals("URLs"))
				return hoja;
		}
		return null;
	}

	private int buscaelmaximoInteger(CompleteDocuments doc) {
		int max=0;
		for (CompleteElement iterable_element : doc.getDescription()) {
			if (iterable_element.getAmbitos().size()>0&&iterable_element.getAmbitos().get(0)>max)
				max=iterable_element.getAmbitos().get(0);
		}
		return max;
	}

	private ArrayList<Integer> calculamaximo(Long total, CompleteDocuments doc) {
		ArrayList<Integer> Max=new ArrayList<Integer>();
		for (CompleteElement long1 : doc.getDescription()) {
			if (total.equals(long1.getHastype().getClavilenoid())&&long1.getAmbitos().size()>0)
				{
				if (Max.isEmpty())
					for (int i = 0; i < long1.getAmbitos().size(); i++) {
								Max.add(0);
					}
				else if (Max.size()<=long1.getAmbitos().size())
					for (int i = 0; i < long1.getAmbitos().size(); i++) {
							if (i>Max.size())
								Max.add(0);
							
								
					}

				}
		}
		return Max;
	}



	


	private CompleteTextElementType generaStructura(String valor_de_celda, CompleteGrammar grammar, HashMap<String, CompleteTextElementType> hashPath) {
		 
		
		 CompleteTextElementType preproducido = hashPath.get(valor_de_celda);
			if (preproducido!=null)
				return preproducido;
		 
		 
		String[] pathL=valor_de_celda.split("\\\\");
		
		CompleteStructure Padre=null;
		
		 if (pathL.length>1)
			 Padre=producePadre(pathL,hashPath,grammar);
		 
		 CompleteTextElementType Salida=null;
		if (Padre!=null)
		 {
			Salida=new CompleteTextElementType(pathL[pathL.length-1], Padre);
			Padre.getSons().add(Salida);
		 }
		else 
			{
			Salida=new CompleteTextElementType(valor_de_celda, grammar);
			grammar.getSons().add(Salida);
			}
		
		hashPath.put(valor_de_celda, Salida);
		return Salida;
	}

	private CompleteStructure producePadre(String[] pathL,
			HashMap<String, CompleteTextElementType> hashPath,CompleteGrammar CG) {
		
		String Acumulado = "";
		CompleteTextElementType Padre = null;
		for (int i = 0; i < pathL.length-1; i++) {
			if (i!=0)
				Acumulado=Acumulado+"\\"+pathL[i];
			else
				Acumulado=Acumulado+pathL[i];
			CompleteTextElementType yo = hashPath.get(Acumulado);
			if (yo==null)
				{
				
				if (Padre!=null)
					{
					CompleteTextElementType Salida = new CompleteTextElementType(pathL[i], Padre);
					Padre.getSons().add(Salida);
					hashPath.put(Acumulado, Salida);
					}
				else
					{
					CompleteTextElementType Salida = new CompleteTextElementType(pathL[i], CG);
					CG.getSons().add(Salida);
					hashPath.put(Acumulado, Salida);
					}
				
				}
			
			Padre=yo;
		}
		return Padre;
	}

	public static void main(String[] args) {


	  String fileName = "ejemplo2.xls";
	 
	  System.out.println(fileName);
	 
	 CollectionXLSOdaTemplate C = new CollectionXLSOdaTemplate();
	 C.Leer_Archivo_Excel(fileName,new ArrayList<String>());
	 
	 System.out.println(C.getColeccion());
	 }



	@Override
	public void ProcessInstances() {
		
		
	}


	public CompleteCollection getColeccion() {
		return coleccionstatica;
	}
	

}
