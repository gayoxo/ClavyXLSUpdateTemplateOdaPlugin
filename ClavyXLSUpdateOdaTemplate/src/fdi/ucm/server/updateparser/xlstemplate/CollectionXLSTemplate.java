/**
 * 
 */
package fdi.ucm.server.updateparser.xlstemplate;

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
import fdi.ucm.server.updateparser.xlstemplate.struture.Hoja;
import fdi.ucm.server.updateparser.xlstemplate.struture.HojaAntigua;
import fdi.ucm.server.updateparser.xlstemplate.struture.HojaNueva;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class CollectionXLSTemplate implements InterfaceXLSTemplateparser {


	private static final String XLS_COLLECTION = "XLS COllection";
	private static final String COLECCION_A_APARTIR_DE_UN_XLS = "Coleccion a partir de un XLS";
	private CompleteCollection coleccionstatica;
	private static final Pattern regexAmbito = Pattern.compile("^Size: \\d+(\\S+\\})+$");
	private static final Pattern regexDatos = Pattern.compile("^Size: \\d+(\\{\\S+\\})+$");
	private static final Pattern regexValue = Pattern.compile("^(\\{\\d+\\})+$");
	
	public CollectionXLSTemplate() {
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
	 
	  */
	 public void Leer_Archivo_Excel(String Nombre_Archivo) {
	 
	  /**
	 
	   * Crea una nueva instancia de Lista_Datos_Celda
	 
	   */
	 
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
	 
	   HashMap<String, HojaNueva> ListaAmbitos=new HashMap<String, HojaNueva>();
	   
	   int NStilos=Libro_trabajo.getNumberOfSheets();
		 
	   for (int i = 0; i < NStilos; i++) {

		   XSSFSheet Hoja_hssf = Libro_trabajo.getSheetAt(i);
		   
		   String Name=Hoja_hssf.getSheetName();
		   if (!Name.endsWith("_Scopes"))
		   {
			   HojaNueva Hojax=new HojaNueva(Hoja_hssf.getSheetName());
			   
			   ListaAmbitos.put(Name+"_Scopes", Hojax);
			   
			   
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
		   else
		   {
			   HojaNueva Hojax= ListaAmbitos.get(Name);
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
			   
			   Hojax.setListaAmbitos(Lista_Datos_Celda2);
			   
			   
		   }
		   
		  
	}
	   
	   

	  } catch (Exception e) {
	 
	   e.printStackTrace();
	 
	  }
	 
	  return Salida;
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
	   
	   HashMap<String, HojaAntigua> ListaAmbitos=new HashMap<String, HojaAntigua>();
	   
	   int NStilos=Libro_trabajo.getNumberOfSheets();
	 
	   for (int i = 0; i < NStilos; i++) {
		   HSSFSheet Hoja_hssf = Libro_trabajo.getSheetAt(i);
		   
		   String Name=Hoja_hssf.getSheetName();
		   if (!Name.endsWith("_Scopes"))
		   {
		   
		   HojaAntigua Hojax=new HojaAntigua(Hoja_hssf.getSheetName());
		   
		   ListaAmbitos.put(Name+"_Scopes", Hojax);
		   
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
		   else
		   {
			   HojaAntigua Hojax= ListaAmbitos.get(Name);

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
			   
			   Hojax.setListaAmbitos(Lista_Datos_Celda2); 
		   }
		   
	}
	   
	   
	  } catch (Exception e) {
	 
	   e.printStackTrace();
	 
	  }
	  
	  return Salida;
	 
	 }
	 
	 /**
	 
	  * Este método se utiliza para imprimir los datos de la celda a la consola.
	 
	  *
	 
	  * @param Datos_celdas
	 
	  *            - Listado de los datos que hay en la hoja de cálculo.
	 
	  */
	 
	 private void Imprimir_Consola(List<Hoja> HojasEntrada) {
	 
		 Long counterbase=Long.MIN_VALUE;
	
	for (Hoja hoja : HojasEntrada) {
		
//		System.out.println("Nombre: " + hoja.getName());
		
		CompleteGrammar Grammar=new CompleteGrammar(hoja.getName(), hoja.getName(), coleccionstatica);
		coleccionstatica.getMetamodelGrammar().add(Grammar);
		HashMap<Integer, CompleteTextElementType> Hash=new HashMap<Integer, CompleteTextElementType>();
		HashMap<String, CompleteTextElementType> HashPath=new HashMap<String, CompleteTextElementType>();
		if (hoja instanceof HojaAntigua)
		{
			
			List<List<HSSFCell>> Datos_celdas = ((HojaAntigua) hoja).getListaHijos();
			List<List<HSSFCell>> Datos_celdas_ambito = ((HojaAntigua) hoja).getListaAmbitos();
			
			 String Valor_de_celda;
			 String Valor_de_celda_ambito;
			 
			  for (int FilaX = 0; FilaX < Datos_celdas.size(); FilaX++) {
			 
				CompleteDocuments Doc=new CompleteDocuments(coleccionstatica, Grammar, "", "");  
				if (FilaX!=0&&FilaX!=1)
					coleccionstatica.getEstructuras().add(Doc);
				  
			   List<HSSFCell> Lista_celda_temporal = Datos_celdas.get(FilaX);
			   List<HSSFCell> Lista_celda_temporal_Ambitos;
			   if (Datos_celdas_ambito.size()>FilaX)
				  Lista_celda_temporal_Ambitos = Datos_celdas_ambito.get(FilaX);
			   else
				   Lista_celda_temporal_Ambitos=new ArrayList<HSSFCell>();
			   
			 
			   ArrayList<CompleteTextElement> Ambitar=new ArrayList<CompleteTextElement>();
			   
			   for (int ColumnaX = 0; ColumnaX < Lista_celda_temporal.size(); ColumnaX++) {
			 
			 
			     HSSFCell hssfCell = Lista_celda_temporal.get(ColumnaX);
			     
			     HSSFCell hssfCell_Ambito;
				   if (Lista_celda_temporal_Ambitos.size()>ColumnaX)
					   hssfCell_Ambito = Lista_celda_temporal_Ambitos.get(ColumnaX);
				   else
					   hssfCell_Ambito=null;
			     
			     
				 if (hssfCell!=null)  
					 Valor_de_celda = hssfCell.toString();
				 else 
					 Valor_de_celda="";
			     
			     if (hssfCell_Ambito!=null)
			    	 Valor_de_celda_ambito = hssfCell_Ambito.toString();
			     else
			    	 Valor_de_celda_ambito="";
			     
			     if (ColumnaX==0)
			     {
			    	 try {
			    		 double Valorposible = Double.parseDouble(Valor_de_celda);
			    		 long valuecelda=(long)Valorposible;
			    		 Doc.setClavilenoid(valuecelda);
						} catch (Exception e) {
							Doc.setClavilenoid(counterbase);
							counterbase++;
						}
			    	
			     }
			     else if (ColumnaX==1)
			     {
			    	 if (FilaX==1)
				    	 try {
				    		 Grammar.setClavilenoid(Long.parseLong(Valor_de_celda));
							} catch (Exception e) {
								Grammar.setClavilenoid(-1l);
							}
			    	 Doc.setDescriptionText(Valor_de_celda);

			    	
			     }
			     else
			     {
			    if (FilaX==0)
			    	 {
			    	CompleteTextElementType C=generaStructura(Valor_de_celda,Grammar,HashPath);
			    	Hash.put(new Integer(ColumnaX), C);
//			    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
			    	
			    	 }
			    else if (FilaX==1)
		    	 {
			    	CompleteTextElementType C=Hash.get(new Integer(ColumnaX));
			    	try {
			    		C.setClavilenoid(Long.parseLong(Valor_de_celda));
					} catch (Exception e) {
						C.setClavilenoid(-1l);
					}
			    	
		    	Hash.put(new Integer(ColumnaX), C);
//		    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
		    	
		    	 }
			    else 
			    	{
			    	
			    	ArrayList<String> ValuesTotal=ProcessValue(Valor_de_celda);
			    	ArrayList<String> ValuesTotalAmbitos=ProcessValueAmbitos(Valor_de_celda_ambito);
			    	
			    	for (int i = 0; i < ValuesTotal.size(); i++) {
						CompleteTextElementType C=Hash.get(new Integer(ColumnaX));
				    	CompleteTextElement CT=new CompleteTextElement(C, ValuesTotal.get(i));
				    	CT.setDocumentsFather(Doc);
				    	ArrayList<Integer> ALIst = new ArrayList<Integer>();
				    	
				    	if (i<ValuesTotalAmbitos.size())    		
				    		ALIst.addAll(procesaAmbitos(ValuesTotalAmbitos.get(i)));
				    	else
				    		if (ValuesTotal.size()>1)
				    			Ambitar.add(CT);
				    		
				    	
				    	CT.setAmbitos(ALIst);
				    	if (!CT.getValue().isEmpty())
				    		Doc.getDescription().add(CT);
//				    	System.out.print("Valor:" +  ValuesTotal.get(i) + "\t\t");
						
					}
			    	
			    	
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
		}
		else if (hoja instanceof HojaNueva)
		{
			
			List<List<XSSFCell>> Datos_celdas = ((HojaNueva) hoja).getListaHijos();
			List<List<XSSFCell>> Datos_celdas_ambito = ((HojaNueva) hoja).getListaAmbitos();
			
			 String Valor_de_celda;
			 String Valor_de_celda_ambito;
			 
			  for (int FilaX = 0; FilaX < Datos_celdas.size(); FilaX++) {
			 
				  List<XSSFCell> Lista_celda_temporal = Datos_celdas.get(FilaX);
				  List<XSSFCell> Lista_celda_temporal_Ambitos;
				   if (Datos_celdas_ambito.size()>FilaX)
					  Lista_celda_temporal_Ambitos = Datos_celdas_ambito.get(FilaX);
				   else
					   Lista_celda_temporal_Ambitos=new ArrayList<XSSFCell>();
				  
				  CompleteDocuments Doc=new CompleteDocuments(coleccionstatica, Grammar, Integer.toString(FilaX), "");  
					if (FilaX!=0&&FilaX!=1)
						coleccionstatica.getEstructuras().add(Doc);
			 
					ArrayList<CompleteTextElement> Ambitar=new ArrayList<CompleteTextElement>();
			   for (int ColumnaX = 0; ColumnaX < Lista_celda_temporal.size(); ColumnaX++) {
			 
			  
			 
			     XSSFCell hssfCell = (XSSFCell) Lista_celda_temporal.get(ColumnaX);
			 
			     XSSFCell hssfCell_Ambito;
				   if (Lista_celda_temporal_Ambitos.size()>ColumnaX)
					   hssfCell_Ambito = Lista_celda_temporal_Ambitos.get(ColumnaX);
				   else
					   hssfCell_Ambito=null;
			     
				   if (hssfCell!=null)  
						 Valor_de_celda = hssfCell.toString();
					 else 
						 Valor_de_celda="";
				   
			     
			     if (hssfCell_Ambito!=null)
			    	 Valor_de_celda_ambito = hssfCell_Ambito.toString();
			     else
			    	 Valor_de_celda_ambito="";
			 
			     if (ColumnaX==0)
			     {
			    	 Doc.setDescriptionText(Valor_de_celda);
			    	 try {
			    		 Doc.setClavilenoid(Long.parseLong(Valor_de_celda));
						} catch (Exception e) {
							Doc.setClavilenoid(counterbase);
							counterbase--;
						}
			    	
			     }
			     else if (ColumnaX==1)
			     {
			    	 if (FilaX==1)
				    	 try {
				    		 Grammar.setClavilenoid(Long.parseLong(Valor_de_celda));
							} catch (Exception e) {
								Grammar.setClavilenoid(-1l);
							}
			    	 Doc.setDescriptionText(Valor_de_celda);

			    	
			     }
			     else
			     
			     {
			    if (FilaX==0)
			    	 {
			    	CompleteTextElementType C=generaStructura(Valor_de_celda,Grammar,HashPath);
			    	Hash.put(new Integer(ColumnaX), C);
//			    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
			    	
			    	 }
			    else if (FilaX==1)
		    	 {
			    	CompleteTextElementType C=Hash.get(new Integer(ColumnaX));
			    	try {
			    		C.setClavilenoid(Long.parseLong(Valor_de_celda));
					} catch (Exception e) {
						C.setClavilenoid(-1l);
					}
			    	
		    	Hash.put(new Integer(ColumnaX), C);
//		    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
		    	
		    	 }
			    else 
			    	{
			    	ArrayList<String> ValuesTotal=ProcessValue(Valor_de_celda);
			    	
			    	ArrayList<String> ValuesTotalAmbitos=ProcessValueAmbitos(Valor_de_celda_ambito);
			    	
			    	
			    	for (int i = 0; i < ValuesTotal.size(); i++) {
						CompleteTextElementType C=Hash.get(new Integer(ColumnaX));
				    	CompleteTextElement CT=new CompleteTextElement(C, ValuesTotal.get(i));
				    	CT.setDocumentsFather(Doc);
				    	ArrayList<Integer> ALIst=new ArrayList<Integer>();
				    	
				    	if (i<ValuesTotalAmbitos.size())
				    		ALIst.addAll(procesaAmbitos(ValuesTotalAmbitos.get(i)));
				    	else
				    		if (ValuesTotal.size()>1)
				    			Ambitar.add(CT);
				    	
				    	CT.setAmbitos(ALIst);
				    	
				    	if (!CT.getValue().isEmpty())
				    		Doc.getDescription().add(CT);
//				    	System.out.print("Valor:" +  ValuesTotal.get(i) + "\t\t");
						
					}
			    	
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



	private ArrayList<java.lang.Integer> procesaAmbitos(
			String Ambitoscompactados) {
		 
		 ArrayList<Integer> ListaAmbitos=new ArrayList<Integer>();
		 Matcher matcher = regexValue.matcher(Ambitoscompactados.trim());
		 if (matcher.matches())
		 {
		String Temp=Ambitoscompactados.replace('{', ' ');
		 String[] list=Temp.split("}");
		 for (String string : list) {
			 string=string.trim();
			 Integer nuevo=null;
			 try {
				nuevo=Integer.parseInt(string);
			} catch (Exception e) {

			}
			 
			 if (nuevo!=null)
				 ListaAmbitos.add(nuevo);
		}
		 }
		return ListaAmbitos;
	}

	private ArrayList<String> ProcessValueAmbitos(String valor_de_celda_ambito) {
		 
		 Matcher matcher = regexAmbito.matcher(valor_de_celda_ambito);
		 ArrayList<String> Salida=new ArrayList<String>();
		 if (matcher.matches())
		 {
			 int itend=valor_de_celda_ambito.indexOf("{");
			 Long it=Long.parseLong(valor_de_celda_ambito.substring(5,itend).trim());
			 String Temp=valor_de_celda_ambito.substring(itend);
			 Temp=Temp.replace("{{", "{");
			 Temp=Temp.replace("}}", "}$");
			 String[] list=Temp.split("\\$");
			 for (int i = 0; i < it && i<list.length; i++) {
				String E = list[i];
				E=E.trim();
				Salida.add(E);
			}
			 
		 }else
		 {
			 if (!valor_de_celda_ambito.trim().isEmpty())
				 Salida.add(valor_de_celda_ambito);
		 }		 
		return Salida;
	}

	private ArrayList<String> ProcessValue(String valor_de_celda) {
		 
		 Matcher matcher = regexDatos.matcher(valor_de_celda);
		 ArrayList<String> Salida=new ArrayList<String>();
		 if (matcher.matches())
		 {
			 int itend=valor_de_celda.indexOf("{");
			 Long it=Long.parseLong(valor_de_celda.substring(5,itend).trim());
			 String Temp=valor_de_celda.substring(itend);
			  Temp=Temp.replace('{', ' ');
			 String[] list=Temp.split("}");
			 for (int i = 0; i < it && i<list.length; i++) {
				String E = list[i];
				E=E.trim();
				Salida.add(E);
			}
			 
		 }else
		 {
			 Salida.add(valor_de_celda);
		 }		 
		return Salida;
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
		
		main2();
		main3();
		main4();

	  String fileName = "ejemplo1.xls";
	 
	  System.out.println(fileName);
	 
	 CollectionXLSTemplate C = new CollectionXLSTemplate();
	 C.Leer_Archivo_Excel(fileName);
	 
	 System.out.println(C);
	 }

	private static void main3() {
		 Matcher matcher = regexAmbito.matcher("Size: 4{34730}{34732}{34731}{-5}");
		 System.out.println(matcher.matches());
		
	}
	
	private static void main4() {
		 Matcher matcher = regexValue.matcher("{10}{10}{10}{10}{10}{10}");
		 System.out.println(matcher.matches());
		
	}

	@Override
	public void ProcessInstances() {
		
		
	}


	public CompleteCollection getColeccion() {
		return coleccionstatica;
	}
	
	private static void main2()
	{
		//^Size:d+(\\{(w+)\\})+$
		 Matcher matcher = regexDatos.matcher("Size: 4{34730}{34732}{34731}{-5}");
		 System.out.println(matcher.matches());

	}
}
