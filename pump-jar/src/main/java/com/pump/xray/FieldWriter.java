/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.xray;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * This writes a java.lang.reflect.Field.
 * <p>
 * Fields are usually declared but not defined, unless the field
 * is a constant. Simple field values (primitives and Strings) may be
 * directly copied into the autogenerated code, but everything else
 * is represented as null.
 * 
 */
public class FieldWriter extends StreamWriter {

	protected Field field;

	/** Create a new FieldWriter.
	 * 
	 * @param sourceCodeManager the optional SourceCodeManager.
	 * @param field the Field this FieldWriter will write.
	 */
	public FieldWriter(SourceCodeManager sourceCodeManager,Field field) {
		super(sourceCodeManager);
		this.field = field;
	}

	/**
	 * Return the Field this FieldWriter emulates.
	 */
	public Field getField() {
		return field;
	}

	@Override
	public void write(ClassWriterStream cws) throws Exception {
		cws.print( toString( field.getModifiers() ));
		cws.print( ' ' + toString(cws.getNameMap(), field.getGenericType())+" "+field.getName());
		Object value = getSupportedConstantValue(cws.getNameMap());
		if(value!=null) {
			cws.println(" = "+value+";");
		} else {
			cws.println(";");
		}
	}

	/**
	 * Get the value to write in the autogenerated code for this field's declaration,
	 * or null if the field shouldn't be given a default value.
	 * 
	 * @param nameToSimpleName a map of full java names to simple names (such as "java.lang.Thread" to "Thread").
	 * @return the String to write in the autogenerated code declaring this field. This could be "0.0", "0L", "null", ""myString"", etc.
	 * If this returns null: then this field should not be declared with a value.
	 */
	protected String getSupportedConstantValue(Map<String, String> nameToSimpleName) throws IllegalArgumentException, IllegalAccessException {
		boolean isFinal = field!=null && Modifier.isFinal(field.getModifiers());
		if (isFinal)
		{	
			String returnValue = null;
			Class valueType = field.getType();
			if (String.class.equals(valueType) || 
					Character.TYPE.equals(valueType) || 
					Long.TYPE.equals(valueType) ||
					Float.TYPE.equals(valueType) ||
					Integer.TYPE.equals(valueType) ||
					Short.TYPE.equals(valueType)  ||
					Float.TYPE.equals(valueType)  ||
					Boolean.TYPE.equals(valueType)  ||
					Double.TYPE.equals(valueType)   ||
					Byte.TYPE.equals(valueType)  )
			{
				boolean isStatic = Modifier.isStatic(field.getModifiers());
				if (isStatic) {
					field.setAccessible(true);
					Object v = field.get(null);
					returnValue = toString(v);
				} else {
					returnValue = getValue(nameToSimpleName, valueType, false);
				}
			}
			if(returnValue==null)
				return "null";
			return returnValue;
		}
		return null;
	}

}