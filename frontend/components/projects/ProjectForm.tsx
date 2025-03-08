"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { createProject } from "@/lib/projectService";

interface Skill {
  id: number;
  name: string;
}

interface Tool {
  id: number;
  name: string;
}

const ProjectForm: React.FC = () => {
  const router = useRouter();

  const [formData, setFormData] = useState({
    name: "",
    startDate: "",
    endDate: "",
    memberCount: 1,
    position: "",
    repositoryLink: "",
    description: "",
    imageUrl: "",
    thumbnailPath: "",
    skills: [] as Skill[],
    tools: [] as Tool[],
  });

  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const [skillOptions, setSkillOptions] = useState<Skill[]>([]);
  const [toolOptions, setToolOptions] = useState<Tool[]>([]);

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const skillsResponse = await fetch(
          "http://localhost:8080/api/v1/user/resume/skills",
          { credentials: "include" }
        );
        const skillsData = await skillsResponse.json();
        setSkillOptions(skillsData.data);

        const toolsResponse = await fetch(
          "http://localhost:8080/api/v1/user/resume/tools",
          { credentials: "include" }
        );
        const toolsData = await toolsResponse.json();
        setToolOptions(toolsData.data);
      } catch (error) {
        console.error("❌ [fetchOptions] 데이터 로딩 실패:", error);
      }
    };

    fetchOptions();
  }, []);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSkillChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedId = Number(event.target.value);
    const selectedSkill = skillOptions.find((skill) => skill.id === selectedId);
    if (selectedSkill && !formData.skills.some((s) => s.id === selectedId)) {
      setFormData((prev) => ({
        ...prev,
        skills: [...prev.skills, { id: selectedId, name: selectedSkill.name }],
      }));
    }
  };

  const handleToolChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedId = Number(event.target.value);
    const selectedTool = toolOptions.find((tool) => tool.id === selectedId);
    if (selectedTool && !formData.tools.some((t) => t.id === selectedId)) {
      setFormData((prev) => ({
        ...prev,
        tools: [...prev.tools, { id: selectedId, name: selectedTool.name }],
      }));
    }
  };

  const handleRemoveSkill = (skillId: number) => {
    setFormData((prev) => ({
      ...prev,
      skills: prev.skills.filter((skill) => skill.id !== skillId),
    }));
  };

  const handleRemoveTool = (toolId: number) => {
    setFormData((prev) => ({
      ...prev,
      tools: prev.tools.filter((tool) => tool.id !== toolId),
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // ✅ 프로젝트 데이터 JSON 변환
    const projectData = {
      ...formData,
      skills: formData.skills.map((skill) => skill.name),
      tools: formData.tools.map((tool) => tool.name),
    };

    console.log("📢 [handleSubmit] 변환된 요청 데이터:", projectData);

    // ✅ FormData 생성
    const formDataObj = new FormData();
    const jsonBlob = new Blob([JSON.stringify(projectData)], {
      type: "application/json",
    });
    formDataObj.append("projectRequest", jsonBlob);

    // ✅ 파일이 있으면 `thumbnail` 필드로 추가
    if (selectedFile) {
      formDataObj.append("thumbnail", selectedFile);
    }

    console.log("📢 [handleSubmit] 최종 전송 데이터:", formDataObj);

    const response = await createProject(formDataObj);

    if (response.resultCode === "201") {
      alert("🎉 프로젝트가 성공적으로 등록되었습니다!");
      router.push("/mypage/projects");
    } else {
      alert(`❌ 프로젝트 등록 실패: ${response.message}`);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="project-form">
      <div className="input-group">
        <label className="input-label">썸네일 이미지</label>
        <input type="file" accept="image/*" onChange={handleFileUpload} />
        {formData.thumbnailPath && (
          <img
            src={formData.thumbnailPath}
            alt="썸네일 미리보기"
            style={{ width: "100px", marginTop: "10px" }}
          />
        )}
      </div>

      <div className="input-group">
        <label className="input-label">프로젝트 이름</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
        />
      </div>

      <div className="input-group">
        <label className="input-label">시작 날짜</label>
        <input
          type="date"
          name="startDate"
          value={formData.startDate}
          onChange={handleChange}
          required
        />
      </div>

      <div className="input-group">
        <label className="input-label">종료 날짜</label>
        <input
          type="date"
          name="endDate"
          value={formData.endDate}
          onChange={handleChange}
          required
        />
      </div>

      <div className="input-group">
        <label className="input-label">멤버 수</label>
        <input
          type="number"
          name="memberCount"
          value={formData.memberCount}
          onChange={handleChange}
          min="1"
          step="1" // ✅ 숫자 증가/감소 가능하게 설정
          required
        />
      </div>

      <div className="input-group">
        <label className="input-label">포지션</label>
        <input
          type="text"
          name="position"
          value={formData.position}
          onChange={handleChange}
          required
        />
      </div>

      <div className="input-group">
        <label className="input-label">GitHub 링크</label>
        <input
          type="text"
          name="repositoryLink"
          value={formData.repositoryLink}
          onChange={handleChange}
        />
      </div>

      <div className="input-group">
        <label className="input-label">프로젝트 설명</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          rows={20} // ✅ 기본 6줄로 넉넉하게 설정
          className="textarea-field"
          required
        />
      </div>

      <div className="input-group">
        <label className="input-label">이미지 URL</label>
        <input
          type="text"
          name="imageUrl"
          value={formData.imageUrl}
          onChange={handleChange}
          required
        />
      </div>

      <div className="input-group">
        <label className="input-label">기술 스택</label>
        <div className="selection-container">
          <select onChange={handleSkillChange} className="input-field">
            <option value="">스킬 선택</option>
            {skillOptions.map((skill) => (
              <option key={skill.id} value={skill.id}>
                {skill.name}
              </option>
            ))}
          </select>
          <div className="selected-list">
            {formData.skills.map((skill) => (
              <div key={skill.id} className="selected-item">
                <span>{skill.name}</span>
                <button
                  onClick={() => handleRemoveSkill(skill.id)}
                  className="remove-button"
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="input-group">
        <label className="input-label">사용 툴</label>
        <div className="selection-container">
          <select onChange={handleToolChange} className="input-field">
            <option value="">툴 선택</option>
            {toolOptions.map((tool) => (
              <option key={tool.id} value={tool.id}>
                {tool.name}
              </option>
            ))}
          </select>
          <div className="selected-list">
            {formData.tools.map((tool) => (
              <div key={tool.id} className="selected-item">
                <span>{tool.name}</span>
                <button
                  onClick={() => handleRemoveTool(tool.id)}
                  className="remove-button"
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="button-group">
        <button type="submit" className="save-button">
          프로젝트 생성
        </button>
        <button
          type="button"
          className="cancel-button"
          onClick={() => router.push("/mypage/projects")}
        >
          취소
        </button>
      </div>

      <style jsx>{`
        .project-form {
          max-width: 500px;
          margin: 0 auto;
          padding: 20px;
        }
        .input-group {
          margin-bottom: 20px;
          padding-bottom: 10px;
          border-bottom: 1px solid #ccc;
        }
        .input-label {
          font-weight: bold;
          display: block;
          margin-bottom: 5px;
        }
        input,
        textarea {
          width: 100%;
          padding: 8px;
          border: none;
          outline: none;
        }

        /* ✅ 멤버 수 숫자 입력 필드 스타일 */
        .member-count-input {
          width: 60px;
          text-align: center;
          border: 1px solid #ccc;
          border-radius: 5px;
          padding: 5px;

          /* ✅ 브라우저 기본 스타일 강제 적용 */
          appearance: auto !important;
          -webkit-appearance: auto !important;
          -moz-appearance: auto !important;
        }

        /* ✅ Safari & Chrome에서 숫자 스핀 버튼 활성화 */
        input[type="number"]::-webkit-inner-spin-button,
        input[type="number"]::-webkit-outer-spin-button {
          appearance: auto !important;
          display: inline-block !important;
        }
        .selection-container {
          display: flex;
          align-items: center;
          gap: 10px;
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
          background: #e6f0ff; /* ✅ 연하늘색 배경 */
          padding: 5px 10px;
          border-radius: 5px;
          white-space: nowrap;
          margin-bottom: 5px;
        }

        .remove-button {
          background: none;
          border: none;
          color: red;
          font-size: 1.2rem;
          cursor: pointer;
          margin-left: 5px;
        }

        /* ✅ 버튼 그룹 스타일 */
        .button-group {
          display: flex;
          justify-content: center;
          gap: 15px;
          margin-top: 20px;
        }
        .save-button,
        .cancel-button {
          padding: 10px 20px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
        }
        .save-button {
          background-color: #007bff;
          color: white;
        }
        .cancel-button {
          background-color: #dc3545;
          color: white;
        }
      `}</style>
    </form>
  );
};

export default ProjectForm;
