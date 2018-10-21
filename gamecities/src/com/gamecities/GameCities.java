package com.gamecities;
 
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
 
public class GameCities extends HttpServlet {
private static final long serialVersionUID = 1L;
private static volatile List<String> citiesList = new ArrayList<>();                     // ������ ��� �������� ��� ��������� ��� ������� (��� ����������� �������������� ������)
	
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html;charset=utf-8");
    PrintWriter pw = resp.getWriter();
    pw.println("errorGet");
  }
   
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	resp.setContentType("text/html;charset=utf-8");
    PrintWriter pw = resp.getWriter();
    
    String cityUserRaw = req.getParameter("userCity");     // �������� ��� ������ �� ������������
    String cityUser = new String (cityUserRaw.getBytes ("ISO-8859-1"), "utf8");
    String replayServer;                                             // ����� �������
    if (cityUser.equals("clear")) {
    	citiesList.clear();
    	pw.println("clear");
    }
    else {
    try {
      Class.forName("org.postgresql.Driver").newInstance();
      UserRequestDb userRequestDb = new UserRequestDb();
      if (userRequestDb.cityExistsDb(cityUser)) {                    // ���� ����� ��������� ������������� ���������� (�������� ������������� � ��)
        userRequestDb.disconnectDb();
        citiesList.add(cityUser);                                    // ��������� ��������� ������������� ��� ������ � ������ ��������� ��� �������

        char[] cityUserChar = cityUser.substring(cityUser.length()-2, cityUser.length()).toUpperCase().toCharArray();  // ���� 2 ��������� ����� ��� ������ ���������� �������������, ��������� � ������� ������� � ���������� � ������ ��������  cityUserChar
        char endChar = cityUserChar[cityUserChar.length-1];                                                            // ��������� ������ ���������� ������������� ����� ������
        String startName;                                                                                              // ��������� startName, ������� ����� ������� � ������ (�� ����� ������ ����� ����� ������ ��� ������) (������ ������: �, �, �, �, �)
        if (endChar == '�') startName = "(name LIKE '�%' OR name LIKE '�%')";
        else if (endChar == '�') startName = "(name LIKE '�%' OR name LIKE '�%')";
        else if (endChar == '�' || endChar == '�' || endChar == '�') startName = "name LIKE '" + cityUserChar[cityUserChar.length-2] + "%'";  // � ���� ������� ���� ��� ������ �� �� ���������, � �� ������������� ����� ���������� ������������� ����� ������
        else startName = "name LIKE '" + endChar + "%'";

        String exceptionNames = "(";                                                                                   // ��������� ����� ������� SQL (������ ��� ��������� ��� �������, ������� �� ���� ������ � ��)
        for (String city: citiesList) exceptionNames = exceptionNames + "'" + city + "',";
        exceptionNames = exceptionNames.substring(0, exceptionNames.length() - 1) +  ")";

        RobotRequestDb robotRequestDb = new RobotRequestDb();
        replayServer = robotRequestDb.robotCityNameDb(startName, exceptionNames);                                      // ���� ����� ��� ������ ������������
        if ( replayServer != null) citiesList.add(replayServer);
        else replayServer = "errorGiveUp";
        robotRequestDb.disconnectDb();
      }
      else {
        replayServer ="errorNotExists";
        userRequestDb.disconnectDb();
      }
      pw.println(replayServer);
    }
    catch (SQLException e) {
      replayServer ="errorBd";
	  pw.println(replayServer);
    }
	catch (Exception e) {
      replayServer ="errorOther";
    }
  }
  }
}