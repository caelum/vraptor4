package br.com.caelum.vraptor.musicjungle.dao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

public class GenericJPADao<T> implements Dao<T> {

	@Inject protected EntityManager em;

	private final Class<T> clazz;
	
	public GenericJPADao(Class<T> clazz) {
		this.clazz = clazz;
	}

	protected CriteriaBuilder builder;
	protected CriteriaQuery<T> criteria;
	protected Root<T> from;
	
	@PostConstruct
	public void postConstruct() {
		this.builder = em.getCriteriaBuilder();
		this.criteria = this.builder.createQuery(clazz);
		this.from = this.criteria.from(clazz);
	}

	@Override
	public List<T> findAll() {
		return findByCriteria(criteria.select(from));
	}

	@Override
	public List<T> findByCriteria(CriteriaQuery<T> criteria) {
		return em.createQuery(criteria).getResultList();
	}
	
	@Override
	public List<T> findByPredicate(Predicate predicate) {
		return findByCriteria(criteria.select(from).where(predicate));
	}

	@Override
	public T findByPK(Object id) {
		return em.find(clazz, id);
	}

	@Override
	public T findUniqueByPredicate(Predicate predicate) {
		return findUniqueByCriteria(criteria.select(from).where(predicate));
	}

	@Override
	public T findUniqueByCriteria(CriteriaQuery<T> criteria) {
		try {
			return em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}			
	}
	
	@Override
	public T persist(T entity) {
		em.persist(entity);
		return entity;
	} 

	@Override
	public T merge(T entity) {
		return em.merge(entity);
	}

	@Override
	public void remove(T entity) {
		em.remove(entity);
	}

	@Override
	public T refresh(T entity) {
		getSession().refresh(entity); // You still can use Hibernate Session
		return entity;
	}
	
	private Session getSession() {
		return em.unwrap(Session.class);
	}
}
