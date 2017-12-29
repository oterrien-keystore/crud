package com.ote.crud;

import com.ote.crud.model.IEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IEntityRepository<TE extends IEntity> extends JpaRepository<TE, Long>, JpaSpecificationExecutor<TE> {


}
