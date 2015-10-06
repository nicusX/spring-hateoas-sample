package com.opencredo.demo.hateoas.domain.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opencredo.demo.hateoas.domain.Publisher;

public interface PublisherRepository extends JpaRepository<Publisher, Long>{
   
   
}
