"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "../../../utils/api"; // 실제 경로에 맞게 수정
import { useAuthStore } from "@/store/authStore"; // Zustand 스토어 사용

export default function AdminDashboard() {
  const [adminName, setAdminName] = useState<string | null>(null);
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const router = useRouter();
  const { logout } = useAuthStore();

  useEffect(() => {
    // HttpOnly 쿠키를 통해 인증된 상태를 백엔드가 관리하므로,
    // 별도의 로컬스토리지 토큰 확인 없이 관리자 정보를 요청합니다.
    fetchAdminData();
  }, []);

  const fetchAdminData = async () => {
    try {
      const response = await api.get("/admin/me");
      setAdminName(response.data.data.username);
    } catch (err: any) {
      setError(err.message || "관리자 정보를 불러올 수 없습니다.");
      // 인증 실패 시 로그인 페이지로 이동
      router.push("/login");
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      await api.post("/admin/logout");
    } catch (error) {
      console.error("로그아웃 API 호출 실패:", error);
    } finally {
      logout();
      // 필요한 경우 상태 변경 함수 호출 (예: setAuthState(false)) 추가
      router.replace("/login");
    }
  };

  if (isLoading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <h1 className="text-3xl font-bold mb-4">관리자 대시보드</h1>
      <p className="text-lg">
        안녕하세요, <span className="font-semibold">{adminName}</span>님!
      </p>
      <div className="mt-6 flex flex-col space-y-4">
        <button
          className="bg-blue-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-blue-600"
          onClick={() => router.push("/admin/notice/manage")}
        >
          공지사항 관리
        </button>
        <button
          className="bg-green-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-green-600"
          onClick={() => router.push("/admin/userstats")}
        >
          사용자 이용 현황
        </button>
      </div>
    </div>
  );
}
