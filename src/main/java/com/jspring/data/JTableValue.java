package com.jspring.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Id;

import com.jspring.Exceptions;
import com.jspring.Strings;

public class JTableValue {

	private static final List<JTableValue> tables = new ArrayList<>();

	public static JTableValue of(Class<?> domain) {
		return tables.stream().filter(a -> a.domain.equals(domain)).findFirst().orElseGet(() -> {
			synchronized (tables) {
				return tables.stream().filter(a -> a.domain.equals(domain)).findFirst().orElseGet(() -> {
					JTableValue t = new JTableValue(domain);
					tables.add(t);
					return t;
				});
			}
		});
	}

	public static JTableValue getByTableName(String tableName) {
		return tables.stream()//
				.filter(a -> a.name.equals(tableName))//
				.findFirst()//
				.orElseThrow(() -> Exceptions.newInstance("Not found table: " + tableName));
	}

	public static JTableValue getByDomainName(String domainName) {
		return tables.stream()//
				.filter(a -> a.domain.getSimpleName().equals(domainName))//
				.findFirst()//
				.orElseThrow(() -> Exceptions.newInstance("Not found domain: " + domainName));
	}

	public final Class<?> domain;
	public final JColumnValue primaryKey;
	public final JColumnValue[] columns;

	public JColumnValue getColumnByFieldName(String name) {
		return Stream.of(columns)//
				.filter(a -> a.getFieldName().equals(name))//
				.findFirst()//
				.get();
	}

	//
	public String schema;
	private String name;

	public String getName() {
		return name;
	}

	private String _sqlColumnPre;

	public String getSQLColumnPre() {
		return _sqlColumnPre;
	}

	private String _sqlJoinedTables;

	public String getSQLJoinedTables() {
		return _sqlJoinedTables;
	}

	private void init() {
		JoinTable[] jt = domain.getAnnotationsByType(JoinTable.class);
		if (null == jt || jt.length == 0) {
			_sqlColumnPre = "";
			_sqlJoinedTables = '`' + name + '`';
			return;
		}
		_sqlColumnPre = "`_`.";
		StringBuilder sql = new StringBuilder('`');
		sql.append(name);
		sql.append("` AS `_`");
		Stream.of(jt)//
				.forEach(a -> {
					sql.append(" ");
					sql.append(a.joinType());
					sql.append(" JOIN ");
					sql.append('`' + a.name() + '`');
					if (!Strings.isNullOrEmpty(a.nickName())) {
						sql.append(" AS `");
						sql.append(a.nickName());
						sql.append('`');
					}
					sql.append(" ON ");
					if (!Strings.isNullOrEmpty(a.nickName())) {
						sql.append("`");
						sql.append(a.nickName());
						sql.append('`');
					}
					sql.append(".`");
					sql.append(a.joinColumn());
					sql.append("` = ");
					if (Strings.isNullOrEmpty(a.referencedTable())) {
						sql.append(_sqlColumnPre);
						sql.append('`');
						sql.append(a.referencedColumn());
						sql.append('`');
					} else {
						sql.append('`');
						sql.append(a.referencedTable());
						sql.append("`.`");
						sql.append(a.referencedColumn());
						sql.append('`');
					}
				});
		_sqlJoinedTables = sql.toString();
	}

	public String title = "";
	public String width = "600px";
	public String height = "";
	public String partitionColumn = "";

	private JTableValue(Class<?> domain) {
		this.domain = domain;
		List<JColumnValue> cs = Stream.of(domain.getFields())//
				.filter(a -> JColumnTypes.of(a) != JColumnTypes.Unknown)//
				.map(a -> JColumnValue.of(this, a))//
				.collect(Collectors.toList());
		primaryKey = cs.stream()//
				.filter(a -> null != a.field.getType().getAnnotation(Id.class))//
				.findFirst()//
				.orElseGet(() -> cs.get(0));
		primaryKey.nullable = false;
		primaryKey.unique = false;
		primaryKey.indexName = "";
		primaryKey.findAllable = true;
		primaryKey.findOnable = true;
		cs.remove(primaryKey);
		this.columns = cs.toArray(new JColumnValue[0]);
		//
		JTable table = domain.getAnnotation(JTable.class);
		if (null == table) {
			this.schema = "spring";
			this.name = domain.getSimpleName();
			this.title = this.name;
			init();
			return;
		}
		this.schema = Strings.isNullOrEmpty(table.schema()) ? "spring" : table.schema();
		this.name = Strings.isNullOrEmpty(table.name()) ? domain.getSimpleName() : table.name();
		this.title = Strings.isNullOrEmpty(table.title()) ? this.name : table.title();
		this.width = table.width();
		this.height = table.height();
		this.partitionColumn = table.partitionColumn();
		init();
	}

