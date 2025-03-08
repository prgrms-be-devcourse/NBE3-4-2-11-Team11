"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "../../../utils/api";
import MonthlyRegistrationChart from "./MonthlyRegistrationChart";
import DormantAccountsChart from "./DormantAccountsChart";
import UserAgeDistributionChart from "./UserAgeDistributionChart";

// DTO 타입 정의 (User 엔티티의 모든 필드를 포함)
interface UserStatsDto {
  id: number;
  email: string;
  name: string;
  sex: string;
  nickname: string;
  age: string;
  createdAt: string;
  lastLoginAt?: string;
  jobInterest?: string;
  userStatus?: string;
  dormantFlg?: string;
  dormantStartAt?: string;
  dormantEndAt?: string;
}

export default function UserStatsPage() {
  const [userStats, setUserStats] = useState<UserStatsDto[]>([]);
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const router = useRouter();

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  // 대시보드 옵션 상태 ("monthly", "dormant", "age")
  const [dashboardOption, setDashboardOption] = useState<string>("monthly");

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

  // 페이징 처리
  const totalPages = Math.ceil(userStats.length / itemsPerPage);
  const paginatedUsers = userStats.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-4">사용자 관리</h1>

      {/* 대시보드 옵션 선택 */}
      <div className="mb-8">
        <label htmlFor="dashboardSelect" className="mr-4 font-medium">
          대시보드 선택:
        </label>
        <select
          id="dashboardSelect"
          value={dashboardOption}
          onChange={(e) => setDashboardOption(e.target.value)}
          className="p-2 border rounded"
        >
          <option value="monthly">월별 가입자</option>
          <option value="dormant">휴먼 계정 현황</option>
          <option value="age">연령별 분포</option>
        </select>
      </div>

      {/* 선택한 대시보드에 따라 차트 렌더링 */}
      <div className="mb-8">
        {dashboardOption === "monthly" && <MonthlyRegistrationChart userStats={userStats} />}
        {dashboardOption === "dormant" && <DormantAccountsChart userStats={userStats} />}
        {dashboardOption === "age" && <UserAgeDistributionChart userStats={userStats} />}
      </div>

      {/* 회원 목록 테이블 - 모든 정보 표시 (폰트 크기를 text-sm로 줄임) */}
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-300 text-sm">
          <thead>
            <tr className="bg-gray-200">
              <th className="py-2 px-4 border whitespace-nowrap">ID</th>
              <th className="py-2 px-4 border whitespace-nowrap">이메일</th>
              <th className="py-2 px-4 border whitespace-nowrap">이름</th>
              <th className="py-2 px-4 border whitespace-nowrap">성별</th>
              <th className="py-2 px-4 border whitespace-nowrap">닉네임</th>
              <th className="py-2 px-4 border whitespace-nowrap">생년월일</th>
              <th className="py-2 px-4 border whitespace-nowrap">가입일</th>
              <th className="py-2 px-4 border whitespace-nowrap">마지막 로그인</th>
              <th className="py-2 px-4 border whitespace-nowrap">관심 직종</th>
              <th className="py-2 px-4 border whitespace-nowrap">취업 상태</th>
              <th className="py-2 px-4 border whitespace-nowrap">휴먼 여부</th>
              <th className="py-2 px-4 border whitespace-nowrap">휴먼 처리 시작</th>
              <th className="py-2 px-4 border whitespace-nowrap">휴먼 처리 종료</th>
            </tr>
          </thead>
          <tbody>
            {paginatedUsers.map((user) => (
              <tr key={user.id} className="text-center">
                <td className="py-2 px-4 border whitespace-nowrap">{user.id}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.email}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.name}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.sex}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.nickname}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.age}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.createdAt}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.lastLoginAt || "N/A"}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.jobInterest || "N/A"}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.userStatus || "N/A"}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.dormantFlg || "N/A"}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.dormantStartAt || "N/A"}</td>
                <td className="py-2 px-4 border whitespace-nowrap">{user.dormantEndAt || "N/A"}</td>
              </tr>
            ))}
          </tbody>
        </table>

      </div>

      {/* 페이지네이션 */}
      <div className="flex justify-center mt-4 space-x-2">
        <button
          className="px-4 py-2 border bg-gray-200 rounded text-sm"
          onClick={() => setCurrentPage(currentPage - 1)}
          disabled={currentPage === 1}
        >
          이전
        </button>
        <span className="px-4 py-2 text-sm">
          {currentPage} / {totalPages}
        </span>
        <button
          className="px-4 py-2 border bg-gray-200 rounded text-sm"
          onClick={() => setCurrentPage(currentPage + 1)}
          disabled={currentPage === totalPages}
        >
          다음
        </button>
      </div>
    </div>
  );
}
