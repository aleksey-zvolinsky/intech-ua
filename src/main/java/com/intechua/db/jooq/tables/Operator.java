/**
 * This class is generated by jOOQ
 */
package com.intechua.db.jooq.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Operator extends org.jooq.impl.TableImpl<com.intechua.db.jooq.tables.records.OperatorRecord> {

	private static final long serialVersionUID = -329346108;

	/**
	 * The singleton instance of <code>PUBLIC.OPERATOR</code>
	 */
	public static final com.intechua.db.jooq.tables.Operator OPERATOR = new com.intechua.db.jooq.tables.Operator();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<com.intechua.db.jooq.tables.records.OperatorRecord> getRecordType() {
		return com.intechua.db.jooq.tables.records.OperatorRecord.class;
	}

	/**
	 * The column <code>PUBLIC.OPERATOR.ID</code>.
	 */
	public final org.jooq.TableField<com.intechua.db.jooq.tables.records.OperatorRecord, java.lang.Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>PUBLIC.OPERATOR.USERNAME</code>.
	 */
	public final org.jooq.TableField<com.intechua.db.jooq.tables.records.OperatorRecord, java.lang.String> USERNAME = createField("USERNAME", org.jooq.impl.SQLDataType.VARCHAR.length(256), this, "");

	/**
	 * The column <code>PUBLIC.OPERATOR.PASSWORD</code>.
	 */
	public final org.jooq.TableField<com.intechua.db.jooq.tables.records.OperatorRecord, java.lang.String> PASSWORD = createField("PASSWORD", org.jooq.impl.SQLDataType.VARCHAR.length(256), this, "");

	/**
	 * Create a <code>PUBLIC.OPERATOR</code> table reference
	 */
	public Operator() {
		this("OPERATOR", null);
	}

	/**
	 * Create an aliased <code>PUBLIC.OPERATOR</code> table reference
	 */
	public Operator(java.lang.String alias) {
		this(alias, com.intechua.db.jooq.tables.Operator.OPERATOR);
	}

	private Operator(java.lang.String alias, org.jooq.Table<com.intechua.db.jooq.tables.records.OperatorRecord> aliased) {
		this(alias, aliased, null);
	}

	private Operator(java.lang.String alias, org.jooq.Table<com.intechua.db.jooq.tables.records.OperatorRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, com.intechua.db.jooq.Public.PUBLIC, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Identity<com.intechua.db.jooq.tables.records.OperatorRecord, java.lang.Integer> getIdentity() {
		return com.intechua.db.jooq.Keys.IDENTITY_OPERATOR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<com.intechua.db.jooq.tables.records.OperatorRecord> getPrimaryKey() {
		return com.intechua.db.jooq.Keys.SYS_PK_10091;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<com.intechua.db.jooq.tables.records.OperatorRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<com.intechua.db.jooq.tables.records.OperatorRecord>>asList(com.intechua.db.jooq.Keys.SYS_PK_10091);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.intechua.db.jooq.tables.Operator as(java.lang.String alias) {
		return new com.intechua.db.jooq.tables.Operator(alias, this);
	}

	/**
	 * Rename this table
	 */
	public com.intechua.db.jooq.tables.Operator rename(java.lang.String name) {
		return new com.intechua.db.jooq.tables.Operator(name, null);
	}
}