	//////////////////
	/// MORE
	//////////////////

	private JColumnValue[] findAllableColumns;

	public JColumnValue[] getFindAllableColumns() {
		if (null == findAllableColumns) {
			List<JColumnValue> cs = Stream.of(columns)//
					.filter(a -> a.findAllable)//
					.collect(Collectors.toList());//
			cs.add(0, primaryKey);
			findAllableColumns = cs.toArray(new JColumnValue[0]);
		}
		return findAllableColumns;
	}

	private JColumnValue[] findOnableColumns;

	public JColumnValue[] getFindOnableColumns() {
		if (null == findOnableColumns) {
			List<JColumnValue> cs = Stream.of(columns)//
					.filter(a -> a.findOnable)//
					.collect(Collectors.toList());//
			cs.add(0, primaryKey);
			findOnableColumns = cs.toArray(new JColumnValue[0]);
		}
		return findOnableColumns;
	}

	private JColumnValue[] insertableColumns;

	public JColumnValue[] getInsertableColumns() {
		if (null == insertableColumns) {
			List<JColumnValue> cs = Stream.of(columns)//
					.filter(a -> a.isInsertable())//
					.collect(Collectors.toList());//
			if (!primaryKey.isIdentity()) {
				cs.add(0, primaryKey);
			}
			insertableColumns = cs.toArray(new JColumnValue[0]);
		}
		return findAllableColumns;
	}

	private JColumnValue[] updatableColumns;

	public JColumnValue[] getUpdatableColumns() {
		if (null == updatableColumns) {
			updatableColumns = Stream.of(columns)//
					.filter(a -> a.isUpdatable())//
					.collect(Collectors.toList())//
					.toArray(new JColumnValue[0]);
		}
		return findAllableColumns;
	}

	private String _definition;

	public String getDefinition() {
		if (null == _definition) {
			StringBuilder sb = new StringBuilder();
			sb.append('`');
			sb.append(name);
			sb.append("`(");
			Stream.of(columns).filter(a -> a.isEditable())//
					.forEach(a -> {
						sb.append(a.getColumnDefinition());
						sb.append(',');
					});
			sb.append("PRIMARY KEY (`" + primaryKey.getColumnName() + "`)");
			// UNIQUE KEY `ix_phone` (`phone`),
			Stream.of(columns)//
					.filter(a -> a.unique)//
					.collect(Collectors.toList())//
					.forEach(a -> {
						sb.append(", UNIQUE KEY `uix_");
						sb.append(a.getColumnName());
						sb.append("`(");
						sb.append(a.getColumnName());
						sb.append(')');
					});
			// KEY `ix_createTime` (`createTime`),
			Stream.of(columns)//
					.filter(a -> !Strings.isNullOrEmpty(a.indexName))//
					.collect(Collectors.groupingBy(a -> a.indexName))//
					.forEach((s, l) -> {
						sb.append(", KEY `");
						sb.append(s);
						sb.append("`(`");
						sb.append(l.stream()//
								.map(a -> a.getColumnName())//
								.reduce((r, i) -> r + "`,`" + i)//
								.get());
						sb.append("`)");
					});
			sb.append(")ENGINE=MyISAM DEFAULT CHARSET=UTF8;");
			_definition = sb.toString();
		}
		return _definition;
	}

	private JTableView _view;

	public JTableView getView() {
		if (null == _view) {
			synchronized (this) {
				if (null == _view) {
					_view = new JTableView(this);
				}
			}
		}
		return _view;
	}

}
