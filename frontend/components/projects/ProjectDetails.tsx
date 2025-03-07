"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getProjectById, deleteProject } from "@/lib/projectService";
import ReactMarkdown from "react-markdown";
import rehypeSanitize from "rehype-sanitize";
import { moveToTrash } from "@/lib/projectService";

const ProjectDetails = () => {
  const params = useParams();
  const router = useRouter();
  const [project, setProject] = useState<any>(null);
  const [isEditing, setIsEditing] = useState(false); // ✅ 수정 모드 상태 추가
  const [updatedProject, setUpdatedProject] = useState<any>({}); // ✅ 수정 데이터 상태 추가

  const projectId = params?.id; // ✅ `useParams()`가 undefined 반환할 가능성 방지
  console.log("📢 useParams()에서 가져온 ID:", projectId);

  useEffect(() => {
    if (!projectId) {
      console.error("❌ 프로젝트 ID가 존재하지 않습니다.");
      return;
    }

    const fetchProject = async () => {
      console.log(`📢 [ProjectDetails] API 요청을 보낼 ID: ${projectId}`);

      const res = await getProjectById(projectId);
      console.log("📢 [ProjectDetails] API 응답 데이터:", res);

      if (res && res.resultCode === "200" && res.data) {
        setProject(res.data); // ✅ `data.data` 대신 `data` 직접 저장
        console.log("📢 [ProjectDetails] 상태 업데이트 완료:", res.data);
      } else {
        console.error(`❌ 프로젝트 조회 실패. 오류 메시지: ${res?.message}`);
        alert(
          `프로젝트 정보를 불러오는 데 실패했습니다. (오류 코드: ${res?.resultCode})`
        );
        router.push("/mypage/projects");
      }
    };

    fetchProject();
  }, [projectId]);

  useEffect(() => {
    console.log("📢 상태 변경 감지됨! project:", project);
  }, [project]);

  // ✅ 프로젝트 수정 핸들러
  const handleUpdate = async () => {
    if (!projectId) return;

    console.log("🚀 [handleUpdate] 수정할 프로젝트 데이터:", updatedProject);

    const res = await updatedProject(projectId, updatedProject);
    console.log("📢 [handleUpdate] API 응답 데이터:", res);

    if (res.code === "200") {
      alert("프로젝트가 성공적으로 수정되었습니다!");
      setIsEditing(false);
      setProject(updatedProject); // ✅ UI에 수정된 내용 반영
    } else {
      alert(`❌ 수정 실패: ${res.message}`);
    }
  };

  // ✅ 프로젝트 삭제 핸들러
  const handleDelete = async () => {
    if (!projectId) return;
    if (!confirm("정말로 이 프로젝트를 삭제하시겠습니까?")) return;

    console.log("🚀 [handleDelete] 삭제할 프로젝트 ID:", projectId);

    try {
      const res = await moveToTrash([project.projectId]); // ✅ 단건 삭제 → 휴지통 이동
      if (res.code === "200") {
        alert("프로젝트가 휴지통으로 이동되었습니다.");
        router.push("/mypage/projects"); // ✅ 삭제 후 전체 프로젝트 페이지로 이동
      } else {
        alert(`삭제 실패: ${res.message}`);
      }
    } catch (error) {
      console.error("❌ [handleDelete] 삭제 중 오류 발생:", error);
      alert("서버 오류가 발생했습니다.");
    }
  };

  if (!project) return <p>로딩 중...</p>;

  // ✅ 대표 이미지 우선순위: thumbnailPath > imageUrl > 기본 이미지
  const thumbnail = project.thumbnailPath
    ? project.thumbnailPath.startsWith("http")
      ? project.thumbnailPath
      : `http://localhost:8080/uploads/${project.thumbnailPath}`
    : "/default_project.png"; // ✅ 기본 썸네일 적용

  const additionalImage = project.imageUrl ? project.imageUrl : null;

  return (
    <div className="container">
      <button
        className="back-button"
        onClick={() => router.push("/mypage/projects")}
      >
        ← 목록으로 돌아가기
      </button>

      <h1 className="project-title">{project.name}</h1>
      <hr className="divider" />
      <div className="info-section">
        <p className="label">설명:</p>
        <div className="markdown-content">
          <ReactMarkdown rehypePlugins={[rehypeSanitize]}>
            {project.description}
          </ReactMarkdown>{" "}
        </div>
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
        {/* ✅ 썸네일 이미지 */}
        <img
          src={thumbnail}
          alt="대표 이미지"
          className="thumbnail"
          onError={(e) => (e.currentTarget.src = "/default_project.png")} // 로드 실패 시 기본 이미지
        />

        {/* ✅ 추가 이미지 (imageUrl) */}
        {additionalImage && (
          <img
            src={additionalImage}
            alt="추가 이미지"
            className="additional-image"
            onError={(e) => (e.currentTarget.style.display = "none")} // 로드 실패 시 숨김 처리
          />
        )}
      </div>

      <div className="button-group">
        <button
          className="edit-button"
          onClick={() => router.push(`/mypage/projects/${projectId}/edit`)}
        >
          수정
        </button>
        <button className="delete-button" onClick={handleDelete}>
          🗑️ 삭제
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
        .back-button {
          display: inline-block;
          margin-bottom: 15px;
          padding: 8px 12px;
          font-size: 1rem;
          border-radius: 5px;
          border: none;
          background-color: #f3f3f3;
          cursor: pointer;
          transition: background 0.2s;
        }
        .back-button:hover {
          background-color: #e0e0e0;
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
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          gap: 10px;
          margin-top: 20px;
        }
        .thumbnail {
          width: 150px;
          height: auto;
          border-radius: 5px;
          border: 1px solid #ddd;
        }
        .additional-image {
          width: 300px;
          height: auto;
          border-radius: 5px;
          border: 1px solid #ddd;
        }
        img {
          max-width: 100%;
          height: auto;
          border-radius: 5px;
        }
        .markdown-content img {
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
