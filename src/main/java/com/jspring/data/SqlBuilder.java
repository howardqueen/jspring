package com.jspring.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.jspring.Exceptions;
import com.jspring.Strings;
import com.jspring.persistence.JColumnValue;
import com.jspring.persistence.JTableValue;

public class SqlBuilder {

	private final StringBuilder sql;
	private final List<Object> args;
	private final JTableValue table;

	public SqlBuilder(JTableValue table) {
		this.sql = new StringBuilder();
		this.args = new ArrayList<>();
		this.table = table;
	}

	//////////////////
	/// EXECUTE
	//////////////////
	@Override
	public String toString() {
		return sql.toString();
	}

	public <R> R execute(Function<String, R> argsNullExecutor, BiFunction<String, Object[], R> executor) {
		if (args.isEmpty()) {
			return argsNullExecutor.apply(this.sql.toString());
		}
		return executor.apply(this.sql.toString(), args.toArray(new Object[0]));
	}

	public <R> R executeArgsNotNull(BiFunction<String, Object[], R> argsNotNullFunc) {
		if (args.isEmpty()) {
			throw Exceptions.newInstance("SQL body is empty: " + sql.toString());
		}
		return argsNotNullFunc.apply(this.sql.toString(), args.toArray(new Object[0]));
	}

	//////////////////
	/// SELECT
	//////////////////
	public SqlBuilder select(JColumnValue... columns) {
		sql.append(" SELECT ");
		boolean isAppend = false;
		for (JColumnValue column : columns) {
			if (isAppend) {
				sql.append(',');
			} else {
				isAppend = true;
			}
			sql.append(column.getColumnSQL());
		}
		sql.append(" FROM ");
		sql.append(table.getSQLJoinedTables());
		return this;
	}

	public SqlBuilder select(String... expressions) {
		sql.append(" SELECT ");
		boolean isAppend = false;
		for (String e : expressions) {
			if (isAppend) {
				sql.append(',');
			} else {
				isAppend = true;
			}
			sql.append(e);
		}
		sql.append(" FROM ");
		sql.append(table.getSQLJoinedTables());
		return this;
	}

	public SqlBuilder where(Where... wheres) {
		if (null == wheres || wheres.length == 0) {
			return this;
		}
		sql.append(" WHERE ");
		Where.appendTo(sql, args, table, wheres);
		return this;
	}

	public SqlBuilder orderBy(OrderBy... orders) {
		if (null == orders || orders.length == 0) {
			return this;
		}
		sql.append(" ORDER BY ");
		OrderBy.appendTo(sql, table, orders);
		return this;
	}

	public SqlBuilder limit(int page, int size) {
		if (page <= 0) {
			page = 1;
		}
		if (size <= 0) {
			size = 1;
		}
		sql.append(" LIMIT ");
		sql.append((page - 1) * size);
		sql.append(',');
		sql.append(' ');
		sql.append(size);
		return this;
	}

	//////////////////
	/// DELETE
	//////////////////
	public SqlBuilder delete() {
		sql.append(" DELETE FROM ");
		sql.append('`');
		sql.append(table.getName());
		sql.append('`');
		return this;
	}

	//////////////////
	/// UPDATE
	//////////////////
	public SqlBuilder update() {
		sql.append(" UPDATE ");
		sql.append('`');
		sql.append(table.getName());
		sql.append('`');
		return this;
	}

	public SqlBuilder setWithKey(Object entity, JColumnValue primaryKey, JColumnValue... updateableColumns) {
		if (null == updateableColumns || updateableColumns.length == 0) {
			throw Exceptions.newInstance("SQL update columns is empty: " + sql.toString());
		}
		sql.append(" SET ");
		boolean isAppend = false;
		for (JColumnValue column : updateableColumns) {
			Object obj = column.getValue(entity);
			// if (null == obj) {
			// continue;
			// }
			if (isAppend) {
				sql.append(',');
			} else {
				isAppend = true;
			}
			sql.append('`');
			sql.append(column.getColumnName());
			sql.append("`=");
			sql.append('?');
			args.add(obj);
		}
		return where(Where.of(primaryKey).equalWith(primaryKey.getValue(entity)));//
	}
	
