"use client";

import { useEffect, useState, useCallback } from "react";
import { useParams, useRouter } from "next/navigation";
import { getProjectById, updateProject } from "@/lib/projectService";

const EditProjectPage = () => {
  const { id } = useParams();
  const projectId = id ?? null;
  const router = useRouter();
  const [projectData, setProjectData] = useState<{
    name: string;
    startDate: string;
    endDate: string;
    memberCount: number;
    position: string;
    repositoryLink: string;
    description: string;
    imageUrl: string;
    thumbnailPath: string | null;
    skills: string[]; // ✅ `string[]`으로 변경
    tools: string[]; // ✅ `string[]`으로 변경
  }>({
    name: "",
    startDate: "",
    endDate: "",
    memberCount: 1,
    position: "",
    repositoryLink: "",
    description: "",
    imageUrl: "",
    thumbnailPath: "",
    skills: [], // ✅ 빈 배열이지만 타입이 명확해짐
    tools: [], // ✅ 빈 배열이지만 타입이 명확해짐
  });

  useEffect(() => {
    const fetchProject = async () => {
      if (!projectId) return;
      const res = await getProjectById(projectId); // ✅ 숫자로 변환된 projectId 사용

      if (res.resultCode === "200") {
        console.log("📢 [useEffect] 최신 데이터 적용:", res.data);

        setProjectData({
          ...res.data,
          skills: res.data.skills ?? [], // ✅ `undefined` 방지
          tools: res.data.tools ?? [], // ✅ `undefined` 방지
        });
      } else {
        alert("프로젝트 정보를 불러오는 데 실패했습니다.");
        router.push("/mypage/projects");
      }
    };

    fetchProject();
  }, [projectId]); // ✅ 숫자로 변환된 projectId를 의존성 배열에 추가

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setProjectData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // ✅ 기술 스택 & 사용 도구 목록 가져오기
  const [skillOptions, setSkillOptions] = useState<string[]>([]);
  const [toolOptions, setToolOptions] = useState<string[]>([]);

  const [selectedFile, setSelectedFile] = useState<File | null>(null); // ✅ 파일 상태 추가

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0];
      console.log("📢 [handleFileUpload] 파일이 선택되었습니다:", file.name);
      setSelectedFile(file);
    } else {
      console.warn("⚠️ 파일이 선택되지 않았습니다. selectedFile을 유지합니다.");
    }
  };

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const skillsResponse = await fetch(
          "http://localhost:8080/api/v1/user/resume/skills",
          {
            credentials: "include",
          }
        );
        const skillsData = await skillsResponse.json();
        setSkillOptions(skillsData.data.map((skill: any) => skill.name)); // ✅ name 리스트 저장

        const toolsResponse = await fetch(
          "http://localhost:8080/api/v1/user/resume/tools",
          {
            credentials: "include",
          }
        );
        const toolsData = await toolsResponse.json();
        setToolOptions(toolsData.data.map((tool: any) => tool.name)); // ✅ name 리스트 저장
      } catch (error) {
        console.error("❌ [fetchOptions] 데이터 로딩 실패:", error);
      }
    };

    fetchOptions();
  }, []);

  // ✅ 기술 스택 추가 (id 대신 name 저장)
  const handleSkillChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedName = event.target.value;
    if (!selectedName) return;

    if (!projectData.skills.includes(selectedName)) {
      setProjectData((prev) => ({
        ...prev,
        skills: [...prev.skills, selectedName],
      }));
    }
  };

  // ✅ 사용 도구 추가 (id 대신 name 저장)
  const handleToolChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedName = event.target.value;
    if (!selectedName) return;

    if (!projectData.tools.includes(selectedName)) {
      setProjectData((prev) => ({
        ...prev,
        tools: [...prev.tools, selectedName],
      }));
    }
  };

  // ✅ 기술 스택 삭제
  const handleRemoveSkill = (skillName: string) => {
    setProjectData((prev) => ({
      ...prev,
      skills: prev.skills.filter((skill) => skill !== skillName),
    }));
  };

  // ✅ 사용 도구 삭제
  const handleRemoveTool = (toolName: string) => {
    setProjectData((prev) => ({
      ...prev,
      tools: prev.tools.filter((tool) => tool !== toolName),
    }));
  };

  // ✅ 최신 상태가 반영되었는지 확인
  useEffect(() => {
    console.log("📢 [useEffect] 현재 skills 상태:", projectData.skills);
    console.log("📢 [useEffect] 현재 tools 상태:", projectData.tools);
  }, [projectData.skills, projectData.tools]);

  const handleSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();

      console.log("📢 [handleSubmit] 현재 selectedFile 상태:", selectedFile);

      if (!projectId) {
        alert("❌ 프로젝트 ID가 존재하지 않습니다!");
        return;
      }

      // 📢 디버깅 추가: 현재 프로젝트 데이터 확인
      console.log("📢 [handleSubmit] 프로젝트 데이터:", projectData);

      // ✅ 새로운 썸네일이 있는 경우에만 `selectedFile`을 전달
      const updatedProject = await updateProject(
        String(projectId),
        projectData,
        selectedFile && selectedFile.size > 0 ? selectedFile : undefined
      );

      console.log("📢 [handleSubmit] API 응답:", updatedProject);

      if (
        updatedProject.resultCode === "200" ||
        updatedProject.resultCode === "201"
      ) {
        alert("✅ 프로젝트가 성공적으로 수정되었습니다!");
        router.push(`/mypage/projects/${projectId}`);
      } else {
        alert("❌ 프로젝트 수정 실패!");
      }
    },
    [projectId, selectedFile, projectData, router]
  );

  return (
    <div className="form-container">
      <h1>프로젝트 수정</h1>
      <form onSubmit={handleSubmit}>
        <label>프로젝트 제목</label>
        <input
          type="text"
          name="name"
          value={projectData.name}
          onChange={handleChange}
          required
        />
        <hr className="divider" />

        {/* ✅ 썸네일 이미지 미리보기 및 업로드 */}
        <label>썸네일 이미지</label>

        <div className="thumbnail-preview">
          <img
            src={
              selectedFile
                ? URL.createObjectURL(selectedFile) // ✅ 새 파일 업로드 시 미리보기
                : projectData.thumbnailPath
                ? projectData.thumbnailPath.startsWith("http")
                  ? projectData.thumbnailPath
                  : `http://localhost:8080/uploads/${projectData.thumbnailPath}`
                : "/default_project.png" // ✅ 기본 썸네일 유지
            }
            alt="썸네일 미리보기"
            className="thumbnail-image"
          />
        </div>

        {/* ✅ 썸네일 삭제 버튼 */}
        {(projectData.thumbnailPath || selectedFile) && (
          <div className="thumbnail-delete-container">
            <button
              className="delete-thumbnail-button"
              type="button"
              onClick={() => {
                setProjectData((prev) => ({
                  ...prev,
                  thumbnailPath: null, // ✅ 기존 썸네일 삭제
                }));
                setSelectedFile(null);
              }}
            >
              ❌
            </button>
            <button
              className="delete-thumbnail-button-text"
              type="button"
              onClick={() => {
                setProjectData((prev) => ({
                  ...prev,
                  thumbnailPath: null, // ✅ 기존 썸네일 삭제
                }));
                setSelectedFile(null);
              }}
            >
              삭제
            </button>
          </div>
        )}

        {/* ✅ 파일 업로드 버튼 (삭제 후에도 활성화) */}
        <input
          type="file"
          accept="image/*"
          onChange={handleFileUpload}
          disabled={selectedFile !== null} // 파일 업로드 시 버튼 비활성화
        />

        <hr className="divider" />

        <label>시작 날짜</label>
        <input
          type="date"
          name="startDate"
          value={projectData.startDate}
          onChange={handleChange}
          required
        />
        <hr className="divider" />

        <label>종료 날짜</label>
        <input
          type="date"
          name="endDate"
          value={projectData.endDate}
          onChange={handleChange}
          required
        />
        <hr className="divider" />

        <label>멤버 수</label>
        <input
          type="number"
          name="memberCount"
          value={projectData.memberCount}
          onChange={handleChange}
          required
        />
        <hr className="divider" />

        <label>포지션</label>
        <input
          type="text"
          name="position"
          value={projectData.position}
          onChange={handleChange}
          required
        />
        <hr className="divider" />

        <label>GitHub 링크</label>
        <input
          type="text"
          name="repositoryLink"
          value={projectData.repositoryLink}
          onChange={handleChange}
        />
        <hr className="divider" />

        <label>설명</label>
        <textarea
          name="description"
          value={projectData.description}
          onChange={handleChange}
          rows={20}
          className="textarea-field"
          required
        />
        <hr className="divider" />

        <label>이미지 URL</label>
        <input
          type="text"
          name="imageUrl"
          value={projectData.imageUrl}
          onChange={handleChange}
          required
        />
        <hr className="divider" />

        <label>기술 스택</label>
        <select onChange={handleSkillChange} className="dropdown">
          <option value="">기술 스택 선택</option>
          {skillOptions.map((skill) => (
            <option key={skill} value={skill}>
              {skill}
            </option>
          ))}
        </select>
        <div className="selected-list">
          {projectData.skills.map((skill) => (
            <div key={skill} className="selected-item">
              <span>{skill}</span>
              <button
                onClick={() => handleRemoveSkill(skill)}
                className="remove-button"
              >
                ×
              </button>
            </div>
          ))}
        </div>

        <hr className="divider" />

        <label>사용 도구</label>
        <select onChange={handleToolChange} className="dropdown">
          <option value="">사용 도구 선택</option>
          {toolOptions.map((tool) => (
            <option key={tool} value={tool}>
              {tool}
            </option>
          ))}
        </select>
        <div className="selected-list">
          {projectData.tools.map((tool) => (
            <div key={tool} className="selected-item">
              <span>{tool}</span>
              <button
                onClick={() => handleRemoveTool(tool)}
                className="remove-button"
              >
                ×
              </button>
            </div>
          ))}
        </div>

        {/* 수정 완료 버튼과 취소 버튼을 중앙에 배치 */}
        <div className="button-container">
          <button type="submit" className="save-button">
            수정 완료
          </button>
          <button
            type="button"
            className="cancel-button"
            onClick={() => router.push(`/mypage/projects/${id}`)}
          >
            취소
          </button>
        </div>
      </form>

      <style jsx>{`
        .form-container {
          max-width: 700px;
          margin: 0 auto;
          padding: 30px;
          background: #f9f9f9;
          border-radius: 10px;
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        h1 {
          font-size: 2rem;
          font-weight: bold;
          text-align: center;
          margin-bottom: 20px;
        }
        .divider {
          border-top: 1px solid #ccc;
          margin: 20px 0;
        }
        img {
          width: 100px;
          height: auto;
          border-radius: 5px;
          margin-top: 10px;
        }
        label {
          display: block;
          margin-bottom: 5px;
          font-weight: bold;
        }
        input,
        textarea {
          width: 100%;
          padding: 8px;
          margin-bottom: 20px;
          border: 1px solid #ccc;
          border-radius: 5px;
          box-sizing: border-box;
        }
        .textarea-field {
          width: 100%;
          min-height: 300px; /* ✅ 최소 높이 설정 */
          max-height: 600px; /* ✅ 최대 높이 설정 */
          padding: 10px;
          font-size: 1rem;
          border: 1px solid #ccc;
          border-radius: 5px;
          resize: vertical; /* ✅ 사용자가 크기 조절 가능 */
        }
        .section-divider {
          border-top: 2px solid #ccc; /* ✅ 구분선 스타일 */
          margin: 20px 0;
        }
        .dropdown {
          width: 100%;
          padding: 10px;
          font-size: 1rem;
          border: 1px solid #ccc;
          border-radius: 5px;
          background-color: #ffffff; /* ✅ 드롭다운 내부를 완전히 흰색으로 */
          color: #333; /* ✅ 텍스트 색상을 진한 회색으로 */
          appearance: none; /* ✅ 기본 브라우저 스타일 제거 */
          cursor: pointer;
        }

        .dropdown:focus {
          border-color: #007bff; /* ✅ 선택 시 테두리를 파란색으로 */
          outline: none;
        }

        /* 드롭다운 리스트 스타일 */
        option {
          background-color: #ffffff; /* ✅ 옵션 배경도 흰색으로 */
          color: #333; /* ✅ 옵션 글씨도 진한 회색 */
        }

        .selected-list {
          display: flex;
          flex-wrap: wrap;
          gap: 5px;
          margin-top: 10px;
        }

        .selected-item {
          display: flex;
          align-items: center;
          background: #e6f0ff;
          padding: 5px 10px;
          border-radius: 5px;
          white-space: nowrap;
        }

        .remove-button {
          background: none;
          border: none;
          color: red;
          font-size: 1.2rem;
          cursor: pointer;
          margin-left: 5px;
        }
        .button-container {
          display: flex;
          justify-content: center;
          gap: 20px;
          margin-top: 20px;
        }
        .save-button {
          background-color: #007bff;
          color: white;
          border: none;
          padding: 10px 20px;
          font-size: 1rem;
          font-weight: bold;
          cursor: pointer;
          border-radius: 5px;
        }
        .save-button:hover {
          background-color: #0056b3;
        }
        .cancel-button {
          background-color: #dc3545;
          color: white;
          border: none;
          padding: 10px 20px;
          font-size: 1rem;
          font-weight: bold;
          cursor: pointer;
          border-radius: 5px;
        }
        .cancel-button:hover {
          background-color: #c82333;
        }
      `}</style>
    </div>
  );
};

export default EditProjectPage;
