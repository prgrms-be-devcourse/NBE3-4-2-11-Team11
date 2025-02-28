"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getProjectById, updateProject } from "@/lib/projectService";

const EditProjectPage = () => {
  const { id } = useParams();
  const router = useRouter();
  const [projectData, setProjectData] = useState({
    name: "",
    startDate: "",
    endDate: "",
    memberCount: 1,
    position: "",
    repositoryLink: "",
    description: "",
    imageUrl: "",
    skills: [], // 수정된 부분
    tools: [], // 수정된 부분
  });

  useEffect(() => {
    const fetchProject = async () => {
      const res = await getProjectById(id);
      if (res.resultCode === "200") {
        setProjectData(res.data);
      } else {
        alert("프로젝트 정보를 불러오는 데 실패했습니다.");
        router.push("/mypage/projects");
      }
    };
    fetchProject();
  }, [id]);

  const handleChange = (e: any) => {
    const { name, value } = e.target;
    if (name === "skills" || name === "tools") {
      // skills와 tools는 쉼표로 구분된 문자열을 배열로 처리
      setProjectData({
        ...projectData,
        [name]: value.split(",").map((item: string) => item.trim()), // 쉼표로 구분된 입력을 배열로 변환
      });
    } else {
      setProjectData({ ...projectData, [name]: value });
    }
  };

  const handleSubmit = async (e: any) => {
    e.preventDefault();
    const res = await updateProject(id, projectData);
    if (res.resultCode === "201") {
      router.push(`/mypage/projects/${id}`);
    } else {
      alert("프로젝트 수정 실패");
    }
  };

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
        <input
          type="text"
          name="skills"
          value={projectData.skills.join(", ")} // skills 배열을 쉼표로 구분된 문자열로 변환하여 표시
          onChange={handleChange}
          placeholder="기술 스택 입력 (쉼표로 구분)"
        />
        <hr className="divider" />

        <label>사용 도구</label>
        <input
          type="text"
          name="tools"
          value={projectData.tools.join(", ")} // tools 배열을 쉼표로 구분된 문자열로 변환하여 표시
          onChange={handleChange}
          placeholder="사용 툴 입력 (쉼표로 구분)"
        />
        <hr className="divider" />

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
        textarea {
          height: 100px;
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
