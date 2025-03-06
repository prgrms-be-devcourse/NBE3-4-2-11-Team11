"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { getProjects } from "@/lib/projectService";
import { Search } from "lucide-react";
import { moveToTrash } from "@/lib/projectService";

const ProjectList = () => {
  const router = useRouter();
  const [projects, setProjects] = useState<any[]>([]);
  const [selectedProjects, setSelectedProjects] = useState<string[]>([]);
  const [searchKeyword, setSearchKeyword] = useState<string>("");
  const [isSelecting, setIsSelecting] = useState<boolean>(false);

  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);
  const [role, setRole] = useState<string | null>(null);

  useEffect(() => {
    const checkAuthAndFetchProjects = async () => {
      try {
        console.log("🚀 `/api/v1/auth/status` API 요청 시작");
        const authResponse = await axios.get("/api/v1/auth/status", {
          withCredentials: true,
        });

        console.log("✅ `/api/v1/auth/status` 응답 데이터:", authResponse.data);

        if (authResponse.data.isLoggedIn) {
          setIsAuthenticated(true);
          setRole(authResponse.data.role);
        } else {
          setIsAuthenticated(false);
          setRole(null);
        }

        if (authResponse.data.isLoggedIn) {
          console.log("🚀 프로젝트 목록 API 요청 시작");

          const res = await getProjects(searchKeyword); // ✅ 검색어 적용

          console.log("📢 [fetchProjects] API 응답:", res);
          if (res.code === "200" && res.data) {
            setProjects(res.data);
          } else {
            alert("프로젝트를 불러오는 데 실패했습니다.");
          }
        } else {
          console.error("❌ 로그인 상태 아님 → 로그인 페이지로 이동");
          alert("로그인이 필요합니다.");
          router.push("/login");
        }
      } catch (error) {
        console.error(
          "❌ 로그인 상태 확인 또는 프로젝트 목록 가져오기 실패:",
          error.response ? error.response.data : error.message
        );
        alert("인증 정보를 확인하는 중 오류가 발생했습니다.");
        router.push("/login");
      }
    };

    checkAuthAndFetchProjects();
  }, [searchKeyword]); // ✅ 검색어 변경 시 API 다시 호출

  // ✅ 체크박스 선택 핸들러
  const handleSelectProject = (projectId: string) => {
    setSelectedProjects((prev) =>
      prev.includes(projectId)
        ? prev.filter((id) => id !== projectId)
        : [...prev, projectId]
    );
  };

  // ✅ 휴지통 버튼 클릭 시 체크박스 활성화
  const toggleSelectionMode = () => {
    setIsSelecting((prev) => !prev);
    setSelectedProjects([]); // ✅ 선택 목록 초기화
  };

  // ✅ 선택한 프로젝트를 휴지통으로 이동
  const handleMoveToTrash = async () => {
    if (selectedProjects.length === 0) {
      alert("이동할 프로젝트를 선택하세요!");
      return;
    }

    if (!confirm("선택한 프로젝트를 휴지통으로 이동하시겠습니까?")) return;

    console.log("🚀 [handleMoveToTrash] 선택한 프로젝트:", selectedProjects);

    const res = await moveToTrash(selectedProjects); // ✅ API 호출

    console.log("📢 [handleMoveToTrash] 응답 데이터:", res);

    if (res.code === "401") {
      alert("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
      window.location.href = "/login";
      return;
    }

    if (res.code === "200") {
      alert("휴지통으로 이동 완료!");
      setProjects((prevProjects) =>
        prevProjects.filter((p) => !selectedProjects.includes(p.projectId))
      ); // ✅ UI에서 제거
      setSelectedProjects([]); // ✅ 선택 초기화
    } else {
      alert(`오류 발생: ${res.message}`);
    }
  };

  return (
    <div className="container">
      <div className="header">
        {/* ✅ "선택 취소" 버튼과 "선택한 프로젝트 휴지통 이동" 버튼 */}
        {isSelecting ? (
          <div className="selection-container">
            <button className="cancel-button" onClick={toggleSelectionMode}>
              ❌ 선택 취소
            </button>
            <button
              className="confirm-trash-button"
              onClick={handleMoveToTrash}
              disabled={selectedProjects.length === 0}
            >
              🗑️ 선택한 프로젝트 휴지통 이동
            </button>
          </div>
        ) : (
          <button className="trash-button" onClick={toggleSelectionMode}>
            🗑️ 휴지통으로 이동
          </button>
        )}

        {/* ✅ 프로젝트 검색을 중앙에 위치 */}
        <div className="search-container">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            placeholder="프로젝트 검색..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
        </div>

        {/* ✅ 기존 "새 프로젝트 추가" 버튼 유지 */}
        <button
          className="add-button"
          onClick={() => router.push("/mypage/projects/new")}
        >
          + 새 프로젝트 추가
        </button>
      </div>

      <div className="grid">
        {projects.length > 0 ? (
          projects.map((project: any) => {
            return (
              <div key={project.projectId} className="card">
                {/* ✅ 체크박스는 선택 모드일 때만 표시 */}
                {isSelecting && (
                  <input
                    type="checkbox"
                    checked={selectedProjects.includes(project.projectId)}
                    onChange={() => handleSelectProject(project.projectId)}
                  />
                )}
                <img
                  src={project.thumbnailPath || project.imageUrl}
                  alt={project.name}
                  onError={(e) => (e.currentTarget.src = "/fallback-image.jpg")}
                />
                <h3>{project.name}</h3>
                <p>{project.description}</p>
              </div>
            );
          })
        ) : (
          <p>등록된 프로젝트가 없습니다.</p>
        )}
      </div>

      <style jsx>{`
        .container {
          text-align: center;
          padding: 20px;
        }
        .header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
          position: relative;
        }
        .selection-container {
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          gap: 10px;
        }
        .search-container {
          display: flex;
          align-items: center;
          border: 1px solid #ddd;
          border-radius: 5px;
          padding: 5px;
          width: 300px;
          justify-content: center;
          position: absolute;
          left: 50%;
          transform: translateX(-50%); /* ✅ 프로젝트 검색을 중앙 정렬 */
        }
        .search-icon {
          color: gray;
          margin-left: 5px;
        }
        .search-container input {
          border: none;
          outline: none;
          font-size: 1rem;
          text-align: center;
          width: 200px;
        }
        .trash-button {
          background-color: #ff4500;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
        }
        .cancel-button {
          background-color: #6c757d;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
        }
        .confirm-trash-button {
          background-color: #d32f2f;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
        }
        .add-button {
          background-color: #007bff;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
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

export default ProjectList;
