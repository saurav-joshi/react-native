package com.iaasimov.dao.service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import play.Logger;

import com.iaasimov.dao.BaseDAO;
import com.iaasimov.utils.HibernateUtil;
import com.iaasimov.utils.PagedSearchResult;
import com.iaasimov.utils.ValueObject;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.8 $ 
 */
public class GenericController {
	
	private Transaction tx = null; 
	
	private Session session = null;
	
	private Boolean isNewTx = Boolean.FALSE;
	
	private Map<String,Object> daos = new HashMap<String,Object>();
	
	public static final int IN_LIST_SIZE=400;
	
	/**
	 * Empty constructor 
	 */
	protected GenericController(){
		super();
	}
	
	public static GenericController getInstance() {
		return new GenericController();
	}
	
	/**
	 * Create a dao
	 * @param daoClass
	 * @param session
	 * @return
	 */
	protected synchronized Object getDAO(Class daoClass, Session session) {
		String key = daoClass.getName();
		if (daos.containsKey(key)) {
			Object dao = daos.get(key);
			((BaseDAO) dao).setSession(session);
			return daos.get(key);
		}
		Object dao =null;
		try {
			Method method =daoClass.getMethod("getInstance", Session.class) ;
			dao = method.invoke(null, session);
			daos.put(key, dao);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return dao;
	}
	
	/**
	 * Destroy all daos
	 */
	protected synchronized void destroyDaos() {
		daos = new HashMap<String, Object>();
	}
	
	/**
	 * Begin a new session 
	 * @return
	 */
	protected Session beginSessionRequiredNewTx(){
		session = HibernateUtil.getSessionFactory().openSession();
		tx = session.beginTransaction();
		isNewTx = Boolean.TRUE;
		return session;		
	}
	
	/**
	 * Use current available session  
	 * @return
	 */
	protected Session beginSessionRequiredTx(){
		session = HibernateUtil.getSessionFactory().getCurrentSession();
		tx = session.getTransaction();
		if (tx == null || !tx.isActive()){
			tx = session.beginTransaction();
			isNewTx = Boolean.TRUE;
		}
		return session;
	}
	
	/**
	 * End a session
	 * @param session
	 * @param isRollBacked
	 */
	protected void endSession(Session session, Boolean isRollBacked){
		boolean isReadyToClose = false;
		if (tx ==null) {
			System.out.println("\n########################################\ntransaction is null") ;
		}
		if (tx != null && tx.isActive() && isNewTx){
			if (!isRollBacked){
				tx.commit();
			}else{
				tx.rollback();
			}
			isReadyToClose = true;
			isNewTx = Boolean.FALSE;
			tx = null;
		}
		if (isReadyToClose && session.isOpen()){
			session.close();
			destroyDaos();
		}
	}
	
	/**
	 * Persist the entity
	 * @param session
	 * @param entity
	 */
	public void persitEntity(Session session, Object entity){
		session.persist(entity);
	}
	
	/**
	 * Delete object instances by Id
	 * 
	 * @param entity
	 *            entity to delete
	 * 
	 * @return void
	 * 
	 * @throws HibernateException
	 */

	public void deleteEntity(Session session,Object entity) {
		/*Logger.debug(":::START:::");*/
		try {
			session.delete(entity);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new HibernateException(e.toString());
		}
		/*Logger.debug(":::END:::");*/
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	public Object findById(Class entityClass, Serializable id) {
		return findById(entityClass, id, null);
	}
	

	/**
	 * 
	 * @param entityClass
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	protected Object findById(Class entityClass, Serializable id, Session session) {
		boolean closeSession = false;
		if (session == null) {
			closeSession = true;
			session = beginSessionRequiredTx();
		}
		
		Object found = null;
		try {
			BaseDAO dao = BaseDAO.getInstance(session);
			found =dao.findById(entityClass, id);
			if (closeSession) {
				endSession(session, Boolean.FALSE);
			}
		} catch (Exception e) {
			if (closeSession) {
				endSession(session, Boolean.TRUE);
			}
			Logger.error("ERROR - findEntityByProperty:" , e);
		} 
		return found;
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param restrictions
	 * @return
	 */
	public List findByCriteria(Class entityClass, Collection restrictions) {
		Session session = beginSessionRequiredTx();
		List founds = null;
		try {
			BaseDAO dao = BaseDAO.getInstance(session);
			founds=dao.findByCriteria(entityClass, restrictions);
			endSession(session, Boolean.FALSE);
			
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			Logger.error("ERROR - findByCriteria:" , e);
		} 
		return founds;	
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param restrictions
	 * @return
	 */
	public List findByCriteria(Class entityClass, Collection restrictions, Collection order) {
		Session session = beginSessionRequiredTx();
		List founds =null;
		try {
			BaseDAO dao = BaseDAO.getInstance(session);
			founds=dao.findByCriteria(entityClass, restrictions, order);
			endSession(session, Boolean.FALSE);
			
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			Logger.error("ERROR - findByCriteria:" , e);
			
		} 
		return founds;
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param restrictions
	 * @return
	 */
	public List findByCriteriaWithEagers(Class entityClass, Collection restrictions, String[] eagers) {
		Session session = beginSessionRequiredTx();
		List founds =null;
		try {
			BaseDAO dao = BaseDAO.getInstance(session);
			founds=dao.findByCriteriaWithEagers(entityClass, restrictions, eagers);
			endSession(session, Boolean.FALSE);
			
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			Logger.error("ERROR - findByCriteria:" , e);
			
		} 
		return founds;
	}
	
	/**
	 * 
	 * @param entityClass
	 * @param restrictions
	 * @return
	 */
	public List findByCriteriaWithEagers(Class entityClass, Collection restrictions, String[] eagers, Collection order) {
		Session session = beginSessionRequiredTx();
		List founds =null;
		try {
			BaseDAO dao = BaseDAO.getInstance(session);
			founds=dao.findByCriteriaWithEagers(entityClass, restrictions, eagers, order);
			endSession(session, Boolean.FALSE);
			
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			Logger.error("ERROR - findByCriteria:" , e);
			
		} 
		return founds;
	}
	
	/**
	 * Remove an persitent object by given Id
	 * @param id
	 */
	public boolean removeById(Class entityClass, Long id) {
		Object found = findById(entityClass,id);
		Session session=beginSessionRequiredTx();
		try {
			if (found != null) {
				session.delete(found);
			}
			endSession(session,Boolean.FALSE);
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			return false;
		}
		return true;
	}
	
	/**
	 * Remove an persitent object by given object
	 * @param id
	 */
	public boolean removeObject(Object entity) {
		Session session = beginSessionRequiredTx();
		try {
			if (entity != null) {
				session.delete(entity);
			}
			endSession(session,Boolean.FALSE);
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			return false;
		}
		return true;
	}
	
	/**
	 * @param list
	 * @throws Exception
	 */
	public void saveOrUpdateObjects(List list) throws Exception {
		if (list == null || list.size() == 0){
			return;
		}
		Session session= beginSessionRequiredTx();
		try {
			for (int i = 0; i < list.size(); i++) {
				session.saveOrUpdate(list.get(i));
			}
			endSession(session, Boolean.FALSE);
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			throw e;
		}
	}
	
	public void saveOrUpdateObjects(Object object) throws Exception {
		if (object == null ){
			return;
		}
		Session session = beginSessionRequiredTx();
		try {
			session.saveOrUpdate(object);
			endSession(session, Boolean.FALSE);
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			throw e;
		}
	}
	
	public Long saveObject(Object object) throws Exception {
		Long ret = 0l;
		if (object == null ){
			return ret;
		}
		Session session = beginSessionRequiredTx();
		try {
			ret = (Long) session.save(object);
			endSession(session, Boolean.FALSE);
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			throw e;
		}
		return ret;
	}
	
	/**
	 * 
	 * @param clazz
	 * @param res
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	public PagedSearchResult findByRestriction(Class clazz, List<ValueObject> res, ValueObject[] alias, int firstResult, int maxResult) throws Exception{
		return findByRestriction(clazz, res, alias, null, null, firstResult, maxResult);
	}
	
	public PagedSearchResult findByRestriction(Class clazz, List<ValueObject> res, ValueObject[] alias, List<Long> exclude, int firstResult, int maxResult) throws Exception{
		return findByRestriction(clazz, res, alias, exclude, null, firstResult, maxResult);
	}
	
	public PagedSearchResult findByRestriction(Class clazz, List<ValueObject> res, ValueObject[] alias, List<Long> exclude, String orders, int firstResult, int maxResult) throws Exception{
		PagedSearchResult ret = null;
		Session session = beginSessionRequiredTx();
		try {
			ret = BaseDAO.getInstance(session).findByRestriction(clazz, res, alias, exclude, orders, firstResult, maxResult);
			endSession(session, Boolean.FALSE);
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
			Logger.error("ERROR", e);
		}
		return ret; 
	}
	
    public interface GenericQuery<T>{
        public T doInTransaction(Session session);
    }
    public <T> T execute(GenericQuery<T> process) {
        final Session session = beginSessionRequiredTx();
        T result = null;
        try {
            result = process.doInTransaction(session);
            endSession(session, Boolean.FALSE);
        } catch (Exception e) {
            endSession(session, Boolean.TRUE);
            if(e instanceof RuntimeException){
                throw (RuntimeException)e;
            }else{
                throw new RuntimeException(e);
            }
        }

        return result;
    }
    
    /**
	 * Remove an persitent object by given Id
	 * @param id
	 */
	public void deleteObject(Object entity) {
		Session session = beginSessionRequiredTx();
		try {
			if (entity != null) {
				session.delete(entity);
			}
			endSession(session,Boolean.FALSE);
		} catch (Exception e) {
			endSession(session, Boolean.TRUE);
		}
	}
}
