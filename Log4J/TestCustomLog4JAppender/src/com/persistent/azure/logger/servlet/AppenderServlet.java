package com.persistent.azure.logger.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class AppenderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger LOGGER = Logger.getLogger(AppenderServlet.class);
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOGGER.debug(req.getParameter("message"));
		req.setAttribute("result", "Message logged");
		req.getRequestDispatcher("index.jsp").forward(req, resp);
	}
}
