package org.tyrant.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.max.server.basic.FactionPortal;

public class CronService extends HttpServlet {
	private static final long serialVersionUID = -9141803862208110520L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		new FactionPortal(UserData.userId, UserData.flashcode, UserData.authToken).updateWarsHistory();
		super.doGet(req, resp);
	}
}
