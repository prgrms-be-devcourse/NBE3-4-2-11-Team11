
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "../../../utils/api";
import MonthlyRegistrationChart from "./MonthlyRegistrationChart";

// DTO 타입 정의
interface UserStatsDto {
  id: number;
  email: string;
  name: string;
  sex: string;
  nickname: string;
  age: string;
  createdAt: string;
}

export default function UserStatsPage() {
  const [userStats, setUserStats] = useState<UserStatsDto[]>([]);
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const router = useRouter();

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    fetchUserStats();
  }, []);

  const fetchUserStats = async () => {
    try {
      const response = await api.get("/admin/userstats");
      console.log("API Response:", response.data);
      const responseData = response.data;
      const data = Array.isArray(responseData)
        ? responseData
        : responseData.content || responseData.data || [];
      setUserStats(data);
    } catch (err: any) {
      setError(err.message || "사용자 정보를 불러올 수 없습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) return <p className="text-center mt-8">로딩중...</p>;
  if (error) return <p className="text-center mt-8 text-red-500">{error}</p>;

  // 페이징된 데이터
  const totalPages = Math.ceil(userStats.length / itemsPerPage);
  const paginatedUsers = userStats.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-4">사용자 관리</h1>

      {/* 차트 추가 */}
      <div className="mb-8">
        <MonthlyRegistrationChart userStats={userStats} />
      </div>

      {/* 회원 목록 */}
      <table className="min-w-full bg-white border border-gray-300">
        <thead>
          <tr className="bg-gray-200">
            <th className="py-2 px-4 border">ID</th>
            <th className="py-2 px-4 border">이메일</th>
            <th className="py-2 px-4 border">이름</th>
            <th className="py-2 px-4 border">성별</th>
            <th className="py-2 px-4 border">닉네임</th>
            <th className="py-2 px-4 border">생년월일</th>
            <th className="py-2 px-4 border">가입일</th>
          </tr>
        </thead>
        <tbody>
          {paginatedUsers.map((user) => (
            <tr key={user.id} className="text-center">
              <td className="py-2 px-4 border">{user.id}</td>
              <td className="py-2 px-4 border">{user.email}</td>
              <td className="py-2 px-4 border">{user.name}</td>
              <td className="py-2 px-4 border">{user.sex}</td>
              <td className="py-2 px-4 border">{user.nickname}</td>
              <td className="py-2 px-4 border">{user.age}</td>
              <td className="py-2 px-4 border">{user.createdAt}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 페이지네이션 */}
      <div className="flex justify-center mt-4 space-x-2">
        <button
          className="px-4 py-2 border bg-gray-200 rounded"
          onClick={() => setCurrentPage(currentPage - 1)}
          disabled={currentPage === 1}
        >
          이전
        </button>
        <span className="px-4 py-2">{currentPage} / {totalPages}</span>
        <button
          className="px-4 py-2 border bg-gray-200 rounded"
          onClick={() => setCurrentPage(currentPage + 1)}
          disabled={currentPage === totalPages}
        >
          다음
        </button>
      </div>
    </div>
  );
}
