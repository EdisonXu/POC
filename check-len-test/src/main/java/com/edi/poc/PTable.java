/**
 * 
 */
package com.edi.poc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A high abstract of DB Table.
 * @author Edison Xu
 *
 * Dec 18, 2013
 */
public class PTable implements Serializable{

	private static final long serialVersionUID = 5534447027747112692L;
	
	private String name;
	private List<Column> columns;
	private DmlType dmlType;
	private CombinedSearchCriteria combinedSearchCriteria;
	
	public enum DmlType {
		INSERT,
		UPDATE,
		DELETE;
	}
	
	public PTable () {
		this(null);
	}
	
	public PTable (String tableName) {
		this.name = tableName;
		this.columns = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public DmlType getDmlType() {
		return dmlType;
	}

	public void setDmlType(DmlType dmlType) {
		this.dmlType = dmlType;
	}
	
	public void setDmlTypeByString(String dmlType) {
		this.dmlType = DmlType.valueOf(dmlType);
	}
	
	public List<Column> getColumns() {
		return columns;
	}

	public boolean addColumn(String colName, Object colValue) {
		return this.columns.add(new Column(colName,colValue));
	}
	
	public boolean addColumn(String colName, Object colValue, boolean isRawType) {
		return this.columns.add(new Column(colName,colValue, isRawType));
	}

	public CombinedSearchCriteria getCombinedSearchCriteria() {
		return combinedSearchCriteria;
	}

	public void setCombinedSearchCriteria(
			CombinedSearchCriteria combinedSearchCriteria) {
		this.combinedSearchCriteria = combinedSearchCriteria;
	}


	/**
	 * An abstract of DB column
	 * @author Edison Xu
	 *
	 * Dec 27, 2013
	 */
	public class Column implements Serializable{
		private static final long serialVersionUID = 3573914032366786211L;
		private String columnName;
		private Object columnValue;
		private boolean rawType = false;
		
		public Column() {
			super();
		}
		
		public Column(String columnName, Object columnValue) {
			this(columnName, columnValue, false);
		}
		public Column(String columnName, Object columnValue, boolean rawType) {
			super();
			this.columnName = columnName;
			this.columnValue = columnValue;
			this.rawType = rawType;
		}

		public String getColumnName() {
			return columnName;
		}
		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}
		public Object getColumnValue() {
			return columnValue;
		}
		public void setColumnValue(Object columnValue) {
			this.columnValue = columnValue;
		}
		public boolean isRawType() {
			return rawType;
		}

		@Override
		public String toString() {
			return "Column [columnName=" + columnName + ", columnValue=" + columnValue + ", rawType=" + rawType + "]";
		}
	}
	
	/**
	 * A combination of multiple search criteria.<p>
	 * A basic <code>CombinedSearchCriteria</code> could be <p>
	 * a single <code>SearchCriteria</code>, e.g. "a=1"<p>
	 * <code>SearchCriteria sc = new SearchCriteria("a", ComparisonOperator.EQUAL, "1");</code><br>
	 * <code>CombinedSearchCriteria csc = new CombinedSearchCriteria(sc);</code>
	 * <br>Or<br>
	 * a combined one, e.g. "a=1 and b=2"<p>
	 * <code>CombinedSearchCriteria left = new CombinedSearchCriteria(new SearchCriteria("a", ComparisonOperator.EQUAL, "1"));</code><br>
	 * <code>CombinedSearchCriteria right = new CombinedSearchCriteria(new SearchCriteria("b", ComparisonOperator.EQUAL, "2"));</code><br>
	 * <code>CombinedSearchCriteria csc = new CombinedSearchCriteria(left, CombinedType.AND, right);</code><br>
	 * <p>
	 * A recursive example: "(firstname=jack and age > 12) or (firstname = marry and tall < 180)"<p>
	 * <code>CombinedSearchCriteria left = new CombinedSearchCriteria(new SearchCriteria("firstname", ComparisonOperator.EQUAL, "jack"));</code><br>
	 * <code>CombinedSearchCriteria right = new CombinedSearchCriteria(new SearchCriteria("age", ComparisonOperator.GREATER_THAN, "12"));</code><br>
	 * <p>
	 * Create the left CombinedSearchCriteria for "(firstname=jack and age > 12)"
	 * <code>CombinedSearchCriteria leftCombinedCriteria = new CombinedSearchCriteria(left, CombinedType.AND, right);</code><br>
	 * <p>
	 * <code>CombinedSearchCriteria left2 = new CombinedSearchCriteria(new SearchCriteria("firstname", ComparisonOperator.EQUAL, "marry"));</code><br>
	 * <code>CombinedSearchCriteria right2 = new CombinedSearchCriteria(new SearchCriteria("tall", ComparisonOperator.LESS_THAN, "180"));</code><br>
	 * <p>
	 * Create the right CombinedSearchCriteria for "(firstname = marry and tall < 180)"
	 * <code>CombinedSearchCriteria rightCombinedCriteria = new CombinedSearchCriteria(left2, CombinedType.AND, right2);</code><br>
	 * <p>
	 * Create the top level CombinedSearchCriteria for "() <strong>or</strong> ()" <br>
	 * <code>CombinedSearchCriteria csc = new CombinedSearchCriteria(leftCombinedCriteria, CombinedType.OR, rightCombinedCriteria);</code><br>
	 * 
	 * @author Edison Xu
	 *
	 * Dec 27, 2013
	 */
	public static class CombinedSearchCriteria implements Serializable
	{
		private static final long serialVersionUID = 4703760975024573300L;
		private SearchCriteria searchCriteria;
		private List<CombinedSearchCriteria> andList;
		private List<CombinedSearchCriteria> orList;
	
