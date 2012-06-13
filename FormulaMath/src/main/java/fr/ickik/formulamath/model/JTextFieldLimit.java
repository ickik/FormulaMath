package fr.ickik.formulamath.model;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Define a plain document (like template or decorator) to apply to JTextField to
 * limit the number of characters entered in the field. It is called after the change
 * listener of the JTextefield to adapt text with the template.
 * @author Ickik
 * @version 0.1.000, 13 June 2012
 * @since 0.3.4
 */
public final class JTextFieldLimit extends PlainDocument {

	private static final long serialVersionUID = 1L;
	private final int maxCharacters;
	
	/**
	 * Constructor which needs the number maximum of characters authorized in
	 * the JTextField.
	 * @param maxCharacters the number maximum of characters.
	 */
	public JTextFieldLimit(int maxCharacters) {
		this.maxCharacters = maxCharacters;
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if(str != null && (getLength() + str.length() < maxCharacters)){
			super.insertString(offs, str, a);
		}
	}
	
	@Override
	public void replace(int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		if(text != null && offset + text.length() < maxCharacters){
			super.replace(offset, length, text, attrs);
		}
	}
}
