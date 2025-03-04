package com.pofo.backend.domain.project.repository;

import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.user.join.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

   List<Project>  findAllByOrderByIdDesc();

   // 이름이나 설명에 키워드가 포함된 프로젝트 검색 (대소문자 구분 X)
   List<Project> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameKeyword, String descriptionKeyword);

   List<Project> findByUserAndIsDeletedTrue(User user);


   List<Project> findByIdInAndIsDeletedTrue(List<Long> projectIds);
}
