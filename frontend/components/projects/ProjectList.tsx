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
      } catch (error: any) {
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
  const handleSelectProject = (event: React.MouseEvent, projectId: string) => {
    event.stopPropagation(); // ✅ 상세보기 이동 차단
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

    const res = await moveToTrash(selectedProjects);

    if (res.code === "200") {
      alert("휴지통으로 이동 완료!");
      setProjects((prevProjects) =>
        prevProjects.filter((p) => !selectedProjects.includes(p.projectId))
      );
      setSelectedProjects([]); // ✅ 선택 초기화

      // ✅ 휴지통 목록 페이지로 이동
      router.push("/mypage/projects/trash");
    } else {
      alert(`오류 발생: ${res.message}`);
    }
  };

  return (
    <div className="container">
      <div className="header">
        {/* 왼쪽: 휴지통으로 이동 버튼 */}
        {isSelecting ? (
          <div>
            <button
              className="cancel-button"
              onClick={() => setIsSelecting(false)}
            >
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
          <button className="trash-button" onClick={() => setIsSelecting(true)}>
            🗑️ 휴지통으로 이동
          </button>
        )}

        {/* 가운데: 검색창 */}
        <div className="search-container">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            placeholder="프로젝트 검색..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
        </div>

        {/* 오른쪽: 새 프로젝트 추가 버튼 👀 수정된 부분 */}
        <button
          className="add-button"
          onClick={() => router.push("/mypage/projects/new")}
        >
          + 새 프로젝트 추가
        </button>

        <button
          className="trash-list-button"
          onClick={() => router.push("/mypage/projects/trash")}
        >
          🗑️ 휴지통 목록 보기
        </button>
      </div>

      <div className="grid">
        {projects.length > 0 ? (
          projects.map((project: any) => (
            <div
              key={project.projectId}
              className="card"
              onClick={() =>
                router.push(`/mypage/projects/${project.projectId}`)
              }
              style={{ cursor: "pointer" }}
            >
              {/* ✅ 체크박스 클릭 시 상세 조회로 이동하지 않도록 수정 */}
              {isSelecting && (
                <input
                  type="checkbox"
                  checked={selectedProjects.includes(project.projectId)}
                  onClick={(event) => event.stopPropagation()} // ✅ 추가: 클릭 이벤트 전파 방지
                  onChange={(event) => {
                    event.stopPropagation(); // ✅ 변경 이벤트도 전파 방지
                    handleSelectProject(event, project.projectId);
                  }}
                />
              )}
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
          flex-wrap: wrap;
          justify-content: space-between;
          align-items: center;
          gap: 10px;
          padding: 10px;
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
        .search-container {
          flex-grow: 1;
          max-width: 300px;
          display: flex;
          align-items: center;
          border: 1px solid #ddd;
          border-radius: 5px;
          padding: 5px;
        }
        .search-icon {
          color: gray;
          margin-left: 5px;
        }
        .search-container input {
          flex-grow: 1;
          border: none;
          outline: none;
          font-size: 1rem;
          text-align: center;
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
          margin-left: auto;
          background-color: #007bff;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
        }
        .trash-list-button {
          background-color: #6c757d;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
          margin-left: 10px;
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
