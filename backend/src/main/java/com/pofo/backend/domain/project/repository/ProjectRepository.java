package com.pofo.backend.domain.project.repository;

import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.user.join.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

   List<Project>  findAllByOrderByIdDesc();

   List<Project> findByIsDeletedFalseOrderByIdDesc();

   @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(CAST(p.description AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
   List<Project> searchByKeyword(@Param("keyword") String keyword);

   List<Project> findByUserAndIsDeletedTrue(User user);


   List<Project> findByIdInAndIsDeletedTrue(List<Long> projectIds);
}
