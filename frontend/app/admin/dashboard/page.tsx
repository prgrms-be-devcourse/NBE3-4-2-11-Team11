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
  const { isLoggedIn, login, logout } = useAuthStore();

  useEffect(() => {
    // 클라이언트 사이드에서만 localStorage 접근
    const token = localStorage.getItem("accessToken");
    if (token && !isLoggedIn) {
      // 토큰은 있는데 스토어에 반영되지 않은 경우 업데이트
      login(token);
    } else if (!token) {
      setError("로그인이 필요합니다.");
      router.push("/admin/login");
      return;
    }
    fetchAdminData();
  }, [router, isLoggedIn, login]);

  const fetchAdminData = async () => {
    try {
      const response = await api.get("/admin/me");
      setAdminName(response.data.data.username);
    } catch (err: any) {
      setError(err.message || "관리자 정보를 불러올 수 없습니다.");
      router.push("/admin/login");
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
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      logout();
      router.push("/admin/login");
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
        <button className="bg-blue-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-blue-600">
          공지사항 작성하기
        </button>
        <button className="bg-green-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-green-600">
          사용자 관리
        </button>
        <button className="bg-yellow-500 text-white px-6 py-3 rounded-lg shadow-md hover:bg-yellow-600">
          시스템 로그 보기
        </button>

      </div>
    </div>
  );
}
