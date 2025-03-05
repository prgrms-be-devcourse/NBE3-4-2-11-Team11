'use client';

import React, { useState, useEffect } from 'react';
import { PlusCircle, MinusCircle } from "lucide-react";
import Postcode from "../../../../components/Postcode";
interface Award {
  name: string;
  institution: string;
  awardDate: string;
}

interface Activity {
  name: string;
  history: string;
  startDate: string;
  endDate: string;
  awards: Award[];
}

interface Course {
  name: string;
  institution: string;
  startDate: string;
  endDate: string;
}

interface Experience {
  name: string;
  department: string;
  position: string;
  responsibility: string;
  startDate: string;
  endDate: string;
}

interface Education {
  name: string;
  major: string;
  startDate: string;
  endDate: string;
  status: string;
}

interface License {
  name: string;
  institution: string;
  certifiedDate: string;
}

interface Language {
  language: string;
  name: string;
  result: string;
  certifiedDate: string;
}

interface SkillOption {
  id: number;
  name: string;
}

interface ToolOption {
  id: number;
  name: string;
}

interface ResumeData {
  name: string;
  birth: string;
  number: string;
  email: string;
  address: string;
  addressDetail: string;
  gitAddress: string;
  blogAddress: string;
  activities: Activity[];
  courses: Course[];
  experiences: Experience[];
  educations: Education[];
  licenses: License[];
  languages: Language[];
  skills: { id: number; name: string; }[];
  tools: { id: number; name: string; }[];
}

