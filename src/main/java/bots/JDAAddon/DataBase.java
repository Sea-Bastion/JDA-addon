package bots.JDAAddon;


import java.nio.ByteBuffer;
import java.sql.*;

class DataBase {

	private Connection DataBase;

	DataBase(String DataBasePath) throws SQLException {

		try {
			Class.forName("org.sqlite.JDBC");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		DataBase = DriverManager.getConnection("jdbc:sqlite:" + DataBasePath);
	}

	String GetValueBlob(String key){
		String sql = "select value from ItemTable where key == \""+ key +"\"";
		try (PreparedStatement statement = DataBase.prepareStatement(sql)) {


			ResultSet result = statement.executeQuery();
			result.next();
			byte[] ValueRaw = result.getBytes(1);

			ByteBuffer Value = ByteBuffer.allocate((ValueRaw.length/2));
			for (int i = 0; i < ValueRaw.length; i+=2){
				Value.put(ValueRaw[i]);
			}

			return new String(Value.array());

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("could not retrieve token");
			return null;
		}

	}

}
