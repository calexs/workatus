package com.project.workatus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.workatus.model.UsuarioModel;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {

    public Optional<UsuarioModel> findByLogin(String login);
    public Optional<UsuarioModel> findById(int id);
	public void deleteById(int id);
}
