package com.guoquan.store.operation.log.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;

import java.util.*;

/**
 * @Description SQL解析工具类
 * @Date 2021/7/22 10:36 
 * @Author wangLuLu
 * @Version 1.0
 */

public class OpeSqlParserUtils {

	/**
	 * 解析insert sql语句
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static Map<String,Object> parserInsertSql(String sql) throws JSQLParserException {
		Map<String,Object> map = new HashMap<>();
		String tableName = OpeSqlParserUtils.parserInserSqlTable(sql);
		List<String> columnList = parserInserSqlColumn(sql);
		List<String> valuesList = parserInserSqlValues(sql);
		map.put("tableName", Arrays.asList(tableName));
		map.put("column",columnList);
		map.put("values",valuesList);
		return map;
	}

	/**
	 * 解析update sql语句
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static Map<String,Object> parserUpdateSql(String sql) throws JSQLParserException {
		Map<String,Object> map = new HashMap<>();
		List<String> tableList = parserUpdateSqlTable(sql);
		List<String> columnList = parserUpdateSqlColumn(sql);
		List<String> valuesList = parserUpdateSqlValues(sql);
		String condition = parserUpdateSqlTiaojian(sql);
		map.put("tableName",tableList);
		map.put("column",columnList);
		map.put("values",valuesList);
		map.put("condition",condition);
		return map;
	}

	/**
	 * 解析insert sql的表名
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static String parserInserSqlTable(String sql) throws JSQLParserException {
		Statement statement = CCJSqlParserUtil.parse(sql);
		Insert insertStatement = (Insert) statement;
		return insertStatement.getTable().getName();
	}

	/**
	 * 解析insert sql的列明
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static List<String> parserInserSqlColumn(String sql) throws JSQLParserException {
		Statement statement = CCJSqlParserUtil.parse(sql);
		Insert insertStatement = (Insert) statement;
		List<Column> columnList = insertStatement.getColumns();
		List<String> columnNameList = new ArrayList<>();
		for (int i = 0; i < columnList.size(); i++) {
			columnNameList.add(columnList.get(i).toString());
		}
		return columnNameList;
	}

	/**
	 * 解析insert sql对应的数值
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static List<String> parserInserSqlValues(String sql) throws JSQLParserException {
		Statement statement = CCJSqlParserUtil.parse(sql);
		Insert insertStatement = (Insert) statement;
		List<Expression> insert_values_expression = ((ExpressionList) insertStatement
				.getItemsList()).getExpressions();
		List<String> str_values = new ArrayList<String>();
		for (int i = 0; i < insert_values_expression.size(); i++) {
			str_values.add(insert_values_expression.get(i).toString());
		}
		return str_values;
	}

	/**
	 * 解析sql获得table名称
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static List<String> parserUpdateSqlTable(String sql) throws JSQLParserException {
		Statement statement = CCJSqlParserUtil.parse(sql);
		Update updateStatement = (Update) statement;
		List<Table> tableList = updateStatement.getTables();
		List<String> tableNameList = new ArrayList<>();
		if (tableList != null) {
			for (int i = 0; i < tableList.size(); i++) {
				tableNameList.add(tableList.get(i).toString());
			}
		}
		return tableNameList;
 
	}

	/**
	 * 解析sql操作的column
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static List<String> parserUpdateSqlColumn(String sql) throws JSQLParserException {
		Statement statement = CCJSqlParserUtil.parse(sql);
		Update updateStatement = (Update) statement;
		List<Column> columnList = updateStatement.getColumns();
		List<String> columnNameList = new ArrayList<String>();
		if (columnList != null) {
			for (int i = 0; i < columnList.size(); i++) {
				columnNameList.add(columnList.get(i).toString());
			}
		}
		return columnNameList;
 
	}

	/**
	 * 解析sql字段的数值
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static List<String> parserUpdateSqlValues(String sql) throws JSQLParserException {
		Statement statement = CCJSqlParserUtil.parse(sql);
		Update updateStatement = (Update) statement;
		List<Expression> updateValues = updateStatement.getExpressions();
		List<String> valuesList = new ArrayList<>();
		if (updateValues != null) {
			for (int i = 0; i < updateValues.size(); i++) {
				valuesList.add(updateValues.get(i).toString());
			}
		}
		return valuesList;
 
	}

	/**
	 * 解析update语句的where条件
	 * @param sql
	 * @return
	 * @throws JSQLParserException
	 */
	public static String parserUpdateSqlTiaojian(String sql) throws JSQLParserException {
		Statement statement = CCJSqlParserUtil.parse(sql);
		Update updateStatement = (Update) statement;
		Expression whereExpression = updateStatement.getWhere();
		return whereExpression.toString();
	}

}