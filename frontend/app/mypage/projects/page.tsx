"use client";

import ProjectList from "@/components/projects/ProjectList"; // ✅ ProjectList 가져오기
import AuthTestComponent from "@/components/projects/AuthTestComponent"; // ✅ 인증 상태 확인 컴포넌트 추가

export default function ProjectsPage() {
  return (
    <div>
      <AuthTestComponent />
      <ProjectList />
    </div>
  );
}
