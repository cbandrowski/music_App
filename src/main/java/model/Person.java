package model;

import datab.DataBase;

public class Person extends DataBase {

    private String DB_URL;

    private String USERNAME;

    private String PASSWORD;

//    public String getUserFullName(String email) {
//        String getUserSQL = "SELECT first_name, last_name FROM users WHERE email = ?";
//        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(getUserSQL)) {
//
//            pstmt.setString(1, email);
//
//            ResultSet resultSet = pstmt.executeQuery();
//            if (resultSet.next()) {
//                String firstName = resultSet.getString("first_name");
//                String lastName = resultSet.getString("last_name");
//                return firstName + " " + lastName;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null; // Return null if no user is found
//    }

}
