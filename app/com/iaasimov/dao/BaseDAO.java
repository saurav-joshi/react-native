package com.iaasimov.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import play.Logger;

import com.iaasimov.utils.PagedSearchResult;
import com.iaasimov.utils.StringUtils;
import com.iaasimov.utils.ValueObject;

/**
 * 
 * @author HaNguyen modified by $Author: ralph $
 * @version $Revision: 1.5 $ 
 */
public class BaseDAO {

	private Session session;
	
	protected BaseDAO() {}

	public static BaseDAO getInstance(Session session){
		BaseDAO	instance = new BaseDAO();
		instance.setSession(session);
		return instance;
	}
	
	/**
	 * Retrieve Hibernate session
	 * 
	 * @return Session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Save object instances by Id
	 * @param entity entity to Save/Update
	 * @return void
	 * @throws HibernateException
	 */
	public Serializable save(Object entity) {
		Logger.debug("START save...");
		Serializable ret = -1l;
		try {
			ret = getSession().save(entity);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new HibernateException(e.toString());
		}
		Logger.debug("END save...");
		return ret;
	}
	
	/**
	 * Save/Update object instances by Id
	 * @param entity entity to Save/Update
	 * @return void
	 * @throws HibernateException
	 */
	public void saveOrUpdate(Object entity) {
		Logger.debug("START saveOrUpdate...");
		try {
			getSession().saveOrUpdate(entity);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new HibernateException(e.toString());
		}
		Logger.debug("END saveOrUpdate...");
	}
	
	/**
	 * Delete object instances by Id
	 * @param entity entity to delete
	 * @return void
	 * @throws HibernateException
	 */
	public void delete(Object entity) {
		Logger.debug("START delete...");
		try {
			getSession().delete(entity);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new HibernateException(e.toString());
		}
		Logger.debug("END delete...");
	}

	/**
	 * Return object instances by Id
	 * @param myClass Class of entity to retrieve
	 * @param id id of object.
	 * @return matching object instance.
	 * @throws HibernateException
	 */
	public Object findById(Class myClass, Serializable id) {
		Logger.debug("START findById...");
		Object entity = null;
		try {
			entity = getSession().get(myClass, id);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new HibernateException(e.toString());
		}
		Logger.debug("END findById...");
		return entity;

	}

	/**
	 * Return a unique object by restrictions.
	 * @param entityClass Class of entity to retrieve
	 * @param restrictions collection of Restrictions.
	 * @return unique object instances.
	 * @throws HibernateException
	 */
	public Object findUniqueByCriteria(Class entityClass, Collection restrictions) {
		Logger.debug("START findUniqueByCriteria...");
		Criteria criteria = null;
		try {
			criteria = getSession().createCriteria(entityClass);
			// Add criteria
			if (restrictions != null) {
				Iterator criteriaIter = restrictions.iterator();
				while (criteriaIter.hasNext()) {
					Criterion criterion = (Criterion) criteriaIter.next();
					criteria.add(criterion);
				}
			}

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new HibernateException(e.toString());
		}
		Logger.debug("END findUniqueByCriteria...");
		return criteria.uniqueResult();
	}

	public List findByCriteria(Class entityClass, Collection restrictions) {
		return findByCriteriaWithEagers(entityClass, restrictions, null);
	}
		
	/**
	 * Return list of object instances by restrictions
	 * @param entityClass Class of entity to retrieve
	 * @param restrictions collection of Restrictions.
	 * @return list of matching object instances.
	 * @throws HibernateException
	 */
	public List findByCriteriaWithEagers(Class entityClass, Collection restrictions, String[] eagers) {
		return findByCriteriaWithEagers(entityClass, restrictions, eagers, null);
	}
	
