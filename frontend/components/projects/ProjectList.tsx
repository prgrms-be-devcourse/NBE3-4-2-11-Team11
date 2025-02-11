"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore"; // ✅ Zustand 스토어 사용
import { getProjects } from "@/lib/projectService";

const ProjectList = () => {
  const router = useRouter();
  const { accessToken, refreshAccessToken } = useAuthStore(); // ✅ Zustand에서 토큰 가져오기
  const [projects, setProjects] = useState<any[]>([]);

  useEffect(() => {
    const fetchProjects = async () => {
      if (!accessToken) {
        console.warn("❌ Access Token 없음, 토큰 갱신 시도");
        const refreshed = await refreshAccessToken();
        if (!refreshed) {
          alert("로그인이 필요합니다.");
          router.push("/login"); // ✅ 로그인 페이지로 이동
          return;
        }
      }

      const res = await getProjects(); // ✅ 토큰 포함하여 API 요청
      console.log("📢 [fetchProjects] API 응답:", res); // 응답 로그 추가

      if (res.code === "200" && res.data) {
        setProjects(res.data);
      } else {
        alert("프로젝트를 불러오는 데 실패했습니다.");
      }
    };

    fetchProjects();
  }, [accessToken]);

  return (
    <div className="container">
      <h1 className="title">프로젝트 모아보기</h1>
      <p className="description">
        그동안 진행한 프로젝트들을 정리해보면서 포트폴리오로 정리해봐요!
      </p>

      <div className="header">
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
            console.log("📢 [ProjectList] 프로젝트 데이터 확인:", project); // ✅ 프로젝트 객체 확인

            return (
              <div
                key={project.projectId} // key값을 정확한 필드명으로 설정
                className="card"
                onClick={() =>
                  router.push(`/mypage/projects/${project.projectId}`)
                } // 클릭 시 해당 ID로 상세페이지로 이동
              >
                <img src={project.imageUrl} alt={project.name} />
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
        .title {
          font-size: 2rem;
          font-weight: bold;
          margin-bottom: 10px;
        }
        .description {
          font-size: 1.1rem;
          color: gray;
          margin-bottom: 20px;
        }
        .header {
          display: flex;
          justify-content: flex-end;
          margin-bottom: 20px;
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
        .add-button:hover {
          background-color: #0056b3;
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
