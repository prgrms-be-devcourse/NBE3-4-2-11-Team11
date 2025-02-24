"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "../../../utils/api"; // 실제 경로에 맞게 수정
import { useAuthStore } from "@/store/authStore"; // Zustand 스토어 사용
import { getAccessToken, getRefreshToken, removeTokens } from "@/utils/token";

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
      // 로그인 상태가 스토어에 반영되지 않았다면, accessToken (및 refreshToken은 내부에서 자동 관리됨)을 사용해 로그인 상태 복원
      // login(token, localStorage.getItem("refreshToken") || "");
      const refreshToken = getRefreshToken() || "";
      login(token, refreshToken);

    } else if (!token) {
      setError("로그인이 필요합니다.");
      router.push("/login");
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
      removeTokens();

      logout();
      router.push("/login");
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
