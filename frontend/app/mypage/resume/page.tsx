'use client';

import React, { useState, useEffect } from 'react';

interface Skill {
  id: number;
  name: string;
}

interface Tool {
  id: number;
  name: string;
}
interface ResumeData {
  name: string;
  birth: string;
  number: string;
  email: string;
  address: string;
  gitAddress: string;
  blogAddress: string;
  activities: Activity[];
  courses: Course[];
  experiences: Experience[];
  educations: Education[];
  licenses: License[];
  languages: Language[];
  skills: Skill[]; 
  tools: Tool[]; 
}

interface Activity {
  name: string;
  history: string;
  startDate: string;
  endDate: string;
  awards: Award[];
}

interface Award {
  name: string;
  institution: string;
  awardDate: string;
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

interface ApiResponse {
  resultCode: string;
  message: string;
  data: ResumeData;
}

export default function ResumePage() {
  const [resumeData, setResumeData] = useState<ResumeData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  useEffect(() => {
    const fetchResumeData = async () => {
      try {
        console.log('✅ API 요청 시작');
  
        const response = await fetch('/api/resume-detail', {
          method: 'GET',
          credentials: 'include', 
        });
        console.log('🔄 응답 상태:', response.status, response.statusText);
  
        if (response.status === 500) {
          alert('로그인을 해야 가능한 서비스입니다.');
          window.location.href = 'http://localhost:3000/login'; // 이력서 생성 페이지로 리디렉션
          return;
        }
        if (response.status === 400) {
          alert('이력서가 없습니다.');
          window.location.href = 'http://localhost:3000/mypage/resume/create'; // 이력서 생성 페이지로 리디렉션
          return;
        }
  
        if (!response.ok) {
          throw new Error('Failed to fetch resume data');
        }
  
        const data: ApiResponse = await response.json();
        setResumeData(data.data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An unknown error occurred');
      } finally {
        setLoading(false);
      }
    };
  
    fetchResumeData();
  }, []);
  

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

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', { 
      year: 'numeric', 
      month: '2-digit', 
      day: '2-digit' 
    });
  };
  const handleDelete = async () => {
    const confirmDelete = window.confirm('정말 삭제하시겠습니까?');
    if (!confirmDelete) return;

    try {
      const response = await fetch('http://localhost:8080/api/v1/user/resume', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include', 
      });

      if (!response.ok) {
        throw new Error('Failed to delete resume');
      }
      window.location.href = 'http://localhost:3000/mypage/resume/create'; // 삭제 후 생성 페이지로 리디렉션
    } catch (err) {
      alert('삭제에 실패했습니다. 다시 시도해주세요.');
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!resumeData) return null;

  return (
    <div className="max-w-4xl mx-auto p-6 bg-white shadow-lg">
      <section className="personal-info mb-6">
        <h1 className="text-2xl font-bold mb-4">{resumeData.name}의 이력서</h1>
        <div className="grid grid-cols-2 gap-4">
          <p>생년월일: {formatDate(resumeData.birth)} (만 {calculateAge(resumeData.birth)}세)</p>
          <p>연락처: {resumeData.number}</p>
          <p>이메일: {resumeData.email}</p>
          <p>주소: {resumeData.address} ({resumeData.addressDetail})</p>

          {resumeData.gitAddress && (
      <div>
        <p>GitHub: <a href={resumeData.gitAddress} target="_blank" rel="noopener noreferrer" className="text-blue-500 hover:underline">{resumeData.gitAddress}</a></p>
      </div>
    )}
    {resumeData.blogAddress && (
      <div>
        <p>Blog: <a href={resumeData.blogAddress} target="_blank" rel="noopener noreferrer" className="text-blue-500 hover:underline">{resumeData.blogAddress}</a></p>
      </div>
    )}
        </div>
      </section>

      {resumeData.skills.length > 0 && (
      <section className="skills mb-6">
        <h2 className="text-xl font-semibold border-b mb-2">기술 스택</h2>
        <div className="flex flex-wrap gap-2">
          {resumeData.skills.map((skill, index) => (
            <span key={index} className="bg-gray-200 px-2 py-1 rounded text-sm">
              {skill.name} 
            </span>
          ))}
        </div>
      </section>
    )}

    {resumeData.tools.length > 0 && (
      <section className="tools mb-6">
        <h2 className="text-xl font-semibold border-b mb-2">개발 도구</h2>
        <div className="flex flex-wrap gap-2">
          {resumeData.tools.map((tool, index) => (
            <span key={index} className="bg-gray-100 px-2 py-1 rounded text-sm">
              {tool.name} 
            </span>
          ))}
        </div>
      </section>
    )}

      {resumeData.activities.length > 0 && (
        <section className="activities mb-6">
          <h2 className="text-xl font-semibold border-b mb-2">대외 활동</h2>
          {resumeData.activities.map((activity, index) => (
            <div key={index} className="mb-4">
              <div className="flex justify-between">
                <h3 className="font-medium">{activity.name}</h3>
                <span className="text-gray-600">
                  {formatDate(activity.startDate)} - {formatDate(activity.endDate)}
                </span>
              </div>
              <p className="text-gray-700">{activity.history}</p>
              {activity.awards.length > 0 && (
                <div className="mt-2">
                  <h4 className="font-medium">수상 내역</h4>
                  <ul>
                    {activity.awards.map((award, awardIndex) => (
                      <li key={awardIndex} className="text-gray-700">
                        {award.name} - {award.institution} ({formatDate(award.awardDate)})
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          ))}
        </section>
      )}

      {resumeData.courses.length > 0 && (
        <section className="courses mb-6">
          <h2 className="text-xl font-semibold border-b mb-2">교육 수강 이력</h2>
          {resumeData.courses.map((course, index) => (
            <div key={index} className="mb-4">
              <div className="flex justify-between">
                <h3 className="font-medium">{course.name}</h3>
                <span className="text-gray-600">
                  {formatDate(course.startDate)} - {formatDate(course.endDate)}
                </span>
              </div>
              <p className="text-gray-700">{course.institution}</p>
            </div>
          ))}
        </section>
      )}

      {resumeData.licenses.length > 0 && (
        <section className="licenses mb-6">
          <h2 className="text-xl font-semibold border-b mb-2">자격증</h2>
          {resumeData.licenses.map((license, index) => (
            <div key={index} className="mb-4">
              <h3 className="font-medium">{license.name}</h3>
              <p className="text-gray-700">{license.institution} - {formatDate(license.certifiedDate)}</p>
            </div>
          ))}
        </section>
      )}

      {resumeData.languages.length > 0 && (
        <section className="languages mb-6">
          <h2 className="text-xl font-semibold border-b mb-2">외국어</h2>
          {resumeData.languages.map((language, index) => (
            <div key={index} className="mb-4">
              <h3 className="font-medium">{language.language}</h3>
              <p className="text-gray-700">{language.name} - {language.result} ({formatDate(language.certifiedDate)})</p>
            </div>
          ))}
        </section>
      )}

      {resumeData.experiences.length > 0 && (
        <section className="experiences mb-6">
          <h2 className="text-xl font-semibold border-b mb-2">경력 사항</h2>
          {resumeData.experiences.map((exp, index) => (
            <div key={index} className="mb-4">
              <div className="flex justify-between">
                <h3 className="font-medium">{exp.name} - {exp.position}</h3>
                <span className="text-gray-600">
                  {formatDate(exp.startDate)} - {formatDate(exp.endDate)}
                </span>
              </div>
              <p className="text-gray-700">{exp.department} | {exp.responsibility}</p>
            </div>
          ))}
        </section>
      )}

      {resumeData.educations.length > 0 && (
        <section className="education mb-6">
          <h2 className="text-xl font-semibold border-b mb-2">학력 사항</h2>
          {resumeData.educations.map((edu, index) => (
            <div key={index} className="mb-4">
              <div className="flex justify-between">
                <h3 className="font-medium">{edu.name} - {edu.major}</h3>
                <span className="text-gray-600">
                  {formatDate(edu.startDate)} - {formatDate(edu.endDate)}
                </span>
              </div>
              <p className="text-gray-700">{edu.status === 'GRADUATED' ? '졸업' : '재학중'}</p>
            </div>
          ))}
        </section>
      )}
      <div className="text-right mt-6 space-x-4">
  <button 
    onClick={() => window.location.href = '/mypage/resume/update'} 
    className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
  >
    수정
  </button>
  <button 
    onClick={() => setShowDeleteModal(true)} 
    className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
  >
    삭제
  </button>
</div>
      {showDeleteModal && (
        <div className="fixed inset-0 bg-gray-500 bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded shadow-lg">
            <h2 className="text-lg font-semibold mb-4">정말 삭제하시겠습니까?</h2>
            <div className="flex justify-end">
              <button 
                onClick={handleDelete} 
                className="mr-4 px-4 py-2 bg-red-600 text-white rounded"
              >
                삭제
              </button>
              <button 
                onClick={() => setShowDeleteModal(false)} 
                className="px-4 py-2 bg-gray-300 text-black rounded"
              >
                취소
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
  
}
