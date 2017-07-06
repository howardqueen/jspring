package com.jspring.persistence.sql.mysql;

import com.jspring.data.JEntity;
import com.jspring.persistence.sql.ICondition;
import com.jspring.persistence.sql.ISqlQueryBuilder;
import com.jspring.persistence.sql.IWhere;
import com.jspring.persistence.sql.IWhereable;

class MysqlQuery<E extends Enum<?>> implements IWhereable<E>, ISqlQueryBuilder {

	//////////////////
	/// FIELDS
	//////////////////
	private final JEntity<?> entityInfo;

	public MysqlQuery(JEntity<?> entityInfo) {
		this.entityInfo = entityInfo;
	}

	//////////////////
	/// WHERE
	//////////////////

	private StringBuilder wheres = null;

	@Override
	public ICondition<IWhere<E>> where(E column) {
		if (null == wheres) {
			wheres = new StringBuilder(" WHERE ");
		} else {
			wheres.append(" AND ");
		}
		SqlWriter writer = new SqlWriter(entityInfo, wheres);
		return new Condition<>(//
				new WhereMore<>(this, writer)//
				, writer, column);
	}

	//////////////////
	/// GROUP BY
	//////////////////
	@Override
	public void groupBy(E[] columns) {
	}

	//////////////////
	/// ORDER BY
	//////////////////
	private StringBuilder orders;

	@Override
	public Ordering<E> orderBy(E column) {
		if (null == orders) {
			orders = new StringBuilder(" ORDER BY ");
		} else {
			orders.append(',');
		}
		SqlWriter writer = new SqlWriter(entityInfo, orders);
		return new Ordering<>(//
				new OrderAnd<>(this, writer)//
				, writer, column);
	}

	//////////////////
	/// LIMIT
	//////////////////
	private int limitPage = 0, limitSize = 0;

	@Override
	public MysqlQuery<E> limit(int page, int size) {
		limitPage = page < 1 ? 1 : page;
		limitSize = size < 1 ? 1 : size;
		return this;
	}

	//////////////////
	/// SQL BUILDER
	//////////////////
	@Override
	public ISqlQueryBuilder commit() {
		return this;
	}

	@Override
	public String getSqlSelectEntities() {
		StringBuilder sql = new StringBuilder("SELECT ");
		String[] cs = entityInfo.getRetrieveableColumns();
		sql.append(cs[0]);
		for (int i = 1; i < cs.length; i++) {
			sql.append(',');
			sql.append(cs[i]);
		}
		sql.append(" FROM ");
		sql.append(entityInfo.getSqlTableExpression());
		if (null != wheres) {
			sql.append(wheres);
		}
		if (null != orders) {
			sql.append(orders);
		}
		if (limitPage > 0 && limitSize > 0) {
			sql.append(" LIMIT ");
			sql.append((limitPage - 1) * limitSize);
			sql.append(',');
			sql.append(limitSize);
		}
		return sql.toString();
	}

	@Override
	public String getSqlSelectEntity() {
		StringBuilder sql = new StringBuilder("SELECT ");
		String[] cs = entityInfo.getRetrieveableColumns();
		sql.append(cs[0]);
		for (int i = 1; i < cs.length; i++) {
			sql.append(',');
			sql.append(cs[i]);
		}
		sql.append(" FROM ");
		sql.append(entityInfo.getSqlTableExpression());
		if (null != wheres) {
			sql.append(wheres);
		}
		if (null != orders) {
			sql.append(orders);
		}
		sql.append(" LIMIT 1 ");
		return sql.toString();
	}

	@Override
	public String getSqlSelectPojos(String[] colExp) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(colExp[0]);
		for (int i = 1; i < colExp.length; i++) {
			sql.append(',');
			sql.append(colExp[i]);
		}
		sql.append(" FROM ");
		sql.append(entityInfo.getSqlTableExpression());
		if (null != wheres) {
			sql.append(wheres);
		}
		if (null != orders) {
			sql.append(orders);
		}
		if (limitPage > 0 && limitSize > 0) {
			sql.append(" LIMIT ");
			sql.append((limitPage - 1) * limitSize);
			sql.append(',');
			sql.append(limitSize);
		}
		return sql.toString();
	}

	@Override
	public String getSqlSelectPojo(String[] colExp) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(colExp[0]);
		for (int i = 1; i < colExp.length; i++) {
			sql.append(',');
			sql.append(colExp[i]);
		}
		sql.append(" FROM ");
		sql.append(entityInfo.getSqlTableExpression());
		if (null != wheres) {
			sql.append(wheres);
		}
		if (null != orders) {
			sql.append(orders);
		}
		sql.append(" LIMIT 1 ");
		return sql.toString();
	}

	@Override
	public String getSqlSelectObjects(String colExp) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(colExp);
		sql.append(" FROM ");
		sql.append(entityInfo.getSqlTableExpression());
		if (null != wheres) {
			sql.append(wheres);
		}
		if (null != orders) {
			sql.append(orders);
		}
		if (limitPage > 0 && limitSize > 0) {
			sql.append(" LIMIT ");
			sql.append((limitPage - 1) * limitSize);
			sql.append(',');
			sql.append(limitSize);
		}
		return sql.toString();
	}

	@Override
	public String getSqlSelectScalar(String colExp) {
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(colExp);
		sql.append(" FROM ");
		sql.append(entityInfo.getSqlTableExpression());
		if (null != wheres) {
			sql.append(wheres);
		}
		// if (null != orders) {
		// sql.append(orders);
		// }
		sql.append(" LIMIT 1 ");
		return sql.toString();
	}

	//////////////////
	/// JOIN
	//////////////////
	// // private List<Select<?, ?>> joins = null;
	// //
	// // public <TR, ER extends Enum<?>> JoinBegin<T, E, TR, ER>
	// // join(Select<TR, ER> right) {
	// // if (null == joins) {
	// // joins = new ArrayList<>();
	// // }
	// // return null;
	// // }
	//
	// //////////////////
	// /// UNION
	// //////////////////
	// // private List<Select<?, ?>> unions = null;
	// //
	// // public <TN, EN extends Enum<?>> Select<T, E> union(Select<TN, EN>
	// // next) {
	// // if (null == unions) {
	// // unions = new ArrayList<>();
	// // }
	// // return this;
	// // }

	////////////////
	// JOIN
	////////////////
	// public static class JoinBegin<TL, EL extends Enum<?>, TR, ER extends
	// Enum<?>> {
	//
	// Condition<JoinEnd<TL, EL, TR, ER>, ER> on(EL leftColumn) {
	// return null;
	// }
	//
	// }
	//
	// public static class JoinEnd<TL, EL extends Enum<?>, TR, ER extends
	// Enum<?>> {
	//
	// Condition<JoinEnd<TL, EL, TR, ER>, ER> and(EL leftColumn) {
	// return null;
	// }
	//
	// Condition<JoinEnd<TL, EL, TR, ER>, ER> or(EL leftColumn) {
	// return null;
	// }
	//
	// SqlBuilderQuery<EL> endJoin() {
	// return null;
	// }
	//
	// }

}