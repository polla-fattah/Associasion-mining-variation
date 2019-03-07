
import java.util.*;
import java.io.*;
import java.sql.*;
import javax.swing.JOptionPane;

public class RandomData
{
	private static Connection con;
	private static Statement stmt;

	public static void connect(){

 		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection ("jdbc:mysql://localhost/supermarket2?user=root&password=sanar");
			stmt = con.createStatement ();
		}
		catch(SQLException sqlEx ){
			sqlEx.printStackTrace ();
			JOptionPane.showMessageDialog (null,"Problem Occurs while trying to connect and open FrequencyBase");
			System.exit (1);
		}
		catch(Exception ex){
			ex.printStackTrace ();
			JOptionPane.showMessageDialog (null,"System Error Database Driver Missing !");
			System.exit (1);
		}
	}

	public static int random(int max){
		return ((int)(Math.random() * max));
	}

	public static void main(String str[])throws Exception{
		connect();
		StringBuffer buf = new StringBuffer("");
		int loop;
		stmt.executeUpdate("delete from transaction");
		try{
			for(int i = 1; i <= 600; i++){
				loop = random(20);
				for(int j = 0; j < loop; j++)
					buf.append("("+i+",'item"+random(200)+"'),");
				if(i % 100 ==0){
					buf.deleteCharAt(buf.lastIndexOf(","));

						System.out.println(i / 100);
						stmt.executeUpdate("Insert IGNORE into transaction values "+buf+";");

					buf = new StringBuffer("");
				}
			}
		}
		catch(SQLException e){e.printStackTrace();}
		finally{
			stmt.close();
			con.close();
		}
	}
}

