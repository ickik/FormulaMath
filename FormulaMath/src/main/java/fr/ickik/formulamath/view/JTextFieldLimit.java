package fr.ickik.formulamath.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * 
 * @author Ickik
 * @version 0.1.000, 13 June 2012
 * @since 0.3.4
 */
public class JTextFieldLimit extends PlainDocument {

	private static final long serialVersionUID = 1L;
	private final int maxCharacters;
	
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
