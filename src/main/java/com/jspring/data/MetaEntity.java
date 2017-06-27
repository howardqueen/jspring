package com.jspring.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import com.jspring.Exceptions;
import com.jspring.Strings;

public class MetaEntity<T> {

	//////////////////
	/// FACTORY
	//////////////////
	private static byte[] locker = new byte[0];
	private static Map<String, MetaEntity<?>> metaEntities = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> MetaEntity<T> getMetaEntity(Class<T> entityClass) {
		if (metaEntities.containsKey(entityClass.getName())) {
			return (MetaEntity<T>) metaEntities.get(entityClass.getName());
		}
		synchronized (locker) {
			if (metaEntities.containsKey(entityClass.getName())) {
				return (MetaEntity<T>) metaEntities.get(entityClass.getName());
			}
			MetaEntity<T> entityInfo = new MetaEntity<>(entityClass);
			metaEntities.put(entityClass.getName(), entityInfo);
			return entityInfo;
		}
	}

	// @SuppressWarnings("unchecked")
	// public static <T> EntityInfo<T, ?> getEntityInfo(Class<T> entityClass) {
	// if (entityInfos.containsKey(entityClass.getName())) {
	// return (EntityInfo<T, ?>) entityInfos.get(entityClass.getName());
	// }
	// synchronized (locker) {
	// if (entityInfos.containsKey(entityClass.getName())) {
	// return (EntityInfo<T, ?>) entityInfos.get(entityClass.getName());
	// }
	// EntityInfo<T, ?> entityInfo = new EntityInfo<>(entityClass);
	// entityInfos.put(entityClass.getName(), entityInfo);
	// return entityInfo;
	// }
	// }

	//////////////////
	/// FACTORY
	//////////////////
	private final Class<T> entityClass;
	private final MetaField[] metaFields;
	// private boolean isPartitionDateTable;

	public Class<T> getEntityClass() {
		return entityClass;
	}

	private MetaEntity(Class<T> entityClass) {
		this.entityClass = entityClass;
		//
		Table table = entityClass.getAnnotation(Table.class);
		if (null == table) {
			this._database = "spring";
			this._sqlTableExpression = entityClass.getSimpleName();
			this._sqlTableName = entityClass.getSimpleName();
		} else {
			this._sqlTableExpression = Strings.isNullOrEmpty(table.name()) ? entityClass.getSimpleName() : table.name();
			this._sqlTableName = _sqlTableExpression.indexOf(' ') > 0 ? entityClass.getSimpleName()
					: _sqlTableExpression;
			this._sqlTableSchema = table.schema();
		}
		Field[] fs = entityClass.getFields();
		this.metaFields = new MetaField[fs.length];
		if (Strings.isNullOrEmpty(this._sqlTableSchema)) {
			StringBuilder schema = new StringBuilder();
			for (int i = 0; i < fs.length; i++) {
				metaFields[i] = new MetaField(fs[i]);
				if (null == this._idColumn && metaFields[i].isIdColumn()) {
					this._idColumn = metaFields[i];
				}
				if (metaFields[i].isReadonly()) {
					continue;
				}
				schema.append(',');
				schema.append(metaFields[i].getSchema());
			}
			this._sqlTableSchema = schema.substring(1);
		} else {
			for (int i = 0; i < fs.length; i++) {
				metaFields[i] = new MetaField(fs[i]);
				if (null == this._idColumn && metaFields[i].isIdColumn()) {
					this._idColumn = metaFields[i];
				}
			}
		}
	}

	//////////////////
	/// TABLE EXPRESSION OR NAME
	//////////////////
	private String _database;

	public String getDatabase() {
		return _database;
	}

	private String _sqlTableExpression;

	public String getSqlTableExpression() {
		return _sqlTableExpression;
	}

	private String _sqlTableSchema;

	public String getSqlTableSchema() {
		return _sqlTableSchema;
	}

	private String _sqlTableName;

	public String getSqlTableName() {
		return _sqlTableName;
	}

	private MetaField _idColumn;

	public MetaField getIdColumn() {
		return _idColumn;
	}

	private CrudTableInfo _crudTableInfo;

	public CrudTableInfo getCrudTableInfo() {
		if (null != _crudTableInfo) {
			return _crudTableInfo;
		}
		_crudTableInfo = new CrudTableInfo();
		CrudTable cv = getEntityClass().getAnnotation(CrudTable.class);
		if (null != cv) {
			_crudTableInfo.idField = getIdColumn().getFieldName();
			_crudTableInfo.title = cv.title();
			_crudTableInfo.width = cv.width();
			_crudTableInfo.height = cv.height();
			_crudTableInfo.createable = cv.createable();
			_crudTableInfo.createCheckNull = cv.createCheckNull();
			_crudTableInfo.updateable = cv.updateable();
			_crudTableInfo.updateCheckNull = cv.updateCheckNull();
			_crudTableInfo.exportable = cv.exportable();
			_crudTableInfo.partitionDateColumn = cv.partitionDateColumn();
		}
		if (Strings.isNullOrEmpty(_crudTableInfo.title)) {
			_crudTableInfo.title = getEntityClass().getSimpleName();
		}
		//
		_crudTableInfo.columns = new CrudColumnInfo[getEntityClass().getFields().length];
		int i = 0;
		for (Field f : getEntityClass().getFields()) {
			CrudColumnInfo v = new CrudColumnInfo();
			v.field = f.getName();
			v.fieldType = f.getType().getSimpleName();
			CrudColumn c = f.getAnnotation(CrudColumn.class);
			if (null != c) {
				v.title = c.title();
				v.header = c.header();
				v.sortable = c.sortable();
				v.filterable = c.filterable();
				v.width = c.width();
				v.height = c.height();
				v.createable = c.createable();
				v.required = c.required();
				v.updateable = c.updateable();
				v.readonly = c.readonly();
			}
			if (Strings.isNullOrEmpty(v.title)) {
				v.title = v.field;
			}
			_crudTableInfo.columns[i++] = v;
		}
		return _crudTableInfo;
	}

	//////////////////
	/// FIELD INFO
	//////////////////
	public MetaField getMetaField(String column) {
		for (MetaField i : metaFields) {
			if (i.getFieldName().equals(column)) {
				return i;
			}
		}
		throw Exceptions.newIllegalArgumentException(column);
	}

	public MetaField getMetaField(Enum<?> column) {
		return getMetaField(column.toString());
	}

	private String[] _sqlColumnExpressions;

	public String[] getSqlColumnExpressions() {
		if (null == _sqlColumnExpressions) {
			_sqlColumnExpressions = new String[entityClass.getFields().length];
			int i = 0;
			for (Field f : entityClass.getFields()) {
				_sqlColumnExpressions[i++] = getMetaField(f.getName()).getSqlColumnExpression();
			}
		}
		return _sqlColumnExpressions;
	}

	private String[] _sqlEditableColumnNames;

	public String[] getSqlEditableColumnNames() {
		if (null == _sqlEditableColumnNames) {
			List<String> columns = new ArrayList<>();
			for (Field f : entityClass.getFields()) {
				MetaField mf = getMetaField(f.getName());
				if (mf.isReadonly()) {
					columns.add(mf.getSqlColumnName());
				}
			}
			_sqlEditableColumnNames = columns.toArray(new String[0]);
		}
		return _sqlEditableColumnNames;
	}

}
