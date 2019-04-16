/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.pdb.pdbreader.type;

import java.math.BigInteger;

import ghidra.pdb.PdbByteReader;
import ghidra.pdb.PdbException;
import ghidra.pdb.pdbreader.*;

/**
 * This class represents various flavors of Member type.
 * <P>
 * Note: we do not necessarily understand each of these data type classes.  Refer to the
 *  base class for more information.
 */
public abstract class AbstractMemberMsType extends AbstractMsType {

	protected ClassFieldMsAttributes attribute;
	protected AbstractTypeIndex fieldTypeRecordIndex;
	protected BigInteger offset;
	protected AbstractString name;

	/**
	 * Constructor for this type.
	 * @param pdb {@link AbstractPdb} to which this type belongs.
	 * @param reader {@link PdbByteReader} from which this type is deserialized.
	 * @throws PdbException upon error parsing a field.
	 */
	public AbstractMemberMsType(AbstractPdb pdb, PdbByteReader reader) throws PdbException {
		super(pdb, reader);
		create();
		parseInitialFields(reader);
		pdb.pushDependencyStack(
			new CategoryIndex(CategoryIndex.Category.DATA, fieldTypeRecordIndex.get()));
		pdb.popDependencyStack();
		//TODO: might be Numeric (is for plain MSMember)
		offset = reader.parseNumeric();
		name.parse(reader);
	}

	/**
	 * Returns the name of this member type.
	 * @return Name type of the member type.
	 */
	@Override
	public String getName() {
		return name.get();
	}

	/**
	 * Returns the offset of the member.
	 * @return The offset of the member.
	 */
	public BigInteger getOffset() {
		return offset;
	}

	/**
	 * Returns the attributes of the member.
	 * @return The attributes of the member.
	 */
	public ClassFieldMsAttributes getAttribute() {
		return attribute;
	}

	/**
	 * Returns the type index of the field data type.
	 * @return Type index of the field data type.
	 */
	public int getFieldTypeRecordIndex() {
		return fieldTypeRecordIndex.get();
	}

	@Override
	public void emit(StringBuilder builder, Bind bind) {
		builder.append(attribute);
		builder.append(": ");
		StringBuilder myBuilder = new StringBuilder();
		myBuilder.append(name);
		pdb.getTypeRecord(fieldTypeRecordIndex.get()).emit(myBuilder, Bind.NONE);
		builder.append(myBuilder);
		builder.append("<@");
		builder.append(offset);
		builder.append(">");
	}

	/**
	 * Creates subcomponents for this class, which can be deserialized later.
	 * <P>
	 * Implementing class must initialize {@link #fieldTypeRecordIndex} and {@link #name}.
	 */
	protected abstract void create();

	/**
	 * Parses the inital fields for this type.
	 * <P>
	 * Implementing class must, in the appropriate order pertinent to itself, allocate/parse
	 * {@link #attribute}; also parse {@link #fieldTypeRecordIndex}.
	 * @param reader {@link PdbByteReader} from which the fields are parsed.
	 * @throws PdbException Upon not enough data left to parse.
	 */
	protected abstract void parseInitialFields(PdbByteReader reader) throws PdbException;

}
