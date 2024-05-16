package com.example.DataBase;

import com.example.DataBase.data.AgencyData;
import com.example.DataBase.data.BlogsData;
import com.example.DataBase.data.HealthExpertData;
import com.example.DataBase.data.Polls;
import com.example.DataBase.data.PostData;
import com.example.DataBase.data.UserData;
import com.example.DataBase.data.currentLoginData;
import com.example.HarIPart.unsafeCoordsFromDB;
import com.example.HarIPart.unsafeRequestsFromDB;
import com.example.MouawiaPart.fragment_home;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class sqlConnector {

    final String user = "admin";
    final String pass = "admin";
    private Connection con;

    public sqlConnector() throws SQLException {

        //issue now seems to be this not running on another thread hmm...
        try {
            //Dirty method, but works.
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
            //The above method is not needed when Thread and Looper stuff is used together.

            //Actual Connection Code
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://<Ip-addressOfCloudServer>:3306/ladycompaniondb";
            this.con = DriverManager.getConnection(connectionString, user, pass);

        } catch (LinkageError | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    //Index Dictionary for retrieveUserDetails
    // 0: email
    // 1: name
    // 2: idnum
    // 3: homeaddress
    // 4: phonenumber
    // 5: age
    // 6: profilepic
    // 7:dateofjoin
    public String[] retrieveRegularUserDetails(int id) throws SQLException {
        String[] returnarray = new String[8];
        String query = "select email, profilepic, name, idnum, homeaddress, phonenumber, age, dateofjoin from regularUser where id = "+id;
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            returnarray[0] = values.getString("email");
            returnarray[1] = values.getString("name");
            returnarray[2] = values.getString("idnum");
            returnarray[3] = values.getString("homeaddress");
            returnarray[4] = values.getString("phonenumber");
            returnarray[5] = values.getString("age");
            returnarray[6] = values.getString("profilepic");
            returnarray[7] = values.getString("dateofjoin");
        }
        return returnarray;
    }

    //Index Dictionary for retrieveAgencyUserDetails:
    // 0: companyType
    // 1: companyName
    // 2: phonenumber
    // 3: email
    // 4: address
    public String[] retrieveAgencyUserDetails(int id) throws SQLException {
        String[] returnarray = new String[7];
        String query = "select profilepic, companyType, companyName, phonenumber, email, address, dateofjoin from agencyUser where id = "+id;
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            returnarray[0] = values.getString("companyType");
            returnarray[1] = values.getString("companyName");
            returnarray[2] = values.getString("phonenumber");
            returnarray[3] = values.getString("email");
            returnarray[4] = values.getString("address");
            returnarray[5] = values.getString("profilepic");
            returnarray[6] = values.getString("dateofjoin");
        }
        return returnarray;
    }

    public int getRegularUserIDfromEmail(String email) throws SQLException {
        String query = "select id from regularUser where email = \""+email+"\"";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        int returnval=-1;
        while (values.next()) {
            returnval = values.getInt("id");
        }
        return returnval;
    }

    public int getAgencyUserIDfromEmail(String email) throws SQLException {
        String query = "select id from agencyUser where email = \'"+email+"\'";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        int returnval=-1;
        while (values.next()) {
            returnval = values.getInt("id");
        }
        return returnval;
    }

    //The logic is as follows:
    // - Returns 0: Regular User
    // - Returns 1: AgencyUser
    public int findUserType(String email) throws SQLException {
        //checking regular:
        String query = "select id from regularUser where email = \'"+email+"\'";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        String result = null;
        while (values.next()) {
            result = values.getString("id");
        }
        if (result == null) {
            return 1;
        }
        else {
            return 0; //Under the assumption that all email ids are unique across both tables
        }
    }

    public boolean checkRegularUserPassword(String email, String password) throws SQLException {
        int id = this.getRegularUserIDfromEmail(email);
        String query = "select password from regularUser where id = "+id;
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        String pwd="";
        while (values.next()) {
            pwd = values.getString("password");
        }
        return password.equals(pwd);
    }

    public boolean checkAgencyUserPassword(String email, String password) throws SQLException {
        int id = this.getAgencyUserIDfromEmail(email);
        String query = "select password from agencyUser where id = "+id;
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        String pwd="";
        while (values.next()) {
            pwd = values.getString("password");
        }
        return password.equals(pwd);
    }

    //Retreive Table information
    public ArrayList<UserData> retrieveUserInformation() throws SQLException {
        ArrayList<UserData> returnArray = new ArrayList<UserData>();
        String query = "select * from regularUser";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            //public UserData(int postId, String postTitle, String postBody, int likes, int dateOfPost, int userId, int userType)
            returnArray.add(new UserData(values.getInt("id"),
                                         values.getString("profilepic"),
                                       values.getString("name"),
                                         values.getString("email"),
                                         values.getString("password"),
                                         values.getString("idnum"),
                                         values.getString("homeaddress"),
                                        values.getString("phonenumber"),
                                         values.getInt("age"),
                                        values.getString("dateofjoin")));
        }
        return returnArray;
    }

    public ArrayList<AgencyData> retrieveAgencyInformation() throws SQLException {
        ArrayList<AgencyData> returnArray = new ArrayList<AgencyData>();
        String query = "select * from agencyUser";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            //public AgencyData(int postId, String postTitle, String postBody, int likes, int dateOfPost, int userId, int userType)
            returnArray.add(new AgencyData(values.getInt("id"),
                    values.getString("profilepic"),
                    values.getString("companyType"),
                    values.getString("companyName"),
                    values.getString("phonenumber"),
                    values.getString("email"),
                    values.getString("address"),
                    values.getString("password"),
                    values.getString("dateofjoin")));
        }
        return returnArray;
    }

    public ArrayList<PostData> retrievePostInformation() throws SQLException {
        ArrayList<PostData> returnArray = new ArrayList<PostData>();
        String query = "select * from post";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            //public PostData(int postId, String postTitle, String postBody, int likes, int dateOfPost, int userId, int userType)
            returnArray.add(new PostData(values.getInt("postid"),
                    values.getString("posttitle"),
                    values.getString("postbody"),
                    values.getInt("likes"),
                    values.getString("dateofPost"),
                    values.getInt("userid"),
                    values.getInt("usertype")));
        }
        return returnArray;
    }

    public ArrayList<BlogsData> retrieveBlogInformation() throws SQLException {
        ArrayList<BlogsData> returnArray = new ArrayList<BlogsData>();
        String query = "select * from blog";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            //public BlogsData(int postId, String postTitle, String postBody, int likes, int dateOfPost, int userId, int userType)
            returnArray.add(new BlogsData(values.getInt("blogid"),
                    values.getString("blogtitle"),
                    values.getString("blogbody"),
                    values.getInt("likes"),
                    values.getString("dateofPost"),
                    values.getInt("agencyid"),
                    values.getString("agencytype"),
                    values.getString("link")));
        }
        return returnArray;
    }

    public void updateSpecificColumnUser(String newValue, String column) throws SQLException {
        int usertype = currentLoginData.userType;
        if (usertype == 0) {
            String query = "update regularUser set "+column+" = '"+newValue+"' where id = "+currentLoginData.getId();
            Statement statement = con.createStatement();
            statement.executeUpdate(query);
            if(column.equals("profilepic"))
                currentLoginData.user.setProfilePic(newValue);
        }
        else if (usertype == 1) {
            String query = "update agencyUser set "+column+" = '"+newValue+"' where id = "+currentLoginData.getId();
            Statement statement = con.createStatement();
            statement.executeUpdate(query);

            if(column.equals("profilepic"))
                currentLoginData.agency.setProfilePic(newValue);
        }
    }

    public void updateSpecificColumnPost(int postId, int newValue, String column, String table) throws SQLException {
        String query;
        if(table.equals("post"))
            query = "update "+table+" set "+ column +" = "+ "'" + newValue + "'" + " where postid = "+ postId; //same Id
        else
            query = "update "+table+" set "+ column +" = "+ "'" + newValue + "'" + " where blogid = "+ postId; //same Id
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public void updateSpecificColumnPost(int postId, String newValue, String column) throws SQLException {
        String query = "update post " +
                "set "+ column +" = "+ newValue +
                "from ladycompaniondb.post"  //from table
                +"where postid = "+ postId; //same Id
        System.out.println(query);
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public void editRegularUser(String fullName, String profilePic, String idNumber, String homeAddress, String phoneNumber, String age) throws SQLException {

        String query = "update regularUser set profilepic = '"+profilePic+"', name = '"+fullName+"', idnum = '"+idNumber+"', homeaddress = '"+homeAddress+"', phonenumber = '"+phoneNumber+"', age = '"+age+"' where id = "+currentLoginData.getId();
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public void editAgencyUser(String profilepic, String companyType, String companyName, String phonenumber, String address) throws SQLException {
        String query = "update agencyUser set profilepic = '"+profilepic+"', companyType = '"+companyType+"', companyName = '"+companyName+"', phonenumber = '"+phonenumber+"', address = '"+address+"' where id = "+currentLoginData.getId();
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public void signUpRegularUser(String fullName, String email, String profilepic, String idNumber, String homeAddress, String phoneNumber, String password, String age, String dateofjoin) throws SQLException {
        int id = this.giveARegularUserID();
        String query = "insert into regularUser values ('"+id+"', '"+email+"', '"+password+"','"+profilepic+"', '"+fullName+"', '"+idNumber+"', '"+homeAddress+"', '"+phoneNumber+"', '"+age+"', '"+dateofjoin+"')";
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    // In sqlConnector.java
    public void signUpAgencyUser(String companyType, String profilepic, String companyName, String phonenumber, String email, String address, String hashedPassword) throws SQLException{
        int id = this.giveAAgencyUserID();
        // Ensure the query matches your table schema
        String query = "INSERT INTO agencyUser (id, profilepic, companyType, companyName, phonenumber, email, address, password, dateofjoin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.setString(2, profilepic);
            ps.setString(3, companyType);
            ps.setString(4, companyName);
            ps.setString(5, phonenumber);
            ps.setString(6, email);
            ps.setString(7, address);
            ps.setString(8, hashedPassword);
            ps.setString(9, java.time.LocalDate.now().toString());
            ps.executeUpdate();
        }
    }


    // added create new post function
    public  void createNewPost(String posttitle, String postbody, int likes, String dateofpost, int userid, int usertype) throws SQLException{
        int postId;
        String query="";

        if(usertype == 0) {
             postId = fragment_home.posts.size() + 1;
             query = "insert into post values ('" + postId + "', '" + posttitle + "', '" + postbody + "', '" + likes + "', '" + dateofpost + "', '" + userid + "', '" + usertype + "')";
            fragment_home.posts.add(new PostData(postId, posttitle, postbody, likes, dateofpost.toString(), userid, usertype));
       } //else //{
//            postId = fragment_home.blogs.size() + 1;
//            query = "insert into blog values ('" + postId + "', '" + posttitle + "', '" + postbody + "', '" + likes + "', '" + dateofpost + "', '" + userid + "', '" + currentLoginData.getAgency().getCompanyType() + "', link = '"+link+"' )";
//            fragment_home.blogs.add(new BlogsData(postId, posttitle, postbody, likes, dateofpost.toString(), userid, currentLoginData.getAgency().getCompanyType()));
//        }

        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    // added create new post function |For blogs with Link
    public  void createNewPost(String posttitle, String postbody, int likes, String dateofpost, int userid, int usertype, String link) throws SQLException{
        int postId;
        String query;

        if(usertype == 0) {
            postId = fragment_home.posts.size() + 1;
            query = "insert into post values ('" + postId + "', '" + posttitle + "', '" + postbody + "', '" + likes + "', '" + dateofpost + "', '" + userid + "', '" + usertype + "')";
            fragment_home.posts.add(new PostData(postId, posttitle, postbody, likes, dateofpost.toString(), userid, usertype));
        } else {
            postId = fragment_home.blogs.size() + 1;
            query = "insert into blog values ('" + postId + "', '" + posttitle + "', '" + postbody + "', '" + likes + "', '" + dateofpost + "', '" + userid + "', '" + currentLoginData.getAgency().getCompanyType() + "', '"+link+"' )";
            fragment_home.blogs.add(new BlogsData(postId, posttitle, postbody, likes, dateofpost.toString(), userid, currentLoginData.getAgency().getCompanyType(), link));
        }

        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public  void addHealthExpert(String name, int age, String position, String bio, String email, String phonenumber) throws SQLException{
        int doctorId = givedoctorID();
        String query = "insert into doctor_info values ('" + doctorId + "', '"+ name + "', '" + age + "', '" + position + "', '" + bio + "', '" + email + "', '" + phonenumber + "')";

        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public ArrayList<HealthExpertData> retrieveExpertInformation() throws SQLException {
        ArrayList<HealthExpertData> returnArray = new ArrayList<HealthExpertData>();
        String query = "select * from doctor_info";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            returnArray.add(new HealthExpertData(values.getInt("doctorid"),
                    values.getString("name"),
                    values.getInt("age"),
                    values.getString("position"),
                    values.getString("bio"),
                    values.getString("email"),
                    values.getString("phonenumber")));
        }
        return returnArray;
    }


    public  void editPost(int id, String posttitle, String postbody, String dateofpost, String link) throws SQLException{
        String query;
        if(currentLoginData.getUserType() == 0) {
            query = "update post set posttitle = '" + posttitle + "', postbody = '" + postbody + "', dateofpost = '" + dateofpost + "' where postid=" + (id + 1);
        } else {
            query = "update blog set blogtitle = '" + posttitle + "', blogbody = '" + postbody + "' , dateofpost = '" + dateofpost + "', link = '"+ link +"' where blogid=" + (id + 1);
        }

        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public void deletePost(int id, int posttype) throws SQLException {
        if(posttype == 0) {
            String query = "delete from post where postid = '" + id + "';";
            //update ids in another table
            Statement statement = con.createStatement();
            statement.executeUpdate(query);

            query = "update post set postid = postid - 1 where postid > '" + id + "';"; //UPdate ids
            statement = con.createStatement();
            statement.executeUpdate(query);

            //Remove from X_Likes tables
            removePostLike(currentLoginData.getId(), id, 0);
        } if(posttype == 1) {
            String query = "delete from blog where blogid = '" + id + "';";
            Statement statement = con.createStatement();
            statement.executeUpdate(query);

            query = "update blog set blogid = blogid - 1 where blogid > '" + id + "';"; //UPdate ids
            statement = con.createStatement();
            statement.executeUpdate(query);
            //Remove from X_Likes tables
            removePostLike(currentLoginData.getId(), id, 1);
        }


    }
//
    public int giveNewPostID() throws SQLException{
        int i = 1;
        boolean A = false;
        while (true){
            A = this.CheckPostEmpty(i);
            if (A == true) {
                return i;
            }
            else if (A == false) {
                i++;
                continue;
            }
        }
    }

    public boolean CheckPostEmpty(int id) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery("select * from post where id ="+id);
        String A = null;
        while (result.next()) {
            A = result.getString("id");
        }
        if (A == null) {
            return true;
        }
        else {
            return false;
        }
    }

    //Checks Available ID in the Regular user Database
    public int giveARegularUserID() throws SQLException {
        int i=1;
        boolean A = false;
        while (true) {
            A = this.CheckRegularUserEmpty(i);
            if (A == true) {
                return i;
            }
            else if (A == false) {
                i++;
                continue;
            }
        }
    }

    //Checks whether a given id is available in Regular user Database
    public boolean CheckRegularUserEmpty(int id) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery("select * from regularUser where id ="+id);
        String A = null;
        while (result.next()) {
            A = result.getString("id");
        }
        if (A == null) {
            return true;
        }
        else {
            return false;
        }
    }

    //Checks Available ID in the Regular user Database
    public int givedoctorID() throws SQLException {
        int i=1;
        boolean A = false;
        while (true) {
            A = this.Checkdoctorid(i);
            if (A == true) {
                return i;
            }
            else if (A == false) {
                i++;
                continue;
            }
        }
    }

    //Checks whether a given id is available in Regular user Database
    public boolean Checkdoctorid(int id) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery("select * from doctor_info where doctorid ="+id);
        String A = null;
        while (result.next()) {
            A = result.getString("doctorid");
        }
        if (A == null) {
            return true;
        }
        else {
            return false;
        }
    }

    //Checks if email exists in database
    public boolean isRegularUserEmailPresent(String email) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery("select * from regularUser where email = '"+email+"'");
        String A = null;
        while (result.next()) {
            A = result.getString("email");
        }
        if (A == null) {
            return false;
        }
        else {
            return true;
        }
    }

    //Checks Available ID in the Agency user Database
    public int giveAAgencyUserID() throws SQLException {
        int i=1;
        boolean A = false;
        while (true) {
            A = this.CheckAgencyUserEmpty(i);
            if (A == true) {
                return i;
            }
            else if (A == false) {
                i++;
                continue;
            }
        }
    }

    //Checks whether a given id is available in Agency user Database
    public boolean CheckAgencyUserEmpty(int id) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery("select * from agencyUser where id ="+id);
        String A = null;
        while (result.next()) {
            A = result.getString("id");
        }
        if (A == null) {
            return true;
        }
        else {
            return false;
        }
    }

    //Checks if email exists in Agencydatabase
    public boolean isAgencyUserEmailPresent(String email) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery("select * from agencyUser where email = '"+email+"'");
        String A = null;
        while (result.next()) {
            A = result.getString("email");
        }
        if (A == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean isUserEmailPresent(String email) throws SQLException {
        if (this.isRegularUserEmailPresent(email) || this.isAgencyUserEmailPresent(email)) {
            return true;
        }
        else {
            return false;
        }
    }

    public ArrayList<Integer> getPostLikes(int userid, int postType) throws SQLException {
        ArrayList<Integer> returnarray = new ArrayList<>();
        String query = "";

        if(currentLoginData.userType == 1)
            query = "select postid from agency_likes where posttype = '"+postType+"' and agencyid = "+userid;
        else if(currentLoginData.userType == 0)
            query = "select postid from user_likes where posttype = '"+postType+"' and userid = "+userid;

        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            returnarray.add(values.getInt("postid"));
        }
        return returnarray;
    }

    //Used to add post and blogs likes for user & agency
    //depends on current login instance
    public void addPostLike(int id, int postId, int posttype) throws SQLException {
        String query = "";
        if(currentLoginData.getUserType() == 0) { //if normal
            query = "insert into user_likes values ('" + id + "', '" + postId + "', '" + posttype + "')";
        } else  if(currentLoginData.getUserType() == 1){ //if not, add to agency like
            query = "insert into agency_likes values ('" + id + "', '" + postId + "', '" + posttype + "')";
        }
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }


    //Remove from Likes tables
    public void removePostLike(int id, int postId, int posttype) throws SQLException {
        String query = "";
        if(currentLoginData.getUserType() == 0) { //if normal
            query = "delete from user_likes where userid = '"+id+"' and postid = '"+postId+"' and posttype = '"+posttype+"'";
        } else  if(currentLoginData.getUserType() == 1){ //if not, add to agency like
            query = "delete from agency_likes where agencyid = '"+id+"' and postid = '"+postId+"' and posttype = '"+posttype+"'";
        }

        Statement statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public void closeConnection() throws SQLException {
        this.con.close();
    }

    public boolean isEmailExists(String email) throws SQLException {
        if(isUserEmailPresent(email) || isAgencyUserEmailPresent(email)) {
            return true;
        } else return false;

//        String query = "SELECT COUNT(*) FROM regularUser WHERE email = ?"; // Adjust table and column names
//        try (PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, email);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1) > 0;
//                }
//                return false;
//            }
//        }
    }

    public void resetPassword(String email, String newPassword) throws SQLException {
        int usertype = findUserType(email);
        String hashedPassword = hashPassword(newPassword);
        String query;
        if (usertype == 0) {
            query = "UPDATE regularUser SET password = ? WHERE email = ?";
        } else {
            query = "UPDATE agencyUser SET password = ? WHERE email = ?";
        }
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            ps.executeUpdate();
        }
    }

    private String hashPassword(String password) {
        // Implement password hashing logic here
        // Return the hashed password

        int workload = 12;
        return BCrypt.hashpw(password, BCrypt.gensalt(workload));
    }

    public boolean checkPassword(String email, String plaintextPassword) throws SQLException {
        String storedHash = getStoredPasswordHash(email);
        if (storedHash == null) {
            // User not found, or no password set for this user.
            return false;
        }
        return BCrypt.checkpw(plaintextPassword, storedHash);
    }

    private String getStoredPasswordHash(String email) throws SQLException {
        int userType = findUserType(email);
        // Implement the SQL logic to retrieve the stored hashed password
        // based on the provided email.
        // Example SQL: "SELECT password FROM users WHERE email = ?"
        String password = "";
        if(userType == 0) {
            String query = "SELECT password FROM regularUser WHERE email = ?"; // Adjust this query
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        password = rs.getString("password");
                    } else {
                        return null; // Or handle this scenario appropriately
                    }
                }
            }
        } else if(userType == 1) {
            String query = "SELECT password FROM agencyUser WHERE email = ?"; // Adjust this query
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        password = rs.getString("password");
                    } else {
                        return null; // Or handle this scenario appropriately
                    }
                }
            }
        }
        return password;
    }

    //Code to retrieve all unsafe zone coordinates from the table
    public ArrayList<unsafeCoordsFromDB> getUnsafeCoords() throws SQLException{
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery("select zoneid, xaxis, yaxis, zonereason from unsafe_zones");
        ArrayList<unsafeCoordsFromDB> returnval = new ArrayList<>();
        while (rs.next()) {
            returnval.add(new unsafeCoordsFromDB(rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4)));
        }
        return returnval;
    }

    public void insertNewUnsafeZone(double x, double y, String reason) throws SQLException {
        Statement statement = con.createStatement();
        int tableid = this.giveANewTableID("unsafe_zones", "zoneid");
        statement.executeUpdate("insert into unsafe_zones values("+tableid+", '"+x+"', '"+y+"', '"+reason+"')");
    }

    public void insertNewUnsafeZoneRequest(double x, double y, String reason) throws SQLException {
        Statement statement = con.createStatement();
        int tableid = this.giveANewTableID("zone_requests", "zonerequestid");
        LocalDateTime curDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateObtained = curDate.format(formatter);
        statement.executeUpdate("insert into zone_requests values("+tableid+", '"+x+"', '"+y+"', '"+reason+"', '"+dateObtained+"')");
    }

    public ArrayList<unsafeRequestsFromDB> getUnsafeZoneRequests() throws SQLException {
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery("select * from zone_requests order by zonerequestdate");
        ArrayList<unsafeRequestsFromDB> returnval = new ArrayList<>();
        while (rs.next()) {
            returnval.add(new unsafeRequestsFromDB(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4)));
        }
        return returnval;
    }

    public void deleteUnsafeZoneRequest(int position) throws SQLException {
        Statement statement = con.createStatement();
        statement.executeUpdate("delete from zone_requests where zonerequestid = "+position);
    }

    //Made specifically to store files into a file for use
    public String getUnsafeReasons() throws SQLException {
        String result = "";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery("select zonereason from unsafe_zones order by zoneid");
        while (rs.next()) {
            result += rs.getString(1)+"%";
        }
        String finalresult = "";
        if (!result.equals("")) {
            finalresult = result.substring(0, result.length()-1);
        }
        return finalresult;
    }

    //To retrieve unsafe zone request info for a particular id
    public unsafeRequestsFromDB getUnsafeRequestDetails(int DBID) throws SQLException{
        unsafeRequestsFromDB returnval = null;
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery("select * from zone_requests where zonerequestid ="+DBID);
        while (rs.next()) {
            returnval = new unsafeRequestsFromDB(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4));
        }
        return returnval;
    }

    //Hari's custom implementation which returns a new table id for any kind of table.
    public int giveANewTableID(String tablename, String tableIDname) throws SQLException {
        int i=1;
        boolean A = false;
        while (true) {
            A = this.CheckTableEmpty(i, tablename, tableIDname);
            if (A == true) {
                return i;
            }
            else if (A == false) {
                i++;
                continue;
            }
        }
    }

    //To check if a particular table ID is available
    public boolean CheckTableEmpty(int id, String tablename, String tableIDname) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery("select * from "+tablename+" where "+tableIDname+" ="+id);
        String A = null;
        while (result.next()) {
            A = result.getString(tableIDname);
        }
        if (A == null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void addPoll(int userType, int userId, String question) throws SQLException {
        Statement statement = con.createStatement();
        int id = fragment_home.polls.size()+1;
        String query = "insert into polls values ('"+id+"', '"+userType+"', '"+userId+"','"+question+"')";
        fragment_home.polls.add(new Polls(id, userId, userType, question));
        statement = con.createStatement();
        statement.executeUpdate(query);
    }

    public ArrayList<Polls> retrievePollsInformation() throws SQLException {
        ArrayList<Polls> returnArray = new ArrayList<Polls>();
        String query = "select * from polls";
        Statement statement = con.createStatement();
        ResultSet values = statement.executeQuery(query);
        while (values.next()) {
            //public Polls(int postId, String postTitle, String postBody, int likes, int dateOfPost, int userId, int userType)
            returnArray.add(new Polls(values.getInt("id"),
                    values.getInt("userid"),
                    values.getInt("usertype"),
                    values.getString("question")));
        }
        return returnArray;
    }


}

//References used:
//https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
