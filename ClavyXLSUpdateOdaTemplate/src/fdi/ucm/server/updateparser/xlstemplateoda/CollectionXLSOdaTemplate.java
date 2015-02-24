/**
 * 
 */
package fdi.ucm.server.updateparser.xlstemplateoda;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementFile;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementURL;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteIterator;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteLinkElementType;
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
	private HashMap<Integer, Long> TablaOdaClavyModel;
	private HashMap<Integer, Long> TablaOdaClavyDocuments;
	private CompleteTextElementType IDOV;
	private CompleteGrammar Files;
	private CompleteGrammar URLS;
	private CompleteGrammar VirtualObject;
	private CompleteElementType URI;
	private CompleteElementType FilesFisico;
	private CompleteElementType FilesOwner;
	private HashSet<Long> TablaLiksIds;
	private HashSet<Long> TablaNumerosIds;
	private HashMap<String,Long> TablaReparacionFilesEquiv;
	private HashMap<String,Long> TablaReparacionUrlsEquiv;
	private HashSet<Long> TablaReparacionFiles;
	private HashSet<Long> TablaReparacionUrls;
	private HashMap<Long, Long> TablaReparacionFilesXLSOda;
	private HashMap<Long, Long> TablaReparacionUrlsXLSOda;
	private CompleteElementType Resource;
	

	
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
	 * @param coleccionActual 
	 
	  */
	 public void Leer_Archivo_Excel(String Nombre_Archivo, ArrayList<String> log, CompleteCollection coleccionActual) {
	 
	  /**
	 
	   * Crea una nueva instancia de Lista_Datos_Celda
	 
	   */
	 
		 Log=log;
		 
			TablaOdaClavyModel= new HashMap<Integer,Long>();
			TablaOdaClavyDocuments= new HashMap<Integer,Long>();
			TablaLiksIds= new HashSet<Long>();
			TablaNumerosIds= new HashSet<Long>();
			TablaReparacionFilesEquiv= new HashMap<String,Long>();
			TablaReparacionFilesXLSOda= new HashMap<Long,Long>();
			TablaReparacionUrlsEquiv= new HashMap<String,Long>();
			TablaReparacionUrlsXLSOda= new HashMap<Long,Long>();
			TablaReparacionFiles= new HashSet<Long>();
			TablaReparacionUrls= new HashSet<Long>();

			
			generaTablaOdaClavyModel1(coleccionActual.getMetamodelGrammar());
			generaTablaOdaClavyDocuments(coleccionActual.getEstructuras());
			generaTablaOdaClavyLink1(coleccionActual.getMetamodelGrammar());
			
			
			Files=findFiles(coleccionActual.getMetamodelGrammar());
			URLS=findURLsG(coleccionActual.getMetamodelGrammar());
			VirtualObject=findVO(coleccionActual.getMetamodelGrammar());
			
			

			if (Files==null)
				Log.add("File no encontrados en la coleccion de destino");
			if (URLS==null)
				Log.add("URL no encontradas en la coleccion de destino");
			if (VirtualObject==null)
				Log.add("Virtual Object no encontrado en la coleccion de destino");
			
			if ((Files==null)&&(URLS==null)&&(VirtualObject==null)) 
				return;
		
			
			if (Files!=null)
				{
				FilesFisico=findFilesFisico(Files.getSons());
				FilesOwner=findOwner(Files.getSons());
				if (FilesFisico==null)
					{
					Log.add("Coleccion destino no tiene el tipo 'File' para las Files");
					FilesFisico=new CompleteElementType(-1l,"Generado",Files);
					}
				if (FilesOwner==null)
					{
					Log.add("Coleccion destino no tiene el tipo 'Idov Owner' para las Files");
					FilesOwner=new CompleteElementType(-2l,"Generado",Files);
					}
				generaTablaOdaClavyFiles(coleccionActual.getEstructuras());
				}
			
			if (URLS!=null)
			{
				URI=findURI(URLS.getSons());
				if (URI==null)
					{
					Log.add("Coleccion destino no tiene el tipo 'URI' para las Urls");
					URI=new CompleteElementType(-1l,"Generado",URLS);
					}
				generaTablaOdaClavyURLS(coleccionActual.getEstructuras());
			}
			
			if (VirtualObject!=null)
			{
				Resource=findResources(VirtualObject.getSons());
				if (Resource==null)
					{
					Log.add("Coleccion destino no tiene el tipo 'Resources' para las Virtual Object");
					Resource=new CompleteElementType(-1l,"Generado",VirtualObject);
					}
				//generaTablaOdaClavyURLS(coleccionActual.getEstructuras());
			}
				
	
			
		 
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
	 
	  Proceso_LecturaHojas(Hojas);
	 
	 }
	 
	 private void generaTablaOdaClavyURLS(List<CompleteDocuments> estructuras) {
		 
		 for (CompleteDocuments completeDocuments : estructuras) {
				if (completeDocuments.getDocument()==URLS)
					{
					for (CompleteElement Elements : completeDocuments.getDescription()) {
						if (Elements.getHastype().getClavilenoid().equals(URI.getClavilenoid()))
							{
							if (Elements instanceof CompleteResourceElementFile)
								TablaReparacionUrlsEquiv.put(((CompleteResourceElementFile)Elements).getValue().getPath(),completeDocuments.getClavilenoid());
							if (Elements instanceof CompleteResourceElementURL)
								TablaReparacionUrlsEquiv.put(((CompleteResourceElementURL)Elements).getValue(),completeDocuments.getClavilenoid());
							TablaReparacionUrls.add(completeDocuments.getClavilenoid());
							}
					}	
					}
			}
			
	}

	private void generaTablaOdaClavyFiles(List<CompleteDocuments> estructuras) {

			for (CompleteDocuments completeDocuments : estructuras) {
				if (completeDocuments.getDocument()==Files)
					{
					for (CompleteElement Elements : completeDocuments.getDescription()) {
						if (Elements.getHastype().getClavilenoid().equals(FilesFisico.getClavilenoid()))
							{
							if (Elements instanceof CompleteResourceElementFile)
								TablaReparacionFilesEquiv.put(((CompleteResourceElementFile)Elements).getValue().getPath(),completeDocuments.getClavilenoid());
							if (Elements instanceof CompleteResourceElementURL)
								TablaReparacionFilesEquiv.put(((CompleteResourceElementURL)Elements).getValue(),completeDocuments.getClavilenoid());
							TablaReparacionFiles.add(completeDocuments.getClavilenoid());
							}
					}	
					}
			}
		
	}

	/**
	  * 
	  * @param metamodelGrammar
	  */
	 private void generaTablaOdaClavyLink1(List<CompleteGrammar> metamodelGrammar) {
		 for (CompleteGrammar completeGrammar : metamodelGrammar) {
			 generaTablaOdaClavyLink2(completeGrammar.getSons());
			}
		
	}

	 /**
	  * 
	  * @param sons
	  */
	private void generaTablaOdaClavyLink2(List<CompleteStructure> sons) {
		for (CompleteStructure completeStructure : sons) {
			if (completeStructure instanceof CompleteLinkElementType)
				TablaLiksIds.add(completeStructure.getClavilenoid());
			else
				if (completeStructure instanceof CompleteTextElementType&&StaticFuctionsOdAaXLS.isNumeric((CompleteTextElementType)completeStructure))
					TablaNumerosIds.add(completeStructure.getClavilenoid());
			
			generaTablaOdaClavyLink2(completeStructure.getSons());
				
			
		}
		
	}

	/**
	  * 
	  * @param estructuras
	  */
	 private void generaTablaOdaClavyDocuments(
			List<CompleteDocuments> estructuras) {
		for (CompleteDocuments completeDocuments : estructuras) {
			for (CompleteElement completeelem : completeDocuments.getDescription()) {
				if (completeelem.getHastype()!=null&&completeelem.getHastype() instanceof CompleteTextElementType&&StaticFuctionsOdAaXLS.isIDOV((CompleteTextElementType)completeelem.getHastype()))
					{
					String Value = ((CompleteTextElement)completeelem).getValue();
					try {
						Integer Idovv=Integer.parseInt(Value);
						TablaOdaClavyDocuments.put(Idovv, completeDocuments.getClavilenoid());
					} catch (Exception e) {
					}
					}
			}
		}
		
	}

	 /**
	  * 
	  * @param metamodelGrammar
	  */
	private void generaTablaOdaClavyModel1(List<CompleteGrammar> metamodelGrammar) {
		for (CompleteGrammar completeGrammar : metamodelGrammar) {
			generaTablaOdaClavyModel2(completeGrammar.getSons());
		}
		
	}

	/**
	 * 
	 * @param sons
	 */
	private void generaTablaOdaClavyModel2(List<CompleteStructure> sons) {
		for (CompleteStructure completeStructure : sons) {
			if (completeStructure instanceof CompleteElementType)
			{
				Integer IdovOda=StaticFuctionsOdAaXLS.getIDODAD((CompleteElementType)completeStructure);
				if (IdovOda!=null)
					TablaOdaClavyModel.put(IdovOda,completeStructure.getClavilenoid());
				
				if (completeStructure instanceof CompleteTextElementType && StaticFuctionsOdAaXLS.isIDOV((CompleteTextElementType) (completeStructure)))
					IDOV=(CompleteTextElementType)completeStructure;
			}
			
			generaTablaOdaClavyModel2(completeStructure.getSons());
				
			
		}
		
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
			   
			   if (Name.equals(NameConstantsOdAaXLS.DATOS)) 
				   Datos=true;
			   
			   if (Name.equals(NameConstantsOdAaXLS.META_DATOS)) 
				   Metadatos=true;
			   
			   if (Name.equals(NameConstantsOdAaXLS.RECURSOS2)) 
				   Recursos=true;
			   
			   if (Name.equals(NameConstantsOdAaXLS.ARCHIVOS)) 
				   Archivos=true;
			   
			   if (Name.equals(NameConstantsOdAaXLS.UR_LS)) 
				   URL=true;
			   
		   }
		   
		   if (!Datos) 
			   Log.add("Pestaña datos no encontrada");
		   
		   if (!Metadatos) 
			   Log.add("Pestaña MetaDatos no encontrada");
		   
		   if (!Recursos) 
			   Log.add("Pestaña Recursos no encontrada");
		   
		   if (!Archivos) 
			   Log.add("Pestaña Archivos no encontrada");
		   
		   if (!URL) 
			   Log.add("Pestaña URLs no encontrada");
		   
		
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
			   
			   if (Name.equals(NameConstantsOdAaXLS.DATOS)) 
				   Datos=true;
			   
			   if (Name.equals(NameConstantsOdAaXLS.META_DATOS)) 
				   Metadatos=true;
			   
			   if (Name.equals(NameConstantsOdAaXLS.RECURSOS2)) 
				   Recursos=true;
			   
			   if (Name.equals(NameConstantsOdAaXLS.ARCHIVOS)) 
				   Archivos=true;
			   
			   if (Name.equals(NameConstantsOdAaXLS.UR_LS)) 
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
	 
	 private void Proceso_LecturaHojas(List<Hoja> HojasEntrada) {
	 
		 Long counterbase=Long.MIN_VALUE;
	
		 Hoja Hoja_hssfDatos= findDatos(HojasEntrada);
		 Hoja Hoja_hssfMetaDatos= findMetadatos(HojasEntrada);
		 Hoja Hoja_hssfRecursos= findRecursos(HojasEntrada);
		 Hoja Hoja_hssfFiles= findArchivos(HojasEntrada);
		 Hoja Hoja_hssfURL= findURLs(HojasEntrada);

		if (Files!=null&&Hoja_hssfFiles!=null)
			procesFiles(Hoja_hssfFiles,counterbase);
			
		if (URLS!=null&&Hoja_hssfURL!=null)
			procesUrls(Hoja_hssfURL,counterbase);
		 
		if (VirtualObject!=null&&Hoja_hssfDatos!=null)
		   procesVO(Hoja_hssfDatos,Hoja_hssfMetaDatos,Hoja_hssfRecursos,counterbase);
		 

		 
		 
	 
	 
	 }


	private void procesUrls(Hoja hoja_hssfURL, Long counterbase) {
		CompleteGrammar Grammar=new CompleteGrammar("Urls", "Urls", coleccionstatica);
		Grammar.setClavilenoid(URLS.getClavilenoid());
		coleccionstatica.getMetamodelGrammar().add(Grammar);
		HashMap<Integer, CompleteTextElementType> Hash=new HashMap<Integer, CompleteTextElementType>();
//		HashMap<String, CompleteTextElementType> HashPath=new HashMap<String, CompleteTextElementType>();
		HashMap<String,CompleteDocuments> Documents=new HashMap<String,CompleteDocuments>();
		
		proceshoja(Grammar,hoja_hssfURL,Hash,
//				HashPath,
				Documents,counterbase, true,false,false,true);
		
		processUrlsTables();
	}

	/**
	 * 
	 */
	private void processUrlsTables() {
		ArrayList<CompleteDocuments> Lista=new ArrayList<CompleteDocuments>();
		for (CompleteDocuments Docs : coleccionstatica.getEstructuras()) {
			if (Docs.getDocument().getClavilenoid().equals(URLS.getClavilenoid()))
				Lista.add(Docs);
		}
		
		//TODO Por Aqui Hay que meterle mano
		
		ArrayList<CompleteDocuments> NoExisten=new ArrayList<CompleteDocuments>();
		
		for (CompleteDocuments completeDocuments : Lista) {
			if (!TablaReparacionUrls.contains(completeDocuments.getClavilenoid()))
				NoExisten.add(completeDocuments);
		}
		
		for (CompleteDocuments completeDocuments : NoExisten) {
			for (CompleteElement elem : completeDocuments.getDescription()) {
				if (elem instanceof CompleteTextElement&&elem.getHastype().getClavilenoid().equals(URI.getClavilenoid()))
					{
					String Value = ((CompleteTextElement)elem).getValue();
					Long Nuevo=TablaReparacionUrlsEquiv.get(Value.trim());
					if (Nuevo!=null)
					{
					Long Antiguo=completeDocuments.getClavilenoid();
					completeDocuments.setClavilenoid(Nuevo);
					TablaReparacionUrlsXLSOda.put(Antiguo, Nuevo);
					}else
					{
					Long Antiguo=completeDocuments.getClavilenoid();
					TablaReparacionUrlsXLSOda.put(Antiguo, Antiguo);
					}
					}
			}
			

			
		}
		
	}

	private void procesFiles(Hoja hoja_hssfFiles, Long counterbase) {
		CompleteGrammar Grammar=new CompleteGrammar("Files", "Files", coleccionstatica);
		coleccionstatica.getMetamodelGrammar().add(Grammar);
		Grammar.setClavilenoid(Files.getClavilenoid());
		HashMap<Integer, CompleteTextElementType> Hash=new HashMap<Integer, CompleteTextElementType>();
//		HashMap<String, CompleteTextElementType> HashPath=new HashMap<String, CompleteTextElementType>();
		HashMap<String,CompleteDocuments> Documents=new HashMap<String,CompleteDocuments>();

		proceshoja(Grammar,hoja_hssfFiles,Hash,
//				HashPath,
				Documents,counterbase, true,false,true,false);
		
		
		
		processFilesTables();
		
	}



	/**
	 * 
	 */
	private void processFilesTables() {
		ArrayList<CompleteDocuments> Lista=new ArrayList<CompleteDocuments>();
		for (CompleteDocuments Docs : coleccionstatica.getEstructuras()) {
			if (Docs.getDocument().getClavilenoid().equals(Files.getClavilenoid()))
				Lista.add(Docs);
		}
		
		//TODO Por Aqui Hay que meterle mano
		
		ArrayList<CompleteDocuments> NoExisten=new ArrayList<CompleteDocuments>();
		
		for (CompleteDocuments completeDocuments : Lista) {
			if (!TablaReparacionFiles.contains(completeDocuments.getClavilenoid()))
				NoExisten.add(completeDocuments);
		}
		
		for (CompleteDocuments completeDocuments : NoExisten) {
			for (CompleteElement elem : completeDocuments.getDescription()) {
				if (elem instanceof CompleteTextElement&&elem.getHastype().getClavilenoid().equals(FilesFisico.getClavilenoid()))
					{
					String Value = ((CompleteTextElement)elem).getValue();
					Long Nuevo=TablaReparacionFilesEquiv.get(Value.trim());
					if (Nuevo!=null)
						{
						Long Antiguo=completeDocuments.getClavilenoid();	
						completeDocuments.setClavilenoid(Nuevo);
						TablaReparacionFilesXLSOda.put(Antiguo, Nuevo);
						}
					else
						{
						Long Antiguo=completeDocuments.getClavilenoid();	
						TablaReparacionFilesXLSOda.put(Antiguo, Antiguo);
						}
					}
			}
			

			
		}
		
		
	}

	private void procesVO(Hoja hoja_hssfDatos, Hoja hoja_hssfMetaDatos,
			Hoja hoja_hssfRecursos, Long counterbase) {
		CompleteGrammar Grammar=new CompleteGrammar("Virtual object", "Virtual object", coleccionstatica);
		coleccionstatica.getMetamodelGrammar().add(Grammar);
		Grammar.setClavilenoid(VirtualObject.getClavilenoid());
		HashMap<Integer, CompleteTextElementType> Hash=new HashMap<Integer, CompleteTextElementType>();
//		HashMap<String, CompleteTextElementType> HashPath=new HashMap<String, CompleteTextElementType>();
		HashMap<String,CompleteDocuments> Documents=new HashMap<String,CompleteDocuments>();
		proceshoja(Grammar,hoja_hssfDatos,Hash,
//				HashPath,
				Documents,counterbase, true,false,false,false);
		proceshoja(Grammar,hoja_hssfMetaDatos,Hash,
//				HashPath,
				Documents,counterbase, false,false,false,false);
		proceshoja(Grammar,hoja_hssfRecursos,Hash,
//				HashPath,
				Documents,counterbase, false,true,false,false);
		
	}



	
	@SuppressWarnings("unchecked")
	private void proceshoja(CompleteGrammar grammar, Hoja hoja_hssfDatos,
			HashMap<Integer, CompleteTextElementType> hash,
//			HashMap<String, CompleteTextElementType> hashPath,
			HashMap<String, CompleteDocuments> documents, Long counterbase,Boolean Datos,Boolean Recursos,Boolean Files, Boolean URls) {
		
		
		HashMap<Long,Integer> Documents=new HashMap<Long,Integer>();
		HashSet<String> DocumentsIds=new HashSet<String>();
		HashMap<String, CompleteTextElementType> hashPath=new HashMap<String, CompleteTextElementType>();
		
		boolean nueva=false;
		
		if (hoja_hssfDatos instanceof HojaAntigua)
			nueva=false;
		else 
			nueva=true;
		
		Object Datos_celdas;
			//List<List<HSSFCell>>
		
		if (nueva)
			Datos_celdas = ((HojaNueva) hoja_hssfDatos).getListaHijos();
		else
			Datos_celdas = ((HojaAntigua) hoja_hssfDatos).getListaHijos();
			
			
			 
			  for (int FilaX = 0; FilaX < ((List<Object>)Datos_celdas).size(); FilaX++) {
			 
				  CompleteDocuments Doc=new CompleteDocuments(coleccionstatica, grammar, "", "");  
						
				
				Object Lista_celda_temporal;
				
				if (nueva)
					Lista_celda_temporal = ((List<List<XSSFCell>>)Datos_celdas).get(FilaX);
				else
					Lista_celda_temporal = ((List<List<HSSFCell>>)Datos_celdas).get(FilaX);
			   
			   Integer Ambito=0;	
			   
			   for (int ColumnaX = 0; ColumnaX < ((List<Object>)Lista_celda_temporal).size(); ColumnaX++) {
			 
				   String Valor_de_celda;
				   if (nueva)
				   {
					   XSSFCell hssfCell = ((List<XSSFCell>)Lista_celda_temporal).get(ColumnaX);
					     
						 if (hssfCell!=null)  
							 Valor_de_celda = hssfCell.toString();
						 else 
							 Valor_de_celda="";
				   }
				   else
				   {
					   HSSFCell hssfCell = ((List<HSSFCell>)Lista_celda_temporal).get(ColumnaX);
					     
						 if (hssfCell!=null)  
							 Valor_de_celda = hssfCell.toString();
						 else 
							 Valor_de_celda="";
			     
				   }
				 
				 if (!Valor_de_celda.isEmpty())  
				 { 
					 
			     if (ColumnaX==0)
			     {
			    	 
			    	 boolean ignorado=false;
			    	 
			    	 try {
			    		 
			    		 //TODO 
			    		 Long valueCeldaL; 
			    		 Integer valueCeldaLI=null;
						if (Valor_de_celda.startsWith("#"))
			    			 {
			    			 Valor_de_celda=Valor_de_celda.substring(1);
			    			 double Valorposible = Double.parseDouble(Valor_de_celda);
				    		 long valuecelda=(long)Valorposible;
				    		 valueCeldaL = Long.valueOf(valuecelda);

			    			 }
			    		 else
			    			 {
			    			 double Valorposible = Double.parseDouble(Valor_de_celda);
				    		 int valuecelda=(int)Valorposible;
				    		 valueCeldaLI = Integer.valueOf(valuecelda);
				    		 valueCeldaL=TablaOdaClavyDocuments.get(valueCeldaLI);
				    		 if (valueCeldaL==null)
				    			 valueCeldaL=(long)valuecelda;
			    			 }
			    							
						
						if (FilaX!=0&&Datos&&(FilaX!=1||Files||URls))
							if (!DocumentsIds.contains(Valor_de_celda))
							{
								DocumentsIds.add(Valor_de_celda);
							}
							else {
								ignorado=true;
							}
							
			    		 CompleteDocuments Doc2 = documents.get(Valor_de_celda );
			    		 if (Doc2!=null){
				    			 if (!ignorado)
				    				 Doc=Doc2;
				    			 else
				    				 Log.add("Advertencia: En la tabla: "+hoja_hssfDatos.getName() + " en la fila: " + FilaX +" el elemento en la columna: 0 con el Identificador:" +Valor_de_celda+ " esta duplicado, este elemento sera duplicado en los resultados");
			    		 }
			    		 else
			    		 {
			    		 Doc.setClavilenoid(valueCeldaL);
			    		 if (valueCeldaLI!=null&&Datos&&!Files&&!URls)
		    			 {
		    			 CompleteTextElement CTE=new CompleteTextElement(IDOV, Integer.toString(valueCeldaLI));
		    			 Doc.getDescription().add(CTE);
		    			 }
			    		 if (FilaX!=0&&(FilaX!=1||Files||URls))
			    			{

			    				 coleccionstatica.getEstructuras().add(Doc);
			    			
			    			}
			    		 documents.put(Valor_de_celda, Doc);
			    		 }
						} catch (Exception e) {
							Doc.setClavilenoid(counterbase);
							if (FilaX!=0&&(FilaX!=1||Files||URls))
								{

									coleccionstatica.getEstructuras().add(Doc);
								
								}
							 documents.put(Long.toString(counterbase), Doc);
							counterbase++;
						}
			    	 
			    	 if (Recursos)
			    	 {
			    	 Ambito=Documents.get(Doc.getClavilenoid());	
			    	 if (Ambito==null)
			    		 Ambito=0;
			    	 else Ambito=new Integer(Ambito.intValue()+1);
			    		 Documents.put(Doc.getClavilenoid(), Ambito);
			    	 }
			    	
			     }
			     
			     
			     else if (ColumnaX==1&&(Datos||Files||URls))
			     {
			    	 if (FilaX!=1||Files||URls)
				    	  Doc.setDescriptionText(Valor_de_celda);

			    	
			     }
			     else if (FilaX==1&&ColumnaX==1&&Recursos)
			     {
			    	 //Ignoralo porque el tipo se marca solo
			     }
			     else
			     {
			    if (FilaX==0)
			    	 {
			    	CompleteTextElementType C=generaStructura(Valor_de_celda,grammar,hashPath);
			    	if (URls&&ColumnaX==2)
			    			C.setClavilenoid(URI.getClavilenoid());
			    	if (Files&&ColumnaX==2)
		    			C.setClavilenoid(FilesOwner.getClavilenoid());
			    	if (Files&&ColumnaX==3)
		    			C.setClavilenoid(FilesFisico.getClavilenoid());
			    	if (Recursos&&ColumnaX==1)
		    			C.setClavilenoid(Resource.getClavilenoid());
			    	hash.put(new Integer(ColumnaX), C);
//			    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
			    	
			    	 }
			    
			    //TODO es aqui
			    else if (!URls&&!Files&&(FilaX==1))
		    	 {
			    	CompleteTextElementType C=hash.get(new Integer(ColumnaX));
			    	try {
			    		
			    		Long valueCeldaL;
			    		if (Valor_de_celda.startsWith("#"))
		    			 {
		    			 Valor_de_celda=Valor_de_celda.substring(1);
		    			 double Valorposible = Double.parseDouble(Valor_de_celda);
			    		 long valuecelda=(long)Valorposible;
			    		 valueCeldaL = Long.valueOf(valuecelda);
		    			 }
		    		 else
		    			 {
		    			 double Valorposible = Double.parseDouble(Valor_de_celda);
			    		 int valuecelda=(int)Valorposible;
			    		 Integer valueCeldaLI = Integer.valueOf(valuecelda);
			    		 valueCeldaL=TablaOdaClavyModel.get(valueCeldaLI);
			    		 if (valueCeldaL==null)
			    			 valueCeldaL=(long)valuecelda;
		    			 }
			    		
			    		C.setClavilenoid(valueCeldaL);
					} catch (Exception e) {
						C.setClavilenoid(-1l);
					}
			    	
		    	hash.put(new Integer(ColumnaX), C);
//		    	System.out.print("Columna:" + Valor_de_celda + "\t\t");
		    	
		    	 }
			    else 
			    	{
			    	CompleteTextElementType C=hash.get(new Integer(ColumnaX));

			    	
			    	if (TablaLiksIds.contains(C.getClavilenoid()))
			    		{
			    		if (Valor_de_celda.startsWith("#"))
			    			{
			    			
			    			
			    			Valor_de_celda=Valor_de_celda.substring(1);
			    			
			    			Long Nuevo1=TablaReparacionFilesXLSOda.get(Valor_de_celda);
				    		 Long Nuevo2=TablaReparacionUrlsXLSOda.get(Valor_de_celda);
				    		 
				    		 if (Nuevo2!=null)
				    			 Valor_de_celda=Long.toString(Nuevo2);
				    		 
				    		 if (Nuevo1!=null)
				    			 Valor_de_celda=Long.toString(Nuevo1);
			    			
			    			}
			    		else
			    		{
			    		try {
			    			double Valorposible = Double.parseDouble(Valor_de_celda);
				    		 int valuecelda=(int)Valorposible;
				    		 Integer valueCeldaLI = Integer.valueOf(valuecelda);
				    		 
				    		 
				    		 Long valueCeldaLIL=(long) valueCeldaLI;
				    		 
				    		 Long Nuevo1=TablaReparacionFilesXLSOda.get(valueCeldaLIL);
				    		 Long Nuevo2=TablaReparacionUrlsXLSOda.get(valueCeldaLIL);
				    		 Long Nuevo3=TablaOdaClavyDocuments.get(valueCeldaLI);
				    		 
				    		 Long valueCeldaL=null;
				    		 
				    		 if (Nuevo2!=null)
				    			 valueCeldaL=Nuevo2;
				    		 
				    		 if (Nuevo1!=null)
				    			 valueCeldaL=Nuevo1;
				    		 
				    		 if (Nuevo3!=null)
				    			 valueCeldaL = Nuevo3;
				    		 
				    		 
				    		 if (valueCeldaL!=null)
				    			 Valor_de_celda= Long.toString(valueCeldaL);

				    				 
						} catch (Exception e) {
							Log.add("Problema en el parseo de la tabla: "+ hoja_hssfDatos.getName() +" en columna: " + ColumnaX + " fila: " + FilaX + "con el valor de la celda "+ "\'"+Valor_de_celda+"\'" +", el elemento de enlace apunta a un valor que no se ha podida calcular como numerico y sera ignorado en el proceso de actualizacion en OdA" );
						}
			    		}
			    		
			    		}
			    	else if (TablaNumerosIds.contains(C.getClavilenoid()))
			    	{
			    		try {
			    			Double.parseDouble(Valor_de_celda);
						} catch (Exception e) {
							Log.add("Problema en el parseo de la tabla: "+ hoja_hssfDatos.getName() +" en columna: " + ColumnaX + " fila: " + FilaX + " con el valor de la celda "+ "\'"+Valor_de_celda+"\'" +", el elemento numerico apunta a un valor que no se ha podida calcular como numerico y sera ignorado en el proceso de actualizacion en OdA" );
						}
			    		
			    	}
			    		
			    	
			    	CompleteTextElement CT=new CompleteTextElement(C, Valor_de_celda);
			    	CT.setDocumentsFather(Doc);
			    	ArrayList<Integer> ALIst = new ArrayList<Integer>();
			    	
			    	if (Recursos)
			    		ALIst.add(Ambito);
			    	
			    	CT.setAmbitos(ALIst);
			    	if (!CT.getValue().isEmpty())
			    		Doc.getDescription().add(CT);
			    	

			    	
			    	
			    	}
			      
			     }
			     
			     
			   }
			   }
			 
			  }

		
	}

	private Hoja findDatos(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals(NameConstantsOdAaXLS.DATOS))
				return hoja;
		}
		return null;
	}
	
	private Hoja findMetadatos(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals(NameConstantsOdAaXLS.META_DATOS))
				return hoja;
		}
		return null;
	}
	
	private Hoja findRecursos(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals(NameConstantsOdAaXLS.RECURSOS2))
				return hoja;
		}
		return null;
	}
	
	private Hoja findArchivos(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals(NameConstantsOdAaXLS.ARCHIVOS))
				return hoja;
		}
		return null;
	}
	
	private Hoja findURLs(List<Hoja> hojasEntrada) {
		for (Hoja hoja : hojasEntrada) {
			if (hoja.getName().equals(NameConstantsOdAaXLS.UR_LS))
				return hoja;
		}
		return null;
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


	  String fileName = "ejemplo3.xls";
	 
	  System.out.println(fileName);
	 
	 CollectionXLSOdaTemplate C = new CollectionXLSOdaTemplate();
	 C.Leer_Archivo_Excel(fileName,new ArrayList<String>(),new CompleteCollection());
	 
	 System.out.println(C.getColeccion());
	 }



	@Override
	public void ProcessInstances() {
		
		
	}


	public CompleteCollection getColeccion() {
		return coleccionstatica;
	}
	
	 

	private static CompleteGrammar findVO(List<CompleteGrammar> metamodelGrammar) {
		for (CompleteGrammar completeGrammar : metamodelGrammar) {
			if (StaticFuctionsOdAaXLS.isVirtualObject(completeGrammar))
				return completeGrammar;
		}
		return null;
	}

	private static CompleteGrammar findFiles(List<CompleteGrammar> metamodelGrammar) {
		for (CompleteGrammar completeGrammar : metamodelGrammar) {
			if (StaticFuctionsOdAaXLS.isFiles(completeGrammar))
				return completeGrammar;
		}
		return null;
	}
	
	private static CompleteGrammar findURLsG(List<CompleteGrammar> metamodelGrammar) {
		for (CompleteGrammar completeGrammar : metamodelGrammar) {
			if (StaticFuctionsOdAaXLS.isURL(completeGrammar))
				return completeGrammar;
		}
		return null;
	}
	
	private static CompleteElementType findFilesFisico(List<CompleteStructure> metaStructures) {
		for (CompleteStructure completeStructure : metaStructures) {
			if (completeStructure instanceof CompleteElementType&&StaticFuctionsOdAaXLS.isFileFisico((CompleteElementType)completeStructure))
				return (CompleteElementType) completeStructure;
		}
		return null;
	}
	
	private static CompleteElementType findOwner(List<CompleteStructure> metaStructures) {
		for (CompleteStructure completeStructure : metaStructures) {
			if (completeStructure instanceof CompleteElementType&&StaticFuctionsOdAaXLS.isOwner((CompleteElementType)completeStructure))
				return (CompleteElementType) completeStructure;
		}
		return null;
	}
	
	private static CompleteElementType findURI(List<CompleteStructure> metaStructures) {
		for (CompleteStructure completeStructure : metaStructures) {
			if (completeStructure instanceof CompleteElementType&&StaticFuctionsOdAaXLS.isURI((CompleteElementType)completeStructure))
				return (CompleteElementType) completeStructure;
		}
		return null;
	}
	
	private static CompleteElementType findResources(ArrayList<CompleteStructure> sons) {
		  
		  for (CompleteStructure completeStruct : sons) {
			  if (completeStruct instanceof CompleteIterator)
				  for (CompleteStructure completeStruct2 : completeStruct.getSons()) {
						if (completeStruct2 instanceof CompleteElementType && StaticFuctionsOdAaXLS.isResources((CompleteElementType)completeStruct2))
							return (CompleteElementType)completeStruct2;
					}
		}
		  
		  
			return null;
	}

}
