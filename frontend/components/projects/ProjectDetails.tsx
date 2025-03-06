"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getProjectById, deleteProject } from "@/lib/projectService";

const ProjectDetails = () => {
  const { id } = useParams();
  const router = useRouter();
  const [project, setProject] = useState<any>(null);

  useEffect(() => {
    console.log("📢 useParams()에서 가져온 ID:", id);

    if (!id) {
      console.error("❌ 프로젝트 ID가 존재하지 않습니다.");
      return;
    }

    const fetchProject = async () => {
      console.log(`📢 [ProjectDetails] API 요청을 보낼 ID: ${id}`);

      const res = await getProjectById(id);
      console.log("📢 [ProjectDetails] API 응답 데이터:", res);

      if (res && res.resultCode === "200" && res.data) {
        setProject(res.data.data);
        console.log("📢 [ProjectDetails] 상태 업데이트 요청됨:", res.data);
      } else {
        alert(
          `프로젝트 정보를 불러오는 데 실패했습니다. (오류 코드: ${res?.resultCode})`
        );
        console.error(`❌ 프로젝트 조회 실패. 오류 메시지: ${res?.message}`);
        router.push("/mypage/projects");
      }
    };

    fetchProject();
  }, [id]);

  useEffect(() => {
    console.log("📢 상태 변경 감지됨! project:", project);
  }, [project]);

  const handleDelete = async () => {
    if (confirm("정말로 이 프로젝트를 삭제하시겠습니까?")) {
      await deleteProject(id);
      router.push("/mypage/projects");
    }
  };

  if (!project) return <p>로딩 중...</p>;

  // ✅ 대표 이미지 우선순위: thumbnailPath > imageUrl
  const imageUrl = project.thumbnailPath
    ? project.thumbnailPath.startsWith("http")
      ? project.thumbnailPath
      : `http://localhost:8080/uploads/${project.thumbnailPath}` // ✅ 로컬 경로라면 URL 변환
    : project.imageUrl;

  return (
    <div className="container">
      <h1 className="project-title">{project.name}</h1>
      <hr className="divider" />
      <div className="info-section">
        <p>
          <span className="label">설명:</span> {project.description}
        </p>
        <hr className="small-divider" />
        <p>
          <span className="label">시작 날짜:</span> {project.startDate}
        </p>
        <p>
          <span className="label">종료 날짜:</span> {project.endDate}
        </p>
        <hr className="small-divider" />
        <p>
          <span className="label">포지션:</span> {project.position}
        </p>
        <hr className="small-divider" />
        <p>
          <span className="label">멤버 수:</span> {project.memberCount}
        </p>
        <hr className="small-divider" />
        <p>
          <span className="label">GitHub 링크:</span>{" "}
          <a
            href={project.repositoryLink}
            target="_blank"
            rel="noopener noreferrer"
          >
            {project.repositoryLink}
          </a>
        </p>
      </div>

      <div className="info-section">
        <p className="label">기술 스택:</p>
        <ul className="list">
          {project.skills && project.skills.length > 0 ? (
            project.skills.map((skill: string, index: number) => (
              <li key={index}>{skill}</li>
            ))
          ) : (
            <p>등록된 기술 스택이 없습니다.</p>
          )}
        </ul>
      </div>

      <div className="info-section">
        <p className="label">사용 도구:</p>
        <ul className="list">
          {project.tools && project.tools.length > 0 ? (
            project.tools.map((tool: string, index: number) => (
              <li key={index}>{tool}</li>
            ))
          ) : (
            <p>등록된 도구가 없습니다.</p>
          )}
        </ul>
      </div>

      <div className="image-container">
        <img src={imageUrl} alt="프로젝트 이미지" />
      </div>

      <div className="button-group">
        <button
          className="edit-button"
          onClick={() => router.push(`/mypage/projects/${id}/edit`)}
        >
          수정
        </button>
        <button className="delete-button" onClick={handleDelete}>
          삭제
        </button>
      </div>

      <style jsx>{`
        .container {
          max-width: 700px;
          margin: 0 auto;
          padding: 30px;
          background: #f9f9f9;
          border-radius: 10px;
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .project-title {
          font-size: 2rem;
          font-weight: bold;
          text-align: center;
          margin-bottom: 10px;
        }
        .divider {
          border: 0;
          height: 2px;
          background: #ddd;
          margin-bottom: 20px;
        }
        .small-divider {
          border: 0;
          height: 1px;
          background: #ddd;
          margin: 8px 0;
        }
        .info-section {
          margin-bottom: 15px;
          padding: 12px;
          background: white;
          border-radius: 5px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .label {
          font-weight: bold;
          color: #333;
          font-size: 1.1rem;
        }
        .list {
          padding-left: 20px;
        }
        .image-container {
          text-align: center;
          margin-top: 20px;
        }
        img {
          max-width: 100%;
          height: auto;
          border-radius: 5px;
        }
        .button-group {
          display: flex;
          justify-content: center;
          gap: 15px;
          margin-top: 20px;
        }
        .edit-button {
          background-color: #007bff;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
        }
        .edit-button:hover {
          background-color: #0056b3;
        }
        .delete-button {
          background-color: #dc3545;
          color: white;
          border: none;
          padding: 10px 15px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
        }
        .delete-button:hover {
          background-color: #c82333;
        }
      `}</style>
    </div>
  );
};

export default ProjectDetails;
