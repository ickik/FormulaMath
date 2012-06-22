package fr.ickik.formulamath.view;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import fr.ickik.formulamath.controler.InformationMessageListener;
import fr.ickik.formulamath.model.InformationModel;

/**
 * Information display in the menu. It display a pool of information.
 * @author Ickik.
 * @version 0.1.0, 22 June 2012
 * @since 0.3.4
 */
public final class InformationPanel extends JLabel implements InformationMessageListener {

	private static final long serialVersionUID = 3510798157965794024L;
	private InformationModel model;
	
	/**
	 * COnstructor of the Component
	 */
	public InformationPanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Information Panel"));
	}

	/**
	 * Return the model which display the text.
	 * @return the model.
	 */
	public InformationModel getModel() {
		return model;
	}
	
	/**
	 * Set the model of displaying of this component.
	 * @param model the model.
	 */
	public void setInformationModel(InformationModel model) {
		this.model = model;
	}

	@Override
	public void displayMessage(String message) {
		setText(message);
	}

}
