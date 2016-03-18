package com.katari.jwallet.repository;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ImmutableRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {

	T findOne(ID id);

	Page<T> findAll(Pageable pageable);
	
	<S extends T> S insert(S entity);
}