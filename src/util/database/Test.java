/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package src.util.database;

/**
 *
 * @author tiago
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jdom.Element;



public class Test {

	/**
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {

            DataBase eSelectType = new DataBase();
            System.out.println("ok");

                eSelectType.setColumnList("domain"); //please, don't use *
		eSelectType.setTableName("plan"); //
                eSelectType.setWhereClause(null);
		//eSelectType.setWhereClause("name = ?"); //where clause, null if not applicable
		eSelectType.setOrderClause(null); //order by clause, null if not applicable
		eSelectType.setGroupClause(null); //group by clause, null if not applicable
		eSelectType.setHavingClause(null); //having clause, null if not applicable
		//eSelectType.addToParametersList(name);
		eSelectType.Select();

                while (eSelectType.getRs().next()) {
			System.out.println(eSelectType.getRs().getString("name").trim());
		}
		eSelectType.Close();
            


            /*

//		File xmlFile = new File(path);
		String name = "teste";
		//ArrayList list = new ArrayList();
		//list.add("teste");
		//list.add(xmlFile);
		DataBase eSelectType = new DataBase();

		eSelectType.setColumnList("itssversion"); //please, don't use *
		eSelectType.setTableName("itsimple"); //
		eSelectType.setWhereClause("itssname = ?"); //where clause, null if not applicable
		eSelectType.setOrderClause(null); //order by clause, null if not applicable
		eSelectType.setGroupClause(null); //group by clause, null if not applicable
		eSelectType.setHavingClause(null); //having clause, null if not applicable
		eSelectType.addToParametersList(name);
		eSelectType.Select();

		double maxVersion = 1.0;
		try {
			 while (eSelectType.getRs().next()) {
				String version = eSelectType.getRs().getString("itssversion");
				double currentVersion = Double.parseDouble(version);
					if(maxVersion < currentVersion){
						maxVersion = currentVersion;
					}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		eSelectType.Close();
		System.out.println(Double.toString(maxVersion+0.1));

             */


	/*	String itssname = "teste";
		String itsversion = "1.0";
		File xmlFile = new File("examples/ElevatorDomainv1.xml");
		ArrayList list = new ArrayList();
		list.add(itssname);
		//list.add(itsversion);
		list.add(xmlFile);*/

		/*// eInsert template begin, generated at 01/06/2006 19:22:11, by esantos
		// 	Don't use the reserved words (INTO, VALUES, )
		DataBase eInsertType = new DataBase();
		eInsertType.setTableName("itsimple");
		eInsertType.setColumnList("itssname, itssversion, itsxmodel"); // please, don't use ()
		eInsertType.setValueList("?, ?, ?"); // please, don't use ()
		eInsertType.setParametersList(list);
		eInsertType.Insert();
		System.out.println("Last inserted ID: " + eInsertType.getLastInsertID());
		// eInsert template end
*/

	/*	// eUpdate template begin, generated at 01/06/2006 19:24:45, by esantos
		// 	Don't use the reserved words (SET, )
		DataBase eUpdate = new DataBase();
		eUpdate.setTableName("e_type");
		eUpdate.setUpdateValueList("TYPsDescription = TYPsDescription, TYPsValue = '" + eInsertType.getLastInsertID() + "'");
		eUpdate.setWhereClause("TYPnID = " + eInsertType.getLastInsertID()); //allways use WHERE
		eUpdate.Update();
		System.out.println("Rows affected on eUpdate: " + eUpdate.getRowsAffected());
		// eUpdate template end
*/
		// eSelect template begin
		//	Don't use the reserved words (FROM, WHERE, ORDER BY, GROUP BY, HAVING, )
		/*DataBase eSelectType = new DataBase();
		eSelectType.setColumnList("*"); //please, don't use *
		eSelectType.setTableName("itsimple"); //
		eSelectType.setWhereClause(" itssname = ? AND itsxmodel = ?"); //where clause, null if not applicable
		eSelectType.setOrderClause(null); //order by clause, null if not applicable
		eSelectType.setGroupClause(null); //group by clause, null if not applicable
		eSelectType.setHavingClause(null); //having clause, null if not applicable
		eSelectType.setParametersList(list);
		eSelectType.Select();
		while (eSelectType.getRs().next()) {
			System.out.print(eSelectType.getRs().getString("itssversion"));
			String xml = eSelectType.getRs().getString("itsxmodel");
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("examples/XMLDOC.xml"));
				out.write(xml);
				System.out.println("OK!!");
				out.close();
				} catch (IOException e) {
					System.out.println("NAO!!");
				}

			for (int iCounter = 1; iCounter <= eSelectType.getRs().getMetaData()
					.getColumnCount(); iCounter++) {
				System.out.print(eSelectType.getRs().getString(iCounter) + "|");
			}
			System.out.println("");
		}
		eSelectType.Close();
		// eSelect template end
		*/
	/*	// eDelete template begin, generated at 01/06/2006 20:28:22, by esantos
		// 	Don't use the reserved words (FROM, WHERE, )
		DataBase eDeleteType2 = new DataBase();
		eDeleteType2.setTableName("e_type");
		eDeleteType2.setWhereClause("TYPnID BETWEEN " + (eInsertType.getLastInsertID() - 5) +
			" AND " + (eInsertType.getLastInsertID() - 2)); //allways use WHERE
		eDeleteType2.Delete();
		System.out.println("Rows affected: " + eDeleteType2.getRowsAffected());
		// eDelete template end

		eSelectType.Select();
		while (eSelectType.getRs().next()) {
			for (int iCounter = 1; iCounter <= eSelectType.getRs().getMetaData()
					.getColumnCount(); iCounter++) {
				System.out.print(eSelectType.getRs().getString(iCounter) + "|");
			}
			System.out.println("");
		}
		eSelectType.Close();*/
	}



}