	public SqlBuilder setSkipNullWithKey(Object entity, JColumnValue primaryKey, JColumnValue... updateableColumns) {
		if (null == updateableColumns || updateableColumns.length == 0) {
			throw Exceptions.newInstance("SQL update columns is empty: " + sql.toString());
		}
		sql.append(" SET ");
		boolean isAppend = false;
		for (JColumnValue column : updateableColumns) {
			Object obj = column.getValue(entity);
			if (null == obj) {
				continue;
			}
			if (isAppend) {
				sql.append(',');
			} else {
				isAppend = true;
			}
			sql.append('`');
			sql.append(column.getColumnName());
			sql.append("`=");
			sql.append('?');
			args.add(obj);
		}
		return where(Where.of(primaryKey).equalWith(primaryKey.getValue(entity)));//
	}

	public SqlBuilder setSkipNull(UnaryOperator<String> map, JColumnValue... updateableColumns) {
		sql.append(" SET ");
		boolean isAppend = false;
		for (JColumnValue column : updateableColumns) {
			String obj = map.apply(column.getFieldName());
			if (Strings.isNullOrEmpty(obj)) {
				continue;
			}
			if (isAppend) {
				sql.append(',');
			} else {
				isAppend = true;
			}
			sql.append('`');
			sql.append(column.getColumnName());
			sql.append("`=");
			sql.append('?');
			args.add(obj);
		}
		if (!isAppend) {
			throw Exceptions.newInstance("SQL body is empty after skip null: " + sql.toString());
		}
		return this;
	}

	public SqlBuilder whereNotNull(Where... wheres) {
		if (null == wheres || wheres.length == 0) {
			throw Exceptions.newInstance("SQL where is empty: " + sql.toString());
		}
		sql.append(" WHERE ");
		Where.appendTo(sql, args, table, wheres);
		return this;
	}

	//////////////////
	/// INPLACE
	//////////////////
	public SqlBuilder insert() {
		sql.append(" INSERT INTO ");
		sql.append('`');
		sql.append(table.getName());
		sql.append('`');
		return this;
	}

	public SqlBuilder insertIgnore() {
		sql.append(" INSERT IGNORE INTO ");
		sql.append('`');
		sql.append(table.getName());
		sql.append('`');
		return this;
	}

	public SqlBuilder replace() {
		sql.append(" REPLACE INTO ");
		sql.append('`');
		sql.append(table.getName());
		sql.append('`');
		return this;
	}

	public SqlBuilder values(JColumnValue[] insertableColumns, Object... entities) {
		if (null == insertableColumns || insertableColumns.length == 0 //
				|| null == entities || entities.length == 0) {
			throw Exceptions.newInstance("SQL insert columns or values is empty: " + sql.toString());
		}
		sql.append('(');
		boolean isAppend = false;
		for (JColumnValue column : insertableColumns) {
			if (isAppend) {
				sql.append(',');
				sql.append(' ');
			} else {
				isAppend = true;
			}
			sql.append('`');
			sql.append(column.getColumnName());
			sql.append('`');
		}
		sql.append(')');
		//
		sql.append(" VALUES ");
		isAppend = false;
		for (Object entity : entities) {
			sql.append('(');
			isAppend = false;
			for (JColumnValue column : insertableColumns) {
				if (isAppend) {
					sql.append(',');
					sql.append(' ');
				} else {
					isAppend = true;
				}
				sql.append('?');
				args.add(column.getValue(entity));
			}
			sql.append(')');
			sql.append(',');
		}
		return this;
	}

	public SqlBuilder valuesSkipNull(UnaryOperator<String> map, JColumnValue... insertableColumns) {
		if (null == insertableColumns || insertableColumns.length == 0) {
			throw Exceptions.newInstance("SQL insert columns is empty: " + sql.toString());
		}
		sql.append('(');
		boolean isAppend = false;
		for (JColumnValue column : insertableColumns) {
			String obj = map.apply(column.getFieldName());
			if (Strings.isNullOrEmpty(obj)) {
				continue;
			}
			if (isAppend) {
				sql.append(',');
				sql.append(' ');
			} else {
				isAppend = true;
			}
			sql.append('`');
			sql.append(column.getColumnName());
			sql.append('`');
		}
		if (!isAppend) {
			throw Exceptions.newInstance("SQL insert columns is empty after skip null: " + sql.toString());
		}
		sql.append(')');
		//
		sql.append(" VALUES ");
		isAppend = false;
		sql.append('(');
		isAppend = false;
		for (JColumnValue column : insertableColumns) {
			String obj = map.apply(column.getFieldName());
			if (Strings.isNullOrEmpty(obj)) {
				continue;
			}
			if (isAppend) {
				sql.append(',');
				sql.append(' ');
			} else {
				isAppend = true;
			}
			sql.append('?');
			args.add(obj);
		}
		sql.append(')');
		return this;
	}

}
