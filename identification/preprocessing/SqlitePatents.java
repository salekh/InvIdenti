package preprocessing;

import base.patent;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by leisun on 15/9/28.
 */


public class SqlitePatents {
    /**
     *  @param path the path of the database
     */
    String path=null;
    Connection connection=null;
    Statement stmt=null;

    ArrayList<patent> patents;

    /**
     * SqlitePatents constructor with the path of the database
     */
    public SqlitePatents(String path) {
        try {

            // connect the database

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+path);
            connection.setAutoCommit(false);
            stmt=connection.createStatement();
            System.out.println("Opened database successfully");



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close some variables
     */
    public void close() {

        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Generate an arraylist of the patents which contains the first "number" patents in a specified table of the database
     * @param number the number of the patents
     * @param str the name of the table
     * @return a arraylist of the patents which contains the first "number" patents
     */
    public ArrayList<patent> getNumPatents(int number,String str){

        if (connection==null) {
            return null;
        }


        ArrayList<String> faillist=new ArrayList<>();

        patents=new ArrayList<>();

        try {
            ResultSet rs=stmt.executeQuery("select * from "+str+";");

            //ResultSet rs=stmt.executeQuery("select * from invpat;");
            int no=0;

            while (rs.next()) {


                if (no<=100)
                {
                    no++;
                    continue;

                }
                if (number<1) {
                    break;
                }

                String patentNumber=rs.getString("Patent");
                String authorLastName=rs.getString("Lastname");
                String assignee=rs.getString("Assignee");
                String category=rs.getString("Class");

                patent temp=new patent(patentNumber,category,assignee,authorLastName);

                if (temp.getAbs()==null&&temp.getClaims()==null&&temp.getDescription()==null&&temp.getTitle()==null) {
                    faillist.add(patentNumber);
                }

                number--;

                patents.add(temp);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            File var1=new File("faillist.txt");
            FileWriter var2=new FileWriter(var1);
            for (String var3:faillist) {
                var2.write(var3);
                var2.write("\n");
            }
            var2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return patents;

    }

    public ArrayList<patent> getPatentsTextFromSQL(int number,String path,String str){
        if (connection==null) {
            return null;
        }



        patents=new ArrayList<>();

        try {
            ResultSet rs=stmt.executeQuery("select * from "+str+";");

            //ResultSet rs=stmt.executeQuery("select * from invpat;");


            while (rs.next()) {


                if (number<1) {
                    break;
                }

                String patentNumber=rs.getString("Patent");
                String authorLastName=rs.getString("Lastname");
                String assignee=rs.getString("Assignee");
                String category=rs.getString("Class");
                String country=rs.getString("Country");

                String abs=readText(path + patentNumber + "/" + "Abstract.txt");
                String claims=readText(path + patentNumber + "/" + "Claims.txt");
                String description=readText(path+patentNumber+"/"+"Description.txt");
                String title=readText(path+patentNumber+"/"+"Title.txt");
                String lat=rs.getString("Lat");
                String lng=rs.getString("Lng");



                patent temp=new patent(patentNumber,abs,claims,description,title,category,assignee,authorLastName,lat,lng,"",country);

                number--;

                patents.add(temp);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }





        return patents;

    }



    /**
     * Write the text of the patent into the path
     * @param path the sqlite database path
     */
    public void writeTextToSqlite(String path)
    {
        try {

            // connect the database

            Class.forName("org.sqlite.JDBC");
            Connection var1 = DriverManager.getConnection("jdbc:sqlite:"+path);

            var1.setAutoCommit(false);

            Statement var2=var1.createStatement();

            System.out.println("Opened database successfully");

            /*
            String sql = "CREATE TABLE PatentText " +
                    "(Patent TEXT PRIMARY KEY     NOT NULL," +
                    " Abstract           TEXT    , " +
                    " Claims            TEXT     , " +
                    " Description        TEXT)";
            */

            String sql;
            for (patent p:this.patents) {

                sql = "INSERT INTO PatentText (Patent,Abstract,Claims,Description) " +
                        "VALUES ('"+p.getPatent_number()+"','"+p.getAbs()+"','"+p.getClaims()+"','"+p.getDescription()+"');";


                var2.executeUpdate(sql);
                var1.commit();
            }

            var2.close();



            var1.close();


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param path store path the text of the patent
     */
    public void writeToTexts(String path){
        for(patent p:patents){
            String path_d=path+"/"+p.getPatent_number()+"/";
            File f=new File(path_d);
            if (!f.exists()) {
                f.mkdirs();
            }
            storeText(path_d+"Abstract.txt",p.getAbs());
            storeText(path_d+"Claims.txt",p.getClaims());
            storeText(path_d+"Description.txt",p.getDescription());
            storeText(path_d+"Title.txt",p.getTitle());
        }
    }

    /**
     *
     * @param path
     * @param str
     */
    public void storeText(String path,String str){
        try {
            FileWriter f=new FileWriter(path);
            if(str!=null) {
                f.write(str);
            }
            else {
                f.write("");
            }

            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readText(String path){
        File f= null;
        try {
            f = new File(path);
            if (!f.exists()) {
                return null;
            } else {
                BufferedReader r=new BufferedReader(new FileReader(path));
                String str="";
                String line=r.readLine();
                while(line!=null){
                    str+=line;
                    line=r.readLine();
                }

                //System.out.println(str);
                return str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
