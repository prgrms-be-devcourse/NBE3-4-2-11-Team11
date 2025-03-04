"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore"; // 제공된 Zustand 스토어 사용

export default function AdminLoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const router = useRouter();
  const { login } = useAuthStore();

  const handleAdminLogin = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8080/api/v1/admin/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
        credentials: "include", // 쿠키 전송을 위해 반드시 필요
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "로그인 실패");
      }

      // 응답 헤더에서 토큰을 추출하는 코드는 더 이상 필요하지 않습니다.
      // 로그인 상태 업데이트 (백엔드에서 HttpOnly 쿠키에 토큰을 설정하므로)
//       await login();
      await login("admin"); // ✅ Zustand 스토어에 역할 저장

      // 대시보드 페이지로 이동
      router.push("/admin/dashboard");
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <h1 className="text-2xl font-bold mb-6">관리자 로그인</h1>
      {error && (
        <div className="bg-red-500 text-white px-4 py-2 rounded-md mb-4">
          ⚠️ {error}
        </div>
      )}
      <input
        type="text"
        placeholder="아이디"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        className="w-64 px-4 py-2 mb-4 border rounded-md"
      />
      <input
        type="password"
        placeholder="비밀번호"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="w-64 px-4 py-2 mb-4 border rounded-md"
        autoComplete="current-password" // 현재 비밀번호를 의미
      />
      <button
        onClick={handleAdminLogin}
        disabled={loading}
        className="bg-blue-600 text-white px-6 py-3 rounded-lg shadow-md hover:bg-blue-700 w-64"
      >
        {loading ? "로그인 중..." : "관리자 로그인"}
      </button>
    </div>
  );
}
