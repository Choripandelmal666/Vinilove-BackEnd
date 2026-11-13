package com.vinilove.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinilove.model.PerfilVendedor;

public interface PerfilVendedorRepository extends JpaRepository<PerfilVendedor, Long> {
	Optional<PerfilVendedor> findByUsuario_Id(Long usuarioId);
}
