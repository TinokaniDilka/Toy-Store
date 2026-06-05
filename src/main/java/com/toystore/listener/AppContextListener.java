package com.toystore.listener;

import com.toystore.dao.UserManager;
import com.toystore.util.DataPaths;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataPaths.init(sce.getServletContext());
        UserManager.getInstance().reload();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // no cleanup required
    }
}
