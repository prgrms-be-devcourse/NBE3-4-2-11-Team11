"use client";

import { useRouter } from "next/navigation";
import { createProject } from "@/lib/projectService";
import ProjectForm from "@/components/projects/ProjectForm"; // ✅ ProjectForm 불러오기

const NewProjectPage = () => {
  const router = useRouter();

  const handleSubmit = async (formData: any) => {
    console.log("📢 [handleSubmit] 요청 데이터:", formData);
    
    const res = await createProject(formData);
  
    // ✅ 프로젝트 생성 성공 시 alert() 한 번만 실행 후 리턴
    if (res && res.resultCode === "201") {
      alert("✅ 프로젝트가 성공적으로 등록되었습니다.");
      window.location.href = "/mypage/projects"; // ✅ 바로 이동
      return; // ✅ 여기서 함수 종료 (이제 alert 두 번 안 뜸)
    }
  
    // ❌ 실패한 경우만 실행 (이제 불필요한 실패 메시지가 출력되지 않음)
    alert(`❌ 프로젝트 생성 실패: ${res?.message || "알 수 없는 오류 발생"}`);
  };
  

  return (
    <div>
      <h1 className="page-title">새 프로젝트 작성하기</h1>
      <style jsx>{`
        .page-title {
          font-size: 2.5rem; /* ✅ 제목 크기 키우기 */
          font-weight: bold;
          text-align: center; /* ✅ 가운데 정렬 */
          margin-top: 50px; /* ✅ 상단 간격 추가 */
          margin-bottom: 20px; /* ✅ 하단 간격 추가 */
        }
      `}</style>
      <ProjectForm onSubmit={handleSubmit} />{" "}
      {/* ✅ ProjectForm을 사용하도록 변경 */}
    </div>
  );
};

export default NewProjectPage;