export default function ResumeUpdatePage() {
  const [formData, setFormData] = useState<ResumeData>({
    name: '',
    birth: '',
    number: '',
    email: '',
    address: '',
    addressDetail: '',
    gitAddress: '',
    blogAddress: '',
    activities: [],
    courses: [],
    experiences: [],
    educations: [],
    licenses: [],
    languages: [],
    skills: [],
    tools: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [skillOptions, setSkillOptions] = useState<SkillOption[]>([]);
  const [toolOptions, setToolOptions] = useState<ToolOption[]>([]);
  

  useEffect(() => {
    const fetchResumeData = async () => {
      try {

        const response = await fetch('http://localhost:8080/api/v1/user/resume', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
        });
        if (response.status === 404) {
          alert('이력서가 없습니다.');
          window.location.href = 'http://localhost:3000/mypage/resume/create';
          return;
        }

        if (!response.ok) {
          throw new Error('이력서 데이터를 불러오는데 실패했습니다.');
        }
        const calculateAge = (birthDateString: string): number => {
          const birthDate = new Date(birthDateString);
          const today = new Date();
          
          let age = today.getFullYear() - birthDate.getFullYear();
          const isBeforeBirthday = 
            today.getMonth() < birthDate.getMonth() || 
            (today.getMonth() === birthDate.getMonth() && today.getDate() < birthDate.getDate());
        
          if (isBeforeBirthday) {
            age--;
          }
        
          return age;
        };
        const skillsResponse = await fetch('http://localhost:8080/api/v1/user/resume/skills', {
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
        });
        const skillsData = await skillsResponse.json();
        setSkillOptions(skillsData.data);

        // 툴 목록 가져오기
        const toolsResponse = await fetch('http://localhost:8080/api/v1/user/resume/tools', {
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
        });
        const toolsData = await toolsResponse.json();
        setToolOptions(toolsData.data);

        const data = await response.json();
        console.log(data);
        setFormData(data.data);
      } catch (err) {
        setError(err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };
    fetchResumeData();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };
  const handleAddressComplete = (roadAddress: string) => {
    setFormData((prev) => ({
      ...prev,
      address: roadAddress, 
    }));
  };
  const handleSkillChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedId = Number(event.target.value);
    const selectedSkill = skillOptions.find(skill => skill.id === selectedId);
    if (selectedSkill && !formData.skills.some(s => s.id === selectedId)) {
      setFormData(prev => ({
        ...prev,
        skills: [...prev.skills, { id: selectedId, name: selectedSkill.name }]
      }));
    }
  };

  // 툴 선택 핸들러
  const handleToolChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedId = Number(event.target.value);
    const selectedTool = toolOptions.find(tool => tool.id === selectedId);
    if (selectedTool && !formData.tools.some(t => t.id === selectedId)) {
      setFormData(prev => ({
        ...prev,
        tools: [...prev.tools, { id: selectedId, name: selectedTool.name }]
      }));
    }
  };
  const handleRemoveSkill = (skillId: number) => {
    setFormData(prev => ({
      ...prev,
      skills: prev.skills.filter(skill => skill.id !== skillId)
    }));
  };

  const handleRemoveTool = (toolId: number) => {
    setFormData(prev => ({
      ...prev,
      tools: prev.tools.filter(tool => tool.id !== toolId)
    }));
  };

  const handleAddActivity = () => {
    setFormData((prevData) => ({
      ...prevData,
      activities: [
        ...prevData.activities,
        {
          name: '',
          history: '',
          startDate: '',
          endDate: '',
          awards: [],
        },
      ],
    }));
  };
  const handleDeleteActivity = (activityIndex: number) => {
    const updatedActivities = formData.activities.filter((_, index) => index !== activityIndex);
    setFormData({ ...formData, activities: updatedActivities });
  };

  const handleAddAward = (activityIndex: number) => {
    const updatedActivities = [...formData.activities];
    updatedActivities[activityIndex].awards.push({
      name: '',
      institution: '',
      awardDate: '',
    });
    setFormData({ ...formData, activities: updatedActivities });
  };

  const handleAwardChange = (
    activityIndex: number,
    awardIndex: number,
    field: string,
    value: string
  ) => {
    const updatedActivities = [...formData.activities];
    updatedActivities[activityIndex].awards[awardIndex][field] = value;
    setFormData({ ...formData, activities: updatedActivities });
  };
  const handleDeleteAward = (activityIndex: number, awardIndex: number) => {
    const updatedActivities = [...formData.activities];
    updatedActivities[activityIndex].awards = updatedActivities[activityIndex].awards.filter(
      (_, index) => index !== awardIndex
    );
    setFormData({ ...formData, activities: updatedActivities });
  };

  const handleAddCourse = () => {
    setFormData((prevData) => ({
      ...prevData,
      courses: [
        ...prevData.courses,
        {
          name: '',
          institution: '',
          startDate: '',
          endDate: '',
        },
      ],
    }));
  };
  const handleDeleteCourse = (courseIndex: number) => {
    const updatedCourses = formData.courses.filter((_, index) => index !== courseIndex);
    setFormData({ ...formData, courses: updatedCourses });
  };

  const handleAddExperience = () => {
    setFormData((prevData) => ({
      ...prevData,
      experiences: [
        ...prevData.experiences,
        {
          name: '',
          department: '',
          position: '',
          responsibility: '',
          startDate: '',
          endDate: '',
        },
      ],
    }));
  };
  const handleDeleteExperience = (experienceIndex: number) => {
    const updatedExperiences = formData.experiences.filter((_, index) => index !== experienceIndex);
    setFormData({ ...formData, experiences: updatedExperiences });
  };

  const handleAddEducation = () => {
    setFormData((prevData) => ({
      ...prevData,
      educations: [
        ...prevData.educations,
        {
          name: '',
          major: '',
          startDate: '',
          endDate: '',
          status: '',
        },
      ],
    }));
  };
  const handleDeleteEducation = (educationIndex: number) => {
    const updatedEducations = formData.educations.filter((_, index) => index !== educationIndex);
    setFormData({ ...formData, educations: updatedEducations });
  };

  const handleAddLicense = () => {
    setFormData((prevData) => ({
      ...prevData,
      licenses: [
        ...prevData.licenses,
        {
          name: '',
          institution: '',
          certifiedDate: '',
        },
      ],
    }));
  };
  const handleDeleteLicense = (licenseIndex: number) => {
    const updatedLicenses = formData.licenses.filter((_, index) => index !== licenseIndex);
    setFormData({ ...formData, licenses: updatedLicenses });
  };

  const handleAddLanguage = () => {
    setFormData((prevData) => ({
      ...prevData,
      languages: [
        ...prevData.languages,
        {
          language: '',
          name: '',
          result: '',
          certifiedDate: '',
        },
      ],
    }));
  };
  const handleDeleteLanguage = (languageIndex: number) => {
    const updatedLanguages = formData.languages.filter((_, index) => index !== languageIndex);
    setFormData({ ...formData, languages: updatedLanguages });
  };

  const handleAddSkill = () => {
    setFormData((prevData) => ({
      ...prevData,
      skills: [...prevData.skills, ''],
    }));
  };
  const handleDeleteSkill = (skillIndex: number) => {
    const updatedSkills = formData.skills.filter((_, index) => index !== skillIndex);
    setFormData({ ...formData, skills: updatedSkills });
  };

  const handleAddTool = () => {
    setFormData((prevData) => ({
      ...prevData,
      tools: [...prevData.tools, ''],
    }));
  };
  const handleDeleteTool = (toolIndex: number) => {
    const updatedTools = formData.tools.filter((_, index) => index !== toolIndex);
    setFormData({ ...formData, tools: updatedTools });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const updatedFormData = {
      ...formData,
      skills: formData.skills.map(skill => (skill.id)),
      tools: formData.tools.map(tool => (tool.id)),
    };
    console.log(updatedFormData);
    try {
      const response = await fetch('http://localhost:8080/api/v1/user/resume', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(updatedFormData),
      });

      if (!response.ok) {
        throw new Error('이력서 업데이트에 실패했습니다.');
      }

      const data = await response.json();
      alert('이력서가 성공적으로 업데이트되었습니다.');
      window.location.href = 'http://localhost:3000/mypage/resume';
    } catch (error) {
      console.error('Error:', error);
      alert('이력서 업데이트 중 오류가 발생했습니다.');
    }
  };

  if (loading) {
    return <div className="flex justify-center items-center h-screen">로딩 중...</div>;
  }

  if (error) {
    return <div className="flex justify-center items-center h-screen text-red-500">{error}</div>;
  }
  

  return (
    <div className="max-w-4xl mx-auto p-6 bg-white shadow-lg">
      <h1 className="text-2xl font-semibold mb-4">이력서 수정</h1>
      <form onSubmit={handleSubmit}>
        {/* 기본 정보 입력 */}
        <div className="mb-4">
          <label htmlFor="name" className="block text-sm font-medium">이름</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full p-2 border border-gray-300 rounded"
          />
        </div>

        <div className="mb-4">
        <label htmlFor="birth" className="block text-sm font-medium">생년월일</label>
          <input
            type="date"
            id="birth"
            name="birth"
            value={formData.birth}
            onChange={handleChange}
            required
            className="w-full p-2 border border-gray-300 rounded"
          />
        </div>

        <div className="mb-4">
        <label htmlFor="number" className="block text-sm font-medium">전화번호</label>
  <input
    type="tel"
    id="number"
    name="number"
    value={formData.number}
    onChange={handleChange}
    required
    pattern="^(01[0-9])-([0-9]{3,4})-([0-9]{4})$"
    title="전화번호는 010-1234-5678 형식으로 입력해주세요."
    placeholder="010-1234-5678"
    className="w-full p-2 border border-gray-300 rounded"
  />
</div>

        <div className="mb-4">
          <label htmlFor="email" className="block text-sm font-medium">이메일</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            className="w-full p-2 border border-gray-300 rounded"
          />
        </div>

        <div className="mb-4">
        <label htmlFor="address" className="block text-sm font-medium">주소</label>
  <div className="flex gap-2">
    <input
      type="text"
      id="address"
      name="address"
      value={formData.address}
      readOnly
      required
      className="w-1/2 p-2 border border-gray-300 rounded bg-gray-100 cursor-not-allowed"
    />
    <input
      type="text"
      id="addressDetail"
      name="addressDetail"
      value={formData.addressDetail}
      onChange={handleChange}
      required
      className="w-1/2 p-2 border border-gray-300 rounded"
      placeholder="상세 주소 입력"
    />
    <Postcode onComplete={handleAddressComplete} />
  </div>
</div>

        <div className="mb-4">
          <label htmlFor="gitAddress" className="block text-sm font-medium">GitHub 주소</label>
          <input
            type="url"
            id="gitAddress"
            name="gitAddress"
            value={formData.gitAddress}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded"
          />
        </div>

        <div className="mb-4">
          <label htmlFor="blogAddress" className="block text-sm font-medium">블로그 주소</label>
          <input
            type="url"
            id="blogAddress"
            name="blogAddress"
            value={formData.blogAddress}
            onChange={handleChange}
            className="w-full p-2 border border-gray-300 rounded"
          />
        </div>
        <div className="mb-4">
      <h3 className="text-lg font-semibold">기술 스킬</h3>
      <select
        onChange={handleSkillChange}
        className="w-full p-2 border border-gray-300 rounded mb-2"
      >
        <option value="">스킬 선택</option>
        {skillOptions.map(skill => (
          <option key={skill.id} value={skill.id}>
            {skill.name}
          </option>
        ))}
      </select>
      <div className="flex flex-wrap gap-2">
        {formData.skills.map(skill => (
          <div key={skill.id} className="flex items-center bg-gray-100 px-3 py-1 rounded">
            <span>{skill.name}</span>
            <button
              onClick={() => handleRemoveSkill(skill.id)}
              className="ml-2 text-red-500 hover:text-red-700"
            >
              ×
            </button>
          </div>
        ))}
      </div>
    </div>

    <div className="mb-4">
      <h3 className="text-lg font-semibold">사용 툴</h3>
      <select
        onChange={handleToolChange}
        className="w-full p-2 border border-gray-300 rounded mb-2"
      >
        <option value="">툴 선택</option>
        {toolOptions.map(tool => (
          <option key={tool.id} value={tool.id}>
            {tool.name}
          </option>
        ))}
      </select>
      <div className="flex flex-wrap gap-2">
        {formData.tools.map(tool => (
          <div key={tool.id} className="flex items-center bg-gray-100 px-3 py-1 rounded">
            <span>{tool.name}</span>
            <button
              onClick={() => handleRemoveTool(tool.id)}
              className="ml-2 text-red-500 hover:text-red-700"
            >
              ×
            </button>
          </div>
        ))}
      </div>
    </div>

    <div className="mb-4">
  <h3 className="text-lg font-semibold">대외 활동</h3>
  {formData.activities.map((activity, activityIndex) => (
    <div key={activityIndex} className="mb-4 p-4 bg-white shadow-md rounded-lg border border-gray-200">
      <div className="flex justify-between items-center">
        <button
          type="button"
          onClick={() => handleDeleteActivity(activityIndex)}
          className="text-red-500 text-xl p-2 rounded-full hover:bg-red-100 transition"
        >
          <MinusCircle />
        </button>
        <input
          type="text"
          name={`activityName_${activityIndex}`}
          value={activity.name}
          onChange={(e) => {
            const updatedActivities = [...formData.activities];
            updatedActivities[activityIndex].name = e.target.value;
            setFormData({ ...formData, activities: updatedActivities });
          }}
          placeholder="활동명"
          className="w-full p-2 border border-gray-300 rounded"
        />
      </div>
      <textarea
        name={`activityHistory_${activityIndex}`}
        value={activity.history}
        onChange={(e) => {
          const updatedActivities = [...formData.activities];
          updatedActivities[activityIndex].history = e.target.value;
          setFormData({ ...formData, activities: updatedActivities });
        }}
        placeholder="활동 내용"
        className="w-full p-2 border border-gray-300 rounded mt-2"
      />
      <div className="flex space-x-2 mt-2">
        <input
          type="date"
          name={`activityStartDate_${activityIndex}`}
          value={activity.startDate}
          onChange={(e) => {
            const updatedActivities = [...formData.activities];
            updatedActivities[activityIndex].startDate = e.target.value;
            setFormData({ ...formData, activities: updatedActivities });
          }}
          className="w-full p-2 border border-gray-300 rounded"
        />
        <input
          type="date"
          name={`activityEndDate_${activityIndex}`}
          value={activity.endDate}
          onChange={(e) => {
            const updatedActivities = [...formData.activities];
            updatedActivities[activityIndex].endDate = e.target.value;
            setFormData({ ...formData, activities: updatedActivities });
          }}
          className="w-full p-2 border border-gray-300 rounded"
        />
      </div>
      <button
        type="button"
        onClick={() => handleAddAward(activityIndex)}
        className="flex items-center text-blue-500 text-xl p-2 rounded-full hover:bg-blue-100 transition mt-2"
      >
        <PlusCircle className="mr-2" />
        <span>수상 추가</span>
      </button>
      {activity.awards.map((award, awardIndex) => (
        <div key={awardIndex} className="mt-2 p-3 bg-gray-50 border border-gray-200 rounded-lg">
          <div className="flex justify-between items-center">
            <button
              type="button"
              onClick={() => handleDeleteAward(activityIndex, awardIndex)}
              className="text-red-500 text-xl p-2 rounded-full hover:bg-red-100 transition"
            >
              <MinusCircle />
            </button>
            <input
              type="text"
              value={award.name}
              onChange={(e) => handleAwardChange(activityIndex, awardIndex, 'name', e.target.value)}
              placeholder="수상명"
              className="w-full p-2 border border-gray-300 rounded"
            />
          </div>
          <input
            type="text"
            value={award.institution}
            onChange={(e) => handleAwardChange(activityIndex, awardIndex, 'institution', e.target.value)}
            placeholder="기관명"
            className="w-full p-2 border border-gray-300 rounded mt-2"
          />
          <input
            type="date"
            value={award.awardDate}
            onChange={(e) => handleAwardChange(activityIndex, awardIndex, 'awardDate', e.target.value)}
            className="w-full p-2 border border-gray-300 rounded mt-2"
          />
        </div>
      ))}
    </div>
  ))}
  <button
    type="button"
    onClick={handleAddActivity}
    className="flex items-center text-blue-500 text-xl p-2 rounded-full hover:bg-blue-100 transition"
  >
    <PlusCircle className="mr-2" />
    <span>대외 활동 추가</span>
  </button>
</div>
<div className="mb-4">
  <h3 className="text-lg font-semibold">학력</h3>
  {formData.educations.map((education, educationIndex) => (
    <div key={educationIndex} className="mb-4 p-4 bg-white shadow-md rounded-lg border border-gray-200">
      <div className="flex justify-between items-center">
        <button
          type="button"
          onClick={() => handleDeleteEducation(educationIndex)}
          className="text-red-500 text-xl p-2 rounded-full hover:bg-red-100 transition"
        >
          <MinusCircle />
        </button>
        <input
          type="text"
          value={education.name}
          onChange={(e) => {
            const updatedEducations = [...formData.educations];
            updatedEducations[educationIndex].name = e.target.value;
            setFormData({ ...formData, educations: updatedEducations });
          }}
          placeholder="학교명"
          className="w-full p-2 border border-gray-300 rounded"
        />
      </div>
      <input
        type="text"
        value={education.major}
        onChange={(e) => {
          const updatedEducations = [...formData.educations];
          updatedEducations[educationIndex].major = e.target.value;
          setFormData({ ...formData, educations: updatedEducations });
        }}
        placeholder="전공"
        className="w-full p-2 border border-gray-300 rounded mt-2"
      />
      <div className="flex space-x-2 mt-2">
        <input
          type="date"
          value={education.startDate}
          onChange={(e) => {
            const updatedEducations = [...formData.educations];
            updatedEducations[educationIndex].startDate = e.target.value;
            setFormData({ ...formData, educations: updatedEducations });
          }}
          className="w-full p-2 border border-gray-300 rounded"
        />
        <input
          type="date"
          value={education.endDate}
          onChange={(e) => {
            const updatedEducations = [...formData.educations];
            updatedEducations[educationIndex].endDate = e.target.value;
            setFormData({ ...formData, educations: updatedEducations });
          }}
          className="w-full p-2 border border-gray-300 rounded"
        />
      </div>
      <select
        value={education.status|| "EXPECTED"}
        onChange={(e) => {
          const updatedEducations = [...formData.educations];
          updatedEducations[educationIndex].status = e.target.value;
          setFormData({ ...formData, educations: updatedEducations });
        }}
        className="w-full p-2 border border-gray-300 rounded mt-2"
      >
        <option value="EXPECTED">졸업 예정</option>
        <option value="GRADUATED">졸업</option>
        <option value="ENROLLED">재학</option>
        <option value="REST">휴학</option>
      </select>
    </div>
  ))}
  <button
    type="button"
    onClick={handleAddEducation}
    className="flex items-center text-blue-500 text-xl p-2 rounded-full hover:bg-blue-100 transition"
  >
    <PlusCircle className="mr-2" />
    <span>학력 추가</span>
  </button>
</div>

{/* 교육 내역 */}
<div className="mb-4">
  <h3 className="text-lg font-semibold">교육 내역</h3>
  {formData.courses.map((course, courseIndex) => (
    <div key={courseIndex} className="mb-4 p-4 bg-white shadow-md rounded-lg border border-gray-200">
      <div className="flex justify-between items-center">
        <button
          type="button"
          onClick={() => handleDeleteCourse(courseIndex)}
          className="text-red-500 text-xl p-2 rounded-full hover:bg-red-100 transition"
        >
          <MinusCircle />
        </button>
        <input
          type="text"
          value={course.name}
          onChange={(e) => {
            const updatedCourses = [...formData.courses];
            updatedCourses[courseIndex].name = e.target.value;
            setFormData({ ...formData, courses: updatedCourses });
          }}
          placeholder="과정명"
          className="w-full p-2 border border-gray-300 rounded mb-2"
        />
      </div>
      <input
        type="text"
        value={course.institution}
        onChange={(e) => {
          const updatedCourses = [...formData.courses];
          updatedCourses[courseIndex].institution = e.target.value;
          setFormData({ ...formData, courses: updatedCourses });
        }}
        placeholder="교육기관명"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <div className="flex space-x-2">
        <input
          type="date"
          value={course.startDate}
          onChange={(e) => {
            const updatedCourses = [...formData.courses];
            updatedCourses[courseIndex].startDate = e.target.value;
            setFormData({ ...formData, courses: updatedCourses });
          }}
          placeholder="시작일"
          className="w-full p-2 border border-gray-300 rounded mb-2"
        />
        <input
          type="date"
          value={course.endDate}
          onChange={(e) => {
            const updatedCourses = [...formData.courses];
            updatedCourses[courseIndex].endDate = e.target.value;
            setFormData({ ...formData, courses: updatedCourses });
          }}
          placeholder="종료일"
          className="w-full p-2 border border-gray-300 rounded mb-2"
        />
      </div>
    </div>
  ))}
  <button
    type="button"
    onClick={handleAddCourse}
    className="inline-flex items-center text-blue-500 text-xl p-2 rounded-full hover:bg-blue-100 transition"
>
  <PlusCircle className="mr-2" />
  <span>교육 내역</span>
  </button>
</div>

{/* 경력 */}
<div className="mb-4">
  <h3 className="text-lg font-semibold">경력</h3>
  {formData.experiences.map((experience, experienceIndex) => (
    <div key={experienceIndex} className="mb-4 p-4 bg-white shadow-md rounded-lg border border-gray-200">
      <div className="flex justify-between items-center">
        <button
          type="button"
          onClick={() => handleDeleteExperience(experienceIndex)}
          className="text-red-500 text-xl p-2 rounded-full hover:bg-red-100 transition"
        >
          <MinusCircle />
        </button>
        <input
          type="text"
          value={experience.name}
          onChange={(e) => {
            const updatedExperiences = [...formData.experiences];
            updatedExperiences[experienceIndex].name = e.target.value;
            setFormData({ ...formData, experiences: updatedExperiences });
          }}
          placeholder="회사명"
          className="w-full p-2 border border-gray-300 rounded mb-2"
        />
      </div>
      <input
        type="text"
        value={experience.department}
        onChange={(e) => {
          const updatedExperiences = [...formData.experiences];
          updatedExperiences[experienceIndex].department = e.target.value;
          setFormData({ ...formData, experiences: updatedExperiences });
        }}
        placeholder="부서"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <input
        type="text"
        value={experience.position}
        onChange={(e) => {
          const updatedExperiences = [...formData.experiences];
          updatedExperiences[experienceIndex].position = e.target.value;
          setFormData({ ...formData, experiences: updatedExperiences });
        }}
        placeholder="직책"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <textarea
        value={experience.responsibility}
        onChange={(e) => {
          const updatedExperiences = [...formData.experiences];
          updatedExperiences[experienceIndex].responsibility = e.target.value;
          setFormData({ ...formData, experiences: updatedExperiences });
        }}
        placeholder="책임"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <div className="flex space-x-2">
        <input
          type="date"
          value={experience.startDate}
          onChange={(e) => {
            const updatedExperiences = [...formData.experiences];
            updatedExperiences[experienceIndex].startDate = e.target.value;
            setFormData({ ...formData, experiences: updatedExperiences });
          }}
          placeholder="시작일"
          className="w-full p-2 border border-gray-300 rounded mb-2"
        />
        <input
          type="date"
          value={experience.endDate}
          onChange={(e) => {
            const updatedExperiences = [...formData.experiences];
            updatedExperiences[experienceIndex].endDate = e.target.value;
            setFormData({ ...formData, experiences: updatedExperiences });
          }}
          placeholder="종료일"
          className="w-full p-2 border border-gray-300 rounded mb-2"
        />
      </div>
    </div>
  ))}
  <button
    type="button"
    onClick={handleAddExperience}
    className="inline-flex items-center text-blue-500 text-xl p-2 rounded-full hover:bg-blue-100 transition"
>
  <PlusCircle className="mr-2" />
  <span>경력</span>
  </button>
</div>



<div className="mb-4">
  <h3 className="text-lg font-semibold">자격증</h3>
  {formData.licenses.map((license, licenseIndex) => (
    <div key={licenseIndex} className="mb-4 p-4 bg-white shadow-md rounded-lg border border-gray-200">
      <div className="flex justify-between items-center">
        <button
          type="button"
          onClick={() => handleDeleteLicense(licenseIndex)}
          className="text-red-500 hover:text-red-600 p-2"
        >
          <MinusCircle size={24} />
        </button>
      </div>
      <input
        type="text"
        value={license.name}
        onChange={(e) => {
          const updatedLicenses = [...formData.licenses];
          updatedLicenses[licenseIndex].name = e.target.value;
          setFormData({ ...formData, licenses: updatedLicenses });
        }}
        placeholder="자격증명"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <input
        type="text"
        value={license.institution}
        onChange={(e) => {
          const updatedLicenses = [...formData.licenses];
          updatedLicenses[licenseIndex].institution = e.target.value;
          setFormData({ ...formData, licenses: updatedLicenses });
        }}
        placeholder="기관명"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <input
        type="date"
        value={license.certifiedDate}
        onChange={(e) => {
          const updatedLicenses = [...formData.licenses];
          updatedLicenses[licenseIndex].certifiedDate = e.target.value;
          setFormData({ ...formData, licenses: updatedLicenses });
        }}
        placeholder="수여일"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
    </div>
  ))}
  <button
    type="button"
    onClick={handleAddLicense}
    className="inline-flex items-center text-blue-500 text-xl p-2 rounded-full hover:bg-blue-100 transition"
>
  <PlusCircle className="mr-2" />
  <span>자격증</span>
  </button>
</div>

<div className="mb-4">
  <h3 className="text-lg font-semibold">어학</h3>
  {formData.languages.map((language, languageIndex) => (
    <div key={languageIndex} className="mb-4 p-4 bg-white shadow-md rounded-lg border border-gray-200">
      <div className="flex justify-between items-center">
        <button
          type="button"
          onClick={() => handleDeleteLanguage(languageIndex)}
          className="text-red-500 hover:text-red-600 p-2"
        >
          <MinusCircle />
        </button>
      </div>
      <input
        type="text"
        value={language.language}
        onChange={(e) => {
          const updatedLanguages = [...formData.languages];
          updatedLanguages[languageIndex].language = e.target.value;
          setFormData({ ...formData, languages: updatedLanguages });
        }}
        placeholder="언어"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <input
        type="text"
        value={language.name}
        onChange={(e) => {
          const updatedLanguages = [...formData.languages];
          updatedLanguages[languageIndex].name = e.target.value;
          setFormData({ ...formData, languages: updatedLanguages });
        }}
        placeholder="시험명"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <input
        type="text"
        value={language.result}
        onChange={(e) => {
          const updatedLanguages = [...formData.languages];
          updatedLanguages[languageIndex].result = e.target.value;
          setFormData({ ...formData, languages: updatedLanguages });
        }}
        placeholder="점수/등급"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
      <input
        type="date"
        value={language.certifiedDate}
        onChange={(e) => {
          const updatedLanguages = [...formData.languages];
          updatedLanguages[languageIndex].certifiedDate = e.target.value;
          setFormData({ ...formData, languages: updatedLanguages });
        }}
        placeholder="인증일"
        className="w-full p-2 border border-gray-300 rounded mb-2"
      />
    </div>
  ))}
  <button
    type="button"
    onClick={handleAddLanguage}
    className="inline-flex items-center text-blue-500 text-xl p-2 rounded-full hover:bg-blue-100 transition"
>
  <PlusCircle className="mr-2" />
  <span>어학</span>
  </button>
</div>
        

        <button
          type="submit"
          className="bg-blue-500 text-white p-2 rounded hover:bg-blue-600 mt-4"
        >
          제출
        </button>
      </form>
    </div>
  );
}
