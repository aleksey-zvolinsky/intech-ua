/**
 * This class is generated by jOOQ
 */
package com.intechua.db.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PacketsRecord extends org.jooq.impl.UpdatableRecordImpl<com.intechua.db.jooq.tables.records.PacketsRecord> implements org.jooq.Record5<java.lang.Integer, java.sql.Timestamp, java.lang.Integer, java.lang.Integer, java.lang.Integer> {

	private static final long serialVersionUID = 781154367;

	/**
	 * Setter for <code>PUBLIC.PACKETS.ID</code>.
	 */
	public void setId(java.lang.Integer value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>PUBLIC.PACKETS.ID</code>.
	 */
	public java.lang.Integer getId() {
		return (java.lang.Integer) getValue(0);
	}

	/**
	 * Setter for <code>PUBLIC.PACKETS.DATE</code>.
	 */
	public void setDate(java.sql.Timestamp value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>PUBLIC.PACKETS.DATE</code>.
	 */
	public java.sql.Timestamp getDate() {
		return (java.sql.Timestamp) getValue(1);
	}

	/**
	 * Setter for <code>PUBLIC.PACKETS.LEVEL1</code>.
	 */
	public void setLevel1(java.lang.Integer value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>PUBLIC.PACKETS.LEVEL1</code>.
	 */
	public java.lang.Integer getLevel1() {
		return (java.lang.Integer) getValue(2);
	}

	/**
	 * Setter for <code>PUBLIC.PACKETS.LEVEL2</code>.
	 */
	public void setLevel2(java.lang.Integer value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>PUBLIC.PACKETS.LEVEL2</code>.
	 */
	public java.lang.Integer getLevel2() {
		return (java.lang.Integer) getValue(3);
	}

	/**
	 * Setter for <code>PUBLIC.PACKETS.LEVEL3</code>.
	 */
	public void setLevel3(java.lang.Integer value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>PUBLIC.PACKETS.LEVEL3</code>.
	 */
	public java.lang.Integer getLevel3() {
		return (java.lang.Integer) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Integer> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.lang.Integer, java.sql.Timestamp, java.lang.Integer, java.lang.Integer, java.lang.Integer> fieldsRow() {
		return (org.jooq.Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.lang.Integer, java.sql.Timestamp, java.lang.Integer, java.lang.Integer, java.lang.Integer> valuesRow() {
		return (org.jooq.Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field1() {
		return com.intechua.db.jooq.tables.Packets.PACKETS.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field2() {
		return com.intechua.db.jooq.tables.Packets.PACKETS.DATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field3() {
		return com.intechua.db.jooq.tables.Packets.PACKETS.LEVEL1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field4() {
		return com.intechua.db.jooq.tables.Packets.PACKETS.LEVEL2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Integer> field5() {
		return com.intechua.db.jooq.tables.Packets.PACKETS.LEVEL3;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value2() {
		return getDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value3() {
		return getLevel1();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value4() {
		return getLevel2();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Integer value5() {
		return getLevel3();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PacketsRecord value1(java.lang.Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PacketsRecord value2(java.sql.Timestamp value) {
		setDate(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PacketsRecord value3(java.lang.Integer value) {
		setLevel1(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PacketsRecord value4(java.lang.Integer value) {
		setLevel2(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PacketsRecord value5(java.lang.Integer value) {
		setLevel3(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PacketsRecord values(java.lang.Integer value1, java.sql.Timestamp value2, java.lang.Integer value3, java.lang.Integer value4, java.lang.Integer value5) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PacketsRecord
	 */
	public PacketsRecord() {
		super(com.intechua.db.jooq.tables.Packets.PACKETS);
	}

	/**
	 * Create a detached, initialised PacketsRecord
	 */
	public PacketsRecord(java.lang.Integer id, java.sql.Timestamp date, java.lang.Integer level1, java.lang.Integer level2, java.lang.Integer level3) {
		super(com.intechua.db.jooq.tables.Packets.PACKETS);

		setValue(0, id);
		setValue(1, date);
		setValue(2, level1);
		setValue(3, level2);
		setValue(4, level3);
	}
}