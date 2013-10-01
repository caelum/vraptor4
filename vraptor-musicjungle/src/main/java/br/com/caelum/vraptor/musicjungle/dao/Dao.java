package br.com.caelum.vraptor.musicjungle.dao;

import java.util.Collection;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

public interface Dao<T> {

	public Collection<T> findAll();

	public Collection<T> findByCriteria(CriteriaQuery<T> criteria);

	public Collection<T> findByPredicate(Predicate predicate);

	public T findByPK(Object id);
	
	public T findUniqueByCriteria(CriteriaQuery<T> criteria);	

	public T findUniqueByPredicate(Predicate predicate);

	public void remove(T entity);

	public T persist(T entity);

	public T merge(T entity);
	
	public T refresh(T entity);
}