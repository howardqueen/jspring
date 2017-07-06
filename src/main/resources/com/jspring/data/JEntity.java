//package com.jspring.data;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collector;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import javax.persistence.Table;
//
//import com.jspring.Exceptions;
//import com.jspring.Strings;
//
//public class JEntity<T> {
//
//	//////////////////
//	/// FACTORY
//	//////////////////
//	private static byte[] locker = new byte[0];
//	private static Map<String, JEntity<?>> entities = new HashMap<>();
//
//	@SuppressWarnings("unchecked")
//	public static <T> JEntity<T> of(Class<T> entityClass) {
//		if (entities.containsKey(entityClass.getName())) {
//			return (JEntity<T>) entities.get(entityClass.getName());
//		}
//		synchronized (locker) {
//			if (entities.containsKey(entityClass.getName())) {
//				return (JEntity<T>) entities.get(entityClass.getName());
//			}
//			JEntity<T> entityInfo = new JEntity<>(entityClass);
//			entities.put(entityClass.getName(), entityInfo);
//			return entityInfo;
//		}
//	}
//
//	//////////////////
//	/// FACTORY
//	//////////////////
//	public final Class<T> entityClass;
//	public final JField[] fields;
//
//	private JEntity(Class<T> entityClass) {
//		this.entityClass = entityClass;
//		//
//		Table table = entityClass.getAnnotation(Table.class);
//		if (null == table) {
//			this._database = "spring";
//			this._sqlTableExpression = entityClass.getSimpleName();
//			this._sqlTableName = entityClass.getSimpleName();
//		} else {
//			this._database = Strings.isNullOrEmpty(table.catalog()) ? "spring" : table.catalog();
//			this._sqlTableExpression = Strings.isNullOrEmpty(table.name()) ? entityClass.getSimpleName() : table.name();
//			this._sqlTableName = _sqlTableExpression.indexOf(' ') > 0 ? entityClass.getSimpleName()
//					: _sqlTableExpression;
//			this._sqlTableSchema = table.schema();
//		}
//		Field[] fs = entityClass.getFields();
//		this.fields = new JField[fs.length];
//		if (Strings.isNullOrEmpty(this._sqlTableSchema)) {
//			StringBuilder schema = new StringBuilder();
//			for (int i = 0; i < fs.length; i++) {
//				fields[i] = new JField(fs[i]);
//				if (null == this._idColumn && fields[i].isIdColumn()) {
//					this._idColumn = fields[i];
//				}
//				if (fields[i].isUpdateable()) {
//					continue;
//				}
//				schema.append(',');
//				schema.append(fields[i].getSchema());
//			}
//			this._sqlTableSchema = schema.substring(1);
//		} else {
//			for (int i = 0; i < fs.length; i++) {
//				fields[i] = new JField(fs[i]);
//				if (null == this._idColumn && fields[i].isIdColumn()) {
//					this._idColumn = fields[i];
//				}
//			}
//		}
//		if (null == this._idColumn) {
//			this._idColumn = fields[0];
//		}
//	}
//
//	//////////////////
//	/// TABLE EXPRESSION OR NAME
//	//////////////////
//	private String _database;
//
//	public String getDatabase() {
//		return _database;
//	}
//
//	private String _sqlTableExpression;
//
//	public String getSqlTableExpression() {
//		return _sqlTableExpression;
//	}
//
//	private String _sqlTableSchema;
//
//	public String getSqlTableSchema() {
//		return _sqlTableSchema;
//	}
//
//	private String _sqlTableName;
//
//	public String getSqlTableName() {
//		return _sqlTableName;
//	}
//
//	private JField _idColumn;
//
//	public JField getIdColumn() {
//		return _idColumn;
//	}
//
//	private JTables _jtablePojo;
//
//	public JTables getJTablePojo() {
//		if (null != _jtablePojo) {
//			return _jtablePojo;
//		}
//		_jtablePojo = new JTables();
//		JTable cv = entityClass.getAnnotation(JTable.class);
//		if (null != cv) {
//			_jtablePojo.idName = getIdColumn().getFieldName();
//			_jtablePojo.title = cv.title();
//			_jtablePojo.width = cv.width();
//			_jtablePojo.height = cv.height();
//			_jtablePojo.createable = cv.createable();
//			// _crudTableInfo.createCheckNull = cv.createCheckNull();
//			_jtablePojo.updateable = cv.updateable();
//			// _crudTableInfo.updateCheckNull = cv.updateCheckNull();
//			_jtablePojo.exportable = cv.exportable();
//			_jtablePojo.partitionDateColumn = cv.partitionDateColumn();
//		}
//		if (Strings.isNullOrEmpty(_jtablePojo.title)) {
//			_jtablePojo.title = entityClass.getSimpleName();
//		}
//		//
//		_jtablePojo.columns = new JColumns[entityClass.getFields().length];
//		int i = 0;
//		for (Field f : entityClass.getFields()) {
//			JColumns v = new JColumns();
//			v.field = f.getName();
//			v.fieldType = f.getType().getSimpleName();
//			JColumn c = f.getAnnotation(JColumn.class);
//			if (null != c) {
//				v.title = c.title();
//				v.header = c.header();
//				v.findAllable = c.selectable();
//				v.sortable = c.sortable();
//				v.filterable = c.filterable();
//				v.width = c.width();
//				v.height = c.height();
//				v.createable = c.insertable();
//				v.required = c.required();
//				v.findOneable = c.firstable();
//				v.editable = c.updateable();
//			}
//			if (Strings.isNullOrEmpty(v.title)) {
//				v.title = v.field;
//			}
//			_jtablePojo.columns[i++] = v;
//		}
//		return _jtablePojo;
//	}
//
//	//////////////////
//	/// FIELD INFO
//	//////////////////
//	public JField getJField(String column) {
//		return Stream.of(fields)//
//				.filter((a) -> a.getFieldName().equals(column))//
//				.findFirst()//
//				.orElseThrow(() -> Exceptions.newIllegalArgumentException(column));
//	}
//
//	public JField getJField(Enum<?> column) {
//		return getJField(column.toString());
//	}
//
//	private String[] _retrieveableColumns;
//
//	public String[] getRetrieveableColumns() {
//		if (null == _retrieveableColumns) {
//			_retrieveableColumns = Stream.of(entityClass.getFields())//
//					.map((a) -> getJField(a.getName()))//
//					//
//					.filter((a) -> a.isUpdateable())//
//					.map((a) -> a.getName4SQL())//
//					//
//					.collect(Collectors.toList())//
//					.toArray(new String[0]);
//		}
//		return _retrieveableColumns;
//	}
//
//	private String[] _updateableColumns;
//
//	public String[] getUpdateableColumns() {
//		if (null == _updateableColumns) {
//			_updateableColumns = Stream.of(entityClass.getFields())//
//					.map((a) -> getJField(a.getName()))//
//					//
//					.filter((a) -> a.isUpdateable())//
//					.map((a) -> a.getName4SQL())//
//					//
//					.collect(Collectors.toList())//
//					.toArray(new String[0]);
//		}
//		return _updateableColumns;
//	}
//
//	private String[] _insertableColumns;
//
//	public String[] getInsertableColumns() {
//		if (null == _insertableColumns) {
//			_insertableColumns = Stream.of(entityClass.getFields())//
//					.map((a) -> getJField(a.getName()))//
//					//
//					.filter((a) -> a.isUpdateable())//
//					.map((a) -> a.getName4SQL())//
//					//
//					.collect(Collectors.toList())//
//					.toArray(new String[0]);
//		}
//		return _insertableColumns;
//	}
//
//}