		public static enum CombinedType{
			OR,AND;
		}
		
		public CombinedSearchCriteria() {
			this(null);
		}
		
		public CombinedSearchCriteria(CombinedSearchCriteria left, CombinedType type, CombinedSearchCriteria right) {
			if(left==null || right==null)
			{
				throw new NullPointerException();
			}
			switch(type)
			{
			case OR:
				this.orList = new ArrayList<CombinedSearchCriteria>();
				this.andList = new ArrayList<CombinedSearchCriteria>();
				this.orList.add(left);
				this.orList.add(right);
				break;
			case AND:
				this.andList = new ArrayList<CombinedSearchCriteria>();
				this.orList = new ArrayList<CombinedSearchCriteria>();
				this.andList.add(left);
				this.andList.add(right);
				break;
			default:
				throw new IllegalArgumentException("Invalid CombinedType!");
			}
		}
		
		public CombinedSearchCriteria(SearchCriteria searchCriteria) {
			super();
			this.searchCriteria = searchCriteria;
			this.andList = new ArrayList<>();
			this.orList = new ArrayList<>();
		}
		
		public void setSearchCriteria(SearchCriteria searchCriteria) {
			this.searchCriteria = searchCriteria;
		}

		public SearchCriteria getSearchCriteria() {
			return searchCriteria;
		}
		public List<CombinedSearchCriteria> getAndList() {
			return andList;
		}
		public List<CombinedSearchCriteria> getOrList() {
			return orList;
		}

		@Override
		public String toString() {
			return "CombinedSearchCriteria [searchCriteria=" + searchCriteria + ", andList=" + andList + ", orList="
					+ orList + "]";
		}
	}
	
	/**
	 * An abstract of search criteria for query operation.<p>
	 * For example, a typical search criteria "a = b" will be
	 * <PRE><code>new SearchCriteria("a", ComparisonOperator.EQUAL, "b")</code></PRE>
	 * @author Edison Xu
	 *
	 * Dec 27, 2013
	 */
	public static class SearchCriteria implements Serializable
	{
		public enum ComparisonOperator {
			EQUAL("="),
			GREATER_THAN(">"),
			LESS_THAN("<"), 
			NOT_GREATER_THAN("<="),
			NOT_LESS_THAN(">="),
			NOT_EQUAL("!=");
			
			private String symbol;
			private ComparisonOperator(String symbol) {
				this.symbol = symbol;
			}
			public String getSymbol() {
				return symbol;
			}
		}
		
		private static final long serialVersionUID = -1903586217325722915L;
		
		private String key;
		private ComparisonOperator compOperator;
		private String value;
		
		public SearchCriteria() {
			super();
		}
		
		public SearchCriteria(String key, ComparisonOperator compOperator, String value) {
			super();
			this.key = key;
			this.compOperator = compOperator;
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public ComparisonOperator getCompOperator() {
			return compOperator;
		}
		public void setCompOperator(ComparisonOperator compOperator) {
			this.compOperator = compOperator;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "SearchCriteria [key=" + key + " " + compOperator + " " + value + "]";
		}
		
	}

	@Override
	public String toString() {
		return "PTable [name=" + name + ", columns=" + columns + ", dmlType=" + dmlType + ", combinedSearchCriteria="
				+ combinedSearchCriteria + "]";
	}
	
}
