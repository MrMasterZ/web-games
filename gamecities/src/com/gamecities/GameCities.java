package com.gamecities;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GameCities extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static volatile List<String> citiesList = new ArrayList<>(); // список для хранения уже названных имён городов (для дальнейшего
																			// восстановления сессии)

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter pw = resp.getWriter();
		pw.println("errorGet");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter pw = resp.getWriter();

		String cityUserRaw = req.getParameter("userCity"); // получили имя города от пользователя
		String cityUser = new String(cityUserRaw.getBytes("ISO-8859-1"), "utf8");
		String replayServer; // ответ сервера
		if (cityUser.equals("clear")) {
			citiesList.clear();
			pw.println("clear");
		} else {
			try {
				Class.forName("org.postgresql.Driver").newInstance();
				UserRequestDb userRequestDb = new UserRequestDb();
				if (userRequestDb.cityExistsDb(cityUser)) { // если город введенный пользователем существует (проверка существования в БД)
					userRequestDb.disconnectDb();
					citiesList.add(cityUser); // добавляем введенное пользователем имя города в список названных имён городов

					char[] cityUserChar = cityUser.substring(cityUser.length() - 2, cityUser.length()).toUpperCase().toCharArray(); // берём 2 последние буквы имя города введенного пользователем, переводим в
																																	// верхний регистр и записываем в массив символов cityUserChar
					char endChar = cityUserChar[cityUserChar.length - 1]; // последний символ введенного пользователем имени города
					String startName; // формируем startName, который будет передан в запрос (по какой первой букве
										// будем искать имя города) (особые случаи: ё, й, ъ, ь, ы)
					if (endChar == 'Ё')
						startName = "(name LIKE 'Е%' OR name LIKE 'Ё%')";
					else if (endChar == 'Й')
						startName = "(name LIKE 'И%' OR name LIKE 'Й%')";
					else if (endChar == 'Ъ' || endChar == 'Ы' || endChar == 'Ь')
						startName = "name LIKE '" + cityUserChar[cityUserChar.length - 2] + "%'"; // в этих случаях ищем имя города не по последней, а по предпоследней букве
																									// введенного пользователем имени города
					else
						startName = "name LIKE '" + endChar + "%'";

					String exceptionNames = "("; // формируем часть запроса SQL (список уже названных имён городов, которые не
													// надо искать в БД)
					for (String city : citiesList)
						exceptionNames = exceptionNames + "'" + city + "',";
					exceptionNames = exceptionNames.substring(0, exceptionNames.length() - 1) + ")";

					RobotRequestDb robotRequestDb = new RobotRequestDb();
					replayServer = robotRequestDb.robotCityNameDb(startName, exceptionNames); // ищем город для ответа пользователю
					if (replayServer != null)
						citiesList.add(replayServer);
					else
						replayServer = "errorGiveUp";
					robotRequestDb.disconnectDb();
				} else {
					replayServer = "errorNotExists";
					userRequestDb.disconnectDb();
				}
				pw.println(replayServer);
			} catch (SQLException e) {
				replayServer = "errorBd";
				pw.println(replayServer);
			} catch (Exception e) {
				replayServer = "errorOther";
			}
		}
	}
}