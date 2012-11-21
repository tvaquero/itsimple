/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2010 Universidade de Sao Paulo
*
*
* This file is part of itSIMPLE.
*
* itSIMPLE is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version. Other licenses might be available
* upon written agreement.
*
* itSIMPLE is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with itSIMPLE.  If not, see <http://www.gnu.org/licenses/>.
*
* Authors:	Eston Santos,
*               Tiago S. Vaquero
*
**/

package util.database;

/**
 *
 * @author tiago
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;


public class DataBase implements IDataBase {



	private ResultSet rs = null;
	private PreparedStatement stmt = null;
	private Connection conn = null;
	private int lastInsertID, rowsAffected;
	private String sqlString, columnList, tableName, valueList, updateValueList,
			whereClause, orderClause, groupClause, havingClause = null;
	private ArrayList parametersList = null;
        private boolean retrieveLastID = false;

	/*
	 * Dim pColumnArray, pSQL, pTitleArray,
	 * pPKField, pFieldArray, pType, pModule, pTablePage, pTrigger,
	 * pMODnIDDetail, pPAGsPrimaryKey
	 */
	protected void Open() {
		try {
			//Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (Exception E) {
			System.err.println("Unable to load driver");
			E.printStackTrace();
		}
		try {
			//conn = DriverManager.getConnection("jdbc:db2://uspmstpc02:50000/itsimple","administrator", "administrator");
			//conn = DriverManager.getConnection("jdbc:mysql://uspmstpc02:50000/itsimple","root", "administrator");
			conn = DriverManager.getConnection("jdbc:postgresql://localhost/itsimple","postgres", "administrator");
			// TODO Read parameters from XML
                        if (!retrieveLastID){
                            stmt = conn.prepareStatement(sqlString);
                        }
                        else{
                            stmt = conn.prepareStatement(sqlString, Statement.RETURN_GENERATED_KEYS);
                        }
			
		} catch (SQLException E) {
			System.out.println("SQLExpression: " + sqlString);
			System.out.println("SQLException:  " + E.getMessage());
			System.out.println("SQLState:      " + E.getSQLState());
			System.out.println("VendorError:   " + E.getErrorCode());
		}
	}

	/* (non-Javadoc)
	 * @see IDataBase#Close()
	 */
	public void Close() {
		try {
			if (conn != null) conn.close();
		} catch (SQLException E) {
			System.out.println("SQLExpression: " + sqlString);
			System.out.println("SQLException:  " + E.getMessage());
			System.out.println("SQLState:      " + E.getSQLState());
			System.out.println("VendorError:   " + E.getErrorCode());
		}

		try {
	        if (stmt != null) stmt.close();
		} catch (SQLException E) {
			System.out.println("SQLExpression: " + sqlString);
			System.out.println("SQLException:  " + E.getMessage());
			System.out.println("SQLState:      " + E.getSQLState());
			System.out.println("VendorError:   " + E.getErrorCode());
		}
	}

	protected void GenerateSQL(char SQLType) {
		switch (SQLType) {
		case 'I':
			sqlString = "INSERT INTO " + tableName + " ( " + columnList
					+ " ) VALUES ( " + valueList + " ) ";
			// TODO Select statement
			break;
		case 'D':
			sqlString = "DELETE FROM " + tableName
					+ ((whereClause == null) ? "" : " WHERE " + whereClause);
			break;
		case 'U':
			sqlString = "UPDATE " + tableName + " SET " + updateValueList
					+ ((whereClause == null) ? "" : " WHERE " + whereClause);
			break;
		case 'S':
			sqlString = "SELECT " + columnList + " FROM " + tableName
					+ ((whereClause  == null) ? "" : " WHERE " + whereClause)
					+ ((orderClause  == null) ? "" : " ORDER BY " + orderClause)
					+ ((groupClause  == null) ? "" : " GROUP BY " + groupClause)
					+ ((havingClause == null) ? "" : " HAVING " + havingClause);
			// TODO GROUP BY, ORDER BY, HAVING
			break;
		default:
			break;
		}
	}

	/* (non-Javadoc)
	 * @see IDataBase#Select()
	 */
	public void Select() {
		try {
			GenerateSQL('S');
			Open();
			if(parametersList != null){
				prepareStatementParameters(parametersList);
			}
			rs = stmt.executeQuery();
		} catch (SQLException E) {
			System.out.println("SQLExpression: " + sqlString);
			System.out.println("SQLException:  " + E.getMessage());
			System.out.println("SQLState:      " + E.getSQLState());
			System.out.println("VendorError:   " + E.getErrorCode());
		}
	}

	/* (non-Javadoc)
	 * @see IDataBase#Insert()
	 */
	public void Insert() {
		ResultSet rsLastInsertID = null;
		try {
			GenerateSQL('I');
			Open();

			if(parametersList != null){
				prepareStatementParameters(parametersList);
			}

                        stmt.execute();

                        // Retrieve the auto generated key(s).
                        rs = stmt.getGeneratedKeys();
                        if (rs != null && rs.next() ) {
                            //get first key - id
                            lastInsertID = rs.getInt(1);
                            //System.out.print(lastInsertID);
                        }
                       
                        /*rsLastInsertID = stmt.getGeneratedKeys();
			if (rsLastInsertID != null && rsLastInsertID.last()) {
				for (int iCounter = 1; iCounter <= rsLastInsertID.getMetaData()
					.getColumnCount(); iCounter++) {
						lastInsertID = rsLastInsertID.getInt(iCounter);
					}
			}
                         */
                        
			Close();
		} catch (SQLException E) {
			System.out.println("SQLExpression: " + sqlString);
			System.out.println("SQLException:  " + E.getMessage());
			System.out.println("SQLState:      " + E.getSQLState());
			System.out.println("VendorError:   " + E.getErrorCode());
		}
	}
      


	/* (non-Javadoc)
	 * @see IDataBase#Update()
	 */
	public void Update() {
		try {
			GenerateSQL('U');
			Open();
			if(parametersList != null){
				prepareStatementParameters(parametersList);
			}
			rowsAffected = stmt.executeUpdate();
			Close();
		} catch (SQLException E) {
			System.out.println("SQLExpression: " + sqlString);
			System.out.println("SQLException:  " + E.getMessage());
			System.out.println("SQLState:      " + E.getSQLState());
			System.out.println("VendorError:   " + E.getErrorCode());
		}
	}

	/* (non-Javadoc)
	 * @see IDataBase#Delete()
	 */
	public void Delete() {
		try {
			GenerateSQL('D');
			Open();
			if(parametersList != null){
				prepareStatementParameters(parametersList);
			}
			rowsAffected = stmt.executeUpdate();
			Close();
		} catch (SQLException E) {
			System.out.println("SQLExpression: " + sqlString);
			System.out.println("SQLException:  " + E.getMessage());
			System.out.println("SQLState:      " + E.getSQLState());
			System.out.println("VendorError:   " + E.getErrorCode());
		}
	}

	private void prepareStatementParameters(ArrayList parametersList){
		int count = 1;
		if(parametersList.size() > 0){
			for(Iterator parameterIter = parametersList.iterator(); parameterIter.hasNext();){
				Object parameter = parameterIter.next();

				if (parameter instanceof File){
					InputStream inBin;
					try {
						inBin = new FileInputStream((File)parameter);
						stmt.setBinaryStream(count, inBin, (int)((File)parameter).length());
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				else if(parameter instanceof String){
					try {
						stmt.setString(count, (String)parameter);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				else if(parameter instanceof Integer){
					try {
						stmt.setInt(count, ((Integer)parameter).intValue());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

				else if(parameter instanceof Boolean){
					try {
						stmt.setBoolean(count, ((Boolean)parameter).booleanValue());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				count++;
			}
		}
	}

	/**
	 * @return Returns the columnList.
	 */
	public String getColumnList() {
		return columnList;
	}

	/**
	 * @param columnList The columnList to set.
	 */
	public void setColumnList(String columnList) {
		this.columnList = columnList;
	}

	/**
	 * @return Returns the conn.
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * @param conn The conn to set.
	 */
	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * @return Returns the groupClause.
	 */
	public String getGroupClause() {
		return groupClause;
	}

	/**
	 * @param groupClause The groupClause to set.
	 */
	public void setGroupClause(String groupClause) {
		this.groupClause = groupClause;
	}

	/**
	 * @return Returns the havingClause.
	 */
	public String getHavingClause() {
		return havingClause;
	}

	/**
	 * @param havingClause The havingClause to set.
	 */
	public void setHavingClause(String havingClause) {
		this.havingClause = havingClause;
	}

	/**
	 * @return Returns the lastInsertID.
	 */
	public int getLastInsertID() {
		return lastInsertID;
	}

	/**
	 * @param lastInsertID The lastInsertID to set.
	 */
	public void setLastInsertID(int lastInsertID) {
		this.lastInsertID = lastInsertID;
	}

	/**
	 * @return Returns the orderClause.
	 */
	public String getOrderClause() {
		return orderClause;
	}

	/**
	 * @param orderClause The orderClause to set.
	 */
	public void setOrderClause(String orderClause) {
		this.orderClause = orderClause;
	}

	/**
	 * @return Returns the rowsAffected.
	 */
	public int getRowsAffected() {
		return rowsAffected;
	}

	/**
	 * @param rowsAffected The rowsAffected to set.
	 */
	public void setRowsAffected(int rowsAffected) {
		this.rowsAffected = rowsAffected;
	}

	/**
	 * @return Returns the rs.
	 */
	public ResultSet getRs() {
		return rs;
	}

	/**
	 * @param rs The rs to set.
	 */
	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

	/**
	 * @return Returns the sqlString.
	 */
	public String getSqlString() {
		return sqlString;
	}

	/**
	 * @param sqlString The sqlString to set.
	 */
	public void setSqlString(String sqlString) {
		this.sqlString = sqlString;
	}

	/**
	 * @return Returns the stmt.
	 */
	public Statement getStmt() {
		return stmt;
	}

	/**
	 * @param stmt The stmt to set.
	 */
	public void setStmt(PreparedStatement stmt) {
		this.stmt = stmt;
	}

	/**
	 * @return Returns the tableName.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName The tableName to set.
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return Returns the updateValueList.
	 */
	public String getUpdateValueList() {
		return updateValueList;
	}

	/**
	 * @param updateValueList The updateValueList to set.
	 */
	public void setUpdateValueList(String updateValueList) {
		this.updateValueList = updateValueList;
	}

	/**
	 * @return Returns the valueList.
	 */
	public String getValueList() {
		return valueList;
	}

	/**
	 * @param valueList The valueList to set.
	 */
	public void setValueList(String valueList) {
		this.valueList = valueList;
	}

	/**
	 * @return Returns the whereClause.
	 */
	public String getWhereClause() {
		return whereClause;
	}

	/**
	 * @param whereClause The whereClause to set.
	 */
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	/**
	 * @param parametersList The parametersList to set.
	 */
	public void addToParametersList(Object parameter) {
		if(parametersList == null){
			parametersList = new ArrayList();
		}
		parametersList.add(parameter);
	}

        /**
         * Set insert process to retrieve the last inserted id
         * @param retrieveLastID
         */
        public void retrieveLastID(boolean retrieveLastID) {
            this.retrieveLastID = retrieveLastID;
        }

}
