package com.iaasimov.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import play.Logger;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.1 $ 
 */
public class HibernateUtil {

	/** Hibernate session Factory */	
	private static SessionFactory sessionFactory;
	
	/** init Hibernate Factory */
	static {
		try {
			//init Configuraion
			AnnotationConfiguration configuration = new AnnotationConfiguration();			
			//init session factory
			sessionFactory = configuration.configure().buildSessionFactory();		
		} catch (Throwable ex) {
			Logger.error(ex.getMessage(), ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Returns static Hibernate Session Factory.
	 * 
	 * @return SessionFactory
	 * 
	 * Thuy to investigate on using Weblogic JNDI to store session factory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Close Hibernate Session Factory.
	 * 
	 * @return void
	 * 
	 */
	public static void shutdown() {
		getSessionFactory().close();
	}
}
