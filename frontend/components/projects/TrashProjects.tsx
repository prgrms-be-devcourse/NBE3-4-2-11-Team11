"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getTrashProjects, restoreProjects } from "@/lib/projectService";
import { permanentlyDeleteProjects } from "@/lib/projectService";

const TrashProjects = () => {
  const router = useRouter();
  const [trashProjects, setTrashProjects] = useState<any[]>([]);
  const [selectedProjects, setSelectedProjects] = useState<string[]>([]);

  useEffect(() => {
    const fetchTrashProjects = async () => {
      console.log("🚀 [fetchTrashProjects] API 요청 시작");
      const res = await getTrashProjects();
      console.log("📢 [fetchTrashProjects] 응답 데이터:", res);

      if (res.code === "200" && res.data) {
        setTrashProjects(res.data);
      } else {
        alert("휴지통을 불러오는 데 실패했습니다.");
      }
    };

    fetchTrashProjects();
  }, []);

  // ✅ 체크박스 선택 핸들러
  const handleSelectProject = (event: React.MouseEvent, projectId: string) => {
    event.stopPropagation(); // ✅ 상세 조회로 이동 방지
    setSelectedProjects((prev) =>
      prev.includes(projectId)
        ? prev.filter((id) => id !== projectId)
        : [...prev, projectId]
    );
  };

  const handleRestore = async () => {
    if (selectedProjects.length === 0) {
      alert("복원할 프로젝트를 선택하세요!");
      return;
    }

    if (!confirm("선택한 프로젝트를 복원하시겠습니까?")) return;

    console.log("🚀 [handleRestore] 선택한 프로젝트:", selectedProjects);
    const res = await restoreProjects(selectedProjects);
    console.log("📢 [handleRestore] 응답 데이터:", res);

    if (res.code === "200") {
      alert("프로젝트가 복원되었습니다!");
      setTrashProjects(
        trashProjects.filter((p) => !selectedProjects.includes(p.projectId))
      ); // ✅ UI에서 제거
      setSelectedProjects([]); // 선택 초기화

      // ✅ 전체 프로젝트 페이지로 이동
      router.push("/mypage/projects");
    } else {
      alert(`오류 발생: ${res.message}`);
    }
  };

  const handlePermanentDelete = async () => {
    if (selectedProjects.length === 0) {
      alert("삭제할 프로젝트를 선택하세요!");
      return;
    }

    if (
      !confirm(
        "선택한 프로젝트를 완전히 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다!"
      )
    )
      return;

    console.log(
      "🚀 [handlePermanentDelete] 선택한 프로젝트:",
      selectedProjects
    );
    const res = await permanentlyDeleteProjects(selectedProjects);
    console.log("📢 [handlePermanentDelete] 응답 데이터:", res);

    if (res.code === "200") {
      alert("선택한 프로젝트가 완전히 삭제되었습니다!");
      setTrashProjects(
        trashProjects.filter((p) => !selectedProjects.includes(p.projectId))
      ); // ✅ UI에서 제거
      setSelectedProjects([]); // 선택 초기화
    } else {
      alert(`오류 발생: ${res.message}`);
    }
  };

  return (
    <div className="container">
      {/* ✅ 제목과 설명을 감싸는 header */}
      <div className="header">
        <h1 className="title">🗑️ 휴지통</h1>
        <p className="description">삭제된 프로젝트 목록입니다.</p>
      </div>

      <div className="top-header">
        <button
          className="view-projects-button"
          onClick={() => router.push("/mypage/projects")}
        >
          📂 전체 프로젝트 보기
        </button>
      </div>

      {/* ✅ 복구 버튼은 왼쪽 정렬 */}
      <div className="button-container">
        <button
          className="restore-button"
          onClick={handleRestore}
          disabled={selectedProjects.length === 0}
        >
          🔄 선택한 프로젝트 복원
        </button>

        <button
          className="delete-permanently-button"
          onClick={handlePermanentDelete}
          disabled={selectedProjects.length === 0}
        >
          🚨 완전 삭제
        </button>
      </div>

      {/* ✅ 휴지통 목록 */}
      <div className="grid">
        {trashProjects.length > 0 ? (
          trashProjects.map((project: any) => (
            <div
              key={project.projectId}
              className="card"
              onClick={() =>
                router.push(`/mypage/projects/${project.projectId}`)
              }
              style={{ cursor: "pointer" }}
            >
              {/* ✅ 체크박스 클릭 시 상세 페이지로 이동하지 않도록 수정 */}
              <input
                type="checkbox"
                checked={selectedProjects.includes(project.projectId)}
                onClick={(event) => event.stopPropagation()} // ✅ 추가: 클릭 이벤트 전파 방지
                onChange={(event) => {
                  event.stopPropagation(); // ✅ 변경 이벤트도 전파 방지
                  handleSelectProject(event, project.projectId);
                }}
              />
              <img
                src={project.thumbnailPath || "/default_project.png"}
                alt={project.name}
                onError={(e) => (e.currentTarget.src = "/default_project.png")}
              />
              <h3>{project.name}</h3>
              <p className="description">{project.description}</p>
            </div>
          ))
        ) : (
          <p>휴지통이 비어 있습니다.</p>
        )}
      </div>

      <style jsx>{`
        .container {
          text-align: center;
          padding: 20px;
          display: flex;
          flex-direction: column;
        }
        header {
          text-align: center; /* ✅ 제목과 설명을 가운데 정렬 */
        }
        .title {
          font-size: 2rem; /* ✅ 제목 크기 키움 */
          font-weight: bold;
          margin-bottom: 10px;
        }
        .description {
          display: -webkit-box;
          -webkit-line-clamp: 2; /* 최대 2줄까지만 표시 */
          -webkit-box-orient: vertical;
          overflow: hidden;
          text-overflow: ellipsis;
          max-height: 3.2em; /* 줄 높이에 맞게 설정 */
          line-height: 1.6em; /* 줄 높이 설정 */
          font-size: 1rem;
          color: #555; /* 가독성을 위한 색상 */
        }
        .top-header {
          display: flex;
          justify-content: flex-end; /* ✅ 오른쪽 정렬 */
          margin-bottom: 15px;
        }
        .button-container {
          display: flex;
          justify-content: center; /* ✅ 버튼을 가운데 정렬 */
          align-items: center;
          gap: 10px; /* ✅ 버튼 간격 동일하게 설정 */
          margin-bottom: 15px;
        }

        .view-projects-button,
        .restore-button,
        .delete-permanently-button {
          background-color: #007bff;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          font-weight: bold;
          border-radius: 5px;
          cursor: pointer;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .restore-button {
          background-color: #28a745; /* ✅ 복원 버튼 (초록색) */
        }

        .delete-permanently-button {
          background-color: #dc3545; /* ✅ 완전 삭제 버튼 (빨간색) */
        }

        .grid {
          display: flex;
          flex-wrap: wrap;
          gap: 20px;
          justify-content: center;
        }
        .card {
          width: 300px;
          border: 1px solid #ddd;
          border-radius: 8px;
          padding: 10px;
          cursor: pointer;
          transition: 0.3s;
          background-color: #fff;
          box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
        }
        .card:hover {
          background-color: #f9f9f9;
        }
        img {
          width: 100%;
          height: 150px;
          object-fit: cover;
          border-radius: 8px;
        }
      `}</style>
    </div>
  );
};

export default TrashProjects;