	public List findByCriteriaWithEagers(Class entityClass, Collection restrictions, String[] eagers, Collection orders) {
		Logger.debug("START findByCriteria...");

		List list = new ArrayList();
		try {
			Criteria criteria = getSession().createCriteria(entityClass);
			// Add criteria
			if (restrictions != null) {
				Iterator criteriaIter = restrictions.iterator();
				while (criteriaIter.hasNext()) {
					Criterion criterion = (Criterion) criteriaIter.next();
					criteria.add(criterion);
					Logger.debug("criteria:" + criterion);
				}
			}

			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			if (eagers !=null && eagers.length>0) {
				for (int i=0; i< eagers.length;i++) {
					criteria.createAlias(eagers[i], eagers[i], CriteriaSpecification.LEFT_JOIN);
				}
				
			}
			
			if (orders != null) {
				Iterator orderIter = orders.iterator();
				while (orderIter.hasNext()) {
					Order order = (Order) orderIter.next();
					criteria.addOrder(order);
					Logger.debug("order:" + order);
				}
			}
			
			list = criteria.list();
		} catch (HibernateException e) {
			Logger.error(e.getMessage(), e);
			throw new HibernateException(e.toString());
		}
		Logger.debug("END findByCriteria...");
		return list;
	}

	
	/**
	 * Return list of object instances by restrictions
	 * @param entityClass Class of entity to retrieve
	 * @param restrictions collection of Restrictions.
	 * @param orders collection of orders.
	 * @return list of matching object instances.
	 * @throws HibernateException
	 */
	public List findByCriteria(Class entityClass, Collection restrictions, Collection orders) {
		Logger.debug("START findByCriteria...");

		List list = new ArrayList();
		try {
			Criteria criteria = getSession().createCriteria(entityClass);
			// Add criteria
			if (restrictions != null) {
				Iterator criteriaIter = restrictions.iterator();
				while (criteriaIter.hasNext()) {
					Criterion criterion = (Criterion) criteriaIter.next();
					criteria.add(criterion);
					Logger.debug("criteria:" + criterion);
				}
			}

			if (orders != null) {
				Iterator orderIter = orders.iterator();
				while (orderIter.hasNext()) {
					Order order = (Order) orderIter.next();
					criteria.addOrder(order);
					Logger.debug("order:" + order);
				}
			}
			criteria.setMaxResults(50);
			list = criteria.list();
		} catch (HibernateException e) {
			Logger.error(e.getMessage(), e);
			throw new HibernateException(e.toString());
		}
		Logger.debug("END findByCriteria...");
		return list;
	}
	
	/**
	 * Get total records base on a criteria
	 * @param criteria
	 * @return
	 */
	public long getTotalRecords(Criteria criteria){		
		criteria.setFirstResult(0);
		criteria.setMaxResults(Integer.MAX_VALUE);
		List results = criteria.setProjection(
				Projections.projectionList().add(Projections.countDistinct("id")))
				.list();
		return ((Integer) results.get(0)).longValue();
	}

