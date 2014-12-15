/**
 * 
 */
package fdi.ucm.server.updateparser.xlstemplateoda;

import java.util.ArrayList;

import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteOperationalValueType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteOperationalView;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;

/**
 * Funcion que implementa las funciones estaticas de la exportacion
 * @author Joaquin Gayoso-Cabada
 *
 */
public class StaticFuctionsOdAaXLS {
	
	
	
	public static Integer getIDODAD(CompleteElementType attribute) {
		ArrayList<CompleteOperationalView> Shows = attribute.getShows();
		for (CompleteOperationalView show : Shows) {
			
			if (show.getName().equals(StaticNamesOdAaXLS.PRESNTACION))
			{
				ArrayList<CompleteOperationalValueType> ShowValue = show.getValues();
				for (CompleteOperationalValueType CompleteOperationalValueType : ShowValue) {
					if (CompleteOperationalValueType.getName().equals(StaticNamesOdAaXLS.OdaID))
						try {
							Integer I=Integer.parseInt(CompleteOperationalValueType.getDefault());
								return I;
						} catch (Exception e) {
							return null;
						}
						

				}
			}
		}
		return null;
		
	}
	
	public static boolean isIDOV(CompleteTextElementType hastype) {
		ArrayList<CompleteOperationalView> Shows = hastype.getShows();
		for (CompleteOperationalView show : Shows) {
			
			if (show.getName().equals(StaticNamesOdAaXLS.META))
			{
				ArrayList<CompleteOperationalValueType> ShowValue = show.getValues();
				for (CompleteOperationalValueType CompleteOperationalValueType : ShowValue) {
					if (CompleteOperationalValueType.getName().equals(StaticNamesOdAaXLS.TYPE))
						if (CompleteOperationalValueType.getDefault().equals(StaticNamesOdAaXLS.IDOV)) 
										return true;

				}
			}
		}
		return false;
	}
}
