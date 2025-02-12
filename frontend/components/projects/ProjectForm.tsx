"use client";

import { useState } from "react";
import { useRouter } from "next/navigation"; // ✅ 취소 버튼 클릭 시 이동하기 위해 추가

interface ProjectFormProps {
  initialData?: any;
  onSubmit: (data: any) => void;
}

const ProjectForm: React.FC<ProjectFormProps> = ({ initialData, onSubmit }) => {
  const router = useRouter(); // ✅ 취소 버튼 클릭 시 이동하기 위해 추가

  const [formData, setFormData] = useState(
    initialData || {
      name: "",
      startDate: "",
      endDate: "",
      memberCount: 1,
      position: "",
      repositoryLink: "",
      description: "",
      imageUrl: "",
      skills: [], // ✅ 변수명 변경
      tools: [], // ✅ 변수명 변경
    }
  );

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const formattedData = {
      ...formData,
      skills: Array.isArray(formData.skills)
        ? formData.skills
        : formData.skills.split(",").map((s) => s.trim()),

      tools: Array.isArray(formData.tools)
        ? formData.tools
        : formData.tools.split(",").map((t) => t.trim()),
    };

    console.log(
      "📢 [handleSubmit] 최종 변환된 요청 데이터:",
      JSON.stringify(formattedData, null, 2)
    );

    onSubmit(formattedData);
  };

  return (
    <form onSubmit={handleSubmit} className="project-form">
      <div className="input-group">
        <label className="input-label">프로젝트 이름</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          placeholder="프로젝트 이름"
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
          placeholder="ex: 프론트엔드, 백엔드"
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
          placeholder="프로젝트 저장소 URL"
        />
      </div>

      <div className="input-group">
        <label className="input-label">프로젝트 설명</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          placeholder="프로젝트에 대한 설명을 입력하세요."
          required
          className="description-textarea"
        />
      </div>

      <div className="input-group">
        <label className="input-label">이미지 URL</label>
        <input
          type="text"
          name="imageUrl"
          value={formData.imageUrl}
          onChange={handleChange}
          placeholder="이미지 링크 입력"
          required
        />
      </div>

      {/* ✅ 기술 스택 & 사용 툴 입력 필드 추가 */}
      <div className="input-group">
        <label className="input-label">기술 스택 (쉼표로 구분)</label>
        <input
          type="text"
          name="skills"
          value={formData.skills}
          onChange={handleChange}
          placeholder="ex: React, Node.js, Spring Boot"
        />
      </div>

      <div className="input-group">
        <label className="input-label">사용 툴 (쉼표로 구분)</label>
        <input
          type="text"
          name="tools"
          value={formData.tools}
          onChange={handleChange}
          placeholder="ex: Docker, Swagger"
        />
      </div>

      {/* ✅ 저장 & 취소 버튼 */}
      <div className="button-group">
        <button type="submit" className="save-button">
          {initialData ? "수정 완료" : "프로젝트 생성"}
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
        .description-textarea {
          height: 150px;
          resize: vertical;
        }
        .button-group {
          display: flex;
          justify-content: center;
          gap: 15px;
          margin-top: 20px;
        }
        .save-button {
          background-color: #007bff;
          color: white;
          border: none;
          padding: 10px 20px;
          font-size: 1rem;
          border-radius: 5px;
          cursor: pointer;
          transition: 0.3s;
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
          border-radius: 5px;
          cursor: pointer;
          transition: 0.3s;
        }
        .cancel-button:hover {
          background-color: #c82333;
        }
      `}</style>
    </form>
  );
};

export default ProjectForm;