	/**
	 * Get total records base on a criteria
	 * @param criteria
	 * @return
	 */
	public List getOnlyId(Criteria criteria){		
		criteria.setFirstResult(0);
		criteria.setMaxResults(Integer.MAX_VALUE);
		List results = criteria.setProjection(
				Projections.projectionList().add(Projections.property("id")))
				.list();
		return results;
	}
	
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * 
	 * @param v
	 * @return
	 * @throws Exception
	 */
	protected Criterion translate(ValueObject v) throws Exception{
		return translate(v.getKey(), v.getOp(), v.getValue());
	}
	/**
	 * 
	 * @param key
	 * @param operator
	 * @param value
	 * @return
	 */
	protected Criterion translate(String key, String operator, Object value) throws Exception{
		if ("eq".equalsIgnoreCase(operator) || "=".equals(operator)) {
			return Restrictions.eq(key, value);
		} else if ("eqProperty".equalsIgnoreCase(operator)) {
			return Restrictions.eqProperty(key, (String) value);
		} else if ("ne".equalsIgnoreCase(operator)) {
			return Restrictions.ne(key, value);
		} else if ("notNull".equalsIgnoreCase(operator) || "isnotNull".equalsIgnoreCase(operator)) {
			return Restrictions.isNotNull(key);
		} else if ("neProperty".equalsIgnoreCase(operator)) {
			return Restrictions.neProperty(key, (String)value);
		} else if ("like".equalsIgnoreCase(operator)) {
			return Restrictions.like(key, value);
		} else if ("ilike".equalsIgnoreCase(operator)) {
			return Restrictions.ilike(key, value);
		} else if ("gt".equalsIgnoreCase(operator)) {
			return Restrictions.gt(key, value);
		} else if ("gtProperty".equalsIgnoreCase(operator)) {
			return Restrictions.gtProperty(key, (String)value);
		} else if ("ge".equalsIgnoreCase(operator)) {
			return Restrictions.ge(key, value);
		} else if ("geProperty".equalsIgnoreCase(operator)) {
			return Restrictions.geProperty(key, (String)value);
		}  else if ("lt".equalsIgnoreCase(operator)) {
			return Restrictions.lt(key, value);
		}  else if ("ltProperty".equalsIgnoreCase(operator)) {
			return Restrictions.ltProperty(key, (String)value);
		}  else if ("le".equalsIgnoreCase(operator)) {
			return Restrictions.le(key, value);
		}  else if ("leProperty".equalsIgnoreCase(operator)) {
			return Restrictions.leProperty(key, (String)value);
		}  else if ("in".equalsIgnoreCase(operator)) {
			if (value instanceof Collection) {
				return Restrictions.in(key, (Collection)value);	
			} else {
				return Restrictions.in(key, (Object[])value);
			}
		}  else if ("inObjectArray".equalsIgnoreCase(operator)) {
			Object[] array=(Object[]) value;
			return Restrictions.in(key, array);
		}  else if ("inCollection".equalsIgnoreCase(operator)) {
			Collection collection=(Collection) value;
			return Restrictions.in(key, collection);
		}  else if ("isNull".equalsIgnoreCase(operator)) {
			return Restrictions.isNull(key );
		} else {
			throw new Exception("buidRestrictions--- operator not found");
		}
		
	}
	
	/**
	 * 
	 * @param T
	 * @return
	 */
	public PagedSearchResult findByRestriction(Class clazz, List<ValueObject> res, ValueObject[] alias, int firstResult, int pageSize) throws Exception{
		return findByRestriction(clazz, res, alias, null, null, firstResult, pageSize);
	}
	
	public PagedSearchResult findByRestriction(Class clazz, List<ValueObject> res, ValueObject[] alias, List<Long> exclude, int firstResult, int pageSize) throws Exception{
		return findByRestriction(clazz, res, alias, exclude, null, firstResult, pageSize);
	}
	
	public PagedSearchResult findByRestriction(Class clazz, List<ValueObject> res, ValueObject[] alias, List<Long> exclude, String orders, int firstResult, int pageSize) throws Exception{
		PagedSearchResult result = new PagedSearchResult(); 
		Criteria criteria=getSession().createCriteria(clazz);
		if (alias !=null) {
			for (int i=0;i< alias.length;i++){
				ValueObject v=alias[i];
				if ("left".equalsIgnoreCase(v.getOp())) {
					criteria.createAlias(v.getKey(), v.getValue().toString(),CriteriaSpecification.LEFT_JOIN);
				} else {
					criteria.createAlias(v.getKey(), v.getValue().toString());
				}
			}
			
		}
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		if (!StringUtils.isEmpty(orders)) {
			StringTokenizer token = new StringTokenizer(orders, ",");
			while (token.hasMoreTokens()) {
				String orderProperty = token.nextToken();
				String sortOrder = orderProperty.substring(orderProperty.lastIndexOf("-")+1);
				orderProperty = orderProperty.substring(0,orderProperty.lastIndexOf("-"));
				if ("asc".equalsIgnoreCase(sortOrder)) {
					criteria.addOrder(Order.asc(orderProperty));
				} else {
					criteria.addOrder(Order.desc(orderProperty));
				}
			}
		}
		
		if (exclude != null && !exclude.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in("id", exclude)));
		}
		
		if (res!=null){
			for (int i=0;i< res.size();i++){
				criteria.add(translate(res.get(i)));
			}
		}
		criteria.setFirstResult(pageSize * (firstResult - 1));
		if (pageSize>0) {
			criteria.setMaxResults(pageSize);
		}
		
		result.setFoundList(criteria.list());
		
		if (pageSize>0) {
			long count = getTotalRecords(criteria);
			result.setFoundCount(count);
		}
		return result;
	}
}

