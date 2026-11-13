package com.vinilove.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.model.MensajeContacto;

public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {
	List<MensajeContacto> findAllByOrderByIdDesc();
}
