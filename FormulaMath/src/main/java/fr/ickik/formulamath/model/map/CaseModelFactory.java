package fr.ickik.formulamath.model.map;

import java.util.HashMap;

import fr.ickik.formulamath.model.CaseModel;

/**
 * Factory of CaseModel. It creates standard model for JCase. Every model returned
 * is the same instance, so it uses less memory because of unicity.
 * @author ickik
 * @version 0.1.000, 14 August 2012
 * @since 0.3.10
 */
public class CaseModelFactory {

	private final HashMap<Field, CaseModel> caseModelMap = new HashMap<Field, CaseModel>();
	
	/**
	 * Return a CaseModel depending the field given in argument.
	 * The model return is the same for 2 calls with the same field parameter.
	 * @param field the field of case model.
	 * @return the instance of the model for this field.
	 */
	public CaseModel getCaseModel(Field field) {
		CaseModel model = caseModelMap.get(field);
		if (model == null) {
			model = new CaseModel(field);
			caseModelMap.put(field, model);
		}
		return model;
	}
}
