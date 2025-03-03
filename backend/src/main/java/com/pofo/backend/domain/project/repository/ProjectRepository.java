package com.pofo.backend.domain.project.repository;

import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.user.join.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

   List<Project>  findAllByOrderByIdDesc();

   // 이름이나 설명에 키워드가 포함된 프로젝트 검색 (대소문자 구분 X)
   List<Project> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameKeyword, String descriptionKeyword);

   List<Project> findByUserAndIsDeletedTrue(User user);

   @Query("SELECT p FROM Project p WHERE p.id IN :projectIds AND p.isDeleted = true")
   List<Project> findAllByIdAndIsDeletedTrue(@Param("projectIds") List<Long> projectIds);
}
